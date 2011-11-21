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
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Dimension2D;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Iterator;

import org.jplot2d.element.HAlign;
import org.jplot2d.element.VAlign;
import org.jplot2d.tex.MathElement;
import org.jplot2d.tex.MathLabel;
import org.jplot2d.tex.TeXMathUtils;
import org.jplot2d.util.DoubleDimension2D;
import org.jplot2d.util.SymbolShape;

/**
 * @author Jingjing Li
 * 
 */
public class XYLegendItemImpl extends LegendItemImpl implements XYLegendItemEx {

	private static final double LABEL_SPACE = 8;

	private static final double LINE_LENGTH = 24;

	private static final double SYMBOL_SIZE = 8;

	private MathElement textModel;

	private transient MathLabel label;

	public XYLegendItemImpl() {

	}

	public XYGraphPlotterEx getParent() {
		return (XYGraphPlotterEx) super.getParent();
	}

	public void setLegend(LegendEx legend) {
		Font oldFont = (getLegend() == null) ? null : getLegend().getEffectiveFont();
		super.setLegend(legend);
		if (legend != null && !legend.getEffectiveFont().equals(oldFont)) {
			label = null;
			if (isVisible()) {
				if (getLegend() != null) {
					getLegend().itemSizeChanged(this);
				}
			}
		}
	}

	public void legendEffectiveFontChanged() {
		label = null;
		if (isVisible()) {
			if (getLegend() != null) {
				getLegend().itemSizeChanged(this);
			}
		}
	}

	private MathLabel getLabel() {
		if (label == null && getLegend() != null && getLegend().getEffectiveFont().getSize2D() > 0) {
			label = new MathLabel(getTextModel(), getLegend().getEffectiveFont(), VAlign.MIDDLE,
					HAlign.LEFT);
		}
		return label;
	}

	public Dimension2D getSize() {
		if (getLabel() == null) {
			return null;
		}
		Rectangle2D labelBounds = getLabel().getBounds();
		double width = labelBounds.getWidth();
		double height = labelBounds.getHeight();
		if (height < SYMBOL_SIZE) {
			height = SYMBOL_SIZE;
		}
		return new DoubleDimension2D(LINE_LENGTH + LABEL_SPACE + width, height);
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
			if (getLegend() != null) {
				getLegend().itemSizeChanged(this);
			}
		}
	}

	public void draw(Graphics2D g) {
		Point2D loc = getLocation();

		/* draw line and mark */
		XYGraphPlotterEx plotter = getParent();

		float ax = (float) loc.getX();
		float bx = ax + (float) (LINE_LENGTH);
		float ay = (float) loc.getY();
		float by = ay;

		Color sc = plotter.getEffectiveSymbolColor();
		if (plotter.isLineVisible()) {
			g.setColor(plotter.getEffectiveColor());
			g.setStroke(plotter.getLineStroke());
			Path2D.Float gp = new Path2D.Float();
			gp.moveTo(ax, ay);
			gp.lineTo(bx, by);
			g.draw(gp);
			if (plotter.isSymbolVisible()) {
				drawSymbol(g, ax, ay, sc);
				drawSymbol(g, bx, by, sc);
			}
		} else if (plotter.isSymbolVisible()) {
			drawSymbol(g, bx, by, sc);
		}

		/* draw label */
		drawLabel(g);
	}

	private void drawSymbol(Graphics2D g, float x, float y, Color color) {
		XYGraphPlotterEx plotter = getParent();

		if (plotter.getSymbolShape() == SymbolShape.DOT) {
			// use 0 width stroke to draw dot marks
			BasicStroke markStroke = new BasicStroke(0);
			g.setStroke(markStroke);
			g.setColor(color);
			Shape dot = new Line2D.Float(x, y, x, y);
			g.draw(dot);
		} else {
			// use half of line stroke to draw marks
			float lw = plotter.getLineStroke().getLineWidth() / 2;
			g.setStroke(new BasicStroke(lw, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER));

			SymbolShape ss = plotter.getSymbolShape();
			g.setColor(color);

			AffineTransform maf = AffineTransform.getTranslateInstance(x, y);
			maf.scale(SYMBOL_SIZE, SYMBOL_SIZE);

			Iterator<Shape> dit = ss.getDrawShapeIterator();
			while (dit.hasNext()) {
				Shape s = maf.createTransformedShape(dit.next());
				g.draw(s);
			}

			Iterator<Shape> fit = ss.getFillShapeIterator();
			while (fit.hasNext()) {
				Shape s = maf.createTransformedShape(fit.next());
				g.fill(s);
			}
		}

	}

	private void drawLabel(Graphics2D g) {
		AffineTransform oldTransform = g.getTransform();

		g.translate(getLocation().getX() + LINE_LENGTH + LABEL_SPACE, getLocation().getY());
		g.scale(1.0, -1.0);
		g.setColor(Color.BLACK);
		getLabel().draw(g);

		g.setTransform(oldTransform);
	}

	@Override
	public void copyFrom(ElementEx src) {
		super.copyFrom(src);

		XYLegendItemImpl xyli = (XYLegendItemImpl) src;
		textModel = xyli.textModel;
	}

}
