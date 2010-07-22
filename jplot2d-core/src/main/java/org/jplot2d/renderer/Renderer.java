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
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.logging.Logger;

import org.jplot2d.element.Component;
import org.jplot2d.element.Plot;

/**
 * A renderer to generate a result for plot. A renderer maintains a cache for
 * self cache components.
 * 
 * @author Jingjing Li
 * 
 */
public abstract class Renderer<T> {

	class CompRenderCallable implements Callable<T> {

		private Component comp;

		private Graphics2D g;

		private T result;

		private Rectangle bounds;

		public CompRenderCallable(Component comp, Graphics2D g, T result,
				Rectangle bounds) {
			this.comp = comp;
			this.g = g;
			this.result = result;
			this.bounds = bounds;
		}

		public T call() throws Exception {
			comp.draw(g, false);
			return result;
		}

		/**
		 * @return
		 */
		public Rectangle getBounds() {
			return bounds;
		}

	}

	static Logger logger = Logger.getLogger("org.jplot2d.renderer");

	/**
	 * Component renderer execute service
	 */
	private static ExecutorService CREService = Executors.newCachedThreadPool();

	/**
	 * The returned map contains the component cache future. They will be
	 * assembled into a renderer result later.
	 * 
	 * @return
	 */
	private AssemblyInfo<T> compCachedFutureMap;

	/**
	 * This method is protected by environment lock.
	 * 
	 * @param plot
	 *            the safe plot
	 * @param ccms
	 *            cacheable component modified state map
	 */
	public abstract void render(Plot plot, Map<Component, Boolean> ccms);

	/**
	 * @param ccms
	 *            cacheable component modified state map
	 * @return
	 */
	protected AssemblyInfo<T> runCompRender(Map<Component, Boolean> ccms) {

		AssemblyInfo<T> result = new AssemblyInfo<T>();
		for (Map.Entry<Component, Boolean> me : ccms.entrySet()) {
			Component comp = me.getKey();
			boolean modified = me.getValue();
			if (modified) {

				// create a new Component Render task
				CompRenderCallable compRenderCallable = createCompRenderCallable(comp);
				Rectangle bounds = compRenderCallable.getBounds();
				FutureTask<T> crtask = new FutureTask<T>(compRenderCallable);

				CREService.execute(crtask);

				result.put(comp, bounds, crtask);

			} else if (compCachedFutureMap.contains(comp)) {
				result.put(comp, compCachedFutureMap.getBounds(comp),
						compCachedFutureMap.getFuture(comp));
			}
		}

		return result;
	}

	/**
	 * Returns a graphics for the given component.
	 * 
	 * @return
	 */
	protected abstract CompRenderCallable createCompRenderCallable(
			Component comp);

	protected abstract T assambleResult(AssemblyInfo<T> ainfo, Dimension size);


}
