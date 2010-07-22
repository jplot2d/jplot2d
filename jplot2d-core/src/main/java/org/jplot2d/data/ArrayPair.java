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

    private final Object _p, _q;

    final Class<?> _pct, _qct;

    private final int _size;

    public ArrayPair(Object p, Object q) {
        int plen = Array.getLength(p);
        int qlen = Array.getLength(q);
        if (plen != qlen) {
            throw new IllegalArgumentException(
                    "The length of the pair of arrays must be equal.");
        }
        _pct = p.getClass().getComponentType();
        _qct = q.getClass().getComponentType();
        _p = p;
        _q = q;
        _size = plen;
    }

    public int size() {
        return _size;
    }

    public Class<?> getPComponentType() {
        return _pct.getComponentType();
    }

    public Class<?> getQComponentType() {
        return _qct.getComponentType();
    }

    public Object getPArray() {
        return _p;
    }

    public Object getQArray() {
        return _q;
    }

    public double getPDouble(int idx) {
        return Array.getDouble(_p, idx);
    }

    public double getQDouble(int idx) {
        return Array.getDouble(_q, idx);
    }

    public ArrayPair append(ArrayPair arrayPair) {
        Object newp = Array.newInstance(_pct, _size + arrayPair._size);
        System.arraycopy(_p, 0, newp, 0, _size);
        System.arraycopy(arrayPair._p, 0, newp, _size, arrayPair._size);
        Object newq = Array.newInstance(_qct, _size + arrayPair._size);
        System.arraycopy(_q, 0, newq, 0, _size);
        System.arraycopy(arrayPair._q, 0, newq, _size, arrayPair._size);

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
