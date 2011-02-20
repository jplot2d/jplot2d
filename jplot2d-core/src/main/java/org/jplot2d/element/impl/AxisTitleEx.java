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

import java.awt.Graphics2D;
import java.awt.geom.Dimension2D;

import org.jplot2d.element.AxisTitle;
import org.jplot2d.element.VAlign;

/**
 * @author Jingjing Li
 * 
 */
public interface AxisTitleEx extends AxisTitle, ElementEx {

	/**
	 * Set the vertical alignment. The alignment can be TOP, MIDDLE, or BOTTOM.
	 * eg, TOP means the title is on the top of the base point
	 * 
	 * @param vAlign
	 *            the vertical alignment.
	 */
	public void setVAlign(VAlign vAlign);

	/**
	 * Returns the title size. The units of bounds is pt (1/72 inch)
	 * 
	 * @return the paper bounds of this title.
	 */
	public Dimension2D getSize();

	/**
	 * Draw this component only. All its children is not drawn.
	 * 
	 * @param g
	 *            to the Graphics2D drawing. The transformation of this
	 *            component is not applied.
	 * @param xt
	 *            the x position
	 * @param titleOffset
	 *            the y position
	 * @param titleVAlign
	 *            the vertical alignment
	 */
	public void draw(Graphics2D g, double x, double y);

}
