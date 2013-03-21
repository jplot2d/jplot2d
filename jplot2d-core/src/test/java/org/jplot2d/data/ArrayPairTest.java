/**
 * Copyright 2010, 2011 Jingjing Li.
 *
 * This file is part of jplot2d.
 *
 * jplot2d is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or any later version.
 *
 * jplot2d is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Lesser Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with jplot2d. If not, see <http://www.gnu.org/licenses/>.
 */
package org.jplot2d.data;

import static org.jplot2d.util.TestUtils.*;
import static org.junit.Assert.*;

import org.junit.Test;

/**
 * @author Jingjing Li
 * 
 */
public class ArrayPairTest {

	/**
	 * Test method for
	 * {@link org.jplot2d.data.ArrayPair#append(java.lang.Object, java.lang.Object, int)}
	 * .
	 */
	@Test
	public void testAppendObjectObjectInt() {
		ArrayPair ap = new ArrayPair(new double[] { 0, 1 },
				new double[] { 0, 1 });
		ap = ap.append(new double[] { 3 }, new double[] { 3 }, 1);
		assertEquals(ap.size(), 3);
		checkDoubleArray(ap.getPArray(), 0, 1, 3);
		checkDoubleArray(ap.getQArray(), 0, 1, 3);
		ap = ap.append(new double[] { 4 }, new double[] { 4 }, 1);
		assertEquals(ap.size(), 4);
		checkDoubleArray(ap.getPArray(), 0, 1, 3, 4);
		checkDoubleArray(ap.getQArray(), 0, 1, 3, 4);
	}

}
