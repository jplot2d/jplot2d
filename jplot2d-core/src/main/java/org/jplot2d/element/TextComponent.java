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

import org.jplot2d.annotation.Property;
import org.jplot2d.tex.MathElement;

/**
 * A component who represent a text string. The text string can be a math
 * element.
 * 
 * @author Jingjing Li
 */
public interface TextComponent extends PComponent {

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

	/**
	 * @return the horizontal alignment.
	 */
	public HAlign getHAlign();

	/**
	 * Set the horizontal alignment. The alignment can be LEFT, CENTER, or
	 * RIGHT. eg, LEFT means the title is on the left of the base point.
	 * 
	 * @param hAlign
	 *            the horizontal alignment.
	 */
	public void setHAlign(HAlign hAlign);

	/**
	 * @return the vertical alignment or null if not set.
	 */
	public VAlign getVAlign();

	/**
	 * Set the vertical alignment. The alignment can be TOP, MIDDLE, or BOTTOM.
	 * eg, TOP means the title is on the top of the base point
	 * 
	 * @param vAlign
	 *            the vertical alignment.
	 */
	public void setVAlign(VAlign vAlign);

	/**
	 * @return the rotation angle value or NaN if not set.
	 */
	public double getAngle();

	/**
	 * Set the rotation angle start to count from horizontal direction and grow
	 * in counter-clock wise direction.
	 * 
	 * @param angle
	 *            the rotation angle
	 */
	public void setAngle(double angle);

}
