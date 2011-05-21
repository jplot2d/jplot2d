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

import java.util.Deque;
import java.util.LinkedList;

/**
 * Maintains a queue of undo sets, a current set, and a redo sets.
 * 
 * @author Jingjing Li
 * 
 */
public class UndoManager<T> {

	/**
	 * Changeset SN.
	 */
	private int csn;

	private T cur;

	/**
	 * The capacity of undo stack. Default is 0.
	 */
	private int capacity;

	private Deque<T> undoQue = new LinkedList<T>();

	private Deque<T> redoQue = new LinkedList<T>();

	/**
	 * Construct a ChangeHistory with capacity 0.
	 */
	public UndoManager() {

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
		if (capacity < 0) {
			throw new IllegalArgumentException("Illegal Capacity: " + capacity);
		}
		this.capacity = capacity;
	}

	/**
	 * Returns the capacity of undo stack.
	 * 
	 * @return
	 */
	public int getCapacity() {
		return capacity;
	}

	/**
	 * Sets the The capacity of undo stack. The default capacity is 0.
	 * 
	 * @param capacity
	 */
	public void setCapacity(int capacity) {
		int pollout = undoQue.size() - capacity;
		if (pollout > 0) {
			for (int i = 0; i < pollout; i++) {
				undoQue.pollLast();
			}
		}
		this.capacity = capacity;
	}

	/**
	 * Returns the possible undo steps. The size dose not count the current
	 * change set.
	 * 
	 * @return
	 */
	public int getUndoSize() {
		return undoQue.size();
	}

	/**
	 * Return the current cset.
	 * 
	 * @return
	 */
	public T current() {
		return cur;
	}

	/**
	 * Add the given cset into this history.
	 * 
	 * @param cs
	 *            the change set
	 * @return the change SN
	 */
	public void add(T cs) {
		if (cur != null) {
			undoQue.push(cur);
			if (undoQue.size() > capacity) {
				undoQue.pollLast();
			}
		}
		redoQue.clear();
		cur = cs;
		csn++;
	}

	/**
	 * Returns <code>true</code> if undo is possible.
	 * 
	 * @return
	 */
	public boolean canUndo() {
		return undoQue.size() > 0;
	}

	/**
	 * Move the cset before the current set to current, and return it.
	 * 
	 * @return
	 */
	public T undo() {
		if (canUndo()) {
			csn--;
			redoQue.push(cur);
			cur = undoQue.pop();
			return cur;
		}
		return null;
	}

	/**
	 * Returns <code>true</code> if redo is possible.
	 * 
	 * @return
	 */
	public boolean canRedo() {
		return redoQue.size() > 0;
	}

	/**
	 * Returns the next cset after the current cset. Returns <code>null</code>
	 * if there is no next cset.
	 * 
	 * @return
	 */
	public T redo() {
		if (canRedo()) {
			csn++;
			undoQue.push(cur);
			cur = redoQue.pop();
			return cur;
		}
		return null;
	}

}
