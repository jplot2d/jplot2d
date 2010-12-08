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
package org.jplot2d.element.impl;

import org.jplot2d.annotation.Hierarchy;
import org.jplot2d.annotation.HierarchyOp;

/**
 * @author Jingjing Li
 * 
 */
public interface MultiParentElementEx extends ElementEx {

	/**
	 * Gets the only parent of this component. If this element has multiple
	 * parents, this methods returns <code>null</code>.
	 * 
	 * @return the only parent of this component
	 */
	@Hierarchy(HierarchyOp.GET)
	public ElementEx getParent();

	/**
	 * Returns all parents of this element.
	 * 
	 * @return all parents of this element
	 */
	@Hierarchy(HierarchyOp.GETARRAY)
	public ElementEx[] getParents();

}
