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
package org.jplot2d.transfrom;

import org.jplot2d.util.Range;

/**
 * Transform between world coordinate and normal coordinate.
 */
public abstract class NormalTransform implements Cloneable {

	public static final Range NORMAL_RANGE = new Range.Double(0.0, 1.0);

	private TransformType type;

	protected boolean _valid = false;

	protected double _a;

	protected double _b;

	/**
	 * Create a non-transformable AxisTranform.
	 */
	protected NormalTransform(TransformType type) {
		this.type = type;
	}

	public TransformType getType() {
		return type;
	}

	/**
	 * Create a copy of this <code>NormalTransform</code>.
	 * 
	 * @return the copy
	 */
	public abstract NormalTransform copy();

	/**
	 * Derive a new NormalTransform with offset set to 0;
	 * 
	 * @return the derived NormalTransform
	 */
	public NormalTransform deriveNoOffset() {
		NormalTransform result = this.copy();
		result._b = 0;
		return result;
	}

	/**
	 * Returns if this AxisTransform is ready to transform values. Only <code>true</code> after both
	 * paper range and world range are set properly.
	 * 
	 * @return <code>true</code> if this AxisTransform is ready to transform values.
	 */
	public boolean isValid() {
		return _valid;
	}

	public double getScale() {
		if (!_valid) {
			throw new IllegalStateException("Transform is invalid");
		}
		return _a;
	}

	public double getOffset() {
		if (!_valid) {
			throw new IllegalStateException("Transform is invalid");
		}
		return _b;
	}

	/**
	 * Transform from world to paper coordinate.
	 * 
	 * @param u
	 *            world value
	 * @return normalized value
	 */
	public abstract double getTransP(double u);

	/**
	 * Transform from normalized value to world coordinate.
	 * 
	 * @param p
	 *            normalized value
	 * @return world value
	 */
	public abstract double getTransU(double p);

	public Range getTransP(Range wrange) {
		return new Range.Double(getTransP(wrange.getStart()), wrange.isStartIncluded(),
				getTransP(wrange.getEnd()), wrange.isEndIncluded());
	}

	public Range getTransU(Range prange) {
		return new Range.Double(getTransU(prange.getStart()), prange.isStartIncluded(),
				getTransU(prange.getEnd()), prange.isEndIncluded());
	}

	public void zoom(Range npr) {
		_b = _b + _a * npr.getStart();
		_a = _a * npr.getSpan();
		// prevent overflow
		if (_a == Double.POSITIVE_INFINITY) {
			_a = Double.MAX_VALUE;
		} else if (_a == Double.NEGATIVE_INFINITY) {
			_a = -Double.MAX_VALUE;
		}
	}

	public void invert() {
		_b += _a;
		_a = -_a;
	}

	public abstract Range getRangeW();

	public abstract double getMinPSpan4PrecisionLimit(double pLo, double pHi, double precisionLimit);

}
