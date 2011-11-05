/**
 * Copyright 2010, 2011 Jingjing Li.
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

import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;

import org.jplot2d.annotation.Hierarchy;
import org.jplot2d.annotation.HierarchyOp;
import org.jplot2d.annotation.Property;
import org.jplot2d.annotation.PropertyGroup;
import org.jplot2d.layout.LayoutDirector;
import org.jplot2d.sizing.SizeMode;
import org.jplot2d.util.DoubleDimension2D;

/**
 * Plot has a content area in the center, surrounded by margin area. The margin area holds axes,
 * title and legend.
 * <p>
 * Plot can optionally contains a group of layers that stack over each other, their viewports have
 * the same bounds of the content area. <br/>
 * Plot can also contains a group of subplots, which are laid out by LayoutDirector.
 * 
 * @author Jingjing Li
 * 
 */
@PropertyGroup("Plot")
public interface Plot extends PComponent {

	public static final Dimension2D MIN_CONTENT_SIZE = new DoubleDimension2D(8, 8);

	@Hierarchy(HierarchyOp.GET)
	public Plot getParent();

	/**
	 * Returns the size mode of this plot.
	 * 
	 * @return
	 */
	@Property(order = 0)
	public SizeMode getSizeMode();

	/**
	 * Sets a size mode to manage the size and scale of this plot.
	 * 
	 * @param mode
	 */
	public void setSizeMode(SizeMode mode);

	/**
	 * Returns the container size in device coordinate system.
	 * 
	 * @return the container size
	 */
	@Property(order = 1)
	public Dimension2D getContainerSize();

	/**
	 * Sets the container size. The container size is given in device coordinate system and used by
	 * size mode to derive paper size and scale. The default value is 640x480 pixels.
	 * <p>
	 * When the size mode is <code>null</code>, the container size has no effect.
	 * 
	 * @param size
	 */
	public void setContainerSize(Dimension2D size);

	/**
	 * Returns the scale of this plot. The scale is ratio device size to paper size.
	 * 
	 * @return the scale of this plot
	 */
	@Property(order = 2)
	public double getScale();

	/**
	 * Sets scale of this plot. This method only take effect when size mode is <code>null</code>.
	 * Otherwise the scale is decided by size mode.
	 * 
	 * @param scale
	 *            the scale
	 */
	@Property(order = 3)
	public void setScale(double scale);

	/**
	 * Sets the paper size of this plot. This method only take effect when size mode is
	 * <code>null</code>. Otherwise the size is decided by size mode.
	 * 
	 * @param size
	 *            the paper size
	 */
	public void setSize(Dimension2D size);

	/**
	 * Sets the paper size of this plot. This method only take effect when size mode is
	 * <code>null</code>. Otherwise the size is decided by size mode.
	 * 
	 * @param width
	 *            the paper width
	 * @param height
	 *            the paper height
	 */
	public void setSize(double width, double height);

	/**
	 * Gets the layout director for this plot.
	 * 
	 * @return the layout director for this plot.
	 */
	@Property(order = 10)
	public LayoutDirector getLayoutDirector();

	/**
	 * Sets the layout director for this plot.
	 * 
	 * @param director
	 *            the layout director
	 */
	public void setLayoutDirector(LayoutDirector director);

	/**
	 * Returns the constraint of the specified subplot in the current LayoutManager.
	 * 
	 * @param subplot
	 *            The subplot whose constraint is being set
	 * @return the constraint
	 * @throws IllegalArgumentException
	 *             if the subplot is not contained by this plot
	 */
	@Property(order = 11)
	public Object getConstraint(Plot subplot);

	/**
	 * Sets the constraint of the specified subplot in the current LayoutManager.
	 * 
	 * @param subplot
	 *            The subplot whose constraint is being set
	 * @param constraint
	 *            the constraint
	 * @throws IllegalArgumentException
	 *             if the subplot is not contained by this plot
	 */
	public void setConstraint(Plot subplot, Object constraint);

	/**
	 * Returns the the preferred content area size.
	 * 
	 * @return the the preferred content area size
	 */
	@Property(order = 12)
	public Dimension2D getPreferredContentSize();

	/**
	 * Sets the preferred content area size
	 * 
	 * @param size
	 *            the size in Dimension2D
	 */
	public void setPreferredContentSize(Dimension2D size);

	/**
	 * Sets the preferred content area size
	 * 
	 * @param width
	 *            the width
	 * @param height
	 *            the height
	 */
	public void setPreferredContentSize(double width, double height);

	/**
	 * Gets the location of this plot in its parent plot. The origin of a plot is the bottom-left
	 * corner of the content box (the intersect point of left axis and bottom axis). For root plot,
	 * the returned value is always (0,0)
	 * 
	 * @return an instance of <code>Point</code> representing the base point of this plot
	 */
	@Property(order = 13)
	public Point2D getLocation();

	/**
	 * Moves this plot to a new location. The new location is specified by point and is given in the
	 * parent's paper coordinate space.
	 * <p>
	 * Notice: This method should be called when the parent plot's layout director does not manage
	 * subplots, such as <code>SimpleLayoutDirector</code>, otherwise the location will be overwrite
	 * by the layout director.
	 * <p>
	 * For root plot, this method has no effect.
	 * 
	 * @param p
	 *            the point defining the origin of the new location
	 */
	public void setLocation(Point2D loc);

	public void setLocation(double locX, double locY);

	/**
	 * Returns the rectangle of content area.
	 * 
	 * @return the rectangle of content area.
	 */
	@Property(order = 14)
	public Dimension2D getContentSize();

	/**
	 * Returns the margin area of this plot.
	 * 
	 * @return
	 */
	@Hierarchy(HierarchyOp.GET)
	public PlotMargin getMargin();

	/**
	 * Returns the legend of this plot.
	 * 
	 * @return the legend of this plot
	 */
	@Hierarchy(HierarchyOp.GET)
	public Legend getLegend();

	/**
	 * Gets the nth title in this plot.
	 * 
	 * @param n
	 *            the index of the title to get.
	 * @return the nth title in this plot
	 */
	@Hierarchy(HierarchyOp.GET)
	public Title getTitle(int index);

	/**
	 * Returns all titles in the order of added.
	 * 
	 * @return all titles
	 */
	@Hierarchy(HierarchyOp.GETARRAY)
	public Title[] getTitles();

	@Hierarchy(HierarchyOp.ADD)
	public void addTitle(Title title);

	@Hierarchy(HierarchyOp.REMOVE)
	public void removeTitle(Title title);

	@Hierarchy(HierarchyOp.GET)
	public Axis getXAxis(int index);

	@Hierarchy(HierarchyOp.GET)
	public Axis getYAxis(int index);

	@Hierarchy(HierarchyOp.GETARRAY)
	public Axis[] getXAxes();

	@Hierarchy(HierarchyOp.GETARRAY)
	public Axis[] getYAxes();

	@Hierarchy(HierarchyOp.ADD)
	public void addXAxis(Axis axis);

	@Hierarchy(HierarchyOp.ADD)
	public void addYAxis(Axis axis);

	/**
	 * Add the given axes created by {@link ElementFactory#createAxes(int)} as x-axes
	 * 
	 * @param axes
	 *            the axes to be added
	 */
	@Hierarchy(HierarchyOp.ADD)
	public void addXAxes(Axis[] axes);

	/**
	 * Add the given axes created by {@link ElementFactory#createAxes(int)} as y-axes
	 * 
	 * @param axes
	 *            the axes to be added
	 */
	@Hierarchy(HierarchyOp.ADD)
	public void addYAxes(Axis[] axes);

	/**
	 * Removes the specified X axis from this plot if it is present.
	 * 
	 * @param axis
	 *            the X axis to be removed
	 * @return <code>true</code> if this plot contained the specified X axis
	 */
	@Hierarchy(HierarchyOp.REMOVE)
	public void removeXAxis(Axis axis);

	/**
	 * Removes the specified Y axis from this plot if it is present.
	 * 
	 * @param axis
	 *            the Y axis to be removed
	 * @return <code>true</code> if this plot contained the specified Y axis
	 */
	@Hierarchy(HierarchyOp.REMOVE)
	public void removeYAxis(Axis axis);

	/**
	 * Gets the nth layer in this plot.
	 * 
	 * @param n
	 *            the index of the layer to get.
	 * @return the nth layer in this plot
	 */
	@Hierarchy(HierarchyOp.GET)
	public Layer getLayer(int index);

	/**
	 * Returns all layers in the order of added.
	 * 
	 * @return all layers
	 */
	@Hierarchy(HierarchyOp.GETARRAY)
	public Layer[] getLayers();

	/**
	 * Add a layer to this plot. The layer will associate with the given X/Y axis range manager to
	 * control which part show in the plot viewport.
	 * 
	 * @param layer
	 *            the layer to be add
	 * @param xRangeManager
	 *            the x axis range manager
	 * @param yRangeManager
	 *            the y axis range manager
	 */
	@Hierarchy(HierarchyOp.ADD_REF2)
	public void addLayer(Layer layer, AxisTransform xRangeManager, AxisTransform yRangeManager);

	/**
	 * Add a layer to this plot. The layer will associate with the given X/Y axis' range manager to
	 * control which part show in the plot viewport. Equivalent to
	 * {@link #addLayer(Layer, AxisTransform, AxisTransform) addLayer(layer,
	 * xaxis.getTickManager().getRangeManager(), yaxis.getTickManager().getRangeManager())}
	 * 
	 * @param layer
	 *            the layer to be add
	 * @param xaxis
	 *            the x axis
	 * @param yaxis
	 *            the y axis
	 */
	@Hierarchy(HierarchyOp.ADD_REF2)
	public void addLayer(Layer layer, Axis xaxis, Axis yaxis);

	@Hierarchy(HierarchyOp.REMOVE)
	public void removeLayer(Layer layer);

	/**
	 * Gets the nth subplot in this plot.
	 * 
	 * @param n
	 *            the index of the component to get.
	 * @return the nth subplot in this subplot
	 */
	@Hierarchy(HierarchyOp.GET)
	public Plot getSubplot(int n);

	/**
	 * Gets the nth subplot in this plot.
	 * 
	 * @param n
	 *            the index of the component to get.
	 * @return the nth subplot in this plot
	 */
	@Hierarchy(HierarchyOp.GETARRAY)
	public Plot[] getSubplots();

	/**
	 * Add a subplot with a constraint to this plot.
	 * 
	 * @param subplot
	 * @param constraint
	 */
	@Hierarchy(HierarchyOp.ADD)
	void addSubplot(Plot subplot, Object constraint);

	/**
	 * remove the specified subplot from this plot.
	 * 
	 * @param subplot
	 */
	@Hierarchy(HierarchyOp.REMOVE)
	void removeSubplot(Plot subplot);

	/**
	 * Zoom the given range to entire X axis
	 * 
	 * @param start
	 *            the normalized start
	 * @param end
	 *            the normalized end
	 */
	public void zoomXRange(double start, double end);

	/**
	 * Zoom the given range to entire Y axis
	 * 
	 * @param start
	 *            the normalized start
	 * @param end
	 *            the normalized end
	 */
	public void zoomYRange(double start, double end);

	/**
	 * Adaptive zoom the x range.
	 */
	public void adaptiveZoomX();

	/**
	 * Adaptive zoom the y range.
	 */
	public void adaptiveZoomY();

}
