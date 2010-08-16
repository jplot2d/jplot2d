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

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.logging.Logger;

import org.jplot2d.element.Component;
import org.jplot2d.element.Plot;

/**
 * A renderer to generate a result for plot. The rendering process in 2 steps:
 * <ol>
 * <li>Rendering modified cacheable components</li>
 * <li>Assembling result of 1st setp into a final result</li>
 * </ol>
 * A renderer maintains a cache for self cache components. The cache is map, key
 * is impl componnets, the value is components future. The environment must
 * offer those values when calling a renderer
 * <ol>
 * <li>all self-cache components impl in assembly order</li>
 * <li>all unmodified self-cache components and their safe copy</li>
 * </ol>
 * 
 * @author Jingjing Li
 * 
 */
public abstract class Renderer<T> {

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
	private AssemblyInfo<T> compCachedFutureMap = new AssemblyInfo<T>();

	protected Assembler<T> assembler;

	private final List<RenderingFinishedListener> renderingFinishedListenerList = Collections
			.synchronizedList(new ArrayList<RenderingFinishedListener>());

	/**
	 * Create a Renderer with the given assembler.
	 * 
	 * @param assembler
	 */
	public Renderer(Assembler<T> assembler) {
		this.crExecutor = COMPONENT_RENDERING_POOL_EXECUTOR;
		this.assembler = assembler;
	}

	public Renderer(Assembler<T> assembler, Executor executor) {
		this.crExecutor = executor;
		this.assembler = assembler;
	}

	/**
	 * This method is protected by environment lock.
	 * 
	 * @param plot
	 *            the plot impl
	 * @param compMap
	 *            A map contains all cacheable components to their safe copy.
	 *            The iteration order is z-order.
	 * @param unmodifiedComps
	 *            unmodified cacheable components
	 */
	public abstract void render(Plot plot, Map<Component, Component> compMap,
			Collection<Component> unmodifiedComps);

	/**
	 * Execute component renderer on every modified cacheable component. This
	 * method must be called from render.
	 * 
	 * @param compMap
	 *            cacheable component map, the key is component, the value is
	 *            components' thread safe copy.
	 * @param unmodifiedComps
	 *            unmodified cacheable components
	 */
	protected AssemblyInfo<T> runCompRender(Map<Component, Component> compMap,
			Collection<Component> unmodifiedComps) {
		AssemblyInfo<T> ainfo = new AssemblyInfo<T>();

		for (Map.Entry<Component, Component> me : compMap.entrySet()) {
			Component comp = me.getKey();
			Component ccopy = me.getValue();

			/*
			 * the component may be set to cacheable, while stay unmodified
			 */
			if (unmodifiedComps.contains(comp)
					&& compCachedFutureMap.contains(comp)) {
				ainfo.put(comp, compCachedFutureMap.getBounds(comp),
						compCachedFutureMap.getFuture(comp));
			} else {
				// create a new Component Render task
				CompRenderCallable<T> compRenderCallable = assembler
						.createCompRenderCallable(ccopy);
				Rectangle bounds = compRenderCallable.getBounds();
				FutureTask<T> crtask = new FutureTask<T>(compRenderCallable);

				crExecutor.execute(crtask);
				ainfo.put(comp, bounds, crtask);
			}
		}

		compCachedFutureMap = ainfo;
		return ainfo;
	}

	/**
	 * @param fsn
	 * @param t
	 */
	protected void fireRenderingFinished(long fsn, T t) {
		RenderingFinishedListener[] ls = renderingFinishedListenerList
				.toArray(new RenderingFinishedListener[0]);
		for (RenderingFinishedListener lsnr : ls) {
			lsnr.renderingFinished(new RenderingFinishedEvent(fsn, t));
		}

	}

	public void addPlotPaintListener(RenderingFinishedListener listener) {
		renderingFinishedListenerList.add(listener);
	}

	public void removePlotPaintListener(RenderingFinishedListener listener) {
		renderingFinishedListenerList.remove(listener);
	}

}
