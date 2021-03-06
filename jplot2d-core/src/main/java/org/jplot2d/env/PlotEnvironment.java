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
import org.jplot2d.element.Plot;
import org.jplot2d.element.impl.ComponentEx;
import org.jplot2d.element.impl.ElementEx;
import org.jplot2d.element.impl.IntermediateCacheEx;
import org.jplot2d.element.impl.PlotEx;
import org.jplot2d.notice.LoggingNotifier;
import org.jplot2d.notice.Notifier;
import org.jplot2d.transform.PaperTransform;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.lang.reflect.Proxy;
import java.util.*;

/**
 * This Environment can host a plot instance and provide undo/redo ability.
 * Once a plot is put in an environment, changes on the plot can trigger re-layout or redraw, according to the changed property.
 *
 * @author Jingjing Li
 */
@SuppressWarnings("unused")
public class PlotEnvironment extends Environment {

    /**
     * the key is impl element, the value is copy of element (for renderer thread safe)
     */
    protected final Map<ElementEx, ElementEx> copyMap = new HashMap<>();
    protected final UndoManager<UndoMemento> undoManager = new UndoManager<>(Integer.MAX_VALUE);
    /**
     * Contains all visible cacheable components in z-order. include uncacheable root plot.
     */
    protected final List<CacheableBlock> cacheBlockList = new ArrayList<>();
    /**
     * A copy to proxy map that contains all element in this environment.
     */
    protected Map<ElementEx, Element> copyProxyMap;
    /**
     * The plot proxy
     */
    protected volatile Plot plot;
    protected PlotEx plotImpl;
    protected PlotEx plotCopy;
    /**
     * Keep hard references to cache holder objects.
     */
    @SuppressWarnings({"FieldCanBeLocal"})
    private List<Object> cacheHolders;

    public PlotEnvironment(boolean threadSafe) {
        super(threadSafe);
    }

    /**
     * update the list order;
     */
    protected static void updateOrder(List<ComponentEx> list) {
        ComponentEx[] comps = list.toArray(new ComponentEx[list.size()]);
        Comparator<PComponent> zComparator = new Comparator<PComponent>() {

            public int compare(PComponent o1, PComponent o2) {
                return o1.getZOrder() - o2.getZOrder();
            }
        };

        Arrays.sort(comps, zComparator);

        list.clear();
        Collections.addAll(list, comps);
    }

    /**
     * Determines whether the component is showing on plot. This means that the component must be visible, and it must
     * be in a container that is visible and showing.
     *
     * @param comp the component
     * @return whether the component is showing
     */
    private static boolean isShowing(ComponentEx comp) {
        return comp.isVisible() && (comp.getParent() == null || isShowing(comp.getParent()));
    }

    public Plot getPlot() {
        return plot;
    }

    /**
     * Sets a plot to this environment. The plot must hosted by a dummy environment. All notices are logged to java
     * logging facilities.
     *
     * @param plot the plot
     */
    public void setPlot(Plot plot) {
        setPlot(plot, LoggingNotifier.getInstance());
    }

    /**
     * Sets a plot to this environment. The plot must hosted by a dummy environment.
     *
     * @param plot     the plot
     * @param notifier the notifier to receive and process notices
     */
    public void setPlot(Plot plot, Notifier notifier) {

        Environment oldEnv = plot.getEnvironment();
        if (!(oldEnv instanceof DummyEnvironment)) {
            throw new IllegalArgumentException("The plot to be added has been added a PlotEnvironment");
        }

        // check this environment is ready to host plot
        beginCommand("setPlot");

        if (this.plot != null) {
            endCommand();
            throw new IllegalArgumentException("This Environment has hosted a plot");
        }

        oldEnv.beginCommand("setPlot");

        // update environment for all adding components
        for (Element proxy : oldEnv.proxyMap.values()) {
            ((ElementAddition) proxy).setEnvironment(this);
        }

        this.plot = plot;
        this.plotImpl = (PlotEx) ((ElementAddition) plot).getImpl();

        componentAdded(plotImpl, oldEnv);

        this.notifier = notifier;
        plotImpl.setNotifier(notifier);
        plotImpl.setRerenderNeeded(true);

        oldEnv.endCommand();
        endCommand();
    }

    /**
     * Returns the notifier of this environment. The notifier will receive notices during command execution and process
     * all notices at once when command finished.
     *
     * @return the notifier of this environment
     */
    public Notifier getNotifier() {
        Notifier result;
        begin();
        result = notifier;
        end();
        return result;
    }

    /**
     * Sets the notifier of this environment. The notifier will receive notices during command execution and process all
     * notices at once when command finished.
     *
     * @param notifier the notifier to be used in this environment
     */
    public void setNotifier(Notifier notifier) {
        begin();
        this.notifier = notifier;
        plotImpl.setNotifier(notifier);
        end();
    }

    @Override
    protected void commit() {
        plotImpl.commit();
        fireChangeProcessed();

        // create cache holder
        List<Object> holders = new ArrayList<>();
        for (ElementEx element : proxyMap.keySet()) {
            if (element instanceof IntermediateCacheEx) {
                Object cacheHolder = ((IntermediateCacheEx) element).createCacheHolder();
                if (cacheHolder != null) {
                    holders.add(cacheHolder);
                }
            }
        }
        this.cacheHolders = holders;

        // fill copyMap and copyProxyMap
        makeUndoMemento();
        // fill cacheBlockList
        buildComponentCacheBlock();

        render();
    }

    /**
     * When a command is committed, this method is called to generate a rendering result.
     */
    protected void render() {

    }

    protected void fireChangeProcessed() {
        ElementChangeListener[] ls = getElementChangeListeners();
        if (ls.length > 0) {
            ElementChangeEvent evt = new ElementChangeEvent(this, null);
            for (ElementChangeListener lsnr : ls) {
                lsnr.propertyChangesProcessed(evt);
            }
        }
    }

    /**
     * Sort components in cacheable blocks.
     */
    protected void buildComponentCacheBlock() {
        /*
         * Contains all visible cacheable components in z-order. include uncacheable root plot.
		 */
        List<ComponentEx> cacheableComponentList = new ArrayList<>();

		/*
         * The key is copy of cacheable components or uncacheable root plot; the value is copy of key's visible
		 * uncacheable descendants, include the key itself, in z-order.
		 */
        Map<ComponentEx, List<ComponentEx>> subcompsMap = new HashMap<>();

        cacheBlockList.clear();

        for (ElementEx element : proxyMap.keySet()) {
            if (element instanceof ComponentEx) {
                ComponentEx comp = (ComponentEx) element;
                ComponentEx copy = (ComponentEx) copyMap.get(comp);
                if (isShowing(copy)) {
                    ComponentEx cacheableAncestor;

                    if (comp.isCacheable() || comp == plotImpl) {
                        cacheableComponentList.add((ComponentEx) element);
                        cacheableAncestor = copy;
                    } else {
                        cacheableAncestor = getCacheableAncestor(copy);
                    }

                    List<ComponentEx> subComps = subcompsMap.get(cacheableAncestor);
                    if (subComps == null) {
                        subComps = new ArrayList<>();
                        subcompsMap.put(cacheableAncestor, subComps);
                    }
                    subComps.add(copy);
                }
            }
        }

        // sort by z-order
        updateOrder(cacheableComponentList);
        for (ComponentEx comp : cacheableComponentList) {
            ComponentEx copy = (ComponentEx) copyMap.get(comp);
            List<ComponentEx> subcomps = subcompsMap.get(copy);
            updateOrder(subcomps);
            CacheableBlock cb = new CacheableBlock(comp, copy, subcomps);
            cacheBlockList.add(cb);
        }
    }

    /**
     * Create a undo memento and add it to change history.
     */
    private void makeUndoMemento() {

        copyMap.clear();

        plotCopy = plotImpl.copyStructure(copyMap);
        for (Map.Entry<ElementEx, ElementEx> me : copyMap.entrySet()) {
            me.getValue().copyFrom(me.getKey());
        }

        if (undoManager.getCapacity() > 0) {
            // build copy to proxy map
            copyProxyMap = new LinkedHashMap<>();
            for (Map.Entry<ElementEx, Element> me : proxyMap.entrySet()) {
                ElementEx element = me.getKey();
                Element proxy = me.getValue();
                ElementEx copye = copyMap.get(element);
                copyProxyMap.put(copye, proxy);
            }

            undoManager.add(new UndoMemento(plotCopy, copyProxyMap));
        }
    }

	/* ====================== Undo/Redo ====================== */

    /**
     * Returns the maximum number of reversible operations.
     *
     * @return the maximum number of reversible operations
     */
    public int getUndoLevels() {
        begin();
        int capacity = undoManager.getCapacity();
        end();

        return capacity;
    }

    /**
     * Sets the maximum number of reversible operations.
     *
     * @param levels the maximum number of reversible operations
     */
    public void setUndoLevels(int levels) {
        begin();
        undoManager.setCapacity(levels);
        end();
    }

    /**
     * Returns <code>true</code> if undo is possible.
     *
     * @return <code>true</code> if undo is possible
     */
    public boolean canUndo() {
        begin();
        boolean b = undoManager.canUndo();
        end();

        return b;
    }

    /**
     * Returns <code>true</code> if redo is possible.
     *
     * @return <code>true</code> if redo is possibl
     */
    public boolean canRedo() {
        begin();
        boolean b = undoManager.canRedo();
        end();

        return b;
    }

    /**
     * Undo the last change. If there is nothing to undo, an exception is thrown.
     */
    public void undo() {
        begin();

        UndoMemento memento = undoManager.undo();
        if (memento == null) {
            end();
            throw new RuntimeException("Cannot undo");
        }

        restore(memento);
        buildComponentCacheBlock();

        plotImpl.setRerenderNeeded(true);
        render();

        end();
    }

    /**
     * Redo the last change. If there is nothing to redo, an exception is thrown.
     */
    public void redo() {
        begin();

        UndoMemento memento = undoManager.redo();
        if (memento == null) {
            end();
            throw new RuntimeException("Cannot redo");
        }

        restore(memento);
        buildComponentCacheBlock();

        plotImpl.setRerenderNeeded(true);
        render();

        end();
    }

    /**
     * copy the memento back to working environment. This method create a copy of memento, and assign the copy as
     * working plotImpl.
     *
     * @param memento the memento to be copied from
     */
    private void restore(UndoMemento memento) {

        copyMap.clear();

        // the key is element in memento, the value is copy of element for restoring
        Map<ElementEx, ElementEx> rcopyMap = new HashMap<>();

        // copy the memento as implements
        plotCopy = memento.getPlot();
        plotImpl = plotCopy.copyStructure(rcopyMap);
        plotImpl.setNotifier(notifier);

        proxyMap.clear();
        copyProxyMap = memento.getProxyMap();
        for (Map.Entry<ElementEx, Element> me : copyProxyMap.entrySet()) {
            ElementEx mmte = me.getKey(); // the memento element
            Element proxy = me.getValue(); // the proxy
            ElementEx impl = rcopyMap.get(mmte); // the copy of memento element, as the new impl
            // copy properties from memento element to new impl
            impl.copyFrom(mmte);
            // replace impl in invocation handler
            ElementIH ih = (ElementIH) Proxy.getInvocationHandler(proxy);
            ih.replaceImpl(impl);

            proxyMap.put(impl, proxy);
            copyMap.put(impl, mmte);
        }
    }

    /**
     * Return the top selectable component at the given location.
     *
     * @param dp the device point relative to top-left corner.
     * @return the top selectable component
     */
    public PComponent getSelectableComponentAt(Point2D dp) {
        PComponent result = null;

        begin();

        for (int i = cacheBlockList.size() - 1; i >= 0; i--) {
            List<ComponentEx> uccList = cacheBlockList.get(i).getSubcomps();
            for (int j = uccList.size() - 1; j >= 0; j--) {
                ComponentEx ucc = uccList.get(j);
                if (ucc.isSelectable()) {
                    PaperTransform pxf = ucc.getPaperTransform();
                    Rectangle2D sbnd = ucc.getSelectableBounds();
                    if (pxf != null && sbnd != null && sbnd.contains(pxf.getDtoP(dp))) {
                        result = (PComponent) copyProxyMap.get(ucc);
                        break;
                    }
                }
            }
        }

        end();

        return result;
    }

    /**
     * Return the most inner plot that contains the given location.
     *
     * @param dp the device point relative to top-left corner.
     * @return the most inner plot
     */
    public Plot getPlotAt(Point2D dp) {
        Plot result = null;

        begin();

        for (int i = cacheBlockList.size() - 1; i >= 0; i--) {
            List<ComponentEx> uccList = cacheBlockList.get(i).getSubcomps();
            for (int j = uccList.size() - 1; j >= 0; j--) {
                ComponentEx ucc = uccList.get(j);
                if (ucc instanceof PlotEx) {
                    Point2D p = ucc.getPaperTransform().getDtoP(dp);
                    if (ucc.getBounds().contains(p)) {
                        result = (Plot) copyProxyMap.get(ucc);
                        break;
                    }
                }
            }
        }

        end();

        return result;
    }

}
