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
package org.jplot2d.element.impl;

import org.jplot2d.element.Annotation;
import org.jplot2d.element.AxisTransform;
import org.jplot2d.element.Graph;
import org.jplot2d.element.Plot;
import org.jplot2d.transform.PaperTransform;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Jingjing Li
 */
public class LayerImpl extends ContainerImpl implements LayerEx {

    private final List<GraphEx> graphs = new ArrayList<>();
    private final List<AnnotationEx> annotations = new ArrayList<>();

    @Nullable
    private AxisTransformEx xarm, yarm;

    public String getId() {
        if (getParent() != null) {
            return "Layer" + getParent().indexOf(this);
        } else {
            return "Layer@" + Integer.toHexString(System.identityHashCode(this));
        }
    }

    public String getShortId() {
        if (getParent() != null) {
            String pid = getParent().getShortId();
            if (pid == null) {
                return getId();
            } else {
                return getId() + "." + pid;
            }
        } else {
            return getId();
        }
    }

    public InvokeStep getInvokeStepFormParent() {
        if (getParent() == null) {
            return null;
        }

        Method method;
        try {
            method = Plot.class.getMethod("getLayer", Integer.TYPE);
        } catch (NoSuchMethodException e) {
            throw new Error(e);
        }
        return new InvokeStep(method, getParent().indexOf(this));
    }

    public PlotEx getParent() {
        return (PlotEx) super.getParent();
    }

    public Point2D getLocation() {
        if (getParent() == null) {
            return null;
        } else {
            return new Point2D.Double(0, 0);
        }
    }

    public Dimension2D getSize() {
        if (getParent() == null) {
            return null;
        } else {
            return getParent().getContentSize();
        }
    }

    public PaperTransform getPaperTransform() {
        if (getParent() != null) {
            return getParent().getPaperTransform();
        }
        return null;
    }

    public void transformChanged() {
        for (GraphEx graph : graphs) {
            redraw(graph);
        }
        for (AnnotationEx annotation : annotations) {
            redraw(annotation);
        }
    }

    public ComponentEx[] getComponents() {
        int size = graphs.size() + annotations.size();
        ComponentEx[] comps = new ComponentEx[size];

        int n = 0;
        for (GraphEx graph : graphs) {
            comps[n++] = graph;
        }
        for (AnnotationEx annotation : annotations) {
            comps[n++] = annotation;
        }

        return comps;
    }

    public GraphEx getGraph(int index) {
        return graphs.get(index);
    }

    @Nonnull
    public GraphEx[] getGraphs() {
        return graphs.toArray(new GraphEx[graphs.size()]);
    }

    public void addGraph(Graph graph) {
        GraphEx gx = (GraphEx) graph;
        graphs.add(gx);
        gx.setParent(this);

        // add legend item
        if (getParent() != null && gx instanceof XYGraphEx) {
            getParent().getLegend().addLegendItem(((XYGraphEx) gx).getLegendItem());
        }

        redraw(gx);

        if (gx.isVisible()) {
            if (xarm != null && xarm.getLockGroup() != null && xarm.getLockGroup().isAutoRange()) {
                xarm.getLockGroup().reAutoRange();
            }
            if (yarm != null && yarm.getLockGroup() != null && yarm.getLockGroup().isAutoRange()) {
                yarm.getLockGroup().reAutoRange();
            }
        }
    }

    public void removeGraph(Graph graph) {
        GraphEx gx = (GraphEx) graph;

        redraw(gx);

        graphs.remove(gx);
        gx.setParent(null);

        // remove legend item
        if (getParent() != null && gx instanceof XYGraphEx) {
            getParent().getLegend().removeLegendItem(((XYGraphEx) gx).getLegendItem());
        }

        if (gx.isVisible()) {
            if (xarm != null && xarm.getLockGroup() != null && xarm.getLockGroup().isAutoRange()) {
                xarm.getLockGroup().reAutoRange();
            }
            if (yarm != null && yarm.getLockGroup() != null && yarm.getLockGroup().isAutoRange()) {
                yarm.getLockGroup().reAutoRange();
            }
        }
    }

    public int indexOf(GraphEx graph) {
        return graphs.indexOf(graph);
    }

    @Nonnull
    public AnnotationEx[] getAnnotations() {
        return annotations.toArray(new AnnotationEx[annotations.size()]);
    }

    public Annotation getAnnotation(int idx) {
        return annotations.get(idx);
    }

    public void addAnnotation(Annotation annotation) {
        AnnotationEx annx = (AnnotationEx) annotation;
        annotations.add(annx);
        annx.setParent(this);

        redraw(annx);
    }

    public void removeAnnotation(Annotation annotation) {
        AnnotationEx annx = (AnnotationEx) annotation;

        redraw(annx);

        annotations.remove(annx);
        annx.setParent(null);
    }

    public int indexOf(AnnotationEx annotation) {
        return annotations.indexOf(annotation);
    }

    public boolean canContribute() {
        return false;
    }

    @Nullable
    public AxisTransformEx getXAxisTransform() {
        return xarm;
    }

    public void setXAxisTransform(@Nullable AxisTransform axis) {
        if (this.xarm != null) {
            this.xarm.removeLayer(this);
        }
        this.xarm = (AxisTransformEx) axis;
        if (this.xarm != null) {
            this.xarm.addLayer(this);
        }
        if (axis != null) {
            transformChanged();
        }
    }

    @Nullable
    public AxisTransformEx getYAxisTransform() {
        return yarm;
    }

    public void setYAxisTransform(@Nullable AxisTransform axis) {
        if (this.yarm != null) {
            this.yarm.removeLayer(this);
        }
        this.yarm = (AxisTransformEx) axis;
        if (this.yarm != null) {
            this.yarm.addLayer(this);
        }
        if (axis != null) {
            transformChanged();
        }
    }

    public void linkXAxisTransform(AxisTransformEx axt) {
        this.xarm = axt;
    }

    public void linkYAxisTransform(AxisTransformEx axt) {
        this.yarm = axt;
    }

    public void setAxesTransform(@Nullable AxisTransform xaxis, @Nullable AxisTransform yaxis) {
        setXAxisTransform(xaxis);
        setYAxisTransform(yaxis);
    }

    @Override
    public LayerEx copyStructure(@Nonnull Map<ElementEx, ElementEx> orig2copyMap) {
        LayerImpl result = (LayerImpl) super.copyStructure(orig2copyMap);

        // copy graphs
        for (GraphEx graph : this.graphs) {
            GraphEx graphCopy = (GraphEx) graph.copyStructure(orig2copyMap);
            graphCopy.setParent(result);
            result.graphs.add(graphCopy);
        }

        // copy annotations
        for (AnnotationEx annotation : this.annotations) {
            AnnotationEx copy = (AnnotationEx) annotation.copyStructure(orig2copyMap);
            copy.setParent(result);
            result.annotations.add(copy);
        }

        return result;
    }

}
