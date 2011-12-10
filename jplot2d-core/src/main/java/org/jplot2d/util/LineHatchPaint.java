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
package org.jplot2d.util;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Paint;
import java.awt.PaintContext;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.ColorModel;

/**
 * The LineHatchPaint class provides a way to fill a Shape with line hatch pattern. Because Java
 * require instances of classes implementing Paint must be read-only, this class is more like a
 * factory to create paint instance for renderer threads.
 * 
 * @author Jingjing Li
 */
public class LineHatchPaint implements Paint {

	private static final Color DEFAULT_COLOR = Color.BLACK;

	private final Color color;

	private final BasicStroke stroke;

	private final double angle;

	private final double spacing;

	/**
	 * Construct a LineHatchPaint with the given angle. The default values:
	 * <ul>
	 * <li>color: BLACK</li>
	 * <li>line width: 0.0</li>
	 * <li>line style: solid</li>
	 * <li>line spacing: 6pt</li>
	 * </ul>
	 * 
	 * @param angle
	 *            the counterclockwise angle in degrees from horizontal
	 */
	public LineHatchPaint(double angle) {
		this(0, angle, 6);
	}

	/**
	 * Construct a LineHatchPaint with the given angle. The default values:
	 * <ul>
	 * <li>color: BLACK</li>
	 * <li>line style: solid</li>
	 * </ul>
	 * 
	 * @param width
	 *            the line width
	 * @param angle
	 *            the counterclockwise angle in degrees from horizontal
	 * @param spacing
	 *            the spacing, in point(1/72 inch), between the parallel lines
	 */
	public LineHatchPaint(float width, double angle, double spacing) {
		this(DEFAULT_COLOR, new BasicStroke(width), angle, spacing);
	}

	/**
	 * Construct a LineHatchPaint with given arguments.
	 * 
	 * @param width
	 *            the line width
	 * @param dash
	 *            the dash array. null means solid line
	 * @param angle
	 *            the counterclockwise angle in degrees from horizontal
	 * @param spacing
	 *            the spacing, in point(1/72 inch), between the parallel lines
	 */
	public LineHatchPaint(Color color, BasicStroke stroke, double angle, double spacing) {
		this.color = color;
		this.stroke = stroke;
		this.angle = angle;
		this.spacing = spacing;
	}

	public Color getColor() {
		return color;
	}

	public BasicStroke getStroke() {
		return stroke;
	}

	public double getAngle() {
		return angle;
	}

	public double spacing() {
		return spacing;
	}

	public PaintContext createContext(ColorModel cm, Rectangle deviceBounds,
			Rectangle2D userBounds, AffineTransform xform, RenderingHints hints) {
		throw new UnsupportedOperationException();
	}

	public int getTransparency() {
		throw new UnsupportedOperationException();
	}

}
