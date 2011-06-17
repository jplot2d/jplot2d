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

import java.awt.Color;
import java.awt.Font;

import org.jplot2d.annotation.Property;
import org.jplot2d.tex.MathElement;

/**
 * A component who represent a text string. The text string can be a math
 * element.
 * 
 * @author Jingjing Li
 */
public interface AxisTitle extends Element {

	/**
	 * Gets the foreground color of this component.
	 * 
	 * @return this component's foreground color; if this component does not
	 *         have a foreground color, the foreground color of its parent is
	 *         returned
	 * @see #setColor
	 */
	public Color getColor();

	/**
	 * Sets the foreground color of this component.
	 * 
	 * @param c
	 *            the color to become this component's foreground color; if this
	 *            parameter is <code>null</code> then this component will
	 *            inherit the foreground color of its parent
	 * @see #getColor
	 */
	public void setColor(Color c);

	/**
	 * Returns the name of the font.
	 * 
	 * @return the name of the font.
	 */
	public String getFontName();

	/**
	 * Apply the new font with the given name
	 * 
	 * @param name
	 *            the font name.
	 */
	public void setFontName(String name);

	/**
	 * Returns the style of the font. The style can be PLAIN, BOLD, ITALIC, or
	 * BOLD+ITALIC.
	 * 
	 * @return the style of the font
	 * @see java.awt.Font
	 */
	public int getFontStyle();

	/**
	 * Apply a new style to the font. The style can be PLAIN, BOLD, ITALIC, or
	 * BOLD+ITALIC.
	 * 
	 * @param style
	 *            the style to apply
	 * @see java.awt.Font
	 */
	public void setFontStyle(int style);

	/**
	 * Returns the Font size.
	 * 
	 * @return the font size.
	 */
	public float getFontSize();

	/**
	 * Sets a new size of the string.
	 * 
	 * @param size
	 *            the new size of the font.
	 */
	public void setFontSize(float size);

	/**
	 * Returns the scale apply to parent's font size when font size is NaN.
	 * 
	 * @return the font scale.
	 */
	public float getFontScale();

	/**
	 * Sets the scale apply to parent's font size when font size is NaN.
	 * 
	 * @param scale
	 *            the scale
	 */
	public void setFontScale(float scale);

	/**
	 * Sets the font name, style and size for this component.
	 * 
	 * @param font
	 *            the desired <code>Font</code> for this component
	 */
	public void setFont(Font font);

	/**
	 * Returns the text string.
	 * 
	 * @return a String
	 */
	public String getText();

	/**
	 * Defines the single line of text to be displayed. The string can be in
	 * TeX-like syntax. A pair of "$" mark into math mode and out math mode. In
	 * math mode, Greek letter can be inputed as \alpha, \beta, etc.
	 * Superscripts (up high) and subscripts (down low) can be inputed by using
	 * "^" and "_". Notice that ^ and _ apply only to the next single character.
	 * If you want several things to be superscripted or subscripted, just
	 * enclose them in braces. eg: "plain text $x_\alpha^{2y}$".
	 * 
	 * @param text
	 */
	public void setText(String text);

	/**
	 * Returns the math model of this component.
	 * 
	 * @return the math model
	 */
	@Property(displayName = "text", description = "Can be TeX math string")
	public MathElement getTextModel();

	/**
	 * Defines lines of math text to be displayed. The math model can be parsed
	 * from a TeX-like string . A pair of "$" mark into math mode and out math
	 * mode. In math mode, Greek letter can be inputed as \alpha, \beta, etc.
	 * Superscripts (up high) and subscripts (down low) can be inputed by using
	 * "^" and "_". Notice that ^ and _ apply only to the next single character.
	 * If you want several things to be superscripted or subscripted, just
	 * enclose them in braces. eg: "plain text $x_\alpha^{2y}$".
	 * 
	 * @param model
	 *            the math model
	 */
	public void setTextModel(MathElement model);

}
