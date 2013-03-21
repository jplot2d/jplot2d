/**
 * Copyright 2010-2013 Jingjing Li.
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
package org.jplot2d.axtick;

import org.jplot2d.util.Range;

/**
 * A calculator to calculate tick values and minor tick values
 * 
 * @author Jingjing Li
 */
public abstract class DoubleTickCalculator extends TickCalculator {

	protected double start, end;

	public final Range getRange() {
		return new Range.Double(start, end);
	}

	public final void setRange(Range range) {
		this.setRange(range.getStart(), range.getEnd());
	}

	/**
	 * @param start
	 * @param end
	 */
	protected void setRange(double start, double end) {
		this.start = start;
		this.end = end;
	}

	/**
	 * Calculate the tick values by the given interval and minor ticks number. The minor ticks number is a proposed
	 * value, and may be different from actual minor ticks number returned by {@link #getMinorNumber()}.
	 * 
	 * @param interval
	 * @param offset
	 * @param minorTickNumber
	 *            if the given number is {@link #AUTO_MINORTICK_NUMBER}, the tick number is derived from interval.
	 */
	public abstract void calcValuesByTickInterval(double interval, double offset, int minorTickNumber);

	/**
	 * @return the tick interval.
	 */
	public abstract double getInterval();

	/**
	 * @return the actual minor tick number.
	 */
	public abstract int getMinorNumber();

	/**
	 * @return the tick values in ascend order
	 */
	public abstract double[] getValues();

	/**
	 * @return the minor tick values in ascend order
	 */
	public abstract double[] getMinorValues();

	/**
	 * Return all values within the range.
	 * 
	 * @param v
	 * @return
	 */
	public int[] getInRangeValuesIdx(Object v) {
		if (v instanceof double[]) {
			return getInRangeValuesIdx((double[]) v);
		}
		throw new IllegalArgumentException();
	}

	private int[] getInRangeValuesIdx(double[] v) {
		boolean inverted = start > end;

		double lo, hi;
		if (!inverted) {
			lo = start;
			hi = end;
		} else {
			lo = end;
			hi = start;
		}
		double expandLo = lo - Math.abs(lo) * DOUBLE_PRECISION_TOLERANCE;
		double expandHi = hi + Math.abs(hi) * DOUBLE_PRECISION_TOLERANCE;

		int[] m = new int[v.length];
		int j = 0;
		for (int i = 0; i < v.length; i++) {
			if (expandLo <= v[i] && v[i] <= expandHi) {
				m[j++] = i;
			}
		}

		if (j == v.length) {
			// all in range selection
			return m;
		} else {
			int[] result = new int[j];
			System.arraycopy(m, 0, result, 0, j);
			return result;
		}
	}

	public boolean isValidFormat(String format) {
		format = TickUtils.convFCm2e(format);
		try {
			String.format(format, 1d);
			return true;
		} catch (IllegalArgumentException e) {
			return false;
		}
	}

}
