/**
 * Copyright 2010-2014 Jingjing Li.
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

import org.jplot2d.util.DoubleDimension2D;

/**
 * @author Jingjing Li
 * 
 */
public abstract class PointAnnotationImpl extends AnnotationImpl implements PointAnnotationEx {

	private double valueX, valueY;

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
			setValuePoint(getXPtoW(locX), getYPtoW(locY));
		}
	}

	public Dimension2D getSize() {
		Rectangle2D bounds = getBounds();
		return new DoubleDimension2D(bounds.getWidth(), bounds.getHeight());
	}

	@Override
	public void copyFrom(ElementEx src) {
		super.copyFrom(src);

		PointAnnotationImpl tc = (PointAnnotationImpl) src;
		this.valueX = tc.valueX;
		this.valueY = tc.valueY;
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

}
