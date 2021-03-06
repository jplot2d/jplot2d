/*
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
package org.jplot2d.element.impl;

/**
 * The getParent() will returns the only parent of this element. If this element has multiple
 * referencer, getParent() returns <code>null</code>. This make a referenceable element can be
 * removed together with its only referencer.
 *
 * @author Jingjing Li
 */
public interface Joinable {

    /**
     * Returns the primary referencer.
     *
     * @return the primary referencer
     */
    public ElementEx getPrim();

}
