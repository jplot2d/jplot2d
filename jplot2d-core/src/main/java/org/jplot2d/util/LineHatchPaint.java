/*
 * This file is part of Herschel Common Science System (HCSS).
 * Copyright 2001-2010 Herschel Science Ground Segment Consortium
 *
 * HCSS is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of
 * the License, or (at your option) any later version.
 *
 * HCSS is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General
 * Public License along with HCSS.
 * If not, see <http://www.gnu.org/licenses/>.
 */
package org.jplot2d.util;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.PaintContext;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.TexturePaint;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;

/**
 * The LineHatchPaint class provides a way to fill a Shape with line hatch pattern. Because Java
 * require instances of classes implementing Paint must be read-only, this class is more like a
 * factory to create paint instance for renderer threads.
 * 
 * @author Jingjing Li
 */
public class LineHatchPaint implements Paint {

	private static final Color TRANSPARENT_COLOR = new Color(0, 0, 0, 0);

	private static final Color DEFAULT_COLOR = Color.BLACK;

	private final Color color;

	private final float lineWidth;

	private final float[] dash;

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
		this(DEFAULT_COLOR, 0, null, angle, 6);
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
		this(DEFAULT_COLOR, width, null, angle, spacing);
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
	public LineHatchPaint(Color color, float width, float[] dash, double angle, double spacing) {
		this.color = color;
		this.lineWidth = width;
		this.dash = dash;
		this.angle = angle;
		this.spacing = spacing;
	}

	public PaintContext createContext(ColorModel cm, Rectangle deviceBounds,
			Rectangle2D userBounds, AffineTransform xform, RenderingHints hints) {
		throw new UnsupportedOperationException();
	}

	public int getTransparency() {
		throw new UnsupportedOperationException();
	}

	/**
	 * Create a Paint with the given image and scale. The given image will be cleared and filled by
	 * hatch lines.
	 * 
	 * @param hatchImg
	 * @param scale
	 * @return a TexturePaint
	 */
	public Paint createPaint(BufferedImage hatchImg, double scale) {
		int imgWidth = hatchImg.getWidth();
		int imgHeight = hatchImg.getHeight();

		Graphics2D g = (Graphics2D) hatchImg.getGraphics();

		// clear the image
		g.setBackground(TRANSPARENT_COLOR);
		g.clearRect(0, 0, imgWidth, imgHeight);

		hatch(g, scale, imgWidth, imgHeight);
		return new TexturePaint(hatchImg, new Rectangle(0, 0, imgWidth, imgHeight));
	}

	/**
	 * fill the buffered image with hatch lines.
	 */
	private void hatch(Graphics2D g, double scale, int width, int height) {
		double sf = scale / 72;

		g.translate(width / 2.0, height / 2.0);
		g.rotate(Math.toRadians(-angle));
		g.scale(sf, sf);

		if (dash == null) {
			g.setStroke(new BasicStroke(lineWidth, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER));
		} else {
			g.setStroke(new BasicStroke(lineWidth, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER,
					10f, dash, 0f));
		}
		g.setColor(color);

		// the max possible length of line, and the height for line spacing.
		double halfStdDiagonal = Math.hypot(width, height) / sf / 2;
		int maxiy = (int) (halfStdDiagonal / spacing);
		for (double iy = -maxiy; iy <= maxiy; iy++) {
			double y = spacing * iy;
			g.draw(new Line2D.Double(-halfStdDiagonal, y, halfStdDiagonal, y));
		}
	}

}
