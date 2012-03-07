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
package org.jplot2d.element;

import org.jplot2d.annotation.Hierarchy;
import org.jplot2d.annotation.HierarchyOp;

/**
 * A layer can contains an dataset and optionally some annotations. Every layer has its own viewport
 * to show data line and annotations.
 * 
 * @author Jingjing Li
 * 
 */
public interface Layer extends PComponent {

	@Hierarchy(HierarchyOp.GET)
	public Plot getParent();

	/**
	 * Returns the graph on the given index.
	 * 
	 * @return the graph on the given index
	 */
	@Hierarchy(HierarchyOp.GET)
	public Graph getGraph(int index);

	/**
	 * Returns all graph of this layer.
	 * 
	 * @return all graph of this layer.
	 */
	@Hierarchy(HierarchyOp.GETARRAY)
	public Graph[] getGraph();

	/**
	 * Adds a graph to this layer.
	 * 
	 * @param graph
	 *            the graoh to be add.
	 */
	@Hierarchy(HierarchyOp.ADD)
	public void addGraph(Graph graph);

	/**
	 * Remove the graph from this layer.
	 * 
	 * @param graph
	 *            the graph to be removed
	 */
	@Hierarchy(HierarchyOp.REMOVE)
	public void removeGraph(Graph graph);

	/**
	 * Returns the annotation.
	 * 
	 * @param id
	 *            the index of annotations
	 * @return the annotation.
	 */
	@Hierarchy(HierarchyOp.GET)
	public Annotation getAnnotation(int idx);

	/**
	 * Returns all the annotations in this layer.
	 * 
	 * @return all annotations in an array
	 */
	@Hierarchy(HierarchyOp.GETARRAY)
	public Annotation[] getAnnotations();

	/**
	 * Add a new annotation to this layer.
	 * 
	 * @param annotation
	 *            the annotation to be added
	 */
	@Hierarchy(HierarchyOp.ADD)
	public void addAnnotation(Annotation annotation);

	/**
	 * Returns the X axis to this layer attaching.
	 * 
	 * @return
	 */
	@Hierarchy(HierarchyOp.GET)
	public AxisTransform getXAxisTransform();

	/**
	 * Returns the Y axis to this layer attaching.
	 * 
	 * @return
	 */
	@Hierarchy(HierarchyOp.GET)
	public AxisTransform getYAxisTransform();

	/**
	 * Attach this layer to the given X axis. When adding a layer to a plot, the axis must exist in
	 * the destination environment, otherwise a exception will be thrown.
	 * 
	 * @param axis
	 */
	@Hierarchy(HierarchyOp.REF)
	public void setXAxisTransform(AxisTransform rangeManager);

	/**
	 * Attach this layer to the given Y axis. When adding a layer to a plot, the axis must exist in
	 * the destination environment, otherwise a exception will be thrown.
	 * 
	 * @param axis
	 */
	@Hierarchy(HierarchyOp.REF)
	public void setYAxisTransform(AxisTransform rangeManager);

	/**
	 * Attach this layer to the given X/Y axis pair. When adding a layer to a plot, the X/Y axes
	 * must exist in the destination environment, otherwise a exception will be thrown.
	 * 
	 * @param xaxis
	 *            the x axis
	 * @param yaxis
	 *            the y axis
	 */
	@Hierarchy(HierarchyOp.REF2)
	public void setAxesTransform(AxisTransform xRangeManager, AxisTransform yRangeManager);

}
