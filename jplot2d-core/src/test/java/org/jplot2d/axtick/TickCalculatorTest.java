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

import static org.junit.Assert.*;

import java.text.Format;

import org.jplot2d.util.Range;
import org.junit.Test;

/**
 * @author Jingjing Li
 * 
 */
public class TickCalculatorTest {

	private static TickCalculator linearTC = new TickCalculator() {

		@Override
		public Range getRange() {
			return null;
		}

		@Override
		public void setRange(Range range) {

		}

		@Override
		public void calcValuesByTickNumber(int tickNumber, int minorTickNumber) {

		}

		@Override
		public void calcValuesByTickInterval(double interval, double offset, int minorTickNumber) {

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
		public Object getValues() {
			return null;
		}

		@Override
		public Object getMinorValues() {
			return null;
		}

		@Override
		public int[] getInRangeValuesIdx(Object v) {
			return null;
		}

		@Override
		public Format calcAutoLabelTextFormat(Object values) {
			return null;
		}

		@Override
		public String calcAutoLabelFormat(Object values) {
			return null;
		}

		@Override
		public String getLabelFormate() {
			return null;
		}

		@Override
		public boolean isValidFormat(String format) {
			return false;
		}
	};

	@Test
	public void testFormat() {
		assertEquals("3.14 $10^2$", linearTC.format("%.2m", 314.159).toString());
		assertEquals("3.1 $10^64$", linearTC.format("%.1m", 3.14159e64).toString());
		assertEquals("-3.1 $10^64$", linearTC.format("%.1m", -3.14159e64).toString());
		assertEquals("-3.1 $10^{-64}$", linearTC.format("%.1m", -3.14159e-64).toString());
		assertEquals("$10^64$", linearTC.format("%.0m", 1e64).toString());
		assertEquals("+1 $10^64$", linearTC.format("%+.0m", 1e64).toString());
		assertEquals("-1 $10^64$", linearTC.format("%+.0m", -1e64).toString());
		assertEquals("3.142", linearTC.format("%.3m", 3.14159).toString());
		assertEquals("0.000", linearTC.format("%.3m", 0.0).toString());

		assertEquals("01/01/70", linearTC.format("%tm/%<td/%<ty", 1L).toString());
		assertEquals("01-01-70", linearTC.format("%tm-%<td-%<ty", 1L).toString());
	}

}
