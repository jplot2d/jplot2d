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
 * A layer can contains an dataset and optionally some markers. Every layer has its own viewport to
 * show data line and markers.
 * 
 * @author Jingjing Li
 * 
 */
public interface Layer extends PComponent {

	@Hierarchy(HierarchyOp.GET)
	public Plot getParent();

	/**
	 * Returns the graph plotter on the given index.
	 * 
	 * @return the graph plotter on the given index
	 */
	@Hierarchy(HierarchyOp.GET)
	public GraphPlotter getGraphPlotter(int index);

	/**
	 * Returns all graph plotters of this layer.
	 * 
	 * @return all graph plotters of this layer.
	 */
	@Hierarchy(HierarchyOp.GETARRAY)
	public GraphPlotter[] getGraphPlotters();

	/**
	 * Adds a graph plotter to this layer.
	 * 
	 * @param plotter
	 *            the plotter to be add.
	 */
	@Hierarchy(HierarchyOp.ADD)
	public void addGraphPlotter(GraphPlotter plotter);

	/**
	 * Remove the graph plotter from this layer.
	 * 
	 * @param plotter
	 *            the plotter to be removed
	 */
	@Hierarchy(HierarchyOp.REMOVE)
	public void removeGraphPlotter(GraphPlotter plotter);

	/**
	 * Returns the marker.
	 * 
	 * @param id
	 *            the index of markers
	 * @return the marker.
	 */
	@Hierarchy(HierarchyOp.GET)
	public Marker getMarker(int idx);

	/**
	 * Returns all the markers in this layer.
	 * 
	 * @return all markers in an array
	 */
	@Hierarchy(HierarchyOp.GETARRAY)
	public Marker[] getMarkers();

	/**
	 * Add a new marker to this layer.
	 * 
	 * @param marker
	 *            the marker to be added
	 */
	@Hierarchy(HierarchyOp.ADD)
	public void addMarker(Marker marker);

	/**
	 * Returns the X axis to this layer attaching.
	 * 
	 * @return
	 */
	@Hierarchy(HierarchyOp.GET)
	public AxisTransform getXRangeManager();

	/**
	 * Returns the Y axis to this layer attaching.
	 * 
	 * @return
	 */
	@Hierarchy(HierarchyOp.GET)
	public AxisTransform getYRangeManager();

	/**
	 * Attach this layer to the given X axis. When adding a layer to a plot, the axis must exist in
	 * the destination environment, otherwise a exception will be thrown.
	 * 
	 * @param axis
	 */
	@Hierarchy(HierarchyOp.REF)
	public void setXRangeManager(AxisTransform rangeManager);

	/**
	 * Attach this layer to the given Y axis. When adding a layer to a plot, the axis must exist in
	 * the destination environment, otherwise a exception will be thrown.
	 * 
	 * @param axis
	 */
	@Hierarchy(HierarchyOp.REF)
	public void setYRangeManager(AxisTransform rangeManager);

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
	public void setRangeManager(AxisTransform xRangeManager, AxisTransform yRangeManager);

}
