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

import org.jplot2d.element.impl.PlotEx;

/**
 * The plot paper size is auto packed to its contents. The plot content size is assigned by
 * preferred content size. Changing the container size will change the scale, while keep the
 * width/height ratio of plot.
 * 
 * @author Jingjing Li
 * 
 */
public class AutoPackSizeMode extends SizeMode {

	/**
	 * The plot paper size is auto packed according to its preferred content size.
	 */
	public AutoPackSizeMode() {
		super(true);
	}

	public Result update(PlotEx plot) {

		Dimension2D size = plot.getSize();
		Dimension2D containerSize = plot.getContainerSize();
		/*
		 * Calculate scale based on container size and physical size.
		 */
		double scaleX = containerSize.getWidth() / size.getWidth();
		double scaleY = containerSize.getHeight() / size.getHeight();
		double scale = (scaleX < scaleY) ? scaleX : scaleY;

		return new Result(size.getWidth(), size.getHeight(), scale);

	}

	public String toString() {
		return "Auto pack to contents";
	}

}
