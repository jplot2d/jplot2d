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

import java.awt.Dimension;
import java.awt.geom.Dimension2D;
import java.awt.geom.Rectangle2D;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import org.jplot2d.element.impl.SubplotEx;
import org.jplot2d.util.DoubleDimension2D;
import org.jplot2d.util.Insets2D;

/**
 * The space are distributed to subplots according to ratio of
 * preferedContentSize.
 * 
 * @author Jingjing Li
 * 
 */
public class GridLayoutDirector extends SimpleLayoutDirector {

	private final double hgap = 0;

	private final double vgap = 0;

	protected static Map<SubplotEx, AxesInSubplot> getSubplotAxisMap(
			SubplotEx[] subplots) {

		Map<SubplotEx, AxesInSubplot> glaMap = new HashMap<SubplotEx, AxesInSubplot>();

		/*
		 * Gather metrics for every axis. Iterate over grid to ensure the axes'
		 * z-order.
		 */
		for (SubplotEx subplot : subplots) {
			AxesInSubplot ais = getAllAxes(subplot);
			glaMap.put(subplot, ais);
		}

		return glaMap;
	}

	/**
	 * calculate max padding (from grid bounds to data area bounds)
	 * 
	 * @return the max padding
	 */
	protected GridCellInsets calcMaxPadding(Map<SubplotEx, AxesInSubplot> saMap) {

		Map<Integer, Double> topPadding = new HashMap<Integer, Double>();
		Map<Integer, Double> leftPadding = new HashMap<Integer, Double>();
		Map<Integer, Double> bottomPadding = new HashMap<Integer, Double>();
		Map<Integer, Double> rightPadding = new HashMap<Integer, Double>();

		for (Map.Entry<SubplotEx, AxesInSubplot> me : saMap.entrySet()) {
			SubplotEx sp = me.getKey();
			AxesInSubplot ais = me.getValue();
			SubPlotGridConstraints grid = (SubPlotGridConstraints) getConstraint(sp);
			int col = (grid == null) ? 0 : grid.getGridX();
			int row = (grid == null) ? 0 : grid.getGridY();

			Insets2D padding = calcMargin(sp.getMargin(), ais);

			if (leftPadding.get(col) == null
					|| leftPadding.get(col) < padding.getLeft()) {
				leftPadding.put(col, padding.getLeft());
			}
			if (rightPadding.get(col) == null
					|| rightPadding.get(col) < padding.getRight()) {
				rightPadding.put(col, padding.getRight());
			}
			if (topPadding.get(row) == null
					|| topPadding.get(row) < padding.getTop()) {
				topPadding.put(row, padding.getTop());
			}
			if (bottomPadding.get(row) == null
					|| bottomPadding.get(row) < padding.getBottom()) {
				bottomPadding.put(row, padding.getBottom());
			}
		}
		return new GridCellInsets(topPadding, leftPadding, bottomPadding,
				rightPadding);
	}

	/**
	 * Calculate cell geometry by adding plot size and cell padding.
	 * 
	 * @param subplots
	 * @param plotSize
	 * @param padding
	 * @return
	 */
	protected GridCellGeom calcCellGeom(SubplotEx[] subplots,
			Dimension2D plotSize, GridCellInsets padding) {
		TreeMap<Integer, Double> cellWidthMap = new TreeMap<Integer, Double>();
		TreeMap<Integer, Double> cellHeightMap = new TreeMap<Integer, Double>();

		for (SubplotEx sp : subplots) {
			SubPlotGridConstraints grid = (SubPlotGridConstraints) getConstraint(sp);
			int col = (grid == null) ? 0 : grid.getGridX();
			int row = (grid == null) ? 0 : grid.getGridY();
			double weightx = 0;
			double weighty = 0;
			if (sp.getPreferredContentSize() != null) {
				weightx = sp.getPreferredContentSize().getWidth();
				weighty = sp.getPreferredContentSize().getHeight();
			}

			double cw = padding.getLeft(col) + plotSize.getWidth() * weightx
					+ padding.getRight(col);
			double ch = padding.getTop(row) + plotSize.getHeight() * weighty
					+ padding.getBottom(row);

			if (cellWidthMap.get(col) == null || cellWidthMap.get(col) < cw) {
				cellWidthMap.put(col, cw);
			}
			if (cellHeightMap.get(row) == null || cellHeightMap.get(row) < ch) {
				cellHeightMap.put(row, ch);
			}
		}

		return new GridCellGeom(cellWidthMap, cellHeightMap);
	}

	/**
	 * Locate every subplot by the bounds and scale information of
	 * PlotLayoutDirector.
	 * <p>
	 * Note: we cannot locate the bounds only, the scale must be set to new
	 * created subplot.
	 * 
	 * @param subplots
	 * @param pld
	 */
	protected void locateSubPlots(SubplotEx[] subplots, Rectangle2D ctsBounds,
			GridCellGeom cellGeom) {
		for (SubplotEx sp : subplots) {
			Rectangle2D sbp = getSubPlotBounds(sp, ctsBounds, cellGeom);
			sp.setLocation(sbp.getX(), sbp.getY());
			sp.setSize(sbp.getWidth(), sbp.getHeight());
		}
	}

	/**
	 * Returns the physical bounds of the given grid.
	 * 
	 * @param subplot
	 * @return the physical bounds
	 */
	private Rectangle2D getSubPlotBounds(SubplotEx subplot,
			Rectangle2D gcBounds, GridCellGeom cellGeom) {
		SubPlotGridConstraints gc = (SubPlotGridConstraints) getConstraint(subplot);
		if (gc == null) {
			gc = new SubPlotGridConstraints(0, 0);
		}
		// the physical bottom-left of a grid in chart(root layer)
		double cws = cellGeom.getSumWidthLeft(gc.getGridX());
		double chs = cellGeom.getSumHeightTop(gc.getGridY());
		double pX = gcBounds.getX() + cws + gc.getGridX() * hgap;
		double pY = gcBounds.getY() + gcBounds.getHeight() - chs
				- cellGeom.getHeight(gc.getGridY()) - gc.getGridY() * vgap;

		return new Rectangle2D.Double(pX, pY, cellGeom.getWidth(gc.getGridX()),
				cellGeom.getHeight(gc.getGridY()));

	}

	/**
	 * Locate sub-elements in a subplot
	 * 
	 * @param subplots
	 *            all subplots in grid
	 * @param cscSize
	 *            the common subplots content size
	 * @param padding
	 */
	protected void layoutSubPlots(SubplotEx[] subplots, Dimension2D cscSize,
			GridCellInsets padding) {

		for (SubplotEx sp : subplots) {
			SubPlotGridConstraints grid = (SubPlotGridConstraints) getConstraint(sp);
			int col = (grid == null) ? 0 : grid.getGridX();
			int row = (grid == null) ? 0 : grid.getGridY();
			double weightx = 0;
			double weighty = 0;
			if (sp.getPreferredContentSize() != null) {
				weightx = sp.getPreferredContentSize().getWidth();
				weighty = sp.getPreferredContentSize().getHeight();
			}

			double portWidth = cscSize.getWidth() * weightx;
			double portHeight = cscSize.getHeight() * weighty;

			/*
			 * impose viewport constraint on its children.
			 */
			sp.setContentConstrant(new Rectangle2D.Double(padding.getLeft(col),
					padding.getBottom(row), portWidth, portHeight));

		}
	}

	public void layout(SubplotEx subplot) {

		super.layout(subplot);

		Rectangle2D cbnds = subplot.getContentBounds();

		SubplotEx[] subplots = subplot.getSubplots();
		Map<SubplotEx, AxesInSubplot> glaMap = getSubplotAxisMap(subplots);

		GridCellInsets padding = calcMaxPadding(glaMap);

		Dimension2D ctsSize = new DoubleDimension2D(cbnds.getWidth(),
				cbnds.getHeight());

		// calculate contents size factor of subplots.
		Dimension2D scsf = calcSubplotContentFactor(subplots, ctsSize, padding);
		layoutSubPlots(subplots, scsf, padding);

		GridCellGeom cellGeom = calcCellGeom(subplots, scsf, padding);
		locateSubPlots(subplots, cbnds, cellGeom);

	}

	/**
	 * calculate cell size by contents size, grid size and gap.
	 * 
	 * @param ctsSize
	 * @param gridSize
	 * @param padding
	 * @param hgap
	 * @param vgap
	 * @return contents size factor
	 */
	private Dimension2D calcSubplotContentFactor(SubplotEx[] subplots,
			Dimension2D ctsSize, GridCellInsets padding) {

		Map<Integer, Double> weightxMap = new HashMap<Integer, Double>();
		Map<Integer, Double> weightyMap = new HashMap<Integer, Double>();
		for (SubplotEx sp : subplots) {
			SubPlotGridConstraints grid = (SubPlotGridConstraints) getConstraint(sp);
			int col = (grid == null) ? 0 : grid.getGridX();
			int row = (grid == null) ? 0 : grid.getGridY();
			double weightx = 0;
			double weighty = 0;
			if (sp.getPreferredContentSize() != null) {
				weightx = sp.getPreferredContentSize().getWidth();
				weighty = sp.getPreferredContentSize().getHeight();
			}
			if (weightxMap.get(col) == null || weightxMap.get(col) < weightx) {
				weightxMap.put(col, weightx);
			}
			if (weightyMap.get(row) == null || weightyMap.get(row) < weighty) {
				weightyMap.put(row, weighty);
			}
		}
		double sumWeightX = 0;
		double sumWeightY = 0;
		for (Double wx : weightxMap.values()) {
			sumWeightX += wx;
		}
		for (Double wy : weightyMap.values()) {
			sumWeightY += wy;
		}
		Dimension gridDim = new Dimension(weightxMap.size(), weightyMap.size());

		double sumXpad = padding.getSumWidth();
		double sumYpad = padding.getSumHeight();

		double plotWidth, plotHeight;
		if (gridDim.width == 0 || gridDim.height == 0) {
			plotWidth = ctsSize.getWidth();
			plotHeight = ctsSize.getHeight();
		} else {
			plotWidth = (ctsSize.getWidth() - sumXpad - hgap
					* (gridDim.width - 1))
					/ sumWeightX;
			plotHeight = (ctsSize.getHeight() - sumYpad - vgap
					* (gridDim.height - 1))
					/ sumWeightY;
		}

		return new DoubleDimension2D(plotWidth, plotHeight);
	}

}
