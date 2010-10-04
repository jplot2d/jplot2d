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
 * This class represent a number in c x 10^n form. c is a integer and in range
 * between -9 and 9.
 * 
 * @author Jingjing Li
 */
public final class DateInterval {

	public enum Unit {
		MILLISECOND(1), SECOND(1000L), MINUTE(1000L * 60), HOUR(1000L * 3600), DAY(
				1000L * 3600 * 24), WEEK(1000L * 3600 * 24 * 7), MONTH(
				1000L * 3600 * 24 * 30), YEAR(1000L * 3600 * 24 * 30 * 12);

		public final long time;

		private Unit(long time) {
			this.time = time;
		}

	};

	private final Unit _unit;

	private final int _v;

	public DateInterval(Unit unit, int value) {
		_unit = unit;
		_v = value;
	}

	public DateInterval(long interval) {
		if (interval < Unit.SECOND.time) {
			_unit = Unit.MILLISECOND;
			_v = (int) interval;
		} else if (interval < Unit.MINUTE.time) {
			_unit = Unit.SECOND;
			_v = (int) (interval / Unit.SECOND.time);
		} else if (interval < Unit.HOUR.time) {
			_unit = Unit.MINUTE;
			_v = (int) (interval / Unit.MINUTE.time);
		} else if (interval < Unit.DAY.time) {
			_unit = Unit.HOUR;
			_v = (int) (interval / Unit.HOUR.time);
		} else if (interval < Unit.MONTH.time) {
			_unit = Unit.DAY;
			_v = (int) (interval / Unit.DAY.time);
		} else if (interval < Unit.YEAR.time) {
			_unit = Unit.MONTH;
			_v = (int) (interval / Unit.MONTH.time);
		} else {
			_unit = Unit.YEAR;
			_v = (int) (interval / Unit.YEAR.time);
		}
	}

	/**
	 * @return the coefficient
	 */
	public Unit getUnit() {
		return _unit;
	}

	/**
	 * @return the exponent
	 */
	public int getValue() {
		return _v;
	}

	public long getTime() {
		return _v * _unit.time;
	}

	public boolean equals(Object obj) {
		if (!(obj instanceof DateInterval)) {
			return false;
		}
		DateInterval ien = (DateInterval) obj;
		return getTime() == ien.getTime();
	}

	public int hashCode() {
		return (int) (getTime() ^ (getTime() >>> 32));
	}

	public String toString() {
		return String.valueOf(_v) + _unit;
	}
}
