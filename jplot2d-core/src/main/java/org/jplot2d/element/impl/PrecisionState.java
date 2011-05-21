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
 * $Id: PrecisionState.java,v 1.2 2010/02/02 10:17:04 hsclib Exp $
 */
package org.jplot2d.element.impl;

/**
 * Thrown to indicate that the given value is closed to precision limit and may
 * lead to inaccurate result.
 * 
 * @author Jingjing Li
 */
class PrecisionState {

    private final String msg;

    public PrecisionState() {
        msg = null;
    }

    public PrecisionState(String message) {
        msg = message;
    }

    public String getMessage() {
        return msg;
    }

}
