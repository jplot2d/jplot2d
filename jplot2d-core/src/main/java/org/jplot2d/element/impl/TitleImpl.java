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
import java.awt.geom.AffineTransform;
import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.lang.reflect.Method;

import org.jplot2d.element.HAlign;
import org.jplot2d.element.Plot;
import org.jplot2d.element.VAlign;
import org.jplot2d.tex.MathElement;
import org.jplot2d.tex.MathLabel;
import org.jplot2d.tex.TeXMathUtils;
import org.jplot2d.util.DoubleDimension2D;

/**
 * @author Jingjing Li
 * 
 */
public class TitleImpl extends ComponentImpl implements TitleEx {

	private Position position = Position.TOPCENTER;

	private double locX, locY;

	private MathElement textModel;

	private HAlign hAlign;

	private VAlign vAlign;

	private MathLabel label;

	private double gapFactor = 0.25;

	/**
	 * A cached bounds to meet the oldValue-calcSize-invalidate procedure in PlotImpl
	 */
	private Rectangle2D bounds = new Rectangle2D.Double();

	public TitleImpl() {
		hAlign = HAlign.CENTER;
		vAlign = VAlign.MIDDLE;
	}

	public String getId() {
		if (getParent() != null) {
			return "Title" + getParent().indexOf(this);
		} else {
			return "Title@" + Integer.toHexString(System.identityHashCode(this));
		}
	}

	public InvokeStep getInvokeStepFormParent() {
		if (parent == null) {
			return null;
		}

		Method method;
		try {
			method = Plot.class.getMethod("getTitle", Integer.TYPE);
		} catch (NoSuchMethodException e) {
			throw new Error(e);
		}
		return new InvokeStep(method, getParent().indexOf(this));
	}

	public PlotEx getParent() {
		return (PlotEx) super.getParent();
	}

	public boolean canContribute() {
		return isVisible() && textModel != null;
	}

	public Point2D getLocation() {
		return new Point2D.Double(locX, locY);
	}

	public final void setLocation(Point2D p) {
		setLocation(p.getX(), p.getY());
	}

	public void setLocation(double locX, double locY) {
		if (getLocation().getX() != locX || getLocation().getY() != locY) {
			this.locX = locX;
			this.locY = locY;
		}
	}

	public void thisEffectiveColorChanged() {
		if (isVisible()) {
			redraw();
		}
	}

	public void thisEffectiveFontChanged() {
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

	public Dimension2D getSize() {
		Rectangle2D bounds = getBounds();
		return new DoubleDimension2D(bounds.getWidth(), bounds.getHeight());
	}

	public Position getPosition() {
		return position;
	}

	public void setPosition(Position position) {
		this.position = position;
	}

	public double getGapFactor() {
		return gapFactor;
	}

	public void setGapFactor(double factor) {
		this.gapFactor = factor;
		if (canContribute()) {
			invalidatePlot();
		}
	}

	/**
	 * Invalidate the parent plot when its position is not null.
	 */
	private void invalidatePlot() {
		if (getParent() != null && position != null) {
			getParent().invalidate();
		}
	}

	public Rectangle2D getBounds() {
		return bounds;
	}

	public void calcSize() {
		if (label == null) {
			label = new MathLabel(getTextModel(), getEffectiveFont(), getVAlign(), getHAlign());
		}
		bounds = label.getBounds();
	}

	@Override
	public void copyFrom(ElementEx src) {
		super.copyFrom(src);

		TitleImpl tc = (TitleImpl) src;
		locX = tc.locX;
		locY = tc.locY;
		this.textModel = tc.textModel;
		this.hAlign = tc.hAlign;
		this.vAlign = tc.vAlign;
		this.label = tc.label;
		this.position = tc.position;
		this.gapFactor = tc.gapFactor;
	}

	public void draw(Graphics2D g) {
		if (label == null) {
			label = new MathLabel(getTextModel(), getEffectiveFont(), getVAlign(), getHAlign());
		}

		AffineTransform oldTransform = g.getTransform();

		g.transform(getParent().getPaperTransform().getTransform());
		g.translate(getLocation().getX(), getLocation().getY());
		g.scale(1.0, -1.0);

		g.setColor(getEffectiveColor());

		label.draw(g);

		g.setTransform(oldTransform);
	}

}
