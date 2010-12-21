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

import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Map;

import org.jplot2d.element.HAlign;
import org.jplot2d.element.VAlign;
import org.jplot2d.util.DoubleDimension2D;
import org.jplot2d.util.MathElement;
import org.jplot2d.util.MathLabel;
import org.jplot2d.util.TeXMathUtils;

/**
 * @author Jingjing Li
 * 
 */
public class TextComponentImpl extends ComponentImpl implements TextComponentEx {

	private MathElement textModel;

	private HAlign hAlign;

	private VAlign vAlign;

	private double angle;

	private MathLabel label;

	public TextComponentImpl() {
		hAlign = HAlign.CENTER;
		vAlign = VAlign.MIDDLE;
	}

	public void setFont(Font font) {
		super.setFont(font);
		label = null;
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

	public Dimension2D getSize() {
		Rectangle2D bounds = label.getBounds();
		return new DoubleDimension2D(bounds.getWidth(), bounds.getHeight());
	}

	public Rectangle2D getBounds() {
		if (label == null) {
			label = new MathLabel(getTextModel(), getEffectiveFont(),
					getVAlign(), getHAlign());
		}
		Rectangle2D bounds = label.getBounds();

		Point2D loc = getLocation();
		return new Rectangle2D.Double(loc.getX() + bounds.getX(), loc.getY()
				+ bounds.getY(), bounds.getWidth(), bounds.getHeight());
	}

	public TextComponentImpl deepCopy(Map<ElementEx, ElementEx> orig2copyMap) {
		return (TextComponentImpl) super.deepCopy(orig2copyMap);
	}

	public void copyFrom(ComponentEx src, Map<ElementEx, ElementEx> orig2copyMap) {
		super.copyFrom(src, orig2copyMap);

		TextComponentImpl tc = (TextComponentImpl) src;
		this.textModel = tc.textModel;
		this.hAlign = tc.hAlign;
		this.vAlign = tc.vAlign;
		this.angle = tc.angle;
		this.label = tc.label;
	}

	public void draw(Graphics2D g) {
		if (label == null) {
			label = new MathLabel(getTextModel(), getEffectiveFont(),
					getVAlign(), getHAlign());
		}

		AffineTransform oldTransform = g.getTransform();

		g.transform(getParent().getPhysicalTransform().getTransform());
		g.translate(getLocation().getX(), getLocation().getY());
		g.scale(1.0, -1.0);
		g.rotate(-Math.PI * angle / 180.0);

		g.setColor(getEffectiveColor());

		label.draw(g);

		g.setTransform(oldTransform);
	}

}
