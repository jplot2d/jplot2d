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

import static org.jplot2d.util.TestUtils.*;
import static org.junit.Assert.*;

import org.jplot2d.axtick.DateInterval.Unit;
import org.jplot2d.axtick.TAIMicrosCalendar;

import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author Jingjing Li
 * 
 */
public class TAITickCalculatorTest {

	private static TimeZone zone = TimeZone.getTimeZone("UTC");

	private static Locale locale = Locale.US;

	private static TAIMicrosTickCalculator taiTC;

	@BeforeClass
	public static void setUpBeforeClass() {
		taiTC = TAIMicrosTickAlgorithm.getInstance(zone, locale).createCalculator();
	}

	@Test
	public void testSetCalendarBelowToMin() {
		TAIMicrosCalendar cal = new TAIMicrosCalendar(zone, locale);
		cal.set(1975, 5, 19, 7, 30, 32);

		TAIMicrosCalendar refCal = (TAIMicrosCalendar) cal.clone();

		TAIMicrosTickCalculator.setCalendarBelowToMin(cal, Unit.MICROSECOND);
		assertEquals(cal, refCal);

		TAIMicrosTickCalculator.setCalendarBelowToMin(cal, Unit.SECOND);
		refCal.set(Calendar.MILLISECOND, 0);
		assertEquals(cal, refCal);

		TAIMicrosTickCalculator.setCalendarBelowToMin(cal, Unit.MINUTE);
		refCal.set(Calendar.SECOND, 0);
		assertEquals(cal, refCal);

		TAIMicrosTickCalculator.setCalendarBelowToMin(cal, Unit.HOUR);
		refCal.set(Calendar.MINUTE, 0);
		assertEquals(cal, refCal);

		TAIMicrosTickCalculator.setCalendarBelowToMin(cal, Unit.DAY);
		refCal.set(Calendar.HOUR_OF_DAY, 0);
		assertEquals(cal, refCal);

		TAIMicrosTickCalculator.setCalendarBelowToMin(cal, Unit.WEEK);
		int woy = refCal.get(Calendar.WEEK_OF_YEAR);
		refCal.set(Calendar.DAY_OF_WEEK, cal.getMinimum(Calendar.DAY_OF_WEEK));
		refCal.set(Calendar.WEEK_OF_YEAR, woy);
		assertEquals(cal, refCal);

		TAIMicrosTickCalculator.setCalendarBelowToMin(cal, Unit.MONTH);
		refCal.set(Calendar.DAY_OF_MONTH, 1);
		assertEquals(cal, refCal);

		TAIMicrosTickCalculator.setCalendarBelowToMin(cal, Unit.YEAR);
		refCal.set(Calendar.DAY_OF_YEAR, cal.getMinimum(Calendar.DAY_OF_YEAR));
		assertEquals(cal, refCal);
	}

	@Test
	public void testCalcInterval() {
		TAIMicrosCalendar start = new TAIMicrosCalendar(zone, locale);
		TAIMicrosCalendar end = new TAIMicrosCalendar(zone, locale);
		start.set(1975, 5, 19, 7, 30, 0);
		start.set(Calendar.MILLISECOND, 0);
		DateInterval di;
		Unit umax;

		end.set(1975, 5, 19, 7, 30, 0);
		end.set(Calendar.MILLISECOND, 0);
		end.set(TAIMicrosCalendar.MICROSECOND, 15);
		di = TAIMicrosTickCalculator.calcInterval(start.getTimeInMicros(), end.getTimeInMicros(), 11);
		assertEquals(di, new DateInterval(DateInterval.Unit.MICROSECOND, 2));
		umax = TAIMicrosTickCalculator.getFirsNonEqualField(start, end);
		assertEquals(umax, Unit.MICROSECOND);

		end.set(1975, 5, 19, 7, 30, 0);
		end.set(Calendar.MILLISECOND, 0);
		end.set(TAIMicrosCalendar.MICROSECOND, 400);
		di = TAIMicrosTickCalculator.calcInterval(start.getTimeInMicros(), end.getTimeInMicros(), 11);
		assertEquals(di, new DateInterval(DateInterval.Unit.MICROSECOND, 50));
		umax = TAIMicrosTickCalculator.getFirsNonEqualField(start, end);
		assertEquals(umax, Unit.MICROSECOND);

		end.set(1975, 5, 19, 7, 30, 0);
		end.set(Calendar.MILLISECOND, 0);
		end.set(TAIMicrosCalendar.MICROSECOND, 800);
		di = TAIMicrosTickCalculator.calcInterval(start.getTimeInMicros(), end.getTimeInMicros(), 11);
		assertEquals(di, new DateInterval(DateInterval.Unit.MICROSECOND, 100));
		umax = TAIMicrosTickCalculator.getFirsNonEqualField(start, end);
		assertEquals(umax, Unit.MICROSECOND);

		end.set(TAIMicrosCalendar.MICROSECOND, 0);

		end.set(1975, 5, 19, 7, 30, 0);
		end.set(Calendar.MILLISECOND, 1);
		di = TAIMicrosTickCalculator.calcInterval(start.getTimeInMicros(), end.getTimeInMicros(), 11);
		assertEquals(di, new DateInterval(DateInterval.Unit.MICROSECOND, 100));
		umax = TAIMicrosTickCalculator.getFirsNonEqualField(start, end);
		assertEquals(umax, Unit.MILLISECOND);

		end.set(1975, 5, 19, 7, 30, 0);
		end.set(Calendar.MILLISECOND, 4);
		di = TAIMicrosTickCalculator.calcInterval(start.getTimeInMicros(), end.getTimeInMicros(), 11);
		assertEquals(di, new DateInterval(DateInterval.Unit.MICROSECOND, 500));
		umax = TAIMicrosTickCalculator.getFirsNonEqualField(start, end);
		assertEquals(umax, Unit.MILLISECOND);

		end.set(1975, 5, 19, 7, 30, 0);
		end.set(Calendar.MILLISECOND, 15);
		di = TAIMicrosTickCalculator.calcInterval(start.getTimeInMicros(), end.getTimeInMicros(), 11);
		assertEquals(di, new DateInterval(DateInterval.Unit.MILLISECOND, 2));
		umax = TAIMicrosTickCalculator.getFirsNonEqualField(start, end);
		assertEquals(umax, Unit.MILLISECOND);

		end.set(1975, 5, 19, 7, 30, 0);
		end.set(Calendar.MILLISECOND, 40);
		di = TAIMicrosTickCalculator.calcInterval(start.getTimeInMicros(), end.getTimeInMicros(), 11);
		assertEquals(di, new DateInterval(DateInterval.Unit.MILLISECOND, 5));
		umax = TAIMicrosTickCalculator.getFirsNonEqualField(start, end);
		assertEquals(umax, Unit.MILLISECOND);

		end.set(1975, 5, 19, 7, 30, 0);
		end.set(Calendar.MILLISECOND, 80);
		di = TAIMicrosTickCalculator.calcInterval(start.getTimeInMicros(), end.getTimeInMicros(), 11);
		assertEquals(di, new DateInterval(DateInterval.Unit.MILLISECOND, 10));
		umax = TAIMicrosTickCalculator.getFirsNonEqualField(start, end);
		assertEquals(umax, Unit.MILLISECOND);

		end.set(1975, 5, 19, 7, 30, 1);
		end.set(Calendar.MILLISECOND, 0);
		di = TAIMicrosTickCalculator.calcInterval(start.getTimeInMicros(), end.getTimeInMicros(), 11);
		assertEquals(di, new DateInterval(DateInterval.Unit.MILLISECOND, 100));
		umax = TAIMicrosTickCalculator.getFirsNonEqualField(start, end);
		assertEquals(umax, Unit.SECOND);

		end.set(1975, 5, 19, 7, 31, 0);
		end.set(Calendar.MILLISECOND, 0);
		di = TAIMicrosTickCalculator.calcInterval(start.getTimeInMicros(), end.getTimeInMicros(), 11);
		assertEquals(di, new DateInterval(DateInterval.Unit.SECOND, 5));
		umax = TAIMicrosTickCalculator.getFirsNonEqualField(start, end);
		assertEquals(umax, Unit.MINUTE);

		end.set(1975, 5, 19, 8, 30, 0);
		end.set(Calendar.MILLISECOND, 0);
		di = TAIMicrosTickCalculator.calcInterval(start.getTimeInMicros(), end.getTimeInMicros(), 11);
		assertEquals(di, new DateInterval(DateInterval.Unit.MINUTE, 5));
		umax = TAIMicrosTickCalculator.getFirsNonEqualField(start, end);
		assertEquals(umax, Unit.HOUR);

		end.set(1975, 5, 19, 22, 30, 0);
		end.set(Calendar.MILLISECOND, 0);
		di = TAIMicrosTickCalculator.calcInterval(start.getTimeInMicros(), end.getTimeInMicros(), 11);
		assertEquals(di, new DateInterval(DateInterval.Unit.HOUR, 2));
		umax = TAIMicrosTickCalculator.getFirsNonEqualField(start, end);
		assertEquals(umax, Unit.HOUR);

		end.set(1975, 5, 20, 7, 30, 0);
		end.set(Calendar.MILLISECOND, 0);
		di = TAIMicrosTickCalculator.calcInterval(start.getTimeInMicros(), end.getTimeInMicros(), 11);
		assertEquals(di, new DateInterval(DateInterval.Unit.HOUR, 3));
		umax = TAIMicrosTickCalculator.getFirsNonEqualField(start, end);
		assertEquals(umax, Unit.DAY);

		end.set(1975, 5, 21, 7, 30, 0);
		end.set(Calendar.MILLISECOND, 0);
		di = TAIMicrosTickCalculator.calcInterval(start.getTimeInMicros(), end.getTimeInMicros(), 11);
		assertEquals(di, new DateInterval(DateInterval.Unit.HOUR, 6));
		umax = TAIMicrosTickCalculator.getFirsNonEqualField(start, end);
		assertEquals(umax, Unit.DAY);

		end.set(1975, 6, 19, 7, 30, 0);
		end.set(Calendar.MILLISECOND, 0);
		di = TAIMicrosTickCalculator.calcInterval(start.getTimeInMicros(), end.getTimeInMicros(), 15);
		assertEquals(di, new DateInterval(DateInterval.Unit.DAY, 2));
		umax = TAIMicrosTickCalculator.getFirsNonEqualField(start, end);
		assertEquals(umax, Unit.MONTH);

		end.set(1975, 6, 19, 7, 30, 0);
		end.set(Calendar.MILLISECOND, 0);
		di = TAIMicrosTickCalculator.calcInterval(start.getTimeInMicros(), end.getTimeInMicros(), 11);
		assertEquals(di, new DateInterval(DateInterval.Unit.WEEK, 1));
		umax = TAIMicrosTickCalculator.getFirsNonEqualField(start, end);
		assertEquals(umax, Unit.MONTH);

		end.set(1976, 5, 19, 7, 30, 0);
		end.set(Calendar.MILLISECOND, 0);
		di = TAIMicrosTickCalculator.calcInterval(start.getTimeInMicros(), end.getTimeInMicros(), 11);
		assertEquals(di, new DateInterval(DateInterval.Unit.MONTH, 1));
		umax = TAIMicrosTickCalculator.getFirsNonEqualField(start, end);
		assertEquals(umax, Unit.YEAR);

		end.set(1985, 5, 19, 7, 30, 0);
		end.set(Calendar.MILLISECOND, 0);
		di = TAIMicrosTickCalculator.calcInterval(start.getTimeInMicros(), end.getTimeInMicros(), 11);
		assertEquals(di, new DateInterval(DateInterval.Unit.YEAR, 1));
		umax = TAIMicrosTickCalculator.getFirsNonEqualField(start, end);
		assertEquals(umax, Unit.YEAR);

		end.set(2000, 5, 19, 7, 30, 0);
		end.set(Calendar.MILLISECOND, 0);
		di = TAIMicrosTickCalculator.calcInterval(start.getTimeInMicros(), end.getTimeInMicros(), 11);
		assertEquals(di, new DateInterval(DateInterval.Unit.YEAR, 2));
		umax = TAIMicrosTickCalculator.getFirsNonEqualField(start, end);
		assertEquals(umax, Unit.YEAR);

	}

	/**
	 * calculate tick by tick number and minor number
	 */
	@Test
	public void testDateTicks() {
		TimeZone zone = TimeZone.getTimeZone("UTC");
		Locale locale = Locale.US;
		TAIMicrosCalendar start = new TAIMicrosCalendar(zone, locale);
		TAIMicrosCalendar end = new TAIMicrosCalendar(zone, locale);
		start.set(1975, 5, 19, 7, 30, 0);
		start.set(Calendar.MILLISECOND, 0);
		long startmicros = start.getTimeInMicros();

		taiTC.setRange(startmicros, startmicros);
		try {
			taiTC.calcValuesByTickNumber(11, 0);
			fail("IllegalArgumentException should be thrown");
		} catch (IllegalArgumentException e) {

		}

		end.set(1975, 5, 19, 7, 30, 0);
		end.set(Calendar.MILLISECOND, 1);
		taiTC.setRange(startmicros, end.getTimeInMicros());
		taiTC.calcValuesByTickNumber(11, TickCalculator.AUTO_MINORTICK_NUMBER);
		checkDouble(taiTC.getInterval(), 100);
		assertEquals(taiTC.getMinorNumber(), 4);
		checkLongArray(taiTC.getValues(), startmicros, startmicros + 100, startmicros + 200, startmicros + 300,
				startmicros + 400, startmicros + 500, startmicros + 600, startmicros + 700, startmicros + 800,
				startmicros + 900, startmicros + 1000);
		assertEquals(taiTC.getLabelFormate(), "%tT.%<tN");

		end.set(1975, 5, 19, 7, 30, 1);
		end.set(Calendar.MILLISECOND, 0);
		taiTC.setRange(startmicros, end.getTimeInMicros());
		taiTC.calcValuesByTickNumber(11, TickCalculator.AUTO_MINORTICK_NUMBER);
		checkDouble(taiTC.getInterval(), 100e3);
		assertEquals(taiTC.getMinorNumber(), 4);
		checkLongArray(taiTC.getValues(), startmicros, startmicros + 100000, startmicros + 200000,
				startmicros + 300000, startmicros + 400000, startmicros + 500000, startmicros + 600000,
				startmicros + 700000, startmicros + 800000, startmicros + 900000, startmicros + 1000000);
		assertEquals(taiTC.getLabelFormate(), "%tT.%<tL");

		end.set(1975, 5, 19, 7, 31, 0);
		end.set(Calendar.MILLISECOND, 0);
		taiTC.setRange(startmicros, end.getTimeInMicros());
		taiTC.calcValuesByTickNumber(11, TickCalculator.AUTO_MINORTICK_NUMBER);
		checkDouble(taiTC.getInterval(), 5 * 1e6);
		assertEquals(taiTC.getMinorNumber(), 4);
		assertEquals(taiTC.getLabelFormate(), "%tT");
		assertEquals(taiTC.calcLabelFormatString(taiTC.getValues()), "%tT");

		end.set(1975, 5, 19, 8, 30, 0);
		end.set(Calendar.MILLISECOND, 0);
		taiTC.setRange(startmicros, end.getTimeInMicros());
		taiTC.calcValuesByTickNumber(11, TickCalculator.AUTO_MINORTICK_NUMBER);
		checkDouble(taiTC.getInterval(), 5 * 60 * 1e6);
		assertEquals(taiTC.getMinorNumber(), 4);
		assertEquals(taiTC.getLabelFormate(), "%tR");
		assertEquals(taiTC.calcLabelFormatString(taiTC.getValues()), "%tR");

		end.set(1975, 5, 20, 7, 30, 0);
		end.set(Calendar.MILLISECOND, 0);
		taiTC.setRange(startmicros, end.getTimeInMicros());
		taiTC.calcValuesByTickNumber(11, TickCalculator.AUTO_MINORTICK_NUMBER);
		checkDouble(taiTC.getInterval(), 3 * 3600 * 1e6);
		assertEquals(taiTC.getMinorNumber(), 2);
		assertEquals(taiTC.getLabelFormate(), "%tF %<tR");
		assertEquals(taiTC.calcLabelFormatString(taiTC.getValues()), "%tF %<tR");

		end.set(1975, 6, 19, 7, 30, 0);
		end.set(Calendar.MILLISECOND, 0);
		taiTC.setRange(startmicros, end.getTimeInMicros());
		taiTC.calcValuesByTickNumber(11, TickCalculator.AUTO_MINORTICK_NUMBER);
		checkDouble(taiTC.getInterval(), 7 * 24 * 3600 * 1e6);
		assertEquals(taiTC.getMinorNumber(), 0);
		assertEquals(taiTC.getLabelFormate(), "%tF");
		assertEquals(taiTC.calcLabelFormatString(taiTC.getValues()), "%tF");

		// non AUTO_MINORTICK_NUMBER
		end.set(1975, 5, 19, 7, 30, 1);
		end.set(Calendar.MILLISECOND, 0);
		taiTC.setRange(startmicros, end.getTimeInMicros());
		taiTC.calcValuesByTickNumber(11, 0);
		checkDouble(taiTC.getInterval(), 100 * 1e3);
		assertEquals(taiTC.getMinorNumber(), 0);
		assertEquals(taiTC.getLabelFormate(), "%tT.%<tL");
		assertEquals(taiTC.calcLabelFormatString(taiTC.getValues()), "%tT.%<tL");

		end.set(1975, 5, 19, 7, 30, 1);
		end.set(Calendar.MILLISECOND, 0);
		taiTC.setRange(startmicros, end.getTimeInMicros());
		taiTC.calcValuesByTickNumber(11, 2);
		checkDouble(taiTC.getInterval(), 100 * 1e3);
		assertEquals(taiTC.getMinorNumber(), 1);
		assertEquals(taiTC.getLabelFormate(), "%tT.%<tL");
		assertEquals(taiTC.calcLabelFormatString(taiTC.getValues()), "%tT.%<tL");

		end.set(1975, 5, 19, 7, 30, 1);
		end.set(Calendar.MILLISECOND, 0);
		taiTC.setRange(startmicros, end.getTimeInMicros());
		taiTC.calcValuesByTickNumber(11, 8);
		checkDouble(taiTC.getInterval(), 100 * 1e3);
		assertEquals(taiTC.getMinorNumber(), 9);
		assertEquals(taiTC.getLabelFormate(), "%tT.%<tL");
		assertEquals(taiTC.calcLabelFormatString(taiTC.getValues()), "%tT.%<tL");

		end.set(1975, 5, 19, 7, 31, 0);
		end.set(Calendar.MILLISECOND, 0);
		taiTC.setRange(startmicros, end.getTimeInMicros());
		taiTC.calcValuesByTickNumber(11, 10);
		checkDouble(taiTC.getInterval(), 5 * 1e6);
		assertEquals(taiTC.getMinorNumber(), 4);
		assertEquals(taiTC.getLabelFormate(), "%tT");
		assertEquals(taiTC.calcLabelFormatString(taiTC.getValues()), "%tT");
	}

	/**
	 * expand range to major date ticks
	 */
	@Test
	public void testExpandRangeByTicksNumber() {
		TimeZone zone = TimeZone.getTimeZone("UTC");
		Locale locale = Locale.US;
		TAIMicrosCalendar start = new TAIMicrosCalendar(zone, locale);
		TAIMicrosCalendar end = new TAIMicrosCalendar(zone, locale);
		start.set(1975, 4, 19, 7, 30, 0);
		start.set(Calendar.MILLISECOND, 0);
		end.set(1975, 4, 19, 7, 30, 1);
		end.set(Calendar.MILLISECOND, 0);
		long startms = start.getTimeInMicros();

		/* span zero */
		taiTC.setRange(start.getTimeInMicros(), start.getTimeInMicros());
		taiTC.expandRangeByTickNumber(11);
		checkRange(taiTC.getRange(), startms - 5, startms + 5);
		assertEquals((long) taiTC.getInterval(), 1);

		/* span 1micros */
		taiTC.setRange(start.getTimeInMicros(), start.getTimeInMicros() + 1);
		taiTC.expandRangeByTickNumber(11);
		checkRange(taiTC.getRange(), startms - 4, startms + 6);
		assertEquals((long) taiTC.getInterval(), 1);

		/* span 1ms */
		taiTC.setRange(start.getTimeInMicros(), start.getTimeInMicros() + 1000);
		taiTC.expandRangeByTickNumber(11);
		checkRange(taiTC.getRange(), startms, startms + 1000);
		assertEquals((long) taiTC.getInterval(), 100);

		/* span 1000ms */
		taiTC.setRange(start.getTimeInMicros(), end.getTimeInMicros());
		taiTC.expandRangeByTickNumber(11);
		checkRange(taiTC.getRange(), startms, startms + 1000000);
		assertEquals((long) taiTC.getInterval(), 100000);

		taiTC.setRange(start.getTimeInMicros() + 1, end.getTimeInMicros() - 1);
		taiTC.expandRangeByTickNumber(11);
		checkRange(taiTC.getRange(), startms, startms + 1000000);
		assertEquals((long) taiTC.getInterval(), 100000);

		taiTC.setRange(start.getTimeInMicros() - 1, end.getTimeInMicros() + 1);
		taiTC.expandRangeByTickNumber(11);
		checkRange(taiTC.getRange(), startms - 100000, startms + 1100000);
		assertEquals((long) taiTC.getInterval(), 100000);
	}

	/**
	 * expand range to major date ticks
	 */
	@Test
	public void testExpandRangeByTicksInterval() {
		TimeZone zone = TimeZone.getTimeZone("UTC");
		Locale locale = Locale.US;
		TAIMicrosCalendar start = new TAIMicrosCalendar(zone, locale);
		TAIMicrosCalendar end = new TAIMicrosCalendar(zone, locale);
		start.set(1975, 4, 19, 7, 30, 0);
		start.set(Calendar.MILLISECOND, 0);
		end.set(1975, 4, 19, 7, 30, 1);
		end.set(Calendar.MILLISECOND, 0);
		long startms = start.getTimeInMicros();

		/* span zero */
		taiTC.setRange(start.getTimeInMicros(), start.getTimeInMicros());
		taiTC.expandRangeByTickInterval(100);
		checkRange(taiTC.getRange(), startms, startms + 100);

		/* span less than interval */
		taiTC.setRange(start.getTimeInMicros(), start.getTimeInMicros() + 1);
		taiTC.expandRangeByTickInterval(100);
		checkRange(taiTC.getRange(), startms, startms + 100);

		/* span 1000ms */
		taiTC.setRange(start.getTimeInMicros(), end.getTimeInMicros());
		taiTC.expandRangeByTickInterval(100);
		checkRange(taiTC.getRange(), startms, startms + 1000000);

		taiTC.setRange(start.getTimeInMicros() + 1, end.getTimeInMicros() - 1);
		taiTC.expandRangeByTickInterval(100);
		checkRange(taiTC.getRange(), startms, startms + 1000000);

		taiTC.setRange(start.getTimeInMicros() - 1, end.getTimeInMicros() + 1);
		taiTC.expandRangeByTickInterval(100);
		checkRange(taiTC.getRange(), startms - 100, startms + 1000100);

	}

}
