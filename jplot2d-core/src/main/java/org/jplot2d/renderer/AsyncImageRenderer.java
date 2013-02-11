/**
 * Copyright 2010-2013 Jingjing Li.
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
import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

import org.jplot2d.element.impl.ComponentEx;
import org.jplot2d.element.impl.PlotEx;
import org.jplot2d.env.PlotEnvironment.CacheBlock;

/**
 * This renderer assemble all cacheable component in a individual thread asynchronously.
 * 
 * @author Jingjing Li
 * 
 */
public class AsyncImageRenderer extends ImageRenderer {

	/**
	 * Add ability to cancel component rendering futures.
	 */
	private interface CancelableRendererCallable extends Callable<BufferedImage> {

		/**
		 * Cancel its component rendering tasks.
		 */
		public void cancel();

	}

	/**
	 * This callable render whole plot in a single thread
	 */
	private final class SingleRendererCallable implements CancelableRendererCallable {

		private final Dimension size;

		private final List<ComponentEx> complist;

		public SingleRendererCallable(Dimension size, List<ComponentEx> complist) {
			this.size = size;
			this.complist = complist;
		}

		public BufferedImage call() throws Exception {
			return runSingleRender(size, complist);
		}

		public void cancel() {
			// no component renderer need to cancel
		}

	}

	/**
	 * This callable assemble component rendering futures.
	 */
	private final class RenderAssemblyCallable implements CancelableRendererCallable {

		private final Dimension size;

		private final ImageAssemblyInfo ainfo;

		private RenderAssemblyCallable(Dimension size, ImageAssemblyInfo ainfo) {
			this.size = size;
			this.ainfo = ainfo;
		}

		public BufferedImage call() throws Exception {
			return assembleResult(size, ainfo);
		}

		public void cancel() {
			for (Object comp : ainfo.componentSet()) {
				ainfo.getFuture(comp).cancel(true);
			}
		}

	}

	/**
	 * A FutureTask to run CancelableRendererCallable.
	 */
	private final class AsyncImageRendererTask extends FutureTask<BufferedImage> {

		protected final long fsn;

		private final CancelableRendererCallable callable;

		public AsyncImageRendererTask(long fsn, CancelableRendererCallable callable) {
			super(callable);
			this.fsn = fsn;
			this.callable = callable;
		}

		public long getFsn() {
			return fsn;
		}

		protected final void done() {
			if (this.isCancelled()) {
				callable.cancel();
				return;
			}

			BufferedImage result = null;
			try {
				result = get();
			} catch (InterruptedException e) {
				// should not happen. Normal cancellation will set the internal state to CANCELLED
			} catch (ExecutionException e) {
				logger.warn("Renderer exception, drop F." + fsn, e);
			}
			if (result != null) {
				try {
					fireRenderingFinished(fsn, result);
				} catch (Exception e) {
					logger.warn("RenderingFinishedListener Error", e);
				}
			}

			/*
			 * remove this renderer and all old renderers from the queue, and cancel all old renderers. The canceling
			 * only occur in CANCEL_AFTER_NEWER_DONE mode. In CANCEL_BEFORE_EXEC_NEWER mode, old renderers are canceled
			 * and removed from the queue when new renderer is added.
			 */
			if (cancelPolicy == RendererCancelPolicy.CANCEL_AFTER_NEWER_DONE) {
				synchronized (renderLock) {
					/*
					 * the renderers in the queue are on fsn order. We can't guarantee the current renderer exist in the
					 * queue. A renderer may be done, before this method is called, a new renderer may be created and
					 * added to the queue, and sweep all old renderer(SURPASS mode), include this. The renderer queue is
					 * not empty on this point, because removal are guarded by renderLock.
					 */
					if (renderTaskQueue.peek().getFsn() > fsn) {
						return;
					}
					for (;;) {
						AsyncImageRendererTask renderer = renderTaskQueue.poll();
						if (renderer.getFsn() == fsn) {
							break;
						}
						/* cancel old renderer */
						if (renderer.getFsn() < fsn && renderer.cancel(true)) {
							logger.info("Renderer {} finished. Cancel the old renderer {}", fsn, renderer.getFsn());
						}
					}
				}
			}
		}

	}

	public static enum RendererCancelPolicy {
		CANCEL_BEFORE_EXEC_NEWER, CANCEL_AFTER_NEWER_DONE, NO_CANCEL
	}

	private volatile RendererCancelPolicy cancelPolicy = RendererCancelPolicy.CANCEL_BEFORE_EXEC_NEWER;

	/** synchronized area for fsn and renderer task queue */
	private Object renderLock = new Object();

	/** synchronized by renderLock */
	private final Queue<AsyncImageRendererTask> renderTaskQueue = new LinkedList<AsyncImageRendererTask>();

	public AsyncImageRenderer(ImageFactory assembler) {
		super(assembler);
	}

	@Override
	public final void render(PlotEx plot, List<CacheBlock> cacheBlockList) {

		Dimension size = getDeviceBounds(plot).getSize();

		CancelableRendererCallable callable;
		if (cacheBlockList.size() == 1) {
			// If the plot has no cacheable component, run renderer directly
			callable = new SingleRendererCallable(size, cacheBlockList.get(0).getSubcomps());
		} else {
			// run cacheable component renderer
			ImageAssemblyInfo ainfo = runCompRender(executor, cacheBlockList);
			callable = new RenderAssemblyCallable(size, ainfo);
		}

		synchronized (renderLock) {
			/* remove all renderers from queue and cancel them */
			if (cancelPolicy == RendererCancelPolicy.CANCEL_BEFORE_EXEC_NEWER) {
				for (;;) {
					AsyncImageRendererTask rtask = renderTaskQueue.poll();
					if (rtask == null) {
						break;
					}
					if (rtask.cancel(true)) {
						logger.info("Render task {} to be exec. Cancel the running render task {}", fsn, rtask.getFsn());
					}
				}
			}

			// add new task
			AsyncImageRendererTask task = new AsyncImageRendererTask(fsn++, callable);
			renderTaskQueue.offer(task);
			executor.execute(task);
		}

	}

	public RendererCancelPolicy getRendererCancelPolicy() {
		return cancelPolicy;
	}

	/**
	 * In PARALLEL mode, all scheduled renderer exist in the queue. In SURPASS mode, there is only 1 renderer in the
	 * queue is scheduled
	 */
	public void setRendererCancelPolicy(RendererCancelPolicy policy) {
		cancelPolicy = policy;
	}

}
