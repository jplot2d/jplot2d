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

public class ShortBottomNFinderTest {

    @Test
    public void testArray1() {
        short[] a = new short[] { 3 };
        ShortBottomNFinder tf = new ShortBottomNFinder(a);
        assertTrue(tf.getMax() == 3);
        assertTrue(tf.getMax2nd() == 3);
        tf.check((short) 2);
        assertTrue(tf.getMax() == 2);
        assertTrue(tf.getMax2nd() == 2);
    }

    @Test
    public void testCheckValue() {
        short[] a = new short[] { 1, 2, 3, 4, 5 };
        ShortBottomNFinder bf = new ShortBottomNFinder(a);
        assertTrue(bf.getMax() == 5);
        assertTrue(bf.getMax2nd() == 4);
        bf.check((short) 4);
        assertTrue(bf.getMax() == 4);
        assertTrue(bf.getMax2nd() == 4);
        bf.check((short) 3);
        assertTrue(bf.getMax() == 4);
        assertTrue(bf.getMax2nd() == 3);
        bf.check((short) 5);
        assertTrue(bf.getMax() == 4);
        assertTrue(bf.getMax2nd() == 3);
    }

}
