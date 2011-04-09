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

import java.util.Collections;
import java.util.Map;

public class GridCellInsets {

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
	public GridCellInsets(Map<Integer, Double> topPadding,
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

	public GridCellInsets() {
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

	public boolean approxEquals(GridCellInsets b) {
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
			if ((bw == null) || !GridLayoutDirector.approximate(aw, bw)) {
				return false;
			}
		}
		for (Integer col : rightPadding.keySet()) {
			Double aw = rightPadding.get(col);
			Double bw = b.rightPadding.get(col);
			if ((bw == null) || !GridLayoutDirector.approximate(aw, bw)) {
				return false;
			}
		}
		for (Integer row : topPadding.keySet()) {
			Double ah = topPadding.get(row);
			Double bh = b.topPadding.get(row);
			if ((bh == null) || !GridLayoutDirector.approximate(ah, bh)) {
				return false;
			}
		}
		for (Integer row : bottomPadding.keySet()) {
			Double ah = bottomPadding.get(row);
			Double bh = b.bottomPadding.get(row);
			if ((bh == null) || !GridLayoutDirector.approximate(ah, bh)) {
				return false;
			}
		}
		return true;
	}

}