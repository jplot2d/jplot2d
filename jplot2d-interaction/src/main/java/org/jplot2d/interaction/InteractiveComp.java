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
package org.jplot2d.interaction;

import java.awt.Point;
import java.awt.Shape;

/**
 * A generic interface for awt component and swt composite, to provide visual feedback.
 *
 * @author Jingjing Li
 */
public interface InteractiveComp {

    public enum CursorStyle {
        DEFAULT_CURSOR, MOVE_CURSOR, CROSSHAIR_CURSOR
    }

    public Point getCursorLocation();

    /**
     * Repaints this component.
     */
    public void repaint();

    /**
     * Gets the cursor.
     */
    public CursorStyle getCursor();

    /**
     * Sets the cursor.
     *
     * @param cursorStyle the style of cursor
     */
    public void setCursor(CursorStyle cursorStyle);

    /**
     * Draws a line with the given graphics object and rgb color.
     *
     * @param g   the graphics object
     * @param rgb the color value
     * @param x1  the first point's x coordinate
     * @param y1  the first point's y coordinate
     * @param x2  the second point's x coordinate
     * @param y2  the second point's y coordinate
     */
    public void drawLine(Object g, int rgb, int x1, int y1, int x2, int y2);

    /**
     * Draws a rectangle with the given graphics object and rgb color.
     *
     * @param g      the graphics object
     * @param rgb    the color value
     * @param x      the x coordinate of the rectangle to be drawn
     * @param y      the y coordinate of the rectangle to be drawn
     * @param width  the width of the rectangle to be drawn
     * @param height the height of the rectangle to be drawn
     */
    public void drawRectangle(Object g, int rgb, int x, int y, int width, int height);

    /**
     * Draws a shape with the given graphics object and rgb color.
     *
     * @param g     the graphics object
     * @param rgb   the color value
     * @param shape the shape to draw
     */
    public void drawShape(Object g, int rgb, Shape shape);

    /**
     * Draws a tooltip on the given location.
     *
     * @param g the graphics object
     * @param s the string
     * @param x the x coordinate of the tooltip to be drawn
     * @param y the x coordinate of the tooltip to be drawn
     */
    public void drawTooltip(Object g, String s, int x, int y);

}
