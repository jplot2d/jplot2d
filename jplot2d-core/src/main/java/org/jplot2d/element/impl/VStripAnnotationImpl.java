/**
 * Copyright 2010-2013 Jingjing Li.
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
public class VStripAnnotationImpl extends AnnotationImpl implements VStripAnnotationEx {

	private static Paint DEFAULT_PAINT = new Color(192, 192, 192, 128);

	private double locX = Double.NaN;

	private double paperThickness;

	private Range range;

	private Paint paint = DEFAULT_PAINT;

	public String getId() {
		if (getParent() != null) {
			return "VStripAnnotation" + getParent().indexOf(this);
		} else {
			return "VStripAnnotation@" + Integer.toHexString(System.identityHashCode(this));
		}
	}

	public Point2D getLocation() {
		return new Point2D.Double(locX, 0);
	}

	public void setLocation(double x, double y) {
		if (locX != x) {
			this.locX = x;
			double endX = locX + paperThickness;
			double valueX = getXPtoW(locX);
			double valueEnd = getXPtoW(endX);
			range = new Range.Double(valueX, valueEnd);
			redraw(this);
		}
	}

	public Dimension2D getSize() {
		if (getParent() == null || getParent().getSize() == null) {
			return null;
		}
		return new DoubleDimension2D(getParent().getSize().getHeight(), Math.abs(paperThickness));
	}

	public Rectangle2D getBounds() {
		if (getParent() == null || getParent().getSize() == null) {
			return null;
		}

		if (paperThickness >= 0) {
			return new Rectangle2D.Double(0, -paperThickness, getParent().getSize().getHeight(), paperThickness);
		} else {
			return new Rectangle2D.Double(0, 0, getParent().getSize().getHeight(), -paperThickness);
		}
	}

	public Rectangle2D getSelectableBounds() {
		if (getParent() == null || getParent().getSize() == null) {
			return null;
		}

		if (-2 < paperThickness && paperThickness < 2) {
			return new Rectangle2D.Double(-1, 0, 2, getParent().getSize().getHeight());
		} else {
			return getBounds();
		}
	}

	public PaperTransform getPaperTransform() {
		if (getParent() == null) {
			return null;
		}
		PaperTransform pxf = getParent().getPaperTransform();
		if (pxf == null) {
			return null;
		}
		return pxf.translate(locX, 0).rotate(Math.PI / 2);
	}

	public Range getValueRange() {
		return range;
	}

	public void setValueRange(Range value) {
		this.range = value;
		if (getParent() != null && getParent().getXAxisTransform() != null) {
			relocate();
		}
	}

	public void relocate() {
		calcLocation();
		redraw(this);
	}

	private void calcLocation() {
		locX = getXWtoP(range.getStart());
		double endX = getXWtoP(range.getEnd());
		paperThickness = endX - locX;
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

		if (Double.isNaN(locX)) {
			calcLocation();
		}

		if (paperThickness == 0) {
			Line2D line = new Line2D.Double(locX, 0, locX, getParent().getSize().getHeight());
			Stroke oldStroke = g.getStroke();
			g.setStroke(ZERO_WIDTH_STROKE);
			g.draw(line);
			g.setStroke(oldStroke);
		} else {
			Rectangle2D strip;
			if (paperThickness > 0) {
				strip = new Rectangle2D.Double(locX, 0, paperThickness, getParent().getSize().getHeight());
			} else {
				strip = new Rectangle2D.Double(locX + paperThickness, 0, -paperThickness, getParent().getSize()
						.getHeight());
			}
			g.fill(strip);
		}

		g.setTransform(oldTransform);
		g.setClip(oldClip);
	}

	@Override
	public void copyFrom(ElementEx src) {
		super.copyFrom(src);

		VStripAnnotationImpl lm = (VStripAnnotationImpl) src;
		this.locX = lm.locX;
		this.paperThickness = lm.paperThickness;
		this.range = lm.range;
		this.paint = lm.paint;
	}

}
