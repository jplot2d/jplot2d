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

import org.jplot2d.element.Component;
import org.jplot2d.element.Element;
import org.jplot2d.element.Plot;
import org.jplot2d.element.impl.ComponentImpl;
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
	protected void renderOnCommit(Plot plot, Map<Element, Element> copyMap) {

		/*
		 * when adding a cacheable component, the requireRedraw is not called on
		 * it. So we must figure out what components are unmodified.
		 */

		List<Component> umCachableComps = new ArrayList<Component>();
		Map<Component, Component> compMap = new LinkedHashMap<Component, Component>();
		for (Component comp : cacheableComponentList) {
			compMap.put(comp, (Component) copyMap.get(comp));
			// unmodified components
			if (!((ComponentImpl) comp).isRedrawNeeded()) {
				umCachableComps.add(comp);
			}
			((ComponentImpl) comp).clearRedrawNeeded();
		}

		// build sub-component map
		Map<Component, Component[]> subcompsMap = new HashMap<Component, Component[]>();
		for (Map.Entry<Component, List<Component>> me : subComponentMap
				.entrySet()) {
			Component key = me.getKey();
			List<Component> sublist = me.getValue();
			int size = sublist.size();
			Component[] copys = new Component[size];
			for (int i = 0; i < size; i++) {
				Component scopy = (Component) copyMap.get(sublist.get(i));
				copys[i] = scopy;
			}
			subcompsMap.put(key, copys);
		}

		for (Renderer<?> r : getRenderers()) {
			r.render(plot, compMap, umCachableComps, subcompsMap);
		}

	}
}
