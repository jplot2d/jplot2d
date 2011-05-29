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
import org.jplot2d.renderer.Renderer;

/**
 * This environment extends plot environment to add ability to render a plot.
 * This class is not thread-safe, and can only be used within a single thread,
 * such as servlet.
 * 
 * @author Jingjing Li
 * 
 */
public class RenderEnvironment extends PlotEnvironment {

	private List<Renderer<?>> rendererList = Collections
			.synchronizedList(new ArrayList<Renderer<?>>());

	public RenderEnvironment() {

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

		end();

		renderer.render(plotImpl, cacheableCompMap, umCachableComps,
				subcompsMap);
	}

	@Override
	protected void renderOnCommit() {
		List<ComponentEx> umCachableComps = new ArrayList<ComponentEx>();
		Map<ComponentEx, ComponentEx> cacheableCompMap = getCacheableCompMap(umCachableComps);
		Map<ComponentEx, ComponentEx[]> subcompsMap = getSubcompsMap();

		for (Renderer<?> r : getRenderers()) {
			r.render(plotImpl, cacheableCompMap, umCachableComps, subcompsMap);
		}
	}

	/**
	 * @param umCachableComps
	 *            unmodified comps
	 * @return a map key comp value saft copy of keys
	 */
	private Map<ComponentEx, ComponentEx> getCacheableCompMap(
			List<ComponentEx> umCachableComps) {
		/*
		 * when adding a cacheable component, the requireRedraw is not called on
		 * it. So we must figure out what components are unmodified.
		 */
		Map<ComponentEx, ComponentEx> cacheableCompMap = new LinkedHashMap<ComponentEx, ComponentEx>();

		for (ComponentEx comp : cacheableComponentList) {
			ComponentEx copy = (ComponentEx) getCopyMap().get(comp);
			assert (copy != null) : "Null copy of Component " + comp;
			cacheableCompMap.put(comp, copy);
			// unmodified components
			if (!((ComponentEx) comp).isRedrawNeeded()) {
				umCachableComps.add(comp);
			}
			((ComponentEx) comp).clearRedrawNeeded();
		}

		return cacheableCompMap;
	}

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
			subcompsMap.put(key, copys);
		}

		return subcompsMap;
	}

}
