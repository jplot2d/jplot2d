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
 * An image in which the value of each pixel is a single sample. It carries only intensity information.
 * 
 * @author Jingjing Li
 */
public class SingleBandImageData extends ImageData {

	private final ImageDataBuffer dataBuffer;

	public SingleBandImageData(ImageDataBuffer dataBuffer, int w, int h) {
		super(w, h, null, null);
		this.dataBuffer = dataBuffer;
	}

	protected SingleBandImageData(ImageDataBuffer dataBuffer, int w, int h, Range xboundary, Range yboundary) {
		super(w, h, xboundary, yboundary);
		this.dataBuffer = dataBuffer;
	}

	public ImageDataBuffer getDataBuffer() {
		return dataBuffer;
	}

	public SingleBandImageData setBoundary(Range xboundary, Range yboundary) {
		return new SingleBandImageData(getDataBuffer(), getWidth(), getHeight(), xboundary, yboundary);
	}

}
