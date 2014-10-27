/**
 * Copyright 2010-2014 Jingjing Li.
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

import org.jplot2d.util.SparseDoubleArray;

public class GridCellGeom {

	private final SparseDoubleArray colWidth, rowHeight;

	private final double sumWidth, sumHeight;

	public GridCellGeom(SparseDoubleArray colWidth, SparseDoubleArray rowHeight) {
		this.colWidth = colWidth;
		this.rowHeight = rowHeight;
		double sw = 0, sh = 0;
		for (int i = 0; i < colWidth.size(); i++) {
			double w = colWidth.valueAt(i);
			sw += w;
		}
		for (int i = 0; i < rowHeight.size(); i++) {
			double h = rowHeight.valueAt(i);
			sh += h;
		}
		sumWidth = sw;
		sumHeight = sh;
	}

	public double getWidth(int col) {
		return colWidth.get(col);
	}

	public double getHeight(int row) {
		return rowHeight.get(row);
	}

	/**
	 * Returns sum of width of all grid cells on left of the given column
	 * 
	 * @param col
	 * @return the sum width
	 */
	public double getSumWidthLeft(int col) {
		double sum = 0;
		for (int i = 0; i < colWidth.size(); i++) {
			int c = colWidth.keyAt(i);
			double v = colWidth.valueAt(i);
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
	 *            the column number form bottom to top
	 * @return the sum height
	 */
	public double getSumHeightBelow(int row) {
		double sum = 0;
		for (int i = 0; i < rowHeight.size(); i++) {
			int r = rowHeight.keyAt(i);
			double v = rowHeight.valueAt(i);
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

}