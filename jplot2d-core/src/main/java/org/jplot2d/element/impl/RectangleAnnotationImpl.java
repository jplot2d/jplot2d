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
package org.jplot2d.element.impl;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.awt.geom.Dimension2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import org.jplot2d.transform.PaperTransform;
import org.jplot2d.util.DoubleDimension2D;
import org.jplot2d.util.Range;

/**
 * @author Jingjing Li
 * 
 */
public class RectangleAnnotationImpl extends AnnotationImpl implements RectangleAnnotationEx {

	private static Paint DEFAULT_PAINT = new Color(192, 192, 192, 128);

	private double paperWidth, paperHeight;

	private Range xrange, yrange;

	private Paint paint = DEFAULT_PAINT;

	public String getId() {
		if (getParent() != null) {
			return "RectangleAnnotation" + getParent().indexOf(this);
		} else {
			return "RectangleAnnotation@" + Integer.toHexString(System.identityHashCode(this));
		}
	}

	public Point2D getLocation() {
		return new Point2D.Double(locX, locY);
	}

	public void setLocation(double x, double y) {
		if (locX != x) {
			this.locX = x;
			double endX = locX + paperWidth;
			double valueX = getXPtoW(locX);
			double valueXEnd = getXPtoW(endX);
			xrange = new Range.Double(valueX, valueXEnd);
			redraw(this);
		}
		if (locY != y) {
			this.locY = y;
			double endY = locY + paperHeight;
			double valueY = getYPtoW(locY);
			double valueYEnd = getYPtoW(endY);
			yrange = new Range.Double(valueY, valueYEnd);
			redraw(this);
		}
	}

	public Dimension2D getSize() {
		if (getParent() == null || getParent().getSize() == null) {
			return null;
		}
		return new DoubleDimension2D(Math.abs(paperWidth), Math.abs(paperHeight));
	}

	public Rectangle2D getBounds() {
		if (getParent() == null || getParent().getSize() == null) {
			return null;
		}

		return new Rectangle2D.Double(Math.min(paperWidth, 0), Math.min(paperHeight, 0), Math.abs(paperWidth),
				Math.abs(paperHeight));
	}

	public Rectangle2D getSelectableBounds() {
		if (getParent() == null || getParent().getSize() == null) {
			return null;
		}

		double rx = Math.min(paperWidth, 0);
		double ry = Math.min(paperHeight, 0);
		double rw = Math.abs(paperWidth);
		double rh = Math.abs(paperHeight);

		if (rw < 2) {
			rx = -1;
			rw = 2;
		}
		if (rh < 2) {
			ry = -1;
			rh = 2;
		}

		return new Rectangle2D.Double(rx, ry, rw, rh);
	}

	public PaperTransform getPaperTransform() {
		if (getParent() == null) {
			return null;
		}
		PaperTransform pxf = getParent().getPaperTransform();
		if (pxf == null) {
			return null;
		}
		return pxf.translate(locX, locY);
	}

	public Range getXValueRange() {
		return xrange;
	}

	public void setXValueRange(Range value) {
		this.xrange = value;
		if (getParent() != null && getParent().getXAxisTransform() != null) {
			relocate();
		}
	}

	public Range getYValueRange() {
		return yrange;
	}

	public void setYValueRange(Range value) {
		this.yrange = value;
		if (getParent() != null && getParent().getYAxisTransform() != null) {
			relocate();
		}
	}

	public void relocate() {
		locX = getXWtoP(xrange.getStart());
		double endX = getXWtoP(xrange.getEnd());
		paperWidth = endX - locX;

		locY = getYWtoP(yrange.getStart());
		double endY = getYWtoP(yrange.getEnd());
		paperHeight = endY - locY;

		redraw(this);
	}

	public Paint getFillPaint() {
		return paint;
	}

	public void setFillPaint(Paint paint) {
		this.paint = paint;
	}

	public void draw(Graphics2D g) {
		AffineTransform oldTransform = g.getTransform();
		Shape oldClip = g.getClip();

		g.transform(getParent().getPaperTransform().getTransform());
		g.setClip(getParent().getBounds());
		g.setPaint(paint);

		double rx = Math.min(paperWidth, 0);
		double ry = Math.min(paperHeight, 0);
		double rw = Math.abs(paperWidth);
		double rh = Math.abs(paperHeight);

		if (paperWidth == 0 || paperHeight == 0) {
			Line2D line = new Line2D.Double(locX + rx, locY + ry, locX + rw, locY + rh);
			Stroke oldStroke = g.getStroke();
			g.setStroke(ZERO_WIDTH_STROKE);
			g.draw(line);
			g.setStroke(oldStroke);
		} else {
			Rectangle2D strip = new Rectangle2D.Double(locX + rx, locY + ry, rw, rh);
			g.fill(strip);
		}

		g.setTransform(oldTransform);
		g.setClip(oldClip);
	}

	@Override
	public void copyFrom(ElementEx src) {
		super.copyFrom(src);

		RectangleAnnotationImpl lm = (RectangleAnnotationImpl) src;
		this.locX = lm.locX;
		this.locY = lm.locY;
		this.paperWidth = lm.paperWidth;
		this.paperHeight = lm.paperHeight;
		this.xrange = lm.xrange;
		this.yrange = lm.yrange;
		this.paint = lm.paint;
	}

}
