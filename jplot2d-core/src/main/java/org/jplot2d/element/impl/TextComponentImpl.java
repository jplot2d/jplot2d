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
import java.awt.geom.Dimension2D;
import java.awt.geom.Rectangle2D;

import org.jplot2d.element.HAlign;
import org.jplot2d.element.VAlign;
import org.jplot2d.util.MathElement;

/**
 * @author Jingjing Li
 * 
 */
public class TextComponentImpl extends ComponentImpl implements TextComponentEx {

	private static final Font DEFAULT_FONT = new Font("Serif", Font.PLAIN, 9);

	private MathLabel label = new MathLabel(DEFAULT_FONT);

	public void setVisible(boolean visible) {
		super.setVisible(visible);
		getParent().invalidate();
		redraw();
	}

	public void setFont(Font font) {
		super.setFont(font);
		getParent().invalidate();
		redraw();
	}

	public String getText() {
		// TODO Auto-generated method stub
		return null;
	}

	public void setText(String text) {
		// TODO Auto-generated method stub

	}

	public MathElement getTextModel() {
		return label.getModel();
	}

	public void setTextModel(MathElement model) {
		label.setModel(model);
		if (isVisible()) {
			getParent().invalidate();
			redraw();
		}
	}

	public HAlign getHAlign() {
		return label.getHAlign();
	}

	public void setHAlign(HAlign hAlign) {
		label.setHAlign(hAlign);
		if (isVisible()) {
			getParent().invalidate();
			redraw();
		}
	}

	public VAlign getVAlign() {
		return label.getVAlign();
	}

	public void setVAlign(VAlign vAlign) {
		label.setVAlign(vAlign);
		if (isVisible()) {
			getParent().invalidate();
			redraw();
		}
	}

	public double getAngle() {
		return label.getAngle();
	}

	public void setAngle(double angle) {
		label.setAngle(angle);
		if (isVisible()) {
			getParent().invalidate();
			redraw();
		}
	}

	public void draw(Graphics2D g) {
		label.setPhysicalTransform(getParent().getPhysicalTransform());
		label.draw(g);
	}

	public Dimension2D getSize() {
		// TODO Auto-generated method stub
		return null;
	}

	public Rectangle2D getBounds() {
		// TODO Auto-generated method stub
		return null;
	}

}
