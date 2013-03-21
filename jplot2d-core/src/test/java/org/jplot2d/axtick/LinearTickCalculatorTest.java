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
public class LinearTickCalculatorTest {

	private static LinearTickCalculator linearTC;

	@BeforeClass
	public static void setUpBeforeClass() {
		linearTC = LinearTickAlgorithm.getInstance().createCalculator();
	}

	private double[] niceLinearTicks(double start, double end, int ticks) {
		linearTC.setRange(start, end);
		linearTC.calcValuesByTickNumber(ticks, 0);
		return linearTC.getValues();
	}

	@Test
	public void testExpandLinearTicks() {
		linearTC.setRange(0, 6);
		linearTC.expandRangeByTickNumber(5);
		checkRange(linearTC.getRange(), 0, 6);
		linearTC.calcValuesByTickNumber(5, 0);
		checkDoubleArray(linearTC.getValues(), 0, 2, 4, 6);

		linearTC.setRange(0, 5.5);
		linearTC.expandRangeByTickNumber(5);
		checkRange(linearTC.getRange(), 0, 6);
		linearTC.calcValuesByTickNumber(5, 0);
		checkDoubleArray(linearTC.getValues(), 0, 2, 4, 6);

		linearTC.setRange(0, 6);
		linearTC.expandRangeByTickNumber(6);
		checkRange(linearTC.getRange(), 0, 6);
		linearTC.calcValuesByTickNumber(6, 0);
		checkDoubleArray(linearTC.getValues(), 0, 1, 2, 3, 4, 5, 6);

	}

	/**
	 * Test method for {@link LinearTickCalculator#calcValuesByTickNumber(int, int)}
	 */
	@Test
	public void testNiceLinearTicks() {
		checkDoubleArray(niceLinearTicks(-1.6, 1.6, 10), -1.5, -1.0, -0.5, 0, 0.5, 1.0, 1.5);
		checkDoubleArray(niceLinearTicks(-1.499, 1.499, 10), -1.4, -1.2, -1.0, -0.8, -0.6, -0.4, -0.2, 0, 0.2, 0.4,
				0.6, 0.8, 1.0, 1.2, 1.4);
		checkDoubleArray(niceLinearTicks(1.6, -1.6, 10), 1.5, 1.0, 0.5, 0, -0.5, -1.0, -1.5);
		checkDoubleArray(niceLinearTicks(1.499, -1.499, 10), 1.4, 1.2, 1.0, 0.8, 0.6, 0.4, 0.2, 0, -0.2, -0.4, -0.6,
				-0.8, -1.0, -1.2, -1.4);
		checkDoubleArray(niceLinearTicks(1E10, 1E10 + 1, 10), 1.0E10, 1.00000000001E10, 1.00000000002E10,
				1.00000000003E10, 1.00000000004E10, 1.00000000005E10, 1.00000000006E10, 1.00000000007E10,
				1.00000000008E10, 1.00000000009E10, 1.0000000001E10);
		checkDoubleArray(niceLinearTicks(10, 60, 11), 10.0, 15.0, 20.0, 25.0, 30.0, 35.0, 40.0, 45.0, 50.0, 55.0, 60.0);
		checkDoubleArray(niceLinearTicks(10, 60.00000000000001, 11), 10.0, 15.0, 20.0, 25.0, 30.0, 35.0, 40.0, 45.0,
				50.0, 55.0, 60.0);
	}

	/**
	 * Test method for {@link LinearTickCalculator#calcValuesByTickInterval(double, double, int)}
	 */
	@Test
	public void testLinearInterval() {
		linearTC.setRange(-1.6, 1.6);
		linearTC.calcValuesByTickInterval(0.4, 0, 0);
		checkDoubleArray(linearTC.getValues(), -1.6, -1.2, -0.8, -0.4, 0, 0.4, 0.8, 1.2, 1.6);
		checkDoubleArray(linearTC.getMinorValues());
		linearTC.calcValuesByTickInterval(0.4, 0, 1);
		checkDoubleArray(linearTC.getValues(), -1.6, -1.2, -0.8, -0.4, 0, 0.4, 0.8, 1.2, 1.6);
		checkDoubleArray(linearTC.getMinorValues(), -1.4, -1.0, -0.6, -0.2, 0.2, 0.6, 1.0, 1.4);
		linearTC.calcValuesByTickInterval(0.4, 0.2, 1);
		checkDoubleArray(linearTC.getValues(), -1.4, -1.0, -0.6, -0.2, 0.2, 0.6, 1.0, 1.4);
		checkDoubleArray(linearTC.getMinorValues(), -1.6, -1.2, -0.8, -0.4, 0, 0.4, 0.8, 1.2, 1.6);
		linearTC.calcValuesByTickInterval(0.4, -0.1, 3);
		checkDoubleArray(linearTC.getValues(), -1.3, -0.9, -0.5, -0.1, 0.3, 0.7, 1.1, 1.5);
		checkDoubleArray(linearTC.getMinorValues(), -1.6, -1.5, -1.4, -1.2, -1.1, -1.0, -0.8, -0.7, -0.6, -0.4, -0.3,
				-0.2, 0, 0.1, 0.2, 0.4, 0.5, 0.6, 0.8, 0.9, 1.0, 1.2, 1.3, 1.4, 1.6);

		linearTC.setRange(0, 50);
		linearTC.calcValuesByTickInterval(10, 3, 1);
		checkDoubleArray(linearTC.getValues(), 3, 13, 23, 33, 43);
		checkDoubleArray(linearTC.getMinorValues(), 8, 18, 28, 38, 48);

	}

	/**
	 * Auto minor ticks on various tick interval.
	 */
	@Test
	public void testLinearAutoMinorTicks() {
		linearTC.setRange(0, 100);
		linearTC.calcValuesByTickInterval(1, 0, -1);
		assertEquals(linearTC.getMinorNumber(), 4);
		linearTC.calcValuesByTickInterval(10, 0, -1);
		assertEquals(linearTC.getMinorNumber(), 4);
		linearTC.calcValuesByTickInterval(2, 0, -1);
		assertEquals(linearTC.getMinorNumber(), 3);
		linearTC.calcValuesByTickInterval(20, 0, -1);
		assertEquals(linearTC.getMinorNumber(), 3);
		linearTC.calcValuesByTickInterval(3, 0, -1);
		assertEquals(linearTC.getMinorNumber(), 2);
		linearTC.calcValuesByTickInterval(30, 0, -1);
		assertEquals(linearTC.getMinorNumber(), 2);
		linearTC.calcValuesByTickInterval(4, 0, -1);
		assertEquals(linearTC.getMinorNumber(), 3);
		linearTC.calcValuesByTickInterval(40, 0, -1);
		assertEquals(linearTC.getMinorNumber(), 3);
		linearTC.calcValuesByTickInterval(5, 0, -1);
		assertEquals(linearTC.getMinorNumber(), 4);
		linearTC.calcValuesByTickInterval(50, 0, -1);
		assertEquals(linearTC.getMinorNumber(), 4);

		linearTC.calcValuesByTickInterval(6, 0, -1);
		assertEquals(linearTC.getMinorNumber(), 2);
		linearTC.calcValuesByTickInterval(60, 0, -1);
		assertEquals(linearTC.getMinorNumber(), 2);
		linearTC.calcValuesByTickInterval(7, 0, -1);
		assertEquals(linearTC.getMinorNumber(), 6);
		linearTC.calcValuesByTickInterval(70, 0, -1);
		assertEquals(linearTC.getMinorNumber(), 6);
		linearTC.calcValuesByTickInterval(8, 0, -1);
		assertEquals(linearTC.getMinorNumber(), 3);
		linearTC.calcValuesByTickInterval(80, 0, -1);
		assertEquals(linearTC.getMinorNumber(), 3);
		linearTC.calcValuesByTickInterval(9, 0, -1);
		assertEquals(linearTC.getMinorNumber(), 2);
		linearTC.calcValuesByTickInterval(90, 0, -1);
		assertEquals(linearTC.getMinorNumber(), 2);

		linearTC.calcValuesByTickInterval(12, 0, -1);
		assertEquals(linearTC.getMinorNumber(), 0);
	}

}
