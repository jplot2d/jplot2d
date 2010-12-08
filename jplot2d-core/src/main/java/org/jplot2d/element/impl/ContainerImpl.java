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
import java.awt.geom.Rectangle2D;
import java.util.Map;

import org.jplot2d.element.PhysicalTransform;

public class ContainerImpl extends ComponentImpl implements ContainerEx {

	public PhysicalTransform getPhysicalTransform() {
		throw new UnsupportedOperationException();
	}

	public final void setPhysicalSize(Dimension2D physicalSize) {
		this.setPhysicalSize(physicalSize.getWidth(), physicalSize.getHeight());
	}

	public void setPhysicalSize(double physicalWidth, double physicalHeight) {
		if (physicalWidth < 0 || physicalHeight < 0) {
			throw new IllegalArgumentException(
					"physical size must be positive " + physicalWidth + "x"
							+ physicalHeight);
		}

		if (this.physicalWidth != physicalWidth
				|| this.physicalHeight != physicalHeight) {
			invalidate();
			redraw();
			this.physicalWidth = physicalWidth;
			this.physicalHeight = physicalHeight;
		}
	}

	public ContainerEx deepCopy(Map<ElementEx, ElementEx> orig2copyMap) {
		ContainerImpl result = new ContainerImpl();
		result.copyFrom(this);
		if (orig2copyMap != null) {
			orig2copyMap.put(this, result);
		}
		return result;
	}
}
