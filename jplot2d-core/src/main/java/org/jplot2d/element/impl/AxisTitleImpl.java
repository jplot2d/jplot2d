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

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Dimension2D;
import java.awt.geom.Rectangle2D;

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
public class AxisTitleImpl extends ElementImpl implements AxisTitleEx {

	private boolean visible = true;

	private Color color = null;

	private String fontName;

	private int fontStyle = -1;

	private float fontSize = Float.NaN;

	private float fontScale = 1.5f;

	private VAlign vAlign;

	private MathElement textModel;

	private MathLabel label;

	public AxisTitleImpl() {

	}

	public AxisEx getParent() {
		return (AxisEx) parent;
	}

	public boolean isVisible() {
		return visible;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
		invalidateThickness();
		redraw();
	}

	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		Color oldColor = getEffectiveColor();
		this.color = color;
		if (isVisible() && !getEffectiveColor().equals(oldColor)) {
			redraw();
		}
	}

	private Color getEffectiveColor() {
		if (color != null) {
			return color;
		} else if (getParent() != null) {
			return getParent().getEffectiveColor();
		} else {
			return null;
		}
	}

	public String getFontName() {
		return fontName;
	}

	public void setFontName(String name) {
		String oldFontName = getEffectiveFontName();
		fontName = name;
		if (!getEffectiveFontName().equals(oldFontName)) {
			updateFont();
		}
	}

	public int getFontStyle() {
		return fontStyle;
	}

	public void setFontStyle(int style) {
		int oldStyle = getEffectiveFontStyle();
		fontStyle = style;
		if (getEffectiveFontStyle() != oldStyle) {
			updateFont();
		}
	}

	public float getFontSize() {
		return fontSize;
	}

	public void setFontSize(float size) {
		float oldSize = getEffectiveFontSize();
		fontSize = size;
		if (getEffectiveFontSize() != oldSize) {
			updateFont();
		}
	}

	public float getFontScale() {
		return fontScale;
	}

	public void setFontScale(float scale) {
		float oldSize = getEffectiveFontSize();
		fontScale = scale;
		if (getEffectiveFontSize() != oldSize) {
			updateFont();
		}
	}

	public void setFont(Font font) {
		Font oldFont = getEffectiveFont();
		if (font == null) {
			fontName = null;
			fontStyle = -1;
			fontSize = Float.NaN;
		} else {
			fontName = font.getName();
			fontStyle = font.getStyle();
			fontSize = font.getSize2D();
		}
		if (!getEffectiveFont().equals(oldFont)) {
			updateFont();
		}
	}

	private void updateFont() {
		label = null;
		if (isVisible()) {
			invalidateThickness();
			redraw();
		}
	}

	private String getEffectiveFontName() {
		if (fontName != null) {
			return fontName;
		} else if (getParent() != null) {
			return getParent().getEffectiveFontName();
		} else {
			return null;
		}
	}

	private int getEffectiveFontStyle() {
		if ((fontStyle & ~0x03) == 0) {
			return fontStyle;
		} else if (getParent() != null) {
			return getParent().getEffectiveFontStyle();
		} else {
			return -1;
		}
	}

	private float getEffectiveFontSize() {
		if (!Float.isNaN(fontSize)) {
			return fontSize;
		} else if (getParent() != null) {
			return getParent().getEffectiveFontSize() * fontScale;
		} else {
			return Float.NaN;
		}
	}

	private Font getEffectiveFont() {
		float size = getEffectiveFontSize();
		return new Font(getEffectiveFontName(), getEffectiveFontStyle(),
				(int) size).deriveFont(size);
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
			invalidateThickness();
			redraw();
		}
	}

	private void invalidateThickness() {
		if (getParent() != null) {
			getParent().invalidateThickness();
		}
	}

	private void redraw() {
		if (getParent() != null) {
			getParent().redraw();
		}
	}

	public void setVAlign(VAlign vAlign) {
		if (this.vAlign != vAlign) {
			this.vAlign = vAlign;
			label = null;
			if (isVisible()) {
				redraw();
			}
		}
	}

	public Dimension2D getSize() {
		if (label == null) {
			label = new MathLabel(getTextModel(), getEffectiveFont(), vAlign,
					HAlign.CENTER);
		}
		Rectangle2D bounds = label.getBounds();

		return new DoubleDimension2D(bounds.getWidth(), bounds.getHeight());
	}

	public void draw(Graphics2D g, double x, double y) {
		if (label == null) {
			label = new MathLabel(getTextModel(), getEffectiveFont(), vAlign,
					HAlign.CENTER);
		}

		AffineTransform oldTransform = g.getTransform();

		g.transform(getParent().getPhysicalTransform().getTransform());
		g.translate(x, y);
		g.scale(1.0, -1.0);

		g.setColor(getEffectiveColor());

		label.draw(g);

		g.setTransform(oldTransform);
	}

	@Override
	public void copyFrom(ElementEx src) {
		super.copyFrom(src);

		AxisTitleImpl tc = (AxisTitleImpl) src;
		visible = tc.visible;
		color = tc.color;
		fontName = tc.fontName;
		fontStyle = tc.fontStyle;
		fontSize = tc.fontSize;
		fontScale = tc.fontScale;
		vAlign = tc.vAlign;
		textModel = tc.textModel;
		label = tc.label;
	}

}
