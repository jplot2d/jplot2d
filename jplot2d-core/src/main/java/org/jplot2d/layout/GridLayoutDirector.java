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
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import org.jplot2d.element.impl.SubplotEx;
import org.jplot2d.util.DoubleDimension2D;
import org.jplot2d.util.Insets2D;

/**
 * The space are distributed to subplots according to ratio of their
 * preferedContentSize.
 * 
 * @author Jingjing Li
 * 
 */
public class GridLayoutDirector extends SimpleLayoutDirector {

	private final double hgap = 0;

	private final double vgap = 0;

	private Map<SubplotEx, GridCellGeom> subplotsPreferredContentsGeomMap = new HashMap<SubplotEx, GridCellGeom>();

	private Map<SubplotEx, GridCellInsets> subplotsMarginGeomMap = new HashMap<SubplotEx, GridCellInsets>();

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

	public void invalidateLayout(SubplotEx subplot) {
		subplotsPreferredContentsGeomMap.remove(subplot);
		subplotsMarginGeomMap.remove(subplot);
	}

	public void layout(SubplotEx subplot) {

		super.layout(subplot);

		GridCellGeom contGeom = getSubplotsPreferredContentsGeom(subplot);
		GridCellInsets marginGeom = getSubplotsMarginGeom(subplot);

		// calculate contents size factor of subplots.
		Dimension2D scsFactor = calcSubplotContentFactor(subplot, marginGeom);

		TreeMap<Integer, Double> cellWidthMap = new TreeMap<Integer, Double>();
		TreeMap<Integer, Double> cellHeightMap = new TreeMap<Integer, Double>();

		for (SubplotEx sp : subplot.getSubplots()) {
			GridConstraint grid = (GridConstraint) getConstraint(sp);
			int col = (grid == null) ? 0 : grid.getGridX();
			int row = (grid == null) ? 0 : grid.getGridY();

			double contWidth = scsFactor.getWidth() * contGeom.getWidth(col);
			double contHeight = scsFactor.getHeight() * contGeom.getHeight(row);

			/*
			 * impose viewport constraint on its children.
			 */
			sp.setContentConstrant(new Rectangle2D.Double(marginGeom
					.getLeft(col), marginGeom.getBottom(row), contWidth,
					contHeight));

			double cw = marginGeom.getLeft(col) + contWidth
					+ marginGeom.getRight(col);
			double ch = marginGeom.getTop(row) + contHeight
					+ marginGeom.getBottom(row);

			if (cellWidthMap.get(col) == null || cellWidthMap.get(col) < cw) {
				cellWidthMap.put(col, cw);
			}
			if (cellHeightMap.get(row) == null || cellHeightMap.get(row) < ch) {
				cellHeightMap.put(row, ch);
			}
		}

		GridCellGeom cellGeom = new GridCellGeom(cellWidthMap, cellHeightMap);

		Rectangle2D cbnds = subplot.getContentBounds();
		for (SubplotEx sp : subplot.getSubplots()) {
			GridConstraint grid = (GridConstraint) getConstraint(sp);
			int col = (grid == null) ? 0 : grid.getGridX();
			int row = (grid == null) ? 0 : grid.getGridY();

			// the physical bottom-left of a grid in chart(root layer)
			double cws = cellGeom.getSumWidthLeft(col);
			double chs = cellGeom.getSumHeightTop(row);
			double pX = cbnds.getX() + cws + col * hgap;
			double pY = cbnds.getY() + cbnds.getHeight() - chs
					- cellGeom.getHeight(row) - row * vgap;

			sp.setLocation(pX, pY);
			sp.setSize(cellGeom.getWidth(col), cellGeom.getHeight(row));
		}

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
	private Dimension2D calcSubplotContentFactor(SubplotEx subplot,
			GridCellInsets padding) {

		double ctsWidth = subplot.getContentBounds().getWidth();
		double ctsHeight = subplot.getContentBounds().getHeight();

		GridCellGeom contGeom = getSubplotsPreferredContentsGeom(subplot);

		double sumWeightX = contGeom.getSumWidth();
		double sumWeightY = contGeom.getSumHeight();

		double xn = contGeom.getColNum();
		double yn = contGeom.getRowNum();

		double sumXpad = padding.getSumWidth();
		double sumYpad = padding.getSumHeight();

		double xfactor, yfactor;
		if (xn == 0 || yn == 0) {
			xfactor = ctsWidth;
			yfactor = ctsHeight;
		} else {
			xfactor = (ctsWidth - sumXpad - hgap * (xn - 1)) / sumWeightX;
			yfactor = (ctsHeight - sumYpad - vgap * (yn - 1)) / sumWeightY;
		}

		return new DoubleDimension2D(xfactor, yfactor);
	}

	public Dimension2D getPreferredContentSize(SubplotEx subplot) {
		GridCellGeom contentGeom = getSubplotsPreferredContentsGeom(subplot);
		GridCellInsets marginGeom = getSubplotsMarginGeom(subplot);

		Map<Integer, Double> colWidthMap = new HashMap<Integer, Double>();
		Map<Integer, Double> rowHeightMap = new HashMap<Integer, Double>();
		for (SubplotEx sp : subplot.getSubplots()) {
			GridConstraint grid = (GridConstraint) getConstraint(sp);
			int col = (grid == null) ? 0 : grid.getGridX();
			int row = (grid == null) ? 0 : grid.getGridY();
			double width = contentGeom.getWidth(col) + marginGeom.getLeft(col)
					+ marginGeom.getRight(col);
			double Height = contentGeom.getHeight(row) + marginGeom.getTop(row)
					+ marginGeom.getBottom(row);
			if (colWidthMap.get(col) == null || colWidthMap.get(col) < width) {
				colWidthMap.put(col, width);
			}
			if (rowHeightMap.get(row) == null || rowHeightMap.get(row) < Height) {
				rowHeightMap.put(row, Height);
			}
		}

		GridCellGeom geom = new GridCellGeom(colWidthMap, rowHeightMap);
		double width = geom.getSumWidth();
		double height = geom.getSumHeight();
		Dimension2D spcs = subplot.getPreferredContentSize();
		if (spcs != null && width < spcs.getWidth()) {
			width = spcs.getWidth();
		}
		if (spcs != null && height < spcs.getHeight()) {
			height = spcs.getHeight();
		}
		return new DoubleDimension2D(width, height);
	}

	/**
	 * Returns row width and column height of the subplots of the given subplot.
	 * 
	 * @param subplot
	 * @return
	 */
	private GridCellGeom getSubplotsPreferredContentsGeom(SubplotEx subplot) {
		if (subplotsPreferredContentsGeomMap.containsKey(subplot)) {
			return subplotsPreferredContentsGeomMap.get(subplot);
		}

		Map<Integer, Double> colWidthMap = new HashMap<Integer, Double>();
		Map<Integer, Double> rowHeightMap = new HashMap<Integer, Double>();
		for (SubplotEx sp : subplot.getSubplots()) {
			GridConstraint grid = (GridConstraint) getConstraint(sp);
			int col = (grid == null) ? 0 : grid.getGridX();
			int row = (grid == null) ? 0 : grid.getGridY();
			double width = 0;
			double Height = 0;
			Dimension2D contentSize = sp.getLayoutDirector()
					.getPreferredContentSize(sp);
			if (contentSize != null) {
				width = contentSize.getWidth();
				Height = contentSize.getHeight();
			}
			if (colWidthMap.get(col) == null || colWidthMap.get(col) < width) {
				colWidthMap.put(col, width);
			}
			if (rowHeightMap.get(row) == null || rowHeightMap.get(row) < Height) {
				rowHeightMap.put(row, Height);
			}
		}

		GridCellGeom geom = new GridCellGeom(colWidthMap, rowHeightMap);
		subplotsPreferredContentsGeomMap.put(subplot, geom);
		return geom;
	}

	/**
	 * calculate the max margin (from grid bounds to data area bounds)
	 * 
	 * @return the max margin
	 */
	private GridCellInsets getSubplotsMarginGeom(SubplotEx subplot) {
		if (subplotsMarginGeomMap.containsKey(subplot)) {
			return subplotsMarginGeomMap.get(subplot);
		}

		Map<SubplotEx, AxesInSubplot> saMap = getSubplotAxisMap(subplot
				.getSubplots());

		Map<Integer, Double> topPadding = new HashMap<Integer, Double>();
		Map<Integer, Double> leftPadding = new HashMap<Integer, Double>();
		Map<Integer, Double> bottomPadding = new HashMap<Integer, Double>();
		Map<Integer, Double> rightPadding = new HashMap<Integer, Double>();

		for (Map.Entry<SubplotEx, AxesInSubplot> me : saMap.entrySet()) {
			SubplotEx sp = me.getKey();
			AxesInSubplot ais = me.getValue();
			GridConstraint grid = (GridConstraint) getConstraint(sp);
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

		GridCellInsets insets = new GridCellInsets(topPadding, leftPadding,
				bottomPadding, rightPadding);
		subplotsMarginGeomMap.put(subplot, insets);
		return insets;
	}

}
