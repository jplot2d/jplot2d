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
package org.jplot2d.transform;

import org.jplot2d.util.Range;

/**
 * Immutable! Transform between value range and normalized range.
 */
public abstract class NormalTransform {

	public static final Range NORMAL_RANGE = new Range.Double(0.0, 1.0);

	protected NormalTransform() {

	}

	public abstract TransformType getType();

	/**
	 * Returns if this AxisTransform is ready to transform values. Only <code>true</code> after both
	 * paper range and world range are set properly.
	 * 
	 * @return <code>true</code> if this AxisTransform is ready to transform values.
	 */
	public abstract boolean isValid();

	public abstract double getScale();

	public abstract double getOffset();

	/**
	 * Derive a new NormalTransform with offset set to 0;
	 * 
	 * @return the derived NormalTransform
	 */
	public abstract NormalTransform deriveNoOffset();

	public abstract NormalTransform zoom(Range npr);

	public abstract NormalTransform invert();

	/**
	 * Transform from value range to normalized range.
	 * 
	 * @param u
	 *            the value
	 * @return normalized value
	 */
	public abstract double convToNR(double u);

	/**
	 * Transform a value from normalized range to value range.
	 * 
	 * @param p
	 *            normalized value
	 * @return world value
	 */
	public abstract double convFromNR(double p);

	public Range getTransP(Range wrange) {
		return new Range.Double(convToNR(wrange.getStart()), wrange.isStartIncluded(),
				convToNR(wrange.getEnd()), wrange.isEndIncluded());
	}

	public Range getTransU(Range prange) {
		return new Range.Double(convFromNR(prange.getStart()), prange.isStartIncluded(),
				convFromNR(prange.getEnd()), prange.isEndIncluded());
	}

	public abstract Range getValueRange();

	public abstract double getMinPSpan4PrecisionLimit(double pLo, double pHi, double precisionLimit);

}
