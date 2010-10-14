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
 * Main axis associate with an axis transform. axis transform defines the axis
 * value range.
 * 
 * @author Jingjing Li
 * 
 */
public interface MainAxis extends Axis {

	/**
	 * Returns the group to that this axis belongs. A main axis must belongs to
	 * a group, which have this axis at least.
	 * 
	 * @return
	 */
	@Hierarchy(HierarchyOp.GET)
	public AxisGroup getGroup();

	/**
	 * Join an axis group. The group must exist in the same environment,
	 * otherwise an exception will be thrown.
	 * 
	 * @param group
	 *            the group to join to.
	 * 
	 * @return the old group to that this axis belongs
	 */
	@Hierarchy(HierarchyOp.JOIN)
	public AxisGroup setGroup(AxisGroup group);

	/**
	 * Returns all layers attaching to this axis.
	 * 
	 * @return
	 */
	@Hierarchy(HierarchyOp.GETARRAY)
	public Layer[] getLayers();

	/**
	 * Returns all auxiliary axes attaching to this axis.
	 * 
	 * @return
	 */
	@Hierarchy(HierarchyOp.GETARRAY)
	public AuxAxis[] getAuxAxes();

}
