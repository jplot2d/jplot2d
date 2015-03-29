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
public class UndoManagerTest {

	@Test
	public void testUndoManager0() {
		UndoManager<Integer> ch = new UndoManager<>();
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
		assertFalse(ch.canUndo());
		assertFalse(ch.canRedo());
		assertEquals(ch.getUndoSize(), 0);

	}

	@Test
	public void testUndoManager2() {
		UndoManager<Integer> ch = new UndoManager<>(2);
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

		ch.add(2);
		assertEquals(ch.current().intValue(), 2);
		assertTrue(ch.canUndo());
		assertFalse(ch.canRedo());
		assertEquals(ch.getUndoSize(), 1);

		ch.add(3);
		assertEquals(ch.current().intValue(), 3);
		assertTrue(ch.canUndo());
		assertFalse(ch.canRedo());
		assertEquals(ch.getUndoSize(), 2);

		ch.add(4);
		assertEquals(ch.current().intValue(), 4);
		assertTrue(ch.canUndo());
		assertFalse(ch.canRedo());
		assertEquals(ch.getUndoSize(), 2);

	}

}
