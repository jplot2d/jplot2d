/**
 * Copyright 2010, 2014 Jingjing Li.
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
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;

import org.jplot2d.tex.MathLabel;
import org.jplot2d.util.SymbolShape;

/**
 * @author Jingjing Li
 * 
 */
public class SymbolAnnotationImpl extends PointAnnotationImpl implements SymbolAnnotationEx {

	private SymbolShape symbolShape;

	private float symbolSize = Float.NaN;

	private float symbolScale = 1;

	private float offsetX = 1, offsetY = 0;

	public String getId() {
		if (getParent() != null) {
			return "SymbolAnnotation" + getParent().indexOf(this);
		} else {
			return "SymbolAnnotation@" + Integer.toHexString(System.identityHashCode(this));
		}
	}

	public SymbolShape getSymbolShape() {
		return symbolShape;
	}

	public void setSymbolShape(SymbolShape symbol) {
		this.symbolShape = symbol;
	}

	public float getSymbolSize() {
		return symbolSize;
	}

	public void setSymbolSize(float size) {
		this.symbolSize = size;
	}

	@Override
	public float getSymbolScale() {
		return symbolScale;
	}

	@Override
	public void setSymbolScale(float scale) {
		this.symbolScale = scale;
	}

	public float getTextOffsetX() {
		return offsetX;
	}

	public void setTextOffsetX(float offset) {
		this.offsetX = offset;
	}

	public float getTextOffsetY() {
		return offsetY;
	}

	public void setTextOffsetY(float offset) {
		this.offsetY = offset;
	}

	private float getEffectiveSymbolSize() {
		if (symbolShape == null) {
			return 0;
		} else if (!Float.isNaN(symbolSize)) {
			return symbolSize;
		} else {
			return getEffectiveFontSize() * symbolScale;
		}
	}

	public void draw(Graphics2D g) {
		Point2D loc = getLocation();
		if (loc == null) {
			return;
		}

		Shape oldClip = g.getClip();
		AffineTransform oldTransform = g.getTransform();

		g.transform(getParent().getPaperTransform().getTransform());
		g.setClip(getParent().getBounds());
		g.translate(loc.getX(), loc.getY());
		g.setColor(getEffectiveColor());

		// draw symbol
		float ess = getEffectiveSymbolSize();
		if (symbolShape != null) {
			AffineTransform maf = AffineTransform.getScaleInstance(ess, ess);
			symbolShape.draw(g, maf);
		}

		// draw label
		if (label == null) {
			label = new MathLabel(getTextModel(), getEffectiveFont(), getVAlign(), getHAlign());
		}
		g.scale(1.0, -1.0);
		g.rotate(-Math.PI * angle / 180.0);
		float offx = offsetX * getEffectiveSymbolSize();
		float offy = offsetY * getEffectiveSymbolSize();
		g.translate(offx, offy);
		label.draw(g);

		g.setTransform(oldTransform);
		g.setClip(oldClip);
	}

	@Override
	public void copyFrom(ElementEx src) {
		super.copyFrom(src);

		SymbolAnnotationImpl tc = (SymbolAnnotationImpl) src;
		this.symbolShape = tc.symbolShape;
		this.symbolSize = tc.symbolSize;
		this.symbolScale = tc.symbolScale;
		this.offsetX = tc.offsetX;
		this.offsetY = tc.offsetY;
	}

}
