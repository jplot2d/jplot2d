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

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * @author Jingjing Li
 * 
 */
public class TickUtilsTest {

	@Test
	public void testMinorNumber() {
		assertEquals(TickUtils.calcMinorNumber(1, 0), 0);
		assertEquals(TickUtils.calcMinorNumber(1, 1), 0);
		assertEquals(TickUtils.calcMinorNumber(2, 0), 0);
		assertEquals(TickUtils.calcMinorNumber(2, 1), 1);
		assertEquals(TickUtils.calcMinorNumber(2, 2), 1);
		assertEquals(TickUtils.calcMinorNumber(5, 0), 0);
		assertEquals(TickUtils.calcMinorNumber(5, 1), 4);
		assertEquals(TickUtils.calcMinorNumber(5, 4), 4);
		assertEquals(TickUtils.calcMinorNumber(10, 0), 0);
		assertEquals(TickUtils.calcMinorNumber(10, 1), 1);
		assertEquals(TickUtils.calcMinorNumber(10, 2), 1);
		assertEquals(TickUtils.calcMinorNumber(10, 3), 4);
		assertEquals(TickUtils.calcMinorNumber(10, 4), 4);
		assertEquals(TickUtils.calcMinorNumber(10, 6), 4);
		assertEquals(TickUtils.calcMinorNumber(10, 7), 9);
		assertEquals(TickUtils.calcMinorNumber(10, 9), 9);
		assertEquals(TickUtils.calcMinorNumber(10, 10), 9);
		assertEquals(TickUtils.calcMinorNumber(6, 0), 0);
		assertEquals(TickUtils.calcMinorNumber(6, 1), 1);
		assertEquals(TickUtils.calcMinorNumber(6, 2), 2);
		assertEquals(TickUtils.calcMinorNumber(6, 3), 2);
		assertEquals(TickUtils.calcMinorNumber(6, 4), 5);
		assertEquals(TickUtils.calcMinorNumber(12, 0), 0);
		assertEquals(TickUtils.calcMinorNumber(12, 1), 1);
		assertEquals(TickUtils.calcMinorNumber(12, 2), 2);
		assertEquals(TickUtils.calcMinorNumber(12, 3), 3);
		assertEquals(TickUtils.calcMinorNumber(12, 4), 3);
		assertEquals(TickUtils.calcMinorNumber(12, 5), 5);
		assertEquals(TickUtils.calcMinorNumber(12, 8), 5);
		assertEquals(TickUtils.calcMinorNumber(12, 9), 11);
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
