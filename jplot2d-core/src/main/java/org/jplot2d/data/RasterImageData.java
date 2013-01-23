/**
 * Copyright 2010-2012 Jingjing Li.
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
package org.jplot2d.data;

import java.awt.image.Raster;

import org.jplot2d.util.Range;

/**
 * Immutable. This class keep (x,y) data pairs and compute data feature such as max/min, NaN indexes.
 * 
 * @author Jingjing Li
 */
public class RasterImageData extends ImageData {

	private Raster raster;

	/**
	 * Construct an ImageGraphData with the given Raster.
	 * 
	 * @param raster
	 *            the Raster
	 */
	public RasterImageData(Raster raster) {
		this(raster, null, null);
	}

	private RasterImageData(Raster raster, Range xboundary, Range yboundary) {
		this.raster = raster;
		this.xboundary = xboundary;
		this.yboundary = yboundary;

		imgWidth = raster.getWidth();
		imgHeight = raster.getHeight();
		updateRanges();
	}

	public RasterImageData setBoundary(Range xboundary, Range yboundary) {
		return new RasterImageData(raster, xboundary, yboundary);
	}

}
