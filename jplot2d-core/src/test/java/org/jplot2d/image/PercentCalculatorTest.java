/*
 * This file is part of Herschel Common Science System (HCSS).
 * Copyright 2001-2012 Herschel Science Ground Segment Consortium
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
package org.jplot2d.image;

import static org.junit.Assert.*;

import java.awt.Dimension;

import org.jplot2d.data.ByteDataBuffer;
import org.jplot2d.data.ImageDataBuffer;
import org.junit.Test;

public class PercentCalculatorTest {

	@Test
	public void testCalculateCutLevelsPercentByte0() {
		byte[][] a2d = {};
		double[] cuts = new PercentCalculator(100).calcLimits(
				new ImageDataBuffer[] { new ByteDataBuffer.Array2D(a2d) }, new Dimension[] { new Dimension(0, 0) });
		assertNull(cuts);
	}

	@Test
	public void testCalculateCutLevelsPercentByte0a() {
		byte[][] a2d = {};
		double[] cuts = new PercentCalculator(99.5).calcLimits(
				new ImageDataBuffer[] { new ByteDataBuffer.Array2D(a2d) }, new Dimension[] { new Dimension(0, 0) });
		assertNull(cuts);
	}

	@Test
	public void testCalculateCutLevelsPercentByte1x0() {
		byte[][] a2d = { {} };
		double[] cuts = new PercentCalculator(100).calcLimits(
				new ImageDataBuffer[] { new ByteDataBuffer.Array2D(a2d) }, new Dimension[] { new Dimension(0, 1) });
		assertNull(cuts);
	}

	@Test
	public void testCalculateCutLevelsPercentByte1x0a() {
		byte[][] a2d = { {} };
		double[] cuts = new PercentCalculator(99.5).calcLimits(
				new ImageDataBuffer[] { new ByteDataBuffer.Array2D(a2d) }, new Dimension[] { new Dimension(0, 1) });
		assertNull(cuts);
	}

	@Test
	public void testCalculateCutLevelsPercentByte1x1() {
		byte[][] a2d = { { 0 } };
		double[] cuts = new PercentCalculator(100).calcLimits(
				new ImageDataBuffer[] { new ByteDataBuffer.Array2D(a2d) }, new Dimension[] { new Dimension(1, 1) });
		assertTrue(cuts[0] == 0);
		assertTrue(cuts[1] == 0);
	}

	@Test
	public void testCalculateCutLevelsPercentByte1x1a() {
		byte[][] a2d = { { 0 } };
		double[] cuts = new PercentCalculator(99.5).calcLimits(
				new ImageDataBuffer[] { new ByteDataBuffer.Array2D(a2d) }, new Dimension[] { new Dimension(1, 1) });
		assertTrue(cuts[0] == 0);
		assertTrue(cuts[1] == 0);
	}

	@Test
	public void testCalculateCutLevelsPercentByte2x2() {
		byte[][] a2d = { { 0, 0 }, { 0, 0 } };
		double[] cuts = new PercentCalculator(100).calcLimits(
				new ImageDataBuffer[] { new ByteDataBuffer.Array2D(a2d) }, new Dimension[] { new Dimension(2, 2) });
		assertTrue(cuts[0] == 0);
		assertTrue(cuts[1] == 0);
	}

	@Test
	public void testCalculateCutLevelsPercentByte2x2a() {
		byte[][] a2d = { { 0, 0 }, { 0, 0 } };
		double[] cuts = new PercentCalculator(99.5).calcLimits(
				new ImageDataBuffer[] { new ByteDataBuffer.Array2D(a2d) }, new Dimension[] { new Dimension(2, 2) });
		assertTrue(cuts[0] == 0);
		assertTrue(cuts[1] == 0);
	}

	@Test
	public void testCalculateCutLevelsPercentByte() {
		byte[][] a2d = { { 0, 1 }, { 2, 3 } };
		double[] cuts100 = new PercentCalculator(100).calcLimits(
				new ImageDataBuffer[] { new ByteDataBuffer.Array2D(a2d) }, new Dimension[] { new Dimension(2, 2) });
		assertTrue(cuts100[0] == 0);
		assertTrue(cuts100[1] == 3);
		double[] cuts75 = new PercentCalculator(75).calcLimits(
				new ImageDataBuffer[] { new ByteDataBuffer.Array2D(a2d) }, new Dimension[] { new Dimension(2, 2) });
		assertTrue(cuts75[0] == 0.5);
		assertTrue(cuts75[1] == 2.5);
		double[] cuts50 = new PercentCalculator(50).calcLimits(
				new ImageDataBuffer[] { new ByteDataBuffer.Array2D(a2d) }, new Dimension[] { new Dimension(2, 2) });
		assertTrue(cuts50[0] == 1);
		assertTrue(cuts50[1] == 2);
	}
}
