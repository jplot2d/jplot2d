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
import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import org.jplot2d.util.MathElement;
import org.jplot2d.util.MathLabel;
import org.jplot2d.util.TeXMathUtils;

/**
 * @author Jingjing Li
 * 
 */
public class LegendItemImpl extends ElementImpl implements LegendItemEx {

	private boolean visible = true;

	private MathElement textModel;

	private MathLabel label;

	private LegendEx legend;

	public LegendItemImpl() {

	}

	public GraphPlotterEx getParent() {
		return (GraphPlotterEx) super.getParent();
	}

	public LegendEx getLegend() {
		return legend;
	}

	public void setLegend(LegendEx legend) {
		this.legend = legend;
	}

	public Point2D getLocation() {
		// TODO Auto-generated method stub
		return null;
	}

	public Dimension2D getSize() {
		// TODO Auto-generated method stub
		return null;
	}

	public Rectangle2D getBounds() {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean isVisible() {
		return visible;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
		invalidate();
		redraw();
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
			invalidate();
			redraw();
		}
	}

	/**
	 * Invalidate the legend
	 */
	private void invalidate() {
		if (getLegend() != null) {
			getLegend().invalidate();
		}
	}

	private void redraw() {
		if (getLegend() != null) {
			getLegend().invalidate();
			getLegend().redraw();
		}
	}

	public void draw(Graphics2D g) {
		// TODO Auto-generated method stub

	}

}
