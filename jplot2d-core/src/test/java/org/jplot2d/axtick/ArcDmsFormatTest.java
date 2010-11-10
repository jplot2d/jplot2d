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

import static org.junit.Assert.*;

import org.junit.Test;

public class ArcDmsFormatTest {

    @Test
    public void testFormat0() {
        ArcDmsFormat format = new ArcDmsFormat(0);
        assertEquals(format.format(-60.1 / 3600), "-00\u00b001\u203200\u2033");
        assertEquals(format.format(59.9 / 3600), "00\u00b001\u203200\u2033");
        assertEquals(format.format(-59.9 / 3600), "-00\u00b001\u203200\u2033");
    }

    @Test
    public void testFormat1() {
        ArcDmsFormat format = new ArcDmsFormat(1);
        assertEquals(format.format(-60.1 / 3600), "-00\u00b001\u203200.1\u2033");
        assertEquals(format.format(59.9 / 3600), "00\u00b000\u203259.9\u2033");
        assertEquals(format.format(-59.9 / 3600), "-00\u00b000\u203259.9\u2033");
        assertEquals(format.format(-60.01 / 3600), "-00\u00b001\u203200.0\u2033");
        assertEquals(format.format(59.99 / 3600), "00\u00b001\u203200.0\u2033");
        assertEquals(format.format(-59.99 / 3600),
                "-00\u00b001\u203200.0\u2033");
    }

}
