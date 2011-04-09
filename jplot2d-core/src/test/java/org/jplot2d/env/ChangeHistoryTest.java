/**
 * Copyright 2010 Jingjing Li.
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
package org.jplot2d.env;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * @author Jingjing Li
 *
 */
public class ChangeHistoryTest {

	@Test
	public void testChangHistory() {
		UndoManager<Integer> ch = new UndoManager<Integer>(5);
		assertNull(ch.current());
		assertFalse(ch.canUndo());
		assertFalse(ch.canRedo());
		assertEquals(ch.getUndoSize(), 0);
		
		ch.add(1);
		assertEquals(ch.current().intValue(), 1);
		assertFalse(ch.canUndo());
		assertFalse(ch.canRedo());
		assertEquals(ch.getUndoSize(), 0);
		
		ch.add(2);
		assertEquals(ch.current().intValue(), 2);
		assertTrue(ch.canUndo());
		assertFalse(ch.canRedo());
		assertEquals(ch.getUndoSize(), 1);
		
		int u = ch.undo();
		assertEquals(u, 1);
		assertEquals(ch.current().intValue(), 1);
		assertFalse(ch.canUndo());
		assertTrue(ch.canRedo());
		assertEquals(ch.getUndoSize(), 0);
		
	}
	
}
