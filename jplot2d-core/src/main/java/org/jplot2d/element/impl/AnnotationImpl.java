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

import java.awt.BasicStroke;
import java.awt.geom.Point2D;
import java.lang.reflect.Method;

import org.jplot2d.element.Layer;

public abstract class AnnotationImpl extends ComponentImpl implements AnnotationEx {

	protected final static BasicStroke DEFAULT_STROKE = new BasicStroke();

	protected final static BasicStroke ZERO_WIDTH_STROKE = new BasicStroke(0);

	public AnnotationImpl() {
		setSelectable(true);
		setMovable(true);
	}

	public InvokeStep getInvokeStepFormParent() {
		if (parent == null) {
			return null;
		}

		Method method;
		try {
			method = Layer.class.getMethod("getMarker", Integer.TYPE);
		} catch (NoSuchMethodException e) {
			throw new Error(e);
		}
		return new InvokeStep(method, getParent().indexOf(this));
	}

	public LayerEx getParent() {
		return (LayerEx) super.getParent();
	}

	public void thisEffectiveColorChanged() {
		if (isVisible()) {
			redraw();
		}
	}

	public void thisEffectiveFontChanged() {
		if (isVisible()) {
			redraw();
		}
	}

	public final void setLocation(Point2D p) {
		setLocation(p.getX(), p.getY());
	}

	protected double getXWtoP(double v) {
		LayerEx layer = getParent();
		return layer.getXAxisTransform().getNormalTransform().convToNR(v)
				* layer.getSize().getWidth();
	}

	protected double getYWtoP(double v) {
		LayerEx layer = getParent();
		return layer.getYAxisTransform().getNormalTransform().convToNR(v)
				* layer.getSize().getHeight();
	}

	protected double getXPtoW(double v) {
		LayerEx layer = getParent();
		return layer.getXAxisTransform().getNormalTransform()
				.convFromNR(v / layer.getSize().getWidth());
	}

	protected double getYPtoW(double v) {
		LayerEx layer = getParent();
		return layer.getYAxisTransform().getNormalTransform()
				.convFromNR(v / layer.getSize().getHeight());
	}

}
