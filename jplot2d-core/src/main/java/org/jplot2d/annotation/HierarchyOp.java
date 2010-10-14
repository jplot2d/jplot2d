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
package org.jplot2d.annotation;

/**
 * Defines hierarchy operation types.
 * 
 * @author Jingjing Li
 */
public enum HierarchyOp {
	/**
	 * The method should returns an element.
	 */
	GET,
	/**
	 * The method should returns an array of elements. (1:n parent:children)
	 */
	GETARRAY,
	/**
	 * The method should add an element as its child. (1:n parent:children)
	 */
	ADD,
	/**
	 * The method should remove an element from its children (1:n
	 * parent:children)
	 */
	REMOVE,
	/**
	 * The method should add this element as a parent of the destination
	 * element. (n:1 parent:children)
	 */
	JOIN,
	/**
	 * The method should set an element as its reference. (1:n reffrom:refto)
	 */
	REF,
	/**
	 * The method should set 2 elements as its reference. (2x 1:n reffrom:refto)
	 */
	REF2
}
