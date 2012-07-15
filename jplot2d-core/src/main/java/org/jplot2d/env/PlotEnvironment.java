/**
 * Copyright 2010-2012 Jingjing Li.
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
import java.util.ArrayList;
import java.util.List;

import org.jplot2d.element.Element;
import org.jplot2d.element.PComponent;
import org.jplot2d.element.Plot;
import org.jplot2d.element.impl.ComponentEx;
import org.jplot2d.element.impl.PlotEx;
import org.jplot2d.notice.LoggingNotifier;
import org.jplot2d.notice.Notifier;

/**
 * This Environment can host a plot instance.
 * 
 * @author Jingjing Li
 * 
 */
public class PlotEnvironment extends Environment {

	/**
	 * The plot proxy
	 */
	protected volatile Plot plot;

	protected PlotEx plotImpl;

	public PlotEnvironment(boolean threadSafe) {
		super(threadSafe);
	}

	/**
	 * Sets a plot to this environment. The plot must hosted by a dummy environment. All notices are
	 * logged to java logging facilities.
	 * 
	 * @param plot
	 */
	public void setPlot(Plot plot) {
		setPlot(plot, LoggingNotifier.getInstance());
	}

	/**
	 * Sets a plot to this environment. The plot must hosted by a dummy environment.
	 * 
	 * @param plot
	 * @param notifier
	 *            the notifier to receive and process notices
	 */
	public void setPlot(Plot plot, Notifier notifier) {
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

		this.notifier = notifier;
		plotImpl.setNotifier(notifier);

		endCommand();
		oldEnv.endCommand();
	}

	public Plot getPlot() {
		return plot;
	}

	public Notifier getNotifier() {
		Notifier result = null;
		begin();
		result = notifier;
		end();
		return result;
	}

	public void setNotifier(Notifier notifier) {
		beginCommand("setNotifier");
		this.notifier = notifier;
		plotImpl.setNotifier(notifier);
		endCommand();
	}

	@Override
	protected void commit() {
		plotImpl.commit();
		fireChangeProcessed();
	}

	/* --- JPlot2DChangeListener --- */

	protected void fireChangeProcessed() {
		ElementChangeListener[] ls = getPlotPropertyListeners();
		if (ls.length > 0) {
			ElementChangeEvent evt = new ElementChangeEvent(this, null);
			for (ElementChangeListener lsnr : ls) {
				lsnr.propertyChangesProcessed(evt);
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
					Point2D p = ucc.getPaperTransform().getDtoP(dp);
					if (ucc.getSelectableBounds().contains(p)) {
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
					Point2D p = ucc.getPaperTransform().getDtoP(dp);
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
