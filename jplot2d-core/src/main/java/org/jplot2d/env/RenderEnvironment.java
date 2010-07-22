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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jplot2d.element.Component;
import org.jplot2d.element.Element;
import org.jplot2d.element.Plot;
import org.jplot2d.layout.LayoutDirector;
import org.jplot2d.renderer.Exporter;
import org.jplot2d.renderer.Renderer;

/**
 * The environment is not thread-safe, and can only be used within a single
 * thread, such as servlet.
 * 
 * @author Jingjing Li
 * 
 */
public class RenderEnvironment extends PlotEnvironment {

	/**
	 * Redraw-require cacheable components
	 */
	protected final Set<Component> rrccs = new HashSet<Component>();

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
		if (impl instanceof Component) {
			switch (((Component) impl).getCacheMode()) {
			case PARENT:
				requireRedraw(impl.getParent());
				break;
			case SELF:
				rrccs.add((Component) impl);
				break;
			case NOCACHE: // do nothing
			}
		} else {
			requireRedraw(impl.getParent());
		}
	}

	@Override
	protected void commit() throws WarningException {

		/*
		 * Layout on plot proxy to ensure layout can set redraw-require
		 * properties.
		 */
		LayoutDirector ld = plot.getLayoutDirector();
		WarningException ex = null;
		try {
			ld.layout(plot);
		} catch (WarningException e) {
			ex = e;
		}

		makeUndoMemento();
		Plot plotRenderSafeCopy = getPlotRenderSafeCopy();

		Map<Component, Boolean> ccms = new HashMap<Component, Boolean>();
		for (Component comp : cacheableComponents) {
			ccms.put(comp, rrccs.contains(comp));
		}
		rrccs.clear();

		for (Renderer<?> r : getRenderers()) {
			r.render(plotRenderSafeCopy, ccms);
		}

		if (ex != null) {
			throw ex;
		}
	}

	@Override
	protected void commitUndoRedo() {
		Plot plotRenderSafeCopy = getPlotRenderSafeCopy();
		Map<Component, Boolean> ccms = new HashMap<Component, Boolean>();
		for (Component comp : cacheableComponents) {
			ccms.put(comp, Boolean.TRUE);
		}
		rrccs.clear();

		for (Renderer<?> r : getRenderers()) {
			r.render(plotRenderSafeCopy, ccms);
		}
	}
}
