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
import org.jplot2d.util.DoubleDimension2D;

/**
 * The plot paper size is fixed. Changing the container size will change the scale, while keep the
 * width/height ratio of plot.
 * 
 * @author Jingjing Li
 * 
 */
public class FixedSizeMode extends SizeMode {

	private static final Dimension2D DEFAULT_SIZE = new DoubleDimension2D(640, 480);

	private final double width, height;

	/**
	 * The plot paper size is set to default size, 640x480.
	 */
	public FixedSizeMode() {
		this(DEFAULT_SIZE);
	}

	public FixedSizeMode(Dimension2D size) {
		super(false);
		this.width = size.getWidth();
		this.height = size.getHeight();
	}

	public FixedSizeMode(double width, double height) {
		super(false);
		this.width = width;
		this.height = height;
	}

	public Result update(PlotEx plot) {
		Dimension2D containerSize = plot.getContainerSize();
		/*
		 * Calculate scale based on container size and paper size.
		 */
		double scaleX = containerSize.getWidth() / width;
		double scaleY = containerSize.getHeight() / height;
		double scale = (scaleX < scaleY) ? scaleX : scaleY;

		return new Result(width, height, scale);
	}

	public String toString() {
		return "Fixed size " + width + "x" + height;
	}

}
