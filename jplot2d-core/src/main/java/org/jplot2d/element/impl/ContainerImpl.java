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

import java.awt.geom.Dimension2D;

import org.jplot2d.element.PhysicalTransform;

public class ContainerImpl extends ComponentImpl implements ContainerEx {

	public PhysicalTransform getPhysicalTransform() {
		throw new UnsupportedOperationException();
	}

	public final void setSize(Dimension2D size) {
		this.setSize(size.getWidth(), size.getHeight());
	}

	public void setSize(double width, double height) {
		if (width < 0 || height < 0) {
			throw new IllegalArgumentException("paper size must be positive "
					+ width + "x" + height);
		}

		if (this.width != width || this.height != height) {
			invalidate();
			redraw();
			this.width = width;
			this.height = height;
		}
	}

}
