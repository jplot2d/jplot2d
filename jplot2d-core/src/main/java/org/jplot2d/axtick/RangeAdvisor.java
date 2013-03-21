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
package org.jplot2d.axtick;

import org.jplot2d.util.Range;

/**
 * Calculate a proper range to arrange major ticks falling on the edge.
 * 
 * @author Jingjing Li
 * 
 */
public interface RangeAdvisor {

	public Range getRange();

	public void setRange(Range range);

	/**
	 * The interval will be 1,2,5 * 10^n
	 * 
	 * @param range
	 *            the old range
	 * @param tickNumber
	 */
	public void expandRangeByTickNumber(int tickNumber);

	/**
	 * 
	 * @param range
	 *            the old range
	 * @param interval
	 */
	public void expandRangeByTickInterval(double interval);

	/**
	 * @return the tick interval.
	 */
	public double getInterval();

}
