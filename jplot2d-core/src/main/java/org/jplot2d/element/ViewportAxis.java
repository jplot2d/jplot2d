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
import org.jplot2d.axtype.AxisType;

/**
 * A viewport axis define a transformation of viewport. It may contains a group
 * of axes, which represent the same user range with different ticks.
 * 
 * @author Jingjing Li
 * 
 */
public interface ViewportAxis extends Container {

	/**
	 * Orientation is not a set-able property. It just show the orientation of
	 * this axis after it has been add as a X/Y axis.
	 * 
	 * @return orientation of this axis
	 */
	public AxisOrientation getOrientation();

	/**
	 * Return the type of this axis.
	 * 
	 * @return the type of this axis
	 */
	public AxisType getType();

	/**
	 * Set the type of the axis.
	 * 
	 * @param type
	 *            the axis type
	 */
	public void setType(AxisType type);

	/**
	 * Returns the lock group to that this axis group belongs. A axis group must
	 * has a lock group, which have this axis group at least.
	 * 
	 * @return
	 */
	@Hierarchy(HierarchyOp.GET)
	public AxisLockGroup getLockGroup();

	/**
	 * Join an axis lock group. The lock group must exist in the same
	 * environment, otherwise an exception will be thrown.
	 * 
	 * @param group
	 *            the lock group to join to.
	 * 
	 * @return the old lock group to that this axis belongs
	 */
	@Hierarchy(HierarchyOp.JOIN)
	public AxisLockGroup setLockGroup(AxisLockGroup group);

	@Hierarchy(HierarchyOp.GET)
	public Axis getAxis(int index);

	/**
	 * Returns all axes belongs to this group.
	 * 
	 * @return all axes belongs to this group
	 */
	@Hierarchy(HierarchyOp.GETARRAY)
	public Axis[] getAxes();

	@Hierarchy(HierarchyOp.ADD)
	public void addAxis(Axis axis);

	@Hierarchy(HierarchyOp.REMOVE)
	public void removeAxis(Axis axis);

	/**
	 * Returns all layers attaching to this axis group.
	 * 
	 * @return
	 */
	@Hierarchy(HierarchyOp.GETARRAY)
	public Layer[] getLayers();

}
