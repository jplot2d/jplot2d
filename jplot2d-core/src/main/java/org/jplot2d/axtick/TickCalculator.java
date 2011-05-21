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

import java.text.Format;

import org.jplot2d.util.Range2D;

/**
 * A calculator to calculate tick values and minor tick values
 * 
 * @author Jingjing Li
 */
public interface TickCalculator {

	public static final int AUTO_MINORTICK_NUMBER = -1;

	/**
	 * The Tolerance error for double computing. doublePrecisionLimit is
	 * 0x1.0p-52
	 */
	public static final double DOUBLE_PRECISION_TOLERANCE = 0x1.0p-40;

	public Range2D getRange();

	public void setRange(Range2D range);

	/**
	 * Calculate the tick values by the given tick number and minor ticks
	 * number. The minor ticks number is a proposed value, and may be different
	 * from actual minor ticks number returned by {@link #getMinorNumber()}.
	 * 
	 * @param tickNumber
	 * @param minorTickNumber
	 *            if the given number is {@link #AUTO_MINORTICK_NUMBER}, the
	 *            tick number is automatically chosen.
	 */
	public void calcValuesByTickNumber(int tickNumber, int minorTickNumber);

	/**
	 * Calculate the tick values by the given interval and minor ticks number.
	 * The minor ticks number is a proposed value, and may be different from
	 * actual minor ticks number returned by {@link #getMinorNumber()}.
	 * 
	 * @param interval
	 * @param offset
	 * @param minorTickNumber
	 *            if the given number is {@link #AUTO_MINORTICK_NUMBER}, the
	 *            tick number is derived from interval.
	 */
	public void calcValuesByTickInterval(double interval, double offset,
			int minorTickNumber);

	/**
	 * @return the tick interval.
	 */
	public double getInterval();

	/**
	 * @return the actual minor tick number.
	 */
	public int getMinorNumber();

	/**
	 * @return the tick values in ascend order
	 */
	public Object getValues();

	/**
	 * @return the minor tick values in ascend order
	 */
	public Object getMinorValues();

	/**
	 * Return all values within the range.
	 * 
	 * @param v
	 * @return
	 */
	public int[] getInRangeValuesIdx(Object v);

	/**
	 * Calculate a proper text format to format the labels on given values. A
	 * calculator can just returns a text format and do not provide a format
	 * string (returns a empty string). If The returned text format is
	 * <code>null</code>, a proper format string must be provided.
	 * 
	 * @param values
	 * @return a text format object
	 */
	public Format calcAutoLabelTextFormat(Object values);

	/**
	 * Calculate a proper format string to format the labels on given values.
	 * 
	 * @param values
	 * @return
	 */
	public String calcAutoLabelFormat(Object values);

	/**
	 * Returns a proper format string to format the labels on ticks values
	 * returned by {@link #getValues()}. Some calculator can derive format from
	 * its internal status directly.
	 */
	public String getLabelFormate();

}
