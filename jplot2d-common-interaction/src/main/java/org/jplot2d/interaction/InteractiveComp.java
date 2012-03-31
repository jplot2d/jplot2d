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
 * 
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
	 * 
	 * @param cursorStyle
	 *            the style of cursor
	 */
	public CursorStyle getCursor();

	/**
	 * Sets the cursor.
	 * 
	 * @param cursorStyle
	 *            the style of cursor
	 */
	public void setCursor(CursorStyle cursorStyle);

	/**
	 * Draws a rectangle with the given graphics object and rgb color.
	 * 
	 * @param g
	 *            the graphics object
	 * @param rgb
	 *            the color value
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 */
	public void drawRectangle(Object g, int rgb, int x, int y, int width, int height);

	/**
	 * Draws a shape with the given graphics object and rgb color.
	 * 
	 * @param g
	 *            the graphics object
	 * @param rgb
	 *            the color value
	 * @param shape
	 *            the shape to draw
	 */
	public void drawShape(Object g, int rgb, Shape shape);

	/**
	 * Draws a tooltip on the given location.
	 * 
	 * @param g
	 *            the graphics object
	 * @param s
	 *            the string
	 * @param x
	 * @param y
	 */
	public void drawTooltip(Object g, String s, int x, int y);

}
