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

import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import org.jplot2d.element.HAlign;
import org.jplot2d.element.VAlign;
import org.jplot2d.tex.MathElement;
import org.jplot2d.tex.MathLabel;
import org.jplot2d.tex.TeXMathUtils;
import org.jplot2d.transform.PaperTransform;
import org.jplot2d.util.DoubleDimension2D;

/**
 * @author Jingjing Li
 * 
 */
public abstract class PointAnnotationImpl extends AnnotationImpl implements PointAnnotationEx {

	private double valueX, valueY;

	private HAlign hAlign = HAlign.LEFT;

	private VAlign vAlign = VAlign.MIDDLE;

	protected double angle;

	private MathElement textModel;

	protected MathLabel label;

	public String getId() {
		if (getParent() != null) {
			return "PointAnnotation" + getParent().indexOf(this);
		} else {
			return "PointAnnotation@" + Integer.toHexString(System.identityHashCode(this));
		}
	}

	public Point2D getLocation() {
		if (getParent() == null || getParent().getSize() == null || getParent().getXAxisTransform() == null
				|| getParent().getYAxisTransform() == null) {
			return null;
		} else {
			double locX = getXWtoP(valueX);
			double locY = getYWtoP(valueY);
			return new Point2D.Double(locX, locY);
		}
	}

	public void setLocation(double locX, double locY) {
		Point2D loc = getLocation();
		if (loc != null && (loc.getX() != locX || loc.getY() != locY)) {
			valueX = getXPtoW(locX);
			valueY = getYPtoW(locY);
			redraw(this);
		}
	}

	public Dimension2D getSize() {
		Rectangle2D bounds = getBounds();
		return new DoubleDimension2D(bounds.getWidth(), bounds.getHeight());
	}

	public PaperTransform getPaperTransform() {
		Point2D loc = getLocation();
		if (getParent() == null || loc == null) {
			return null;
		} else {
			PaperTransform pxf = getParent().getPaperTransform().translate(loc.getX(), loc.getY());
			if (angle != 0) {
				pxf = pxf.rotate(angle / 180 * Math.PI);
			}
			return pxf;
		}
	}

	@Override
	public void copyFrom(ElementEx src) {
		super.copyFrom(src);

		PointAnnotationImpl tc = (PointAnnotationImpl) src;
		this.valueX = tc.valueX;
		this.valueY = tc.valueY;
		this.hAlign = tc.hAlign;
		this.vAlign = tc.vAlign;
		this.angle = tc.angle;
		this.textModel = tc.textModel;
		this.label = tc.label;
	}

	public Point2D getValuePoint() {
		return new Point2D.Double(valueX, valueY);
	}

	public void setValuePoint(Point2D point) {
		setValuePoint(point.getX(), point.getY());
	}

	public void setValuePoint(double x, double y) {
		this.valueX = x;
		this.valueY = y;
		redraw(this);
	}

	public HAlign getHAlign() {
		return hAlign;
	}

	public void setHAlign(HAlign hAlign) {
		this.hAlign = hAlign;
		label = null;
		redraw(this);
	}

	public VAlign getVAlign() {
		return vAlign;
	}

	public void setVAlign(VAlign vAlign) {
		this.vAlign = vAlign;
		label = null;
		redraw(this);
	}

	public double getAngle() {
		return angle;
	}

	public void setAngle(double angle) {
		this.angle = angle;
		redraw(this);
	}

	public String getText() {
		return TeXMathUtils.toString(textModel);
	}

	public void setText(String text) {
		setTextModel(TeXMathUtils.parseText(text));
	}

	public MathElement getTextModel() {
		return textModel;
	}

	public void setTextModel(MathElement model) {
		this.textModel = model;
		label = null;
		redraw(this);
	}

}
