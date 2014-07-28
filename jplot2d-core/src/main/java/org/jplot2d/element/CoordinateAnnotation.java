/**
 * Copyright 2010-2014 Jingjing Li.
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

import org.jplot2d.annotation.Property;
import org.jplot2d.annotation.PropertyGroup;

/**
 * A annotation with a symbol and a text string to show the coordinates. The text will automatically update whenever the
 * annotation changes to a new location.
 * 
 * @author Jingjing Li
 * 
 */
@PropertyGroup("Coordinate Annotation")
public interface CoordinateAnnotation extends SymbolAnnotation {

	@Property(order = 0, readOnly = true, styleable = false)
	public String getText();

	/**
	 * The text cannot be set. Setting a text will throw an UnsupportedOperationException.
	 */
	public void setText(String text);

}
