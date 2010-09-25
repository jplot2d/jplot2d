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

import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;

import org.jplot2d.element.Component;
import org.jplot2d.element.Element;
import org.jplot2d.element.Plot;
import org.jplot2d.layout.LayoutDirector;

/**
 * This Environment can host a plot instance and provide undo/redo ability.
 * 
 * @author Jingjing Li
 * 
 */
public abstract class PlotEnvironment extends Environment {

	protected final UndoManager<UndoMemento> changeHistory = new UndoManager<UndoMemento>();

	/**
	 * The plot proxy
	 */
	protected Plot plot;

	protected Plot plotImpl;

	public Plot getPlot() {
		synchronized (getGlobalLock()) {
			begin();
		}
		Plot result = plot;
		end();
		return result;
	}

	/**
	 * Sets a plot to this environment. The plot to be added must hosted by a
	 * dummy environment.
	 * 
	 * @param plot
	 */
	public void setPlot(Plot plot) {
		Environment oldEnv;
		synchronized (getGlobalLock()) {
			// remove the env of the given plot
			oldEnv = ((ElementEx) plot).getEnvironment();
			if (!(oldEnv instanceof DummyEnvironment)) {
				throw new IllegalArgumentException(
						"The plot to be added has been added a PlotEnvironment");
			}

			// check this environment is ready to host plot
			beginCommand("set plot");

			if (this.plot != null) {
				endCommand();
				throw new IllegalArgumentException(
						"This Environment has hosted a plot");
			}

			oldEnv.beginCommand("");

			// update environment for all adding components
			for (Element proxy : oldEnv.proxyMap.values()) {
				((ElementEx) proxy).setEnvironment(this);
			}
		}

		this.plot = plot;
		this.plotImpl = (Plot) ((ElementEx) plot).getImpl();

		componentAdded(plotImpl, oldEnv);

		endCommand();
		oldEnv.endCommand();
	}

	/**
	 * Remove a plot from this environment. A dummy environment will be created
	 * to host the removed plot.
	 * 
	 * @param plot
	 * @throws WarningException
	 */
	public void removePlot() {
		Environment nenv;
		synchronized (getGlobalLock()) {
			beginCommand("removePlot");

			// assign a dummy environment to the removed plot
			nenv = this.componentRemoving(plot);
			nenv.beginCommand("");

			// update environment for the removing component
			for (Element proxy : nenv.proxyMap.values()) {
				((ElementEx) proxy).setEnvironment(nenv);
			}
		}

		plot = null;
		plotImpl = null;

		componentRemoved(plotImpl);

		nenv.endCommand();
		endCommand();
	}

	@Override
	protected void commit() {

		/*
		 * Layout on plot proxy to ensure layout can set redraw-require
		 * properties.
		 */
		LayoutDirector ld = plot.getLayoutDirector();

		ld.layout(plot);

		Map<Element, Element> copyMap = makeUndoMemento();

		renderOnCommit(plot, copyMap);

	}

	public int getHistoryCapacity() {
		begin();
		int capacity = changeHistory.getCapacity();
		end();

		return capacity;
	}

	/**
	 * Returns <code>true</code> if undo is possible.
	 * 
	 * @return
	 */
	public boolean canUndo() {
		begin();
		boolean b = changeHistory.canUndo();
		end();

		return b;
	}

	/**
	 * Returns <code>true</code> if undo is possible.
	 * 
	 * @return
	 */
	public boolean canRedo() {
		begin();
		boolean b = changeHistory.canRedo();
		end();

		return b;
	}

	/**
	 * Undo the last change. If there is nothing to undo, an exception is
	 * thrown.
	 * 
	 * @return
	 */
	public void undo() {
		begin();

		UndoMemento memento = changeHistory.undo();
		if (memento == null) {
			throw new RuntimeException("Cannot undo");
		}

		Map<Element, Element> copyMap = restore(memento);

		renderOnCommit(plot, copyMap);

		end();
	}

	/**
	 * Undo the last change. If there is nothing to undo, an exception is
	 * thrown.
	 * 
	 * @return
	 */
	public void redo() {
		begin();

		UndoMemento memento = changeHistory.redo();
		if (memento == null) {
			throw new RuntimeException("Cannot redo");
		}

		Map<Element, Element> copyMap = restore(memento);

		renderOnCommit(plot, copyMap);

		end();
	}

	/**
	 * Create a undo memento and add it to change history.
	 * 
	 * @return a map, the key is impl element, the value is copy of element
	 */
	protected Map<Element, Element> makeUndoMemento() {

		// the value is copy of element, the key is original element
		Map<Element, Element> copyMap = new HashMap<Element, Element>();
		/*
		 * only when no history and all renderer is sync renderer and the
		 * component renderer is caller run, the deepCopy can be omitted.
		 */
		Plot plotRenderSafeCopy = plotImpl.deepCopy(copyMap);

		// build copy to proxy map
		Map<Element, Element> proxyMap2 = new HashMap<Element, Element>();
		for (Map.Entry<Element, Element> me : proxyMap.entrySet()) {
			Element element = me.getKey();
			Element proxy = me.getValue();
			Element copye = copyMap.get(element);
			proxyMap2.put(copye, proxy);
		}

		changeHistory.add(new UndoMemento(plotRenderSafeCopy, proxyMap2));

		return copyMap;
	}

	/**
	 * copy the memento back to working environment
	 * 
	 * @param proxyMap
	 */
	@SuppressWarnings("unchecked")
	private Map<Element, Element> restore(UndoMemento memento) {
		Map<Element, Element> mmtProxyMap = memento.getProxyMap();

		// the value is copy of element, the key is original element
		Map<Element, Element> copyMap = new HashMap<Element, Element>();
		// copy implements
		plotImpl = memento.getPlot().deepCopy(copyMap);

		Map<Element, Element> eleMap = new HashMap<Element, Element>();
		this.proxyMap.clear();
		for (Map.Entry<Element, Element> me : mmtProxyMap.entrySet()) {
			Element mmte = me.getKey();
			Element proxy = me.getValue();
			Element impl = copyMap.get(mmte);
			ElementIH ih = (ElementIH) Proxy.getInvocationHandler(proxy);
			ih.replaceImpl(impl);
			this.proxyMap.put(impl, proxy);

			if (impl instanceof Component) {
				cacheableComponentList.add((Component) impl);
			}
			eleMap.put(impl, mmte);
		}

		return eleMap;
	}

	/**
	 * Redraw the plot in this environment.
	 * 
	 * @param plot
	 *            the plot to be redrawn.
	 * @param copyMap
	 *            the key contains all components in the plot. The values are
	 *            the thread safe copies of keys.
	 */
	protected abstract void renderOnCommit(Plot plot,
			Map<Element, Element> copyMap);

	/* --- JPlot2DChangeListener --- */

	protected void fireChangeProcessed() {
		JPlot2DChangeListener[] ls = getPlotPropertyListeners();
		if (ls.length > 0) {
			JPlot2DChangeEvent evt = new JPlot2DChangeEvent(this, null);
			for (JPlot2DChangeListener lsnr : ls) {
				lsnr.batchModeChanged(evt);
			}
		}
	}

}
