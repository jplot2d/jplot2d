/**
 * Copyright 2010, 2011 Jingjing Li.
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

import java.awt.geom.Point2D;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jplot2d.element.Element;
import org.jplot2d.element.PComponent;
import org.jplot2d.element.Plot;
import org.jplot2d.element.impl.ComponentEx;
import org.jplot2d.element.impl.ElementEx;
import org.jplot2d.element.impl.PlotEx;
import org.jplot2d.warning.LogWarningManager;
import org.jplot2d.warning.WarningManager;

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
	protected volatile Plot plot;

	protected PlotEx plotImpl;

	private Map<ElementEx, ElementEx> copyMap;

	protected PlotEnvironment(boolean threadSafe) {
		super(threadSafe);
	}

	/**
	 * Sets a plot to this environment. The plot must hosted by a dummy environment. All warnings
	 * are logged to java logging facilities.
	 * 
	 * @param plot
	 */
	public void setPlot(Plot plot) {
		setPlot(plot, LogWarningManager.getInstance());
	}

	/**
	 * Sets a plot to this environment. The plot must hosted by a dummy environment.
	 * 
	 * @param plot
	 * @param warningReceiver
	 *            the WarningManager to receive and process warnings
	 */
	public void setPlot(Plot plot, WarningManager warningReceiver) {
		Environment oldEnv;
		synchronized (getGlobalLock()) {
			// remove the env of the given plot
			oldEnv = plot.getEnvironment();
			if (!(oldEnv instanceof DummyEnvironment)) {
				throw new IllegalArgumentException(
						"The plot to be added has been added a PlotEnvironment");
			}

			// check this environment is ready to host plot
			beginCommand("set plot");

			if (this.plot != null) {
				endCommand();
				throw new IllegalArgumentException("This Environment has hosted a plot");
			}

			oldEnv.beginCommand("");

			// update environment for all adding components
			for (Element proxy : oldEnv.proxyMap.values()) {
				((ElementAddition) proxy).setEnvironment(this);
			}
		}

		this.plot = plot;
		this.plotImpl = (PlotEx) ((ElementAddition) plot).getImpl();

		componentAdded(plotImpl, oldEnv);
		plotImpl.setWarningManager(warningReceiver);

		endCommand();
		oldEnv.endCommand();
	}

	public Plot getPlot() {
		return plot;
	}

	public WarningManager getWarningManager() {
		WarningManager result = null;
		begin();
		result = plotImpl.getWarningManager();
		end();
		return result;
	}

	public void setWarningManager(WarningManager warningReceiver) {
		beginCommand("setWarningReceiver");
		plotImpl.setWarningManager(warningReceiver);
		endCommand();
	}

	protected Map<ElementEx, ElementEx> getCopyMap() {
		return copyMap;
	}

	@Override
	protected void commit() {

		plotImpl.commit();

		copyMap = makeUndoMemento();

		if (plotImpl.isRerenderNeeded()) {
			plotImpl.clearRerenderNeeded();
			renderOnCommit();
		}

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
	 * Undo the last change. If there is nothing to undo, an exception is thrown.
	 * 
	 * @return
	 */
	public void undo() {
		begin();

		UndoMemento memento = changeHistory.undo();
		if (memento == null) {
			throw new RuntimeException("Cannot undo");
		}

		copyMap = restore(memento);

		renderOnCommit();

		end();
	}

	/**
	 * Undo the last change. If there is nothing to undo, an exception is thrown.
	 * 
	 * @return
	 */
	public void redo() {
		begin();

		UndoMemento memento = changeHistory.redo();
		if (memento == null) {
			throw new RuntimeException("Cannot redo");
		}

		copyMap = restore(memento);

		renderOnCommit();

		end();
	}

	/**
	 * Create a undo memento and add it to change history.
	 * 
	 * @return a map, the key is impl element, the value is copy of element
	 */
	protected Map<ElementEx, ElementEx> makeUndoMemento() {

		// the value is copy of element, the key is original element
		Map<ElementEx, ElementEx> copyMap = new HashMap<ElementEx, ElementEx>();
		/*
		 * only when no history and all renderer is sync renderer and the component renderer is
		 * caller run, the deepCopy can be omitted.
		 */
		PlotEx plotRenderSafeCopy = (PlotEx) plotImpl.copyStructure(copyMap);
		for (Map.Entry<ElementEx, ElementEx> me : copyMap.entrySet()) {
			me.getValue().copyFrom(me.getKey());
		}

		// build copy to proxy map
		Map<ElementEx, Element> proxyMap2 = new HashMap<ElementEx, Element>();
		for (Map.Entry<ElementEx, Element> me : proxyMap.entrySet()) {
			Element element = me.getKey();
			Element proxy = me.getValue();
			ElementEx copye = copyMap.get(element);
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
	private Map<ElementEx, ElementEx> restore(UndoMemento memento) {
		Map<ElementEx, Element> mmtProxyMap = memento.getProxyMap();

		// the value is copy of element, the key is original element
		Map<ElementEx, ElementEx> copyMap = new HashMap<ElementEx, ElementEx>();
		// copy implements
		plotImpl = (PlotEx) memento.getPlot().copyStructure(copyMap);

		Map<ElementEx, ElementEx> eleMap = new HashMap<ElementEx, ElementEx>();
		this.proxyMap.clear();
		for (Map.Entry<ElementEx, Element> me : mmtProxyMap.entrySet()) {
			ElementEx mmte = me.getKey();
			Element proxy = me.getValue();
			ElementEx impl = copyMap.get(mmte);
			ElementIH<ElementEx> ih = (ElementIH<ElementEx>) Proxy.getInvocationHandler(proxy);
			ih.replaceImpl(impl);
			this.proxyMap.put(impl, proxy);

			if (impl instanceof ComponentEx) {
				cacheableComponentList.add((ComponentEx) impl);
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
	 *            the key contains all components in the plot. The values are the thread safe copies
	 *            of keys.
	 */
	protected abstract void renderOnCommit();

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

	/**
	 * Return the top selectable component at the given location.
	 * 
	 * @param dp
	 *            the device point relative to top-left corner.
	 * @return
	 */
	public PComponent getSelectableCompnentAt(Point2D dp) {
		PComponent result = null;

		begin();

		/* add top plot if it's uncacheable */
		List<ComponentEx> ccl;
		if (!plotImpl.isCacheable()) {
			ccl = new ArrayList<ComponentEx>(cacheableComponentList);
			addOrder(0, ccl, plotImpl);
		} else {
			ccl = cacheableComponentList;
		}

		for (int i = cacheableComponentList.size() - 1; i >= 0; i--) {
			ComponentEx cacheableComp = cacheableComponentList.get(i);
			List<ComponentEx> uccList = subComponentMap.get(cacheableComp);
			for (int j = uccList.size() - 1; j >= 0; j--) {
				ComponentEx ucc = uccList.get(j);
				if (ucc.isSelectable()) {
					Point2D p = ucc.getPhysicalTransform().getDtoP(dp);
					if (ucc.getBounds().contains(p)) {
						result = (PComponent) proxyMap.get(ucc);
						break;
					}
				}
			}
		}

		end();

		return result;
	}

	/**
	 * Return the top plot at the given location.
	 * 
	 * @param dp
	 *            the device point relative to top-left corner.
	 * @return
	 */
	public Plot getPlotAt(Point2D dp) {
		Plot result = null;

		begin();

		/* add top plot if it's uncacheable */
		List<ComponentEx> ccl;
		if (!plotImpl.isCacheable()) {
			ccl = new ArrayList<ComponentEx>(cacheableComponentList);
			addOrder(0, ccl, plotImpl);
		} else {
			ccl = cacheableComponentList;
		}

		for (int i = cacheableComponentList.size() - 1; i >= 0; i--) {
			ComponentEx cacheableComp = cacheableComponentList.get(i);
			List<ComponentEx> uccList = subComponentMap.get(cacheableComp);
			for (int j = uccList.size() - 1; j >= 0; j--) {
				ComponentEx ucc = uccList.get(j);
				if (ucc instanceof PlotEx) {
					Point2D p = ucc.getPhysicalTransform().getDtoP(dp);
					if (ucc.getBounds().contains(p)) {
						result = (Plot) proxyMap.get(ucc);
						break;
					}
				}
			}
		}

		end();

		return result;
	}

}
