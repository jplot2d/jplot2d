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
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

import org.jplot2d.element.Component;
import org.jplot2d.element.Element;
import org.jplot2d.element.Plot;

/**
 * This Environment can host a plot instance and provide undo/redo ability.
 * 
 * @author Jingjing Li
 * 
 */
public abstract class PlotEnvironment extends Environment {

	protected final Set<Component> zOrderedComponents = new LinkedHashSet<Component>();

	/**
	 * Ordered cacheable component set. The order is z-order.
	 */
	protected final Set<Component> cacheableComponents = new LinkedHashSet<Component>();

	protected final UndoManager<UndoMemento> changeHistory = new UndoManager<UndoMemento>();

	protected Plot plot;

	protected Plot plotImpl;

	private final List<JPlot2DChangeListener> plotStructureListenerList = Collections
			.synchronizedList(new ArrayList<JPlot2DChangeListener>());

	void componentAdded(Component impl, Map<Element, Element> addedProxyMap) {
		super.componentAdded(impl, addedProxyMap);
		fireComponentAdded(getProxy(impl));
	}

	/**
	 * Called when a component z-order changed.
	 */
	void componentZOrderChanged(Component impl) {
		updateOrder();
	}

	void componentRemoving(Component impl) {
		fireComponentRemoving(getProxy(impl));
	}

	/**
	 * A component has been added to this environment
	 * 
	 * @param impl
	 * @param proxy
	 */
	Map<Element, Element> componentRemoved(Component impl) {
		Map<Element, Element> result = super.componentRemoved(impl);

		for (Element e : result.keySet()) {
			if (e instanceof Component) {
				zOrderedComponents.remove((Component) e);
			}
		}

		return result;
	}

	void elementPropertyChanged(Element impl) {
		fireElementPropertyChanged(getProxy(impl));
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
			if (comp.getCacheMode() != Component.CacheMode.PARENT) {
				zOrderedComponents.add(comp);
			}
		}
	}

	public Plot getPlot() {
		AtomicReference<Plot> ar = new AtomicReference<Plot>();
		beginCommand("set plot");
		ar.set(plot);
		try {
			endCommand();
		} catch (WarningException e) {
			// should net happen
		}
		return ar.get();
	}

	public void setPlot(Plot plot) throws WarningException {
		Environment oldEnv;
		synchronized (getGlobalLock()) {
			beginCommand("set plot");
			// assign dummy env to the exist plot
			if (this.plot != null) {
				Environment nenv = this.createDummyEnvironment();
				((ElementEx) this.plot).setEnvironment(nenv);
			}

			// remove the env of the given plot
			oldEnv = ((ElementEx) plot).getEnvironment();
			oldEnv.beginCommand("remove plot");
			if (oldEnv instanceof PlotEnvironment) {
				((PlotEnvironment) oldEnv).plot = null;
			}
			oldEnv.endCommand();

			((ElementEx) plot).setEnvironment(this);
			this.plot = plot;
			this.plotImpl = (Plot) ((ElementEx) plot).getImpl();
		}
		endCommand();
	}

	public int getHistoryCapacity() {
		beginCommand("getHistoryCapacity");
		int capacity = changeHistory.getCapacity();
		try {
			endCommand();
		} catch (WarningException e) {
			// should not happen
		}
		return capacity;
	}

	protected Plot getPlotRenderSafeCopy() {
		return changeHistory.current().getPlot();
	}

	/**
	 * Returns <code>true</code> if undo is possible.
	 * 
	 * @return
	 */
	public boolean canUndo() {
		beginCommand("canUndo");
		boolean b = changeHistory.canUndo();
		try {
			endCommand();
		} catch (WarningException e) {
			// should not happen
		}
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
		restore(memento);

		try {
			endCommand();
		} catch (WarningException e) {
			// should not happen
		}
	}

	/**
	 * Returns <code>true</code> if undo is possible.
	 * 
	 * @return
	 */
	public boolean canRedo() {
		beginCommand("canRedo");
		boolean b = changeHistory.canRedo();
		try {
			endCommand();
		} catch (WarningException e) {
			// should not happen
		}
		return b;
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
		restore(memento);
		commitUndoRedo();

		endUndoRedo();
	}

	protected void makeUndoMemento() {

		// the value is copy of element, the key is original element
		Map<Element, Element> copyMap = new HashMap<Element, Element>();
		/*
		 * only when no history and all renderer is sync renderer and the
		 * component renderer is caller run, the deepCopy can be omitted.
		 */
		Plot plotRenderSafeCopy = plotImpl.deepCopy(copyMap);

		Map<Element, Element> proxyMap2 = new HashMap<Element, Element>();
		for (Map.Entry<Element, Element> me : proxyMap.entrySet()) {
			Element element = me.getKey();
			Element proxy = me.getValue();
			Element copye = copyMap.get(element);
			proxyMap2.put(copye, proxy);
		}

		changeHistory.add(new UndoMemento(plotRenderSafeCopy, proxyMap2));

	}

	/**
	 * copy the memento back to working environment
	 * 
	 * @param proxyMap
	 */
	@SuppressWarnings("unchecked")
	private void restore(UndoMemento memento) {
		Map<Element, Element> mmtProxyMap = memento.getProxyMap();

		// the value is copy of element, the key is original element
		Map<Element, Element> copyMap = new HashMap<Element, Element>();
		// copy implements
		plotImpl = memento.getPlot().deepCopy(copyMap);

		this.proxyMap.clear();
		for (Map.Entry<Element, Element> me : mmtProxyMap.entrySet()) {
			Element mmte = me.getKey();
			Element proxy = me.getValue();
			Element impl = copyMap.get(mmte);
			ElementIH ih = (ElementIH) Proxy.getInvocationHandler(proxy);
			ih.replaceImpl(impl);
			this.proxyMap.put(impl, proxy);
		}

	}

	protected void beginUndoRedo(String msg) {

	}

	protected void endUndoRedo() {

	}

	/**
	 * Called when undo/redo has been made.
	 */
	protected abstract void commitUndoRedo();

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
