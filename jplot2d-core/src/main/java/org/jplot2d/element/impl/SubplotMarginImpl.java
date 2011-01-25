/**
 * Copyright 2010 Jingjing Li.
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

import java.util.Map;

/**
 * @author Jingjing Li
 * 
 */
public class SubplotMarginImpl extends ElementImpl implements SubplotMarginEx,
		Cloneable {

	private boolean autoMarginTop = true, autoMarginLeft = true,
			autoMarginBottom = true, autoMarginRight = true;

	private double marginTop, marginLeft, marginBottom, marginRight;

	private double extraTop, extraLeft, extraBottom, extraRight;

	public SubplotEx getParent() {
		return (SubplotEx) parent;
	}

	public boolean isAutoMarginTop() {
		return autoMarginTop;
	}

	public boolean isAutoMarginLeft() {
		return autoMarginLeft;
	}

	public boolean isAutoMarginBottom() {
		return autoMarginBottom;
	}

	public boolean isAutoMarginRight() {
		return autoMarginRight;
	}

	public void setAutoMarginTop(boolean auto) {
		autoMarginTop = auto;
	}

	public void setAutoMarginLeft(boolean auto) {
		autoMarginLeft = auto;
	}

	public void setAutoMarginBottom(boolean auto) {
		autoMarginBottom = auto;
	}

	public void setAutoMarginRight(boolean auto) {
		autoMarginRight = auto;
	}

	public double getMarginTop() {
		return marginTop;
	}

	public double getMarginLeft() {
		return marginLeft;
	}

	public double getMarginBottom() {
		return marginBottom;
	}

	public double getMarginRight() {
		return marginRight;
	}

	public void setMarginTop(double marginTop) {
		this.marginTop = marginTop;
	}

	public void setMarginLeft(double marginLeft) {
		this.marginLeft = marginLeft;
	}

	public void setMarginBottom(double marginBottom) {
		this.marginBottom = marginBottom;
	}

	public void setMarginRight(double marginRight) {
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
	}

	public void setExtraLeft(double marginLeft) {
		this.extraLeft = marginLeft;
	}

	public void setExtraBottom(double marginBottom) {
		this.extraBottom = marginBottom;
	}

	public void setExtraRight(double marginRight) {
		this.extraRight = marginRight;
	}

	@Override
	public SubplotMarginImpl copyStructure(Map<ElementEx, ElementEx> orig2copyMap) {
		SubplotMarginImpl result = null;
		try {
			result = (SubplotMarginImpl) this.clone();
		} catch (CloneNotSupportedException e) {
		}

		if (orig2copyMap != null) {
			orig2copyMap.put(this, result);
		}
		return result;
	}

}
