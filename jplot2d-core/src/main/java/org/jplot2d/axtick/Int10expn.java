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
package org.jplot2d.axtick;

/**
 * This class represent a number in c x 10^n form. c is a integer and in range
 * between -9 and 9.
 * 
 * @author Jingjing Li
 */
public final class Int10expn implements DoubleInterval {

	private final int _c, _e;

	public Int10expn(int coefficient, int exponent) {
		_c = coefficient;
		_e = exponent;
	}

	public Int10expn(double value) {
		boolean neg = false;
		if (value < 0) {
			neg = true;
			value = -value;
		}
		int e = (int) Math.floor(Math.log10(value));
		int c = (int) Math.round(value / Math.pow(10, e));
		if (c == 10) {
			e++;
			c = 1;
		}
		_e = e;
		if (!neg) {
			_c = c;
		} else {
			_c = -c;
		}
	}

	/**
	 * @return the coefficient
	 */
	public int getCoefficient() {
		return _c;
	}

	/**
	 * @return the exponent
	 */
	public int getExponent() {
		return _e;
	}

	public double doubleValue() {
		return Math.pow(10, _e) * _c;
	}

	public boolean equals(Object obj) {
		if (!(obj instanceof Int10expn)) {
			return false;
		}
		Int10expn ien = (Int10expn) obj;
		return _c == ien._c && _e == ien._e;
	}

	public int hashCode() {
		return _c ^ (_e << 16);
	}

	public String toString() {
		return _c + "e" + _e;
	}
}
