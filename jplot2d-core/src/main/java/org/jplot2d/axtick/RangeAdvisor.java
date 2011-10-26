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
