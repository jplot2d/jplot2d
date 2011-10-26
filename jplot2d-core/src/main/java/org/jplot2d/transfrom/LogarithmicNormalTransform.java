/**
 * Copyright 2010, 2011 Jingjing Li.
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
package org.jplot2d.transfrom;

import org.jplot2d.util.Range2D;

/**
 * Performs a log transformation on Cartesian axes. The equation is <code>worldvalue =
 * 10^(a * np + b)<code>
 */
public class LogarithmicNormalTransform extends NormalTransform {

	public LogarithmicNormalTransform(double u1, double u2) {
		super(TransformType.LOGARITHMIC);
		computeTransform(u1, u2);
	}

	public LogarithmicNormalTransform(Range2D ur) {
		super(TransformType.LOGARITHMIC);
		computeTransform(ur.getStart(), ur.getEnd());
	}

	public LogarithmicNormalTransform copy() {
		LogarithmicNormalTransform newTransform;
		try {
			newTransform = (LogarithmicNormalTransform) clone();
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
		if (w <= 0) {
			return Double.NEGATIVE_INFINITY * _a;
		}
		return (Math.log10(w) - _b) / _a;
	}

	public double getTransU(double p) {
		if (!_valid) {
			throw new IllegalStateException("Transform is invalid");
		}
		return Math.pow(10, _a * p + _b);
	}

	protected void computeTransform(double _u1, double _u2) {

		if (Double.isNaN(_u1) || Double.isNaN(_u2)) {
			_valid = false;
			return;
		}

		if (_u1 <= 0 || _u2 <= 0) {
			_a = Double.NaN;
			_b = Double.NaN;
			_valid = false;
			return;
		}

		_a = Math.log10(_u2) - Math.log10(_u1);
		_b = Math.log10(_u1);
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
	 *         u'/u &gt; 1 - t
	 *         10&circ;(_ap'-_b)/10&circ;(_ap-_b) &gt; 1-t
	 *         10&circ;(_a(p'-p)) &gt; 1-t
	 *         _a(p'-p) &gt; log10(1-t)
	 *         dP &gt; log10(1-t)/abs(_a)
	 * </pre>
	 */
	public double getMinPSpan4PrecisionLimit(double pLo, double pHi, double precisionLimit) {
		return Math.log10(1 - precisionLimit) / Math.abs(_a);
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
		return new Range2D.Double(Math.pow(10, _b), Math.pow(10, _a + _b));
	}

}
