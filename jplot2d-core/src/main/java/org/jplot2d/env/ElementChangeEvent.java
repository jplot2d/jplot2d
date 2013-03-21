/**
 * Copyright 2010-2012 Jingjing Li.
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
package org.jplot2d.env;

import java.util.EventObject;

import org.jplot2d.element.Element;

/**
 * Encapsulates information describing changes to a hierarchy structure or properties of an element.
 * 
 * @author Jingjing Li
 * 
 */
public class ElementChangeEvent extends EventObject {

	private static final long serialVersionUID = 1L;

	private Element element;

	/**
	 * create an event when hierarchy structure or properties changed.
	 * 
	 * @param element
	 *            the element which added, removing, or properties changed
	 */
	public ElementChangeEvent(Environment source, Element element) {
		super(source);
		this.element = element;
	}

	/**
	 * Returns the element which added, removing, or properties changed
	 * 
	 * @return the element
	 */
	public Element getElement() {
		return element;
	}

}
