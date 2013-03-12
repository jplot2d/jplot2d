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
package org.jplot2d.element.impl;

import java.awt.Dimension;

import org.jplot2d.data.ImageDataBuffer;
import org.jplot2d.element.ImageBandTransform;

/**
 * @author Jingjing Li
 * 
 */
public interface ImageBandTransformEx extends ImageBandTransform, ElementEx {

	public RGBImageMappingEx getParent();

	/**
	 * Returns the number of significant bits that the ILUT index should match. When applying the limits, the generated
	 * values should match the ILUT indexes.
	 * 
	 * @return the number of significant bits
	 */
	public int getILUTInputBits();

	/**
	 * Returns a ILUT for processing data, for applying intensity transform and bias/gain. The input bits is
	 * getInputDataBits(). The output bits is 256. If there is no intensity transform or bias/gain tweak, returns
	 * <code>null</code>.
	 * 
	 * @return
	 */
	public byte[] getILUT();

	/**
	 * Called by RGBImageGraphEx when its data changed, to notify the limits of this band need to be recalculated.
	 */
	public void recalcLimits();

	/**
	 * Calculate limits if needed
	 */
	public void calcLimits(ImageDataBuffer[] dataBuffers, Dimension[] sizeArray);

	public double[] getLimits();

}
