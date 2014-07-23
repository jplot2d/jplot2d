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
import org.jplot2d.util.SymbolShape;

/**
 * A point annotation with a symbol and a text string.
 * 
 * @author Jingjing Li
 * 
 */
@PropertyGroup("Symbol Annotation")
public interface SymbolAnnotation extends PointAnnotation, TextComponent {

	/**
	 * Returns the symbol shape of this annotation
	 * 
	 * @return the symbol shape of this annotation
	 */
	@Property(order = 0, styleable = false)
	public SymbolShape getSymbolShape();

	/**
	 * Sets the symbol shape of this annotation
	 * 
	 * @param symbol
	 *            the symbol shape of this annotation
	 */
	public void setSymbolShape(SymbolShape symbol);

	/**
	 * Returns the symbol size of this annotation
	 * 
	 * @return the symbol size of this annotation
	 */
	@Property(order = 1)
	public float getSymbolSize();

	/**
	 * Sets the symbol shape of this annotation
	 * 
	 * @param symbol
	 *            the symbol size of this annotation
	 */
	public void setSymbolSize(float size);

	/**
	 * Returns the scale apply to effective font size to derive symbol size when symbol size is NaN.
	 * 
	 * @return the font scale.
	 */
	@Property(order = 2)
	public float getSymbolScale();

	/**
	 * Sets the scale apply to effective font size to derive symbol size when symbol size is NaN.
	 * 
	 * @param scale
	 *            the scale
	 */
	public void setSymbolScale(float scale);

	/**
	 * Returns the X offset factor of text. The offset is relative to the base point of this annotation. The factor
	 * apply to the half size of symbol.
	 * 
	 * @return the X offset factor of text
	 */
	@Property(order = 3)
	public float getTextOffsetFactorX();

	/**
	 * Sets the X offset factor of text. The offset is relative to the base point of this annotation. The factor apply
	 * to the half size of symbol.
	 * 
	 * @param offset
	 *            the X offset factor of text
	 */
	public void setTextOffsetFactorX(float offset);

	/**
	 * Returns the Y offset of text.The offset is relative to the base point of this annotation. The factor apply to the
	 * half size of symbol.
	 * 
	 * @return the Y offset factor of text
	 */
	@Property(order = 4)
	public float getTextOffsetFactorY();

	/**
	 * Sets the Y offset of text.The offset is relative to the base point of this annotation. The factor apply to the
	 * half size of symbol.
	 * 
	 * @param offset
	 *            the Y offset factor of text
	 */
	public void setTextOffsetFactorY(float offset);

}
