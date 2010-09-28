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
import java.util.HashMap;
import java.util.Map;

import org.jplot2d.element.Plot;
import org.jplot2d.element.Subplot;
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

	/** The layout constraints */
	private Map<Subplot, Object> constraints = new HashMap<Subplot, Object>();

	/**
	 * the margin around the content
	 */
	Insets2D contentsMargin = new Insets2D(0.1, 0.1, 0.1, 0.1);

	/**
	 * the margin around the chart
	 */
	Insets2D margin = new Insets2D(0.1, 0.1, 0.1, 0.1);

	double hgap = 0.1;

	double vgap = 0.1;

	boolean restrictedAxisBox = true;

	/**
	 * the physical size of every grid cell
	 */
	GridCellGeom cellGeom;

	GridCellInsets cellPadding = new GridCellInsets();

	private Plot plot;

	static boolean approximate(double a, double b) {
		return NumberUtils.approximate(a, b, 4);
	}

	public GridLayoutDirector(Plot plot) {
		this.plot = plot;
	}

	public Object getConstraint(Subplot subplot) {
		return constraints.get(subplot);
	}

	public void remove(Subplot subplot) {
		constraints.remove(subplot);
	}

	public void setConstraint(Subplot subplot, Object constraint) {
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
		return margin;
	}

	public void setMargin(Insets2D margin) {
		if (margin.equals(margin)) {
			return;
		}
		this.margin = margin;
		plot.invalidate();
	}

	public Dimension2D getGap() {
		return new DoubleDimension2D(hgap, vgap);
	}

	public void setGap(Dimension2D gap) {
		if (hgap == gap.getWidth() && vgap == gap.getHeight()) {
			return;
		}
		hgap = gap.getWidth();
		vgap = gap.getHeight();
		plot.invalidate();
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
			plot.setPhysicalSize(new DoubleDimension2D(size.getWidth() / scale, size
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

	}

}
