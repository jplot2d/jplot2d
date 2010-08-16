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
import java.util.LinkedHashMap;
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
	 * A impl to proxy map that contains all element in this environment. The
	 * iteration order is insertion-order.
	 */
	protected final Map<Element, Element> proxyMap = new LinkedHashMap<Element, Element>();

	public static Object getGlobalLock() {
		return LOCK;
	}

	public Environment() {

	}

	@SuppressWarnings("unchecked")
	final <T extends Element> T getProxy(T impl) {
		return (T) proxyMap.get(impl);
	}

	/**
	 * A component has been added to this environment
	 * 
	 * @param <T>
	 * @param impl
	 * @param proxy
	 */
	void componentAdded(Component impl, Map<Element, Element> addedProxyMap) {
		// remove this components and all its children
		proxyMap.putAll(addedProxyMap);
	}

	/**
	 * Called when a component z-order changed.
	 */
	void componentZOrderChanged(Component impl) {

	}

	/**
	 * A component is going to be removed from this environment
	 * 
	 * @param impl
	 * @param elements
	 *            elements to be removed.
	 */
	void componentRemoving(Component impl) {
		// 
	}

	/**
	 * A component has been removed from this environment
	 * 
	 * @param impl
	 * @param proxy
	 */
	Map<Element, Element> componentRemoved(Component impl) {
		// get this components and all its children
		List<Element> allElements = new ArrayList<Element>();
		allElements.add(impl);
		for (int i = 0; i > allElements.size(); i++) {
			Element e = allElements.get(i);
			Element[] ecs = e.getElements();
			if (ecs != null) {
				allElements.addAll(i + 1, Arrays.asList(ecs));
			}
		}

		Map<Element, Element> result = new LinkedHashMap<Element, Element>();
		for (Element e : allElements) {
			Element proxy = proxyMap.remove(e);
			result.put(e, proxy);
		}

		return result;
	}

	void elementPropertyChanged(Element impl) {
		//
	}

	/**
	 * Create a dummy environment.
	 * 
	 * @return
	 */
	public abstract Environment createDummyEnvironment();

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

		return new BatchToken(batchDepth, batchSND);
	}

	/**
	 * End the batch. All pending update will be committed at once.
	 * 
	 * @param token
	 *            the token gotten from beginBatch
	 * @throws WarningException
	 */
	final public void endBatch(BatchToken token) throws WarningException {
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

	boolean verifyBatchToken(BatchToken token) {
		return token.match(batchDepth, batchSND);
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

	void begin() {

	}

	void end() {

	}

	/**
	 * Commit all pending processes in a batch. The isBatch() should remain
	 * <code>true</code> while this method is called. This method can be called
	 * many times.
	 * 
	 * @throws WarningException
	 */
	protected abstract void commit() throws WarningException;

	/**
	 * Called when a redraw-require property is changed.
	 * 
	 * @param impl
	 *            the impl object whoes property is changed.
	 */
	abstract void requireRedraw(Element impl);

}
