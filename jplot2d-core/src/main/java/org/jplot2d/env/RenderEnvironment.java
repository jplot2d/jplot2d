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
package org.jplot2d.env;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.jplot2d.element.impl.ComponentEx;
import org.jplot2d.element.impl.PlotEx;
import org.jplot2d.renderer.Renderer;

/**
 * This environment extends plot environment to add ability to render a plot.
 * 
 * @author Jingjing Li
 * 
 */
public class RenderEnvironment extends PlotEnvironment {

	private final List<Renderer<?>> rendererList = Collections
			.synchronizedList(new ArrayList<Renderer<?>>());

	/**
	 * Construct a environment to render the given plot.
	 * 
	 * @param threadSafe
	 *            if <code>false</code>, all plot properties can only be changed
	 *            within a single thread, such as servlet. if <code>true</code>,
	 *            all plot properties can be safely changed by multiple threads.
	 */
	public RenderEnvironment(boolean threadSafe) {
		super(threadSafe);
	}

	public Renderer<?>[] getRenderers() {
		return rendererList.toArray(new Renderer[0]);
	}

	public boolean addRenderer(Renderer<?> renderer) {
		return rendererList.add(renderer);
	}

	public boolean removeRenderer(Renderer<?> renderer) {
		return rendererList.remove(renderer);
	}

	/**
	 * Export plot to the given renderer.
	 * 
	 * @param renderer
	 */
	public void exportPlot(Renderer<?> renderer) {
		begin();

		List<ComponentEx> umCachableComps = new ArrayList<ComponentEx>();
		Map<ComponentEx, ComponentEx> cacheableCompMap = getCacheableCompMap(umCachableComps);
		Map<ComponentEx, ComponentEx[]> subcompsMap = getSubcompsMap();

		renderer.render((PlotEx) getCopyMap().get(plotImpl), cacheableCompMap,
				umCachableComps, subcompsMap);

		end();
	}

	@Override
	protected void renderOnCommit() {
		List<ComponentEx> umCachableComps = new ArrayList<ComponentEx>();
		Map<ComponentEx, ComponentEx> cacheableCompMap = getCacheableCompMap(umCachableComps);
		Map<ComponentEx, ComponentEx[]> subcompsMap = getSubcompsMap();

		for (Renderer<?> r : getRenderers()) {
			r.render((PlotEx) getCopyMap().get(plotImpl), cacheableCompMap,
					umCachableComps, subcompsMap);
		}
	}

	/**
	 * Returns a cacheable component map with unmodified components' uid filled
	 * in the given list. The returned map contains the top plot, even if the
	 * plot is uncacheable.
	 * 
	 * @param umCachableComps
	 *            a list will be filled with unmodified components' uid
	 * @return a map that key is uid of value and value is cacheable components
	 */
	private Map<ComponentEx, ComponentEx> getCacheableCompMap(
			List<ComponentEx> umCachableComps) {
		/*
		 * when adding a cacheable component, the requireRedraw is not called on
		 * it. So we must figure out what components are unmodified.
		 */
		Map<ComponentEx, ComponentEx> cacheableCompMap = new LinkedHashMap<ComponentEx, ComponentEx>();

		/* add top plot if it's uncacheable */
		List<ComponentEx> ccl;
		if (!plotImpl.isCacheable()) {
			ccl = new ArrayList<ComponentEx>(cacheableComponentList);
			addOrder(0, ccl, plotImpl);
		} else {
			ccl = cacheableComponentList;
		}

		for (ComponentEx comp : ccl) {
			ComponentEx copy = (ComponentEx) getCopyMap().get(comp);
			assert (copy != null) : "Null copy of Component " + comp;
			cacheableCompMap.put(comp, copy);
			// unmodified components
			if (((ComponentEx) comp).isRedrawNeeded()) {
				((ComponentEx) comp).clearRedrawNeeded();
			} else {
				umCachableComps.add(comp);
			}
		}

		return cacheableCompMap;
	}

	/**
	 * @return a map key is safe copy of cacheable components and values is safe
	 *         copies of key's subcomponents.
	 */
	private Map<ComponentEx, ComponentEx[]> getSubcompsMap() {
		// build sub-component map
		Map<ComponentEx, ComponentEx[]> subcompsMap = new HashMap<ComponentEx, ComponentEx[]>();
		for (Map.Entry<ComponentEx, List<ComponentEx>> me : subComponentMap
				.entrySet()) {
			ComponentEx key = me.getKey();
			List<ComponentEx> sublist = me.getValue();
			int size = sublist.size();
			ComponentEx[] copys = new ComponentEx[size];
			for (int i = 0; i < size; i++) {
				ComponentEx copy = (ComponentEx) getCopyMap().get(
						sublist.get(i));
				assert (copy != null) : "Null copy of Component "
						+ sublist.get(i);
				copys[i] = copy;
			}
			subcompsMap.put((ComponentEx) getCopyMap().get(key), copys);
		}

		return subcompsMap;
	}

}
