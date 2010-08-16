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

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.concurrent.Callable;

import org.jplot2d.element.Component;

public class CompRenderCallable<T> implements Callable<T> {

	private Component comp;

	private Graphics2D g;

	private T result;

	private Rectangle bounds;

	public CompRenderCallable(Component comp, Graphics2D g, T result,
			Rectangle bounds) {
		this.comp = comp;
		this.g = g;
		this.result = result;
		this.bounds = bounds;
	}

	public T call() throws Exception {
		comp.draw(g, false);
		g.dispose();
		return result;
	}

	/**
	 * @return
	 */
	public Rectangle getBounds() {
		return bounds;
	}

}