/**
 * Copyright 2010, 2011 Jingjing Li.
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
package org.jplot2d.util;

import static org.junit.Assert.*;

import org.junit.Test;

public class NumberUtilsTest {

	@Test
	public void testCalcLabelFormatStrDouble() {
		assertEquals(NumberUtils.calcFormatStr(Double.NaN), "%.0f");
		assertEquals(NumberUtils.calcFormatStr(Double.POSITIVE_INFINITY), "%.0f");
		assertEquals(NumberUtils.calcFormatStr(Double.NEGATIVE_INFINITY), "%.0f");
		assertEquals(NumberUtils.calcFormatStr(0d), "%.0f");
		assertEquals(NumberUtils.calcFormatStr(1d), "%.0f");
		assertEquals(NumberUtils.calcFormatStr(-1d), "%.0f");
		assertEquals(NumberUtils.calcFormatStr(0.01), "%.2f");
		assertEquals(NumberUtils.calcFormatStr(0.001), "%.3f");
		assertEquals(NumberUtils.calcFormatStr(0.0011), "%.4f");
		assertEquals(NumberUtils.calcFormatStr(0.00111), "%.5f");
		// more than 4 '0' can be saved
		assertEquals(NumberUtils.calcFormatStr(0.0001), "%.0e");

		assertEquals(NumberUtils.calcFormatStr(10d), "%.0f");
		assertEquals(NumberUtils.calcFormatStr(100d), "%.0f");
		assertEquals(NumberUtils.calcFormatStr(1000d), "%.0f");
		assertEquals(NumberUtils.calcFormatStr(11000d), "%.0f");
		assertEquals(NumberUtils.calcFormatStr(111000d), "%.0f");
		// more than 1000000
		assertEquals(NumberUtils.calcFormatStr(1000000d), "%.0e");
		assertEquals(NumberUtils.calcFormatStr(1000001d), "%.6e");
		// more than 4 '0' can be saved
		assertEquals(NumberUtils.calcFormatStr(10000d), "%.0e");
		assertEquals(NumberUtils.calcFormatStr(110000d), "%.1e");
	}

	@Test
	public void testCalcLabelFormatStrFloat() {
		assertEquals(NumberUtils.calcFormatStr(Float.NaN), "%.0f");
		assertEquals(NumberUtils.calcFormatStr(Float.POSITIVE_INFINITY), "%.0f");
		assertEquals(NumberUtils.calcFormatStr(Float.NEGATIVE_INFINITY), "%.0f");
		assertEquals(NumberUtils.calcFormatStr(0f), "%.0f");
		assertEquals(NumberUtils.calcFormatStr(1f), "%.0f");
		assertEquals(NumberUtils.calcFormatStr(-1f), "%.0f");
		assertEquals(NumberUtils.calcFormatStr(0.01f), "%.2f");
		assertEquals(NumberUtils.calcFormatStr(0.001f), "%.3f");
		assertEquals(NumberUtils.calcFormatStr(0.0011f), "%.4f");
		assertEquals(NumberUtils.calcFormatStr(0.00111f), "%.5f");
		// more than 4 '0' can be saved
		assertEquals(NumberUtils.calcFormatStr(0.0001f), "%.0e");

		assertEquals(NumberUtils.calcFormatStr(10f), "%.0f");
		assertEquals(NumberUtils.calcFormatStr(100f), "%.0f");
		assertEquals(NumberUtils.calcFormatStr(1000f), "%.0f");
		assertEquals(NumberUtils.calcFormatStr(11000f), "%.0f");
		assertEquals(NumberUtils.calcFormatStr(111000f), "%.0f");
		// more than 1000000
		assertEquals(NumberUtils.calcFormatStr(1000000f), "%.0e");
		assertEquals(NumberUtils.calcFormatStr(1000001f), "%.0e");
		assertEquals(NumberUtils.calcFormatStr(1000010f), "%.5e");
		// more than 4 '0' can be saved
		assertEquals(NumberUtils.calcFormatStr(10000f), "%.0e");
		assertEquals(NumberUtils.calcFormatStr(110000f), "%.1e");
	}

}
