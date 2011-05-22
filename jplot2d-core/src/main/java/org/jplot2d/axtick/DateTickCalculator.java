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

import java.text.Format;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import org.jplot2d.axtick.DateInterval.Unit;

/**
 * 
 * @author Jingjing Li
 * 
 */
public class DateTickCalculator extends LongTickCalculator implements
		RangeAdvisor {

	private final Locale _locale;

	private final TimeZone _zone;

	/**
	 * Always be positive
	 */
	private DateInterval _interval;

	private int _minorNumber;

	private long[] _tickValues;

	private long[] _minorValues;

	public DateTickCalculator(TimeZone zone, Locale locale) {
		_zone = zone;
		_locale = locale;
	}

	protected static DateInterval calcInterval(long start, long end,
			int tickNumber) {

		if (start == end) {
			throw new IllegalArgumentException(
					"The range span must be great than zero");
		}
		if (tickNumber <= 0) {
			throw new IllegalArgumentException(
					"The ticks number must be great than zero");
		} else if (tickNumber == 1) {
			tickNumber = 2;
		}

		long lo, hi;
		if (end > start) {
			lo = start;
			hi = end;
		} else {
			lo = end;
			hi = start;
		}
		tickNumber = Math.abs(tickNumber);

		DateInterval itvA, itvB;
		double rough = (double) (hi - lo) / (tickNumber - 1);
		if (rough < 1000) {
			int scale;
			if (rough < 10) {
				scale = 1;
			} else if (rough < 100) {
				scale = 10;
			} else {
				scale = 100;
			}
			/* 1 <= rough/scale < 10 */
			double coeff = rough / scale;
			if (coeff < 2) {
				itvA = new DateInterval(Unit.MILLISECOND, scale * 1);
				itvB = new DateInterval(Unit.MILLISECOND, scale * 2);
			} else if (coeff < 5) {
				itvA = new DateInterval(Unit.MILLISECOND, scale * 2);
				itvB = new DateInterval(Unit.MILLISECOND, scale * 5);
			} else {
				itvA = new DateInterval(Unit.MILLISECOND, scale * 5);
				itvB = new DateInterval(Unit.MILLISECOND, scale * 10);
			}
		} else if (rough < Unit.SECOND.time * 2) {
			itvA = new DateInterval(Unit.SECOND, 1);
			itvB = new DateInterval(Unit.SECOND, 2);
		} else if (rough < Unit.SECOND.time * 5) {
			itvA = new DateInterval(Unit.SECOND, 2);
			itvB = new DateInterval(Unit.SECOND, 5);
		} else if (rough < Unit.SECOND.time * 10) {
			itvA = new DateInterval(Unit.SECOND, 5);
			itvB = new DateInterval(Unit.SECOND, 10);
		} else if (rough < Unit.SECOND.time * 15) {
			itvA = new DateInterval(Unit.SECOND, 10);
			itvB = new DateInterval(Unit.SECOND, 15);
		} else if (rough < Unit.SECOND.time * 30) {
			itvA = new DateInterval(Unit.SECOND, 15);
			itvB = new DateInterval(Unit.SECOND, 30);
		} else if (rough < Unit.MINUTE.time) {
			itvA = new DateInterval(Unit.SECOND, 30);
			itvB = new DateInterval(Unit.MINUTE, 1);
		} else if (rough < Unit.MINUTE.time * 2) {
			itvA = new DateInterval(Unit.MINUTE, 1);
			itvB = new DateInterval(Unit.MINUTE, 2);
		} else if (rough < Unit.MINUTE.time * 5) {
			itvA = new DateInterval(Unit.MINUTE, 2);
			itvB = new DateInterval(Unit.MINUTE, 5);
		} else if (rough < Unit.MINUTE.time * 10) {
			itvA = new DateInterval(Unit.MINUTE, 5);
			itvB = new DateInterval(Unit.MINUTE, 10);
		} else if (rough < Unit.MINUTE.time * 15) {
			itvA = new DateInterval(Unit.MINUTE, 10);
			itvB = new DateInterval(Unit.MINUTE, 15);
		} else if (rough < Unit.MINUTE.time * 30) {
			itvA = new DateInterval(Unit.MINUTE, 15);
			itvB = new DateInterval(Unit.MINUTE, 30);
		} else if (rough < Unit.HOUR.time) {
			itvA = new DateInterval(Unit.MINUTE, 30);
			itvB = new DateInterval(Unit.HOUR, 1);
		} else if (rough < Unit.HOUR.time * 2) {
			itvA = new DateInterval(Unit.HOUR, 1);
			itvB = new DateInterval(Unit.HOUR, 2);
		} else if (rough < Unit.HOUR.time * 3) {
			itvA = new DateInterval(Unit.HOUR, 2);
			itvB = new DateInterval(Unit.HOUR, 3);
		} else if (rough < Unit.HOUR.time * 6) {
			itvA = new DateInterval(Unit.HOUR, 3);
			itvB = new DateInterval(Unit.HOUR, 6);
		} else if (rough < Unit.HOUR.time * 12) {
			itvA = new DateInterval(Unit.HOUR, 6);
			itvB = new DateInterval(Unit.HOUR, 12);
		} else if (rough < Unit.DAY.time) {
			itvA = new DateInterval(Unit.HOUR, 12);
			itvB = new DateInterval(Unit.DAY, 1);
		} else if (rough < Unit.DAY.time * 2) {
			itvA = new DateInterval(Unit.DAY, 1);
			itvB = new DateInterval(Unit.DAY, 2);
		} else if (rough < Unit.WEEK.time) {
			itvA = new DateInterval(Unit.DAY, 2);
			itvB = new DateInterval(Unit.WEEK, 1);
		} else if (rough < Unit.WEEK.time * 2) {
			itvA = new DateInterval(Unit.WEEK, 1);
			itvB = new DateInterval(Unit.WEEK, 2);
		} else if (rough < Unit.MONTH.time) {
			itvA = new DateInterval(Unit.WEEK, 2);
			itvB = new DateInterval(Unit.MONTH, 1);
		} else if (rough < Unit.MONTH.time * 3) {
			itvA = new DateInterval(Unit.MONTH, 1);
			itvB = new DateInterval(Unit.MONTH, 3);
		} else if (rough < Unit.MONTH.time * 6) {
			itvA = new DateInterval(Unit.MONTH, 3);
			itvB = new DateInterval(Unit.MONTH, 6);
		} else if (rough < Unit.YEAR.time * 1) {
			itvA = new DateInterval(Unit.MONTH, 6);
			itvB = new DateInterval(Unit.YEAR, 1);
		} else {
			double roughYear = rough / Unit.YEAR.time;
			int expn = (int) Math.floor(Math.log10(roughYear));
			int scale = (int) Math.pow(10, expn);
			/* 1 <= rough/scale < 10 */
			double coeff = roughYear / scale;
			if (coeff < 2) {
				itvA = new DateInterval(Unit.YEAR, scale * 1);
				itvB = new DateInterval(Unit.YEAR, scale * 2);
			} else if (coeff < 5) {
				itvA = new DateInterval(Unit.YEAR, scale * 2);
				itvB = new DateInterval(Unit.YEAR, scale * 5);
			} else {
				itvA = new DateInterval(Unit.YEAR, scale * 5);
				itvB = new DateInterval(Unit.YEAR, scale * 10);
			}
		}

		long iLoA = lo / itvA.getTime();
		long iHiA = hi / itvA.getTime();
		if (hi % itvA.getTime() != 0) {
			iHiA++;
		}
		int tickNumA = (int) (iHiA - iLoA + 1);
		long iLoB = lo / itvB.getTime();
		long iHiB = hi / itvB.getTime();
		if (hi % itvB.getTime() != 0) {
			iHiB++;
		}
		int tickNumB = (int) (iHiB - iLoB + 1);

		/* tickNumB < tickNumber < tickNumA */
		if (tickNumA - tickNumber <= tickNumber - tickNumB) {
			return itvA;
		} else {
			return itvB;
		}

	}

	/**
	 * @param interval
	 *            must be positive
	 * @param offset
	 * @param minorTickNumber
	 */
	protected void calcValues() {

		if (_interval.getValue() == 0)
			throw new IllegalArgumentException("delta cannot be zero");

		Calendar loCal = Calendar.getInstance(_zone, _locale);
		loCal.setTimeInMillis(_lo);
		Calendar hiCal = Calendar.getInstance(_zone, _locale);
		hiCal.setTimeInMillis(_hi);

		Calendar t1cal = (Calendar) loCal.clone();
		/* round up to unit */
		if (setCalendarBelowToMin(t1cal, _interval.getUnit())) {
			add(t1cal, _interval.getUnit(), 1);
		}

		List<Long> ticks = new ArrayList<Long>();
		List<Long> mticks = new ArrayList<Long>();
		if (_minorNumber == 0) {
			int delta = distanceToIntervalBoundary(t1cal, _interval);
			if (delta != 0) {
				add(t1cal, _interval.getUnit(), _interval.getValue() - delta);
			}
			while (!t1cal.after(hiCal)) {
				// System.out.println(t1cal.getTimeInMillis());
				ticks.add(t1cal.getTimeInMillis());
				add(t1cal, _interval.getUnit(), _interval.getValue());
			}
		} else {
			int mitv = _interval.getValue() / (_minorNumber + 1);
			DateInterval minorInterval = new DateInterval(_interval.getUnit(),
					mitv);
			int delta = distanceToIntervalBoundary(t1cal, minorInterval);
			if (delta != 0) {
				add(t1cal, _interval.getUnit(), mitv - delta);
			}
			while (!t1cal.after(hiCal)) {
				if (distanceToIntervalBoundary(t1cal, _interval) == 0) {
					ticks.add(t1cal.getTimeInMillis());
				} else {
					mticks.add(t1cal.getTimeInMillis());
				}
				add(t1cal, _interval.getUnit(), mitv);
			}

		}

		_tickValues = new long[ticks.size()];
		_minorValues = new long[mticks.size()];
		if (_inverted) {
			for (int i = 0, j = _tickValues.length - 1; i < _tickValues.length; i++, j--) {
				_tickValues[i] = ticks.get(j);
			}
			for (int i = 0, j = _minorValues.length - 1; i < _minorValues.length; i++, j--) {
				_minorValues[i] = mticks.get(j);
			}
		} else {
			for (int i = 0; i < _tickValues.length; i++) {
				_tickValues[i] = ticks.get(i);
			}
			for (int i = 0; i < _minorValues.length; i++) {
				_minorValues[i] = mticks.get(i);
			}
		}
	}

	public void expandRangeByTickNumber(int tickNumber) {
		if (tickNumber <= 0) {
			throw new IllegalArgumentException("tick number must be positive.");
		} else if (tickNumber == 1) {
			tickNumber = 2;
		}

		long span = _hi - _lo;
		if (span < tickNumber - 1) {
			/* expand range to tick number */
			long halfXpand = (tickNumber - 1 - span) / 2;
			long odd = (tickNumber - 1 - span) % 2;
			_lo -= halfXpand;
			_hi += halfXpand;
			_hi += odd;
			if (_lo < 0) {
				_lo = 0;
				_hi -= _lo;
			}
			_interval = new DateInterval(1);
		} else {
			_interval = calcInterval(_lo, _hi, tickNumber);
			expandRangeByTickInterval();
		}
	}

	public void expandRangeByTickInterval(double interval) {
		_interval = new DateInterval((long) Math.round(interval));
		expandRangeByTickInterval();

		/* if _lo == _hi and on interval boundary, the expanding has no effect */
		if (_lo == _hi) {
			Calendar hiCal = Calendar.getInstance(_zone, _locale);
			hiCal.setTimeInMillis(_hi);
			add(hiCal, _interval.getUnit(), _interval.getValue());
			_hi = hiCal.getTimeInMillis();
		}
	}

	protected void expandRangeByTickInterval() {

		Calendar loCal = Calendar.getInstance(_zone, _locale);
		loCal.setTimeInMillis(_lo);
		Calendar hiCal = Calendar.getInstance(_zone, _locale);
		hiCal.setTimeInMillis(_hi);

		setCalendarBelowToMin(loCal, _interval.getUnit());
		int loDelta = distanceToIntervalBoundary(loCal, _interval);
		if (loDelta != 0) {
			add(loCal, _interval.getUnit(), -loDelta);
		}

		boolean rounddown = setCalendarBelowToMin(hiCal, _interval.getUnit());
		if (rounddown) {
			add(hiCal, _interval.getUnit(), 1);
		}
		int hiDelta = distanceToIntervalBoundary(hiCal, _interval);
		if (hiDelta != 0) {
			add(hiCal, _interval.getUnit(), _interval.getValue() - hiDelta);
		}

		_lo = loCal.getTimeInMillis();
		_hi = hiCal.getTimeInMillis();

	}

	public void calcValuesByTickNumber(int tickNumber, int minorTickNumber) {
		_interval = calcInterval(_lo, _hi, tickNumber);
		if (minorTickNumber == AUTO_MINORTICK_NUMBER) {
			if (_interval.getValue() == 1) {
				_minorNumber = 0;
			} else {
				_minorNumber = TickUtils.calcMinorNumber(_interval.getValue(),
						3);
			}
		} else {
			_minorNumber = minorTickNumber;
		}
		calcValues();
	}

	@Override
	public void calcValuesByTickInterval(long interval, long offset,
			int minorTickNumber) {
		_interval = new DateInterval(interval);

		if (minorTickNumber == AUTO_MINORTICK_NUMBER) {
			if (_interval.getValue() == 1) {
				_minorNumber = 0;
			} else {
				_minorNumber = TickUtils.calcMinorNumber(_interval.getValue(),
						3);
			}
		} else {
			_minorNumber = minorTickNumber;
		}
		calcValues();
	}

	public double getInterval() {
		return _interval.getTime();
	}

	public int getMinorNumber() {
		return _minorNumber;
	}

	public long[] getValues() {
		return _tickValues;
	}

	public long[] getMinorValues() {
		return _minorValues;
	}

	public String getLabelFormate() {
		Calendar loCal = Calendar.getInstance(_zone, _locale);
		loCal.setTimeInMillis(_lo);
		Calendar hiCal = Calendar.getInstance(_zone, _locale);
		hiCal.setTimeInMillis(_hi);

		Unit umax = getFirsNonEqualField(loCal, hiCal);
		return calcLabelFormat(_interval.getUnit(), umax);
	}

	public Format calcAutoLabelTextFormat(Object canonicalValues) {
		return null;
	}

	public String calcAutoLabelFormat(Object values) {
		if (((long[]) values).length == 0) {
			return "";
		}

		Calendar cal = Calendar.getInstance(_zone, _locale);
		Unit umin = Unit.YEAR;
		long lo = Long.MAX_VALUE, hi = 0;
		for (long v : (long[]) values) {
			if (lo > v) {
				lo = v;
			}
			if (hi < v) {
				hi = v;
			}
			cal.setTimeInMillis(v);
			Unit lnmf = getLastNonMinField(cal);
			if (umin.time > lnmf.time) {
				umin = lnmf;
			}
		}

		Calendar loCal = Calendar.getInstance(_zone, _locale);
		loCal.setTimeInMillis(lo);
		Calendar hiCal = Calendar.getInstance(_zone, _locale);
		hiCal.setTimeInMillis(hi);
		Unit umax = getFirsNonEqualField(loCal, hiCal);

		return calcLabelFormat(umin, umax);
	}

	protected static String calcLabelFormat(Unit uprec, Unit umdiff) {
		if (uprec.time > umdiff.time) {
			throw new IllegalArgumentException(
					"The MaxDiff unit must larger than precision unit.");
		}

		/* format string for hh:mm:ss.mmm without the % char */
		String hms;
		switch (uprec) {
		case MILLISECOND:
			hms = "tT.%<tL";
			break;
		case SECOND:
			hms = "tT";
			break;
		case MINUTE:
		case HOUR:
			hms = "tR";
			break;
		default:
			hms = null;
		}

		if (hms == null) {
			return "%tF";
		} else if (umdiff.time >= Unit.DAY.time) {
			return "%tF %<" + hms;
		} else {
			return "%" + hms;
		}
	}

	protected static Unit getLastNonMinField(Calendar cal) {
		if (cal.get(Calendar.MILLISECOND) > cal
				.getMinimum(Calendar.MILLISECOND)) {
			return Unit.MILLISECOND;
		}
		if (cal.get(Calendar.SECOND) > cal.getMinimum(Calendar.SECOND)) {
			return Unit.SECOND;
		}
		if (cal.get(Calendar.MINUTE) > cal.getMinimum(Calendar.MINUTE)) {
			return Unit.MINUTE;
		}
		if (cal.get(Calendar.HOUR_OF_DAY) > cal
				.getMinimum(Calendar.HOUR_OF_DAY)) {
			return Unit.HOUR;
		}
		if (cal.get(Calendar.DAY_OF_MONTH) > cal
				.getMinimum(Calendar.DAY_OF_MONTH)) {
			return Unit.DAY;
		}
		if (cal.get(Calendar.MONTH) > cal.getMinimum(Calendar.MONTH)) {
			return Unit.MONTH;
		}
		if (cal.get(Calendar.YEAR) > cal.getMinimum(Calendar.YEAR)) {
			return Unit.YEAR;
		}
		return null;
	}

	protected static Unit getFirsNonEqualField(Calendar a, Calendar b) {
		if (a.get(Calendar.YEAR) != b.get(Calendar.YEAR)) {
			return Unit.YEAR;
		}
		if (a.get(Calendar.MONTH) != b.get(Calendar.MONTH)) {
			return Unit.MONTH;
		}
		if (a.get(Calendar.DAY_OF_MONTH) != b.get(Calendar.DAY_OF_MONTH)) {
			return Unit.DAY;
		}
		if (a.get(Calendar.HOUR_OF_DAY) != b.get(Calendar.HOUR_OF_DAY)) {
			return Unit.HOUR;
		}
		if (a.get(Calendar.MINUTE) != b.get(Calendar.MINUTE)) {
			return Unit.MINUTE;
		}
		if (a.get(Calendar.SECOND) != b.get(Calendar.SECOND)) {
			return Unit.SECOND;
		}
		if (a.get(Calendar.MILLISECOND) != b.get(Calendar.MILLISECOND)) {
			return Unit.MILLISECOND;
		}
		return null;
	}

	/**
	 * Sets all calendar fields below the given unit to its minimal value.
	 * 
	 * @param cal
	 * @param unit
	 * @return true if any fields below the given unit is larger than its
	 *         minimal value
	 */
	protected static boolean setCalendarBelowToMin(Calendar cal, Unit unit) {
		switch (unit) {
		case MILLISECOND:
			return false;
		case SECOND:
			if (cal.get(Calendar.MILLISECOND) > cal
					.getMinimum(Calendar.MILLISECOND)) {
				cal.set(Calendar.MILLISECOND,
						cal.getMinimum(Calendar.MILLISECOND));
				return true;
			} else {
				return false;
			}
		case MINUTE:
			if (cal.get(Calendar.MILLISECOND) > cal
					.getMinimum(Calendar.MILLISECOND)
					|| cal.get(Calendar.SECOND) > cal
							.getMinimum(Calendar.SECOND)) {
				cal.set(Calendar.MILLISECOND,
						cal.getMinimum(Calendar.MILLISECOND));
				cal.set(Calendar.SECOND, cal.getMinimum(Calendar.SECOND));
				return true;
			} else {
				return false;
			}
		case HOUR:
			if (cal.get(Calendar.MILLISECOND) > cal
					.getMinimum(Calendar.MILLISECOND)
					|| cal.get(Calendar.SECOND) > cal
							.getMinimum(Calendar.SECOND)
					|| cal.get(Calendar.MINUTE) > cal
							.getMinimum(Calendar.MINUTE)) {
				cal.set(Calendar.MILLISECOND,
						cal.getMinimum(Calendar.MILLISECOND));
				cal.set(Calendar.SECOND, cal.getMinimum(Calendar.SECOND));
				cal.set(Calendar.MINUTE, cal.getMinimum(Calendar.MINUTE));
				return true;
			} else {
				return false;
			}
		case DAY:
			if (cal.get(Calendar.MILLISECOND) > cal
					.getMinimum(Calendar.MILLISECOND)
					|| cal.get(Calendar.SECOND) > cal
							.getMinimum(Calendar.SECOND)
					|| cal.get(Calendar.MINUTE) > cal
							.getMinimum(Calendar.MINUTE)
					|| cal.get(Calendar.HOUR_OF_DAY) > cal
							.getMinimum(Calendar.HOUR_OF_DAY)) {
				cal.set(Calendar.MILLISECOND,
						cal.getMinimum(Calendar.MILLISECOND));
				cal.set(Calendar.SECOND, cal.getMinimum(Calendar.SECOND));
				cal.set(Calendar.MINUTE, cal.getMinimum(Calendar.MINUTE));
				cal.set(Calendar.HOUR_OF_DAY,
						cal.getMinimum(Calendar.HOUR_OF_DAY));
				return true;
			} else {
				return false;
			}
		case WEEK:
			if (cal.get(Calendar.MILLISECOND) > cal
					.getMinimum(Calendar.MILLISECOND)
					|| cal.get(Calendar.SECOND) > cal
							.getMinimum(Calendar.SECOND)
					|| cal.get(Calendar.MINUTE) > cal
							.getMinimum(Calendar.MINUTE)
					|| cal.get(Calendar.HOUR_OF_DAY) > cal
							.getMinimum(Calendar.HOUR_OF_DAY)
					|| cal.get(Calendar.DAY_OF_WEEK) > cal
							.getMinimum(Calendar.DAY_OF_WEEK)) {
				cal.set(Calendar.MILLISECOND,
						cal.getMinimum(Calendar.MILLISECOND));
				cal.set(Calendar.SECOND, cal.getMinimum(Calendar.SECOND));
				cal.set(Calendar.MINUTE, cal.getMinimum(Calendar.MINUTE));
				cal.set(Calendar.HOUR_OF_DAY,
						cal.getMinimum(Calendar.HOUR_OF_DAY));
				int woy = cal.get(Calendar.WEEK_OF_YEAR);
				cal.set(Calendar.DAY_OF_WEEK,
						cal.getMinimum(Calendar.DAY_OF_WEEK));
				cal.set(Calendar.WEEK_OF_YEAR, woy);
				return true;
			} else {
				return false;
			}
		case MONTH:
			if (cal.get(Calendar.MILLISECOND) > cal
					.getMinimum(Calendar.MILLISECOND)
					|| cal.get(Calendar.SECOND) > cal
							.getMinimum(Calendar.SECOND)
					|| cal.get(Calendar.MINUTE) > cal
							.getMinimum(Calendar.MINUTE)
					|| cal.get(Calendar.HOUR_OF_DAY) > cal
							.getMinimum(Calendar.HOUR_OF_DAY)
					|| cal.get(Calendar.DAY_OF_MONTH) > cal
							.getMinimum(Calendar.DAY_OF_MONTH)) {
				cal.set(Calendar.MILLISECOND,
						cal.getMinimum(Calendar.MILLISECOND));
				cal.set(Calendar.SECOND, cal.getMinimum(Calendar.SECOND));
				cal.set(Calendar.MINUTE, cal.getMinimum(Calendar.MINUTE));
				cal.set(Calendar.HOUR_OF_DAY,
						cal.getMinimum(Calendar.HOUR_OF_DAY));
				cal.set(Calendar.DAY_OF_MONTH,
						cal.getMinimum(Calendar.DAY_OF_MONTH));
				return true;
			} else {
				return false;
			}
		case YEAR:
			if (cal.get(Calendar.MILLISECOND) > cal
					.getMinimum(Calendar.MILLISECOND)
					|| cal.get(Calendar.SECOND) > cal
							.getMinimum(Calendar.SECOND)
					|| cal.get(Calendar.MINUTE) > cal
							.getMinimum(Calendar.MINUTE)
					|| cal.get(Calendar.HOUR_OF_DAY) > cal
							.getMinimum(Calendar.HOUR_OF_DAY)
					|| cal.get(Calendar.DAY_OF_YEAR) > cal
							.getMinimum(Calendar.DAY_OF_YEAR)) {
				cal.set(Calendar.MILLISECOND,
						cal.getMinimum(Calendar.MILLISECOND));
				cal.set(Calendar.SECOND, cal.getMinimum(Calendar.SECOND));
				cal.set(Calendar.MINUTE, cal.getMinimum(Calendar.MINUTE));
				cal.set(Calendar.HOUR_OF_DAY,
						cal.getMinimum(Calendar.HOUR_OF_DAY));
				cal.set(Calendar.DAY_OF_YEAR,
						cal.getMinimum(Calendar.DAY_OF_YEAR));
				return true;
			} else {
				return false;
			}
		default:
			return false;
		}
	}

	protected static void add(Calendar cal, Unit unit, int amount) {
		switch (unit) {
		case MILLISECOND:
			cal.add(Calendar.MILLISECOND, amount);
			break;
		case SECOND:
			cal.add(Calendar.SECOND, amount);
			break;
		case MINUTE:
			cal.add(Calendar.MINUTE, amount);
			break;
		case HOUR:
			cal.add(Calendar.HOUR_OF_DAY, amount);
			break;
		case DAY:
			cal.add(Calendar.DAY_OF_MONTH, amount);
			break;
		case WEEK:
			cal.add(Calendar.WEEK_OF_YEAR, amount);
			break;
		case MONTH:
			cal.add(Calendar.MONTH, amount);
			break;
		case YEAR:
			cal.add(Calendar.YEAR, amount);
			break;
		}
	}

	/**
	 * Returns the distance downward to a interval boundary.
	 * 
	 * @param cal
	 * @param itv
	 * @return the distance.
	 */
	protected static int distanceToIntervalBoundary(Calendar cal,
			DateInterval itv) {
		int v = 0, min = 0;
		switch (itv.getUnit()) {
		case MILLISECOND:
			v = cal.get(Calendar.MILLISECOND);
			min = cal.getMinimum(Calendar.MILLISECOND);
			break;
		case SECOND:
			v = cal.get(Calendar.SECOND);
			min = cal.getMinimum(Calendar.SECOND);
			break;
		case MINUTE:
			v = cal.get(Calendar.MINUTE);
			min = cal.getMinimum(Calendar.MINUTE);
			break;
		case HOUR:
			v = cal.get(Calendar.HOUR_OF_DAY);
			min = cal.getMinimum(Calendar.HOUR_OF_DAY);
			break;
		case DAY:
			v = cal.get(Calendar.DAY_OF_MONTH);
			min = cal.getMinimum(Calendar.DAY_OF_MONTH);
			break;
		case WEEK:
			v = cal.get(Calendar.WEEK_OF_YEAR);
			min = cal.getMinimum(Calendar.WEEK_OF_YEAR);
			break;
		case MONTH:
			v = cal.get(Calendar.MONTH);
			min = cal.getMinimum(Calendar.MONTH);
			break;
		case YEAR:
			v = cal.get(Calendar.YEAR);
			min = 0;
			break;
		}

		int delta = (v - min) % itv.getValue();
		return delta;
	}

}
