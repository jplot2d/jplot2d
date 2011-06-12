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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Logger;

import org.jplot2d.element.Component;
import org.jplot2d.element.Element;
import org.jplot2d.element.impl.ComponentEx;
import org.jplot2d.element.impl.ElementEx;
import org.jplot2d.util.WarningMessage;

/**
 * An environment that a plot can realization. Once a plot is put an
 * environment, changes on the plot can cause re-layout or redraw, according to
 * the changed property.
 * <p>
 * Renderers can be added to an environment to get the rendered result
 * synchronously or asynchronously.
 * 
 * @author Jingjing Li
 * 
 */
public abstract class Environment {

	static Logger logger = Logger.getLogger("org.jplot2d.env");

	/**
	 * The global locking object for get/set environment.
	 */
	private static final Object LOCK = new Object();

	private static final int MAX_BATCH_DEPTH = 64;

	private final ReentrantLock lock;

	/**
	 * batch SN in every depth. batchSND[n], n is batch depth -1. the value is
	 * the SN for the batch depth
	 */
	private int[] batchSND = new int[MAX_BATCH_DEPTH];

	private int batchDepth;

	/**
	 * A impl to proxy map that contains all element in this environment.
	 */
	protected final Map<ElementEx, Element> proxyMap = new HashMap<ElementEx, Element>();

	/**
	 * Contains all cacheable components in z-order
	 */
	protected final List<ComponentEx> cacheableComponentList = new ArrayList<ComponentEx>();

	/**
	 * The key is cacheable components or uncacheable top component; the value
	 * is key's uncacheable descendants, include the key itself, in z-order.
	 */
	protected final Map<ComponentEx, List<ComponentEx>> subComponentMap = new HashMap<ComponentEx, List<ComponentEx>>();

	private final List<JPlot2DChangeListener> plotStructureListenerList = Collections
			.synchronizedList(new ArrayList<JPlot2DChangeListener>());

	private final List<WarningMessage> warnings = new ArrayList<WarningMessage>();

	protected static Object getGlobalLock() {
		return LOCK;
	}

	protected Environment(boolean threadSafe) {
		if (threadSafe) {
			lock = new ReentrantLock();
		} else {
			lock = null;
		}
	}

	/**
	 * Returns the proxy wrapper of the given element.
	 * 
	 * @param impl
	 * @return
	 */
	final Element getProxy(ElementEx impl) {
		return proxyMap.get(impl);
	}

	/**
	 * Remove the orphan element from this environment, and create a dummy
	 * environment for it.
	 * 
	 * @param impl
	 * @return
	 */
	Environment removeOrphan(ElementEx impl) {

		Map<ElementEx, Element> removedProxyMap = new HashMap<ElementEx, Element>();

		removedProxyMap.put(impl, proxyMap.remove(impl));

		// remove the elements whoes parent has been removed
		Iterator<Entry<ElementEx, Element>> ite = proxyMap.entrySet()
				.iterator();
		while (ite.hasNext()) {
			Entry<ElementEx, Element> e = ite.next();
			if (removedProxyMap.containsKey(e.getKey().getParent())) {
				removedProxyMap.put(e.getKey(), e.getValue());
				ite.remove();
			}
		}

		DummyEnvironment result = createDummyEnvironment();
		result.proxyMap.putAll(removedProxyMap);
		return result;
	}

	/**
	 * Called after a component has been added to this environment, and its
	 * parent has been set.
	 * 
	 * @param element
	 *            the added element
	 * @param env
	 *            the environment of the added element
	 */
	void elementAdded(ElementEx element, Environment env) {

		proxyMap.putAll(env.proxyMap);
		addOrder(cacheableComponentList, env.cacheableComponentList);
		subComponentMap.putAll(env.subComponentMap);
		warnings.addAll(env.warnings);

		env.proxyMap.clear();
		env.cacheableComponentList.clear();
		env.subComponentMap.clear();
		env.warnings.clear();
	}

	/**
	 * Called after a component has been added to this environment, and its
	 * parent has been set. Or called when adding a top plot to a plot
	 * environment. The added component either is cacheable, or has a cacheable
	 * parent.
	 * 
	 * @param comp
	 *            the added ComponentEx
	 * @param env
	 *            the environment of the added component
	 */
	void componentAdded(ComponentEx comp, Environment env) {

		proxyMap.putAll(env.proxyMap);
		addOrder(cacheableComponentList, env.cacheableComponentList);
		subComponentMap.putAll(env.subComponentMap);
		warnings.addAll(env.warnings);

		/* merge uncacheable component tree */
		if (!comp.isCacheable()) {
			Component cc = getCacheableAncestor(comp);
			if (cc != comp) {
				/*
				 * remove the added top uncacheable component and add it to the
				 * list of its parent
				 */
				addOrder(subComponentMap.get(cc), subComponentMap.remove(comp));
			}
		}

		env.proxyMap.clear();
		env.cacheableComponentList.clear();
		env.subComponentMap.clear();
		env.warnings.clear();

		fireComponentAdded((Component) getProxy(comp));
	}

	/**
	 * A component is going to be removed from this environment
	 * 
	 * @param comp
	 * @param elements
	 *            elements to be removed.
	 */
	void componentRemoving(ComponentEx comp) {
		fireComponentRemoving((Component) getProxy(comp));
	}

	/**
	 * Called when a component has been removed from its parent
	 * 
	 * @param oldParent
	 * @param comp
	 *            the removed component
	 * @return a DummyEnvironment hosts the removed component and descendants.
	 */
	DummyEnvironment componentRemoved(ComponentEx oldParent, ComponentEx comp) {

		Map<ElementEx, Element> removedProxyMap = new HashMap<ElementEx, Element>();
		List<ComponentEx> removedCacheableComponentList = new ArrayList<ComponentEx>();
		Map<ComponentEx, List<ComponentEx>> removedSubComponentMap = new HashMap<ComponentEx, List<ComponentEx>>();

		// remove all cacheable descendants
		Iterator<ComponentEx> itc = cacheableComponentList.iterator();
		while (itc.hasNext()) {
			ComponentEx c = itc.next();
			if (isAncestor(comp, c)) {
				itc.remove();
				removedCacheableComponentList.add(c);

				List<ComponentEx> removedSubcomps = subComponentMap.remove(c);
				removedSubComponentMap.put(c, removedSubcomps);
			}
		}

		// remove all the uncacheable descendants
		if (!comp.isCacheable()) {
			List<ComponentEx> removedUncacheableComps = new ArrayList<ComponentEx>();
			// all the possible uncacheable descendants
			ComponentEx cacheableParent = getCacheableAncestor(oldParent);
			List<ComponentEx> possibleDesList = subComponentMap
					.get(cacheableParent);

			Iterator<ComponentEx> it = possibleDesList.iterator();
			while (it.hasNext()) {
				ComponentEx c = it.next();
				if (isAncestor(comp, c)) {
					it.remove();
					removedUncacheableComps.add(c);
				}
			}

			// put top uncacheable component to subComponentMap
			removedSubComponentMap.put(comp, removedUncacheableComps);
		}

		// remove all elements whoes parent has been removed
		Iterator<Entry<ElementEx, Element>> ite = proxyMap.entrySet()
				.iterator();
		while (ite.hasNext()) {
			Entry<ElementEx, Element> e = ite.next();
			if (isAncestor(comp, e.getKey())) {
				removedProxyMap.put(e.getKey(), e.getValue());
				ite.remove();
			}
		}

		DummyEnvironment result = createDummyEnvironment();
		result.proxyMap.putAll(removedProxyMap);
		result.cacheableComponentList.addAll(removedCacheableComponentList);
		result.subComponentMap.putAll(removedSubComponentMap);
		return result;
	}

	private boolean isAncestor(Element ancestor, Element c) {
		if (c == null) {
			return false;
		} else if (c == ancestor) {
			return true;
		} else {
			return isAncestor(ancestor, c.getParent());
		}
	}

	/**
	 * Called when a component z-order has been changed.
	 */
	void componentZOrderChanged(ComponentEx comp) {
		if (comp.isCacheable()) {
			// update order of cacheable Components
			updateOrder(cacheableComponentList);
		} else {
			// update order within its cacheable parent
			Component cc = getCacheableAncestor(comp);
			List<ComponentEx> subComps = subComponentMap.get(cc);
			updateOrder(subComps);
		}
	}

	/**
	 * Called when a component's cache mode has been changed.
	 * 
	 * @param comp
	 */
	void componentCacheModeChanged(ComponentEx comp) {
		/* cache mode changed should not trigger a redraw */

		if (comp.getParent() == null) {
			if (comp.isCacheable()) {
				addOrder(0, cacheableComponentList, comp);
			} else {
				cacheableComponentList.remove(comp);
			}
		} else {

			if (comp.isCacheable()) {
				/* component changed from uncacheable to cacheable */

				List<ComponentEx> descendant = new ArrayList<ComponentEx>();
				// all the possible uncacheable descendants
				Component cacheableParent = getCacheableAncestor(comp
						.getParent());
				List<ComponentEx> possibleDesList = subComponentMap
						.get(cacheableParent);

				Iterator<ComponentEx> it = possibleDesList.iterator();
				while (it.hasNext()) {
					ComponentEx c = it.next();
					if (isAncestor(comp, c)) {
						it.remove();
						descendant.add(c);
					}
				}

				/* insert to cacheable list and put into subComponentMap */
				// cacheable parent
				int cpi = cacheableComponentList.indexOf(cacheableParent);
				addOrder(cpi + 1, cacheableComponentList, comp);
				subComponentMap.put(comp, descendant);

			} else {
				/* component changed from cacheable to uncacheable */

				cacheableComponentList.remove(comp);
				List<ComponentEx> subcomps = subComponentMap.remove(comp);
				Component newParent = getCacheableAncestor(comp);
				List<ComponentEx> newSubcompList = subComponentMap
						.get(newParent);
				newSubcompList.addAll(subcomps);

				updateOrder(newSubcompList);
			}
		}
	}

	void elementPropertyChanged(ElementEx comp) {
		fireElementPropertyChanged(getProxy(comp));
	}

	/**
	 * Returns the first cacheable parent. If all its ancestor are not
	 * cacheable, the top ancestor will be returned.
	 * 
	 * @param comp
	 * @return
	 */
	protected final ComponentEx getCacheableAncestor(ComponentEx comp) {
		if (comp.isCacheable()) {
			return comp;
		} else {
			ComponentEx parent = comp.getParent();
			if (parent == null) {
				return comp;
			} else {
				return getCacheableAncestor(parent);
			}
		}
	}

	/**
	 * update the zOrderedComponents;
	 */
	protected final void addOrder(List<ComponentEx> list, ComponentEx comp) {
		list.add(comp);
		updateOrder(list);
	}

	/**
	 * update the zOrderedComponents;
	 */
	private void addOrder(int index, List<ComponentEx> list, ComponentEx comp) {
		list.add(index, comp);
		updateOrder(list);
	}

	/**
	 * update the zOrderedComponents;
	 */
	private void addOrder(List<ComponentEx> list, List<ComponentEx> comps) {
		list.addAll(comps);
		updateOrder(list);
	}

	/**
	 * update the list order;
	 */
	private void updateOrder(List<ComponentEx> list) {

		ComponentEx[] comps = list.toArray(new ComponentEx[list.size()]);
		Comparator<Component> zComparator = new Comparator<Component>() {

			public int compare(Component o1, Component o2) {
				return o1.getZOrder() - o2.getZOrder();
			}
		};
		Arrays.sort(comps, zComparator);

		list.clear();
		for (ComponentEx comp : comps) {
			list.add(comp);
		}
	}

	/* --- JPlot2DChangeListener --- */

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

	public JPlot2DChangeListener[] getPlotPropertyListeners() {
		return plotStructureListenerList.toArray(new JPlot2DChangeListener[0]);
	}

	public void addPlotPropertyListener(JPlot2DChangeListener listener) {
		plotStructureListenerList.add(listener);
	}

	public void removePlotPropertyListener(JPlot2DChangeListener listener) {
		plotStructureListenerList.remove(listener);
	}

	/**
	 * Create a dummy environment.
	 * 
	 * @return a dummy environment
	 */
	protected DummyEnvironment createDummyEnvironment() {
		return new DummyEnvironment((lock != null));
	}

	private String getBatchString() {
		if (batchDepth == 0) {
			throw new Error("Not in batch block.");
		}
		String result = "(" + batchSND[0];
		for (int i = 1; i < batchDepth; i++) {
			result += "-" + batchSND[i];
		}
		return result + ")";
	}

	private BatchToken createBatchToken() {
		return new BatchToken(batchDepth, batchSND);
	}

	private boolean verifyBatchToken(BatchToken token) {
		return token.match(batchDepth, batchSND);
	}

	/**
	 * Begin a batch updating. In this mode, all update on environment will not
	 * send redraw command to renderers, until endBatch is called.
	 * 
	 * @param msg
	 *            the short description about the batch
	 * @return a batch token
	 */
	final public BatchToken beginBatch(String msg) {
		beginCommand(msg);
		return createBatchToken();
	}

	/**
	 * End the batch. All pending update will be committed at once.
	 * 
	 * @param token
	 *            the token gotten from beginBatch
	 */
	final public void endBatch(BatchToken token) {
		begin();

		if (!verifyBatchToken(token)) {
			throw new IllegalArgumentException("Batch token not match. "
					+ token);
		}

		try {
			endCommand();
		} finally {
			end();
		}
	}

	/**
	 * A light weight version of beginBatch, without creating the batch token.
	 * 
	 * @param msg
	 */
	final void beginCommand(String msg) {
		begin();

		++batchSND[batchDepth];
		batchDepth++;
		batchSND[batchDepth] = 0;

		logger.fine("[>] " + Integer.toHexString(hashCode()) + getBatchString()
				+ " :" + msg);
	}

	/**
	 * A light weight version of beginBatch, without verifying a batch token.
	 * 
	 * @throws WarningException
	 *             if warning message is sent out.
	 */
	final void endCommand() {
		logger.fine("[<] " + Integer.toHexString(hashCode()) + getBatchString());

		try {
			if (batchDepth == 1) {
				commit();
			}
		} finally {
			batchDepth--;
			end();
		}
	}

	/**
	 * The begin of a block wrapper, to synchronize calling.
	 */
	void begin() {
		if (lock != null) {
			lock.lock();
		}
	}

	/**
	 * The end of a block wrapper, to synchronize calling.
	 */
	void end() {
		if (lock != null) {
			lock.unlock();
		}
	}

	/**
	 * Commit all pending processes in a batch. The isBatch() should remain
	 * <code>true</code> while this method is called. This method can be called
	 * many times.
	 * 
	 * @throws WarningException
	 */
	protected abstract void commit();

}
