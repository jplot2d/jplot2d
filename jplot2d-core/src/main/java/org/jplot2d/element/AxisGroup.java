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
package org.jplot2d.element;

import org.jplot2d.annotation.Hierarchy;
import org.jplot2d.annotation.HierarchyOp;

/**
 * @author Jingjing Li
 * 
 */
public interface AxisGroup extends Element {

	/**
	 * Return the autorange status of this axis group.
	 * 
	 * @return true if the axis is in autorange status
	 */
	public boolean isAutoRange();

	/**
	 * If true set the range of the axis group to allow to display the minimum
	 * and maximum value of the Layer.
	 * 
	 * @param isAutoRange
	 *            the flag.
	 * @throws WarningException
	 *             This WarningException can be a NegativeValueInLogException or
	 *             multiple NegativeValueInLogException in a MultiException.
	 *             Might only be thrown when isAutoRange=<code>true</code>.
	 */
	public void setAutoRange(boolean autoRange);

	/**
	 * Zoom the given range to entire axis
	 * 
	 * @param start
	 *            the norm-physical start
	 * @param end
	 *            the norm-physical end
	 */
	public void zoomRange(double start, double end);

	/**
	 * Returns all axes belongs to this group.
	 * 
	 * @return all axes belongs to this group
	 */
	@Hierarchy(HierarchyOp.GETARRAY)
	public MainAxis[] getAxes();

}