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

public class DoubleBottomNFinderTest {

    @Test
    public void testArray1() {
        double[] a = new double[] { 3d };
        DoubleBottomNFinder tf = new DoubleBottomNFinder(a);
        assertTrue(tf.getMax() == 3d);
        assertTrue(tf.getMax2nd() == 3d);
        tf.check(2d);
        assertTrue(tf.getMax() == 2d);
        assertTrue(tf.getMax2nd() == 2d);
    }

    @Test
    public void testCheckValue() {
        double[] a = new double[] { 1d, 2d, 3d, 4d, 5d };
        DoubleBottomNFinder bf = new DoubleBottomNFinder(a);
        assertTrue(bf.getMax() == 5d);
        assertTrue(bf.getMax2nd() == 4d);
        bf.check(4d);
        assertTrue(bf.getMax() == 4d);
        assertTrue(bf.getMax2nd() == 4d);
        bf.check(3d);
        assertTrue(bf.getMax() == 4d);
        assertTrue(bf.getMax2nd() == 3d);
        bf.check(5d);
        assertTrue(bf.getMax() == 4d);
        assertTrue(bf.getMax2nd() == 3d);
    }

}
