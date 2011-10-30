/**
 * Copyright 2010, 2011 Jingjing Li.
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
import org.jplot2d.annotation.Property;
import org.jplot2d.annotation.PropertyGroup;

/**
 * A group of axes who are pinned together. When one axis changes its range, all axes follow the
 * change together.
 * 
 * @author Jingjing Li
 * 
 */
@PropertyGroup("Axis Range Lock Group")
public interface AxisRangeLockGroup extends Element {

	/**
	 * Return the autorange status of this axis group.
	 * 
	 * @return true if the axis is in autorange status
	 */
	@Property(order = 0)
	public boolean isAutoRange();

	/**
	 * If true set the range of the axis group to allow to display the minimum and maximum value of
	 * the Layer.
	 * 
	 * @param isAutoRange
	 *            the flag.
	 */
	public void setAutoRange(boolean autoRange);

	/**
	 * Zoom the given range to entire axis
	 * 
	 * @param start
	 *            the normalized start
	 * @param end
	 *            the normalized end
	 */
	public void zoomRange(double start, double end);

	/**
	 * Returns all axes belongs to this group.
	 * 
	 * @return all axes belongs to this group
	 */
	@Hierarchy(HierarchyOp.GETARRAY)
	public AxisTransform[] getRangeManagers();

}
