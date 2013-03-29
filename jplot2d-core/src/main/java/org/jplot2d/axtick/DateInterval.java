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

/**
 * This class represent a number in c x 10^n form. c is a integer and in range between -9 and 9.
 * 
 * @author Jingjing Li
 */
public final class DateInterval {

	public enum Unit {
		MICROSECOND(0), MILLISECOND(1), SECOND(1000L), MINUTE(1000L * 60), HOUR(1000L * 3600), DAY(1000L * 3600 * 24), WEEK(
				1000L * 3600 * 24 * 7), MONTH(1000L * 3600 * 24 * 30), YEAR(1000L * 3600 * 24 * 30 * 12);

		/**
		 * the time in milliseconds
		 */
		public final long time;

		private Unit(long time) {
			this.time = time;
		}

	};

	private final Unit unit;

	private final int v;

	public DateInterval(Unit unit, int value) {
		this.unit = unit;
		this.v = value;
	}

	public DateInterval(long interval) {
		if (interval < Unit.SECOND.time) {
			unit = Unit.MILLISECOND;
			v = (int) interval;
		} else if (interval < Unit.MINUTE.time) {
			unit = Unit.SECOND;
			v = (int) (interval / Unit.SECOND.time);
		} else if (interval < Unit.HOUR.time) {
			unit = Unit.MINUTE;
			v = (int) (interval / Unit.MINUTE.time);
		} else if (interval < Unit.DAY.time) {
			unit = Unit.HOUR;
			v = (int) (interval / Unit.HOUR.time);
		} else if (interval < Unit.MONTH.time) {
			unit = Unit.DAY;
			v = (int) (interval / Unit.DAY.time);
		} else if (interval < Unit.YEAR.time) {
			unit = Unit.MONTH;
			v = (int) (interval / Unit.MONTH.time);
		} else {
			unit = Unit.YEAR;
			v = (int) (interval / Unit.YEAR.time);
		}
	}

	/**
	 * @return the coefficient
	 */
	public Unit getUnit() {
		return unit;
	}

	/**
	 * @return the exponent
	 */
	public int getValue() {
		return v;
	}

	public long getTimeInMillis() {
		return v * unit.time;
	}

	public long getTimeInMicros() {
		if (unit == Unit.MICROSECOND) {
			return v;
		} else {
			return v * unit.time * 1000;
		}
	}

	public boolean equals(Object obj) {
		if (!(obj instanceof DateInterval)) {
			return false;
		}
		DateInterval ien = (DateInterval) obj;
		return getValue() == ien.getValue() && getUnit() == ien.getUnit();
	}

	public int hashCode() {
		return (int) (getValue() ^ (getUnit().ordinal() << 28));
	}

	public String toString() {
		return String.valueOf(v) + unit;
	}
}
