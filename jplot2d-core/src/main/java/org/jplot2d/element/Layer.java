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
package org.jplot2d.element;

import org.jplot2d.annotation.Hierarchy;
import org.jplot2d.annotation.HierarchyOp;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * A layer can contains graphs and annotations. Every layer has a viewport to show graphs and annotations.
 * It associate with an X AxisTransform and a Y AxisTransform to control the viewport.
 *
 * @author Jingjing Li
 */
@SuppressWarnings("unused")
public interface Layer extends PComponent {

    @Hierarchy(HierarchyOp.GET)
    @Nullable
    Plot getParent();

    /**
     * Returns the graph on the given index.
     *
     * @return the graph on the given index
     */
    @Hierarchy(HierarchyOp.GET)
    Graph getGraph(int index);

    /**
     * Returns all graph of this layer.
     *
     * @return all graph of this layer.
     */
    @Hierarchy(HierarchyOp.GETARRAY)
    @Nonnull
    Graph[] getGraphs();

    /**
     * Adds a graph to this layer.
     *
     * @param graph the graoh to be add.
     */
    @Hierarchy(HierarchyOp.ADD)
    void addGraph(Graph graph);

    /**
     * Remove the graph from this layer.
     *
     * @param graph the graph to be removed
     */
    @Hierarchy(HierarchyOp.REMOVE)
    void removeGraph(Graph graph);

    /**
     * Returns the annotation.
     *
     * @param idx the index of annotations
     * @return the annotation.
     */
    @Hierarchy(HierarchyOp.GET)
    Annotation getAnnotation(int idx);

    /**
     * Returns all the annotations in this layer.
     *
     * @return all annotations in an array
     */
    @Hierarchy(HierarchyOp.GETARRAY)
    @Nonnull
    Annotation[] getAnnotations();

    /**
     * Add a new annotation to this layer.
     *
     * @param annotation the annotation to be added
     */
    @Hierarchy(HierarchyOp.ADD)
    void addAnnotation(Annotation annotation);

    /**
     * Remove the annotation from this layer.
     *
     * @param annotation the annotation to be removed
     */
    @Hierarchy(HierarchyOp.REMOVE)
    void removeAnnotation(Annotation annotation);

    /**
     * Returns the X axis that this layer attach to.
     *
     * @return the X axis that this layer attach to
     */
    @Hierarchy(HierarchyOp.GET)
    @Nullable
    AxisTransform getXAxisTransform();

    /**
     * Returns the Y axis that this layer attach to.
     *
     * @return the Y axis that this layer attach to
     */
    @Hierarchy(HierarchyOp.GET)
    @Nullable
    AxisTransform getYAxisTransform();

    /**
     * Attach this layer to the given X axis. When adding a layer to a plot,
     * the axis must exist in the destination environment, otherwise an exception will be thrown.
     *
     * @param axt the axis transform
     */
    @Hierarchy(HierarchyOp.REF)
    void setXAxisTransform(@Nonnull AxisTransform axt);

    /**
     * Attach this layer to the given Y axis. When adding a layer to a plot,
     * the axis must exist in the destination environment, otherwise an exception will be thrown.
     *
     * @param axt the axis transform
     */
    @Hierarchy(HierarchyOp.REF)
    void setYAxisTransform(@Nonnull AxisTransform axt);

    /**
     * Attach this layer to the given X/Y axis pair. When adding a layer to a plot, the X/Y axes must exist in the
     * destination environment, otherwise an exception will be thrown.
     *
     * @param xaxt the x axis transform
     * @param yaxt the y axis transform
     */
    @Hierarchy(HierarchyOp.REF2)
    void setAxesTransform(@Nonnull AxisTransform xaxt, @Nonnull AxisTransform yaxt);

}
