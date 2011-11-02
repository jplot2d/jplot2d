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

import java.awt.Paint;

import org.jplot2d.annotation.Property;
import org.jplot2d.annotation.PropertyGroup;
import org.jplot2d.util.Range;

/**
 * A vertical strip marker to highlight a range.
 * 
 * @author Jingjing Li
 * 
 */
@PropertyGroup("Vertical Strip Marker")
public interface VStripMarker extends Marker {

	/**
	 * Returns the value range of this marker
	 * 
	 * @return the value range of this marker
	 */
	@Property(order = 0)
	public Range getValueRange();

	/**
	 * Sets the value range of this marker
	 * 
	 * @param range
	 *            the value range of this marker
	 */
	public void setValueRange(Range range);

	/**
	 * Returns the <code>Paint</code> to be used to draw the marker strip.
	 * 
	 * @return the <code>Paint</code>
	 */
	@Property(order = 0)
	public Paint getPaint();

	/**
	 * Sets the <code>Paint</code> to be used to draw the marker strip. The default paint is
	 * semi-transparent grey.
	 * 
	 * @param stroke
	 *            the <code>Paint</code> to be used to draw the marker strip
	 */
	public void setPaint(Paint stroke);

}
