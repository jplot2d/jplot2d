/**
 * Copyright 2010, 2011 Jingjing Li.
 *
 * This file is part of jplot2d.
 *
 * jplot2d is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or any later version.
 *
 * jplot2d is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Lesser Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with jplot2d. If not, see <http://www.gnu.org/licenses/>.
 */
package org.jplot2d.renderer;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.logging.Level;

import org.jplot2d.element.impl.ComponentEx;
import org.jplot2d.element.impl.PlotEx;

/**
 * Since Graphics can draw image, It's possible to render all cacheable component individually, then
 * assemble them together.
 * <p>
 * The rendering steps:
 * <ol>
 * <li>Rendering all modified cacheable components</li>
 * <li>Assembling result of 1st step into a final result</li>
 * <li>notify all registered RenderingFinishedListener</li>
 * </ol>
 * A renderer maintains a cache for all cacheable components. The cache is a map, the key is uid of
 * components, the value is components future. The environment must offer those values when calling
 * a renderer
 * <ol>
 * <li>all cacheable components and their uid in assembly order</li>
 * <li>all uid of unmodified cacheable components</li>
 * </ol>
 * 
 * @author Jingjing Li
 * 
 */
public abstract class ImageRenderer extends Renderer<BufferedImage> {

	/**
	 * Component renderer execute service
	 */
	protected static Executor COMPONENT_RENDERING_POOL_EXECUTOR = Executors.newCachedThreadPool();

	protected static Executor COMPONENT_RENDERING_CALLER_RUN_EXECUTOR = new Executor() {

		public void execute(Runnable command) {
			command.run();
		}

	};

	/**
	 * The Executor to run component rendering tasks.
	 */
	protected final Executor executor;

	protected final ImageFactory imageFactory;

	/**
	 * The map contains the component cache future in the latest status. They will be assembled into
	 * a renderer result later.
	 * 
	 * @return
	 */
	private ImageAssemblyInfo compCachedFutureMap = new ImageAssemblyInfo();

	protected int fsn;

	/**
	 * Create a Renderer with the given image factory.
	 * 
	 * @param imageFactory
	 */
	protected ImageRenderer(ImageFactory imageFactory) {
		this(imageFactory, COMPONENT_RENDERING_POOL_EXECUTOR);
	}

	public ImageRenderer(ImageFactory imageFactory, Executor executor) {
		this.imageFactory = imageFactory;
		this.executor = executor;
	}

	public void render(PlotEx plot, Map<ComponentEx, ComponentEx> cacheableCompMap,
			Collection<ComponentEx> unmodifiedCacheableComps,
			Map<ComponentEx, ComponentEx[]> subcompsMap) {

		Dimension size = getDeviceBounds(plot).getSize();

		BufferedImage result;
		// If the plot has no cacheable component, run renderer directly
		if (subcompsMap.size() == 1) {
			result = runSingleRender(size, subcompsMap.get(plot));
		} else {
			ImageAssemblyInfo ainfo;
			if (cacheableCompMap.size() - unmodifiedCacheableComps.size() == 1) {
				/*
				 * avoid pooled thread when there is only one modified cacheable component
				 */
				ainfo = runCompRender(COMPONENT_RENDERING_CALLER_RUN_EXECUTOR, cacheableCompMap,
						unmodifiedCacheableComps, subcompsMap);
			} else {
				ainfo = runCompRender(executor, cacheableCompMap, unmodifiedCacheableComps,
						subcompsMap);
			}

			result = assembleResult(size, ainfo);
		}

		fireRenderingFinished(fsn++, result);
	}

	protected final BufferedImage runSingleRender(Dimension size, ComponentEx[] sublist) {
		BufferedImage result = imageFactory.createImage(size.width, size.height);
		Graphics2D g = result.createGraphics();
		for (ComponentEx subcomp : sublist) {
			if (Thread.interrupted()) {
				break;
			}
			subcomp.draw(g);
		}
		g.dispose();

		return result;
	}

	/**
	 * Execute component renderer on every modified cacheable component. This method must be called
	 * from render.
	 * 
	 * @param cacheableCompMap
	 *            cacheable component map in Z-order, the key is component, the value is components'
	 *            thread safe copy.
	 * @param umCacheableComps
	 *            unmodified cacheable components
	 * @param subcompsMap
	 *            the key is safe copy of cacheable component, the value is the safe copies of all
	 *            its sub-components in Z-order.
	 * @return
	 */
	protected final ImageAssemblyInfo runCompRender(Executor executor,
			Map<ComponentEx, ComponentEx> cacheableCompMap,
			Collection<ComponentEx> umCacheableComps, Map<ComponentEx, ComponentEx[]> subcompsMap) {
		ImageAssemblyInfo ainfo = new ImageAssemblyInfo();

		for (Map.Entry<ComponentEx, ComponentEx> me : cacheableCompMap.entrySet()) {
			ComponentEx comp = me.getKey();
			ComponentEx ccopy = me.getValue();

			/*
			 * the component may be set to cacheable, while stay unmodified
			 */
			if (umCacheableComps.contains(comp) && compCachedFutureMap.contains(comp)) {
				ainfo.put(comp, compCachedFutureMap.getBounds(comp),
						compCachedFutureMap.getFuture(comp));
			} else {
				// create a new Component Render task
				Rectangle bounds = getDeviceBounds(ccopy);
				ComponentEx[] sublist = subcompsMap.get(ccopy);
				CompRenderCallable compRenderCallable = new CompRenderCallable(sublist,
						imageFactory, bounds);
				FutureTask<BufferedImage> crtask = new FutureTask<BufferedImage>(compRenderCallable);

				executor.execute(crtask);
				ainfo.put(comp, bounds, crtask);
			}
		}

		compCachedFutureMap = ainfo;
		return ainfo;
	}

	/**
	 * Assemble the rendered component given in AssemblyInfo into a result.
	 * 
	 * @param size
	 *            the result size
	 * @param ainfo
	 *            the AssemblyInfo
	 * @return the assembled result
	 */
	protected BufferedImage assembleResult(Dimension size, ImageAssemblyInfo ainfo) {
		BufferedImage image = imageFactory.createImage(size.width, size.height);

		Graphics2D g = (Graphics2D) image.getGraphics();

		for (ComponentEx c : ainfo.componentSet()) {
			Rectangle bounds = ainfo.getBounds(c);
			Future<BufferedImage> f = ainfo.getFuture(c);
			try {
				BufferedImage bi = f.get();
				g.drawImage(bi, bounds.x, bounds.y, null);
			} catch (InterruptedException e) {
				// logger.log(Level.WARNING, "[R] Renderer interrupted, drop F." + fsn, e);
			} catch (ExecutionException e) {
				logger.log(Level.WARNING, "[R] Renderer exception, drop F." + fsn, e);
			}
		}

		g.dispose();
		return image;
	}

}
