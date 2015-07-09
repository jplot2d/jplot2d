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
package org.jplot2d.swing.outline;

import org.jplot2d.element.*;

import javax.swing.event.EventListenerList;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

/**
 * A adapter that convert plot to TreeModel.
 *
 * @author Jingjing Li
 */
public class PlotTreeModel implements TreeModel {

    /**
     * Listeners.
     */
    protected final EventListenerList listenerList = new EventListenerList();

    private final Plot root;

    public PlotTreeModel(Plot plot) {
        root = plot;
    }

    public Object getRoot() {
        return root;
    }

    public Object getChild(Object parent, int index) {
        if (parent instanceof Plot) {
            // margin + titles + legend + x axes + y axes + layers + subplots
            Plot plot = (Plot) parent;
            int titleNum = plot.getTitles().length;
            int colorbarNum = plot.getColorbars().length;
            int xaxisNum = plot.getXAxes().length;
            int yaxisNum = plot.getYAxes().length;
            int layerNum = plot.getLayers().length;
            int subplotNum = plot.getSubplots().length;
            int start = 0;
            if (index == start) {
                return plot.getMargin();
            }
            start += 1;

            if (start <= index && index < start + titleNum) {
                return plot.getTitle(index - start);
            }
            start += titleNum;

            if (start <= index && index < start + colorbarNum) {
                return plot.getColorbar(index - start);
            }
            start += colorbarNum;

            if (index == start) {
                return plot.getLegend();
            }
            start += 1;

            if (start <= index && index < start + xaxisNum) {
                return plot.getXAxis(index - start);
            }
            start += xaxisNum;

            if (start <= index && index < start + yaxisNum) {
                return plot.getYAxis(index - start);
            }
            start += yaxisNum;

            if (start <= index && index < start + layerNum) {
                return plot.getLayer(index - start);
            }
            start += layerNum;

            if (start <= index && index < start + subplotNum) {
                return plot.getSubplot(index - start);
            }
        } else if (parent instanceof Colorbar) {
            Colorbar colorbar = (Colorbar) parent;
            if (index == 0) {
                return colorbar.getInnerAxis();
            } else if (index == 1) {
                return colorbar.getOuterAxis();
            } else if (index == 2) {
                return colorbar.getImageMapping();
            }
        } else if (parent instanceof Axis) {
            // title + tick manager
            Axis axis = (Axis) parent;
            if (index == 0) {
                return axis.getTitle();
            } else if (index == 1) {
                AxisTickManager atm = axis.getTickManager();
                if (atm == null) {
                    throw new IllegalStateException(axis + " has no tick manager.");
                } else {
                    return atm;
                }
            }
        } else if (parent instanceof AxisTickManager) {
            AxisTickManager atm = (AxisTickManager) parent;
            if (index == 0) {
                return atm.getAxisTransform();
            }
        } else if (parent instanceof AxisTransform) {
            AxisTransform arm = (AxisTransform) parent;
            if (index == 0) {
                return arm.getLockGroup();
            }
        } else if (parent instanceof Layer) {
            // x range manager + y range manager + graphs
            Layer layer = (Layer) parent;
            int gpNum = layer.getGraphs().length;
            int annNum = layer.getAnnotations().length;
            int start = 0;
            if (layer.getXAxisTransform() != null) {
                if (index == start) {
                    return layer.getXAxisTransform();
                }
                start++;
            }
            if (layer.getXAxisTransform() != null) {
                if (index == start) {
                    return layer.getYAxisTransform();
                }
                start++;
            }
            if (start <= index && index < start + gpNum) {
                return layer.getGraph(index - start);
            }
            start += gpNum;
            if (start <= index && index < start + annNum) {
                return layer.getAnnotation(index - start);
            }
        } else if (parent instanceof XYGraph) {
            XYGraph gp = (XYGraph) parent;
            if (index == 0) {
                return gp.getLegendItem();
            }
        } else if (parent instanceof ImageGraph) {
            ImageGraph gp = (ImageGraph) parent;
            if (index == 0) {
                ImageMapping mapping = gp.getMapping();
                if (mapping == null) {
                    throw new IllegalStateException(gp + " has no mapping.");
                } else {
                    return mapping;
                }
            }
        } else if (parent instanceof RGBImageGraph) {
            RGBImageGraph gp = (RGBImageGraph) parent;
            if (index == 0) {
                RGBImageMapping mapping = gp.getMapping();
                if (mapping == null) {
                    throw new IllegalStateException(gp + " has no mapping.");
                } else {
                    return mapping;
                }
            }
        } else if (parent instanceof RGBImageMapping) {
            RGBImageMapping mapping = (RGBImageMapping) parent;
            if (index == 0) {
                return mapping.getRedTransform();
            } else if (index == 1) {
                return mapping.getGreenTransform();
            } else if (index == 2) {
                return mapping.getBlueTransform();
            }
        }
        throw new IllegalStateException("The child " + index + " of " + parent + " doesn't exist.");
    }

    public int getChildCount(Object parent) {
        if (parent instanceof Plot) {
            // margin + titles + colorbars + legend + x axes + y axes + layers + subplots
            Plot plot = (Plot) parent;
            int titleNum = plot.getTitles().length;
            int colorbarNum = plot.getColorbars().length;
            int xaxisNum = plot.getXAxes().length;
            int yaxisNum = plot.getYAxes().length;
            int layerNum = plot.getLayers().length;
            int subplotNum = plot.getSubplots().length;
            return 2 + titleNum + colorbarNum + xaxisNum + yaxisNum + layerNum + subplotNum;
        } else if (parent instanceof Colorbar) {
            // lowerAxis + upperAxis + imageMapping
            return 3;
        } else if (parent instanceof Axis) {
            // title + tick manager
            if (((Axis) parent).getTickManager() == null) {
                return 1;
            } else {
                return 2;
            }
        } else if (parent instanceof AxisTickManager) {
            // axis range manager
            return 1;
        } else if (parent instanceof AxisTransform) {
            // axis lock manager
            if (((AxisTransform) parent).getLockGroup() == null) {
                return 0;
            } else {
                return 1;
            }
        } else if (parent instanceof Layer) {
            // x range manager + y range manager + graphs
            Layer layer = (Layer) parent;
            int n = 0;
            if (layer.getXAxisTransform() != null) {
                n++;
            }
            if (layer.getYAxisTransform() != null) {
                n++;
            }
            n += layer.getGraphs().length;
            n += layer.getAnnotations().length;
            return n;
        } else if (parent instanceof XYGraph) {
            // legend item
            return 1;
        } else if (parent instanceof ImageGraph) {
            // ImageMapping
            if (((ImageGraph) parent).getMapping() == null) {
                return 0;
            } else {
                return 1;
            }
        } else if (parent instanceof RGBImageGraph) {
            // RGBImageMapping
            if (((RGBImageGraph) parent).getMapping() == null) {
                return 0;
            } else {
                return 1;
            }
        } else if (parent instanceof RGBImageMapping) {
            // RGB band transform
            return 3;
        }
        return 0;
    }

    public int getIndexOfChild(Object parent, Object child) {
        if (parent == null || child == null) {
            return -1;
        }
        if (parent instanceof Plot) {
            // margin + titles + legend + x axes + y axes + layers + subplots
            Plot plot = (Plot) parent;
            Title[] titles = plot.getTitles();
            Colorbar[] colorbars = plot.getColorbars();
            PlotAxis[] xaxes = plot.getXAxes();
            PlotAxis[] yaxes = plot.getYAxes();
            Layer[] layers = plot.getLayers();
            Plot[] subplots = plot.getSubplots();

            int start = 0;
            if (child == plot.getMargin()) {
                return start;
            }
            start += 1;

            for (int i = 0; i < titles.length; i++) {
                if (titles[i] == child) {
                    return start + i;
                }
            }
            start += titles.length;

            for (int i = 0; i < colorbars.length; i++) {
                if (colorbars[i] == child) {
                    return start + i;
                }
            }
            start += colorbars.length;

            if (child == plot.getLegend()) {
                return start;
            }
            start += 1;

            for (int i = 0; i < xaxes.length; i++) {
                if (xaxes[i] == child) {
                    return start + i;
                }
            }
            start += xaxes.length;

            for (int i = 0; i < yaxes.length; i++) {
                if (yaxes[i] == child) {
                    return start + i;
                }
            }
            start += yaxes.length;

            for (int i = 0; i < layers.length; i++) {
                if (layers[i] == child) {
                    return start + i;
                }
            }
            start += layers.length;

            for (int i = 0; i < subplots.length; i++) {
                if (subplots[i] == child) {
                    return start + i;
                }
            }
        } else if (parent instanceof Colorbar) {
            Colorbar colorbar = (Colorbar) parent;
            if (child == colorbar.getInnerAxis()) {
                return 0;
            } else if (child == colorbar.getOuterAxis()) {
                return 1;
            } else if (child == colorbar.getImageMapping()) {
                return 2;
            }
        } else if (parent instanceof Axis) {
            Axis axis = (Axis) parent;
            // title + tick manager
            if (child == axis.getTitle()) {
                return 0;
            } else if (child == axis.getTickManager()) {
                return 1;
            }
        } else if (parent instanceof AxisTickManager) {
            // axis range manager
            if (child == ((AxisTickManager) parent).getAxisTransform()) {
                return 0;
            }
        } else if (parent instanceof AxisTransform) {
            // axis lock manager
            if (child == ((AxisTransform) parent).getLockGroup()) {
                return 0;
            }
        } else if (parent instanceof Layer) {
            // x range manager + y range manager + graphs
            Layer layer = (Layer) parent;
            Graph[] graphs = layer.getGraphs();
            Annotation[] anns = layer.getAnnotations();
            int start = 0;
            if (layer.getXAxisTransform() != null) {
                if (child == layer.getXAxisTransform()) {
                    return start;
                }
                start++;
            }
            if (layer.getYAxisTransform() != null) {
                if (child == layer.getYAxisTransform()) {
                    return start;
                }
                start++;
            }
            for (int i = 0; i < graphs.length; i++) {
                if (graphs[i] == child) {
                    return start + i;
                }
            }
            start += anns.length;
            for (int i = 0; i < anns.length; i++) {
                if (anns[i] == child) {
                    return start + i;
                }
            }
        } else if (parent instanceof XYGraph) {
            // legend item
            if (child == ((XYGraph) parent).getLegendItem()) {
                return 0;
            }
        } else if (parent instanceof ImageGraph) {
            // legend item
            if (child == ((ImageGraph) parent).getMapping()) {
                return 0;
            }
        } else if (parent instanceof RGBImageGraph) {
            // legend item
            if (child == ((RGBImageGraph) parent).getMapping()) {
                return 0;
            }
        } else if (parent instanceof RGBImageMapping) {
            // legend item
            if (child == ((RGBImageMapping) parent).getRedTransform()) {
                return 0;
            } else if (child == ((RGBImageMapping) parent).getGreenTransform()) {
                return 1;
            } else if (child == ((RGBImageMapping) parent).getBlueTransform()) {
                return 2;
            }
        }
        return -1;
    }

    public boolean isLeaf(Object node) {
        return node instanceof PlotMargin || node instanceof Title
                || node instanceof AxisTitle || node instanceof AxisRangeLockGroup
                || node instanceof LegendItem || node instanceof ImageMapping || node instanceof ImageBandTransform
                || getChildCount(node) == 0;
    }

    public void valueForPathChanged(TreePath path, Object newValue) {
        // We won't be making changes in the GUI
    }

    public void addTreeModelListener(TreeModelListener l) {
        listenerList.add(TreeModelListener.class, l);
    }

    public void removeTreeModelListener(TreeModelListener l) {
        listenerList.remove(TreeModelListener.class, l);
    }

    /**
     * Notify all tree mode listeners that a tree structure has changed
     *
     * @param cpath the path that the structure change happen.
     */
    @SuppressWarnings("UnusedParameters")
    public void fireTreeStructureChanged(Element cpath) {
        TreeModelListener[] listeners = listenerList.getListeners(TreeModelListener.class);
        TreeModelEvent e = new TreeModelEvent(this, new TreePath(root));
        for (int i = listeners.length - 1; i >= 0; i--) {
            listeners[i].treeStructureChanged(e);
        }
    }

}
