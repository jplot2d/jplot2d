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
 * $Id: MainAxisTransform.java,v 1.6 2010/02/02 10:16:48 hsclib Exp $
 */
package org.jplot2d.axtrans;

import org.jplot2d.util.NumberUtils;
import org.jplot2d.util.Range2D;

/**
 * Transform between user value and paper value.
 */
public abstract class AxisTransform extends AbstractAxisTransform {

	protected Range2D _pRange;

	protected Range2D _wRange;

	/**
	 * Create a non-transformable MainAxisTranform.
	 */
	protected AxisTransform() {
		_pRange = new Range2D.Double();
		_wRange = new Range2D.Double();
	}

	/**
	 * <code>AxisTransform</code> space constructor. This constructor is used to
	 * define transforms that use <Range2D> values.
	 * 
	 * @param pr
	 *            physical coordinate range
	 * @param wr
	 *            world coordinate range
	 */
	protected AxisTransform(Range2D pr, Range2D wr) {
		_pRange = pr;
		_wRange = wr;
		computeTransform();
	}

	public void setRangeP(Range2D prange) {
		if (!approximate(_pRange, prange)) {
			Range2D tempOld = _pRange;
			this._pRange = prange;
			computeTransform();
			_changes.firePropertyChange("rangeP", tempOld, _pRange);
		}
	}

	/**
	 * Get the physical coordinate range.
	 * 
	 * @return physical coordinate range
	 */
	public Range2D getRangeP() {
		return _pRange;
	}

	public void setRangeU(Range2D wrange) {
		if (!approximate(_wRange, wrange)) {
			Range2D tempOld = _wRange;
			_wRange = wrange;
			computeTransform();
			_changes.firePropertyChange("rangeU", tempOld, _wRange);
		}
	}

	/**
	 * Get the world coordinate range for double values.
	 * 
	 * @return world range
	 */
	public Range2D getRangeU() {
		return _wRange;
	}

	/**
	 * Compute the internal parameter, and set the _valid status.
	 */
	protected abstract void computeTransform();

	public abstract void invert();

	/**
	 * Create a copy of this <code>MainAxisTransform</code>.
	 * 
	 * @return the copy
	 */
	public abstract AxisTransform copy();

	public boolean equals(Object obj) {
		if (obj == null || this.getClass() != obj.getClass()) {
			return false;
		}
		AxisTransform t = (AxisTransform) obj;
		return _pRange.equals(t._pRange) && _wRange.equals(t._wRange);
	}

	public int hashCode() {
		return _pRange.hashCode() ^ _wRange.hashCode();
	}

	public String toString() {
		return this.getClass().getSimpleName() + ": physical " + _pRange
				+ " world " + _wRange;
	}

	private static boolean approximate(Range2D a, Range2D b) {
		return NumberUtils.approximate(a.getStart(), b.getStart(), 4)
				&& NumberUtils.approximate(a.getEnd(), b.getEnd(), 4);
	}
}
