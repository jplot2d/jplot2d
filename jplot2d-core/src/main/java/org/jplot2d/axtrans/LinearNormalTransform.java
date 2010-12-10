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
 * $Id: NormalLinearTransform.java,v 1.1 2010/06/11 09:15:07 jli Exp $
 */
package org.jplot2d.axtrans;

import org.jplot2d.util.Range2D;

/**
 * Performs a linear transformation on Cartesian axes. The equation is <code>
 * worldvalue = a * np + b
 * </code>
 */
public class LinearNormalTransform extends NormalTransform {

	public LinearNormalTransform(double u1, double u2) {
		computeTransform(u1, u2);
	}

	public LinearNormalTransform(Range2D ur) {
		computeTransform(ur.getStart(), ur.getEnd());
	}

	public LinearNormalTransform copy() {
		LinearNormalTransform newTransform;
		try {
			newTransform = (LinearNormalTransform) clone();
		} catch (CloneNotSupportedException e) {
			throw new Error();
		}
		return newTransform;
	}

	/**
	 * Transform from user to physical coordinates.
	 * 
	 * @param u
	 *            user value
	 * @return physical value
	 */
	public double getTransP(double w) {
		if (!_valid) {
			throw new IllegalStateException("Transform is invalid");
		}
		return (w - _b) / _a;
	}

	public double getTransU(double p) {
		if (!_valid) {
			throw new IllegalStateException("Transform is invalid");
		}
		return _a * p + _b;
	}

	protected void computeTransform(double _u1, double _u2) {

		if (Double.isNaN(_u1) || Double.isNaN(_u2)) {
			_valid = false;
			return;
		}

		_a = _u2 - _u1;
		_b = _u1;
		if (_a == 0) {
			_a = Double.NaN;
			_b = Double.NaN;
			_valid = false;
		} else {
			_valid = true;
		}
	}

	/**
	 * t: the precision limit; u: the abs larger user point of range to zoom in<br>
	 * The formula: p = _a * u + _b *
	 * 
	 * <pre>
	 *         (u-dU)/u &lt; 1 - t
	 *         dU/u &gt; t
	 *         (dP / _a) / ((p - _b) / _a) &gt; t
	 *         dP &gt; t * (p - _b)
	 *         [[ _b= getTansP(0) ]]
	 * </pre>
	 */
	@Override
	public double getMinPSpan4PrecisionLimit(double pLo, double pHi,
			double precisionLimit) {
		double r;
		double b = getTransP(0);
		if (pLo < b && pHi > b) {
			r = 0;
		} else {
			// find the far point from b
			double p = ((pLo + pHi) > 2 * b) ? pHi : pLo;
			r = precisionLimit * Math.abs(p - b);
		}
		return r;
	}

	public Range2D getRange4PrecisionLimit(Range2D range, double precisionLimit) {
		double pcsHFactor = precisionLimit / (2 - precisionLimit);
		double mid = (range.getStart() + range.getEnd()) / 2;
		// precision half span
		double pcsHSpan = Math.abs(mid * pcsHFactor);
		if (range.getSpan() >= pcsHSpan * 2) {
			return range;
		} else {
			return new Range2D.Double(mid - pcsHSpan, mid + pcsHSpan);
		}
	}

	@Override
	public Range2D getRangeW() {
		return new Range2D.Double(_b, _a + _b);
	}

}