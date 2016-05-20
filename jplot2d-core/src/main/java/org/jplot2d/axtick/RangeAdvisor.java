/*
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
package org.jplot2d.axtick;

import org.jplot2d.util.Range;

/**
 * Calculate a proper range to arrange major ticks falling on the edge.
 *
 * @author Jingjing Li
 */
public interface RangeAdvisor {

    /**
     * Returns the range.
     *
     * @return the range
     */
    public Range getRange();

    /**
     * Sets the range.
     *
     * @param range the range
     */
    public void setRange(Range range);

    /**
     * Expand the range by the given tick number. The interval will be 1,2,5 * 10^n
     *
     * @param tickNumber the tick number
     */
    public void expandRangeByTickNumber(int tickNumber);

    /**
     * Expand the range by the given tick interval.
     *
     * @param interval the tick interval
     */
    public void expandRangeByTickInterval(double interval);

    /**
     * Returns the tick interval.
     *
     * @return the tick interval.
     */
    public double getInterval();

}
