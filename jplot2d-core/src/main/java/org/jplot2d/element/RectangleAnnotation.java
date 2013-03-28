/**
 * Copyright 2010-2012 Jingjing Li.
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
 * A rectangle annotation to highlight an area defined by horizontal and vertical ranges.
 * 
 * @author Jingjing Li
 * 
 */
@PropertyGroup("Rectangle Annotation")
public interface RectangleAnnotation extends Annotation {

	/**
	 * Returns the x value range of this annotation
	 * 
	 * @return the x value range of this annotation
	 */
	@Property(order = 0, styleable = false)
	public Range getXValueRange();

	/**
	 * Sets the x value range of this annotation
	 * 
	 * @param range
	 *            the x value range of this annotation
	 */
	public void setXValueRange(Range range);

	/**
	 * Returns the y value range of this annotation
	 * 
	 * @return the y value range of this annotation
	 */
	@Property(order = 1, styleable = false)
	public Range getYValueRange();

	/**
	 * Sets the y value range of this annotation
	 * 
	 * @param range
	 *            the y value range of this annotation
	 */
	public void setYValueRange(Range range);

	/**
	 * Returns the <code>Paint</code> to be used to fill the annotation strip.
	 * 
	 * @return the <code>Paint</code>
	 */
	@Property(order = 2)
	public Paint getFillPaint();

	/**
	 * Sets the <code>Paint</code> to be used to fill the annotation strip. The default paint is semi-transparent grey.
	 * 
	 * @param stroke
	 *            the <code>Paint</code> to be used to fill the annotation strip
	 */
	public void setFillPaint(Paint stroke);

}
