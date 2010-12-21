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

import org.jplot2d.element.Element;
import org.jplot2d.element.Plot;
import org.jplot2d.element.impl.AxisEx;
import org.jplot2d.element.impl.ComponentEx;
import org.jplot2d.element.impl.ElementEx;
import org.jplot2d.element.impl.PlotEx;
import org.jplot2d.element.impl.SubplotEx;
import org.jplot2d.element.impl.ViewportAxisEx;

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

	protected PlotEx plotImpl;

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
			oldEnv = plot.getEnvironment();
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
				((ElementAddition) proxy).setEnvironment(this);
			}
		}

		this.plot = plot;
		this.plotImpl = (PlotEx) ((ElementAddition) plot).getImpl();

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

		synchronized (getGlobalLock()) {
			beginCommand("removePlot");

			// assign a dummy environment to the removed plot
			this.componentRemoving(plotImpl);

			plot = null;
			plotImpl = null;

			Environment nenv = componentRemoved(plotImpl, plotImpl);
			// update environment for the removing component
			for (Element proxy : nenv.proxyMap.values()) {
				((ElementAddition) proxy).setEnvironment(nenv);
			}
		}

		endCommand();
	}

	@Override
	protected void commit() {

		/*
		 * Axis a special component. Its length can be set by layout manager,
		 * but its thick depends on its internal status, such as tick height,
		 * labels. The auto range must be re-calculated after all axes length
		 * are set. So we cannot use deep-first validate tree. we must layout
		 * all subplot, then calculate auto range, then validate all axes.
		 */
		calcAxesThickness();

		while (!plotImpl.isValid()) {

			/*
			 * Laying out axes may register some axis that ticks need be
			 * re-calculated
			 */
			plotImpl.validate();

			/*
			 * Auto range axes MUST be executed after they are laid out. <br>
			 * Auto range axes may register some axis that ticks need be
			 * re-calculated
			 */
			calcPendingLockGroupAutoRange();

			/*
			 * Calculating axes tick may invalidate some axis. Their metrics
			 * need be re-calculated
			 */
			calcAxesTick(plotImpl);

			/* thickness changes may invalidate the plot */
			calcAxesThickness();

		}

		Map<ElementEx, ElementEx> copyMap = makeUndoMemento();

		renderOnCommit(plotImpl, copyMap);

	}

	/**
	 * 
	 */
	private void calcPendingLockGroupAutoRange() {
		// TODO Auto-generated method stub

	}

	/**
	 * Calculate axis ticks according to its length, range and tick properties.
	 */
	private void calcAxesThickness() {
		for (SubplotEx sp : plotImpl.getSubplots()) {
			for (ViewportAxisEx va : sp.getXViewportAxes()) {
				for (AxisEx axis : va.getAxes()) {
					axis.calcThickness();
				}
			}
			for (ViewportAxisEx va : sp.getYViewportAxes()) {
				for (AxisEx axis : va.getAxes()) {
					axis.calcThickness();
				}
			}
		}
	}

	/**
	 * Calculate axis ticks according to its length, range and tick properties.
	 */
	private void calcAxesTick(SubplotEx subplot) {
		for (ViewportAxisEx va : subplot.getXViewportAxes()) {
			for (AxisEx axis : va.getAxes()) {
				axis.calcTicks();
			}
		}
		for (ViewportAxisEx va : subplot.getYViewportAxes()) {
			for (AxisEx axis : va.getAxes()) {
				axis.calcTicks();
			}
		}
		for (SubplotEx sp : plotImpl.getSubplots()) {
			calcAxesTick(sp);
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

		Map<ElementEx, ElementEx> copyMap = restore(memento);

		renderOnCommit(plotImpl, copyMap);

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

		Map<ElementEx, ElementEx> copyMap = restore(memento);

		renderOnCommit(plotImpl, copyMap);

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
		 * only when no history and all renderer is sync renderer and the
		 * component renderer is caller run, the deepCopy can be omitted.
		 */
		PlotEx plotRenderSafeCopy = (PlotEx) plotImpl.deepCopy(copyMap);

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
		plotImpl = (PlotEx) memento.getPlot().deepCopy(copyMap);

		Map<ElementEx, ElementEx> eleMap = new HashMap<ElementEx, ElementEx>();
		this.proxyMap.clear();
		for (Map.Entry<ElementEx, Element> me : mmtProxyMap.entrySet()) {
			ElementEx mmte = me.getKey();
			Element proxy = me.getValue();
			ElementEx impl = copyMap.get(mmte);
			ElementIH<ElementEx> ih = (ElementIH<ElementEx>) Proxy
					.getInvocationHandler(proxy);
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
	 *            the key contains all components in the plot. The values are
	 *            the thread safe copies of keys.
	 */
	protected abstract void renderOnCommit(PlotEx plot,
			Map<ElementEx, ElementEx> copyMap);

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
