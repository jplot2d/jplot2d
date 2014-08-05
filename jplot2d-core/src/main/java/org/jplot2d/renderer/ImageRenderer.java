/**
 * Copyright 2010-2014 Jingjing Li.
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

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.jplot2d.element.impl.ComponentEx;

/**
 * Since Graphics can draw image, It's possible to render all cacheable component individually, then assemble them
 * together.
 * <p>
 * The rendering steps:
 * <ol>
 * <li>Rendering all modified cacheable components</li>
 * <li>Assembling result of 1st step into a final result</li>
 * <li>notify all registered RenderingFinishedListener</li>
 * </ol>
 * A renderer maintains a cache for all cacheable components. The cache is a map, the key is uid of components, the
 * value is components future. The environment must offer those values when calling a renderer
 * <ol>
 * <li>all cacheable components and their uid in assembly order</li>
 * <li>all uid of unmodified cacheable components</li>
 * </ol>
 * 
 * @author Jingjing Li
 * 
 */
public abstract class ImageRenderer extends Renderer {

	/**
	 * The renderer thread factory to create daemon thread
	 */
	private static class RendererThreadFactory implements ThreadFactory {
		final ThreadGroup group;
		final AtomicInteger threadNumber = new AtomicInteger(1);

		RendererThreadFactory() {
			SecurityManager s = System.getSecurityManager();
			group = (s != null) ? s.getThreadGroup() : Thread.currentThread().getThreadGroup();
		}

		public Thread newThread(Runnable r) {
			Thread t = new Thread(group, r, "JPlot2d-renderer-" + threadNumber.getAndIncrement(), 0);
			if (!t.isDaemon()) {
				t.setDaemon(true);
			}
			if (t.getPriority() != Thread.NORM_PRIORITY) {
				t.setPriority(Thread.NORM_PRIORITY);
			}
			return t;
		}
	}

	private static class CompRenderCallable implements Callable<BufferedImage> {

		private final CacheableBlock cacheableBlock;

		private final ImageFactory imageFactory;

		private final Rectangle bounds;

		/**
		 * @param comps
		 *            the components in z-order
		 * @param imageFactory
		 * @param bounds
		 */
		public CompRenderCallable(CacheableBlock cacheableBlock, ImageFactory imageFactory, Rectangle bounds) {
			this.cacheableBlock = cacheableBlock;
			this.imageFactory = imageFactory;
			this.bounds = bounds;
		}

		public BufferedImage call() throws Exception {
			if (Thread.interrupted()) {
				return null;
			}

			BufferedImage image = imageFactory.createTransparentImage(bounds.width, bounds.height);
			Graphics2D g = image.createGraphics();
			g.translate(-bounds.x, -bounds.y);
			g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
			g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
			g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
			g.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);

			for (ComponentEx comp : cacheableBlock.getSubcomps()) {
				if (Thread.interrupted()) {
					g.dispose();
					return null;
				}
				comp.draw(g);
			}

			g.dispose();

			return image;
		}

	}

	private static final int cores = Runtime.getRuntime().availableProcessors();

	/**
	 * Component renderer execute service
	 */
	protected static final Executor COMPONENT_RENDERING_POOL_EXECUTOR = new ThreadPoolExecutor(1, cores, 0L,
			TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>(), new RendererThreadFactory());

	protected static final Executor COMPONENT_RENDERING_CALLER_RUN_EXECUTOR = new Executor() {

		public void execute(Runnable command) {
			command.run();
		}

	};

	static {
		logger.info("Max Renderer Threads: " + cores);
	}

	/**
	 * The Executor to run component rendering tasks.
	 */
	protected final Executor executor;

	protected final ImageFactory imageFactory;

	/**
	 * The map contains the component cache future in the latest status. They will be assembled into a renderer result
	 * later.
	 */
	private ImageAssemblyInfo compCachedFutureMap = new ImageAssemblyInfo();

	protected long fsn;

	private final List<RenderingFinishedListener> renderingFinishedListenerList = Collections
			.synchronizedList(new ArrayList<RenderingFinishedListener>());

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

	public void render(ComponentEx comp, List<CacheableBlock> cacheBlockList) {

		Rectangle bounds = getDeviceBounds(comp);

		BufferedImage result;
		if (cacheBlockList.size() == 0) {
			result = null;
		} else if (cacheBlockList.size() == 1) {
			// If the plot has no cacheable component, run renderer directly
			result = renderCacheableBlock(bounds, cacheBlockList.get(0));
		} else {
			ImageAssemblyInfo ainfo;
			ainfo = runCompRender(executor, cacheBlockList);
			result = assembleResult(fsn, bounds, ainfo);
		}

		fireRenderingFinished(fsn++, result);
	}

	/**
	 * Called when a BufferedImage has been generated by this renderer.
	 * 
	 * @param sn
	 *            the result sn
	 * @param img
	 *            the generated BufferedImage
	 */
	protected void fireRenderingFinished(long sn, BufferedImage img) {
		if (img == null) {
			return;
		}

		RenderingFinishedListener[] ls = renderingFinishedListenerList.toArray(new RenderingFinishedListener[0]);
		for (RenderingFinishedListener lsnr : ls) {
			try {
				lsnr.renderingFinished(new RenderingFinishedEvent(sn, img));
			} catch (Exception e) {
				logger.warn("RenderingFinishedListener Error", e);
			}
		}

	}

	public void addRenderingFinishedListener(RenderingFinishedListener listener) {
		renderingFinishedListenerList.add(listener);
	}

	public void removeRenderingFinishedListener(RenderingFinishedListener listener) {
		renderingFinishedListenerList.remove(listener);
	}

	/**
	 * Render a cacheable block in the given bounds.
	 * 
	 * @param bounds
	 *            the bounds to limit the components
	 * @param sublist
	 *            components to be rendered
	 * @return the rendering result
	 */
	protected final BufferedImage renderCacheableBlock(Rectangle bounds, CacheableBlock cb) {
		if (Thread.interrupted()) {
			return null;
		}

		BufferedImage image = imageFactory.createImage(bounds.width, bounds.height);
		Graphics2D g = image.createGraphics();
		g.translate(-bounds.x, -bounds.y);
		g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
		g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
		g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);

		for (ComponentEx comp : cb.getSubcomps()) {
			if (Thread.interrupted()) {
				g.dispose();
				imageFactory.cacheImage(image);
				return null;
			}
			comp.draw(g);
		}

		g.dispose();

		return image;
	}

	/**
	 * Execute component renderer on every modified cacheable component. This method must be called from a render.
	 * 
	 * @param executor
	 *            a executor to render cacheable blocks
	 * @param cacheBlockList
	 *            cacheable blocks to be rendered in executor
	 * @return assembly info
	 */
	protected final ImageAssemblyInfo runCompRender(Executor executor, List<CacheableBlock> cacheBlockList) {
		ImageAssemblyInfo ainfo = new ImageAssemblyInfo();

		for (CacheableBlock cb : cacheBlockList) {
			ComponentEx comp = cb.getUid();
			ComponentEx ccopy = cb.getComp();

			/*
			 * the component may be set to cacheable, while stay unmodified
			 */
			if (!ccopy.isRedrawNeeded() && compCachedFutureMap.contains(comp)) {
				ainfo.put(comp, compCachedFutureMap.getBounds(comp), compCachedFutureMap.getFuture(comp));
			} else {
				// create a new Component Render task
				Rectangle bounds = getDeviceBounds(ccopy);
				CompRenderCallable compRenderCallable = new CompRenderCallable(cb, imageFactory, bounds);
				FutureTask<BufferedImage> crtask = new FutureTask<BufferedImage>(compRenderCallable);

				executor.execute(crtask);
				ainfo.put(comp, bounds, crtask);
			}
		}

		compCachedFutureMap = ainfo;
		return ainfo;
	}

	/**
	 * Returns <code>true</code> if the future for given component is cached for using later.
	 * 
	 * @param comp
	 * @param future
	 * @return
	 */
	protected boolean isFutureCached(ComponentEx comp, Future<BufferedImage> future) {
		return compCachedFutureMap.getFuture(comp) == future;
	}

	/**
	 * Assemble the rendered component given in AssemblyInfo into a result.
	 * 
	 * @param sn
	 *            the result sn
	 * @param size
	 *            the result size
	 * @param ainfo
	 *            the AssemblyInfo
	 * @return the assembled result
	 */
	protected BufferedImage assembleResult(long sn, Rectangle bounds, ImageAssemblyInfo ainfo) {
		BufferedImage image = imageFactory.createImage(bounds.width, bounds.height);
		Graphics2D g = (Graphics2D) image.getGraphics();
		g.translate(-bounds.x, -bounds.y);

		try {
			for (ComponentEx c : ainfo.componentSet()) {
				Rectangle cbounds = ainfo.getBounds(c);
				BufferedImage bi = ainfo.getFuture(c).get();
				g.drawImage(bi, cbounds.x, cbounds.y, null);
			}
			g.dispose();
			return image;
		} catch (CancellationException e) {
			// logger.trace("Renderer cancelled, drop R.{}", sn);
		} catch (InterruptedException e) {
			// logger.log(Level.WARNING, "Renderer interrupted, drop R." + sn, e);
		} catch (ExecutionException e) {
			logger.warn("Renderer exception, drop R." + sn, e);
		}

		imageFactory.cacheImage(image);
		return null;
	}

}
