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

import org.junit.Test;

import static org.jplot2d.util.TestUtils.checkDouble;
import static org.jplot2d.util.TestUtils.checkDoubleArray;
import static org.jplot2d.util.TestUtils.checkFormat;
import static org.junit.Assert.assertEquals;

/**
 * @author Jingjing Li
 */
public class TimeHmsTickCalculatorTest {

    private static final TimeHmsTickCalculator hmsTC = TimeHmsTickAlgorithm
            .getInstance().createCalculator();

    @Test
    public void testCalcValuesByTickNumber() {
        TimeHmsFormat format;

        hmsTC.setRange(0, 1);
        hmsTC.calcValuesByTickNumber(11, TickCalculator.AUTO_MINOR_TICK_NUMBER);
        checkDouble(hmsTC.getInterval(), 0.1);
        assertEquals(hmsTC.getMinorNumber(), 0);
        checkDoubleArray(hmsTC.getValues(), 0, 0.1, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7, 0.8, 0.9, 1.0);
        format = hmsTC.calcLabelTextFormat(hmsTC.getValues());
        assertEquals(format, new TimeHmsFormat(1));
        checkFormat(format, hmsTC.getValues(), "00:00:00.0", "00:00:00.1",
                "00:00:00.2", "00:00:00.3", "00:00:00.4", "00:00:00.5",
                "00:00:00.6", "00:00:00.7", "00:00:00.8", "00:00:00.9",
                "00:00:01.0");

        hmsTC.setRange(0, 60);
        hmsTC.calcValuesByTickNumber(11, TickCalculator.AUTO_MINOR_TICK_NUMBER);
        checkDouble(hmsTC.getInterval(), 5.0);
        assertEquals(hmsTC.getMinorNumber(), 4);
        checkDoubleArray(hmsTC.getValues(), 0, 5, 10, 15, 20, 25, 30, 35, 40, 45, 50, 55, 60);
        format = hmsTC.calcLabelTextFormat(hmsTC.getValues());
        assertEquals(format, new TimeHmsFormat(0));
        checkFormat(format, hmsTC.getValues(), "00:00:00", "00:00:05",
                "00:00:10", "00:00:15", "00:00:20", "00:00:25", "00:00:30",
                "00:00:35", "00:00:40", "00:00:45", "00:00:50", "00:00:55",
                "00:01:00");

        hmsTC.setRange(0, 3600);
        hmsTC.calcValuesByTickNumber(11, TickCalculator.AUTO_MINOR_TICK_NUMBER);
        checkDouble(hmsTC.getInterval(), 300.0);
        assertEquals(hmsTC.getMinorNumber(), 4);
        checkDoubleArray(hmsTC.getValues(), 0, 300, 600, 900, 1200, 1500, 1800,
                2100, 2400, 2700, 3000, 3300, 3600);
        format = hmsTC.calcLabelTextFormat(hmsTC.getValues());
        assertEquals(format, new TimeHmsFormat(0));
        checkFormat(format, hmsTC.getValues(), "00:00:00", "00:05:00",
                "00:10:00", "00:15:00", "00:20:00", "00:25:00", "00:30:00",
                "00:35:00", "00:40:00", "00:45:00", "00:50:00", "00:55:00",
                "01:00:00");

        hmsTC.setRange(0, 24 * 3600);
        hmsTC.calcValuesByTickNumber(11, TickCalculator.AUTO_MINOR_TICK_NUMBER);
        checkDouble(hmsTC.getInterval(), 7200.0);
        assertEquals(hmsTC.getMinorNumber(), 1);
        checkDoubleArray(hmsTC.getValues(), 0, 2 * 3600, 4 * 3600, 6 * 3600,
                8 * 3600, 10 * 3600, 12 * 3600, 14 * 3600, 16 * 3600,
                18 * 3600, 20 * 3600, 22 * 3600, 24 * 3600);
        format = hmsTC.calcLabelTextFormat(hmsTC.getValues());
        assertEquals(format, new TimeHmsFormat(0));
        checkFormat(format, hmsTC.getValues(), "00:00:00", "02:00:00",
                "04:00:00", "06:00:00", "08:00:00", "10:00:00", "12:00:00",
                "14:00:00", "16:00:00", "18:00:00", "20:00:00", "22:00:00",
                "24:00:00");

    }

    @Test
    public void testCalcValuesByInterval() {
        TimeHmsFormat format;

        hmsTC.setRange(0, 1);
        hmsTC.calcValuesByTickInterval(0.1, 0, TickCalculator.AUTO_MINOR_TICK_NUMBER);
        assertEquals(hmsTC.getMinorNumber(), 0);
        checkDoubleArray(hmsTC.getValues(), 0, 0.1, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7, 0.8, 0.9, 1.0);
        format = hmsTC.calcLabelTextFormat(hmsTC.getValues());
        assertEquals(format, new TimeHmsFormat(1));
        checkFormat(format, hmsTC.getValues(), "00:00:00.0", "00:00:00.1",
                "00:00:00.2", "00:00:00.3", "00:00:00.4", "00:00:00.5",
                "00:00:00.6", "00:00:00.7", "00:00:00.8", "00:00:00.9",
                "00:00:01.0");

        hmsTC.setRange(0, 60);
        hmsTC.calcValuesByTickInterval(5, 0, TickCalculator.AUTO_MINOR_TICK_NUMBER);
        assertEquals(hmsTC.getMinorNumber(), 4);
        checkDoubleArray(hmsTC.getValues(), 0, 5, 10, 15, 20, 25, 30, 35, 40, 45, 50, 55, 60);
        format = hmsTC.calcLabelTextFormat(hmsTC.getValues());
        assertEquals(format, new TimeHmsFormat(0));
        checkFormat(format, hmsTC.getValues(), "00:00:00", "00:00:05",
                "00:00:10", "00:00:15", "00:00:20", "00:00:25", "00:00:30",
                "00:00:35", "00:00:40", "00:00:45", "00:00:50", "00:00:55",
                "00:01:00");

        hmsTC.setRange(0, 3600);
        hmsTC.calcValuesByTickInterval(300, 0, TickCalculator.AUTO_MINOR_TICK_NUMBER);
        assertEquals(hmsTC.getMinorNumber(), 4);
        checkDoubleArray(hmsTC.getValues(), 0, 300, 600, 900, 1200, 1500, 1800,
                2100, 2400, 2700, 3000, 3300, 3600);
        format = hmsTC.calcLabelTextFormat(hmsTC.getValues());
        assertEquals(format, new TimeHmsFormat(0));
        checkFormat(format, hmsTC.getValues(), "00:00:00", "00:05:00",
                "00:10:00", "00:15:00", "00:20:00", "00:25:00", "00:30:00",
                "00:35:00", "00:40:00", "00:45:00", "00:50:00", "00:55:00",
                "01:00:00");

        hmsTC.setRange(0, 24 * 3600);
        hmsTC.calcValuesByTickInterval(7200, 0, TickCalculator.AUTO_MINOR_TICK_NUMBER);
        assertEquals(hmsTC.getMinorNumber(), 1);
        checkDoubleArray(hmsTC.getValues(), 0, 2 * 3600, 4 * 3600, 6 * 3600,
                8 * 3600, 10 * 3600, 12 * 3600, 14 * 3600, 16 * 3600,
                18 * 3600, 20 * 3600, 22 * 3600, 24 * 3600);
        format = hmsTC.calcLabelTextFormat(hmsTC.getValues());
        assertEquals(format, new TimeHmsFormat(0));
        checkFormat(format, hmsTC.getValues(), "00:00:00", "02:00:00",
                "04:00:00", "06:00:00", "08:00:00", "10:00:00", "12:00:00",
                "14:00:00", "16:00:00", "18:00:00", "20:00:00", "22:00:00",
                "24:00:00");

    }

}
