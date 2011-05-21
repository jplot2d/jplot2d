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

import java.util.Map;

import org.jplot2d.element.Element;
import org.jplot2d.util.WarningMessage;

/**
 * @author Jingjing Li
 * 
 */
public interface ElementEx extends Element {

	public ElementEx getParent();

	/**
	 * Sets the parent of this component
	 * 
	 * @param parent
	 *            the new parent
	 */
	public void setParent(ElementEx parent);

	public String getId();

	/**
	 * Element call this method to notify warning to its parent.
	 * 
	 * @param msg
	 *            the warning message
	 */
	public void warning(WarningMessage msg);

	/**
	 * Create a structural copy of this element and put them into orig2copyMap.
	 * All properties are not copied. The parent of the copy are not set by this
	 * method.
	 * 
	 * @param orig2copyMap
	 *            original element to copy map
	 * @return the structural copy of this element
	 */
	public ElementEx copyStructure(Map<ElementEx, ElementEx> orig2copyMap);

	/**
	 * Copies all properties from given src to this element. Notice properties
	 * of sub-elements are not copied over.
	 * 
	 * @param src
	 *            the src
	 */
	public void copyFrom(ElementEx src);
}
