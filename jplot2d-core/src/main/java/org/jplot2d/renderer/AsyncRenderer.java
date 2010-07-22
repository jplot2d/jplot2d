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
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.logging.Level;

import org.jplot2d.element.Component;
import org.jplot2d.element.Plot;

/**
 * This renderer assemble all cached component and render uncacheable component
 * asynchronously. The method {@link #renderingComplete(long, Object)} will be
 * called once a result is ready.
 * 
 * @author Jingjing Li
 * 
 * @param <T>
 *            the render result class type
 */
public abstract class AsyncRenderer<T> extends Renderer<T> {

	private class RenderCallable implements Callable<T> {

		private Dimension size;

		private Map<Component, Boolean> ccms;

		private RenderCallable(Dimension size, Map<Component, Boolean> ccms) {
			this.size = size;
			this.ccms = ccms;
		}

		public T call() throws Exception {
			AssemblyInfo<T> ainfo = runCompRender(ccms);
			return assambleResult(ainfo, size);
		}

	}

	protected class RenderTask extends FutureTask<T> {

		protected final long fsn;

		/**
		 * @param callable
		 */
		public RenderTask(long fsn, Callable<T> callable) {
			super(callable);
			this.fsn = fsn;
		}

		/**
		 * @return
		 */
		public long getFsn() {
			return fsn;
		}

		protected final void done() {
			if (this.isCancelled()) {
				return;
			}

			T result = null;
			try {
				result = get();
			} catch (InterruptedException e) {
				// should not happen. Normal cancellation will set the internal
				// state to CANCELLED
			} catch (ExecutionException e) {
				logger.info("[R] Renderer exception, drop F." + fsn);
				logger.log(Level.FINE, "", e);
			}
			if (result != null) {
				renderingComplete(fsn, result);
			}

			synchronized (renderLock) {
				/*
				 * the renderers in the queue are on fsn order. We can't
				 * guarantee the current renderer exist in the queue. A renderer
				 * may be done, before this method is called, a new renderer may
				 * be created and added to the queue, and sweep all old
				 * renderer(SURPASS mode), include this. The renderer queue is
				 * not empty on this point, because removal are guarded by
				 * renderLock.
				 */
				if (renderTaskQueue.peek().getFsn() > fsn) {
					return;
				}
				/*
				 * remove this renderer and all old renderers from the queue,
				 * and cancel all old renderers. The canceling only occur in
				 * PARALLEL mode. In SURPASS mode, old renderers are canceled
				 * and removed from the queue when new renderer is added.
				 */
				if (cancelPolicy == RendererCancelPolicy.CANCEL_AFTER_NEWER_DONE)
					;
				for (;;) {
					RenderTask renderer = renderTaskQueue.poll();
					if (renderer.getFsn() == fsn) {
						break;
					}
					/* cancel old renderer */
					if (renderer.getFsn() < fsn && renderer.cancel(true)) {
						logger.info("[R] Renderer " + fsn
								+ " finished. Cancel the old renderer "
								+ renderer.getFsn());
					}
				}

			}
		}

	}

	public static enum RendererCancelPolicy {
		CANCEL_BEFORE_EXEC_NEWER, CANCEL_AFTER_NEWER_DONE, NO_CANCEL
	}

	/**
	 * Asynchrony renderer execute service
	 */
	private static ExecutorService ARES = Executors.newCachedThreadPool();

	private RendererCancelPolicy cancelPolicy = RendererCancelPolicy.CANCEL_BEFORE_EXEC_NEWER;

	/** synchronized area for fsn and renderer task queue */
	private Object renderLock = new Object();

	private int fsn;

	private final Queue<RenderTask> renderTaskQueue = new LinkedList<RenderTask>();

	@Override
	public final void render(Plot plot, Map<Component, Boolean> ccms) {
		Dimension size = plot.getBounds().getBounds().getSize();
		Callable<T> callable = new RenderCallable(size, ccms);
		RenderTask task = new RenderTask(fsn++, callable);

		synchronized (renderLock) {
			if (cancelPolicy == RendererCancelPolicy.CANCEL_BEFORE_EXEC_NEWER) {
				/* remove all renderers from queue and cancel them */
				for (;;) {
					RenderTask rtask = renderTaskQueue.poll();
					if (rtask == null) {
						break;
					}
					if (rtask.cancel(true)) {
						logger
								.info("[R] Render task "
										+ fsn
										+ " to be exec. Cancel the running render task "
										+ rtask.getFsn());
					}
				}
			}
			renderTaskQueue.offer(task);

			ARES.execute(task);
		}
	}

	public RendererCancelPolicy getRendererCancelPolicy() {
		synchronized (renderLock) {
			return cancelPolicy;
		}
	}

	/**
	 * In PARALLEL mode, all scheduled renderer exist in the queue. In SURPASS
	 * mode, there is only 1 renderer in the queue is scheduled
	 */
	public void setRendererCancelPolicy(RendererCancelPolicy policy) {
		synchronized (renderLock) {
			cancelPolicy = policy;
		}
	}

	/**
	 * @param fsn
	 * @param t
	 */
	protected void renderingComplete(long fsn, T t) {

	}

}
