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

/**
 * Warp a band of image data. The data can be in a array, a 2d array , or a nio Buffer. The ImageDataBuffer can
 * optionally take an offset, so that data in an existing array can be used even if the interesting data doesn't start
 * at array location zero. It can also associate with an ImageMaskBuffer, to determines whether a element is valid or
 * not.
 * 
 * @author Jingjing Li
 * 
 */
public abstract class ImageDataBuffer {

	protected final ImageMaskBuffer mask;

	public ImageDataBuffer(ImageMaskBuffer mask) {
		this.mask = mask;
	}

	public boolean hasMasks() {
		return mask != null;
	}

	/**
	 * A boolean that determines for the associated element whether the value is valid or not. When an element of the
	 * mask is False, the corresponding element is valid and is said to be unmasked. When an element of the mask is
	 * True, the corresponding element is said to be masked (invalid).
	 * 
	 * @param x
	 *            The X coordinate of the pixel location
	 * @param y
	 *            The Y coordinate of the pixel location
	 * @return an boolean
	 */
	public boolean isMasked(int x, int y) {
		if (mask == null) {
			return false;
		} else {
			return mask.isMasked(x, y);
		}
	}

	/**
	 * Returns the sample for the pixel located at (x,y) as a double.
	 * 
	 * @param x
	 *            The X coordinate of the pixel location
	 * @param y
	 *            The Y coordinate of the pixel location
	 * @return the sample for the specified pixel
	 */
	public abstract byte getByte(int x, int y);

	/**
	 * Returns the sample for the pixel located at (x,y) as a double.
	 * 
	 * @param x
	 *            The X coordinate of the pixel location
	 * @param y
	 *            The Y coordinate of the pixel location
	 * @return the sample for the specified pixel
	 */
	public abstract short getShort(int x, int y);

	/**
	 * Returns the sample for the pixel located at (x,y) as a double.
	 * 
	 * @param x
	 *            The X coordinate of the pixel location
	 * @param y
	 *            The Y coordinate of the pixel location
	 * @return the sample for the specified pixel
	 */
	public abstract int getInt(int x, int y);

	/**
	 * Returns the sample for the pixel located at (x,y) as a double.
	 * 
	 * @param x
	 *            The X coordinate of the pixel location
	 * @param y
	 *            The Y coordinate of the pixel location
	 * @return the sample for the specified pixel
	 */
	public abstract float getFloat(int x, int y);

	/**
	 * Returns the sample for the pixel located at (x,y) as a double.
	 * 
	 * @param x
	 *            The X coordinate of the pixel location
	 * @param y
	 *            The Y coordinate of the pixel location
	 * @return the sample for the specified pixel
	 */
	public abstract double getDouble(int x, int y);

	/**
	 * Count the number of valid values in the given rectangle for this data buffer. For double and float data, NaN,
	 * POSITIVE_INFINITY and NEGATIVE_INFINITY are considered as invalid.
	 * 
	 * @param w
	 *            the width of the given rectangle
	 * @param h
	 *            the height of the given rectangle
	 * @return the number of valid values
	 */
	public abstract double countValid(int w, int h);

	/**
	 * Calculate the min and max value in the given rectangle for this data buffer, and return them in a double array.
	 * For double and float data, NaN, POSITIVE_INFINITY and NEGATIVE_INFINITY are considered as invalid and ignored.
	 * 
	 * @param w
	 *            the width of the given rectangle
	 * @param h
	 *            the height of the given rectangle
	 * @return the min and max values
	 */
	public abstract double[] calcMinMax(int w, int h);

}
