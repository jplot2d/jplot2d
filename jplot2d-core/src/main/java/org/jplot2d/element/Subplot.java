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

import java.awt.geom.Dimension2D;
import java.awt.geom.Rectangle2D;

import org.jplot2d.annotation.Hierarchy;
import org.jplot2d.annotation.HierarchyOp;
import org.jplot2d.layout.LayoutDirector;

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

	public Subplot getParent();

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
	 * Returns the the preferred viewport physical size.
	 * 
	 * @return the the preferred viewport physical size
	 */
	public Dimension2D getViewportPreferredSize();

	/**
	 * Sets the preferred viewport physical size
	 * 
	 * @param size
	 */
	public void setViewportPreferredSize(Dimension2D size);

	/**
	 * Returns the rectangle of viewport.
	 * 
	 * @return the rectangle of viewport.
	 */
	public Rectangle2D getViewportBounds();

	/**
	 * Sets the rectangle of viewport. All layers in this subplot have the same
	 * viewport bounds.
	 * 
	 * @param bounds
	 *            the rectangle of viewport
	 */
	public void setViewportBounds(Rectangle2D bounds);

	@Hierarchy(HierarchyOp.GET)
	public Layer getLayer(int index);

	/**
	 * Returns all layers in the order of added.
	 * 
	 * @return all layers
	 */
	@Hierarchy(HierarchyOp.GETARRAY)
	public Layer[] getLayers();

	@Hierarchy(HierarchyOp.ADD)
	public void addLayer(Layer layer);

	@Hierarchy(HierarchyOp.REMOVE)
	public void removeLayer(Layer layer);

	@Hierarchy(HierarchyOp.GET)
	public ViewportAxis getXViewportAxis(int index);

	@Hierarchy(HierarchyOp.GET)
	public ViewportAxis getYViewportAxis(int index);

	@Hierarchy(HierarchyOp.GETARRAY)
	public ViewportAxis[] getXViewportAxes();

	@Hierarchy(HierarchyOp.GETARRAY)
	public ViewportAxis[] getYViewportAxes();

	@Hierarchy(HierarchyOp.ADD)
	public void addXViewportAxis(ViewportAxis vpAxis);

	@Hierarchy(HierarchyOp.ADD)
	public void addYViewportAxis(ViewportAxis vpAxis);

	/**
	 * Removes the specified X axis from this subplot if it is present.
	 * 
	 * @param axis
	 *            the X axis to be removed
	 * @return <code>true</code> if this subplot contained the specified X axis
	 */
	@Hierarchy(HierarchyOp.REMOVE)
	public void removeXViewportAxis(ViewportAxis axisGroup);

	/**
	 * Removes the specified Y axis from this subplot if it is present.
	 * 
	 * @param axis
	 *            the Y axis to be removed
	 * @return <code>true</code> if this subplot contained the specified Y axis
	 */
	@Hierarchy(HierarchyOp.REMOVE)
	public void removeYViewportAxis(ViewportAxis axisGroup);

	/**
	 * Gets the nth subplot in this plot.
	 * 
	 * @param n
	 *            the index of the component to get.
	 * @return the nth subplot in this plot
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
