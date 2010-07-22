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
import java.util.Map;

import org.jplot2d.element.Component;
import org.jplot2d.element.Plot;

/**
 * @author Jingjing Li
 * 
 */
public abstract class SyncRenderer<T> extends Renderer<T> {

	private T result;

	@Override
	public final void render(Plot plot, Map<Component, Boolean> ccms) {
		AssemblyInfo<T> ainfo = runCompRender(ccms);
		Dimension size = plot.getBounds().getBounds().getSize();
		result = assambleResult(ainfo, size);
	}

	public T getResult() {
		return result;
	}

}
