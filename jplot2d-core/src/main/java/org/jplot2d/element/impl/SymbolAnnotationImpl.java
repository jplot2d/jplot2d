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

import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import org.jplot2d.element.HAlign;
import org.jplot2d.element.VAlign;
import org.jplot2d.tex.MathElement;
import org.jplot2d.tex.MathLabel;
import org.jplot2d.tex.TeXMathUtils;
import org.jplot2d.transform.PaperTransform;
import org.jplot2d.util.SymbolShape;

/**
 * @author Jingjing Li
 * 
 */
public class SymbolAnnotationImpl extends PointAnnotationImpl implements SymbolAnnotationEx {

	private SymbolShape symbolShape;

	private float symbolSize = Float.NaN;

	private float symbolScale = 1;

	private double angle;

	private float offsetX = 1.25f, offsetY = 0;

	private HAlign hAlign = HAlign.LEFT;

	private VAlign vAlign = VAlign.MIDDLE;

	protected MathElement textModel;

	private MathLabel label;

	public String getId() {
		if (getParent() != null) {
			return "SymbolAnnotation" + getParent().indexOf(this);
		} else {
			return "SymbolAnnotation@" + Integer.toHexString(System.identityHashCode(this));
		}
	}

	public PaperTransform getPaperTransform() {
		PaperTransform pxf = super.getPaperTransform();
		if (pxf == null || angle == 0) {
			return pxf;
		} else {
			return pxf.rotate(angle / 180 * Math.PI);
		}
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
		this.hAlign = tc.hAlign;
		this.vAlign = tc.vAlign;
		this.angle = tc.angle;
		this.textModel = tc.textModel;
		this.label = tc.label;
	}

	public SymbolShape getSymbolShape() {
		return symbolShape;
	}

	public void setSymbolShape(SymbolShape symbol) {
		this.symbolShape = symbol;
		redraw(this);
	}

	public float getSymbolSize() {
		return symbolSize;
	}

	public void setSymbolSize(float size) {
		this.symbolSize = size;
		redraw(this);
	}

	@Override
	public float getSymbolScale() {
		return symbolScale;
	}

	@Override
	public void setSymbolScale(float scale) {
		this.symbolScale = scale;
		redraw(this);
	}

	public float getTextOffsetFactorX() {
		return offsetX;
	}

	public void setTextOffsetFactorX(float offset) {
		this.offsetX = offset;
		redraw(this);
	}

	public float getTextOffsetFactorY() {
		return offsetY;
	}

	public void setTextOffsetFactorY(float offset) {
		this.offsetY = offset;
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
		return TeXMathUtils.toString(getTextModel());
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

	private float getEffectiveSymbolSize() {
		if (symbolShape == null) {
			return 0;
		} else if (!Float.isNaN(symbolSize)) {
			return symbolSize;
		} else {
			return getEffectiveFontSize() * symbolScale;
		}
	}

	public Rectangle2D getBounds() {
		if (label == null) {
			label = new MathLabel(getTextModel(), getEffectiveFont(), getVAlign(), getHAlign());
		}

		if (symbolShape == null) {
			return label.getBounds();
		}

		float ess = getEffectiveSymbolSize();
		Rectangle2D symbolBounds = new Rectangle2D.Double(-ess / 2, -ess / 2, ess, ess);
		if (getTextModel() == null) {
			return symbolBounds;
		}

		Rectangle2D lb = label.getBounds();
		float offx = offsetX * ess / 2;
		float offy = offsetY * ess / 2;
		Rectangle2D bounds = new Rectangle2D.Double(lb.getX() + offx, lb.getY() + offy, lb.getWidth(), lb.getHeight());
		bounds.add(symbolBounds);
		return bounds;
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
		g.rotate(Math.PI * angle / 180.0);
		float offx = offsetX * ess / 2;
		float offy = offsetY * ess / 2;
		g.translate(offx, offy);
		g.scale(1.0, -1.0);
		label.draw(g);

		g.setTransform(oldTransform);
		g.setClip(oldClip);
	}

}
