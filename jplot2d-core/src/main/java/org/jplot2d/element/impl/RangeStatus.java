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
/**
 * $Id: RangeStatus.java,v 1.5 2010/04/19 04:18:31 jli Exp $
 */
package org.jplot2d.element.impl;

import org.jplot2d.util.Range;

/**
 * Extends the Range to supply a status object. Immutable.
 * 
 * @author Jingjing Li
 * 
 * @param <T>
 *            the status type
 */
class RangeStatus<T> extends Range.Double {

    private static final long serialVersionUID = 1L;

    private final T _status;

    public RangeStatus(double start, double end, T status) {
        super(start, end);
        this._status = status;
    }

    public RangeStatus(Range range, T status) {
        super(range);
        this._status = status;
    }

    public T getStatus() {
        return _status;
    }

}