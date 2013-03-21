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

import static org.jplot2d.util.TestUtils.*;
import static org.junit.Assert.*;

import org.junit.Test;

/**
 * @author Jingjing Li
 * 
 */
public class ArcDmsTickCalculatorTest {

    private static ArcDmsTickCalculator admsTC = ArcDmsTickAlgorithm
            .getInstance().createCalculator();

    @Test
    public void testCalcValuesByTickNumber() {
        ArcDmsFormat format;

        admsTC.setRange(0, 1.0 / 3600);
        admsTC.calcValuesByTickNumber(11, TickCalculator.AUTO_MINORTICK_NUMBER);
        checkDouble(admsTC.getInterval(), 0.1 / 3600);
        assertEquals(admsTC.getMinorNumber(), 0);
        checkDoubleArray(admsTC.getValues(), 0, 0.1 / 3600, 0.2 / 3600,
                0.3 / 3600, 0.4 / 3600, 0.5 / 3600, 0.6 / 3600, 0.7 / 3600,
                0.8 / 3600, 0.9 / 3600, 1.0 / 3600);
        format = admsTC.calcLabelTextFormat(admsTC.getValues());
        assertEquals(format, new ArcDmsFormat(1));
        checkFormat(format, admsTC.getValues(), "00\u00b000\u203200.0\u2033",
                "00\u00b000\u203200.1\u2033", "00\u00b000\u203200.2\u2033",
                "00\u00b000\u203200.3\u2033", "00\u00b000\u203200.4\u2033",
                "00\u00b000\u203200.5\u2033", "00\u00b000\u203200.6\u2033",
                "00\u00b000\u203200.7\u2033", "00\u00b000\u203200.8\u2033",
                "00\u00b000\u203200.9\u2033", "00\u00b000\u203201.0\u2033");

        admsTC.setRange(0, 60.0 / 3600);
        admsTC.calcValuesByTickNumber(11, TickCalculator.AUTO_MINORTICK_NUMBER);
        checkDouble(admsTC.getInterval(), 5.0 / 3600);
        assertEquals(admsTC.getMinorNumber(), 4);
        checkDoubleArray(admsTC.getValues(), 0, 5.0 / 3600, 10.0 / 3600,
                15.0 / 3600, 20.0 / 3600, 25.0 / 3600, 30.0 / 3600,
                35.0 / 3600, 40.0 / 3600, 45.0 / 3600, 50.0 / 3600,
                55.0 / 3600, 60.0 / 3600);
        format = admsTC.calcLabelTextFormat(admsTC.getValues());
        assertEquals(format, new ArcDmsFormat(0));
        checkFormat(format, admsTC.getValues(), "00\u00b000\u203200\u2033",
                "00\u00b000\u203205\u2033", "00\u00b000\u203210\u2033",
                "00\u00b000\u203215\u2033", "00\u00b000\u203220\u2033",
                "00\u00b000\u203225\u2033", "00\u00b000\u203230\u2033",
                "00\u00b000\u203235\u2033", "00\u00b000\u203240\u2033",
                "00\u00b000\u203245\u2033", "00\u00b000\u203250\u2033",
                "00\u00b000\u203255\u2033", "00\u00b001\u203200\u2033");

        admsTC.setRange(0, 1);
        admsTC.calcValuesByTickNumber(11, TickCalculator.AUTO_MINORTICK_NUMBER);
        checkDouble(admsTC.getInterval(), 300.0 / 3600);
        assertEquals(admsTC.getMinorNumber(), 4);
        checkDoubleArray(admsTC.getValues(), 0, 300.0 / 3600, 600.0 / 3600,
                900.0 / 3600, 1200.0 / 3600, 1500.0 / 3600, 1800.0 / 3600,
                2100.0 / 3600, 2400.0 / 3600, 2700.0 / 3600, 3000.0 / 3600,
                3300.0 / 3600, 3600.0 / 3600);
        format = admsTC.calcLabelTextFormat(admsTC.getValues());
        assertEquals(format, new ArcDmsFormat(0));
        checkFormat(format, admsTC.getValues(), "00\u00b000\u203200\u2033",
                "00\u00b005\u203200\u2033", "00\u00b010\u203200\u2033",
                "00\u00b015\u203200\u2033", "00\u00b020\u203200\u2033",
                "00\u00b025\u203200\u2033", "00\u00b030\u203200\u2033",
                "00\u00b035\u203200\u2033", "00\u00b040\u203200\u2033",
                "00\u00b045\u203200\u2033", "00\u00b050\u203200\u2033",
                "00\u00b055\u203200\u2033", "01\u00b000\u203200\u2033");

        admsTC.setRange(0, 360);
        admsTC.calcValuesByTickNumber(11, TickCalculator.AUTO_MINORTICK_NUMBER);
        checkDouble(admsTC.getInterval(), 30);
        assertEquals(admsTC.getMinorNumber(), 2);
        checkDoubleArray(admsTC.getValues(), 0, 30, 60, 90, 120, 150, 180, 210,
                240, 270, 300, 330, 360);
        format = admsTC.calcLabelTextFormat(admsTC.getValues());
        assertEquals(format, new ArcDmsFormat(0));
        checkFormat(format, admsTC.getValues(), "00\u00b000\u203200\u2033",
                "30\u00b000\u203200\u2033", "60\u00b000\u203200\u2033",
                "90\u00b000\u203200\u2033", "120\u00b000\u203200\u2033",
                "150\u00b000\u203200\u2033", "180\u00b000\u203200\u2033",
                "210\u00b000\u203200\u2033", "240\u00b000\u203200\u2033",
                "270\u00b000\u203200\u2033", "300\u00b000\u203200\u2033",
                "330\u00b000\u203200\u2033", "360\u00b000\u203200\u2033");

    }

    @Test
    public void testCalcValuesByInterval() {
        ArcDmsFormat format;

        admsTC.setRange(0, 1.0 / 3600);
        admsTC.calcValuesByTickInterval(0.1 / 3600, 0,
                TickCalculator.AUTO_MINORTICK_NUMBER);
        assertEquals(admsTC.getMinorNumber(), 0);
        checkDoubleArray(admsTC.getValues(), 0, 0.1 / 3600, 0.2 / 3600,
                0.3 / 3600, 0.4 / 3600, 0.5 / 3600, 0.6 / 3600, 0.7 / 3600,
                0.8 / 3600, 0.9 / 3600, 1.0 / 3600);
        format = admsTC.calcLabelTextFormat(admsTC.getValues());
        assertEquals(format, new ArcDmsFormat(1));
        checkFormat(format, admsTC.getValues(), "00\u00b000\u203200.0\u2033",
                "00\u00b000\u203200.1\u2033", "00\u00b000\u203200.2\u2033",
                "00\u00b000\u203200.3\u2033", "00\u00b000\u203200.4\u2033",
                "00\u00b000\u203200.5\u2033", "00\u00b000\u203200.6\u2033",
                "00\u00b000\u203200.7\u2033", "00\u00b000\u203200.8\u2033",
                "00\u00b000\u203200.9\u2033", "00\u00b000\u203201.0\u2033");

        admsTC.setRange(0, 60.0 / 3600);
        admsTC.calcValuesByTickInterval(5.0 / 3600, 0,
                TickCalculator.AUTO_MINORTICK_NUMBER);
        assertEquals(admsTC.getMinorNumber(), 4);
        checkDoubleArray(admsTC.getValues(), 0, 5.0 / 3600, 10.0 / 3600,
                15.0 / 3600, 20.0 / 3600, 25.0 / 3600, 30.0 / 3600,
                35.0 / 3600, 40.0 / 3600, 45.0 / 3600, 50.0 / 3600,
                55.0 / 3600, 60.0 / 3600);
        format = admsTC.calcLabelTextFormat(admsTC.getValues());
        assertEquals(format, new ArcDmsFormat(0));
        checkFormat(format, admsTC.getValues(), "00\u00b000\u203200\u2033",
                "00\u00b000\u203205\u2033", "00\u00b000\u203210\u2033",
                "00\u00b000\u203215\u2033", "00\u00b000\u203220\u2033",
                "00\u00b000\u203225\u2033", "00\u00b000\u203230\u2033",
                "00\u00b000\u203235\u2033", "00\u00b000\u203240\u2033",
                "00\u00b000\u203245\u2033", "00\u00b000\u203250\u2033",
                "00\u00b000\u203255\u2033", "00\u00b001\u203200\u2033");

        admsTC.setRange(0, 1);
        admsTC.calcValuesByTickInterval(300.0 / 3600, 0,
                TickCalculator.AUTO_MINORTICK_NUMBER);
        assertEquals(admsTC.getMinorNumber(), 4);
        checkDoubleArray(admsTC.getValues(), 0, 300.0 / 3600, 600.0 / 3600,
                900.0 / 3600, 1200.0 / 3600, 1500.0 / 3600, 1800.0 / 3600,
                2100.0 / 3600, 2400.0 / 3600, 2700.0 / 3600, 3000.0 / 3600,
                3300.0 / 3600, 3600.0 / 3600);
        format = admsTC.calcLabelTextFormat(admsTC.getValues());
        assertEquals(format, new ArcDmsFormat(0));
        checkFormat(format, admsTC.getValues(), "00\u00b000\u203200\u2033",
                "00\u00b005\u203200\u2033", "00\u00b010\u203200\u2033",
                "00\u00b015\u203200\u2033", "00\u00b020\u203200\u2033",
                "00\u00b025\u203200\u2033", "00\u00b030\u203200\u2033",
                "00\u00b035\u203200\u2033", "00\u00b040\u203200\u2033",
                "00\u00b045\u203200\u2033", "00\u00b050\u203200\u2033",
                "00\u00b055\u203200\u2033", "01\u00b000\u203200\u2033");

        admsTC.setRange(0, 360);
        admsTC.calcValuesByTickInterval(30, 0,
                TickCalculator.AUTO_MINORTICK_NUMBER);
        assertEquals(admsTC.getMinorNumber(), 2);
        checkDoubleArray(admsTC.getValues(), 0, 30, 60, 90, 120, 150, 180, 210,
                240, 270, 300, 330, 360);
        format = admsTC.calcLabelTextFormat(admsTC.getValues());
        assertEquals(format, new ArcDmsFormat(0));
        checkFormat(format, admsTC.getValues(), "00\u00b000\u203200\u2033",
                "30\u00b000\u203200\u2033", "60\u00b000\u203200\u2033",
                "90\u00b000\u203200\u2033", "120\u00b000\u203200\u2033",
                "150\u00b000\u203200\u2033", "180\u00b000\u203200\u2033",
                "210\u00b000\u203200\u2033", "240\u00b000\u203200\u2033",
                "270\u00b000\u203200\u2033", "300\u00b000\u203200\u2033",
                "330\u00b000\u203200\u2033", "360\u00b000\u203200\u2033");

    }

}
