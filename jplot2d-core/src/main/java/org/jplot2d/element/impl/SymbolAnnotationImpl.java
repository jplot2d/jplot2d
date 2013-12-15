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
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;

import org.jplot2d.tex.MathLabel;
import org.jplot2d.util.SymbolShape;

/**
 * @author Jingjing Li
 * 
 */
public class SymbolAnnotationImpl extends PointAnnotationImpl implements SymbolAnnotationEx {

	private double gap;

	public String getId() {
		if (getParent() != null) {
			return "SymbolAnnotation" + getParent().indexOf(this);
		} else {
			return "SymbolAnnotation@" + Integer.toHexString(System.identityHashCode(this));
		}
	}

	public SymbolShape getSymbolShape() {
		// TODO Auto-generated method stub
		return null;
	}

	public void setSymbolShape(SymbolShape symbol) {
		// TODO Auto-generated method stub

	}

	public double getSymbolSize() {
		// TODO Auto-generated method stub
		return 0;
	}

	public void setSymbolSize(double size) {
		// TODO Auto-generated method stub

	}

	public double getTextGap() {
		return gap;
	}

	public void setTextGap(double factor) {
		this.gap = factor;
	}

	public void draw(Graphics2D g) {
		Point2D loc = getLocation();
		if (loc == null) {
			return;
		}

		Shape oldClip = g.getClip();

		if (label == null) {
			label = new MathLabel(getTextModel(), getEffectiveFont(), getVAlign(), getHAlign());
		}

		AffineTransform oldTransform = g.getTransform();

		g.transform(getParent().getPaperTransform().getTransform());
		g.setClip(getParent().getBounds());

		g.translate(loc.getX(), loc.getY());
		g.scale(1.0, -1.0);
		g.rotate(-Math.PI * angle / 180.0);

		g.setColor(getEffectiveColor());

		label.draw(g);

		g.setTransform(oldTransform);
		g.setClip(oldClip);
	}

	@Override
	public void copyFrom(ElementEx src) {
		super.copyFrom(src);

		SymbolAnnotationImpl tc = (SymbolAnnotationImpl) src;
		this.gap = tc.gap;
	}

}
