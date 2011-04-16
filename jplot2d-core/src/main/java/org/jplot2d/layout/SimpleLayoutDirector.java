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
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.jplot2d.element.AxisPosition;
import org.jplot2d.element.HAlign;
import org.jplot2d.element.VAlign;
import org.jplot2d.element.impl.AxisEx;
import org.jplot2d.element.impl.LegendEx;
import org.jplot2d.element.impl.PlotEx;
import org.jplot2d.element.impl.PlotMarginEx;
import org.jplot2d.element.impl.TitleEx;
import org.jplot2d.util.DoubleDimension2D;
import org.jplot2d.util.Insets2D;
import org.jplot2d.util.NumberUtils;

/**
 * This LayoutDirector only layout layers, all subplot are not considered.
 * 
 * @author Jingjing Li
 * 
 */
public class SimpleLayoutDirector implements LayoutDirector {

	/**
	 * All Axes in a plot grouped by position.
	 */
	public static class AxesInPlot {
		final ArrayList<AxisEx> leftAxes = new ArrayList<AxisEx>();
		final ArrayList<AxisEx> rightAxes = new ArrayList<AxisEx>();
		final ArrayList<AxisEx> topAxes = new ArrayList<AxisEx>();
		final ArrayList<AxisEx> bottomAxes = new ArrayList<AxisEx>();
	}

	private static final double TITLE_GAP_RATIO = 0.25;

	static double LEGEND_GAP = 8.0;

	/** The layout constraints */
	private Map<PlotEx, Object> constraints = new HashMap<PlotEx, Object>();

	static boolean approximate(double a, double b) {
		return NumberUtils.approximate(a, b, 4);
	}

	public Object getConstraint(PlotEx plot) {
		return constraints.get(plot);
	}

	public void remove(PlotEx plot) {
		constraints.remove(plot);
	}

	public void setConstraint(PlotEx plot, Object constraint) {
		constraints.put(plot, constraint);
	}

	public void invalidateLayout(PlotEx plot) {
		// nothing to do
	}

	public void layout(PlotEx plot) {

		Rectangle2D contentRect;
		AxesInPlot ais = getAllAxes(plot);

		if (plot.getContentConstrant() != null) {
			contentRect = plot.getContentConstrant();
		} else {
			Insets2D margin = calcMargin(plot, ais);

			plot.getMargin().setMarginTop(margin.getTop());
			plot.getMargin().setMarginLeft(margin.getLeft());
			plot.getMargin().setMarginBottom(margin.getBottom());
			plot.getMargin().setMarginRight(margin.getRight());

			double contentWidth = plot.getSize().getWidth() - margin.getLeft()
					- margin.getRight();
			double contentHeight = plot.getSize().getHeight() - margin.getTop()
					- margin.getBottom();
			if (contentWidth < PlotEx.MIN_CONTENT_SIZE.getWidth()) {
				contentWidth = PlotEx.MIN_CONTENT_SIZE.getWidth();
			}
			if (contentHeight < PlotEx.MIN_CONTENT_SIZE.getHeight()) {
				contentHeight = PlotEx.MIN_CONTENT_SIZE.getHeight();
			}
			contentRect = new Rectangle2D.Double(margin.getLeft(),
					margin.getBottom(), contentWidth, contentHeight);
		}

		plot.setContentBounds(contentRect);
		layoutLeftMargin(plot, contentRect, ais);
		layoutRightMargin(plot, contentRect, ais);
		layoutTopMargin(plot, contentRect, ais);
		layoutBottomMargin(plot, contentRect, ais);
	}

	/**
	 * Return all visible axes on left, right, top, bottom margins.
	 * 
	 * @param plot
	 * @return
	 */
	protected static AxesInPlot getAllAxes(PlotEx plot) {
		AxesInPlot ais = new AxesInPlot();

		for (AxisEx axis : plot.getXAxes()) {
			if (axis.canContribute()) {
				if (axis.getPosition() == AxisPosition.POSITIVE_SIDE) {
					ais.topAxes.add(axis);
				} else {
					ais.bottomAxes.add(axis);
				}
			}
		}
		for (AxisEx axis : plot.getYAxes()) {
			if (axis.canContribute()) {
				if (axis.getPosition() == AxisPosition.POSITIVE_SIDE) {
					ais.rightAxes.add(axis);
				} else {
					ais.leftAxes.add(axis);
				}
			}
		}

		return ais;
	}

	protected static double calcLeftMargin(PlotEx plot, AxesInPlot ais) {
		PlotMarginEx margin = plot.getMargin();

		if (!margin.isAutoMarginLeft()) {
			return margin.getMarginLeft() + margin.getExtraLeft();
		}

		double mLeft = margin.getExtraLeft();

		if (ais.leftAxes.size() > 0) {
			for (AxisEx am : ais.leftAxes) {
				mLeft += am.getAsc() + am.getDesc();
			}
			mLeft -= ais.leftAxes.get(0).getDesc();
		}

		LegendEx legend = plot.getLegend();
		if (legend.canContribute()) {
			switch (legend.getPosition()) {
			case LEFTTOP:
			case LEFTMIDDLE:
			case LEFTBOTTOM: {
				mLeft += LEGEND_GAP + legend.getSize().getWidth();
				break;
			}
			}
		}

		return mLeft;
	}

	protected static double calcRightMargin(PlotEx plot, AxesInPlot ais) {
		PlotMarginEx margin = plot.getMargin();

		if (!margin.isAutoMarginRight()) {
			return margin.getMarginRight() + margin.getExtraRight();
		}

		double mRight = margin.getExtraRight();

		if (ais.rightAxes.size() > 0) {
			if (ais.rightAxes.size() > 0) {
				for (AxisEx am : ais.rightAxes) {
					mRight += am.getAsc() + am.getDesc();
				}
				mRight -= ais.rightAxes.get(0).getAsc();
			}
		}

		LegendEx legend = plot.getLegend();
		if (legend.canContribute()) {
			switch (legend.getPosition()) {
			case RIGHTTOP:
			case RIGHTMIDDLE:
			case RIGHTBOTTOM: {
				mRight += LEGEND_GAP + legend.getSize().getWidth();
				break;
			}
			}
		}

		return mRight;
	}

	protected static double calcTopMargin(PlotEx plot, AxesInPlot ais) {
		PlotMarginEx margin = plot.getMargin();

		if (!margin.isAutoMarginTop()) {
			return margin.getMarginTop() + margin.getExtraTop();
		}

		double mTop = margin.getExtraTop();

		if (ais.topAxes.size() > 0) {
			for (AxisEx am : ais.topAxes) {
				mTop += am.getAsc() + am.getDesc();
			}
			mTop -= ais.topAxes.get(0).getDesc();
		}

		LegendEx legend = plot.getLegend();
		if (legend.canContribute()) {
			switch (legend.getPosition()) {
			case TOPLEFT:
			case TOPCENTER:
			case TOPRIGHT: {
				mTop += LEGEND_GAP + legend.getSize().getHeight();
				break;
			}
			}
		}

		for (TitleEx title : plot.getTitles()) {
			if (title.canContribute()) {
				double titleHeight = title.getSize().getHeight();
				switch (title.getPosition()) {
				case TOPLEFT:
				case TOPCENTER:
				case TOPRIGHT: {
					mTop += (1 + TITLE_GAP_RATIO) * titleHeight;
					break;
				}
				}
			}
		}

		return mTop;
	}

	protected static double calcBottomMargin(PlotEx plot, AxesInPlot ais) {
		PlotMarginEx margin = plot.getMargin();

		if (!margin.isAutoMarginBottom()) {
			return margin.getMarginBottom() + margin.getExtraBottom();
		}

		double mBottom = margin.getExtraBottom();

		if (ais.bottomAxes.size() > 0) {
			for (AxisEx am : ais.bottomAxes) {
				mBottom += am.getAsc() + am.getDesc();
			}
			mBottom -= ais.bottomAxes.get(0).getAsc();
		}

		LegendEx legend = plot.getLegend();
		if (legend.canContribute()) {
			switch (legend.getPosition()) {
			case BOTTOMLEFT:
			case BOTTOMCENTER:
			case BOTTOMRIGHT: {
				mBottom += LEGEND_GAP + legend.getSize().getHeight();
				break;
			}
			}
		}

		for (TitleEx title : plot.getTitles()) {
			if (title.canContribute()) {
				double titleHeight = title.getSize().getHeight();
				switch (title.getPosition()) {
				case BOTTOMLEFT:
				case BOTTOMCENTER:
				case BOTTOMRIGHT: {
					mBottom += (1 + TITLE_GAP_RATIO) * titleHeight;
					break;
				}
				}
			}
		}

		return mBottom;
	}

	/**
	 * Calculate the margin of the given plot. The legend size can be calculated
	 * by pre-setting its length constraint.
	 * 
	 * @param plot
	 * @param ais
	 * @return
	 */
	static Insets2D calcMargin(PlotEx plot, AxesInPlot ais) {

		double mLeft, mRight, mTop, mBottom;

		LegendEx legend = plot.getLegend();

		if (legend.canContribute()) {
			switch (legend.getPosition()) {
			case TOPLEFT:
			case TOPCENTER:
			case TOPRIGHT:
			case BOTTOMLEFT:
			case BOTTOMCENTER:
			case BOTTOMRIGHT: {
				double legendWidth = plot.getSize().getWidth()
						- plot.getMargin().getExtraLeft()
						- plot.getMargin().getExtraRight();
				legend.setLengthConstraint(legendWidth);
				break;
			}
			}
		}

		mTop = calcTopMargin(plot, ais);
		mBottom = calcBottomMargin(plot, ais);

		if (legend.canContribute()) {
			switch (legend.getPosition()) {
			case LEFTTOP:
			case LEFTMIDDLE:
			case LEFTBOTTOM:
			case RIGHTTOP:
			case RIGHTMIDDLE:
			case RIGHTBOTTOM: {
				double contentHeight = plot.getSize().getHeight() - mTop
						- mBottom;
				legend.setLengthConstraint(contentHeight);
				break;
			}
			}
		}

		mLeft = calcLeftMargin(plot, ais);
		mRight = calcRightMargin(plot, ais);

		return new Insets2D(mTop, mLeft, mBottom, mRight);
	}

	private static void layoutLeftMargin(PlotEx sp, Rectangle2D contentBox,
			AxesInPlot ais) {

		// all left axes in inner-to-outer order
		ArrayList<AxisEx> leftAxes = ais.leftAxes;

		// viewport box
		double iabLeft = contentBox.getMinX();
		double iabBottom = contentBox.getMinY();

		double xloc = iabLeft;

		/* locate axes */
		if (leftAxes.size() > 0) {
			AxisEx am = leftAxes.get(0);
			am.setLength(contentBox.getHeight());
			am.setLocation(xloc, iabBottom);
			xloc -= am.getDesc();
			for (int i = 1; i < leftAxes.size(); i++) {
				am = leftAxes.get(i);
				am.setLength(contentBox.getHeight());
				xloc -= am.getAsc();
				am.setLocation(xloc, iabBottom);
				xloc -= am.getDesc();
			}
		}

		LegendEx legend = sp.getLegend();

		/* locate legend */
		if (legend.canContribute()) {
			xloc -= LEGEND_GAP;
			double y;
			VAlign valign;
			switch (legend.getPosition()) {
			case LEFTTOP:
				y = contentBox.getMaxY();
				valign = VAlign.TOP;
				break;
			case LEFTMIDDLE:
				y = contentBox.getCenterY();
				valign = VAlign.MIDDLE;
				break;
			case LEFTBOTTOM:
				y = contentBox.getMinY();
				valign = VAlign.BOTTOM;
				break;
			default:
				return;
			}
			legend.setLocation(xloc, y);
			legend.setHAlign(HAlign.RIGHT);
			legend.setVAlign(valign);
		}

	}

	private static void layoutRightMargin(PlotEx sp, Rectangle2D contentBox,
			AxesInPlot ais) {

		// all right axes in inner-to-outer order
		ArrayList<AxisEx> rightAxes = ais.rightAxes;

		// viewport box
		double iabRight = contentBox.getMaxX();
		double iabBottom = contentBox.getMinY();

		double xloc = iabRight;

		/* locate axes */
		if (rightAxes.size() > 0) {
			AxisEx am = rightAxes.get(0);
			am.setLength(contentBox.getHeight());
			am.setLocation(xloc, iabBottom);
			xloc += am.getAsc();
			for (int i = 1; i < rightAxes.size(); i++) {
				am = rightAxes.get(i);
				am.setLength(contentBox.getHeight());
				xloc += am.getDesc();
				am.setLocation(xloc, iabBottom);
				xloc += am.getAsc();
			}
		}

		/* locate legend */
		LegendEx legend = sp.getLegend();
		if (legend.canContribute()) {
			xloc += LEGEND_GAP;
			double y;
			VAlign valign;
			switch (legend.getPosition()) {
			case RIGHTTOP:
				y = contentBox.getMaxY();
				valign = VAlign.TOP;
				break;
			case RIGHTMIDDLE:
				y = contentBox.getCenterY();
				valign = VAlign.MIDDLE;
				break;
			case RIGHTBOTTOM:
				y = contentBox.getMinY();
				valign = VAlign.BOTTOM;
				break;
			default:
				return;
			}
			legend.setLocation(xloc, y);
			legend.setHAlign(HAlign.LEFT);
			legend.setVAlign(valign);
		}

	}

	private static void layoutTopMargin(PlotEx sp, Rectangle2D contentBox,
			AxesInPlot ais) {

		// all left axes in inner-to-outer order
		ArrayList<AxisEx> topAxes = ais.topAxes;

		// viewport box
		double iabLeft = contentBox.getMinX();
		double iabTop = contentBox.getMaxY();

		double yloc = iabTop;

		// locate axes
		if (topAxes.size() > 0) {
			AxisEx am = topAxes.get(0);
			am.setLength(contentBox.getWidth());
			am.setLocation(iabLeft, yloc);
			yloc += am.getAsc();
			for (int i = 1; i < topAxes.size(); i++) {
				am = topAxes.get(i);
				am.setLength(contentBox.getWidth());
				yloc += am.getDesc();
				am.setLocation(iabLeft, yloc);
				yloc += am.getAsc();
			}
		}

		// locate legend
		LegendEx legend = sp.getLegend();
		if (legend.canContribute()) {
			yloc += LEGEND_GAP;
			double x;
			HAlign halign;
			switch (legend.getPosition()) {
			case TOPLEFT:
				x = contentBox.getMinX();
				halign = HAlign.LEFT;
				break;
			case TOPCENTER:
				x = contentBox.getCenterX();
				halign = HAlign.CENTER;
				break;
			case TOPRIGHT:
				x = contentBox.getMaxX();
				halign = HAlign.RIGHT;
				break;
			default:
				return;
			}
			legend.setLocation(x, yloc);
			legend.setHAlign(halign);
			legend.setVAlign(VAlign.BOTTOM);
		}
	}

	private static void layoutBottomMargin(PlotEx sp, Rectangle2D contentBox,
			AxesInPlot ais) {

		// all bottom axes in inner-to-outer order
		ArrayList<AxisEx> bottomAxes = ais.bottomAxes;

		// viewport box
		double iabLeft = contentBox.getMinX();
		double iabBottom = contentBox.getMinY();

		double yloc = iabBottom;

		// locate axes
		if (bottomAxes.size() > 0) {
			AxisEx am = bottomAxes.get(0);
			am.setLength(contentBox.getWidth());
			am.setLocation(iabLeft, yloc);
			yloc -= am.getDesc();
			for (int i = 1; i < bottomAxes.size(); i++) {
				am = bottomAxes.get(i);
				am.setLength(contentBox.getWidth());
				yloc -= am.getAsc();
				am.setLocation(iabLeft, yloc);
				yloc -= am.getDesc();
			}
		}

		// locate legend
		LegendEx legend = sp.getLegend();
		if (legend.canContribute()) {
			yloc -= LEGEND_GAP;
			double x;
			HAlign halign;
			switch (legend.getPosition()) {
			case BOTTOMLEFT:
				x = contentBox.getMinX();
				halign = HAlign.LEFT;
				break;
			case BOTTOMCENTER:
				x = contentBox.getCenterX();
				halign = HAlign.CENTER;
				break;
			case BOTTOMRIGHT:
				x = contentBox.getMaxX();
				halign = HAlign.RIGHT;
				break;
			default:
				return;
			}
			legend.setLocation(x, yloc);
			legend.setHAlign(halign);
			legend.setVAlign(VAlign.TOP);
		}
	}

	/**
	 * Calculate the preferred content size of the given plot. The default
	 * implementation returns the plot.getPreferredContentSize(), which can be
	 * override by subclass to consider nested subplots. The returned size may
	 * be <code>null</code> if there is no enough information to derive a value.
	 * 
	 * @param plot
	 * @return the preferred content size
	 */
	public Dimension2D getPreferredContentSize(PlotEx plot) {
		return plot.getPreferredContentSize();
	}

	public Dimension2D getPreferredSize(PlotEx plot) {
		Dimension2D prefContSize = getPreferredContentSize(plot);
		if (prefContSize == null) {
			return null;
		} else {
			AxesInPlot ais = getAllAxes(plot);
			Insets2D margin = calcMargin(plot, ais);
			double w = prefContSize.getWidth() + margin.getLeft()
					+ margin.getRight();
			double h = prefContSize.getHeight() + margin.getTop()
					+ margin.getBottom();
			return new DoubleDimension2D(w, h);
		}
	}

}
