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

import java.awt.geom.Point2D;

import org.jplot2d.annotation.Hierarchy;
import org.jplot2d.annotation.HierarchyOp;

/**
 * A marker with a symbol and a text string.
 * 
 * @author Jingjing Li
 * 
 */
public interface Marker extends PComponent {

	@Hierarchy(HierarchyOp.GET)
	public Layer getParent();

	/**
	 * Returns <code>true</code> if the component is movable by mouse dragging.
	 * Only selectable component can be movable.
	 * 
	 * @return <code>true</code> if movable
	 */
	public boolean isMovable();

	/**
	 * Set the movable property.
	 */
	public void setMovable(boolean movable);

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

}
