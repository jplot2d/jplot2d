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
package org.jplot2d.transform;

import org.jplot2d.util.Range;

/**
 * Performs a linear transformation on Cartesian axes. The equation is <code>
 * worldvalue = a * np + b
 * </code>
 */
public class LinearNormalTransform extends NormalTransform {

	private static final TransformType type = TransformType.LINEAR;

	private final boolean valid;

	private final double slope;

	private final double offset;

	public LinearNormalTransform(Range ur) {
		this(ur.getStart(), ur.getEnd());
	}

	public LinearNormalTransform(double u1, double u2) {
		if (Double.isNaN(u1) || Double.isNaN(u2)) {
			valid = false;
			slope = Double.NaN;
			offset = Double.NaN;
			return;
		}

		if (u2 - u1 == 0) {
			valid = false;
			slope = Double.NaN;
			offset = Double.NaN;
		} else {
			valid = true;
			slope = u2 - u1;
			offset = u1;
		}
	}

	private LinearNormalTransform(boolean valid, double a, double b) {
		this.valid = valid;
		this.slope = a;
		this.offset = b;
	}

	public TransformType getType() {
		return type;
	}

	public boolean isValid() {
		return valid;
	}

	public double getScale() {
		if (!valid) {
			throw new IllegalStateException("Transform is invalid");
		}
		return slope;
	}

	public double getOffset() {
		if (!valid) {
			throw new IllegalStateException("Transform is invalid");
		}
		return offset;
	}

	/**
	 * Transform from user to normalized coordinates.
	 * 
	 * @param u
	 *            user value
	 * @return normalized value
	 */
	public double getTransP(double w) {
		if (!valid) {
			throw new IllegalStateException("Transform is invalid");
		}
		return (w - offset) / slope;
	}

	public double getTransU(double p) {
		if (!valid) {
			throw new IllegalStateException("Transform is invalid");
		}
		return slope * p + offset;
	}

	public NormalTransform deriveNoOffset() {
		return new LinearNormalTransform(valid, slope, 0);
	}

	public NormalTransform zoom(Range npr) {
		double b = offset + slope * npr.getStart();
		double a = slope * npr.getSpan();
		// prevent overflow
		if (a == Double.POSITIVE_INFINITY) {
			a = Double.MAX_VALUE;
		} else if (a == Double.NEGATIVE_INFINITY) {
			a = -Double.MAX_VALUE;
		}
		return new LinearNormalTransform(valid, a, b);
	}

	public NormalTransform invert() {
		return new LinearNormalTransform(valid, -slope, slope + offset);
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
	public double getMinPSpan4PrecisionLimit(double pLo, double pHi, double precisionLimit) {
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

	public Range getRange4PrecisionLimit(Range range, double precisionLimit) {
		double pcsHFactor = precisionLimit / (2 - precisionLimit);
		double mid = (range.getStart() + range.getEnd()) / 2;
		// precision half span
		double pcsHSpan = Math.abs(mid * pcsHFactor);
		if (range.getSpan() >= pcsHSpan * 2) {
			return range;
		} else {
			return new Range.Double(mid - pcsHSpan, mid + pcsHSpan);
		}
	}

	@Override
	public Range getRangeW() {
		return new Range.Double(offset, slope + offset);
	}

}
