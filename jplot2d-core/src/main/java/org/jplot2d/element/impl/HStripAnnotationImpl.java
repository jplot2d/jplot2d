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
public class HStripAnnotationImpl extends AnnotationImpl implements HStripAnnotationEx {

	private static Paint DEFAULT_PAINT = new Color(192, 192, 192, 128);

	private double locY;

	private double paperThickness;

	private Range range;

	private Paint paint = DEFAULT_PAINT;

	public String getId() {
		if (getParent() != null) {
			return "HStripAnnotation" + getParent().indexOf(this);
		} else {
			return "HStripAnnotation@" + Integer.toHexString(System.identityHashCode(this));
		}
	}

	public Point2D getLocation() {
		return new Point2D.Double(0, locY);
	}

	public void setLocation(double x, double y) {
		if (locY != y) {
			this.locY = y;
			double endY = locY + paperThickness;
			double valueY = getYPtoW(locY);
			double valueEnd = getYPtoW(endY);
			range = new Range.Double(valueY, valueEnd);
			if (isVisible()) {
				redraw();
			}
		}
	}

	public Dimension2D getSize() {
		if (getParent() == null || getParent().getSize() == null) {
			return null;
		}
		return new DoubleDimension2D(getParent().getSize().getWidth(), Math.abs(paperThickness));
	}

	public Rectangle2D getBounds() {
		if (getParent() == null || getParent().getSize() == null) {
			return null;
		}

		if (paperThickness >= 0) {
			return new Rectangle2D.Double(0, 0, getParent().getSize().getWidth(), paperThickness);
		} else {
			return new Rectangle2D.Double(0, paperThickness, getParent().getSize().getWidth(),
					-paperThickness);
		}
	}

	public Rectangle2D getSelectableBounds() {
		if (getParent() == null || getParent().getSize() == null) {
			return null;
		}

		if (-2 < paperThickness && paperThickness < 2) {
			return new Rectangle2D.Double(0, -1, getParent().getSize().getWidth(), 2);
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
		return pxf.translate(0, locY);
	}

	public Range getValueRange() {
		return range;
	}

	public void setValueRange(Range value) {
		this.range = value;
		if (getParent() != null && getParent().getYAxisTransform() != null) {
			relocate();
		}
	}

	public void relocate() {
		locY = getYWtoP(range.getStart());
		double endY = getYWtoP(range.getEnd());
		paperThickness = endY - locY;
		if (isVisible()) {
			redraw();
		}
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

		if (paperThickness == 0) {
			Line2D line = new Line2D.Double(0, locY, getParent().getSize().getWidth(), locY);
			Stroke oldStroke = g.getStroke();
			g.setStroke(ZERO_WIDTH_STROKE);
			g.draw(line);
			g.setStroke(oldStroke);
		} else {
			Rectangle2D strip;
			if (paperThickness > 0) {
				strip = new Rectangle2D.Double(0, locY, getParent().getSize().getWidth(),
						paperThickness);
			} else {
				strip = new Rectangle2D.Double(0, locY + paperThickness, getParent().getSize()
						.getWidth(), -paperThickness);
			}
			g.fill(strip);
		}

		g.setTransform(oldTransform);
		g.setClip(oldClip);
	}

	@Override
	public void copyFrom(ElementEx src) {
		super.copyFrom(src);

		HStripAnnotationImpl lm = (HStripAnnotationImpl) src;
		this.locY = lm.locY;
		this.paperThickness = lm.paperThickness;
		this.range = lm.range;
		this.paint = lm.paint;
	}

}
