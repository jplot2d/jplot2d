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

import java.awt.geom.Point2D;

import org.jplot2d.annotation.Property;
import org.jplot2d.annotation.PropertyGroup;

/**
 * A marker which can highlight a point. The marker can contains a text string.
 * 
 * @author Jingjing Li
 * 
 */
@PropertyGroup("Point Marker")
public interface PointMarker extends Marker {

	@Property(order = 0)
	public boolean isMovable();

	public void setMovable(boolean movable);

	/**
	 * returns the x,y values location in layer's world coordinate system.
	 * 
	 * @return the x,y values location
	 */
	@Property(order = 1)
	public Point2D getValuePoint();

	/**
	 * Sets the x,y values location in layer's world coordinate system
	 * 
	 * @param point
	 *            the x,y values location
	 */
	public void setValuePoint(Point2D point);

	/**
	 * Returns the rotation angle of this component.
	 * 
	 * @return the rotation angle value
	 */
	@Property(order = 2)
	public double getAngle();

	/**
	 * Set the rotation angle start to count from horizontal direction and grow in counter-clock
	 * wise direction.
	 * 
	 * @param angle
	 *            the rotation angle
	 */
	public void setAngle(double angle);

}
