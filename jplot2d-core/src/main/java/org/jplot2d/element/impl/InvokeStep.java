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
package org.jplot2d.element.impl;

import java.lang.reflect.Method;

/**
 * @author Jingjing Li
 * 
 */
public class InvokeStep {

	private final Method method;
	private final int index;

	/**
	 * Create a invoke step with a method
	 * 
	 * @param method
	 *            the method to invoke
	 */
	public InvokeStep(Method method) {
		this.method = method;
		index = -1;
	}

	/**
	 * Create a invoke step with a method and an int argument
	 * 
	 * @param method
	 *            the method to invoke
	 * @param index
	 *            the argument
	 */
	public InvokeStep(Method method, int index) {
		this.method = method;
		this.index = index;
	}

	public Method getMethod() {
		return method;
	}

	public int getIndex() {
		return index;
	}
}
