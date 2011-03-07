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

import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import org.jplot2d.element.LegendItem;
import org.jplot2d.util.MathElement;
import org.jplot2d.util.TeXMathUtils;

/**
 * @author Jingjing Li
 * 
 */
public abstract class GraphPlotterImpl extends ComponentImpl implements
		GraphPlotterEx {

	private static Point2D LOCATION = new Point2D.Double();

	private MathElement name;

	private LegendItemEx legendItem;

	public GraphPlotterImpl() {
		legendItem = new LegendItemImpl(this);
	}

	public LayerEx getParent() {
		return (LayerEx) super.getParent();
	}

	public Point2D getLocation() {
		return LOCATION;
	}

	public void setLocation(double locX, double locY) {
		throw new UnsupportedOperationException();
	}

	public Dimension2D getSize() {
		if (getParent() == null) {
			return null;
		} else {
			return getParent().getSize();
		}
	}

	public Rectangle2D getBounds() {
		if (getParent() == null) {
			return null;
		} else {
			return new Rectangle2D.Double(0, 0, getSize().getWidth(), getSize()
					.getHeight());
		}
	}

	public String getName() {
		return TeXMathUtils.toString(name);
	}

	public void setName(String name) {
		this.name = TeXMathUtils.parseText(name);
	}

	public MathElement getNameModel() {
		return name;
	}

	public void setNameModel(MathElement name) {
		this.name = name;
	}

	public LegendItem getLegendItem() {
		return legendItem;
	}

	@Override
	public void copyFrom(ElementEx src) {
		GraphPlotterImpl gp = (GraphPlotterImpl) src;
		this.name = gp.name;
	}

}
