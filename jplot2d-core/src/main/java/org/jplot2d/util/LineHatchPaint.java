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
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
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

	/**
	 * Calculate hatch shapes to fill the given shape.
	 * 
	 * @param shape
	 *            the shape to hatch
	 * @param clip
	 *            the clip
	 * @param scale
	 * @return hatch shapes
	 */
	public Shape[] calcHatchShapes(Shape shape, Rectangle2D clip, double scale) {

		Line2D[] clippedLines = calcHatchLinesClipped(this, clip, scale);

		for (Line2D line : clippedLines) {

		}

		return new Line2D[0];
	}

	public static Line2D[] calcHatchLinesClipped(LineHatchPaint lhp, Rectangle2D clip, double scale) {

		double angle = lhp.getAngle() % 360.0;
		if (angle < 0) {
			angle += 360;
		}

		double diagonalLength = Math.hypot(clip.getHeight(), clip.getWidth());
		double diagonalAngle = Math.atan(clip.getHeight() / clip.getWidth());

		double halfLength = diagonalLength / 2 * Math.cos(angle - diagonalAngle);
		double halfRange = diagonalLength / 2 * Math.sin(angle + diagonalAngle);
		double spacing = lhp.spacing() * scale;
		int halfn = (int) (halfRange / spacing);

		Line2D[] result = new Line2D[2 * halfn + 1];

		double a0x = clip.getCenterX() - halfLength * Math.cos(angle);
		double a0y = clip.getCenterY() - halfLength * Math.sin(angle);
		double b0x = clip.getCenterX() + halfLength * Math.cos(angle);
		double b0y = clip.getCenterY() + halfLength * Math.sin(angle);
		for (int i = -halfn; i < halfn; i++) {
			double aix = a0x - spacing * Math.sin(angle);
			double aiy = a0y + spacing * Math.cos(angle);
			double bix = b0x - spacing * Math.sin(angle);
			double biy = b0y + spacing * Math.cos(angle);
			result[1] = new Line2D.Float((float) aix, (float) aiy, (float) bix, (float) biy);
		}

		return result;
	}
}
