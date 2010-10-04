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
package org.jplot2d.tick;

/**
 * This class represent a number in hour minute second format
 * 
 * @author Jingjing Li
 */
public final class TimeHmsInterval implements DoubleInterval {

	public enum Unit {
		SECOND(1), MINUTE(1 * 60), HOUR(1 * 3600);

		public final int time;

		private Unit(int time) {
			this.time = time;
		}
	};

	private final TickUnitConverter tuc;

	private final int _c, _e;

	private final Unit _unit;

	public TimeHmsInterval(TickUnitConverter tuc, int coefficient, int exponent) {
		this.tuc = tuc;
		_unit = Unit.SECOND;
		_c = coefficient;
		_e = exponent;
	}

	public TimeHmsInterval(TickUnitConverter tuc, Unit unit, int value) {
		this.tuc = tuc;
		_unit = unit;
		_c = value;
		_e = 0;
	}

	public Unit getUnit() {
		return _unit;
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
		return tuc.convertT2D(Math.pow(10, _e) * _c * _unit.time);
	}

	public boolean equals(Object obj) {
		if (!(obj instanceof TimeHmsInterval)) {
			return false;
		}
		TimeHmsInterval ien = (TimeHmsInterval) obj;
		return _c == ien._c && _e == ien._e;
	}

	public int hashCode() {
		return _c ^ (_e << 16);
	}

	public String toString() {
		return _c + "e" + _e;
	}
}
