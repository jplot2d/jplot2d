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

import java.lang.reflect.Method;
import java.util.Map;

import org.jplot2d.element.Plot;

/**
 * @author Jingjing Li
 * 
 */
public class PlotMarginImpl extends ElementImpl implements PlotMarginEx, Cloneable {

	private boolean autoMarginTop = true, autoMarginLeft = true, autoMarginBottom = true, autoMarginRight = true;

	private double marginTop, marginLeft, marginBottom, marginRight;

	private double extraTop, extraLeft, extraBottom, extraRight;

	public PlotEx getParent() {
		return (PlotEx) parent;
	}

	public String getId() {
		if (getParent() != null) {
			return "Margin";
		} else {
			return "Margin@" + Integer.toHexString(System.identityHashCode(this));
		}
	}

	public InvokeStep getInvokeStepFormParent() {
		if (parent == null) {
			return null;
		}

		Method method;
		try {
			method = Plot.class.getMethod("getMargin");
		} catch (NoSuchMethodException e) {
			throw new Error(e);
		}
		return new InvokeStep(method);
	}

	public boolean isAutoTop() {
		return autoMarginTop;
	}

	public boolean isAutoLeft() {
		return autoMarginLeft;
	}

	public boolean isAutoBottom() {
		return autoMarginBottom;
	}

	public boolean isAutoRight() {
		return autoMarginRight;
	}

	public void setAutoTop(boolean auto) {
		autoMarginTop = auto;
		getParent().invalidate();
	}

	public void setAutoLeft(boolean auto) {
		autoMarginLeft = auto;
		getParent().invalidate();
	}

	public void setAutoBottom(boolean auto) {
		autoMarginBottom = auto;
		getParent().invalidate();
	}

	public void setAutoRight(boolean auto) {
		autoMarginRight = auto;
		getParent().invalidate();
	}

	public double getTop() {
		return marginTop;
	}

	public double getLeft() {
		return marginLeft;
	}

	public double getBottom() {
		return marginBottom;
	}

	public double getRight() {
		return marginRight;
	}

	public void setTop(double marginTop) {
		this.marginTop = marginTop;
		this.autoMarginTop = false;
		getParent().invalidate();
	}

	public void setLeft(double marginLeft) {
		this.marginLeft = marginLeft;
		this.autoMarginLeft = false;
		getParent().invalidate();
	}

	public void setBottom(double marginBottom) {
		this.marginBottom = marginBottom;
		this.autoMarginBottom = false;
		getParent().invalidate();
	}

	public void setRight(double marginRight) {
		this.marginRight = marginRight;
		this.autoMarginRight = false;
		getParent().invalidate();
	}

	public void directTop(double marginTop) {
		this.marginTop = marginTop;
	}

	public void directLeft(double marginLeft) {
		this.marginLeft = marginLeft;
	}

	public void directBottom(double marginBottom) {
		this.marginBottom = marginBottom;
	}

	public void directRight(double marginRight) {
		this.marginRight = marginRight;
	}

	public double getExtraTop() {
		return extraTop;
	}

	public double getExtraLeft() {
		return extraLeft;
	}

	public double getExtraBottom() {
		return extraBottom;
	}

	public double getExtraRight() {
		return extraRight;
	}

	public void setExtraTop(double marginTop) {
		this.extraTop = marginTop;
		getParent().invalidate();
	}

	public void setExtraLeft(double marginLeft) {
		this.extraLeft = marginLeft;
		getParent().invalidate();
	}

	public void setExtraBottom(double marginBottom) {
		this.extraBottom = marginBottom;
		getParent().invalidate();
	}

	public void setExtraRight(double marginRight) {
		this.extraRight = marginRight;
		getParent().invalidate();
	}

	@Override
	public PlotMarginImpl copyStructure(Map<ElementEx, ElementEx> orig2copyMap) {
		PlotMarginImpl result = null;
		try {
			result = (PlotMarginImpl) this.clone();
		} catch (CloneNotSupportedException e) {
		}

		if (orig2copyMap != null) {
			orig2copyMap.put(this, result);
		}
		return result;
	}

}
