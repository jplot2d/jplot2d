/**
 * Copyright 2010-2013 Jingjing Li.
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
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.locks.ReentrantLock;

import org.jplot2d.element.Element;
import org.jplot2d.element.PComponent;
import org.jplot2d.element.impl.ComponentEx;
import org.jplot2d.element.impl.ElementEx;
import org.jplot2d.notice.NoticeType;
import org.jplot2d.notice.Notifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An environment that a plot can realization. Once a plot is put an environment, changes on the plot can cause
 * re-layout or redraw, according to the changed property.
 * <p>
 * Renderers can be added to an environment to get the rendered result synchronously or asynchronously.
 * 
 * @author Jingjing Li
 * 
 */
public abstract class Environment {

	private static final Logger logger = LoggerFactory.getLogger("org.jplot2d.env");

	/**
	 * The global locking object for get/set environment.
	 */
	private static final Object LOCK = new Object();

	private static final int MAX_BATCH_DEPTH = 64;

	private final ReentrantLock lock;

	/**
	 * batch SN in every depth. batchSND[n], n is batch depth -1. the value is the SN for the batch depth
	 */
	private int[] batchSND = new int[MAX_BATCH_DEPTH];

	private int batchDepth;

	/**
	 * To log a command
	 */
	protected CommandLogger cmdLogger;

	/**
	 * To notify something when ending a command.
	 */
	protected Notifier notifier;

	/**
	 * A impl to proxy map that contains all element in this environment. It's a LinkedHashMap to keep the order of
	 * elements added. In case of the same z-order, the adding order take into account.
	 */
	protected final Map<ElementEx, Element> proxyMap = new LinkedHashMap<ElementEx, Element>();

	private final List<ElementChangeListener> plotStructureListenerList = Collections
			.synchronizedList(new ArrayList<ElementChangeListener>());

	protected static Object getGlobalLock() {
		return LOCK;
	}

	/**
	 * Returns <code>true</code> if the given element a is ancestor of element c, or they are the same element.
	 * 
	 * @param a
	 * @param c
	 * @return
	 */
	protected static boolean isAncestor(Element a, Element c) {
		if (c == null) {
			return false;
		} else if (c == a) {
			return true;
		} else {
			return isAncestor(a, c.getParent());
		}
	}

	/**
	 * Returns the first cacheable parent. If all its ancestor are not cacheable, the top ancestor will be returned.
	 * 
	 * @param comp
	 * @return
	 */
	protected static final ComponentEx getCacheableAncestor(ComponentEx comp) {
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
	public final Element getProxy(ElementEx impl) {
		return proxyMap.get(impl);
	}

	/**
	 * Called after a component has been added to this environment, and its parent has been set.
	 * 
	 * @param element
	 *            the added element
	 * @param env
	 *            the environment of the added element
	 */
	void elementAdded(ElementEx element, Environment env) {
		proxyMap.putAll(env.proxyMap);
		env.proxyMap.clear();
	}

	/**
	 * Called after a component has been added to this environment, and its parent has been set. Or called when adding a
	 * top plot to a plot environment. The added component either is cacheable, or has a cacheable parent.
	 * 
	 * @param comp
	 *            the added ComponentEx
	 * @param env
	 *            the environment of the added component
	 */
	void componentAdded(ComponentEx comp, Environment env) {
		proxyMap.putAll(env.proxyMap);
		env.proxyMap.clear();
		fireComponentAdded((PComponent) getProxy(comp));
	}

	/**
	 * A component is going to be removed from this environment
	 * 
	 * @param comp
	 * @param elements
	 *            elements to be removed.
	 */
	void componentRemoving(ComponentEx comp) {
		fireComponentRemoving((PComponent) getProxy(comp));
	}

	/**
	 * Called when a element has been removed from its parent
	 * 
	 * @param comp
	 *            the removed component
	 * @return a DummyEnvironment hosts the removed component and descendants.
	 */
	DummyEnvironment componentRemoved(ElementEx comp) {

		DummyEnvironment result = createDummyEnvironment();

		// remove all elements whoes parent has been removed
		Iterator<Entry<ElementEx, Element>> ite = proxyMap.entrySet().iterator();
		while (ite.hasNext()) {
			Entry<ElementEx, Element> e = ite.next();
			if (isAncestor(comp, e.getKey())) {
				result.proxyMap.put(e.getKey(), e.getValue());
				ite.remove();
			}
		}

		fireComponentRemoved((PComponent) getProxy(comp));

		return result;
	}

	void elementPropertyChanged(ElementEx comp) {
		fireElementPropertyChanged(getProxy(comp));
	}

	/* --- JPlot2DChangeListener --- */

	private void fireComponentAdded(PComponent element) {
		ElementChangeListener[] ls = getElementChangeListeners();
		if (ls.length > 0) {
			ElementChangeEvent evt = new ElementChangeEvent(this, element);
			for (ElementChangeListener lsnr : ls) {
				lsnr.componentAdded(evt);
			}
		}
	}

	private void fireComponentRemoving(PComponent element) {
		ElementChangeListener[] ls = getElementChangeListeners();
		if (ls.length > 0) {
			ElementChangeEvent evt = new ElementChangeEvent(this, element);
			for (ElementChangeListener lsnr : ls) {
				lsnr.componentRemoving(evt);
			}
		}
	}

	private void fireComponentRemoved(PComponent element) {
		ElementChangeListener[] ls = getElementChangeListeners();
		if (ls.length > 0) {
			ElementChangeEvent evt = new ElementChangeEvent(this, element);
			for (ElementChangeListener lsnr : ls) {
				lsnr.componentRemoved(evt);
			}
		}
	}

	/**
	 * Notify all registered JPlot2DChangeListener that the properties of the given element has been changed.
	 * 
	 * @param element
	 */
	private void fireElementPropertyChanged(Element element) {
		ElementChangeListener[] ls = getElementChangeListeners();
		if (ls.length > 0) {
			ElementChangeEvent evt = new ElementChangeEvent(this, element);
			for (ElementChangeListener lsnr : ls) {
				lsnr.propertiesChanged(evt);
			}
		}
	}

	/**
	 * Returns all ElementChangeListeners.
	 * 
	 * @return ElementChangeListener in an array
	 */
	public ElementChangeListener[] getElementChangeListeners() {
		return plotStructureListenerList.toArray(new ElementChangeListener[0]);
	}

	/**
	 * Add an ElementChangeListener
	 * 
	 * @param listener
	 */
	public void addElementChangeListener(ElementChangeListener listener) {
		plotStructureListenerList.add(listener);
	}

	/**
	 * Remove an ElementChangeListener
	 * 
	 * @param listener
	 */
	public void removeElementChangeListener(ElementChangeListener listener) {
		plotStructureListenerList.remove(listener);
	}

	/**
	 * Return the command logger who receive commands executed in this environment. If there is no command logger,
	 * returns <code>null</code>.
	 * 
	 * @return the command logger
	 */
	public CommandLogger getCommandLogger() {
		return cmdLogger;
	}

	/**
	 * Sets a command logger to receive commands executed in this environment.
	 * 
	 * @param cmdLogger
	 *            a CommandLogger
	 */
	public void setCommandLogger(CommandLogger cmdLogger) {
		this.cmdLogger = cmdLogger;
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
	 * Begin a batch block. In this mode, all update on plot will not be processed, until endBatch is called. If this
	 * environment is thread-safe, this method also lock the ReentrantLock, will block other thread call this method.
	 * 
	 * @param msg
	 *            the short description about the batch
	 * @return a batch token
	 */
	public final BatchToken beginBatch(String msg) {
		beginCommand(msg);
		return createBatchToken();
	}

	/**
	 * End the batch. All pending update will be committed at once.
	 * 
	 * @param token
	 *            the token gotten from beginBatch
	 */
	public final void endBatch(BatchToken token) {
		endBatch(token, null);
	}

	/**
	 * End the batch. All pending update will be committed at once.
	 * 
	 * @param token
	 *            the token gotten from beginBatch
	 * @param type
	 *            the type for notice processor
	 */
	public final void endBatch(BatchToken token, NoticeType type) {
		begin();

		if (!verifyBatchToken(token)) {
			throw new IllegalArgumentException("Batch token not match. " + token);
		}

		endCommand(type);
		end();
	}

	/**
	 * The calling to method must be surrounded by {@link #begin()} and {@link #end()}.
	 * 
	 * @return if this environment is in batch mode.
	 */
	protected final boolean isBatch() {
		return batchDepth > 0;
	}

	/**
	 * A light weight version of beginBatch, without creating a batch token.
	 * <p>
	 * This method is called from element proxy invocation handler before a setter method of the proxy is called.
	 * 
	 * @param msg
	 *            the message for logging
	 */
	protected final void beginCommand(String msg) {
		begin();

		++batchSND[batchDepth];
		batchDepth++;
		batchSND[batchDepth] = 0;

		logger.trace("[>] {}{} {}", Integer.toHexString(hashCode()), getBatchString(), msg);
	}

	/**
	 * A light weight version of endBatch, without verifying a batch token. It will never throw an exception in any
	 * case.
	 * <p>
	 * This method is called from element proxy invocation handler after a setter method of the proxy is called.
	 */
	protected final void endCommand() {
		endCommand(null);
	}

	/**
	 * A light weight version of endBatch, without verifying a batch token. It will never throw an exception in any
	 * case.
	 * 
	 * @param type
	 *            the type for notice processor
	 */
	private void endCommand(NoticeType type) {
		logger.trace("[<] {}{}", Integer.toHexString(hashCode()), getBatchString());

		if (batchDepth == 1) {
			try {
				commit();
			} catch (Exception e) {
				logger.warn("", e);
			}
			if (notifier != null) {
				try {
					notifier.processNotices(type);
				} catch (Exception e) {
					logger.warn("", e);
				}
			}
		}

		batchDepth--;
		end();
	}

	/**
	 * The begin of a block wrapper, to synchronize calling.
	 */
	protected final void begin() {
		if (lock != null) {
			lock.lock();
		}
	}

	/**
	 * The end of a block wrapper, to synchronize calling.
	 */
	protected final void end() {
		if (lock != null) {
			lock.unlock();
		}
	}

	/**
	 * Commit all pending processes in a batch. The isBatch() should remain <code>true</code> while this method is
	 * called. This method can be called many times.
	 */
	protected abstract void commit();

}
