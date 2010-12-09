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

import org.jplot2d.element.impl.LayerEx;
import org.jplot2d.element.impl.SubplotEx;
import org.jplot2d.element.impl.ViewportAxisEx;
import org.jplot2d.util.Insets2D;
import org.jplot2d.util.NumberUtils;

/**
 * This LayoutDirector only layout layers, all subplot are invisible.
 * 
 * @author Jingjing Li
 * 
 */
public class SimpleLayoutDirector implements LayoutDirector {

	/** The layout constraints */
	private Map<SubplotEx, Object> constraints = new HashMap<SubplotEx, Object>();

	static boolean approximate(double a, double b) {
		return NumberUtils.approximate(a, b, 4);
	}

	/*
	 * This LayoutDirector doesn't impose viewport constraint on its children.
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
		// invalidate its parent
		if (subplot.getParent() != null) {
			subplot.getParent().invalidate();
		}
	}

	public void invalidateLayout(SubplotEx subplot) {
		// do not handle subplot at all
	}

	public void layout(SubplotEx subplot) {

		Insets2D margin = calcMargin();

		double contentWidth = subplot.getSize().getWidth();
		double contentHeight = subplot.getSize().getHeight() - margin.getTop()
				- margin.getBottom();
		Rectangle2D contentRect = new Rectangle2D.Double(margin.getLeft(),
				margin.getBottom(), contentWidth, contentHeight);
		for (LayerEx layer : subplot.getLayers()) {
			layer.setLocation(new Point2D.Double(0, 0));
			layer.setSize(subplot.getSize());
			subplot.setViewportBounds(contentRect);
		}

		for (ViewportAxisEx axis : subplot.getXViewportAxes()) {
			axis.setLength(subplot.getViewportBounds().getWidth());
			axis.validate();
		}
		for (ViewportAxisEx axis : subplot.getYViewportAxes()) {
			axis.setLength(subplot.getViewportBounds().getHeight());
			axis.validate();
		}

	}

	private Insets2D calcMargin() {
		// TODO Auto-generated method stub
		return new Insets2D(8, 8, 8, 8);
	}

}
