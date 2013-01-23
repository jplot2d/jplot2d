/**
 * Copyright 2010-2013 Jingjing Li.
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

import java.awt.image.BufferedImage;

import org.jplot2d.util.Range;

/**
 * Immutable. This class keep (x,y) data pairs and compute data feature such as max/min, NaN indexes.
 * 
 * @author Jingjing Li
 */
public class BufferedImageData extends ImageData {

	private BufferedImage image;

	/**
	 * Construct an ImageGraphData with the given BufferedImage. The given BufferedImage may be modified during image
	 * processing.
	 * 
	 * @param img
	 *            the BufferedImage
	 */
	public BufferedImageData(BufferedImage img) {
		this(img, null, null);
	}

	private BufferedImageData(BufferedImage img, Range xboundary, Range yboundary) {
		this.image = img;
		this.xboundary = xboundary;
		this.yboundary = yboundary;

		imgWidth = img.getWidth();
		imgHeight = img.getHeight();
		updateRanges();
	}

	public BufferedImageData setBoundary(Range xboundary, Range yboundary) {
		return new BufferedImageData(image, xboundary, yboundary);
	}

}
