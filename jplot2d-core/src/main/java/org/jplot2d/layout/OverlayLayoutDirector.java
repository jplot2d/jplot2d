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
package org.jplot2d.layout;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.HashMap;
import java.util.Map;

import org.jplot2d.element.impl.SubplotEx;
import org.jplot2d.element.impl.ViewportAxisEx;
import org.jplot2d.util.Insets2D;
import org.jplot2d.util.NumberUtils;

/**
 * This LayoutDirector overlay all subplots over the top of each other.
 * 
 * 
 * @author Jingjing Li
 * 
 */
public class OverlayLayoutDirector implements LayoutDirector {

	/** The layout constraints */
	private Map<SubplotEx, Object> constraints = new HashMap<SubplotEx, Object>();

	/**
	 * the margin around the subplot
	 */
	Insets2D contentsMargin = new Insets2D(0.1, 0.1, 0.1, 0.1);

	/**
	 * the margin around the plot
	 */
	Insets2D margin = new Insets2D(0.1, 0.1, 0.1, 0.1);

	/**
	 * the physical size of every grid cell
	 */
	GridCellGeom cellGeom;

	GridCellInsets cellPadding = new GridCellInsets();

	private SubplotEx plot;

	static boolean approximate(double a, double b) {
		return NumberUtils.approximate(a, b, 4);
	}

	public OverlayLayoutDirector(SubplotEx plot) {
		this.plot = plot;
	}

	/*
	 * OverlayLayoutDirector doesn't impose viewport constraint on its children.
	 */
	public Rectangle2D getViewportConstrant(SubplotEx subplot) {
		return null;
	}

	public Object getConstraint(SubplotEx subplot) {
		return constraints.get(subplot);
	}

	public void remove(SubplotEx subplot) {
		constraints.remove(subplot);
	}

	public void setConstraint(SubplotEx subplot, Object constraint) {
		constraints.put(subplot, constraint);
	}

	public void layout() {

		for (SubplotEx sp : plot.getSubplots()) {
			sp.setLocation(new Point2D.Double(0, 0));
			sp.setPhysicalSize(plot.getSize());
			sp.setViewportBounds(plot.getPhysicalBounds());

			for (ViewportAxisEx axis : sp.getXViewportAxes()) {
				axis.setLength(sp.getViewportBounds().getWidth());
				axis.validate();
			}
			for (ViewportAxisEx axis : sp.getYViewportAxes()) {
				axis.setLength(sp.getViewportBounds().getHeight());
				axis.validate();
			}

			sp.validate();
		}
		plot.validate();

	}

	public Insets2D getMargin() {
		return margin;
	}

	public void setMargin(Insets2D margin) {
		if (margin.equals(margin)) {
			return;
		}
		this.margin = margin;
		plot.invalidate();
	}

}
