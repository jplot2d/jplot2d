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
package org.jplot2d.interaction;

import java.awt.Graphics2D;
import java.util.EventObject;

/**
 * @author Jingjing Li
 * 
 */
public class PlotPaintEvent extends EventObject {

	private static final long serialVersionUID = 7168676901312678175L;

	private final Graphics2D g;

	public PlotPaintEvent(Object source, Graphics2D graphics) {
		super(source);
		this.g = graphics;
	}

	public Graphics2D getGraphics() {
		return g;
	}

}
