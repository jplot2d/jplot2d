/**
 * Copyright 2010 Jingjing Li.
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
import java.awt.Rectangle;
import java.awt.geom.Dimension2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.logging.Logger;

import org.jplot2d.element.impl.ComponentEx;
import org.jplot2d.element.impl.PlotEx;

/**
 * Since Graphics can draw image, It's possible to render all cacheable
 * component individually, then assemble them together.
 * <p>
 * The rendering steps:
 * <ol>
 * <li>Rendering all modified cacheable components</li>
 * <li>Assembling result of 1st step into a final result</li>
 * <li>notify all registered RenderingFinishedListener</li>
 * </ol>
 * A renderer maintains a cache for all cacheable components. The cache is a
 * map, the key is impl components, the value is components future. The
 * environment must offer those values when calling a renderer
 * <ol>
 * <li>all self-cache components impl in assembly order</li>
 * <li>all unmodified self-cache components and their safe copy</li>
 * </ol>
 * 
 * @author Jingjing Li
 * 
 */
public abstract class ImageRenderer {

	static Logger logger = Logger.getLogger("org.jplot2d.renderer");

	/**
	 * Component renderer execute service
	 */
	private static Executor COMPONENT_RENDERING_POOL_EXECUTOR = Executors
			.newCachedThreadPool();

	/**
	 * The Executor to run component rendering tasks.
	 */
	private final Executor crExecutor;

	/**
	 * The map contains the component cache future in the latest status. They
	 * will be assembled into a renderer result later.
	 * 
	 * @return
	 */
	private ImageAssemblyInfo compCachedFutureMap = new ImageAssemblyInfo();

	protected ImageAssembler assembler;

	protected int fsn;

	private final List<RenderingFinishedListener> renderingFinishedListenerList = Collections
			.synchronizedList(new ArrayList<RenderingFinishedListener>());

	/**
	 * Create a Renderer with the given assembler.
	 * 
	 * @param assembler
	 */
	public ImageRenderer(ImageAssembler assembler) {
		this.crExecutor = COMPONENT_RENDERING_POOL_EXECUTOR;
		this.assembler = assembler;
	}

	public ImageRenderer(ImageAssembler assembler, Executor executor) {
		this.crExecutor = executor;
		this.assembler = assembler;
	}

	/**
	 * This method is protected by environment lock.
	 * 
	 * @param plot
	 *            the plot impl
	 * @param cacheableCompMap
	 *            A map contains all cacheable components to their safe copy.
	 *            The iteration order is z-order.
	 * @param unmodifiedCacheableComps
	 *            unmodified cacheable components
	 * @param subcompOrderMap
	 *            the key is cacheable component, the value is safe-copy of all
	 *            its sub-component in z-order.
	 */
	public void render(PlotEx plot,
			Map<ComponentEx, ComponentEx> cacheableCompMap,
			Collection<ComponentEx> unmodifiedCacheableComps,
			Map<ComponentEx, ComponentEx[]> subcompsMap) {

		ImageAssemblyInfo ainfo = runCompRender(cacheableCompMap,
				unmodifiedCacheableComps, subcompsMap);

		Dimension size = getDeviceBounds(plot).getSize();
		BufferedImage result = assembler.assembleResult(size, ainfo);
		fireRenderingFinished(fsn++, result);

	}

	/**
	 * Execute component renderer on every modified cacheable component. This
	 * method must be called from render.
	 * 
	 * @param compMap
	 *            cacheable component map in Z-order, the key is component, the
	 *            value is components' thread safe copy.
	 * @param unmodifiedComps
	 *            unmodified cacheable components
	 * @param subcompOrderMap
	 *            the key is cacheable component, the value is the safe copies
	 *            of all its sub-components in Z-order.
	 * @return
	 */
	protected ImageAssemblyInfo runCompRender(
			Map<ComponentEx, ComponentEx> compMap,
			Collection<ComponentEx> unmodifiedComps,
			Map<ComponentEx, ComponentEx[]> subcompOrderMap) {
		ImageAssemblyInfo ainfo = new ImageAssemblyInfo();

		for (Map.Entry<ComponentEx, ComponentEx> me : compMap.entrySet()) {
			ComponentEx comp = me.getKey();
			ComponentEx ccopy = me.getValue();

			/*
			 * the component may be set to cacheable, while stay unmodified
			 */
			if (unmodifiedComps.contains(comp)
					&& compCachedFutureMap.contains(comp)) {
				ainfo.put(comp, compCachedFutureMap.getBounds(comp),
						compCachedFutureMap.getFuture(comp));
			} else {
				// create a new Component Render task
				Rectangle bounds = getDeviceBounds(ccopy);
				ComponentEx[] sublist = subcompOrderMap.get(comp);
				CompRenderCallable<BufferedImage> compRenderCallable = assembler
						.createCompRenderCallable(bounds, sublist);
				FutureTask<BufferedImage> crtask = new FutureTask<BufferedImage>(
						compRenderCallable);

				crExecutor.execute(crtask);
				ainfo.put(comp, bounds, crtask);
			}
		}

		compCachedFutureMap = ainfo;
		return ainfo;
	}

	protected Rectangle getDeviceBounds(ComponentEx comp) {
		if (comp instanceof PlotEx) {
			double scale = ((PlotEx) comp).getPhysicalTransform().getScale();
			Dimension2D size = ((PlotEx) comp).getSize();
			return new Rectangle2D.Double(0, 0, size.getWidth() * scale,
					size.getHeight() * scale).getBounds();
		} else {
			Rectangle2D pbounds = comp.getBounds();
			return comp.getParent().getPhysicalTransform().getPtoD(pbounds)
					.getBounds();
		}
	}

	/**
	 * @param fsn
	 * @param t
	 */
	protected void fireRenderingFinished(long fsn, BufferedImage image) {
		RenderingFinishedListener[] ls = renderingFinishedListenerList
				.toArray(new RenderingFinishedListener[0]);
		for (RenderingFinishedListener lsnr : ls) {
			lsnr.renderingFinished(new RenderingFinishedEvent(fsn, image));
		}

	}

	public void addRenderingFinishedListener(RenderingFinishedListener listener) {
		renderingFinishedListenerList.add(listener);
	}

	public void removeRenderingFinishedListener(
			RenderingFinishedListener listener) {
		renderingFinishedListenerList.remove(listener);
	}

}
