/*
 * This file is part of Herschel Common Science System (HCSS).
 * Copyright 2001-2010 Herschel Science Ground Segment Consortium
 *
 * HCSS is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of
 * the License, or (at your option) any later version.
 *
 * HCSS is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General
 * Public License along with HCSS.
 * If not, see <http://www.gnu.org/licenses/>.
 */
/*
 * $Id: PlotGridLayoutDirector.java,v 1.3 2010/02/02 10:16:49 hsclib Exp $
 */
package org.jplot2d.layout;

import java.awt.Dimension;
import java.awt.geom.Dimension2D;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;

import org.jplot2d.element.Plot;
import org.jplot2d.element.SubPlot;
import org.jplot2d.util.DoubleDimension2D;
import org.jplot2d.util.Insets2D;
import org.jplot2d.util.NumberUtils;

/**
 * all physical size is managed by this class.
 * <p>
 * plot + axis_padding = cell<br>
 * cell * gridSize + gap = contents<br>
 * contents + _contentsMargin + titles + legend + margin = chart
 * </p>
 * 
 * @author Jingjing Li
 * 
 */
public class GridLayoutDirector implements LayoutDirector {

	public static class CellInsets {

		final Map<Integer, Double> topPadding;

		final Map<Integer, Double> leftPadding;

		final Map<Integer, Double> bottomPadding;

		final Map<Integer, Double> rightPadding;

		final double sumWidth, sumHeight;

		/**
		 * @param topPadding
		 *            the key is row id
		 * @param leftPadding
		 *            the key is column id
		 * @param bottomPadding
		 *            the key is row id
		 * @param rightPadding
		 *            the key is column id
		 */
		public CellInsets(Map<Integer, Double> topPadding,
				Map<Integer, Double> leftPadding,
				Map<Integer, Double> bottomPadding,
				Map<Integer, Double> rightPadding) {
			this.topPadding = topPadding;
			this.leftPadding = leftPadding;
			this.bottomPadding = bottomPadding;
			this.rightPadding = rightPadding;

			double sumXpad = 0;
			for (double left : leftPadding.values()) {
				sumXpad += left;
			}
			for (double right : rightPadding.values()) {
				sumXpad += right;
			}
			sumWidth = sumXpad;

			double sumYpad = 0;
			for (double top : topPadding.values()) {
				sumYpad += top;
			}
			for (double bottom : bottomPadding.values()) {
				sumYpad += bottom;
			}
			sumHeight = sumYpad;
		}

		public CellInsets() {
			this(Collections.<Integer, Double> emptyMap(), Collections
					.<Integer, Double> emptyMap(), Collections
					.<Integer, Double> emptyMap(), Collections
					.<Integer, Double> emptyMap());
		}

		public double getTop(int row) {
			Double v = topPadding.get(row);
			return (v == null) ? 0 : v.doubleValue();
		}

		public double getLeft(int col) {
			Double v = leftPadding.get(col);
			return (v == null) ? 0 : v.doubleValue();
		}

		public double getBottom(int row) {
			Double v = bottomPadding.get(row);
			return (v == null) ? 0 : v.doubleValue();
		}

		public double getRight(int col) {
			Double v = rightPadding.get(col);
			return (v == null) ? 0 : v.doubleValue();
		}

		public double getSumWidth() {
			return sumWidth;
		}

		public double getSumHeight() {
			return sumHeight;
		}

		public boolean approxEquals(CellInsets b) {
			if (topPadding.size() != b.topPadding.size()
					|| leftPadding.size() != b.leftPadding.size()
					|| bottomPadding.size() != b.bottomPadding.size()
					|| rightPadding.size() != b.rightPadding.size()) {
				return false;
			}
			if (getSumWidth() != b.getSumWidth()
					|| getSumHeight() != b.getSumHeight()) {
				return false;
			}
			for (Integer col : leftPadding.keySet()) {
				Double aw = leftPadding.get(col);
				Double bw = b.leftPadding.get(col);
				if ((bw == null) || !approximate(aw, bw)) {
					return false;
				}
			}
			for (Integer col : rightPadding.keySet()) {
				Double aw = rightPadding.get(col);
				Double bw = b.rightPadding.get(col);
				if ((bw == null) || !approximate(aw, bw)) {
					return false;
				}
			}
			for (Integer row : topPadding.keySet()) {
				Double ah = topPadding.get(row);
				Double bh = b.topPadding.get(row);
				if ((bh == null) || !approximate(ah, bh)) {
					return false;
				}
			}
			for (Integer row : bottomPadding.keySet()) {
				Double ah = bottomPadding.get(row);
				Double bh = b.bottomPadding.get(row);
				if ((bh == null) || !approximate(ah, bh)) {
					return false;
				}
			}
			return true;
		}

	}

	static class CellGeom {

		private final SortedMap<Integer, Double> colWidth, rowHeight;

		private final double sumWidth, sumHeight;

		public CellGeom(SortedMap<Integer, Double> colWidth,
				SortedMap<Integer, Double> rowHeight) {
			this.colWidth = colWidth;
			this.rowHeight = rowHeight;
			double sw = 0, sh = 0;
			for (double w : colWidth.values()) {
				sw += w;
			}
			for (double h : rowHeight.values()) {
				sh += h;
			}
			sumWidth = sw;
			sumHeight = sh;
		}

		public double getWidth(int col) {
			Double v = colWidth.get(col);
			return v.doubleValue();
		}

		public double getHeight(int row) {
			Double v = rowHeight.get(row);
			return v.doubleValue();
		}

		public double getSumWidthLeft(int col) {
			double sum = 0;
			for (SortedMap.Entry<Integer, Double> me : colWidth.entrySet()) {
				int c = me.getKey();
				double v = me.getValue();
				if (c < col) {
					sum += v;
				}
			}
			return sum;
		}

		public double getSumHeightTop(int row) {
			double sum = 0;
			for (SortedMap.Entry<Integer, Double> me : rowHeight.entrySet()) {
				int r = me.getKey();
				double v = me.getValue();
				if (r < row) {
					sum += v;
				}
			}
			return sum;
		}

		public double getSumWidth() {
			return sumWidth;
		}

		public double getSumHeight() {
			return sumHeight;
		}

		public int getRowNum() {
			return rowHeight.size();
		}

		public int getColNum() {
			return colWidth.size();
		}

		public boolean approxEquals(CellGeom b) {
			if (getColNum() != b.getColNum() || getRowNum() != b.getRowNum()) {
				return false;
			}
			if (getSumWidth() != b.getSumWidth()
					|| getSumHeight() != b.getSumHeight()) {
				return false;
			}
			for (Integer col : colWidth.keySet()) {
				Double aw = colWidth.get(col);
				Double bw = b.colWidth.get(col);
				if ((bw == null) || !approximate(aw, bw)) {
					return false;
				}
			}
			for (Integer row : rowHeight.keySet()) {
				Double ah = rowHeight.get(row);
				Double bh = b.rowHeight.get(row);
				if ((bh == null) || !approximate(ah, bh)) {
					return false;
				}
			}
			return true;
		}

	}

	class ChangeFlags {
		/**
		 * When axis added, removed, position changed, metrics changed
		 */
		boolean axisChanged;

		boolean plotSizeChanged;

		boolean subPlotConstraintsChanged;

		/**
		 * When subplot added, removed
		 */
		boolean gridChanged;

		/**
		 * When plotLayout.gap changed.
		 */
		boolean gapChanged;

		/**
		 * When plotLayout.margin changed.
		 */
		boolean marginChanged;

		/**
		 * When title, subtitle, legend changed
		 */
		boolean tslChanged;

		boolean chartSizeChanged;

		public void reset() {
			axisChanged = false;
			plotSizeChanged = false;
			subPlotConstraintsChanged = false;
			gridChanged = false;
			gapChanged = false;
			marginChanged = false;
			tslChanged = false;
			chartSizeChanged = false;
		}

		public boolean hasChanges() {
			return axisChanged || plotSizeChanged || subPlotConstraintsChanged
					|| gridChanged || gapChanged || marginChanged || tslChanged
					|| chartSizeChanged;
		}
	}

	/** The layout constraints */
	private Map<SubPlot, Object> constraints = new HashMap<SubPlot, Object>();

	/**
	 * the margin around the content
	 */
	Insets2D _contentsMargin = new Insets2D(0.1, 0.1, 0.1, 0.1);

	/**
	 * the margin around the chart
	 */
	Insets2D _margin = new Insets2D(0.1, 0.1, 0.1, 0.1);

	double _hgap = 0.1;

	double _vgap = 0.1;

	boolean _restrictedAxisBox = true;

	/**
	 * the physical size of every grid cell
	 */
	CellGeom _cellGeom;

	CellInsets _cellPadding = new CellInsets();

	private final ChangeFlags _cflags = new ChangeFlags();

	private static boolean approximate(double a, double b) {
		return NumberUtils.approximate(a, b, 4);
	}

	public GridLayoutDirector() {

	}

	public Object getConstraint(SubPlot subplot) {
		return constraints.get(subplot);
	}

	public void remove(SubPlot subplot) {
		constraints.remove(subplot);
	}

	public void setConstraint(SubPlot subplot, Object constraint) {
		constraints.put(subplot, constraint);
	}

	public void layout(Plot plot) {

		calcPendingAxesMetrics();
		while (true) {

			/*
			 * Laying out axes may register some axis that ticks need be
			 * re-calculated
			 */
			execLayout(plot);

			/*
			 * Auto range axes MUST be executed after they are laid out. <br>
			 * Auto range axes may register some axis that ticks need be
			 * re-calculated
			 */
			calcPendingLockGroupAutoRange();

			/*
			 * Calculating axes tick may register some axis that metrics need be
			 * re-calculated
			 */
			calcPendingAxesTick();

			/* axis metrics change need re-laying out the plot */
			calcPendingAxesMetrics();
			if (plot.isValid()) {
				break;
			}
		}

	}

	/**
	 * 
	 */
	private void calcPendingAxesTick() {
		// TODO Auto-generated method stub

	}

	/**
	 * 
	 */
	private void calcPendingLockGroupAutoRange() {
		// TODO Auto-generated method stub

	}

	/**
	 * 
	 */
	private void calcPendingAxesMetrics() {
		// TODO Auto-generated method stub

	}

	public Insets2D getMargin() {
		return _margin;
	}

	public void setMargin(Insets2D margin) {
		if (_margin.equals(margin)) {
			return;
		}
		_margin = margin;
		_cflags.marginChanged = true;
	}

	public Dimension2D getGap() {
		return new DoubleDimension2D(_hgap, _vgap);
	}

	public void setGap(Dimension2D gap) {
		if (_hgap == gap.getWidth() && _vgap == gap.getHeight()) {
			return;
		}
		_hgap = gap.getWidth();
		_vgap = gap.getHeight();
		_cflags.gapChanged = true;
		Log.layout.fine("[Y] layout contents request, by PlotLayout: setGap");
	}

	/**
	 * Layout the chart according to the xxxNeeded flags. Called by
	 * batchController.clearBatch()
	 * <p>
	 * Design note: To execute the laying out by invokeLater() has bad
	 * performance and is unnecessary complicated.
	 */
	public void execLayout(Plot plot) {

		if (plot.isValid()) {
			Log.layout.fine("[Y] nothing to do.");
			return;
		}

		/* layout */
		switch (plot.getSizeMode()) {
		case FIT_CONTAINER_SIZE:
			Dimension size = plot.getContainerSize();
			double scale = plot.getScale();
			plot.setPhySize(new DoubleDimension2D(size.getWidth() / scale, size
					.getHeight()
					/ scale));
		case FIT_CONTAINER_WITH_TARGET_SIZE:
		case FIXED_SIZE:
			// PlotGridLayoutStrategyC2P.getInstance().doLayout(this, _cflags);
			break;
		case FIT_CONTENTS:
			// PlotGridLayoutStrategyP2C.getInstance().doLayout(this, _cflags);
			break;
		}

		// mark validate
		plot.validate();

		_cflags.reset();
	}

}
