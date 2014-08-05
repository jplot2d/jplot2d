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

import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

import org.jplot2d.element.impl.ComponentEx;

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
	private abstract class CancelableRendererCallable implements Callable<BufferedImage> {

		protected final long sn;
		protected final Rectangle bounds;

		public CancelableRendererCallable(long sn, Rectangle bounds) {
			this.sn = sn;
			this.bounds = bounds;
		}

		/**
		 * Cancel its component rendering tasks.
		 */
		public abstract void cancel();

	}

	/**
	 * This callable render whole plot in a single thread
	 */
	private final class SingleRendererCallable extends CancelableRendererCallable {

		private final CacheableBlock cacheableBlock;

		public SingleRendererCallable(long sn, Rectangle bounds, CacheableBlock cacheableBlock) {
			super(sn, bounds);
			this.cacheableBlock = cacheableBlock;
		}

		public BufferedImage call() throws Exception {
			return renderCacheableBlock(bounds, cacheableBlock);
		}

		public void cancel() {
			// no component renderer need to cancel
		}

	}

	/**
	 * This callable assemble component rendering futures.
	 */
	private final class RenderAssemblyCallable extends CancelableRendererCallable {

		private final ImageAssemblyInfo ainfo;

		private RenderAssemblyCallable(long sn, Rectangle bounds, ImageAssemblyInfo ainfo) {
			super(sn, bounds);
			this.ainfo = ainfo;
		}

		public BufferedImage call() throws Exception {
			return assembleResult(sn, bounds, ainfo);
		}

		public void cancel() {
			for (ComponentEx comp : ainfo.componentSet()) {
				if (!isFutureCached(comp, ainfo.getFuture(comp))) {
					// boolean cancelled =
					ainfo.getFuture(comp).cancel(true);
					// logger.trace("Cancelling cacheable block renderer for R.{} {}", sn, cancelled);
				}
			}
		}

	}

	/**
	 * A FutureTask to run CancelableRendererCallable.
	 */
	private final class AsyncImageRendererTask extends FutureTask<BufferedImage> {

		private final CancelableRendererCallable callable;

		public AsyncImageRendererTask(CancelableRendererCallable callable) {
			super(callable);
			this.callable = callable;
		}

		public long getSN() {
			return callable.sn;
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
				logger.warn("Renderer exception, drop R." + getSN(), e);
			}

			fireRenderingFinished(getSN(), result);

			/*
			 * remove this renderer and all old renderers from the queue, and cancel all old renderers. The cancelling
			 * only occur in CANCEL_AFTER_NEWER_DONE mode. In CANCEL_BEFORE_EXEC_NEWER mode, old renderers are cancelled
			 * and removed from the queue when new renderer is added.
			 */
			if (cancelPolicy == RendererCancelPolicy.CANCEL_AFTER_NEWER_DONE) {
				synchronized (renderLock) {
					/*
					 * The tasks in the queue are on SN order. We can't guarantee the current tasks exist in the queue.
					 * Another newer task may be done just before this method is called, and sweep all old tasks,
					 * include this. In this case, The renderer queue might be empty.
					 */
					AsyncImageRendererTask head = renderTaskQueue.peek();
					if (head != null && head.getSN() <= getSN()) {
						for (;;) {
							AsyncImageRendererTask renderer = renderTaskQueue.poll();
							if (renderer.getSN() == getSN()) {
								break;
							}
							/* cancel old renderer */
							if (renderer.getSN() < getSN() && renderer.cancel(true)) {
								logger.trace("Renderer {} finished. Cancel the old renderer {}", getSN(),
										renderer.getSN());
							}
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

	/** synchronized area for renderer task queue */
	private Object renderLock = new Object();

	/** synchronized by renderLock */
	private final Queue<AsyncImageRendererTask> renderTaskQueue = new LinkedList<AsyncImageRendererTask>();

	public AsyncImageRenderer(ImageFactory imageFactory) {
		super(imageFactory);
	}

	@Override
	public final void render(ComponentEx comp, List<CacheableBlock> cacheBlockList) {

		Rectangle bounds = getDeviceBounds(comp);

		CancelableRendererCallable callable;
		if (cacheBlockList.size() == 1) {
			// If the plot has no cacheable component, run renderer directly
			callable = new SingleRendererCallable(fsn++, bounds, cacheBlockList.get(0));
		} else {
			// run cacheable component renderer
			ImageAssemblyInfo ainfo = runCompRender(executor, cacheBlockList);
			callable = new RenderAssemblyCallable(fsn++, bounds, ainfo);
		}

		synchronized (renderLock) {
			AsyncImageRendererTask task = new AsyncImageRendererTask(callable);

			/* remove all renderers from queue and cancel them */
			if (cancelPolicy == RendererCancelPolicy.CANCEL_BEFORE_EXEC_NEWER) {
				for (;;) {
					AsyncImageRendererTask rtask = renderTaskQueue.poll();
					if (rtask == null) {
						break;
					}
					if (rtask.cancel(true)) {
						logger.trace("Render task {} to be exec. The render task {} is cancelled.", task.getSN(),
								rtask.getSN());
					}
				}
			}

			// add new task
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
