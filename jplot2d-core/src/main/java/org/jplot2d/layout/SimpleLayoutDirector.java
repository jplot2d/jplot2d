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

import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.jplot2d.element.AxisPosition;
import org.jplot2d.element.impl.AxisEx;
import org.jplot2d.element.impl.LayerEx;
import org.jplot2d.element.impl.SubplotEx;
import org.jplot2d.element.impl.SubplotMarginEx;
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

	/**
	 * All Axes in a subplot grouped by position.
	 */
	public static class AxesInSubplot {
		final ArrayList<AxisEx> leftAxes = new ArrayList<AxisEx>();
		final ArrayList<AxisEx> rightAxes = new ArrayList<AxisEx>();
		final ArrayList<AxisEx> topAxes = new ArrayList<AxisEx>();
		final ArrayList<AxisEx> bottomAxes = new ArrayList<AxisEx>();
	}

	/** The layout constraints */
	private Map<SubplotEx, Object> constraints = new HashMap<SubplotEx, Object>();

	static boolean approximate(double a, double b) {
		return NumberUtils.approximate(a, b, 4);
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

	public void invalidateLayout(SubplotEx subplot) {
		// do not handle subplot at all
	}

	public void layout(SubplotEx subplot) {

		Rectangle2D contentRect;
		AxesInSubplot ais = getAllAxes(subplot);

		if (subplot.getContentConstrant() != null) {
			contentRect = subplot.getContentConstrant();
		} else {
			Insets2D margin = calcMargin(subplot.getMargin(), ais);

			subplot.getMargin().setMarginTop(margin.getTop());
			subplot.getMargin().setMarginLeft(margin.getLeft());
			subplot.getMargin().setMarginBottom(margin.getBottom());
			subplot.getMargin().setMarginRight(margin.getRight());

			double contentWidth = subplot.getSize().getWidth()
					- margin.getLeft() - margin.getRight();
			double contentHeight = subplot.getSize().getHeight()
					- margin.getTop() - margin.getBottom();
			if (contentWidth < SubplotEx.MIN_CONTENT_SIZE.getWidth()) {
				contentWidth = SubplotEx.MIN_CONTENT_SIZE.getWidth();
			}
			if (contentHeight < SubplotEx.MIN_CONTENT_SIZE.getHeight()) {
				contentHeight = SubplotEx.MIN_CONTENT_SIZE.getHeight();
			}
			contentRect = new Rectangle2D.Double(margin.getLeft(),
					margin.getBottom(), contentWidth, contentHeight);
		}

		subplot.setContentBounds(contentRect);
		locateAxes(subplot, contentRect, ais);
		locateLayers(subplot, contentRect);
	}

	private AxesInSubplot getAllAxes(SubplotEx subplot) {
		AxesInSubplot ais = new AxesInSubplot();

		for (ViewportAxisEx xva : subplot.getXViewportAxes()) {
			for (AxisEx axis : xva.getAxes()) {
				if (axis.getPosition() == AxisPosition.POSITIVE_SIDE) {
					ais.topAxes.add(axis);
				} else {
					ais.bottomAxes.add(axis);
				}
			}
		}
		for (ViewportAxisEx yva : subplot.getYViewportAxes()) {
			for (AxisEx axis : yva.getAxes()) {
				if (axis.getPosition() == AxisPosition.POSITIVE_SIDE) {
					ais.rightAxes.add(axis);
				} else {
					ais.leftAxes.add(axis);
				}
			}
		}

		return ais;
	}

	static Insets2D calcMargin(SubplotMarginEx margin, AxesInSubplot ais) {

		// SubplotMarginEx margin = subplot.getMargin();

		double mTop;
		double mLeft;
		double mBottom;
		double mRight;

		if (margin.isAutoMarginLeft()) {
			mLeft = margin.getExtraLeft();
			if (ais.leftAxes.size() > 0) {
				for (AxisEx am : ais.leftAxes) {
					mLeft += am.getAsc() + am.getDesc();
				}
				mLeft -= ais.leftAxes.get(0).getDesc();
			}
		} else {
			mLeft = margin.getMarginLeft();
		}

		if (margin.isAutoMarginRight()) {
			mRight = margin.getExtraRight();
			if (ais.rightAxes.size() > 0) {
				for (AxisEx am : ais.rightAxes) {
					mRight += am.getAsc() + am.getDesc();
				}
				mRight -= ais.rightAxes.get(0).getAsc();
			}
		} else {
			mRight = margin.getMarginRight();
		}

		if (margin.isAutoMarginTop()) {
			mTop = margin.getExtraTop();
			if (ais.topAxes.size() > 0) {
				for (AxisEx am : ais.topAxes) {
					mTop += am.getAsc() + am.getDesc();
				}
				mTop -= ais.topAxes.get(0).getDesc();
			}
		} else {
			mTop = margin.getMarginTop();
		}

		if (margin.isAutoMarginBottom()) {
			mBottom = margin.getExtraBottom();
			if (ais.bottomAxes.size() > 0) {
				for (AxisEx am : ais.bottomAxes) {
					mBottom += am.getAsc() + am.getDesc();
				}
				mBottom -= ais.bottomAxes.get(0).getAsc();
			}
		} else {
			mBottom = margin.getMarginBottom();
		}

		return new Insets2D(mTop, mLeft, mBottom, mRight);
	}

	private static void locateAxes(SubplotEx sp, Rectangle2D contentBox,
			AxesInSubplot ais) {

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
		ArrayList<AxisEx> topAxes = ais.topAxes;
		ArrayList<AxisEx> leftAxes = ais.leftAxes;
		ArrayList<AxisEx> bottomAxes = ais.bottomAxes;
		ArrayList<AxisEx> rightAxes = ais.rightAxes;

		// viewport box
		double iabLeft = contentBox.getMinX();
		double iabRight = contentBox.getMaxX();
		double iabBottom = contentBox.getMinY();
		double iabTop = contentBox.getMaxY();

		/* locate axes */
		if (leftAxes.size() > 0) {
			double xloc = iabLeft;
			AxisEx am = leftAxes.get(0);
			am.setLocation(xloc, iabBottom);
			xloc -= am.getDesc();
			for (int i = 1; i < leftAxes.size(); i++) {
				am = leftAxes.get(i);
				if (i > 0) {
					xloc -= am.getAsc();
				}
				am.setLocation(xloc, iabBottom);
				xloc -= am.getDesc();
			}
		}
		if (rightAxes.size() > 0) {
			double xloc = iabRight;
			AxisEx am = rightAxes.get(0);
			am.setLocation(xloc, iabBottom);
			xloc += am.getAsc();
			for (int i = 1; i < rightAxes.size(); i++) {
				am = rightAxes.get(i);
				xloc += am.getDesc();
				am.setLocation(xloc, iabBottom);
				xloc += am.getAsc();
			}
		}
		if (bottomAxes.size() > 0) {
			double yloc = iabBottom;
			AxisEx am = bottomAxes.get(0);
			am.setLocation(iabLeft, yloc);
			yloc += am.getDesc();
			for (int i = 1; i < bottomAxes.size(); i++) {
				am = bottomAxes.get(i);
				yloc -= am.getAsc();
				am.setLocation(iabLeft, yloc);
				yloc -= am.getDesc();
			}
		}
		if (topAxes.size() > 0) {
			double yloc = iabTop;
			AxisEx am = topAxes.get(0);
			am.setLocation(iabLeft, yloc);
			yloc += am.getAsc();
			for (int i = 1; i < topAxes.size(); i++) {
				am = topAxes.get(i);
				yloc += am.getDesc();
				am.setLocation(iabLeft, yloc);
				yloc += am.getAsc();
			}
		}
	}

	private static void locateLayers(SubplotEx subplot, Rectangle2D contentBox) {
		for (LayerEx layer : subplot.getLayers()) {
			layer.setLocation(new Point2D.Double(contentBox.getX(), contentBox
					.getY()));
		}
	}

	public Dimension2D getPreferredSize(SubplotEx subplot) {
		Dimension2D prefContSize = getPreferredContentSize(subplot);
		if (prefContSize == null) {
			return null;
		} else {
			AxesInSubplot ais = getAllAxes(subplot);
			Insets2D margin = calcMargin(subplot.getMargin(), ais);
			double w = prefContSize.getWidth() + margin.getLeft()
					+ margin.getRight();
			double h = prefContSize.getHeight() + margin.getTop()
					+ margin.getBottom();
			return new DoubleDimension2D(w, h);
		}
	}

	/**
	 * Calculate the preferred content size of the given subplot. The default
	 * implementation returns the subplot.getPreferredContentSize(), which can
	 * be override by subclass to consider nested subplots. The returned size
	 * may be <code>null</code> if there is no enough information to derive a
	 * value.
	 * 
	 * @param subplot
	 * @return the preferred content size
	 */
	protected Dimension2D getPreferredContentSize(SubplotEx subplot) {
		return subplot.getPreferredContentSize();
	}

}
