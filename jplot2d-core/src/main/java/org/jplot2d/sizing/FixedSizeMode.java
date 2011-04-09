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
package org.jplot2d.sizing;

import java.awt.geom.Dimension2D;

/**
 * The plot paper size is fixed. Changing the container size will change the
 * scale. Changing the scale will set container size to null.
 * 
 * @author Jingjing Li
 * 
 */
public class FixedSizeMode extends AbstractSizeMode {

	public FixedSizeMode(Dimension2D size) {
		if (size == null) {
			this.autoPack = true;
		} else {
			this.autoPack = false;
			this.width = size.getWidth();
			this.height = size.getHeight();
		}
	}

	public FixedSizeMode(double width, double height) {
		this.autoPack = false;
		this.width = width;
		this.height = height;
	}

	/**
	 * The plot paper size is auto packed according to its preferred content
	 * size.
	 */
	public FixedSizeMode() {
		this.autoPack = true;
	}

	public void update() {

		Dimension2D containerSize = plot.getContainerSize();
		/*
		 * Calculate scale based on container size and physical size.
		 */
		double scaleX = containerSize.getWidth() / width;
		double scaleY = containerSize.getHeight() / height;
		double scale = (scaleX < scaleY) ? scaleX : scaleY;

		if (this.scale != scale) {
			this.scale = scale;
			updatePxf();
		}
	}

}
