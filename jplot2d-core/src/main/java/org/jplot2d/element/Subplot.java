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

/**
 * 
 * 
 * @author Jingjing Li
 * 
 */
public interface Subplot extends Container {

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
	 * Returns the physical rectangle of viewport.
	 * 
	 * @return the physical rectangle of viewport.
	 */
	public Rectangle2D getViewportBounds();

	/**
	 * Sets the physical rectangle of viewport. All layers in this subplot have
	 * the same viewport bounds.
	 * 
	 * @param bounds
	 *            the physical rectangle of viewport
	 */
	public void setViewportBounds(Rectangle2D bounds);

	@Hierarchy(HierarchyOp.GET)
	public Layer getLayer(int index);

	@Hierarchy(HierarchyOp.GETARRAY)
	public Layer[] getLayers();

	@Hierarchy(HierarchyOp.ADD)
	public void addLayer(Layer layer);

	@Hierarchy(HierarchyOp.REMOVE)
	public void removeLayer(Layer layer);

	@Hierarchy(HierarchyOp.GET)
	public Axis getAxis(int index);

	@Hierarchy(HierarchyOp.GETARRAY)
	public Axis[] getAxes();

	@Hierarchy(HierarchyOp.ADD)
	public void addAxis(Axis axis);

	@Hierarchy(HierarchyOp.REMOVE)
	public void removeAxis(Axis axis);

}
