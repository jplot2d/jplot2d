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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
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

	/**
	 * Contains all cacheable components in z-order
	 */
	protected final List<Component> zOrderedComponents = new ArrayList<Component>();

	protected final UndoManager<UndoMemento> changeHistory = new UndoManager<UndoMemento>();

	/**
	 * The plot proxy
	 */
	protected Plot plot;

	protected Plot plotImpl;

	private final List<JPlot2DChangeListener> plotStructureListenerList = Collections
			.synchronizedList(new ArrayList<JPlot2DChangeListener>());

	@Override
	void componentAdded(Component impl, Map<Element, Element> addedProxyMap) {
		super.componentAdded(impl, addedProxyMap);

		if (impl.isCacheable()) {
			zOrderedComponents.add(impl);
			updateOrder();
		}

		fireComponentAdded(getProxy(impl));
	}

	@Override
	void componentRemoving(Component impl) {
		fireComponentRemoving(getProxy(impl));
	}

	/**
	 * A component has been added to this environment
	 * 
	 * @param impl
	 * @param proxy
	 */
	@Override
	Map<Element, Element> componentRemoved(Component impl) {
		Map<Element, Element> result = super.componentRemoved(impl);

		for (Element e : result.keySet()) {
			if (e instanceof Component) {
				zOrderedComponents.remove((Component) e);
			}
		}

		return result;
	}

	@Override
	void elementPropertyChanged(Element impl) {
		fireElementPropertyChanged(getProxy(impl));
	}

	/**
	 * Called when a component z-order has been changed.
	 */
	@Override
	void componentZOrderChanged(Component impl) {
		updateOrder();
	}

	/**
	 * update the zOrderedComponents;
	 */
	private void updateOrder() {
		Component[] comps = proxyMap.keySet().toArray(
				new Component[proxyMap.size()]);
		Comparator<Component> zComparator = new Comparator<Component>() {

			public int compare(Component o1, Component o2) {
				return o1.getZOrder() - o2.getZOrder();
			}
		};
		Arrays.sort(comps, zComparator);

		zOrderedComponents.clear();
		for (Component comp : comps) {
			zOrderedComponents.add(comp);
		}
	}

	/**
	 * Called when a component's cache mode has been changed.
	 * 
	 * @param comp
	 */
	void componentCacaheModeChanged(Component comp) {
		/* cache mode changed should not trigger a redraw */
	}

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
			((DummyEnvironment) oldEnv).proxyMap.clear();

			// assign dummy env to the exist plot
			beginCommand("set plot");

			if (this.plot != null) {
				throw new IllegalArgumentException(
						"This Environment has hosted a plot");
			}

			((ElementEx) plot).setEnvironment(this);
		}

		this.plot = plot;
		this.plotImpl = (Plot) ((ElementEx) plot).getImpl();
		Map<Element, Element> proxyMap = new HashMap<Element, Element>();
		proxyMap.put(plotImpl, plot);
		componentAdded(plotImpl, proxyMap);

		endCommand();
	}

	/**
	 * Remove a plot from this environment. A dummy environment will be created
	 * to host the removed plot.
	 * 
	 * @param plot
	 * @throws WarningException
	 */
	public void removePlot() {
		synchronized (getGlobalLock()) {
			beginCommand("removePlot");
			plot = null;
			// assign a dummy env to the removed plot
			Environment nenv = this.createDummyEnvironment();
			Plot plotImpl = (Plot) ((ElementEx) plot).getImpl();
			Map<Element, Element> proxyMap = new HashMap<Element, Element>();
			proxyMap.put(plotImpl, plot);
			nenv.componentAdded(plotImpl, proxyMap);
			((ElementEx) plot).setEnvironment(nenv);

		}
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

		Map<Component, Component> compMap = new LinkedHashMap<Component, Component>();
		for (Component comp : zOrderedComponents) {
			compMap.put(comp, (Component) copyMap.get(comp));
		}
		renderOnCommit(plot, compMap);

	}

	public int getHistoryCapacity() {
		beginCommand("getHistoryCapacity");
		int capacity = changeHistory.getCapacity();
		endCommand();

		return capacity;
	}

	/**
	 * Returns <code>true</code> if undo is possible.
	 * 
	 * @return
	 */
	public boolean canUndo() {
		beginCommand("canUndo");
		boolean b = changeHistory.canUndo();
		endCommand();

		return b;
	}

	/**
	 * Returns <code>true</code> if undo is possible.
	 * 
	 * @return
	 */
	public boolean canRedo() {
		beginCommand("canRedo");
		boolean b = changeHistory.canRedo();
		endCommand();

		return b;
	}

	/**
	 * Undo the last change. If there is nothing to undo, an exception is
	 * thrown.
	 * 
	 * @return
	 */
	public void undo() {
		beginUndoRedo("undo");

		UndoMemento memento = changeHistory.undo();
		if (memento == null) {
			throw new RuntimeException("Cannot undo");
		}

		Map<Element, Element> copyMap = restore(memento);

		Map<Component, Component> compMap = new LinkedHashMap<Component, Component>();
		for (Component comp : zOrderedComponents) {
			compMap.put(comp, (Component) copyMap.get(comp));
		}
		renderOnCommit(plot, compMap);

		endUndoRedo();
	}

	/**
	 * Undo the last change. If there is nothing to undo, an exception is
	 * thrown.
	 * 
	 * @return
	 */
	public void redo() {
		beginUndoRedo("redo");

		UndoMemento memento = changeHistory.redo();
		if (memento == null) {
			throw new RuntimeException("Cannot redo");
		}

		Map<Element, Element> copyMap = restore(memento);

		Map<Component, Component> compMap = new LinkedHashMap<Component, Component>();
		for (Component comp : zOrderedComponents) {
			compMap.put(comp, (Component) copyMap.get(comp));
		}
		renderOnCommit(plot, compMap);

		endUndoRedo();
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

		Map<Element, Element> proxyMap2 = new LinkedHashMap<Element, Element>();
		// add components copy map in z-order
		for (Component comp : zOrderedComponents) {
			Element copye = copyMap.get(comp);
			Element proxy = proxyMap.get(comp);
			proxyMap2.put(copye, proxy);
		}
		// add non-component elements to proxyMap2
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
				zOrderedComponents.add((Component) impl);
			}
			eleMap.put(impl, mmte);
		}

		return eleMap;
	}

	protected void beginUndoRedo(String msg) {
		begin();
	}

	protected void endUndoRedo() {
		end();
	}

	protected void renderOnCommit(Plot plot, Map<Component, Component> compMap) {
		// TODO Auto-generated method stub

	}

	/* --- JPlot2DChangeListener --- */

	public JPlot2DChangeListener[] getPlotPropertyListeners() {
		return plotStructureListenerList.toArray(new JPlot2DChangeListener[0]);
	}

	public void addPlotPropertyListener(JPlot2DChangeListener listener) {
		plotStructureListenerList.add(listener);
	}

	public void removePlotPropertyListener(JPlot2DChangeListener listener) {
		plotStructureListenerList.remove(listener);
	}

	protected void fireChangeProcessed() {
		JPlot2DChangeListener[] ls = getPlotPropertyListeners();
		if (ls.length > 0) {
			JPlot2DChangeEvent evt = new JPlot2DChangeEvent(this, null);
			for (JPlot2DChangeListener lsnr : ls) {
				lsnr.batchModeChanged(evt);
			}
		}
	}

	private void fireComponentAdded(Component engine) {
		JPlot2DChangeListener[] ls = getPlotPropertyListeners();
		if (ls.length > 0) {
			JPlot2DChangeEvent evt = new JPlot2DChangeEvent(this,
					new Element[] { engine });
			for (JPlot2DChangeListener lsnr : ls) {
				lsnr.componentCreated(evt);
			}
		}
	}

	private void fireComponentRemoving(Component engine) {
		JPlot2DChangeListener[] ls = getPlotPropertyListeners();
		if (ls.length > 0) {
			JPlot2DChangeEvent evt = new JPlot2DChangeEvent(this,
					new Element[] { engine });
			for (JPlot2DChangeListener lsnr : ls) {
				lsnr.componentRemoved(evt);
			}
		}
	}

	/**
	 * Notify all registered JPlot2DChangeListener that the properties of the
	 * given element has been changed.
	 * 
	 * @param element
	 */
	private void fireElementPropertyChanged(Element element) {
		JPlot2DChangeListener[] ls = getPlotPropertyListeners();
		if (ls.length > 0) {
			JPlot2DChangeEvent evt = new JPlot2DChangeEvent(this,
					new Element[] { element });
			for (JPlot2DChangeListener lsnr : ls) {
				lsnr.enginePropertiesChanged(evt);
			}
		}
	}

}
