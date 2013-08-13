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
	private abstract class CancelableRendererCallable implements Callable<BufferedImage> {

		protected final int sn;
		protected final Dimension size;

		public CancelableRendererCallable(int sn, Dimension size) {
			this.sn = sn;
			this.size = size;
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

		private final List<ComponentEx> complist;

		public SingleRendererCallable(int sn, Dimension size, List<ComponentEx> complist) {
			super(sn, size);
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
	private final class RenderAssemblyCallable extends CancelableRendererCallable {

		private final ImageAssemblyInfo ainfo;

		private RenderAssemblyCallable(int sn, Dimension size, ImageAssemblyInfo ainfo) {
			super(sn, size);
			this.ainfo = ainfo;
		}

		public BufferedImage call() throws Exception {
			return assembleResult(sn, size, ainfo);
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

		private final CancelableRendererCallable callable;

		public AsyncImageRendererTask(CancelableRendererCallable callable) {
			super(callable);
			this.callable = callable;
		}

		public int getSN() {
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
			if (result != null) {
				try {
					fireRenderingFinished(getSN(), result);
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
					 * the renderers in the queue are on sn order. We can't guarantee the current renderer exist in the
					 * queue. A renderer may be done, before this method is called, a new renderer may be created and
					 * added to the queue, and sweep all old renderer(SURPASS mode), include this. The renderer queue is
					 * not empty on this point, because removal are guarded by renderLock.
					 */
					if (renderTaskQueue.peek().getSN() > getSN()) {
						return;
					}
					for (;;) {
						AsyncImageRendererTask renderer = renderTaskQueue.poll();
						if (renderer.getSN() == getSN()) {
							break;
						}
						/* cancel old renderer */
						if (renderer.getSN() < getSN() && renderer.cancel(true)) {
							logger.info("Renderer {} finished. Cancel the old renderer {}", getSN(), renderer.getSN());
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

	public AsyncImageRenderer(ImageFactory assembler) {
		super(assembler);
	}

	@Override
	public final void render(PlotEx plot, List<CacheBlock> cacheBlockList) {

		Dimension size = getDeviceBounds(plot).getSize();

		CancelableRendererCallable callable;
		if (cacheBlockList.size() == 1) {
			// If the plot has no cacheable component, run renderer directly
			callable = new SingleRendererCallable(fsn++, size, cacheBlockList.get(0).getSubcomps());
		} else {
			// run cacheable component renderer
			ImageAssemblyInfo ainfo = runCompRender(executor, cacheBlockList);
			callable = new RenderAssemblyCallable(fsn++, size, ainfo);
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
						logger.info("Render task {} to be exec. Cancel the running render task {}", task.getSN(),
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
