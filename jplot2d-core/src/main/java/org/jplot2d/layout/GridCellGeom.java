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

import java.util.Map;
import java.util.SortedMap;

public class GridCellGeom {

	private final Map<Integer, Double> colWidth, rowHeight;

	private final double sumWidth, sumHeight;

	public GridCellGeom(Map<Integer, Double> colWidth, Map<Integer, Double> rowHeight) {
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

	/**
	 * Returns sum of width of all grid cells on left of the given column
	 * 
	 * @param col
	 * @return the sum width
	 */
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

	/**
	 * Returns sum of height of all grid cells on left of the given column
	 * 
	 * @param col
	 * @return the sum height
	 */
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

	public boolean approxEquals(GridCellGeom b) {
		if (getColNum() != b.getColNum() || getRowNum() != b.getRowNum()) {
			return false;
		}
		if (getSumWidth() != b.getSumWidth() || getSumHeight() != b.getSumHeight()) {
			return false;
		}
		for (Integer col : colWidth.keySet()) {
			Double aw = colWidth.get(col);
			Double bw = b.colWidth.get(col);
			if ((bw == null) || !GridLayoutDirector.approximate(aw, bw)) {
				return false;
			}
		}
		for (Integer row : rowHeight.keySet()) {
			Double ah = rowHeight.get(row);
			Double bh = b.rowHeight.get(row);
			if ((bh == null) || !GridLayoutDirector.approximate(ah, bh)) {
				return false;
			}
		}
		return true;
	}

}