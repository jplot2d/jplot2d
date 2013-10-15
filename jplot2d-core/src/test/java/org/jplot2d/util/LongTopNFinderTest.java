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

public class LongTopNFinderTest {

    @Test
    public void testArray1() {
        long[] a = new long[] { 3 };
        LongTopNFinder tf = new LongTopNFinder(a);
        assertTrue(tf.getMin() == 3);
        assertTrue(tf.getMin2nd() == 3);
        tf.check(4);
        assertTrue(tf.getMin() == 4);
        assertTrue(tf.getMin2nd() == 4);
    }

    @Test
    public void testCheckValue() {
        long[] a = new long[] { 1, 2, 3, 4, 5 };
        LongTopNFinder tf = new LongTopNFinder(a);
        assertTrue(tf.getMin() == 1);
        assertTrue(tf.getMin2nd() == 2);
        tf.check(2);
        assertTrue(tf.getMin() == 2);
        assertTrue(tf.getMin2nd() == 2);
        tf.check(3);
        assertTrue(tf.getMin() == 2);
        assertTrue(tf.getMin2nd() == 3);
        tf.check(1);
        assertTrue(tf.getMin() == 2);
        assertTrue(tf.getMin2nd() == 3);
    }

}
