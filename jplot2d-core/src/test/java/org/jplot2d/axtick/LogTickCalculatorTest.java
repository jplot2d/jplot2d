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

import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author Jingjing Li
 * 
 */
public class LogTickCalculatorTest {

	private static LogTickCalculator logTC;

	@BeforeClass
	public static void setUpBeforeClass() {
		logTC = LogTickAlgorithm.getInstance().createCalculator();
	}

	private DoubleTickCalculator niceLogTicks(double start, double end,
			int ticks) {
		logTC.setRange(start, end);
		logTC.calcValuesByTickNumber(ticks, 1);
		return logTC;
	}

	/**
	 * Test method for
	 * {@link LogTickCalculator#calcValuesByTickNumber(int, int)}
	 */
	@Test
	public void testNiceLogTicks() {

		DoubleTickCalculator tc;

		tc = niceLogTicks(1.0, 10.0, 1);
		checkDoubleArray(tc.getValues(), 1, 10);
		tc = niceLogTicks(1.0, 10.0, 2);
		checkDoubleArray(tc.getValues(), 1, 10);
		checkDoubleArray(tc.getMinorValues(), 2, 3, 4, 5, 6, 7, 8, 9);
		tc = niceLogTicks(1.0, 10.0, 5);
		checkDoubleArray(tc.getValues(), 1, 10);
		tc = niceLogTicks(1.0, 10.0, 10);
		checkDoubleArray(tc.getValues(), 1, 10);
		tc = niceLogTicks(1.0, 10.0, 100);
		checkDoubleArray(tc.getValues(), 1, 10);

		checkDoubleArray(niceLogTicks(1.0, 9.0, 1).getValues(), 1);
		checkDoubleArray(niceLogTicks(1.0, 9.0, 2).getValues(), 1);
		checkDoubleArray(niceLogTicks(1.0, 9.0, 5).getValues(), 1);
		checkDoubleArray(niceLogTicks(1.0, 9.0, 10).getValues(), 1);
		checkDoubleArray(niceLogTicks(1.0, 9.0, 100).getValues(), 1);

		checkDoubleArray(niceLogTicks(1.0, 11.0, 1).getValues(), 1, 10);
		checkDoubleArray(niceLogTicks(1.0, 11.0, 2).getValues(), 1, 10);
		checkDoubleArray(niceLogTicks(1.0, 11.0, 5).getValues(), 1, 10);
		checkDoubleArray(niceLogTicks(1.0, 11.0, 10).getValues(), 1, 10);
		checkDoubleArray(niceLogTicks(1.0, 11.0, 100).getValues(), 1, 10);

		tc = niceLogTicks(0.9, 20.0, 2);
		checkDoubleArray(tc.getValues(), 1, 10);
		checkDoubleArray(tc.getMinorValues(), 0.9, 2, 3, 4, 5, 6, 7, 8, 9, 20);

		// on range (0.01, 10)
		tc = niceLogTicks(0.01, 10.0, 2);
		checkDoubleArray(tc.getValues(), 0.01, 1.0);
		checkDoubleArray(tc.getMinorValues(), 0.1, 10.0);
		tc = niceLogTicks(0.01, 10.0, 3);
		checkDoubleArray(tc.getValues(), 0.01, 0.1, 1.0, 10);
		checkDoubleArray(tc.getMinorValues(), 0.02, 0.03, 0.04, 0.05, 0.06,
				0.07, 0.08, 0.09, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7, 0.8, 0.9, 2, 3,
				4, 5, 6, 7, 8, 9);
		tc = niceLogTicks(0.01, 10.0, 4);
		checkDoubleArray(tc.getValues(), 0.01, 0.1, 1.0, 10);
		checkDoubleArray(tc.getMinorValues(), 0.02, 0.03, 0.04, 0.05, 0.06,
				0.07, 0.08, 0.09, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7, 0.8, 0.9, 2, 3,
				4, 5, 6, 7, 8, 9);
		checkDoubleArray(niceLogTicks(0.01, 10.0, 5).getValues(), 0.01, 0.1,
				1.0, 10);
		checkDoubleArray(niceLogTicks(0.01, 10.0, 10).getValues(), 0.01, 0.1,
				1.0, 10);
		checkDoubleArray(niceLogTicks(0.01, 10.0, 100).getValues(), 0.01, 0.1,
				1.0, 10);

		// reverse
		checkDoubleArray(niceLogTicks(10.0, 0.01, 4).getValues(), 10, 1, 0.1,
				0.01);

		// boundary
		checkDoubleArray(niceLogTicks(0.01001, 10.0, 3).getValues(), 0.1, 1.0,
				10.0);
		checkDoubleArray(niceLogTicks(0.01001, 9.99999, 3).getValues(), 0.1,
				1.0);

		// linear on exponent
		tc = niceLogTicks(1e-4, 1e4, 4);
		checkDoubleArray(tc.getValues(), 1e-4, 1e-2, 1, 1e2, 1e4);
		checkDoubleArray(tc.getMinorValues(), 1e-3, 1e-1, 1e1, 1e3);
		tc = niceLogTicks(1e-4, 1e4, 5);
		checkDoubleArray(tc.getValues(), 1e-4, 1e-2, 1, 1e2, 1e4);
		checkDoubleArray(tc.getMinorValues(), 1e-3, 1e-1, 1e1, 1e3);
		tc = niceLogTicks(1e-4, 1e4, 6);
		checkDoubleArray(tc.getValues(), 1e-4, 1e-2, 1, 1e2, 1e4);
		checkDoubleArray(tc.getMinorValues(), 1e-3, 1e-1, 1e1, 1e3);
		checkDoubleArray(niceLogTicks(1e-4, 1e4, 7).getValues(), 1e-4, 1e-3,
				1e-2, 1e-1, 1, 1e1, 1e2, 1e3, 1e4);
		checkDoubleArray(niceLogTicks(1e-4, 1e4, 8).getValues(), 1e-4, 1e-3,
				1e-2, 1e-1, 1, 1e1, 1e2, 1e3, 1e4);
		checkDoubleArray(niceLogTicks(1e-4, 1e4, 9).getValues(), 1e-4, 1e-3,
				1e-2, 1e-1, 1, 1e1, 1e2, 1e3, 1e4);

		// only 1 major tick (HCSS-5886)
		tc = niceLogTicks(0.95, 1.05, 4);
		checkDoubleArray(tc.getValues(), 1);
		checkDoubleArray(tc.getMinorValues());

		// fallback linear
		checkDoubleArray(niceLogTicks(1.1, 1.9, 4).getValues(), 1.2, 1.4, 1.6,
				1.8);

	}

	@Test
	public void testExpandLogInterval() {
		logTC.setRange(2, 9);
		logTC.expandRangeByTickInterval(10);
		checkRange(logTC.getRange(), 1, 10);

		logTC.setRange(2, 9);
		logTC.expandRangeByTickInterval(100);
		checkRange(logTC.getRange(), 1, 100);

		logTC.setRange(2, 9);
		logTC.expandRangeByTickInterval(0.1);
		checkRange(logTC.getRange(), 1, 10);

		logTC.setRange(0.5, 9);
		logTC.expandRangeByTickInterval(10);
		checkRange(logTC.getRange(), 0.1, 10);

		logTC.setRange(0.5, 9);
		logTC.expandRangeByTickInterval(100);
		checkRange(logTC.getRange(), 0.01, 100);

		logTC.setRange(0.5, 9);
		logTC.expandRangeByTickInterval(3);
		checkRange(logTC.getRange(), 0.1, 10);

		logTC.setRange(0.5, 9);
		logTC.expandRangeByTickInterval(6);
		checkRange(logTC.getRange(), 0.1, 10);

	}

	@Test
	public void testCalcLabelFormatStrForLinear() {
		assertEquals("%.0f", TickUtils.calcLabelFormatStr(new double[] { 1000,
				2000, 3000 }));
		assertEquals("%.0f", TickUtils.calcLabelFormatStr(new double[] { 1200,
				1200, 1300 }));
		assertEquals("%.0m", TickUtils.calcLabelFormatStr(new double[] { 10000,
				20000, 30000 }));
		assertEquals("%.1m", TickUtils.calcLabelFormatStr(new double[] {
				110000, 120000, 130000 }));
		assertEquals("%.0f", TickUtils.calcLabelFormatStr(new double[] {
				123000, 124000, 125000 }));
		assertEquals("%.4m", TickUtils.calcLabelFormatStr(new double[] {
				1234500, 1234600, 1234700 }));

		assertEquals("%d", TickUtils.calcLabelFormatStr(new long[] { 1000,
				2000, 3000 }));
		assertEquals("%d", TickUtils.calcLabelFormatStr(new long[] { 1200,
				1200, 1300 }));
		assertEquals("%.0m", TickUtils.calcLabelFormatStr(new long[] { 10000,
				20000, 30000 }));
		assertEquals("%.1m", TickUtils.calcLabelFormatStr(new long[] { 110000,
				120000, 130000 }));
		assertEquals("%d", TickUtils.calcLabelFormatStr(new long[] { 123000,
				124000, 125000 }));
		assertEquals("%.4m", TickUtils.calcLabelFormatStr(new long[] { 1234500,
				1234600, 1234700 }));

		assertEquals("%.0f", TickUtils.calcLabelFormatStr(new double[] { 1, 10,
				100, 1000 }));
		assertEquals("%.0f", TickUtils.calcLabelFormatStr(new double[] { 12,
				120, 1200, 12000 }));
		assertEquals("%.0m", TickUtils.calcLabelFormatStr(new double[] { 1, 10,
				100, 1000, 10000 }));
		assertEquals("%.1m", TickUtils.calcLabelFormatStr(new double[] { 12,
				120, 1200, 12000, 120000 }));
		assertEquals("%.0f", TickUtils.calcLabelFormatStr(new double[] { 12,
				123, 1200, 12000, 120000 }));

		assertEquals("%d", TickUtils.calcLabelFormatStr(new long[] { 1, 10,
				100, 1000 }));
		assertEquals("%d", TickUtils.calcLabelFormatStr(new long[] { 12, 120,
				1200, 12000 }));
		assertEquals("%.0m", TickUtils.calcLabelFormatStr(new long[] { 1, 10,
				100, 1000, 10000 }));
		assertEquals("%.1m", TickUtils.calcLabelFormatStr(new long[] { 12, 120,
				1200, 12000, 120000 }));
		assertEquals("%d", TickUtils.calcLabelFormatStr(new long[] { 12, 123,
				1200, 12000, 120000 }));

		assertEquals("%.2f", TickUtils.calcLabelFormatStr(new double[] { 0.11,
				1.1, 11, 110 }));
		assertEquals("%.0m", TickUtils.calcLabelFormatStr(new double[] { 0.1,
				10, 1000 }));
		assertEquals("%.1m", TickUtils.calcLabelFormatStr(new double[] { 0.11,
				1.1, 11, 110, 1100 }));

		assertEquals("%.1f", TickUtils.calcLabelFormatStr(new double[] { -0.2,
				0, 0.2, 0.4, 0.6, 0.8, 1.0 }));
		assertEquals("%.0f", TickUtils.calcLabelFormatStr(new double[] { 2, 4,
				6, 8, 10, 12, 14, 16, 18, 20 }));
	}
}
