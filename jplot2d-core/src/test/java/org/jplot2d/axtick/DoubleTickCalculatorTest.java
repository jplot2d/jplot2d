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

import org.junit.Test;

import java.text.Format;

import javax.annotation.Nonnull;

import static org.junit.Assert.assertEquals;

/**
 * @author Jingjing Li
 */
public class DoubleTickCalculatorTest {

    private static final DoubleTickCalculator tc = new DoubleTickCalculator() {

        @Override
        public void calcValuesByTickNumber(int tickNumber, int minorTickNumber) {

        }

        @Override
        public double getInterval() {
            return 0;
        }

        @Override
        public int getMinorNumber() {
            return 0;
        }

        @Override
        public double[] getValues() {
            return null;
        }

        @Override
        public double[] getMinorValues() {
            return null;
        }

        @Override
        public Format calcLabelTextFormat(@Nonnull Object values) {
            return null;
        }

        @Override
        public String getLabelFormat() {
            return null;
        }

        @Override
        public void calcValuesByTickInterval(double interval, double offset, int minorTickNumber) {

        }

    };

    @Test
    public void testCalcLabelFormatString() {
        assertEquals("%.0f", tc.calcLabelFormatString(new double[]{1000, 2000, 3000}));
        assertEquals("%.0f", tc.calcLabelFormatString(new double[]{1200, 1200, 1300}));
        assertEquals("%.0m", tc.calcLabelFormatString(new double[]{10000, 20000, 30000}));
        assertEquals("%.1m", tc.calcLabelFormatString(new double[]{110000, 120000, 130000}));
        assertEquals("%.0f", tc.calcLabelFormatString(new double[]{123000, 124000, 125000}));
        assertEquals("%.4m", tc.calcLabelFormatString(new double[]{1234500, 1234600, 1234700}));

        assertEquals("%.0f", tc.calcLabelFormatString(new double[]{1, 10, 100, 1000}));
        assertEquals("%.0f", tc.calcLabelFormatString(new double[]{12, 120, 1200, 12000}));
        assertEquals("%.0m", tc.calcLabelFormatString(new double[]{1, 10, 100, 1000, 10000}));
        assertEquals("%.1m", tc.calcLabelFormatString(new double[]{12, 120, 1200, 12000, 120000}));
        assertEquals("%.0f", tc.calcLabelFormatString(new double[]{12, 123, 1200, 12000, 120000}));

        assertEquals("%.2f", tc.calcLabelFormatString(new double[]{0.11, 1.1, 11, 110}));
        assertEquals("%.0m", tc.calcLabelFormatString(new double[]{0.1, 10, 1000}));
        assertEquals("%.1m", tc.calcLabelFormatString(new double[]{0.11, 1.1, 11, 110, 1100}));

        assertEquals("%.1f", tc.calcLabelFormatString(new double[]{-0.2, 0, 0.2, 0.4, 0.6, 0.8, 1.0}));
        assertEquals("%.0f", tc.calcLabelFormatString(new double[]{2, 4, 6, 8, 10, 12, 14, 16, 18, 20}));
    }

}
