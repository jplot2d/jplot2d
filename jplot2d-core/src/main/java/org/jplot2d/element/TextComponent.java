/*
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
import org.jplot2d.annotation.PropertyGroup;

/**
 * A component who represent a text string. The text string can be a math element.
 *
 * @author Jingjing Li
 */
@PropertyGroup("Text")
public interface TextComponent extends PComponent {

    /**
     * Returns the text string.
     *
     * @return a String
     */
    @Property(order = 0, description = "Can be TeX math string", styleable = false)
    String getText();

    /**
     * Defines the text to be displayed. The text can be multi-line, splited by \n.
     * The string of every line can be in TeX-like syntax. A pair of "$" mark into math mode and out math mode.
     * In math mode, Greek letter can be inputed as \alpha, \beta, etc.
     * Superscripts (up high) and subscripts (down low) can be inputed by using "^" and "_".
     * Notice that ^ and _ apply only to the next single character.
     * If you want several things to be superscripted or subscripted, just enclose them in braces. eg:
     * "plain text $x_\alpha^{2y}$".
     *
     * @param text the text to be displayed
     */
    void setText(String text);

    /**
     * @return the horizontal alignment.
     */
    @Property(order = 1)
    HAlign getHAlign();

    /**
     * Set the horizontal alignment. The alignment can be LEFT, CENTER, or RIGHT.
     * eg, LEFT means the title is on the left of the base point.
     *
     * @param hAlign the horizontal alignment.
     */
    void setHAlign(HAlign hAlign);

    /**
     * @return the vertical alignment or null if not set.
     */
    @Property(order = 2)
    VAlign getVAlign();

    /**
     * Set the vertical alignment. The alignment can be TOP, MIDDLE, or BOTTOM.
     * eg, TOP means the title is on the top of the base point
     *
     * @param vAlign the vertical alignment.
     */
    void setVAlign(VAlign vAlign);

}
