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

public class ShortTopNFinderTest {

    @Test
    public void testArray1() {
        short[] a = new short[] { 3 };
        ShortTopNFinder tf = new ShortTopNFinder(a);
        assertTrue(tf.getMin() == 3);
        assertTrue(tf.getMin2nd() == 3);
        tf.check((short) 4);
        assertTrue(tf.getMin() == 4);
        assertTrue(tf.getMin2nd() == 4);
    }

    @Test
    public void testCheckValue() {
        short[] a = new short[] { 1, 2, 3, 4, 5 };
        ShortTopNFinder tf = new ShortTopNFinder(a);
        assertTrue(tf.getMin() == 1);
        assertTrue(tf.getMin2nd() == 2);
        tf.check((short) 2);
        assertTrue(tf.getMin() == 2);
        assertTrue(tf.getMin2nd() == 2);
        tf.check((short) 3);
        assertTrue(tf.getMin() == 2);
        assertTrue(tf.getMin2nd() == 3);
        tf.check((short) 1);
        assertTrue(tf.getMin() == 2);
        assertTrue(tf.getMin2nd() == 3);
    }

}
