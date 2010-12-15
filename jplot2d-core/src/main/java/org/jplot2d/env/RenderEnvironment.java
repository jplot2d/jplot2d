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
import org.jplot2d.element.impl.ElementEx;
import org.jplot2d.element.impl.PlotEx;
import org.jplot2d.renderer.Exporter;
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

	public void exportPlot(Exporter exporter) {

	}

	@Override
	protected void renderOnCommit(PlotEx plot, Map<ElementEx, ElementEx> copyMap) {

		/*
		 * when adding a cacheable component, the requireRedraw is not called on
		 * it. So we must figure out what components are unmodified.
		 */

		List<ComponentEx> umCachableComps = new ArrayList<ComponentEx>();
		Map<ComponentEx, ComponentEx> compMap = new LinkedHashMap<ComponentEx, ComponentEx>();
		for (ComponentEx comp : cacheableComponentList) {
			ComponentEx copy = (ComponentEx) copyMap.get(comp);
			assert (copy != null) : "Null copy of Component " + comp;
			compMap.put(comp, copy);
			// unmodified components
			if (!((ComponentEx) comp).isRedrawNeeded()) {
				umCachableComps.add(comp);
			}
			((ComponentEx) comp).clearRedrawNeeded();
		}

		// build sub-component map
		Map<ComponentEx, ComponentEx[]> subcompsMap = new HashMap<ComponentEx, ComponentEx[]>();
		for (Map.Entry<ComponentEx, List<ComponentEx>> me : subComponentMap
				.entrySet()) {
			ComponentEx key = me.getKey();
			List<ComponentEx> sublist = me.getValue();
			int size = sublist.size();
			ComponentEx[] copys = new ComponentEx[size];
			for (int i = 0; i < size; i++) {
				ComponentEx copy = (ComponentEx) copyMap.get(sublist.get(i));
				assert (copy != null) : "Null copy of Component "
						+ sublist.get(i);
				copys[i] = copy;
			}
			subcompsMap.put(key, copys);
		}

		for (Renderer<?> r : getRenderers()) {
			r.render(plot, compMap, umCachableComps, subcompsMap);
		}

	}
}
