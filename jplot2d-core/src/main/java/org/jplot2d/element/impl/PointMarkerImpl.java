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

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.lang.reflect.Method;

import org.jplot2d.element.Plot;
import org.jplot2d.util.SymbolShape;

/**
 * @author Jingjing Li
 * 
 */
public class PointMarkerImpl extends TextComponentImpl implements PointMarkerEx {

	private double gapFactor = 0.25;

	/**
	 * A cached bounds to meet the oldValue-calcSize-invalidate procedure in PlotImpl
	 */
	private Rectangle2D bounds = new Rectangle2D.Double();

	public String getId() {
		if (getParent() != null) {
			return "PointMarker" + getParent().indexOf(this);
		} else {
			return "PointMarker@" + Integer.toHexString(System.identityHashCode(this));
		}
	}

	public InvokeStep getInvokeStepFormParent() {
		if (parent == null) {
			return null;
		}

		Method method;
		try {
			method = Plot.class.getMethod("getMarker", Integer.TYPE);
		} catch (NoSuchMethodException e) {
			throw new Error(e);
		}
		return new InvokeStep(method, getParent().indexOf(this));
	}

	public LayerEx getParent() {
		return (LayerEx) super.getParent();
	}

	public double getGapFactor() {
		return gapFactor;
	}

	public void setGapFactor(double factor) {
		this.gapFactor = factor;
	}

	public Rectangle2D getBounds() {
		return bounds;
	}

	public void calcSize() {
		bounds = super.getBounds();
	}

	@Override
	public void copyFrom(ElementEx src) {
		super.copyFrom(src);

		PointMarkerImpl tc = (PointMarkerImpl) src;
		this.gapFactor = tc.gapFactor;
	}

	public SymbolShape getSymbolShape() {
		// TODO Auto-generated method stub
		return null;
	}

	public void getSymbolShape(SymbolShape symbol) {
		// TODO Auto-generated method stub

	}

	public double getSymbolSize() {
		// TODO Auto-generated method stub
		return 0;
	}

	public void getSymbolSize(double size) {
		// TODO Auto-generated method stub

	}

	public Point2D getValuePoint() {
		// TODO Auto-generated method stub
		return null;
	}

	public void setValuePoint(Point2D point) {
		// TODO Auto-generated method stub

	}

}
