/**
 * Copyright 2010-2012 Jingjing Li.
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

import java.util.Locale;

import org.junit.Test;

public class NumberUtilsTest {

	@Test
	public void testApproximate() {
		assertFalse(NumberUtils.approximate(1, 1 - 0x1p-53, 0));
		assertTrue(NumberUtils.approximate(1, 1 - 0x1p-53, 1));
		assertFalse(NumberUtils.approximate(1, 1 - 0x1p-52, 1));
		assertTrue(NumberUtils.approximate(1, 1 - 0x1p-52, 2));
		assertFalse(NumberUtils.approximate(1, 1 - 0x1p-50, 3));
		assertTrue(NumberUtils.approximate(1, 1 - 0x1p-50, 4));

		assertTrue(NumberUtils.approximate(1, 1 + 0x1p-53, 0));
		assertFalse(NumberUtils.approximate(1, 1 + 0x1p-52, 0));
		assertTrue(NumberUtils.approximate(1, 1 + 0x1p-52, 1));
		assertFalse(NumberUtils.approximate(1, 1 + 0x1p-51, 1));
		assertTrue(NumberUtils.approximate(1, 1 + 0x1p-51, 2));
		assertFalse(NumberUtils.approximate(1, 1 + 0x1p-49, 3));
		assertTrue(NumberUtils.approximate(1, 1 + 0x1p-49, 4));

		assertFalse(NumberUtils.approximate(-1, -1 + 0x1p-53, 0));
		assertTrue(NumberUtils.approximate(-1, -1 + 0x1p-53, 1));
		assertFalse(NumberUtils.approximate(-1, -1 + 0x1p-52, 1));
		assertTrue(NumberUtils.approximate(-1, -1 + 0x1p-52, 2));
		assertFalse(NumberUtils.approximate(-1, -1 + 0x1p-50, 3));
		assertTrue(NumberUtils.approximate(-1, -1 + 0x1p-50, 4));

		assertTrue(NumberUtils.approximate(-1, -1 - 0x1p-53, 0));
		assertFalse(NumberUtils.approximate(-1, -1 - 0x1p-52, 0));
		assertTrue(NumberUtils.approximate(-1, -1 - 0x1p-52, 1));
		assertFalse(NumberUtils.approximate(-1, -1 - 0x1p-51, 1));
		assertTrue(NumberUtils.approximate(-1, -1 - 0x1p-51, 2));
		assertFalse(NumberUtils.approximate(-1, -1 - 0x1p-49, 3));
		assertTrue(NumberUtils.approximate(-1, -1 - 0x1p-49, 4));
	}

	@Test
	public void testCalcLabelFormatStrDouble() {
		assertEquals(NumberUtils.calcFormatStr(Double.NaN, 15), "%.0f");
		assertEquals(NumberUtils.calcFormatStr(Double.POSITIVE_INFINITY, 15), "%.0f");
		assertEquals(NumberUtils.calcFormatStr(Double.NEGATIVE_INFINITY, 15), "%.0f");
		assertEquals(NumberUtils.calcFormatStr(0d, 15), "%.0f");
		assertEquals(NumberUtils.calcFormatStr(1d, 15), "%.0f");
		assertEquals(NumberUtils.calcFormatStr(-1d, 15), "%.0f");
		assertEquals(NumberUtils.calcFormatStr(0.01, 15), "%.2f");
		assertEquals(NumberUtils.calcFormatStr(0.001, 15), "%.3f");
		assertEquals(NumberUtils.calcFormatStr(0.0011, 15), "%.4f");
		assertEquals(NumberUtils.calcFormatStr(0.00111, 15), "%.5f");

		assertEquals(NumberUtils.calcFormatStr(10d, 15), "%.0f");
		assertEquals(NumberUtils.calcFormatStr(100d, 15), "%.0f");
		assertEquals(NumberUtils.calcFormatStr(1000d, 15), "%.0f");
		assertEquals(NumberUtils.calcFormatStr(11000d, 15), "%.0f");
		assertEquals(NumberUtils.calcFormatStr(111000d, 15), "%.0f");

		// more than 4 '0' can be saved, produce 'e' conversion
		assertEquals(NumberUtils.calcFormatStr(0.0001, 15), "%.0e");
		assertEquals(NumberUtils.calcFormatStr(10000d, 15), "%.0e");
		assertEquals(NumberUtils.calcFormatStr(110000d, 15), "%.1e");

		// more than 14 significant digits
		assertEquals(NumberUtils.calcFormatStr(1e14d + 1d, 15), "%.14e");
		assertEquals(NumberUtils.calcFormatStr(1e15d + 1d, 15), "%.0e");
	}

	@Test
	public void testCalcLabelFormatStrFloat() {
		assertEquals(NumberUtils.calcFormatStr(Float.NaN, 6), "%.0f");
		assertEquals(NumberUtils.calcFormatStr(Float.POSITIVE_INFINITY, 6), "%.0f");
		assertEquals(NumberUtils.calcFormatStr(Float.NEGATIVE_INFINITY, 6), "%.0f");
		assertEquals(NumberUtils.calcFormatStr(0f, 6), "%.0f");
		assertEquals(NumberUtils.calcFormatStr(1f, 6), "%.0f");
		assertEquals(NumberUtils.calcFormatStr(-1f, 6), "%.0f");
		assertEquals(NumberUtils.calcFormatStr(0.01f, 6), "%.2f");
		assertEquals(NumberUtils.calcFormatStr(0.001f, 6), "%.3f");
		assertEquals(NumberUtils.calcFormatStr(0.0011f, 6), "%.4f");
		assertEquals(NumberUtils.calcFormatStr(0.00111f, 6), "%.5f");

		assertEquals(NumberUtils.calcFormatStr(10f, 6), "%.0f");
		assertEquals(NumberUtils.calcFormatStr(100f, 6), "%.0f");
		assertEquals(NumberUtils.calcFormatStr(1000f, 6), "%.0f");
		assertEquals(NumberUtils.calcFormatStr(11000f, 6), "%.0f");
		assertEquals(NumberUtils.calcFormatStr(111000f, 6), "%.0f");

		// more than 4 '0' can be saved, produce 'e' conversion
		assertEquals(NumberUtils.calcFormatStr(0.0001f, 6), "%.0e");
		assertEquals(NumberUtils.calcFormatStr(10000f, 6), "%.0e");
		assertEquals(NumberUtils.calcFormatStr(110000f, 6), "%.1e");

		// more than 5 significant digits
		assertEquals(NumberUtils.calcFormatStr(1000010f, 6), "%.5e");
		assertEquals(NumberUtils.calcFormatStr(1000001f, 6), "%.0e");
	}

	@Test
	public void calcDeltaFormatStr() {
		assertEquals(NumberUtils.calcDeltaFormatStr(Float.NaN, 1), "%.0f");
		assertEquals(NumberUtils.calcDeltaFormatStr(0, 1e-3), "%.0f");

		assertEquals(NumberUtils.calcDeltaFormatStr(1, 1e2), "0");
		assertEquals(NumberUtils.calcDeltaFormatStr(1e-3, 1), "0");
		assertEquals(NumberUtils.calcDeltaFormatStr(1e-3, 1e-1), "%.1f");
		assertEquals(NumberUtils.calcDeltaFormatStr(1e-3, 1e-2), "%.2f");
		assertEquals(NumberUtils.calcDeltaFormatStr(1e-3, 1e-3), "%.3f");
		assertEquals(NumberUtils.calcDeltaFormatStr(1e-8, 1e-3), "%.3f");
		assertEquals(NumberUtils.calcDeltaFormatStr(1e-4, 1e-4), "%.4f");
		assertEquals(NumberUtils.calcDeltaFormatStr(1e-8, 1e-4), "%.4f");
		assertEquals(NumberUtils.calcDeltaFormatStr(1e-6, 1e-5), "0");
		assertEquals(NumberUtils.calcDeltaFormatStr(1e-8, 1e-5), "0");

		assertEquals(NumberUtils.calcDeltaFormatStr(1e-1, 1e-3), "%.3f");
		assertEquals(NumberUtils.calcDeltaFormatStr(1.1e-1, 1e-3), "%.3f");
		assertEquals(NumberUtils.calcDeltaFormatStr(1.1e-1, 1.1e-3), "%.3f");
		assertEquals(NumberUtils.calcDeltaFormatStr(1e-2, 1e-7), "%.7f");
		assertEquals(NumberUtils.calcDeltaFormatStr(1e-3, 1e-7), "%.7f");
		assertEquals(NumberUtils.calcDeltaFormatStr(1e-3, 1e-8), "%.8f");
		assertEquals(NumberUtils.calcDeltaFormatStr(1e-4, 1e-7), "%.7f");
		assertEquals(NumberUtils.calcDeltaFormatStr(1e-4, 1e-8), "%.8f");
		assertEquals(NumberUtils.calcDeltaFormatStr(1e-5, 1e-7), "%.2e");
		assertEquals(NumberUtils.calcDeltaFormatStr(1e-5, 1e-8), "%.3e");
		assertEquals(NumberUtils.calcDeltaFormatStr(1e5, 1), "%.0f");
		assertEquals(NumberUtils.calcDeltaFormatStr(1e6, 1), "%.6e");
		assertEquals(NumberUtils.calcDeltaFormatStr(1e5, 1e4), "%.0f");
		assertEquals(NumberUtils.calcDeltaFormatStr(1e6, 1e5), "%.1e");
		assertEquals(NumberUtils.calcDeltaFormatStr(1e7, 1e6), "%.1e");
		assertEquals(NumberUtils.calcDeltaFormatStr(1e8, 1e7), "%.1e");

		assertEquals(String.format((Locale) null, "0", 0.001), "0");
		assertEquals(String.format((Locale) null, "%.2f", 0.001), "0.00");
	}
}
