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
public class DateTickCalculator extends LongTickCalculator implements RangeAdvisor {

	private final TimeZone zone;

	private final Locale locale;

	/**
	 * Always be positive
	 */
	private DateInterval dateInterval;

	private int minorNumber;

	private long[] tickValues;

	private long[] minorValues;

	public DateTickCalculator(TimeZone zone, Locale locale) {
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

		double rough = (double) (hi - lo) / (tickNumber - 1);
		DateInterval[] itvAB = calcCandidateIntervals(rough);
		DateInterval itvA = itvAB[0];
		DateInterval itvB = itvAB[1];

		long iLoA = lo / itvA.getTimeInMillis();
		long iHiA = hi / itvA.getTimeInMillis();
		if (hi % itvA.getTimeInMillis() != 0) {
			iHiA++;
		}
		int tickNumA = (int) (iHiA - iLoA + 1);
		long iLoB = lo / itvB.getTimeInMillis();
		long iHiB = hi / itvB.getTimeInMillis();
		if (hi % itvB.getTimeInMillis() != 0) {
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
	 * Calculate 2 candidate DateInterval based on rough interval in milliseconds
	 * 
	 * @param interval
	 *            the rough interval in milliseconds
	 * @return 2 candidate DateInterval in a array
	 */
	protected static DateInterval[] calcCandidateIntervals(double rough) {
		DateInterval itvA, itvB;

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

		return new DateInterval[] { itvA, itvB };
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

		Calendar loCal = Calendar.getInstance(zone, locale);
		loCal.setTimeInMillis(lo);
		Calendar hiCal = Calendar.getInstance(zone, locale);
		hiCal.setTimeInMillis(hi);

		Calendar t1cal = (Calendar) loCal.clone();
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
				// System.out.println(t1cal.getTimeInMillis());
				ticks.add(t1cal.getTimeInMillis());
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
					ticks.add(t1cal.getTimeInMillis());
				} else {
					mticks.add(t1cal.getTimeInMillis());
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
			dateInterval = new DateInterval(Unit.MILLISECOND, 1);
		} else {
			dateInterval = calcInterval(lo, hi, tickNumber);
			expandRangeByTickInterval();
		}
	}

	public void expandRangeByTickInterval(double interval) {
		dateInterval = DateInterval.createWithMillis((long) Math.round(interval));
		expandRangeByTickInterval();

		/* if _lo == _hi and on interval boundary, the expanding has no effect */
		if (lo == hi) {
			Calendar hiCal = Calendar.getInstance(zone, locale);
			hiCal.setTimeInMillis(hi);
			add(hiCal, dateInterval.getUnit(), dateInterval.getValue());
			hi = hiCal.getTimeInMillis();
		}
	}

	protected void expandRangeByTickInterval() {

		Calendar loCal = Calendar.getInstance(zone, locale);
		loCal.setTimeInMillis(lo);
		Calendar hiCal = Calendar.getInstance(zone, locale);
		hiCal.setTimeInMillis(hi);

		setCalendarBelowToMin(loCal, dateInterval.getUnit());
		int loDelta = distanceToIntervalBoundary(loCal, dateInterval);
		if (loDelta != 0) {
			add(loCal, dateInterval.getUnit(), -loDelta);
		}

		setCalendarBelowToMin(hiCal, dateInterval.getUnit());
		if (hiCal.getTimeInMillis() < hi) {
			add(hiCal, dateInterval.getUnit(), 1);
		}
		int hiDelta = distanceToIntervalBoundary(hiCal, dateInterval);
		if (hiDelta != 0) {
			add(hiCal, dateInterval.getUnit(), dateInterval.getValue() - hiDelta);
		}

		lo = loCal.getTimeInMillis();
		hi = hiCal.getTimeInMillis();

	}

	public void calcValuesByTickNumber(int tickNumber, int minorTickNumber) {
		DateInterval interval = calcInterval(lo, hi, tickNumber);
		calcValuesByTickInterval(interval, 0, minorTickNumber);
	}

	public void calcValuesByTickInterval(long interval, long offset, int minorTickNumber) {
		calcValuesByTickInterval(DateInterval.createWithMillis(interval), offset, minorTickNumber);
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
		return dateInterval.getTimeInMillis();
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
		Calendar loCal = Calendar.getInstance(zone, locale);
		loCal.setTimeInMillis(lo);
		Calendar hiCal = Calendar.getInstance(zone, locale);
		hiCal.setTimeInMillis(hi);

		Unit umax = getFirsNonEqualField(loCal, hiCal);
		return calcLabelFormat(dateInterval.getUnit(), umax);
	}

	public Format calcLabelTextFormat(Object canonicalValues) {
		return null;
	}

	public String calcLabelFormatString(Object values) {
		if (((long[]) values).length == 0) {
			return "";
		}

		Calendar cal = Calendar.getInstance(zone, locale);
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

		Calendar loCal = Calendar.getInstance(zone, locale);
		loCal.setTimeInMillis(lo);
		Calendar hiCal = Calendar.getInstance(zone, locale);
		hiCal.setTimeInMillis(hi);
		Unit umax = getFirsNonEqualField(loCal, hiCal);

		return calcLabelFormat(umin, umax);
	}

	public boolean isValidFormat(String format) {
		try {
			String.format(format, Calendar.getInstance(zone, locale));
			return true;
		} catch (IllegalArgumentException e) {
			return false;
		}
	}

	protected static String calcLabelFormat(Unit uprec, Unit umdiff) {
		if (uprec.time > umdiff.time) {
			throw new IllegalArgumentException("The MaxDiff unit must larger than precision unit.");
		}

		/* format string for hh:mm:ss.mmm without the % char */
		String hms;
		switch (uprec) {
		case MICROSECOND:
			hms = "tT.%<tN";
			break;
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
		if (cal.get(Calendar.MILLISECOND) > cal.getMinimum(Calendar.MILLISECOND)) {
			return Unit.MILLISECOND;
		}
		if (cal.get(Calendar.SECOND) > cal.getMinimum(Calendar.SECOND)) {
			return Unit.SECOND;
		}
		if (cal.get(Calendar.MINUTE) > cal.getMinimum(Calendar.MINUTE)) {
			return Unit.MINUTE;
		}
		if (cal.get(Calendar.HOUR_OF_DAY) > cal.getMinimum(Calendar.HOUR_OF_DAY)) {
			return Unit.HOUR;
		}
		if (cal.get(Calendar.DAY_OF_MONTH) > cal.getMinimum(Calendar.DAY_OF_MONTH)) {
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
	 * @return true if any fields below the given unit is larger than its minimal value
	 */
	protected static void setCalendarBelowToMin(Calendar cal, Unit unit) {
		switch (unit) {
		case MILLISECOND:
			break;
		case SECOND:
			cal.set(Calendar.MILLISECOND, cal.getMinimum(Calendar.MILLISECOND));
			break;
		case MINUTE:
			cal.set(Calendar.MILLISECOND, cal.getMinimum(Calendar.MILLISECOND));
			cal.set(Calendar.SECOND, cal.getMinimum(Calendar.SECOND));
			break;
		case HOUR:
			cal.set(Calendar.MILLISECOND, cal.getMinimum(Calendar.MILLISECOND));
			cal.set(Calendar.SECOND, cal.getMinimum(Calendar.SECOND));
			cal.set(Calendar.MINUTE, cal.getMinimum(Calendar.MINUTE));
			break;
		case DAY:
			cal.set(Calendar.MILLISECOND, cal.getMinimum(Calendar.MILLISECOND));
			cal.set(Calendar.SECOND, cal.getMinimum(Calendar.SECOND));
			cal.set(Calendar.MINUTE, cal.getMinimum(Calendar.MINUTE));
			cal.set(Calendar.HOUR_OF_DAY, cal.getMinimum(Calendar.HOUR_OF_DAY));
			break;
		case WEEK:
			cal.set(Calendar.MILLISECOND, cal.getMinimum(Calendar.MILLISECOND));
			cal.set(Calendar.SECOND, cal.getMinimum(Calendar.SECOND));
			cal.set(Calendar.MINUTE, cal.getMinimum(Calendar.MINUTE));
			cal.set(Calendar.HOUR_OF_DAY, cal.getMinimum(Calendar.HOUR_OF_DAY));
			int woy = cal.get(Calendar.WEEK_OF_YEAR);
			cal.set(Calendar.DAY_OF_WEEK, cal.getMinimum(Calendar.DAY_OF_WEEK));
			cal.set(Calendar.WEEK_OF_YEAR, woy);
			break;
		case MONTH:
			cal.set(Calendar.MILLISECOND, cal.getMinimum(Calendar.MILLISECOND));
			cal.set(Calendar.SECOND, cal.getMinimum(Calendar.SECOND));
			cal.set(Calendar.MINUTE, cal.getMinimum(Calendar.MINUTE));
			cal.set(Calendar.HOUR_OF_DAY, cal.getMinimum(Calendar.HOUR_OF_DAY));
			cal.set(Calendar.DAY_OF_MONTH, cal.getMinimum(Calendar.DAY_OF_MONTH));
			break;
		case YEAR:
			cal.set(Calendar.MILLISECOND, cal.getMinimum(Calendar.MILLISECOND));
			cal.set(Calendar.SECOND, cal.getMinimum(Calendar.SECOND));
			cal.set(Calendar.MINUTE, cal.getMinimum(Calendar.MINUTE));
			cal.set(Calendar.HOUR_OF_DAY, cal.getMinimum(Calendar.HOUR_OF_DAY));
			cal.set(Calendar.DAY_OF_YEAR, cal.getMinimum(Calendar.DAY_OF_YEAR));
			break;
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
	protected static int distanceToIntervalBoundary(Calendar cal, DateInterval itv) {
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

	public MathElement[] formatValues(String format, Object values) {
		Calendar cal = Calendar.getInstance(zone, locale);

		int n = Array.getLength(values);
		MathElement[] labels = new MathElement[n];
		for (int i = 0; i < n; i++) {
			cal.setTimeInMillis(Array.getLong(values, i));

			@SuppressWarnings("resource")
			String texString = new Formatter(locale).format(format, cal).toString();
			if (texString.indexOf('$') == -1) {
				labels[i] = new MathElement.Mtext(texString);
			} else {
				labels[i] = TeXMathUtils.parseText(texString);
			}
		}
		return labels;
	}

}
