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
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.jplot2d.element.Component;
import org.jplot2d.element.Element;

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

	/**
	 * The global locking object for get/set environment.
	 */
	private static final Object LOCK = new Object();

	private static final int MAX_BATCH_DEPTH = 64;

	/**
	 * batch SN in every depth. batchSND[n], n is batch depth -1. the value is
	 * the SN for the batch depth
	 */
	private int[] batchSND = new int[MAX_BATCH_DEPTH];

	private int batchDepth;

	/**
	 * A impl to proxy map that contains all element in this environment.
	 */
	protected final Map<Element, Element> proxyMap = new HashMap<Element, Element>();

	/**
	 * Contains all cacheable components in z-order
	 */
	protected final List<Component> cacheableComponentList = new ArrayList<Component>();

	/**
	 * The key is cacheable components or uncacheable top component; the value
	 * is key's uncacheable descendants, include the key itself, in z-order.
	 */
	protected final Map<Component, List<Component>> subComponentMap = new HashMap<Component, List<Component>>();

	private final List<JPlot2DChangeListener> plotStructureListenerList = Collections
			.synchronizedList(new ArrayList<JPlot2DChangeListener>());

	public static Object getGlobalLock() {
		return LOCK;
	}

	protected Environment() {

	}

	/**
	 * Returns the proxy wrapper of the given element.
	 * 
	 * @param <T>
	 * @param impl
	 * @return
	 */
	@SuppressWarnings("unchecked")
	final <T extends Element> T getProxy(T impl) {
		return (T) proxyMap.get(impl);
	}

	/**
	 * A component has been added to this environment, after setting the parent.
	 * The added component either is cacheable, or has a cacheable parent.
	 * 
	 * @param <T>
	 * @param comp
	 * @param proxy
	 */
	void componentAdded(Component comp, Environment env) {

		if (!comp.isCacheable() && !getCacheableAncestor(comp).isCacheable()) {
			throw new IllegalArgumentException(
					"Uncacheable component must has a cacheable parent.");
		}

		proxyMap.putAll(env.proxyMap);
		addOrder(cacheableComponentList, env.cacheableComponentList);
		subComponentMap.putAll(env.subComponentMap);

		/* merge uncacheable component tree */
		if (!comp.isCacheable()) {
			// remove the added top uncacheable component
			subComponentMap.remove(comp);

			Component cc = getCacheableAncestor(comp);
			// add to list in subComponentMap
			List<Component> subComps = subComponentMap.get(cc);
			addOrder(subComps, env.subComponentMap.get(comp));
		}

		env.proxyMap.clear();
		env.cacheableComponentList.clear();
		env.subComponentMap.clear();

		fireComponentAdded(getProxy(comp));
	}

	/**
	 * A component is going to be removed from this environment
	 * 
	 * @param comp
	 * @param elements
	 *            elements to be removed.
	 */
	DummyEnvironment componentRemoving(Component comp) {
		fireComponentRemoving(getProxy(comp));

		Map<Element, Element> removedProxyMap = new HashMap<Element, Element>();
		List<Component> removedCacheableComponentList = new ArrayList<Component>();
		Map<Component, List<Component>> removedSubComponentMap = new HashMap<Component, List<Component>>();

		// remove all cacheable descendants
		Iterator<Component> ite = cacheableComponentList.iterator();
		while (ite.hasNext()) {
			Component c = ite.next();
			if (isAncestor(comp, c)) {
				ite.remove();
				removedCacheableComponentList.add(c);

				List<Component> removedSubcomps = subComponentMap.remove(c);
				removedSubComponentMap.put(c, removedSubcomps);

				// remove the components from proxyMap
				for (Component sc : removedSubcomps) {
					removedProxyMap.put(sc, proxyMap.remove(sc));
				}
			}
		}

		// remove all the uncacheable descendants
		if (!comp.isCacheable()) {
			List<Component> removedUncacheableComps = new ArrayList<Component>();
			// all the possible uncacheable descendants
			Component cacheableParent = getCacheableAncestor(comp);
			List<Component> possibleDesList = subComponentMap
					.get(cacheableParent);

			Iterator<Component> it = possibleDesList.iterator();
			while (it.hasNext()) {
				Component c = it.next();
				if (isAncestor(comp, c)) {
					it.remove();
					removedUncacheableComps.add(c);
				}
			}

			// put top uncacheable component to subComponentMap
			removedSubComponentMap.put(comp, removedUncacheableComps);

			// remove the components from proxyMap
			for (Component sc : removedUncacheableComps) {
				removedProxyMap.put(sc, proxyMap.remove(sc));
			}
		}

		DummyEnvironment result = new DummyEnvironment();
		result.proxyMap.putAll(removedProxyMap);
		result.cacheableComponentList.addAll(removedCacheableComponentList);
		result.subComponentMap.putAll(removedSubComponentMap);
		return result;
	}

	/**
	 * Called when a component has been removed from this environment
	 * 
	 * @param comp
	 *            the removed component
	 * @return A map. The key is removed component. The value is the removed
	 *         component proxy
	 */
	void componentRemoved(Component comp) {

	}

	private boolean isAncestor(Component ancestor, Component c) {
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
	void componentZOrderChanged(Component comp) {
		if (comp.isCacheable()) {
			// update order of cacheable Components
			updateOrder(cacheableComponentList);
		} else {
			// update order within its cacheable parent
			Component cc = getCacheableAncestor(comp);
			List<Component> subComps = subComponentMap.get(cc);
			updateOrder(subComps);
		}
	}

	/**
	 * Called when a component's cache mode has been changed.
	 * 
	 * @param comp
	 */
	void componentCacheModeChanged(Component comp) {
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

				List<Component> descendant = new ArrayList<Component>();
				// all the possible uncacheable descendants
				Component cacheableParent = getCacheableAncestor(comp
						.getParent());
				List<Component> possibleDesList = subComponentMap
						.get(cacheableParent);

				Iterator<Component> it = possibleDesList.iterator();
				while (it.hasNext()) {
					Component c = it.next();
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
				List<Component> subcomps = subComponentMap.remove(comp);
				Component newParent = getCacheableAncestor(comp);
				List<Component> newSubcompList = subComponentMap.get(newParent);
				newSubcompList.addAll(subcomps);

				updateOrder(newSubcompList);
			}
		}
	}

	void elementPropertyChanged(Element comp) {
		fireElementPropertyChanged(getProxy(comp));
	}

	/**
	 * Returns the first cacheable parent. If all its ancestor are not
	 * cacheable, the top ancestor will be returned.
	 * 
	 * @param comp
	 * @return
	 */
	protected final Component getCacheableAncestor(Component comp) {
		if (comp.isCacheable()) {
			return comp;
		} else {
			Component parent = comp.getParent();
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
	protected final void addOrder(List<Component> list, Component comp) {
		list.add(comp);
		updateOrder(list);
	}

	/**
	 * update the zOrderedComponents;
	 */
	private void addOrder(int index, List<Component> list, Component comp) {
		list.add(index, comp);
		updateOrder(list);
	}

	/**
	 * update the zOrderedComponents;
	 */
	private void addOrder(List<Component> list, List<Component> comps) {
		list.addAll(comps);
		updateOrder(list);
	}

	/**
	 * update the list order;
	 */
	private void updateOrder(List<Component> list) {

		Component[] comps = list.toArray(new Component[list.size()]);
		Comparator<Component> zComparator = new Comparator<Component>() {

			public int compare(Component o1, Component o2) {
				return o1.getZOrder() - o2.getZOrder();
			}
		};
		Arrays.sort(comps, zComparator);

		list.clear();
		for (Component comp : comps) {
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
	protected Environment createDummyEnvironment() {
		return new DummyEnvironment();
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

		Log.env.fine("[>] " + Integer.toHexString(hashCode())
				+ getBatchString() + " :" + msg);
	}

	/**
	 * A light weight version of beginBatch, without verifying a batch token.
	 * 
	 * @throws WarningException
	 *             if warning message is sent out.
	 */
	final void endCommand() {
		Log.env.fine("[<] " + Integer.toHexString(hashCode())
				+ getBatchString());

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

	}

	/**
	 * The end of a block wrapper, to synchronize calling.
	 */
	void end() {

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
