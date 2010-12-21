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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.jplot2d.element.AxisPosition;
import org.jplot2d.element.impl.AxisEx;
import org.jplot2d.element.impl.LayerEx;
import org.jplot2d.element.impl.SubplotEx;
import org.jplot2d.element.impl.ViewportAxisEx;
import org.jplot2d.util.DoubleDimension2D;
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

		Insets2D margin = calcMargin(subplot);

		double contentWidth = subplot.getSize().getWidth() - margin.getLeft()
				- margin.getRight();
		double contentHeight = subplot.getSize().getHeight() - margin.getTop()
				- margin.getBottom();
		Rectangle2D contentRect = new Rectangle2D.Double(margin.getLeft(),
				margin.getBottom(), contentWidth, contentHeight);

		subplot.setViewportBounds(contentRect);

		// layers always have the same bounds as subplot
		for (LayerEx layer : subplot.getLayers()) {
			layer.setLocation(new Point2D.Double(margin.getLeft(), margin
					.getBottom()));
			layer.setSize(new DoubleDimension2D(contentWidth, contentHeight));
		}

		locateAxes(subplot, contentRect);

	}

	private Insets2D calcMargin(SubplotEx sp) {
		// quick return
		if (!sp.isAutoMarginTop() && !sp.isAutoMarginLeft()
				&& !sp.isAutoMarginBottom() && !sp.isAutoMarginRight()) {
			return new Insets2D(sp.getMarginTop(), sp.getMarginLeft(),
					sp.getMarginBottom(), sp.getMarginRight());
		}

		double mTop = 0, mLeft = 0, mBottom = 0, mRight = 0;

		// count axis thickness
		for (ViewportAxisEx xva : sp.getXViewportAxes()) {
			for (AxisEx axis : xva.getAxes()) {
				if (axis.getPosition() == AxisPosition.POSITIVE_SIDE) {
					mTop += axis.getThickness();
				} else {
					mBottom += axis.getThickness();
				}
			}
		}
		for (ViewportAxisEx yva : sp.getYViewportAxes()) {
			for (AxisEx axis : yva.getAxes()) {
				if (axis.getPosition() == AxisPosition.POSITIVE_SIDE) {
					mRight += axis.getThickness();
				} else {
					mLeft += axis.getThickness();
				}
			}
		}

		if (!sp.isAutoMarginTop()) {
			mTop = sp.getMarginTop();
		}
		if (!sp.isAutoMarginLeft()) {
			mLeft = sp.getMarginLeft();
		}
		if (!sp.isAutoMarginBottom()) {
			mBottom = sp.getMarginBottom();
		}
		if (!sp.isAutoMarginRight()) {
			mRight = sp.getMarginRight();
		}

		return new Insets2D(mTop, mLeft, mBottom, mRight);
	}

	private static void locateAxes(SubplotEx sp, Rectangle2D contentBox) {

		// set offset and length for ViewportAxisEx
		for (ViewportAxisEx xva : sp.getXViewportAxes()) {
			xva.setOffset(contentBox.getX());
			xva.setLength(contentBox.getWidth());
		}
		for (ViewportAxisEx yva : sp.getYViewportAxes()) {
			yva.setOffset(contentBox.getY());
			yva.setLength(contentBox.getHeight());
		}

		// find all axes in inner-to-outer order
		ArrayList<AxisEx> topAxisM = new ArrayList<AxisEx>();
		ArrayList<AxisEx> leftAxisM = new ArrayList<AxisEx>();
		ArrayList<AxisEx> bottomAxisM = new ArrayList<AxisEx>();
		ArrayList<AxisEx> rightAxisM = new ArrayList<AxisEx>();

		LayerEx[] layers = sp.getLayers();
		for (LayerEx layer : layers) {
			ViewportAxisEx xva = layer.getXViewportAxis();
			ViewportAxisEx yva = layer.getYViewportAxis();
			if (xva != null) {
				for (AxisEx axis : xva.getAxes()) {
					if (axis.getPosition() == AxisPosition.POSITIVE_SIDE) {
						topAxisM.add(axis);
					} else {
						bottomAxisM.add(axis);
					}
				}
			}
			if (yva != null) {
				for (AxisEx axis : yva.getAxes()) {
					if (axis.getPosition() == AxisPosition.POSITIVE_SIDE) {
						rightAxisM.add(axis);
					} else {
						leftAxisM.add(axis);
					}
				}
			}
		}

		// viewport box
		double iabLeft = contentBox.getMinX();
		double iabRight = contentBox.getMaxX();
		double iabBottom = contentBox.getMinY();
		double iabTop = contentBox.getMaxY();

		/* locate axes */
		if (leftAxisM.size() > 0) {
			double xloc = iabLeft;
			AxisEx am = leftAxisM.get(0);
			am.setLocation(xloc, iabBottom);
			xloc -= am.getDesc();
			for (int i = 1; i < leftAxisM.size(); i++) {
				am = leftAxisM.get(i);
				if (i > 0) {
					xloc -= am.getAsc();
				}
				am.setLocation(xloc, iabBottom);
				xloc -= am.getDesc();
			}
		}
		if (rightAxisM.size() > 0) {
			double xloc = iabRight;
			AxisEx am = rightAxisM.get(0);
			am.setLocation(xloc, iabBottom);
			xloc += am.getAsc();
			for (int i = 1; i < rightAxisM.size(); i++) {
				am = rightAxisM.get(i);
				xloc += am.getDesc();
				am.setLocation(xloc, iabBottom);
				xloc += am.getAsc();
			}
		}
		if (bottomAxisM.size() > 0) {
			double yloc = iabBottom;
			AxisEx am = bottomAxisM.get(0);
			am.setLocation(iabLeft, yloc);
			yloc += am.getDesc();
			for (int i = 1; i < bottomAxisM.size(); i++) {
				am = bottomAxisM.get(i);
				yloc -= am.getAsc();
				am.setLocation(iabLeft, yloc);
				yloc -= am.getDesc();
			}
		}
		if (topAxisM.size() > 0) {
			double yloc = iabTop;
			AxisEx am = topAxisM.get(0);
			am.setLocation(iabLeft, yloc);
			yloc += am.getAsc();
			for (int i = 1; i < topAxisM.size(); i++) {
				am = topAxisM.get(i);
				yloc += am.getDesc();
				am.setLocation(iabLeft, yloc);
				yloc += am.getAsc();
			}
		}
	}

}
