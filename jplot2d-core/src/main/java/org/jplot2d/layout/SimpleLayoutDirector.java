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
package org.jplot2d.layout;

import java.awt.geom.Dimension2D;
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

		Dimension2D contentSize;
		AxesInPlot ais = getAllAxes(plot);

		Insets2D margin = calcMargin(plot, ais);

		if (plot.getContentConstrant() != null) {
			contentSize = plot.getContentConstrant();
			double width = contentSize.getWidth() + margin.getLeft() + margin.getRight();
			double height = contentSize.getHeight() + margin.getTop() + margin.getBottom();
			plot.setSize(width, height);
		} else {
			double contentWidth = plot.getSize().getWidth() - margin.getLeft() - margin.getRight();
			double contentHeight = plot.getSize().getHeight() - margin.getTop() - margin.getBottom();
			if (contentWidth < PlotEx.MIN_CONTENT_SIZE.getWidth()) {
				contentWidth = PlotEx.MIN_CONTENT_SIZE.getWidth();
			}
			if (contentHeight < PlotEx.MIN_CONTENT_SIZE.getHeight()) {
				contentHeight = PlotEx.MIN_CONTENT_SIZE.getHeight();
			}
			contentSize = new DoubleDimension2D(contentWidth, contentHeight);
		}

		plot.setContentSize(contentSize);
		layoutLeftMargin(plot, contentSize, ais.leftAxes);
		layoutRightMargin(plot, contentSize, ais.rightAxes);
		layoutTopMargin(plot, contentSize, ais.topAxes);
		layoutBottomMargin(plot, contentSize, ais.bottomAxes);
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
			if (axis.isVisible() && axis.canContribute()) {
				if (axis.getPosition() == AxisPosition.POSITIVE_SIDE) {
					ais.topAxes.add(axis);
				} else {
					ais.bottomAxes.add(axis);
				}
			}
		}
		for (AxisEx axis : plot.getYAxes()) {
			if (axis.isVisible() && axis.canContribute()) {
				if (axis.getPosition() == AxisPosition.POSITIVE_SIDE) {
					ais.rightAxes.add(axis);
				} else {
					ais.leftAxes.add(axis);
				}
			}
		}

		return ais;
	}

	protected static double calcLeftMargin(PlotEx plot, ArrayList<AxisEx> leftAxes) {
		PlotMarginEx margin = plot.getMargin();

		if (!margin.isAutoLeft()) {
			return margin.getLeft() + margin.getExtraLeft();
		}

		double mLeft = 0;

		if (leftAxes.size() > 0) {
			for (AxisEx am : leftAxes) {
				mLeft += am.getAsc() + am.getDesc();
			}
			mLeft -= leftAxes.get(0).getDesc();
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

		plot.getMargin().directLeft(mLeft);

		return mLeft + margin.getExtraLeft();
	}

	protected static double calcRightMargin(PlotEx plot, ArrayList<AxisEx> rightAxes) {
		PlotMarginEx margin = plot.getMargin();

		if (!margin.isAutoRight()) {
			return margin.getRight() + margin.getExtraRight();
		}

		double mRight = 0;

		if (rightAxes.size() > 0) {
			for (AxisEx am : rightAxes) {
				mRight += am.getAsc() + am.getDesc();
			}
			mRight -= rightAxes.get(0).getAsc();
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

		plot.getMargin().directRight(mRight);

		return mRight + margin.getExtraRight();
	}

	protected static double calcTopMargin(PlotEx plot, ArrayList<AxisEx> topAxes) {
		PlotMarginEx margin = plot.getMargin();

		if (!margin.isAutoTop()) {
			return margin.getTop() + margin.getExtraTop();
		}

		double mTop = 0;

		if (topAxes.size() > 0) {
			for (AxisEx am : topAxes) {
				mTop += am.getAsc() + am.getDesc();
			}
			mTop -= topAxes.get(0).getDesc();
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
					mTop += (1 + title.getGapFactor()) * titleHeight;
					break;
				}
				}
			}
		}

		plot.getMargin().directTop(mTop);

		return mTop + margin.getExtraTop();
	}

	protected static double calcBottomMargin(PlotEx plot, ArrayList<AxisEx> bottomAxes) {
		PlotMarginEx margin = plot.getMargin();

		if (!margin.isAutoBottom()) {
			return margin.getBottom() + margin.getExtraBottom();
		}

		double mBottom = 0;

		if (bottomAxes.size() > 0) {
			for (AxisEx am : bottomAxes) {
				mBottom += am.getAsc() + am.getDesc();
			}
			mBottom -= bottomAxes.get(0).getAsc();
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
					mBottom += (1 + title.getGapFactor()) * titleHeight;
					break;
				}
				}
			}
		}

		plot.getMargin().directBottom(mBottom);

		return mBottom + margin.getExtraBottom();
	}

	/**
	 * Calculate the margin of the given plot. The legend size can be calculated by pre-setting its length constraint.
	 * 
	 * @param plot
	 * @param ais
	 * @return
	 */
	private static Insets2D calcMargin(PlotEx plot, AxesInPlot ais) {

		double mLeft, mRight, mTop, mBottom;

		mTop = calcTopMargin(plot, ais.topAxes);
		mBottom = calcBottomMargin(plot, ais.bottomAxes);
		mLeft = calcLeftMargin(plot, ais.leftAxes);
		mRight = calcRightMargin(plot, ais.rightAxes);

		return new Insets2D.Double(mTop, mLeft, mBottom, mRight);
	}

	private static void layoutLeftMargin(PlotEx sp, Dimension2D contentSize, ArrayList<AxisEx> leftAxes) {

		// viewport box
		double iabLeft = 0;
		double iabBottom = 0;

		double xloc = iabLeft;

		/* locate axes */
		if (leftAxes.size() > 0) {
			AxisEx am = leftAxes.get(0);
			am.setLength(contentSize.getHeight());
			am.setLocation(xloc, iabBottom);
			xloc -= am.getAsc();
			for (int i = 1; i < leftAxes.size(); i++) {
				am = leftAxes.get(i);
				am.setLength(contentSize.getHeight());
				xloc -= am.getDesc();
				am.setLocation(xloc, iabBottom);
				xloc -= am.getAsc();
			}
		}

		LegendEx legend = sp.getLegend();

		/* locate legend */
		if (legend.canContribute()) {
			switch (legend.getPosition()) {
			case LEFTTOP:
				xloc -= LEGEND_GAP;
				legend.directLocation(xloc, contentSize.getHeight());
				legend.setHAlign(HAlign.RIGHT);
				legend.setVAlign(VAlign.TOP);
				break;
			case LEFTMIDDLE:
				xloc -= LEGEND_GAP;
				legend.directLocation(xloc, contentSize.getHeight() / 2);
				legend.setHAlign(HAlign.RIGHT);
				legend.setVAlign(VAlign.MIDDLE);
				break;
			case LEFTBOTTOM:
				xloc -= LEGEND_GAP;
				legend.directLocation(xloc, 0);
				legend.setHAlign(HAlign.RIGHT);
				legend.setVAlign(VAlign.BOTTOM);
				break;
			}
		}

	}

	private static void layoutRightMargin(PlotEx sp, Dimension2D contentSize, ArrayList<AxisEx> rightAxes) {

		// viewport box
		double iabRight = contentSize.getWidth();
		double iabBottom = 0;

		double xloc = iabRight;

		/* locate axes */
		if (rightAxes.size() > 0) {
			AxisEx am = rightAxes.get(0);
			am.setLength(contentSize.getHeight());
			am.setLocation(xloc, iabBottom);
			xloc += am.getDesc();
			for (int i = 1; i < rightAxes.size(); i++) {
				am = rightAxes.get(i);
				am.setLength(contentSize.getHeight());
				xloc += am.getAsc();
				am.setLocation(xloc, iabBottom);
				xloc += am.getDesc();
			}
		}

		/* locate legend */
		LegendEx legend = sp.getLegend();
		if (legend.canContribute()) {
			switch (legend.getPosition()) {
			case RIGHTTOP:
				xloc += LEGEND_GAP;
				legend.directLocation(xloc, contentSize.getHeight());
				legend.setHAlign(HAlign.LEFT);
				legend.setVAlign(VAlign.TOP);
				break;
			case RIGHTMIDDLE:
				xloc += LEGEND_GAP;
				legend.directLocation(xloc, contentSize.getHeight() / 2);
				legend.setHAlign(HAlign.LEFT);
				legend.setVAlign(VAlign.MIDDLE);
				break;
			case RIGHTBOTTOM:
				xloc += LEGEND_GAP;
				legend.directLocation(xloc, 0);
				legend.setHAlign(HAlign.LEFT);
				legend.setVAlign(VAlign.BOTTOM);
				break;
			}
		}

	}

	private static void layoutTopMargin(PlotEx sp, Dimension2D contentSize, ArrayList<AxisEx> topAxes) {

		// viewport box
		double iabLeft = 0;
		double iabTop = contentSize.getHeight();

		double yloc = iabTop;

		// locate axes
		if (topAxes.size() > 0) {
			AxisEx am = topAxes.get(0);
			am.setLength(contentSize.getWidth());
			am.setLocation(iabLeft, yloc);
			yloc += am.getAsc();
			for (int i = 1; i < topAxes.size(); i++) {
				am = topAxes.get(i);
				am.setLength(contentSize.getWidth());
				yloc += am.getDesc();
				am.setLocation(iabLeft, yloc);
				yloc += am.getAsc();
			}
		}

		// locate legend
		LegendEx legend = sp.getLegend();
		if (legend.canContribute()) {
			switch (legend.getPosition()) {
			case TOPLEFT:
				yloc += LEGEND_GAP;
				legend.directLocation(0, yloc);
				legend.setHAlign(HAlign.LEFT);
				legend.setVAlign(VAlign.BOTTOM);
				yloc += legend.getSize().getHeight();
				break;
			case TOPCENTER:
				yloc += LEGEND_GAP;
				legend.directLocation(contentSize.getWidth() / 2, yloc);
				legend.setHAlign(HAlign.CENTER);
				legend.setVAlign(VAlign.BOTTOM);
				yloc += legend.getSize().getHeight();
				break;
			case TOPRIGHT:
				yloc += LEGEND_GAP;
				legend.directLocation(contentSize.getWidth(), yloc);
				legend.setHAlign(HAlign.RIGHT);
				legend.setVAlign(VAlign.BOTTOM);
				yloc += legend.getSize().getHeight();
				break;
			}
		}

		// locate titles
		TitleEx[] titles = sp.getTitles();
		for (int i = titles.length - 1; i >= 0; i--) {
			TitleEx title = titles[i];
			if (title.canContribute()) {
				double titleHeight = title.getSize().getHeight();
				switch (title.getPosition()) {
				case TOPLEFT:
					yloc += title.getGapFactor() * titleHeight;
					title.setLocation(0, yloc);
					title.setHAlign(HAlign.LEFT);
					title.setVAlign(VAlign.BOTTOM);
					yloc += titleHeight;
					break;
				case TOPCENTER:
					yloc += title.getGapFactor() * titleHeight;
					title.setLocation(contentSize.getWidth() / 2, yloc);
					title.setHAlign(HAlign.CENTER);
					title.setVAlign(VAlign.BOTTOM);
					yloc += titleHeight;
					break;
				case TOPRIGHT: {
					yloc += title.getGapFactor() * titleHeight;
					title.setLocation(contentSize.getWidth(), yloc);
					title.setHAlign(HAlign.RIGHT);
					title.setVAlign(VAlign.BOTTOM);
					yloc += titleHeight;
					break;
				}
				}
			}

		}
	}

	private static void layoutBottomMargin(PlotEx sp, Dimension2D contentSize, ArrayList<AxisEx> bottomAxes) {

		// viewport box
		double iabLeft = 0;
		double iabBottom = 0;

		double yloc = iabBottom;

		// locate axes
		if (bottomAxes.size() > 0) {
			AxisEx am = bottomAxes.get(0);
			am.setLength(contentSize.getWidth());
			am.setLocation(iabLeft, yloc);
			yloc -= am.getDesc();
			for (int i = 1; i < bottomAxes.size(); i++) {
				am = bottomAxes.get(i);
				am.setLength(contentSize.getWidth());
				yloc -= am.getAsc();
				am.setLocation(iabLeft, yloc);
				yloc -= am.getDesc();
			}
		}

		// locate legend
		LegendEx legend = sp.getLegend();
		if (legend.canContribute()) {
			switch (legend.getPosition()) {
			case BOTTOMLEFT:
				yloc -= LEGEND_GAP;
				legend.directLocation(0, yloc);
				legend.setHAlign(HAlign.LEFT);
				legend.setVAlign(VAlign.TOP);
				yloc -= legend.getSize().getHeight();
				break;
			case BOTTOMCENTER:
				yloc -= LEGEND_GAP;
				legend.directLocation(contentSize.getWidth() / 2, yloc);
				legend.setHAlign(HAlign.CENTER);
				legend.setVAlign(VAlign.TOP);
				yloc -= legend.getSize().getHeight();
				break;
			case BOTTOMRIGHT:
				yloc -= LEGEND_GAP;
				legend.directLocation(contentSize.getWidth(), yloc);
				legend.setHAlign(HAlign.RIGHT);
				legend.setVAlign(VAlign.TOP);
				yloc -= legend.getSize().getHeight();
				break;
			}
		}

		// locate titles
		for (TitleEx title : sp.getTitles()) {
			if (title.canContribute()) {
				double titleHeight = title.getSize().getHeight();
				switch (title.getPosition()) {
				case BOTTOMLEFT:
					yloc -= title.getGapFactor() * titleHeight;
					title.setLocation(0, yloc);
					title.setHAlign(HAlign.LEFT);
					title.setVAlign(VAlign.TOP);
					yloc -= titleHeight;
					break;
				case BOTTOMCENTER:
					yloc -= title.getGapFactor() * titleHeight;
					title.setLocation(contentSize.getWidth() / 2, yloc);
					title.setHAlign(HAlign.CENTER);
					title.setVAlign(VAlign.TOP);
					yloc -= titleHeight;
					break;
				case BOTTOMRIGHT: {
					yloc -= title.getGapFactor() * titleHeight;
					title.setLocation(contentSize.getWidth(), yloc);
					title.setHAlign(HAlign.RIGHT);
					title.setVAlign(VAlign.TOP);
					yloc -= titleHeight;
					break;
				}
				}
			}

		}
	}

	/**
	 * Calculate the preferred content size of the given plot. The default implementation returns the
	 * plot.getPreferredContentSize(), which can be override by subclass to consider nested subplots. The returned size
	 * may be <code>null</code> if there is no enough information to derive a value.
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
			double w = prefContSize.getWidth() + margin.getLeft() + margin.getRight();
			double h = prefContSize.getHeight() + margin.getTop() + margin.getBottom();
			return new DoubleDimension2D(w, h);
		}
	}

	public String toString() {
		return this.getClass().getSimpleName();
	}

}
