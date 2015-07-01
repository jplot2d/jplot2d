/*
 * Copyright 2010-2015 Jingjing Li.
 *
 * This file is part of jplot2d.
 *
 * jplot2d is free software:
 * you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation, either version 3 of the License, or any later version.
 *
 * jplot2d is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Lesser Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with jplot2d.
 * If not, see <http://www.gnu.org/licenses/>.
 */
package org.jplot2d.data;

import org.jplot2d.util.Range;

/**
 * The common interface for graph data.
 *
 * @author Jingjing Li
 */
public interface GraphData {

    /**
     * Apply the new boundary on x/y direction. Data outside the boundary are ignored.
     *
     * @param xboundary the new boundary to apply
     * @param yboundary the new boundary to apply
     * @return a new GraphData instance
     */
    GraphData applyBoundary(Range xboundary, Range yboundary);

    /**
     * Returns x range which contains all valid data in x boundary.
     * If there is no valid data in x boundary, the empty property of returned Range will be set to <code>true</code>
     */
    Range getXRange();

    /**
     * Returns y range which contains all valid data in y boundary.
     * If there is no valid data in y boundary, the empty property of returned Range will be set to <code>true</code>
     */
    Range getYRange();

    /**
     * Returns <code>true</code> if there are valid data out of the x boundary.
     *
     * @return the flag
     */
    boolean hasPointOutsideXBounds();

    /**
     * Returns <code>true</code> if there are valid data out of the y boundary.
     *
     * @return the flag
     */
    public boolean hasPointOutsideYBounds();

}