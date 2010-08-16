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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jplot2d.element.Component;
import org.jplot2d.element.Element;
import org.jplot2d.element.Plot;
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

	/**
	 * Redraw-required self cache components
	 */
	protected final Set<Component> rrSccs = new HashSet<Component>();

	private List<Renderer<?>> rendererList = Collections
			.synchronizedList(new ArrayList<Renderer<?>>());

	public RenderEnvironment() {

	}

	@Override
	public Environment createDummyEnvironment() {
		return new DummyEnvironment();
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
	void requireRedraw(Element impl) {
		if (impl instanceof Component && ((Component) impl).isCacheable()) {
			rrSccs.add((Component) impl);
		} else {
			requireRedraw(impl.getParent());
		}
	}

	@Override
	protected void renderOnCommit(Plot plot, Map<Component, Component> compMap) {

		// unmodified components
		List<Component> umsccs = new ArrayList<Component>();
		for (Component comp : compMap.keySet()) {
			if (!rrSccs.contains(comp)) {
				umsccs.add(comp);
			}
		}
		rrSccs.clear();

		for (Renderer<?> r : getRenderers()) {
			r.render(plot, compMap, umsccs);
		}

	}

}
