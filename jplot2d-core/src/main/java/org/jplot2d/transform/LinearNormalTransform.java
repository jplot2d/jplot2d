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
 * Performs a linear transformation on Cartesian axes. The equation is
 * <code> worldvalue = scale * normalized v + offset </code>
 */
public class LinearNormalTransform extends NormalTransform {

	private static final TransformType type = TransformType.LINEAR;

	private final double scale;

	private final double offset;

	public LinearNormalTransform(Range ur) {
		this(ur.getStart(), ur.getEnd());
	}

	public LinearNormalTransform(double u1, double u2) {
		if (Double.isNaN(u1) || Double.isNaN(u2)) {
			throw new IllegalArgumentException("Transform is invalid");
		}

		if (u2 - u1 == 0) {
			throw new IllegalArgumentException("Transform is invalid");
		} else {
			scale = u2 - u1;
			offset = u1;
		}
	}

	private LinearNormalTransform(Void v, double a, double b) {
		this.scale = a;
		this.offset = b;
	}

	public TransformType getType() {
		return type;
	}

	public double getScale() {
		return scale;
	}

	public double getOffset() {
		return offset;
	}

	/**
	 * Transform from user to normalized coordinates.
	 * 
	 * @param u
	 *            user value
	 * @return normalized value
	 */
	public double convToNR(double w) {
		return (w - offset) / scale;
	}

	public double convFromNR(double p) {
		return scale * p + offset;
	}

	public NormalTransform deriveNoOffset() {
		return new LinearNormalTransform(null, scale, 0);
	}

	public NormalTransform zoom(Range npr) {
		double b = offset + scale * npr.getStart();
		double a = scale * npr.getSpan();
		// prevent overflow
		if (a == Double.POSITIVE_INFINITY) {
			a = Double.MAX_VALUE;
		} else if (a == Double.NEGATIVE_INFINITY) {
			a = -Double.MAX_VALUE;
		}
		return new LinearNormalTransform(null, a, b);
	}

	public NormalTransform invert() {
		return new LinearNormalTransform(null, -scale, scale + offset);
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
		double b = convToNR(0);
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
	public Range getValueRange() {
		return new Range.Double(offset, scale + offset);
	}

	@Override
	public Transform1D createTransform(double d1, double d2) {
		return new LinearTransform(offset, scale + offset, d1, d2);
	}

}
