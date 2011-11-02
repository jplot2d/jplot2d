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

import org.jplot2d.util.DoubleDimension2D;
import org.jplot2d.util.Range;

/**
 * @author Jingjing Li
 * 
 */
public class HStripMarkerImpl extends MarkerImpl implements HStripMarkerEx {

	private static Paint DEFAULT_PAINT = new Color(192, 192, 192, 128);

	private double locY;

	private double paperWidth;

	private Range range;

	private Paint paint = DEFAULT_PAINT;

	/**
	 * A local variable to avoid re-create strip when drawing
	 */
	private Rectangle2D strip = new Rectangle2D.Double();

	public String getId() {
		if (getParent() != null) {
			return "HStripMarker" + getParent().indexOf(this);
		} else {
			return "HStripMarker@" + Integer.toHexString(System.identityHashCode(this));
		}
	}

	public Point2D getLocation() {
		return new Point2D.Double(0, locY);
	}

	public void setLocation(double x, double y) {
		if (locY != y) {
			this.locY = y;
			double endY = locY + paperWidth;
			double valueY = getYPtoW(locY);
			double valueEnd = getYPtoW(endY);
			range = new Range.Double(valueY, valueEnd);
			if (isVisible()) {
				redraw();
			}
		}
	}

	public Dimension2D getSize() {
		if (getParent() == null && getParent().getSize() == null) {
			return null;
		}
		return new DoubleDimension2D(getParent().getSize().getWidth(), 0);
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
		paperWidth = endY - locY;
		if (isVisible()) {
			redraw();
		}
	}

	public Paint getPaint() {
		return paint;
	}

	public void setPaint(Paint paint) {
		this.paint = paint;
	}

	public void draw(Graphics2D g) {
		AffineTransform oldTransform = g.getTransform();
		Shape oldClip = g.getClip();

		g.transform(getParent().getPaperTransform().getTransform());
		g.setClip(getParent().getBounds());
		g.setPaint(paint);

		if (paperWidth == 0) {
			Line2D line = new Line2D.Double(0, locY, getParent().getSize().getWidth(), locY);
			Stroke oldStroke = g.getStroke();
			g.setStroke(ZERO_WIDTH_STROKE);
			g.draw(line);
			g.setStroke(oldStroke);
		} else {
			if (paperWidth > 0) {
				strip.setRect(0, locY, getParent().getSize().getWidth(), paperWidth);
			} else {
				strip.setRect(0, locY + paperWidth, getParent().getSize().getWidth(), -paperWidth);
			}
			g.fill(strip);
		}

		g.setTransform(oldTransform);
		g.setClip(oldClip);
	}

	@Override
	public void copyFrom(ElementEx src) {
		super.copyFrom(src);

		HStripMarkerImpl lm = (HStripMarkerImpl) src;
		this.locY = lm.locY;
		this.paperWidth = lm.paperWidth;
		this.range = lm.range;
		this.paint = lm.paint;
		this.strip = (Rectangle2D) lm.strip.clone();
	}

}
