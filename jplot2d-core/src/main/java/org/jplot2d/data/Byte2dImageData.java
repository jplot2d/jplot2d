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

import org.jplot2d.util.Range;

/**
 * Image data represented by a byte 2d array.
 * 
 * @author Jingjing Li
 */
public class Byte2dImageData extends ImageData {

	private byte[][] byte2d;

	/**
	 * Construct an ImageGraphData with the given size and data array.
	 * 
	 * @param w
	 *            The width (in pixels) of the region of image data.
	 * @param h
	 *            The height (in pixels) of the region of image data.
	 * @param dataArray
	 *            The byte array for the DataBuffer.
	 */
	public Byte2dImageData(byte[][] byte2d) {
		this(byte2d, null, null);
	}

	private Byte2dImageData(byte[][] byte2d, Range xboundary, Range yboundary) {
		this.byte2d = byte2d;
		this.xboundary = xboundary;
		this.yboundary = yboundary;

		imgHeight = byte2d.length;
		imgWidth = byte2d[0].length;
		updateRanges();
	}

	public Byte2dImageData setBoundary(Range xboundary, Range yboundary) {
		return new Byte2dImageData(byte2d, xboundary, yboundary);
	}

}
