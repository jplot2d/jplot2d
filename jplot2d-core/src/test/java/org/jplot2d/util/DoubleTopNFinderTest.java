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
package org.jplot2d.util;

import static org.junit.Assert.*;

import org.junit.Test;

public class DoubleTopNFinderTest {

    @Test
    public void testArray1() {
        double[] a = new double[] { 3d };
        DoubleTopNFinder tf = new DoubleTopNFinder(a);
        assertTrue(tf.getMin() == 3d);
        assertTrue(tf.getMin2nd() == 3d);
        tf.check(4d);
        assertTrue(tf.getMin() == 4d);
        assertTrue(tf.getMin2nd() == 4d);
    }

    @Test
    public void testCheckValue() {
        double[] a = new double[] { 1d, 2d, 3d, 4d, 5d };
        DoubleTopNFinder tf = new DoubleTopNFinder(a);
        assertTrue(tf.getMin() == 1d);
        assertTrue(tf.getMin2nd() == 2d);
        tf.check(2d);
        assertTrue(tf.getMin() == 2d);
        assertTrue(tf.getMin2nd() == 2d);
        tf.check(3d);
        assertTrue(tf.getMin() == 2d);
        assertTrue(tf.getMin2nd() == 3d);
        tf.check(1d);
        assertTrue(tf.getMin() == 2d);
        assertTrue(tf.getMin2nd() == 3d);
    }

}
