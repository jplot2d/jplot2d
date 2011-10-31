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

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import org.jplot2d.element.HAlign;
import org.jplot2d.element.VAlign;
import org.jplot2d.tex.MathElement;
import org.jplot2d.tex.MathLabel;
import org.jplot2d.tex.TeXMathUtils;
import org.jplot2d.util.DoubleDimension2D;

/**
 * @author Jingjing Li
 * 
 */
public class PointMarkerImpl extends MarkerImpl implements PointMarkerEx {

	private Point2D valuePoint;

	protected HAlign hAlign;

	protected VAlign vAlign;

	protected double angle;

	private MathElement textModel;

	private MathLabel label;

	public String getId() {
		if (getParent() != null) {
			return "PointMarker" + getParent().indexOf(this);
		} else {
			return "PointMarker@" + Integer.toHexString(System.identityHashCode(this));
		}
	}

	public Dimension2D getSize() {
		Rectangle2D bounds = getBounds();
		return new DoubleDimension2D(bounds.getWidth(), bounds.getHeight());
	}

	public Rectangle2D getBounds() {
		if (label == null) {
			label = new MathLabel(getTextModel(), getEffectiveFont(), getVAlign(), getHAlign());
		}
		return label.getBounds();
	}

	@Override
	public void copyFrom(ElementEx src) {
		super.copyFrom(src);

		PointMarkerImpl tc = (PointMarkerImpl) src;
		this.valuePoint = tc.valuePoint;
		this.hAlign = tc.hAlign;
		this.vAlign = tc.vAlign;
		this.angle = tc.angle;
		this.textModel = tc.textModel;
		this.label = tc.label;
	}

	public Point2D getValuePoint() {
		return valuePoint;
	}

	public void setValuePoint(Point2D point) {
		this.valuePoint = point;
		relocate();
	}

	public HAlign getHAlign() {
		return hAlign;
	}

	public void setHAlign(HAlign hAlign) {
		this.hAlign = hAlign;
		label = null;
		if (isVisible()) {
			redraw();
		}
	}

	public VAlign getVAlign() {
		return vAlign;
	}

	public void setVAlign(VAlign vAlign) {
		this.vAlign = vAlign;
		label = null;
		if (isVisible()) {
			redraw();
		}
	}

	public double getAngle() {
		return angle;
	}

	public void setAngle(double angle) {
		this.angle = angle;
		if (isVisible()) {
			redraw();
		}
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
		if (isVisible()) {
			redraw();
		}
	}

	public void draw(Graphics2D g) {
		if (label == null) {
			label = new MathLabel(getTextModel(), getEffectiveFont(), getVAlign(), getHAlign());
		}

		AffineTransform oldTransform = g.getTransform();

		g.transform(getParent().getPaperTransform().getTransform());
		g.translate(getLocation().getX(), getLocation().getY());
		g.scale(1.0, -1.0);
		g.rotate(-Math.PI * angle / 180.0);

		g.setColor(getEffectiveColor());

		label.draw(g);

		g.setTransform(oldTransform);
	}

	/**
	 * Recalculate the paper location by the user location
	 */
	public void relocate() {
		locX = getParent().getXAxisTransform().getNormalTransform().getTransP(valuePoint.getX());
		locY = getParent().getXAxisTransform().getNormalTransform().getTransP(valuePoint.getX());
		if (isVisible()) {
			redraw();
		}
	}

}
