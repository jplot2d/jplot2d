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
import java.util.HashMap;
import java.util.Map;

import org.jplot2d.element.impl.PlotEx;
import org.jplot2d.element.impl.SubplotEx;
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
	private Map<SubplotEx, Object> constraints = new HashMap<SubplotEx, Object>();

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

	private PlotEx plot;

	static boolean approximate(double a, double b) {
		return NumberUtils.approximate(a, b, 4);
	}

	public GridLayoutDirector(PlotEx plot) {
		this.plot = plot;
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

		/*
		 * Laying out axes may register some axis that ticks need be
		 * re-calculated
		 */
		execLayout();

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
	private void execLayout() {

		if (plot.isValid()) {
			Log.layout.fine("[Y] nothing to do.");
			return;
		}

		/* layout */
		switch (plot.getSizeMode()) {
		case FIT_CONTAINER_SIZE:
		case FIT_CONTAINER_WITH_TARGET_SIZE:
		case FIXED_SIZE:
			// PlotGridLayoutStrategyC2P.getInstance().doLayout(this, _cflags);
			break;
		case FIT_CONTENTS:
			// PlotGridLayoutStrategyP2C.getInstance().doLayout(this, _cflags);
			break;
		}

	}

}
