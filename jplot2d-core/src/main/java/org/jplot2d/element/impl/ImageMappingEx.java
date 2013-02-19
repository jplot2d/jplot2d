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

import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;

import org.jplot2d.element.ImageMapping;

/**
 * @author Jingjing Li
 * 
 */
public interface ImageMappingEx extends ImageMapping, ElementEx {

	public ImageGraphEx getParent();

	public void addImageGraph(ImageGraphEx graph);

	public void removeImageGraph(ImageGraphEx graph);

	public void calcLimits();

	public double[] getLimits();

	/**
	 * Returns the number of significant bits that the input data should match. When apply the
	 * limits, the generated unsigned short array should match the bits number.
	 * 
	 * @return the number of significant bits
	 */
	public int getInputDataBits();

	/**
	 * Apply intensity transform and bias/gain
	 * 
	 * @param raster
	 */
	public void processImage(WritableRaster raster);

	/**
	 * Apply the color LUT to the given raster. If the given raster only has a band, it will be
	 * duplicated to meet the output band number.
	 * 
	 * @param raster
	 * @return
	 */
	public BufferedImage colorImage(WritableRaster raster);

}
