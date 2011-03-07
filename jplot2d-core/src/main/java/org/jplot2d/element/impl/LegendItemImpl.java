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

/**
 * @author Jingjing Li
 * 
 */
public class LegendItemImpl extends ComponentImpl implements LegendItemEx {

	private final GraphPlotterEx graphPlotter;

	public LegendItemImpl(GraphPlotterEx graphPlotter) {
		this.graphPlotter = graphPlotter;
	}

	public LegendEx getParent() {
		return (LegendEx) super.getParent();
	}

	public GraphPlotterEx getGraphPlotter() {
		return graphPlotter;
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

	public void draw(Graphics2D g) {
		// TODO Auto-generated method stub
		
	}

}
