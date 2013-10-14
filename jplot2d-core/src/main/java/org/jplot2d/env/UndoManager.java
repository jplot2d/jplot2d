/**
 * Copyright 2010-2013 Jingjing Li.
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

/**
 * Maintains a queue of undo sets, a current set, and a redo sets.
 * 
 * @author Jingjing Li
 * 
 */
public class UndoManager<E> {

	private static class Entry<E> {
		E element;
		Entry<E> next;
		Entry<E> previous;

		Entry(E element, Entry<E> next, Entry<E> previous) {
			this.element = element;
			this.next = next;
			this.previous = previous;
		}
	}

	/**
	 * Change set SN.
	 */
	private int csn;

	private Entry<E> header = new Entry<E>(null, null, null);

	private int curIdx = -1;

	private Entry<E> cur = header;

	/**
	 * The capacity of undo stack. Default is 0.
	 */
	private int capacity;

	/**
	 * Construct a ChangeHistory with capacity 0.
	 */
	public UndoManager() {
		header.next = header.previous = header;
	}

	/**
	 * Construct a ChangeHistory with the given capacity.
	 * 
	 * @param capacity
	 *            the capacity of the undo stack.
	 * @throws IllegalArgumentException
	 *             if the specified capacity is negative
	 */
	public UndoManager(int capacity) throws IllegalArgumentException {
		this();
		if (capacity < 0) {
			throw new IllegalArgumentException("Illegal Capacity: " + capacity);
		}
		this.capacity = capacity;
	}

	/**
	 * Returns the capacity of undo stack.
	 * 
	 * @return the capacity of undo stack
	 */
	public int getCapacity() {
		return capacity;
	}

	/**
	 * Sets the The capacity of undo stack. The default capacity is 0.
	 * 
	 * @param capacity
	 *            the capacity of undo stack
	 */
	public void setCapacity(int capacity) {
		if (capacity < 0) {
			throw new IllegalArgumentException("Illegal Capacity: " + capacity);
		}

		this.capacity = capacity;
	}

	/**
	 * Returns the possible undo steps. The size dose not count the current change set.
	 * 
	 * @return the possible undo steps
	 */
	public int getUndoSize() {
		return curIdx == -1 ? 0 : curIdx;
	}

	/**
	 * Return the current change set.
	 * 
	 * @return the current change set
	 */
	public E current() {
		return cur == header ? null : cur.element;
	}

	/**
	 * Returns the current CSN
	 * 
	 * @return the current CSN
	 */
	public int getCSN() {
		return csn;
	}

	/**
	 * Add the given change set as current, and push old change set into history.
	 * 
	 * @param cs
	 *            the change set
	 * @return the change SN
	 */
	public void add(E cs) {
		csn++;

		if (curIdx == 0 && capacity == 0) {
			cur.element = cs;
			return;
		}

		cur = new Entry<E>(cs, header, cur);
		cur.previous.next = cur;
		cur.next.previous = cur;
		curIdx++;

		// remove the oldest change sets to satisfy capacity
		while (curIdx > capacity) {
			header.next = header.next.next;
			header.next.next.previous = header;
			curIdx--;
		}
	}

	/**
	 * Returns <code>true</code> if undo is possible.
	 * 
	 * @return <code>true</code> if undo is possible
	 */
	public boolean canUndo() {
		return cur != header && cur.previous != header;
	}

	/**
	 * Move the change set before the current set to current, and return it.
	 * 
	 * @return the previous change set
	 */
	public E undo() {
		if (canUndo()) {
			csn--;
			curIdx--;
			cur = cur.previous;
			return cur.element;
		}
		return null;
	}

	/**
	 * Returns <code>true</code> if redo is possible.
	 * 
	 * @return <code>true</code> if redo is possible
	 */
	public boolean canRedo() {
		return cur != null && cur.next != header;
	}

	/**
	 * Returns the next change set after the current change set. Returns <code>null</code> if there is no next change
	 * set.
	 * 
	 * @return the next change set
	 */
	public E redo() {
		if (canRedo()) {
			csn++;
			curIdx++;
			cur = cur.next;
			return cur.element;
		}
		return null;
	}

}
