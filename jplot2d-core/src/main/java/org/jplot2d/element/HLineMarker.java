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

import java.awt.Stroke;

import org.jplot2d.annotation.Property;
import org.jplot2d.annotation.PropertyGroup;

/**
 * A point marker with a line and a text string.
 * 
 * @author Jingjing Li
 * 
 */
@PropertyGroup("Horizontal Line Marker")
public interface HLineMarker extends Marker {

	/**
	 * Returns the value of this marker
	 * 
	 * @return the value of this marker
	 */
	@Property(order = 0)
	public double getValue();

	/**
	 * Sets the value of this marker
	 * 
	 * @param value
	 *            the value of this marker
	 */
	public void setValue(double value);

	/**
	 * Returns the <code>Stroke</code> to be used to draw the marker line.
	 * 
	 * @return the <code>Stroke</code>
	 */
	@Property(order = 0)
	public Stroke getStroke();

	/**
	 * Sets the <code>Stroke</code> to be used to draw the marker line.
	 * 
	 * @param stroke
	 *            the <code>Stroke</code> to be used to draw the marker line
	 */
	public void setStroke(Stroke stroke);

}
