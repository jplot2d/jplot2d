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
 * Performs a log transformation on Cartesian axes. The equation is
 * <code>worldvalue = 10^(scale * normalized v + offset)<code>
 */
public class LogarithmicNormalTransform extends NormalTransform {

	private static final TransformType type = TransformType.LOGARITHMIC;

	private final double scale;

	private final double offset;

	public LogarithmicNormalTransform(Range ur) {
		this(ur.getStart(), ur.getEnd());
	}

	public LogarithmicNormalTransform(double u1, double u2) {
		if (Double.isNaN(u1) || Double.isNaN(u2)) {
			throw new IllegalArgumentException("Transform is invalid");
		}

		if (u1 <= 0 || u2 <= 0) {
			throw new IllegalArgumentException("Transform is invalid");
		}

		double sc = Math.log10(u2) - Math.log10(u1);
		if (sc == 0) {
			throw new IllegalArgumentException("Transform is invalid");
		} else {
			scale = sc;
			offset = Math.log10(u1);
		}
	}

	private LogarithmicNormalTransform(Void v, double a, double b) {
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
		if (w <= 0) {
			return Double.NEGATIVE_INFINITY * scale;
		}
		return (Math.log10(w) - offset) / scale;
	}

	public double convFromNR(double p) {
		return Math.pow(10, scale * p + offset);
	}

	public NormalTransform deriveNoOffset() {
		return new LogarithmicNormalTransform(null, scale, 0);
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
		return new LogarithmicNormalTransform(null, a, b);
	}

	public NormalTransform invert() {
		return new LogarithmicNormalTransform(null, -scale, scale + offset);
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
		return Math.log10(1 - precisionLimit) / Math.abs(scale);
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
		return new Range.Double(Math.pow(10, offset), Math.pow(10, scale + offset));
	}

	@Override
	public Transform1D createTransform(double d1, double d2) {
		return new LogTransform(offset, scale, d1, d2);
	}

}
