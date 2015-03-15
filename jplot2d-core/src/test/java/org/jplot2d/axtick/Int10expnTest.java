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

/**
 * @author Jingjing Li
 */
public class Int10expnTest {

    @Test
    public void testCreateFromDouble() {
        assertEquals(new Int10expn(1.49), new Int10expn(1, 0));
        assertEquals(new Int10expn(9.51), new Int10expn(1, 1));
        assertEquals(new Int10expn(0.149), new Int10expn(1, -1));
        assertEquals(new Int10expn(0.951), new Int10expn(1, 0));
        assertEquals(new Int10expn(14.9), new Int10expn(1, 1));
        assertEquals(new Int10expn(95.1), new Int10expn(1, 2));
    }

}
