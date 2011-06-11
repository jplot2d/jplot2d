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
package org.jplot2d.data;

import java.lang.reflect.Array;

/**
 * A pair of array (P/Q), which have the same length. Immutable.
 * 
 * @author Jingjing Li
 */
public class ArrayPair implements Cloneable {

	private final Object p, q;

	private final Class<?> pctype, qctype;

	private final int size;

	/**
	 * Create a ArrayPair instance with the given p/q array. The length of the
	 * p/q array must be same.
	 * 
	 * @param p
	 * @param q
	 */
	public ArrayPair(Object p, Object q) {
		int plen = Array.getLength(p);
		int qlen = Array.getLength(q);
		if (plen != qlen) {
			throw new IllegalArgumentException(
					"The length of the pair of arrays must be equal.");
		}
		pctype = p.getClass().getComponentType();
		qctype = q.getClass().getComponentType();
		this.p = p;
		this.q = q;
		size = plen;
	}

	/**
	 * Create a ArrayPair instance with the given p/q array and length. The
	 * length of the p/q array must be same or longer than the given length.
	 * 
	 * @param p
	 * @param q
	 * @param length
	 *            the length to be used in this ArrayPair
	 */
	public ArrayPair(Object p, Object q, int length) {
		int plen = Array.getLength(p);
		int qlen = Array.getLength(q);
		if (plen < length) {
			throw new IllegalArgumentException(
					"The length of the p arrays must be equal or longer than the given length.");
		}
		if (qlen < length) {
			throw new IllegalArgumentException(
					"The length of the q arrays must be equal or longer than the given length.");
		}
		pctype = p.getClass().getComponentType();
		qctype = q.getClass().getComponentType();
		this.p = p;
		this.q = q;
		size = length;
	}

	public int size() {
		return size;
	}

	public Class<?> getPComponentType() {
		return pctype.getComponentType();
	}

	public Class<?> getQComponentType() {
		return qctype.getComponentType();
	}

	public Object getPArray() {
		return p;
	}

	public Object getQArray() {
		return q;
	}

	public double getPDouble(int idx) {
		return Array.getDouble(p, idx);
	}

	public double getQDouble(int idx) {
		return Array.getDouble(q, idx);
	}

	public ArrayPair append(ArrayPair arrayPair) {
		Object newp = Array.newInstance(pctype, size + arrayPair.size);
		System.arraycopy(p, 0, newp, 0, size);
		System.arraycopy(arrayPair.p, 0, newp, size, arrayPair.size);
		Object newq = Array.newInstance(qctype, size + arrayPair.size);
		System.arraycopy(q, 0, newq, 0, size);
		System.arraycopy(arrayPair.q, 0, newq, size, arrayPair.size);

		return new ArrayPair(newp, newq);
	}

	public ArrayPair append(Object ap, Object aq, int length) {
		Object newp = Array.newInstance(pctype, size + length);
		System.arraycopy(p, 0, newp, 0, size);
		System.arraycopy(ap, 0, newp, size, length);
		Object newq = Array.newInstance(qctype, size + length);
		System.arraycopy(q, 0, newq, 0, size);
		System.arraycopy(aq, 0, newq, size, length);

		return new ArrayPair(newp, newq);
	}

	public ArrayPair copy() {
		try {
			return (ArrayPair) this.clone();
		} catch (CloneNotSupportedException e) {
			throw new InternalError();
		}
	}
}
