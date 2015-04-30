/**
 * Copyright 2010-2015 Jingjing Li.
 * <p/>
 * This file is part of jplot2d.
 * <p/>
 * jplot2d is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or any later version.
 * <p/>
 * jplot2d is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Lesser Public License for more details.
 * <p/>
 * You should have received a copy of the GNU Lesser General Public License
 * along with jplot2d. If not, see <http://www.gnu.org/licenses/>.
 */
package org.jplot2d.element;

import org.jplot2d.annotation.Property;
import org.jplot2d.annotation.PropertyGroup;

import java.awt.*;

/**
 * An element of {@link Axis} to group title related properties.
 *
 * @author Jingjing Li
 */
@PropertyGroup("AxisTitle")
public interface AxisTitle extends Element {

    /**
     * Determines whether this title should be visible when its parent is visible. AxisTitle are initially visible.
     *
     * @return <code>true</code> if the title is visible, <code>false</code> otherwise
     * @see #setVisible
     */
    @Property(order = 0)
    public boolean isVisible();

    /**
     * Shows or hides this title depending on the value of parameter <code>b</code>.
     *
     * @param b if <code>true</code>, shows this title; otherwise, hides this title
     * @see #isVisible
     */
    public void setVisible(boolean b);

    /**
     * Gets the color of this title text.
     *
     * @return the text color of this title
     * @see #setColor
     */
    @Property(order = 1)
    public Color getColor();

    /**
     * Sets the color of this title text.
     *
     * @param c the text color of this title
     * @see #getColor
     */
    public void setColor(Color c);

    /**
     * Returns the name of the font.
     *
     * @return the name of the font.
     */
    @Property(order = 2)
    public String getFontName();

    /**
     * Apply the new font with the given name
     *
     * @param name the font name.
     */
    public void setFontName(String name);

    /**
     * Returns the style of the font. The style can be PLAIN, BOLD, ITALIC, or BOLD+ITALIC.
     *
     * @return the style of the font
     * @see java.awt.Font
     */
    @Property(order = 3)
    public int getFontStyle();

    /**
     * Apply a new style to the font. The style can be PLAIN, BOLD, ITALIC, or BOLD+ITALIC.
     *
     * @param style the style to apply
     * @see java.awt.Font
     */
    public void setFontStyle(int style);

    /**
     * Returns the font size of the title text.
     *
     * @return the font size
     */
    @Property(order = 4)
    public float getFontSize();

    /**
     * Sets a new font size of the title text.
     *
     * @param size the new font size of the title text
     */
    public void setFontSize(float size);

    /**
     * Returns the scale apply to the font size of axis when font size of title is NaN.
     *
     * @return the font scale
     */
    @Property(order = 5)
    public float getFontScale();

    /**
     * Sets the scale apply to the font size of axis when font size of title is NaN.
     *
     * @param scale the scale
     */
    public void setFontScale(float scale);

    /**
     * Returns the effective font of this axis title.
     *
     * @return the effective font of this axis title
     */
    public Font getEffectiveFont();

    /**
     * Sets the font name, style and size for axis title.
     *
     * @param font the desired <code>Font</code> for this axis title
     */
    public void setFont(Font font);

    /**
     * Returns the text string.
     *
     * @return a String
     */
    @Property(order = 6, description = "Can be TeX math string", styleable = false)
    public String getText();

    /**
     * Defines the text to be displayed. The text can be multi-line, split by \n.
     * The string of every line can be in TeX-like syntax. A pair of "$" mark into math mode and out math mode.
     * In math mode, Greek letter can be inputted as \alpha, \beta, etc.
     * Superscripts (up high) and subscripts (down low) can be inputted by using "^" and "_".
     * Notice that ^ and _ apply only to the next single character.
     * If you want several things to be superscripted or subscripted, just enclose them in braces. eg:
     * "plain text $x_\alpha^{2y}$".
     *
     * @param text the text to be displayed
     */
    public void setText(String text);

}
