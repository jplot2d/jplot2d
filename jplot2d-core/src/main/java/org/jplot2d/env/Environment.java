/*
 * Copyright 2010-2015 Jingjing Li.
 *
 * This file is part of jplot2d.
 *
 * jplot2d is free software:
 * you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation, either version 3 of the License, or any later version.
 *
 * jplot2d is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Lesser Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with jplot2d.
 * If not, see <http://www.gnu.org/licenses/>.
 */
package org.jplot2d.env;

import org.jplot2d.element.Element;
import org.jplot2d.element.PComponent;
import org.jplot2d.element.impl.ComponentEx;
import org.jplot2d.element.impl.ElementEx;
import org.jplot2d.element.impl.InvokeStep;
import org.jplot2d.element.impl.Joinable;
import org.jplot2d.notice.NoticeType;
import org.jplot2d.notice.Notifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.locks.ReentrantLock;

/**
 * An environment manage a group of plot elements. Any structure change or property change is tracked.
 * It will log all changes to a {@link CommandLogger}
 * and send {@link ElementChangeEvent} to all registered {@link ElementChangeListener}.
 *
 * @author Jingjing Li
 */
@SuppressWarnings("unused")
public abstract class Environment {

    private static final Logger logger = LoggerFactory.getLogger("org.jplot2d.env");

    private static final int MAX_BATCH_DEPTH = 64;
    /**
     * A impl to proxy map that contains all element in this environment. It's a LinkedHashMap to keep the order of
     * elements added. In case of the same z-order, the adding order take into account.
     */
    protected final Map<ElementEx, Element> proxyMap = new LinkedHashMap<>();
    private final ReentrantLock lock;
    /**
     * batch SN in every depth. batchSND[n], n is batch depth -1. the value is the SN for the batch depth
     */
    private final int[] batchSND = new int[MAX_BATCH_DEPTH];
    private final List<ElementChangeListener> plotStructureListenerList = Collections
            .synchronizedList(new ArrayList<ElementChangeListener>());
    /**
     * To notify something when ending a command.
     */
    protected Notifier notifier;
    private int batchDepth;
    /**
     * To log a command
     */
    private CommandLogger cmdLogger;

    protected Environment(boolean threadSafe) {
        if (threadSafe) {
            lock = new ReentrantLock();
        } else {
            lock = null;
        }
    }

    /**
     * Returns <code>true</code> if the given element a is ancestor of element c, or they are the same element.
     *
     * @param a the ancestor
     * @param c the element
     * @return <code>true</code> if the given element a is ancestor of element c
     */
    protected static boolean isAncestor(Element a, Element c) {
        return c != null && (c == a || isAncestor(a, c.getParent()));
    }

    /**
     * Returns the first cacheable parent. If all its ancestor are not cacheable, the top ancestor will be returned.
     *
     * @param comp the component
     * @return the first cacheable parent
     */
    protected static ComponentEx getCacheableAncestor(ComponentEx comp) {
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
     * Fills the argument object into the given stringBuilder
     *
     * @param sb  the string builder to fill in
     * @param arg the argument object
     */
    private static void fillArgString(StringBuilder sb, Object arg) {
        if (arg instanceof Element) {
            fillElementExpString(sb, ((Element) arg).getEnvironment(), ((ElementAddition) arg).getImpl());
        } else if (arg instanceof Element[]) {
            sb.append("[");
            if (((Element[]) arg).length > 0) {
                fillArgString(sb, ((Element[]) arg)[0]);
                for (int i = 1; i < ((Element[]) arg).length; i++) {
                    sb.append(", ");
                    fillArgString(sb, ((Element[]) arg)[i]);
                }
            }
            sb.append("]");
        } else if (arg instanceof String) {
            sb.append("\"");
            sb.append(arg);
            sb.append("\"");
        } else {
            sb.append(arg);
        }
    }

    /**
     * Fills into a string builder to represent how to get the object from it's ancestor proxy.
     *
     * @param sb  the string builder to fill in
     * @param env the environment that contains the giving obj
     * @param obj the object unpacked from proxy
     */
    private static void fillElementExpString(StringBuilder sb, Environment env, Object obj) {

        if (obj instanceof ElementEx) {
            ElementEx parent;
            if (obj instanceof Joinable) {
                parent = ((Joinable) obj).getPrim();
            } else {
                parent = ((ElementEx) obj).getParent();
            }

            InvokeStep ivs = null;
            if (parent != null) {
                ivs = ((ElementEx) obj).getInvokeStepFormParent();
            }

            if (ivs != null) {
                fillElementExpString(sb, env, parent);
                sb.append(".");
                sb.append(ivs.getMethod().getName());
                sb.append("(");
                if (ivs.getIndex() >= 0) {
                    sb.append(ivs.getIndex());
                }
                sb.append(")");
            } else {
                sb.append(String.valueOf(env.getProxy((ElementEx) obj)));
            }
        } else {
            sb.append(obj.toString());
        }
    }

    /**
     * Returns the proxy wrapper of the given element.
     *
     * @param impl the implementation
     * @return the proxy wrapper
     */
    public final Element getProxy(ElementEx impl) {
        return proxyMap.get(impl);
    }

    /**
     * Called after a component has been added to this environment, and its parent has been set.
     *
     * @param element the added element
     * @param env     the environment of the added element
     */
    void elementAdded(ElementEx element, Environment env) {
        proxyMap.putAll(env.proxyMap);
        env.proxyMap.clear();
    }

    /**
     * Called after a component has been added to this environment, and its parent has been set.
     * Or called when adding a top plot to a plot environment.
     * The added component either is cacheable, or has a cacheable parent.
     *
     * @param comp the added ComponentEx
     * @param env  the environment of the added component
     */
    void componentAdded(ComponentEx comp, Environment env) {
        proxyMap.putAll(env.proxyMap);
        env.proxyMap.clear();
        fireComponentAdded((PComponent) getProxy(comp));
    }

    /**
     * A component is going to be removed from this environment
     *
     * @param comp the component to be removed.
     */
    void componentRemoving(ComponentEx comp) {
        fireComponentRemoving((PComponent) getProxy(comp));
    }

    /**
     * Called when a element has been removed from its parent
     *
     * @param comp the removed component
     * @return a DummyEnvironment hosts the removed component and descendants.
     */
    DummyEnvironment componentRemoved(ElementEx comp) {

        DummyEnvironment result = createDummyEnvironment();

        // remove all elements whose parent has been removed
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
     * Notify all registered {@link ElementChangeListener} that the properties of the given element has been changed.
     *
     * @param element the element who's properties changed
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
        synchronized (plotStructureListenerList) {
            return plotStructureListenerList.toArray(new ElementChangeListener[plotStructureListenerList.size()]);
        }
    }

    /**
     * Add an ElementChangeListener.
     *
     * @param listener the ElementChangeListener to be added
     */
    public void addElementChangeListener(ElementChangeListener listener) {
        plotStructureListenerList.add(listener);
    }

    /**
     * Remove the given ElementChangeListener.
     *
     * @param listener the ElementChangeListener to be removed
     */
    public void removeElementChangeListener(ElementChangeListener listener) {
        plotStructureListenerList.remove(listener);
    }

    /**
     * Return the command logger who receive commands executed in this environment.
     * If there is no command logger, returns <code>null</code>.
     *
     * @return the command logger
     */
    public CommandLogger getCommandLogger() {
        return cmdLogger;
    }

    /**
     * Sets a command logger to receive commands executed in this environment.
     *
     * @param cmdLogger a CommandLogger
     */
    public void setCommandLogger(CommandLogger cmdLogger) {
        this.cmdLogger = cmdLogger;
    }

    /**
     * Log a command invocation. The logging is protected by environment.
     *
     * @param method the command method
     * @param impl   the element implementation
     * @param args   plain java object or Element proxy object
     */
    void logCommand(Method method, Object impl, Object[] args) {
        if (cmdLogger == null) {
            return;
        }

        StringBuilder sb = new StringBuilder();
        fillElementExpString(sb, this, impl);
        sb.append(".");
        sb.append(method.getName());
        sb.append("(");
        if (args != null && args.length > 0) {
            fillArgString(sb, args[0]);
            for (int i = 1; i < args.length; i++) {
                sb.append(", ");
                fillArgString(sb, args[i]);
            }
        }
        sb.append(")");
        cmdLogger.log(sb.toString());
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
     * Begin a batch block.
     * In this mode, all update on plot will not be processed, until endBatch is called.
     * If this environment is thread-safe, this method also lock the internal ReentrantLock,
     * and block other thread from calling any method of elements in this environment.
     *
     * @param msg the short description about the batch
     * @return a batch token
     */
    public final BatchToken beginBatch(String msg) {
        beginCommand(msg);
        return createBatchToken();
    }

    /**
     * End the batch. All pending update will be committed at once.
     *
     * @param token the token gotten from beginBatch
     */
    public final void endBatch(BatchToken token) {
        endBatch(token, null);
    }

    /**
     * End the batch. All pending update will be committed at once.
     *
     * @param token the token gotten from beginBatch
     * @param type  the type for notice processor
     */
    public final void endBatch(BatchToken token, NoticeType type) {
        begin();

        if (!verifyBatchToken(token)) {
            throw new IllegalArgumentException("Batch token not match. Require " + getBatchString() + " but is "
                    + token);
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
     * <p/>
     * This method is called from element proxy invocation handler before a setter method of the proxy is called.
     *
     * @param msg the message for logging
     */
    protected final void beginCommand(String msg) {
        begin();

        ++batchSND[batchDepth];
        batchDepth++;
        batchSND[batchDepth] = 0;

        logger.trace("[>] {}{} {}", Integer.toHexString(hashCode()), getBatchString(), msg);
    }

    /**
     * A light weight version of endBatch, without verifying a batch token.
     * It will never throw an exception in any case.
     * <p/>
     * This method is called from element proxy invocation handler after a setter method of the proxy is called.
     */
    protected final void endCommand() {
        endCommand(null);
    }

    /**
     * A light weight version of endBatch, without verifying a batch token.
     * It will never throw an exception in any case.
     *
     * @param type the type for notice processor
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
     * Commit all pending processes in a batch.
     * The isBatch() should remain <code>true</code> while this method is called.
     * This method can be called many times.
     */
    protected abstract void commit();

}
