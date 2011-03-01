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
import java.awt.geom.Rectangle2D;

import org.jplot2d.annotation.Hierarchy;
import org.jplot2d.annotation.HierarchyOp;
import org.jplot2d.layout.LayoutDirector;
import org.jplot2d.util.DoubleDimension2D;

/**
 * Subplot has a content area in the center, surrounded by margin area. The
 * margin area holds axes, title and legend.
 * <p>
 * Subplot can optionally contains a group of layers that stack over each other,
 * their viewports have the same bounds of the content area. <br/>
 * Subplot can also contains a group of subplots, which are laid out by
 * LayoutDirector.
 * 
 * @author Jingjing Li
 * 
 */
public interface Subplot extends Container {

	public static final Dimension2D MIN_CONTENT_SIZE = new DoubleDimension2D(8,
			8);

	@Hierarchy(HierarchyOp.GET)
	public Subplot getParent();

	/**
	 * Moves this plot component to a new location. The origin of the new
	 * location is specified by point <code>p</code>. Point2D <code>p</code> is
	 * given in the parent's physical coordinate space.
	 * 
	 * @param p
	 *            the point defining the origin of the new location, given in
	 *            the coordinate space of this component's parent
	 */
	public void setLocation(Point2D loc);

	public void setLocation(double locX, double locY);

	/**
	 * Sets the paper size of this container.
	 * 
	 * @param paper
	 *            size
	 */
	public void setSize(Dimension2D size);

	public void setSize(double width, double height);

	/**
	 * Returns the margin area of this subplot.
	 * 
	 * @return
	 */
	public SubplotMargin getMargin();

	/**
	 * Gets the layout director for this subplot.
	 * 
	 * @return the layout director for this subplot.
	 */
	public LayoutDirector getLayoutDirector();

	/**
	 * Sets the layout director for this subplot.
	 * 
	 * @param director
	 *            the layout director
	 */
	public void setLayoutDirector(LayoutDirector director);

	/**
	 * Returns the constraint of the specified subplot in the current
	 * LayoutManager.
	 * 
	 * @param subplot
	 *            The subplot whose constraint is being set
	 * @return the constraint
	 * @throws IllegalArgumentException
	 *             if the subplot is not contained by this plot
	 */
	public Object getConstraint(Subplot subplot);

	/**
	 * Sets the constraint of the specified subplot in the current
	 * LayoutManager.
	 * 
	 * @param subplot
	 *            The subplot whose constraint is being set
	 * @param constraint
	 *            the constraint
	 * @throws IllegalArgumentException
	 *             if the subplot is not contained by this plot
	 */
	public void setConstraint(Subplot subplot, Object constraint);

	/**
	 * Returns the the preferred content area size.
	 * 
	 * @return the the preferred content area size
	 */
	public Dimension2D getPreferredContentSize();

	/**
	 * Sets the preferred content area size
	 * 
	 * @param size
	 */
	public void setPreferredContentSize(Dimension2D size);

	/**
	 * Returns the rectangle of content area.
	 * 
	 * @return the rectangle of content area.
	 */
	public Rectangle2D getContentBounds();

	/**
	 * Returns the legend of this subplot.
	 * 
	 * @return the legend of this subplot
	 */
	@Hierarchy(HierarchyOp.GET)
	public Legend getLegend();

	/**
	 * Gets the nth title in this plot.
	 * 
	 * @param n
	 *            the index of the title to get.
	 * @return the nth title in this subplot
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

	/**
	 * Gets the nth layer in this plot.
	 * 
	 * @param n
	 *            the index of the layer to get.
	 * @return the nth layer in this subplot
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

	@Hierarchy(HierarchyOp.ADD_REF2)
	public void addLayer(Layer layer, AxisRangeManager xRangeManager,
			AxisRangeManager yRangeManager);

	@Hierarchy(HierarchyOp.REMOVE)
	public void removeLayer(Layer layer);

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
	 * Removes the specified X axis from this subplot if it is present.
	 * 
	 * @param axis
	 *            the X axis to be removed
	 * @return <code>true</code> if this subplot contained the specified X axis
	 */
	@Hierarchy(HierarchyOp.REMOVE)
	public void removeXAxis(Axis axis);

	/**
	 * Removes the specified Y axis from this subplot if it is present.
	 * 
	 * @param axis
	 *            the Y axis to be removed
	 * @return <code>true</code> if this subplot contained the specified Y axis
	 */
	@Hierarchy(HierarchyOp.REMOVE)
	public void removeYAxis(Axis axis);

	/**
	 * Gets the nth subplot in this plot.
	 * 
	 * @param n
	 *            the index of the component to get.
	 * @return the nth subplot in this subplot
	 */
	@Hierarchy(HierarchyOp.GET)
	public Subplot getSubplot(int n);

	/**
	 * Gets the nth subplot in this plot.
	 * 
	 * @param n
	 *            the index of the component to get.
	 * @return the nth subplot in this plot
	 */
	@Hierarchy(HierarchyOp.GETARRAY)
	public Subplot[] getSubplots();

	/**
	 * @param subplot
	 * @param constraint
	 */
	@Hierarchy(HierarchyOp.ADD)
	void addSubplot(Subplot subplot, Object constraint);

	/**
	 * @param subplot
	 */
	@Hierarchy(HierarchyOp.REMOVE)
	void removeSubplot(Subplot subplot);

}
