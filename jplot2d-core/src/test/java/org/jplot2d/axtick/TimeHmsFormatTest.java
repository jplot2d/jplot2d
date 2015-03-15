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

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TimeHmsFormatTest {

    @Test
    public void testFormat0() {
        TimeHmsFormat format = new TimeHmsFormat(TickUnitConverter.IDENTITY, 0);
        assertEquals(format.format(-60.1), "-00:01:00");
        assertEquals(format.format(59.99), "00:01:00");
        assertEquals(format.format(-59.99), "-00:01:00");
    }

    @Test
    public void testFormat1() {
        TimeHmsFormat format = new TimeHmsFormat(TickUnitConverter.IDENTITY, 1);
        assertEquals(format.format(-60.1), "-00:01:00.1");
        assertEquals(format.format(59.9), "00:00:59.9");
        assertEquals(format.format(-59.9), "-00:00:59.9");
        assertEquals(format.format(-60.01), "-00:01:00.0");
        assertEquals(format.format(59.99), "00:01:00.0");
        assertEquals(format.format(-59.99), "-00:01:00.0");
    }

}
