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
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.awt.geom.Dimension2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;

import org.jplot2d.util.DoubleDimension2D;

/**
 * @author Jingjing Li
 * 
 */
public class VLineMarkerImpl extends MarkerImpl implements VLineMarkerEx {

	private double locX;

	private double valueX;

	private Stroke stroke = DEFAULT_STROKE;

	/**
	 * A local variable to avoid re-create line when drawing
	 */
	private Line2D line = new Line2D.Double();

	public String getId() {
		if (getParent() != null) {
			return "VLineMarker" + getParent().indexOf(this);
		} else {
			return "VLineMarker@" + Integer.toHexString(System.identityHashCode(this));
		}
	}

	public Point2D getLocation() {
		return new Point2D.Double(locX, 0);
	}

	public void setLocation(double x, double y) {
		if (locX != x) {
			this.locX = x;
			valueX = getXPtoW(locX);
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

	public double getValue() {
		return valueX;
	}

	public void setValue(double value) {
		this.valueX = value;
		if (getParent() != null && getParent().getXAxisTransform() != null) {
			relocate();
		}
	}

	public void relocate() {
		locX = getXWtoP(valueX);
		if (isVisible()) {
			redraw();
		}
	}

	public Stroke getStroke() {
		return stroke;
	}

	public void setStroke(Stroke stroke) {
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

		line.setLine(locX, 0, locX, getParent().getSize().getHeight());
		g.draw(line);

		g.setTransform(oldTransform);
		g.setClip(oldClip);
		g.setStroke(oldStroke);
	}

	@Override
	public void copyFrom(ElementEx src) {
		super.copyFrom(src);

		VLineMarkerImpl lm = (VLineMarkerImpl) src;
		this.locX = lm.locX;
		this.valueX = lm.valueX;
		this.stroke = lm.stroke;
		this.line = (Line2D) lm.line.clone();
	}

}
