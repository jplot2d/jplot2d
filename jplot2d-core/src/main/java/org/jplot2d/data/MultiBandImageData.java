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
 * An image data which the value of each pixel is a component samples.
 * 
 * @author Jingjing Li
 */
public class MultiBandImageData extends ImageData {

	private final ImageDataBuffer[] dataBuffer;

	public MultiBandImageData(ImageDataBuffer[] dataBuffer, int w, int h) {
		this(dataBuffer, w, h, new ImageCoordinateReference(), null, null);
	}

	public MultiBandImageData(ImageDataBuffer[] dataBuffer, int w, int h, ImageCoordinateReference cr) {
		this(dataBuffer, w, h, cr, null, null);
	}

	protected MultiBandImageData(ImageDataBuffer[] dataBuffer, int w, int h, ImageCoordinateReference cr,
			Range xboundary, Range yboundary) {
		super(w, h, cr, xboundary, yboundary);
		this.dataBuffer = dataBuffer;
	}

	public ImageDataBuffer[] getDataBuffer() {
		return dataBuffer;
	}

	public MultiBandImageData applyCoordinateReference(ImageCoordinateReference cr) {
		return new MultiBandImageData(getDataBuffer(), getWidth(), getHeight(), cr, getXRange(), getYRange());
	}

	public MultiBandImageData applyBoundary(Range xboundary, Range yboundary) {
		return new MultiBandImageData(getDataBuffer(), getWidth(), getHeight(), getCoordinateReference(), xboundary,
				yboundary);
	}

}
