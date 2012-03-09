/**
 * Copyright 2010-2012 Jingjing Li.
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

import org.jplot2d.element.LegendItem;
import org.jplot2d.tex.MathElement;

/**
 * @author Jingjing Li
 * 
 */
public interface LegendItemEx extends LegendItem, ElementEx {

	public GraphEx getParent();

	public LegendEx getLegend();

	public void setLegend(LegendEx legend);

	/**
	 * Returns the text displayed in the legend item
	 * 
	 * @return the text
	 */
	public String getText();

	/**
	 * Sets the text displayed in the legend item
	 * 
	 * @param text
	 *            the text displayed in the legend item
	 */
	public void setText(String text);

	/**
	 * Returns the text displayed in the legend item
	 * 
	 * @return the text
	 */
	public MathElement getTextModel();

	/**
	 * Sets the text displayed in the legend item
	 * 
	 * @param text
	 *            the text displayed in the legend
	 */
	public void setTextModel(MathElement model);

	/**
	 * Called by legend when its effective font changed
	 */
	public void legendEffectiveFontChanged();

	public Dimension2D getSize();

	/**
	 * Set location in legend
	 */
	public void setLocation(double locx, double locy);

	/**
	 * This method is called by LegendImpl. The given g has been transformed to legend's paper
	 * space.
	 * 
	 * @param g
	 *            the graphic to draw.
	 */
	public void draw(Graphics2D g);

}
