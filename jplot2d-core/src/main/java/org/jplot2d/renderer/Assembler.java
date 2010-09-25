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
package org.jplot2d.renderer;

import java.awt.Dimension;
import java.awt.Rectangle;

import org.jplot2d.element.Component;

/**
 * Assembler is a service in rendering. It can create CompRenderCallable to a
 * renderer, and assemble rendered component into final artifact.
 * 
 * @author Jingjing Li
 * 
 * @param <T>
 *            The assembly artifact type
 */
public abstract class Assembler<T> {

	/**
	 * Create a CompRenderCallable for the given component. The
	 * CompRenderCallable is used by component renderer.
	 * 
	 * @return a CompRenderCallable
	 */
	public abstract CompRenderCallable<T> createCompRenderCallable(
			Rectangle bounds, Component[] comps);

	/**
	 * Assemble the rendered component given in AssemblyInfo into a result.
	 * 
	 * @param size
	 *            the result size
	 * @param ainfo
	 *            the AssemblyInfo
	 * @return the assembled result
	 */
	public abstract T assembleResult(Dimension size, AssemblyInfo<T> ainfo);

}
