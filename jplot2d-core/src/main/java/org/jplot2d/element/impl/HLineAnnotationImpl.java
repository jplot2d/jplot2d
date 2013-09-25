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

import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.awt.geom.Dimension2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import org.jplot2d.transform.PaperTransform;
import org.jplot2d.util.DoubleDimension2D;

/**
 * @author Jingjing Li
 * 
 */
public class HLineAnnotationImpl extends AnnotationImpl implements HLineAnnotationEx {

	private double locY = Double.NaN;

	private double valueY;

	private BasicStroke stroke = DEFAULT_STROKE;

	public String getId() {
		if (getParent() != null) {
			return "HLineAnnotation" + getParent().indexOf(this);
		} else {
			return "HLineAnnotation@" + Integer.toHexString(System.identityHashCode(this));
		}
	}

	public Point2D getLocation() {
		return new Point2D.Double(0, locY);
	}

	public void setLocation(double x, double y) {
		if (locY != y) {
			this.locY = y;
			valueY = getYPtoW(locY);
			redraw(this);
		}
	}

	public Dimension2D getSize() {
		if (getParent() == null || getParent().getSize() == null) {
			return null;
		}
		return new DoubleDimension2D(getParent().getSize().getWidth(), 0);
	}

	public Rectangle2D getSelectableBounds() {
		if (getParent() == null || getParent().getSize() == null) {
			return null;
		}

		double lineWidth = 0;
		if (stroke instanceof BasicStroke) {
			lineWidth = ((BasicStroke) stroke).getLineWidth();
		}
		if (lineWidth < 2) {
			lineWidth = 2;
		}
		return new Rectangle2D.Double(0, -lineWidth / 2, getParent().getSize().getWidth(), lineWidth);
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

	public double getValue() {
		return valueY;
	}

	public void setValue(double value) {
		this.valueY = value;
		if (getParent() != null && getParent().getYAxisTransform() != null) {
			relocate();
		}
	}

	public void relocate() {
		locY = getYWtoP(valueY);
		redraw(this);
	}

	public BasicStroke getStroke() {
		return stroke;
	}

	public void setStroke(BasicStroke stroke) {
		this.stroke = stroke;
	}

	public void draw(Graphics2D g) {
		Stroke oldStroke = g.getStroke();
		AffineTransform oldTransform = g.getTransform();
		Shape oldClip = g.getClip();

		g.transform(getParent().getPaperTransform().getTransform());
		g.setClip(getParent().getBounds());
		g.setColor(getEffectiveColor());
		g.setStroke(stroke);

		// calculate locY
		if (Double.isNaN(locY)) {
			locY = getYWtoP(valueY);
		}

		Line2D line = new Line2D.Double(0, locY, getParent().getSize().getWidth(), locY);
		g.draw(line);

		g.setTransform(oldTransform);
		g.setClip(oldClip);
		g.setStroke(oldStroke);
	}

	@Override
	public void copyFrom(ElementEx src) {
		super.copyFrom(src);

		HLineAnnotationImpl lm = (HLineAnnotationImpl) src;
		this.locY = lm.locY;
		this.valueY = lm.valueY;
		this.stroke = lm.stroke;
	}

}
