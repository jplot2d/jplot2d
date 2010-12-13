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
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;

import org.jplot2d.data.XYData;

/**
 * @author Jingjing Li
 * 
 */
public class XYDataPlotImpl extends LayerDataPlotImpl implements
		XYDataPlotEx {

	private XYData data;

	public XYData getData() {
		return data;
	}

	public void setData(XYData data) {
		this.data = data;
	}

	public void draw(Graphics2D g) {
		Shape oldClip = g.getClip();
		Rectangle2D clip = getParent().getPhysicalTransform().getPtoD(
				getParent().getParent().getViewportBounds());
		g.setClip(clip);

		g.setColor(Color.BLACK);
		Rectangle rect = getParent().getPhysicalTransform()
				.getPtoD(getBounds()).getBounds();
		g.drawLine(rect.x, rect.y, (int) rect.getMaxX(), (int) rect.getMaxY());
		g.drawLine(rect.x, (int) rect.getMaxY(), (int) rect.getMaxX(), rect.y);

		g.setClip(oldClip);
	}

}
