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

import org.jplot2d.axtick.DateInterval.Unit;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

import static org.jplot2d.util.TestUtils.checkDouble;
import static org.jplot2d.util.TestUtils.checkLongArray;
import static org.jplot2d.util.TestUtils.checkRange;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * @author Jingjing Li
 */
@SuppressWarnings("MagicConstant")
public class DateTickCalculatorTest {

    private static final TimeZone zone = TimeZone.getTimeZone("UTC");

    private static final Locale locale = Locale.US;

    private static DateTickCalculator dateTC;

    @BeforeClass
    public static void setUpBeforeClass() {
        dateTC = new DateTickAlgorithm(zone, locale).createCalculator();
    }

    @Test
    public void testSetCalendarBelowToMin() {
        Calendar cal = Calendar.getInstance(zone, locale);
        cal.set(1975, 5, 19, 7, 30, 32);
        cal.set(Calendar.MILLISECOND, 129);

        Calendar refCal = Calendar.getInstance(zone, locale);
        refCal.set(1975, 5, 19, 7, 30, 32);
        refCal.set(Calendar.MILLISECOND, 129);

        DateTickCalculator.setCalendarBelowToMin(cal, Unit.MILLISECOND);
        assertEquals(cal, refCal);

        DateTickCalculator.setCalendarBelowToMin(cal, Unit.SECOND);
        refCal.set(Calendar.MILLISECOND, 0);
        assertEquals(cal, refCal);

        DateTickCalculator.setCalendarBelowToMin(cal, Unit.MINUTE);
        refCal.set(Calendar.SECOND, 0);
        assertEquals(cal, refCal);

        DateTickCalculator.setCalendarBelowToMin(cal, Unit.HOUR);
        refCal.set(Calendar.MINUTE, 0);
        assertEquals(cal, refCal);

        DateTickCalculator.setCalendarBelowToMin(cal, Unit.DAY);
        refCal.set(Calendar.HOUR_OF_DAY, 0);
        assertEquals(cal, refCal);

        DateTickCalculator.setCalendarBelowToMin(cal, Unit.WEEK);
        int woy = refCal.get(Calendar.WEEK_OF_YEAR);
        refCal.set(Calendar.DAY_OF_WEEK, cal.getMinimum(Calendar.DAY_OF_WEEK));
        refCal.set(Calendar.WEEK_OF_YEAR, woy);
        assertEquals(cal, refCal);

        DateTickCalculator.setCalendarBelowToMin(cal, Unit.MONTH);
        refCal.set(Calendar.DAY_OF_MONTH, 1);
        assertEquals(cal, refCal);

        DateTickCalculator.setCalendarBelowToMin(cal, Unit.YEAR);
        refCal.set(Calendar.DAY_OF_YEAR, cal.getMinimum(Calendar.DAY_OF_YEAR));
        assertEquals(cal, refCal);
    }

    @Test
    public void testCalcInterval() {
        Calendar start = Calendar.getInstance(zone, locale);
        Calendar end = Calendar.getInstance(zone, locale);
        start.set(1975, 5, 19, 7, 30, 0);
        start.set(Calendar.MILLISECOND, 0);
        DateInterval di;
        Unit umax;

        end.set(1975, 5, 19, 7, 30, 0);
        end.set(Calendar.MILLISECOND, 15);
        di = DateTickCalculator.calcInterval(start.getTimeInMillis(), end.getTimeInMillis(), 11);
        assertEquals(di, new DateInterval(DateInterval.Unit.MILLISECOND, 2));
        umax = DateTickCalculator.getFirsNonEqualField(start, end);
        assertEquals(umax, Unit.MILLISECOND);

        end.set(1975, 5, 19, 7, 30, 0);
        end.set(Calendar.MILLISECOND, 40);
        di = DateTickCalculator.calcInterval(start.getTimeInMillis(), end.getTimeInMillis(), 11);
        assertEquals(di, new DateInterval(DateInterval.Unit.MILLISECOND, 5));
        umax = DateTickCalculator.getFirsNonEqualField(start, end);
        assertEquals(umax, Unit.MILLISECOND);

        end.set(1975, 5, 19, 7, 30, 0);
        end.set(Calendar.MILLISECOND, 80);
        di = DateTickCalculator.calcInterval(start.getTimeInMillis(), end.getTimeInMillis(), 11);
        assertEquals(di, new DateInterval(DateInterval.Unit.MILLISECOND, 10));
        umax = DateTickCalculator.getFirsNonEqualField(start, end);
        assertEquals(umax, Unit.MILLISECOND);

        end.set(1975, 5, 19, 7, 30, 1);
        end.set(Calendar.MILLISECOND, 0);
        di = DateTickCalculator.calcInterval(start.getTimeInMillis(), end.getTimeInMillis(), 11);
        assertEquals(di, new DateInterval(DateInterval.Unit.MILLISECOND, 100));
        umax = DateTickCalculator.getFirsNonEqualField(start, end);
        assertEquals(umax, Unit.SECOND);

        end.set(1975, 5, 19, 7, 31, 0);
        end.set(Calendar.MILLISECOND, 0);
        di = DateTickCalculator.calcInterval(start.getTimeInMillis(), end.getTimeInMillis(), 11);
        assertEquals(di, new DateInterval(DateInterval.Unit.SECOND, 5));
        umax = DateTickCalculator.getFirsNonEqualField(start, end);
        assertEquals(umax, Unit.MINUTE);

        end.set(1975, 5, 19, 8, 30, 0);
        end.set(Calendar.MILLISECOND, 0);
        di = DateTickCalculator.calcInterval(start.getTimeInMillis(), end.getTimeInMillis(), 11);
        assertEquals(di, new DateInterval(DateInterval.Unit.MINUTE, 5));
        umax = DateTickCalculator.getFirsNonEqualField(start, end);
        assertEquals(umax, Unit.HOUR);

        end.set(1975, 5, 19, 22, 30, 0);
        end.set(Calendar.MILLISECOND, 0);
        di = DateTickCalculator.calcInterval(start.getTimeInMillis(), end.getTimeInMillis(), 11);
        assertEquals(di, new DateInterval(DateInterval.Unit.HOUR, 2));
        umax = DateTickCalculator.getFirsNonEqualField(start, end);
        assertEquals(umax, Unit.HOUR);

        end.set(1975, 5, 20, 7, 30, 0);
        end.set(Calendar.MILLISECOND, 0);
        di = DateTickCalculator.calcInterval(start.getTimeInMillis(), end.getTimeInMillis(), 11);
        assertEquals(di, new DateInterval(DateInterval.Unit.HOUR, 3));
        umax = DateTickCalculator.getFirsNonEqualField(start, end);
        assertEquals(umax, Unit.DAY);

        end.set(1975, 5, 21, 7, 30, 0);
        end.set(Calendar.MILLISECOND, 0);
        di = DateTickCalculator.calcInterval(start.getTimeInMillis(), end.getTimeInMillis(), 11);
        assertEquals(di, new DateInterval(DateInterval.Unit.HOUR, 6));
        umax = DateTickCalculator.getFirsNonEqualField(start, end);
        assertEquals(umax, Unit.DAY);

        end.set(1975, 6, 19, 7, 30, 0);
        end.set(Calendar.MILLISECOND, 0);
        di = DateTickCalculator.calcInterval(start.getTimeInMillis(), end.getTimeInMillis(), 15);
        assertEquals(di, new DateInterval(DateInterval.Unit.DAY, 2));
        umax = DateTickCalculator.getFirsNonEqualField(start, end);
        assertEquals(umax, Unit.MONTH);

        end.set(1975, 6, 19, 7, 30, 0);
        end.set(Calendar.MILLISECOND, 0);
        di = DateTickCalculator.calcInterval(start.getTimeInMillis(), end.getTimeInMillis(), 11);
        assertEquals(di, new DateInterval(DateInterval.Unit.WEEK, 1));
        umax = DateTickCalculator.getFirsNonEqualField(start, end);
        assertEquals(umax, Unit.MONTH);

        end.set(1976, 5, 19, 7, 30, 0);
        end.set(Calendar.MILLISECOND, 0);
        di = DateTickCalculator.calcInterval(start.getTimeInMillis(), end.getTimeInMillis(), 11);
        assertEquals(di, new DateInterval(DateInterval.Unit.MONTH, 1));
        umax = DateTickCalculator.getFirsNonEqualField(start, end);
        assertEquals(umax, Unit.YEAR);

        end.set(1985, 5, 19, 7, 30, 0);
        end.set(Calendar.MILLISECOND, 0);
        di = DateTickCalculator.calcInterval(start.getTimeInMillis(), end.getTimeInMillis(), 11);
        assertEquals(di, new DateInterval(DateInterval.Unit.YEAR, 1));
        umax = DateTickCalculator.getFirsNonEqualField(start, end);
        assertEquals(umax, Unit.YEAR);

        end.set(2000, 5, 19, 7, 30, 0);
        end.set(Calendar.MILLISECOND, 0);
        di = DateTickCalculator.calcInterval(start.getTimeInMillis(), end.getTimeInMillis(), 11);
        assertEquals(di, new DateInterval(DateInterval.Unit.YEAR, 2));
        umax = DateTickCalculator.getFirsNonEqualField(start, end);
        assertEquals(umax, Unit.YEAR);

    }

    /**
     * calculate tick by tick number and minor number
     */
    @Test
    public void testDateTicks() {
        TimeZone zone = TimeZone.getTimeZone("UTC");
        Locale locale = Locale.US;
        Calendar start = Calendar.getInstance(zone, locale);
        Calendar end = Calendar.getInstance(zone, locale);
        start.set(1975, 5, 19, 7, 30, 0);
        start.set(Calendar.MILLISECOND, 0);
        long startms = start.getTimeInMillis();
        end.set(1975, 5, 19, 7, 30, 1);
        end.set(Calendar.MILLISECOND, 0);

        dateTC.setRange(start.getTimeInMillis(), start.getTimeInMillis());
        try {
            dateTC.calcValuesByTickNumber(11, 0);
            fail("IllegalArgumentException should be thrown");
        } catch (IllegalArgumentException e) {
            // will be caught
        }

        dateTC.setRange(start.getTimeInMillis(), end.getTimeInMillis());
        dateTC.calcValuesByTickNumber(11, TickCalculator.AUTO_MINOR_TICK_NUMBER);
        checkDouble(dateTC.getInterval(), 100);
        assertEquals(dateTC.getMinorNumber(), 4);
        checkLongArray(dateTC.getValues(), startms, startms + 100, startms + 200, startms + 300, startms + 400,
                startms + 500, startms + 600, startms + 700, startms + 800, startms + 900, startms + 1000);
        assertEquals(dateTC.getLabelFormat(), "%tT.%<tL");

        end.set(1975, 5, 19, 7, 31, 0);
        end.set(Calendar.MILLISECOND, 0);
        dateTC.setRange(start.getTimeInMillis(), end.getTimeInMillis());
        dateTC.calcValuesByTickNumber(11, TickCalculator.AUTO_MINOR_TICK_NUMBER);
        checkDouble(dateTC.getInterval(), 5 * 1000);
        assertEquals(dateTC.getMinorNumber(), 4);
        assertEquals(dateTC.getLabelFormat(), "%tT");
        assertEquals(dateTC.calcLabelFormatString(dateTC.getValues()), "%tT");

        end.set(1975, 5, 19, 8, 30, 0);
        end.set(Calendar.MILLISECOND, 0);
        dateTC.setRange(start.getTimeInMillis(), end.getTimeInMillis());
        dateTC.calcValuesByTickNumber(11, TickCalculator.AUTO_MINOR_TICK_NUMBER);
        checkDouble(dateTC.getInterval(), 5 * 60 * 1000);
        assertEquals(dateTC.getMinorNumber(), 4);
        assertEquals(dateTC.getLabelFormat(), "%tR");
        assertEquals(dateTC.calcLabelFormatString(dateTC.getValues()), "%tR");

        end.set(1975, 5, 20, 7, 30, 0);
        end.set(Calendar.MILLISECOND, 0);
        dateTC.setRange(start.getTimeInMillis(), end.getTimeInMillis());
        dateTC.calcValuesByTickNumber(11, TickCalculator.AUTO_MINOR_TICK_NUMBER);
        checkDouble(dateTC.getInterval(), 3 * 3600 * 1000);
        assertEquals(dateTC.getMinorNumber(), 2);
        assertEquals(dateTC.getLabelFormat(), "%tF %<tR");
        assertEquals(dateTC.calcLabelFormatString(dateTC.getValues()), "%tF %<tR");

        end.set(1975, 6, 19, 7, 30, 0);
        end.set(Calendar.MILLISECOND, 0);
        dateTC.setRange(start.getTimeInMillis(), end.getTimeInMillis());
        dateTC.calcValuesByTickNumber(11, TickCalculator.AUTO_MINOR_TICK_NUMBER);
        checkDouble(dateTC.getInterval(), 7 * 24 * 3600 * 1000);
        assertEquals(dateTC.getMinorNumber(), 0);
        assertEquals(dateTC.getLabelFormat(), "%tF");
        assertEquals(dateTC.calcLabelFormatString(dateTC.getValues()), "%tF");

        // non AUTO_MINOR_TICK_NUMBER
        end.set(1975, 5, 19, 7, 30, 1);
        end.set(Calendar.MILLISECOND, 0);
        dateTC.setRange(start.getTimeInMillis(), end.getTimeInMillis());
        dateTC.calcValuesByTickNumber(11, 0);
        checkDouble(dateTC.getInterval(), 100);
        assertEquals(dateTC.getMinorNumber(), 0);
        assertEquals(dateTC.getLabelFormat(), "%tT.%<tL");
        assertEquals(dateTC.calcLabelFormatString(dateTC.getValues()), "%tT.%<tL");

        end.set(1975, 5, 19, 7, 30, 1);
        end.set(Calendar.MILLISECOND, 0);
        dateTC.setRange(start.getTimeInMillis(), end.getTimeInMillis());
        dateTC.calcValuesByTickNumber(11, 2);
        checkDouble(dateTC.getInterval(), 100);
        assertEquals(dateTC.getMinorNumber(), 1);
        assertEquals(dateTC.getLabelFormat(), "%tT.%<tL");
        assertEquals(dateTC.calcLabelFormatString(dateTC.getValues()), "%tT.%<tL");

        end.set(1975, 5, 19, 7, 30, 1);
        end.set(Calendar.MILLISECOND, 0);
        dateTC.setRange(start.getTimeInMillis(), end.getTimeInMillis());
        dateTC.calcValuesByTickNumber(11, 8);
        checkDouble(dateTC.getInterval(), 100);
        assertEquals(dateTC.getMinorNumber(), 9);
        assertEquals(dateTC.getLabelFormat(), "%tT.%<tL");
        assertEquals(dateTC.calcLabelFormatString(dateTC.getValues()), "%tT.%<tL");

        end.set(1975, 5, 19, 7, 31, 0);
        end.set(Calendar.MILLISECOND, 0);
        dateTC.setRange(start.getTimeInMillis(), end.getTimeInMillis());
        dateTC.calcValuesByTickNumber(11, 10);
        checkDouble(dateTC.getInterval(), 5 * 1000);
        assertEquals(dateTC.getMinorNumber(), 4);
        assertEquals(dateTC.getLabelFormat(), "%tT");
        assertEquals(dateTC.calcLabelFormatString(dateTC.getValues()), "%tT");
    }

    /**
     * expand range to major date ticks
     */
    @Test
    public void testExpandRangeByTicksNumber() {
        TimeZone zone = TimeZone.getTimeZone("UTC");
        Locale locale = Locale.US;
        Calendar start = Calendar.getInstance(zone, locale);
        Calendar end = Calendar.getInstance(zone, locale);
        start.set(1975, 4, 19, 7, 30, 0);
        start.set(Calendar.MILLISECOND, 0);
        end.set(1975, 4, 19, 7, 30, 1);
        end.set(Calendar.MILLISECOND, 0);
        long startms = start.getTimeInMillis();

		/* span zero */
        dateTC.setRange(start.getTimeInMillis(), start.getTimeInMillis());
        dateTC.expandRangeByTickNumber(11);
        checkRange(dateTC.getRange(), startms - 5, startms + 5);
        assertEquals((long) dateTC.getInterval(), 1l);

		/* span 1ms */
        dateTC.setRange(start.getTimeInMillis(), start.getTimeInMillis() + 1);
        dateTC.expandRangeByTickNumber(11);
        checkRange(dateTC.getRange(), startms - 4, startms + 6);
        assertEquals((long) dateTC.getInterval(), 1l);

		/* span 1000ms */
        dateTC.setRange(start.getTimeInMillis(), end.getTimeInMillis());
        dateTC.expandRangeByTickNumber(11);
        checkRange(dateTC.getRange(), startms, startms + 1000);
        assertEquals((long) dateTC.getInterval(), 100l);

        dateTC.setRange(start.getTimeInMillis() + 1, end.getTimeInMillis() - 1);
        dateTC.expandRangeByTickNumber(11);
        checkRange(dateTC.getRange(), startms, startms + 1000);
        assertEquals((long) dateTC.getInterval(), 100l);

        dateTC.setRange(start.getTimeInMillis() - 1, end.getTimeInMillis() + 1);
        dateTC.expandRangeByTickNumber(11);
        checkRange(dateTC.getRange(), startms - 100, startms + 1100);
        assertEquals((long) dateTC.getInterval(), 100l);
    }

    /**
     * expand range to major date ticks
     */
    @Test
    public void testExpandRangeByTicksInterval() {
        TimeZone zone = TimeZone.getTimeZone("UTC");
        Locale locale = Locale.US;
        Calendar start = Calendar.getInstance(zone, locale);
        Calendar end = Calendar.getInstance(zone, locale);
        start.set(1975, 4, 19, 7, 30, 0);
        start.set(Calendar.MILLISECOND, 0);
        end.set(1975, 4, 19, 7, 30, 1);
        end.set(Calendar.MILLISECOND, 0);
        long startms = start.getTimeInMillis();

		/* span zero */
        dateTC.setRange(start.getTimeInMillis(), start.getTimeInMillis());
        dateTC.expandRangeByTickInterval(100);
        checkRange(dateTC.getRange(), startms, startms + 100);

		/* span less than interval */
        dateTC.setRange(start.getTimeInMillis(), start.getTimeInMillis() + 1);
        dateTC.expandRangeByTickInterval(100);
        checkRange(dateTC.getRange(), startms, startms + 100);

		/* span 1000ms */
        dateTC.setRange(start.getTimeInMillis(), end.getTimeInMillis());
        dateTC.expandRangeByTickInterval(100);
        checkRange(dateTC.getRange(), startms, startms + 1000);

        dateTC.setRange(start.getTimeInMillis() + 1, end.getTimeInMillis() - 1);
        dateTC.expandRangeByTickInterval(100);
        checkRange(dateTC.getRange(), startms, startms + 1000);

        dateTC.setRange(start.getTimeInMillis() - 1, end.getTimeInMillis() + 1);
        dateTC.expandRangeByTickInterval(100);
        checkRange(dateTC.getRange(), startms - 100, startms + 1100);

    }

}
