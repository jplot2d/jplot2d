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
/*
 * $Id: Range2DTest.java,v 1.3 2010/10/28 04:32:45 jli Exp $
 */
package org.jplot2d.util;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * @author Jingjing Li
 * 
 */
public class Range2DTest {

    @Test
    public void testIsEmpty() {
        assertFalse(new Range2D.Double(0.1, 0.1).isEmpty());
        assertTrue(new Range2D.Double(0.1, true, 0.1, false).isEmpty());
        assertTrue(new Range2D.Double(0.1, false, 0.1, true).isEmpty());
        assertTrue(new Range2D.Double(0.1, false, 0.1, false).isEmpty());
    }

    @Test
    public void testEmptyEquals() {
        assertEquals(new Range2D.Double(0.1, true, 0.1, false),
                new Range2D.Double());
        assertEquals(new Range2D.Double(0.1, false, 0.1, true),
                new Range2D.Double());
    }

    @Test
    public void testInvert() {
        assertEquals(new Range2D.Double(0.1, true, 0.1, false).invert(),
                new Range2D.Double(0.1, false, 0.1, true));
        assertEquals(new Range2D.Double(0.1, false, 0.1, true).invert(),
                new Range2D.Double(0.1, true, 0.1, false));
    }

    @Test
    public void testContains() {
        Range2D a = new Range2D.Double(0, true, 1, false);
        assertTrue(a.contains(0));
        assertTrue(a.contains(0.5));
        assertFalse(a.contains(1));
    }

    @Test
    public void testIntersection() {
        Range2D a, b, c;

        a = new Range2D.Double(0d, false, 1d, false);
        b = new Range2D.Double(0d, true, 1d, true);
        c = a.intersect(b);
        assertEquals(c, a);

        a = new Range2D.Double(0d, false, 1d, false);
        b = new Range2D.Double(-1d, true, 2d, true);
        c = a.intersect(b);
        assertEquals(c, a);

        a = new Range2D.Double(0d, false, 1d, false);
        b = new Range2D.Double(0.1d, true, 0.9d, true);
        c = a.intersect(b);
        assertEquals(c, b);

        a = new Range2D.Double(0, true, 1, true);
        b = new Range2D.Double(0, true, 1, true);
        c = a.intersect(b);
        assertEquals(c, a);

        a = new Range2D.Double(0, true, 1, true);
        b = new Range2D.Double(-1, true, 2, true);
        c = a.intersect(b);
        assertEquals(c, a);

        a = new Range2D.Double(0, true, 1, true);
        b = new Range2D.Double(0.1, true, 0.9, true);
        c = a.intersect(b);
        assertEquals(c, b);
    }

    @Test
    public void testUnion() {
        Range2D a, b, c;

        a = new Range2D.Double(0d, false, 1d, false);
        b = new Range2D.Double(0d, true, 1d, true);
        c = a.union(b);
        assertEquals(c, b);

        a = new Range2D.Double(0d, false, 1d, false);
        b = new Range2D.Double(-1d, true, 2d, true);
        c = a.union(b);
        assertEquals(c, b);

        a = new Range2D.Double(0d, false, 1d, false);
        b = new Range2D.Double(0.1d, true, 0.9d, true);
        c = a.union(b);
        assertEquals(c, a);

        a = new Range2D.Double(0, true, 1, true);
        b = new Range2D.Double(0, true, 1, true);
        c = a.union(b);
        assertEquals(c, b);

        a = new Range2D.Double(0, true, 1, true);
        b = new Range2D.Double(-1, true, 2, true);
        c = a.union(b);
        assertEquals(c, b);

        a = new Range2D.Double(0, true, 1, true);
        b = new Range2D.Double(0.1, true, 0.9, true);
        c = a.union(b);
        assertEquals(c, a);
    }

    @Test
    public void testRang2DLong() {
        Range2D a = new Range2D.Long(1, 1);
        assertTrue(a.isStartIncluded());
        assertTrue(a.isEndIncluded());
        assertFalse(a.isEmpty());
        assertFalse(a.isInverted());

        assertEquals(a.getStart(), 1.0, 0);
        assertEquals(a.getEnd(), 1.0, 0);
        assertEquals(a.getMin(), 1.0, 0);
        assertEquals(a.getMax(), 1.0, 0);
        assertEquals(a.getSpan(), 0.0, 0);

        assertEquals(a, a.invert());

        assertEquals(new Range2D.Long(), new Range2D.Long());

        // test create from a Range2D
        assertEquals(new Range2D.Long(new Range2D.Double(1.0, 1.0)),
                new Range2D.Long(1, 1));
        assertEquals(new Range2D.Long(new Range2D.Double(0.9, 1.1)),
                new Range2D.Long(1, 1));
        assertEquals(new Range2D.Long(new Range2D.Double(0.9, -1.1)),
                new Range2D.Long(0, -1));
        assertTrue(new Range2D.Long(new Range2D.Double(0.8, 0.9)).isEmpty());
        assertTrue(new Range2D.Long(new Range2D.Double(0.9, 0.8)).isEmpty());

        // intersect
        assertEquals(new Range2D.Long(1, 5).intersect(new Range2D.Long(2, 6)),
                new Range2D.Long(2, 5));
        assertEquals(new Range2D.Long(1, 5).intersect(new Range2D.Long(6, 2)),
                new Range2D.Long(2, 5));
        assertEquals(new Range2D.Long(5, 1).intersect(new Range2D.Long(2, 6)),
                new Range2D.Long(5, 2));
        assertEquals(new Range2D.Long(5, 1).intersect(new Range2D.Long(6, 2)),
                new Range2D.Long(5, 2));

        // union
        assertEquals(new Range2D.Long(1, 5).union(new Range2D.Long(2, 6)),
                new Range2D.Long(1, 6));
        assertEquals(new Range2D.Long(1, 5).union(new Range2D.Long(6, 2)),
                new Range2D.Long(1, 6));
        assertEquals(new Range2D.Long(5, 1).union(new Range2D.Long(2, 6)),
                new Range2D.Long(6, 1));
        assertEquals(new Range2D.Long(5, 1).union(new Range2D.Long(6, 2)),
                new Range2D.Long(6, 1));

    }

}
