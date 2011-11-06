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
package org.jplot2d.data;

import org.jplot2d.util.Range;

/**
 * Immutable. This class keep (x,y) data pairs and compute data feature such as max/min, NaN
 * indexes.
 * 
 * @author Jingjing Li
 */
public final class XYGraph implements Graph {

	/** the default capacity for idx lists */
	private static final int DEFAULT_CAPACITY = 4;

	private final ArrayPair _xy;

	private final ArrayPair _errorX, _errorY;

	/** A pair of arrays to store NaN data range */
	private int[] _rsIdxes, _reIdxes;

	/** the last index in the array of NaN data ranges */
	private int _nraLastIdx;

	private int _nanCount;

	private boolean xPositiveInfinity;

	private boolean xNegativeInfinity;

	private boolean yPositiveInfinity;

	private boolean yNegativeInfinity;

	private double _xmin = Double.NaN;

	private double _xmax = Double.NaN;

	private double _ymin = Double.NaN;

	private double _ymax = Double.NaN;

	private Range _xboundary;

	private Range _yboundary;

	private boolean hasPointOutsideXBounds, hasPointOutsideYBounds;

	public XYGraph(ArrayPair xy) {
		this(xy, null, null);
	}

	public XYGraph(ArrayPair xy, ArrayPair errorX, ArrayPair errorY) {
		this(xy, errorX, errorY, null, null);
	}

	public XYGraph(ArrayPair xy, ArrayPair errorX, ArrayPair errorY, Range xboundary,
			Range yboundary) {
		_xy = xy;
		_errorX = errorX;
		_errorY = errorY;
		_xboundary = xboundary;
		_yboundary = yboundary;
		extractDataFeature();
	}

	public XYGraph setXBoundary(Range xboundary) {
		return setBoundary(xboundary, null);
	}

	public XYGraph setYBoundary(Range yboundary) {
		return setBoundary(null, yboundary);
	}

	public XYGraph setBoundary(Range xboundary, Range yboundary) {
		return new XYGraph(_xy, _errorX, _errorY, xboundary, yboundary);
	}

	/**
	 * Extracts the data feature form the XY array.
	 * 
	 */
	private final void extractDataFeature() {
		_rsIdxes = new int[DEFAULT_CAPACITY];
		_reIdxes = new int[DEFAULT_CAPACITY];
		_nraLastIdx = -1;

		for (int i = 0; i < _xy.size(); i++) {
			double x = getX(i);
			double y = getY(i);
			if (Double.isNaN(x) || Double.isNaN(y)) {
				addNaNIdx(i);
			} else if (x == Double.POSITIVE_INFINITY) {
				xPositiveInfinity = true;
			} else if (x == Double.NEGATIVE_INFINITY) {
				xNegativeInfinity = true;
			} else if (y == Double.POSITIVE_INFINITY) {
				yPositiveInfinity = true;
			} else if (y == Double.NEGATIVE_INFINITY) {
				yNegativeInfinity = true;
			} else {
				double xlow = x, xhigh = x, ylow = y, yhigh = y;
				if (_errorX != null && i < _errorX.size()) {
					double xel = getXErrorLow(i);
					double xeh = getXErrorHigh(i);
					if (!Double.isNaN(xel) && !Double.isInfinite(xel)) {
						double xelow = x - xel;
						if (xlow > xelow) {
							xlow = xelow;
						}
						if (xhigh < xelow) {
							xhigh = xelow;
						}
					}
					if (!Double.isNaN(xeh) && !Double.isInfinite(xeh)) {
						double xehigh = x + xeh;
						if (xlow > xehigh) {
							xlow = xehigh;
						}
						if (xhigh < xehigh) {
							xhigh = xehigh;
						}
					}
				}
				if (_errorY != null && i < _errorY.size()) {
					double yel = getYErrorLow(i);
					double yeh = getYErrorHigh(i);
					if (!Double.isNaN(yel) && !Double.isInfinite(yel)) {
						double yelow = y - yel;
						if (ylow > yelow) {
							ylow = yelow;
						}
						if (yhigh < yelow) {
							yhigh = yelow;
						}
					}
					if (!Double.isNaN(yeh) && !Double.isInfinite(yeh)) {
						double yehigh = y + yeh;
						if (ylow > yehigh) {
							ylow = yehigh;
						}
						if (yhigh < yehigh) {
							yhigh = yehigh;
						}
					}
				}

				if (inXBoundary(x) && inYBoundary(y)) {
					if (inXBoundary(xlow)) {
						if (!(_xmin <= xlow)) {
							_xmin = xlow;
						}
					} else {
						if (!(_xmin <= x)) {
							_xmin = x;
						}
					}
					if (inXBoundary(xhigh)) {
						if (!(_xmax >= xhigh)) {
							_xmax = xhigh;
						}
					} else {
						if (!(_xmax >= x)) {
							_xmax = x;
						}
					}
					if (inYBoundary(ylow)) {
						if (!(_ymin <= ylow)) {
							_ymin = ylow;
						}
					} else {
						if (!(_ymin <= y)) {
							_ymin = y;
						}
					}
					if (inYBoundary(yhigh)) {
						if (!(_ymax >= yhigh)) {
							_ymax = yhigh;
						}
					} else {
						if (!(_ymax >= y)) {
							_ymax = y;
						}
					}
				} else {
					if (!inXBoundary(x)) {
						hasPointOutsideXBounds = true;
					}
					if (!inYBoundary(y)) {
						hasPointOutsideYBounds = true;
					}
				}
			}
		}
	}

	/**
	 * Returns <code>true</code> if the given x is in the x boundary
	 */
	private boolean inXBoundary(double x) {
		return (_xboundary == null) ? true : _xboundary.contains(x);
	}

	/**
	 * Returns <code>true</code> if the given y is in the y boundary
	 */
	private boolean inYBoundary(double y) {
		return (_yboundary == null) ? true : _yboundary.contains(y);
	}

	private void addNaNIdx(int idx) {
		_nanCount++;
		if (_nraLastIdx == -1) { // first idx
			_nraLastIdx = 0;
			_rsIdxes[_nraLastIdx] = _reIdxes[_nraLastIdx] = idx;
		} else if (idx == _reIdxes[_nraLastIdx] + 1) { // continuous idx
			_reIdxes[_nraLastIdx] = idx;
		} else { // new range
			_nraLastIdx++;

			/* ensure capacity */
			if (_nraLastIdx == _rsIdxes.length) {
				int[] rstmp = new int[_rsIdxes.length * 2];
				System.arraycopy(_rsIdxes, 0, rstmp, 0, _rsIdxes.length);
				_rsIdxes = rstmp;
				int[] retmp = new int[_reIdxes.length * 2];
				System.arraycopy(_reIdxes, 0, retmp, 0, _reIdxes.length);
				_reIdxes = retmp;
			}

			_rsIdxes[_nraLastIdx] = _reIdxes[_nraLastIdx] = idx;
		}
	}

	/**
	 * Returns a new LineData that contains the appended points.
	 * 
	 * @param xy
	 * @return a new LineData object.
	 */
	public XYGraph addPoints(ArrayPair xy) {
		if (xy != null) {
			XYGraph nld = new XYGraph(_xy.append(xy), _errorX, _errorY);
			return nld;
		}
		return this;
	}

	public ArrayPair getXy() {
		return _xy;
	}

	/**
	 * Get the X coordinate array.
	 */
	public double getX(int idx) {
		return _xy.getPDouble(idx);
	}

	/**
	 * Get the Y coordinate array.
	 */
	public double getY(int idx) {
		return _xy.getQDouble(idx);
	}

	public Range getXRange() {
		return new Range.Double(_xmin, _xmax);
	}

	public Range getYRange() {
		return new Range.Double(_ymin, _ymax);
	}

	public boolean hasPointOutsideXBounds() {
		return hasPointOutsideXBounds;
	}

	public boolean hasPointOutsideYBounds() {
		return hasPointOutsideYBounds;
	}

	/**
	 * Returns if there is a infinity value in x array.
	 * 
	 * @return if there is a infinity value in x array.
	 */
	public boolean isInfiniteX() {
		return xPositiveInfinity || xNegativeInfinity;
	}

	/**
	 * Returns if there is a infinity value in x array.
	 * 
	 * @return if there is a infinity value in x array.
	 */
	public boolean isInfiniteY() {
		return yPositiveInfinity || yNegativeInfinity;
	}

	/**
	 * Returns the array of indexes of non-real values.
	 * 
	 * @return the array of indexes.
	 */
	public int[][] getNaNIndexes() {
		int[][] result = new int[2][_nraLastIdx + 1];
		System.arraycopy(_rsIdxes, 0, result[0], 0, _nraLastIdx + 1);
		System.arraycopy(_reIdxes, 0, result[1], 0, _nraLastIdx + 1);
		return result;
	}

	public int getNaNIndexesCount() {
		return _nanCount;
	}

	/**
	 * Returns the number of elements in this line data.
	 * 
	 * @return the number of elements in this line data.
	 */
	public int size() {
		return _xy.size();
	}

	public ArrayPair getXError() {
		return _errorX;
	}

	public ArrayPair getYError() {
		return _errorY;
	}

	public double getXErrorLow(int idx) {
		return _errorX.getPDouble(idx);
	}

	public double getXErrorHigh(int idx) {
		return _errorX.getQDouble(idx);
	}

	public double getYErrorLow(int idx) {
		return _errorY.getPDouble(idx);
	}

	public double getYErrorHigh(int idx) {
		return _errorY.getQDouble(idx);
	}

}
