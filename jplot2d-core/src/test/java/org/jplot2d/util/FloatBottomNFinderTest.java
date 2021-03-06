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

public class FloatBottomNFinderTest {

    @Test
    public void testArray1() {
        float[] a = new float[] { 3f };
        FloatBottomNFinder tf = new FloatBottomNFinder(a);
        assertTrue(tf.getMax() == 3f);
        assertTrue(tf.getMax2nd() == 3f);
        tf.check(2f);
        assertTrue(tf.getMax() == 2f);
        assertTrue(tf.getMax2nd() == 2f);
    }

    @Test
    public void testCheckValue() {
        float[] a = new float[] { 1f, 2f, 3f, 4f, 5f };
        FloatBottomNFinder bf = new FloatBottomNFinder(a);
        assertTrue(bf.getMax() == 5f);
        assertTrue(bf.getMax2nd() == 4f);
        bf.check(4f);
        assertTrue(bf.getMax() == 4f);
        assertTrue(bf.getMax2nd() == 4f);
        bf.check(3f);
        assertTrue(bf.getMax() == 4f);
        assertTrue(bf.getMax2nd() == 3f);
        bf.check(5f);
        assertTrue(bf.getMax() == 4f);
        assertTrue(bf.getMax2nd() == 3f);
    }

}
