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

import java.lang.reflect.Array;
import java.text.Format;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Formatter;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import org.jplot2d.axtick.DateInterval.Unit;
import org.jplot2d.tex.MathElement;
import org.jplot2d.tex.TeXMathUtils;

/**
 * 
 * @author Jingjing Li
 * 
 */
public class TAIMicrosTickCalculator extends LongTickCalculator implements RangeAdvisor {

	private final TimeZone zone;

	private final Locale locale;

	/**
	 * Always be positive
	 */
	private DateInterval dateInterval;

	private int minorNumber;

	private long[] tickValues;

	private long[] minorValues;

	public TAIMicrosTickCalculator(TimeZone zone, Locale locale) {
		this.zone = zone;
		this.locale = locale;
	}

	protected static DateInterval calcInterval(long start, long end, int tickNumber) {

		if (start == end) {
			throw new IllegalArgumentException("The range span must be great than zero");
		}
		if (tickNumber <= 0) {
			throw new IllegalArgumentException("The ticks number must be great than zero");
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
				itvA = new DateInterval(Unit.MICROSECOND, scale * 1);
				itvB = new DateInterval(Unit.MICROSECOND, scale * 2);
			} else if (coeff < 5) {
				itvA = new DateInterval(Unit.MICROSECOND, scale * 2);
				itvB = new DateInterval(Unit.MICROSECOND, scale * 5);
			} else {
				itvA = new DateInterval(Unit.MICROSECOND, scale * 5);
				itvB = new DateInterval(Unit.MICROSECOND, scale * 10);
			}
		} else {
			DateInterval[] itvAB = DateTickCalculator.calcCandidateIntervals(rough / 1000);
			itvA = itvAB[0];
			itvB = itvAB[1];
		}

		long iLoA = lo / itvA.getTimeInMicros();
		long iHiA = hi / itvA.getTimeInMicros();
		if (hi % itvA.getTimeInMicros() != 0) {
			iHiA++;
		}
		int tickNumA = (int) (iHiA - iLoA + 1);
		long iLoB = lo / itvB.getTimeInMicros();
		long iHiB = hi / itvB.getTimeInMicros();
		if (hi % itvB.getTimeInMicros() != 0) {
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

		if (dateInterval.getValue() == 0)
			throw new IllegalArgumentException("delta cannot be zero");

		TAIMicrosCalendar loCal = new TAIMicrosCalendar(zone, locale);
		loCal.setTimeInMicros(lo);
		TAIMicrosCalendar hiCal = new TAIMicrosCalendar(zone, locale);
		hiCal.setTimeInMicros(hi);

		TAIMicrosCalendar t1cal = (TAIMicrosCalendar) loCal.clone();
		/* round up to unit */
		setCalendarBelowToMin(t1cal, dateInterval.getUnit());
		if (t1cal.before(loCal)) {
			add(t1cal, dateInterval.getUnit(), 1);
		}

		List<Long> ticks = new ArrayList<Long>();
		List<Long> mticks = new ArrayList<Long>();
		if (minorNumber == 0) {
			int delta = distanceToIntervalBoundary(t1cal, dateInterval);
			if (delta != 0) {
				add(t1cal, dateInterval.getUnit(), dateInterval.getValue() - delta);
			}
			while (!t1cal.after(hiCal)) {
				// System.out.println(t1cal.getTimeInMicros());
				ticks.add(t1cal.getTimeInMicros());
				add(t1cal, dateInterval.getUnit(), dateInterval.getValue());
			}
		} else {
			int mitv = dateInterval.getValue() / (minorNumber + 1);
			DateInterval minorInterval = new DateInterval(dateInterval.getUnit(), mitv);
			int delta = distanceToIntervalBoundary(t1cal, minorInterval);
			if (delta != 0) {
				add(t1cal, dateInterval.getUnit(), mitv - delta);
			}
			while (!t1cal.after(hiCal)) {
				if (distanceToIntervalBoundary(t1cal, dateInterval) == 0) {
					ticks.add(t1cal.getTimeInMicros());
				} else {
					mticks.add(t1cal.getTimeInMicros());
				}
				add(t1cal, dateInterval.getUnit(), mitv);
			}

		}

		tickValues = new long[ticks.size()];
		minorValues = new long[mticks.size()];
		if (inverted) {
			for (int i = 0, j = tickValues.length - 1; i < tickValues.length; i++, j--) {
				tickValues[i] = ticks.get(j);
			}
			for (int i = 0, j = minorValues.length - 1; i < minorValues.length; i++, j--) {
				minorValues[i] = mticks.get(j);
			}
		} else {
			for (int i = 0; i < tickValues.length; i++) {
				tickValues[i] = ticks.get(i);
			}
			for (int i = 0; i < minorValues.length; i++) {
				minorValues[i] = mticks.get(i);
			}
		}
	}

	public void expandRangeByTickNumber(int tickNumber) {
		if (tickNumber <= 0) {
			throw new IllegalArgumentException("tick number must be positive.");
		} else if (tickNumber == 1) {
			tickNumber = 2;
		}

		long span = hi - lo;
		if (span < tickNumber - 1) {
			/* expand range to tick number */
			long halfXpand = (tickNumber - 1 - span) / 2;
			long odd = (tickNumber - 1 - span) % 2;
			lo -= halfXpand;
			hi += halfXpand;
			hi += odd;
			if (lo < 0) {
				lo = 0;
				hi -= lo;
			}
			dateInterval = new DateInterval(Unit.MICROSECOND, 1);
		} else {
			dateInterval = calcInterval(lo, hi, tickNumber);
			expandRangeByTickInterval();
		}
	}

	public void expandRangeByTickInterval(double interval) {
		dateInterval = DateInterval.createWithMicros((long) Math.round(interval));
		expandRangeByTickInterval();

		/* if _lo == _hi and on interval boundary, the expanding has no effect */
		if (lo == hi) {
			TAIMicrosCalendar hiCal = new TAIMicrosCalendar(zone, locale);
			hiCal.setTimeInMicros(hi);
			add(hiCal, dateInterval.getUnit(), dateInterval.getValue());
			hi = hiCal.getTimeInMicros();
		}
	}

	protected void expandRangeByTickInterval() {

		TAIMicrosCalendar loCal = new TAIMicrosCalendar(zone, locale);
		loCal.setTimeInMicros(lo);
		TAIMicrosCalendar hiCal = new TAIMicrosCalendar(zone, locale);
		hiCal.setTimeInMicros(hi);

		setCalendarBelowToMin(loCal, dateInterval.getUnit());
		int loDelta = distanceToIntervalBoundary(loCal, dateInterval);
		if (loDelta != 0) {
			add(loCal, dateInterval.getUnit(), -loDelta);
		}

		setCalendarBelowToMin(hiCal, dateInterval.getUnit());
		if (hiCal.getTimeInMicros() < hi) {
			add(hiCal, dateInterval.getUnit(), 1);
		}
		int hiDelta = distanceToIntervalBoundary(hiCal, dateInterval);
		if (hiDelta != 0) {
			add(hiCal, dateInterval.getUnit(), dateInterval.getValue() - hiDelta);
		}

		lo = loCal.getTimeInMicros();
		hi = hiCal.getTimeInMicros();

	}

	public void calcValuesByTickNumber(int tickNumber, int minorTickNumber) {
		DateInterval interval = calcInterval(lo, hi, tickNumber);
		calcValuesByTickInterval(interval, 0, minorTickNumber);
	}

	public void calcValuesByTickInterval(long interval, long offset, int minorTickNumber) {
		calcValuesByTickInterval(DateInterval.createWithMicros(interval), offset, minorTickNumber);
	}

	private void calcValuesByTickInterval(DateInterval interval, long offset, int minorTickNumber) {
		dateInterval = interval;

		if (dateInterval.getValue() == 1) {
			minorNumber = 0;
		} else {
			if (minorTickNumber == AUTO_MINORTICK_NUMBER) {
				minorTickNumber = 3;
			}
			minorNumber = calcMinorNumber(dateInterval.getValue(), minorTickNumber);
		}
		calcValues();
	}

	public double getInterval() {
		return dateInterval.getTimeInMicros();
	}

	public int getMinorNumber() {
		return minorNumber;
	}

	public long[] getValues() {
		return tickValues;
	}

	public long[] getMinorValues() {
		return minorValues;
	}

	public String getLabelFormate() {
		TAIMicrosCalendar loCal = new TAIMicrosCalendar(zone, locale);
		loCal.setTimeInMicros(lo);
		TAIMicrosCalendar hiCal = new TAIMicrosCalendar(zone, locale);
		hiCal.setTimeInMicros(hi);

		Unit umax = getFirsNonEqualField(loCal, hiCal);
		return DateTickCalculator.calcLabelFormat(dateInterval.getUnit(), umax);
	}

	public Format calcLabelTextFormat(Object canonicalValues) {
		return null;
	}

	public String calcLabelFormatString(Object values) {
		if (((long[]) values).length == 0) {
			return "";
		}

		TAIMicrosCalendar cal = new TAIMicrosCalendar(zone, locale);
		Unit umin = Unit.YEAR;
		long lo = Long.MAX_VALUE, hi = 0;
		for (long v : (long[]) values) {
			if (lo > v) {
				lo = v;
			}
			if (hi < v) {
				hi = v;
			}
			cal.setTimeInMicros(v);
			Unit lnmf = getLastNonMinField(cal);
			if (umin.time > lnmf.time) {
				umin = lnmf;
			}
		}

		TAIMicrosCalendar loCal = new TAIMicrosCalendar(zone, locale);
		loCal.setTimeInMicros(lo);
		TAIMicrosCalendar hiCal = new TAIMicrosCalendar(zone, locale);
		hiCal.setTimeInMicros(hi);
		Unit umax = getFirsNonEqualField(loCal, hiCal);

		return DateTickCalculator.calcLabelFormat(umin, umax);
	}

	public boolean isValidFormat(String format) {
		try {
			String.format(format, Calendar.getInstance(zone, locale));
			return true;
		} catch (IllegalArgumentException e) {
			return false;
		}
	}

	protected static Unit getLastNonMinField(TAIMicrosCalendar cal) {
		if (cal.get(TAIMicrosCalendar.MICROSECOND) > cal.getMinimum(TAIMicrosCalendar.MICROSECOND)) {
			return Unit.MICROSECOND;
		} else {
			return DateTickCalculator.getLastNonMinField(cal.calendar);
		}
	}

	protected static Unit getFirsNonEqualField(TAIMicrosCalendar a, TAIMicrosCalendar b) {
		Unit u = DateTickCalculator.getFirsNonEqualField(a.calendar, b.calendar);
		if (u != null) {
			return u;
		} else if (a.get(TAIMicrosCalendar.MICROSECOND) != b.get(TAIMicrosCalendar.MICROSECOND)) {
			return Unit.MICROSECOND;
		}
		return null;
	}

	/**
	 * Sets all calendar fields below the given unit to its minimal value.
	 * 
	 * @param cal
	 * @param unit
	 * @return true if any fields below the given unit is larger than its minimal value
	 */
	protected static void setCalendarBelowToMin(TAIMicrosCalendar cal, Unit unit) {
		switch (unit) {
		case MICROSECOND:
			break;
		default:
			DateTickCalculator.setCalendarBelowToMin(cal.calendar, unit);
			cal.set(TAIMicrosCalendar.MICROSECOND, 0);
			break;
		}
	}

	protected static void add(TAIMicrosCalendar cal, Unit unit, int amount) {
		switch (unit) {
		case MICROSECOND:
			cal.add(TAIMicrosCalendar.MICROSECOND, amount);
			break;
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
	protected static int distanceToIntervalBoundary(TAIMicrosCalendar cal, DateInterval itv) {
		switch (itv.getUnit()) {
		case MICROSECOND:
			return cal.get(TAIMicrosCalendar.MICROSECOND) % itv.getValue();
		default:
			return DateTickCalculator.distanceToIntervalBoundary(cal.calendar, itv);
		}
	}

	public MathElement[] formatValues(String format, Object values) {
		TAIMicrosCalendar cal = new TAIMicrosCalendar(zone, locale);

		int n = Array.getLength(values);
		MathElement[] labels = new MathElement[n];
		for (int i = 0; i < n; i++) {
			cal.setTimeInMicros(Array.getLong(values, i));

			@SuppressWarnings("resource")
			String microString = String.valueOf(cal.microsecond);
			if (microString.length() == 1) {
				microString = "00" + microString;
			} else if (microString.length() == 2) {
				microString = "0" + microString;
			}

			String texString = new Formatter(locale).format(format, cal.calendar).toString();
			int z6idx = texString.lastIndexOf("000000");
			texString = texString.substring(0, z6idx) + microString + texString.substring(z6idx + 6);

			if (texString.indexOf('$') == -1) {
				labels[i] = new MathElement.Mtext(texString);
			} else {
				labels[i] = TeXMathUtils.parseText(texString);
			}
		}
		return labels;
	}

}
