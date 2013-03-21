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
package org.jplot2d.element;

import org.jplot2d.annotation.Hierarchy;
import org.jplot2d.annotation.HierarchyOp;
import org.jplot2d.annotation.Property;
import org.jplot2d.annotation.PropertyGroup;
import org.jplot2d.image.ColorMap;
import org.jplot2d.image.IntensityTransform;
import org.jplot2d.image.LimitsAlgorithm;

/**
 * This class defines how to transform a number array to a pseudo-color or grayscale image. The transformation take
 * these steps:
 * <ol>
 * <li>Apply limit algorithm</li>
 * <li>Apply intensity transform</li>
 * <li>Apply bias/gain. method of Schlick{@link <a href=http://dept-info.labri.fr/~schlick/DOC/gem2.ps.gz>(C. Schlick,
 * Fast Alternatives to Perlin's Bias and Gain Functions)</a>}</li>
 * <li>zoom to correct size for display</li>
 * <li>Apply color map to produce a pseudo-color image</li>
 * </ol>
 * 
 * @author Jingjing Li
 * 
 */
@PropertyGroup("Image Mapping")
public interface ImageMapping extends Element {

	/**
	 * Returns all ImageGraph whose mapping are controlled by this ImageMapping.
	 * 
	 * @return all ImageGraph whose mapping are controlled by this ImageMapping
	 */
	@Hierarchy(HierarchyOp.GETARRAY)
	public ImageGraph[] getGraphs();

	/**
	 * Returns the LimitsAlgorithm
	 * 
	 * @return the LimitsAlgorithm
	 */
	@Property(order = 0)
	public LimitsAlgorithm getLimitsAlgorithm();

	/**
	 * Sets the LimitsAlgorithm
	 * 
	 * @param algo
	 *            the LimitsAlgorithm
	 */
	public void setLimitsAlgorithm(LimitsAlgorithm algo);

	@Property(order = 1)
	public IntensityTransform getIntensityTransform();

	public void setIntensityTransform(IntensityTransform it);

	/**
	 * Returns the bias value. The default value is 0.5.
	 * 
	 * @return the bias value
	 */
	@Property(order = 2)
	public double getBias();

	/**
	 * Sets the bias value. The default value is 0.5.
	 * 
	 * @param bias
	 *            the bias value
	 * @see <a href=http://dept-info.labri.fr/~schlick/DOC/gem2.ps.gz>C. Schlick, Fast Alternatives to Perlin's Bias and
	 *      Gain Functions</a>
	 */
	public void setBias(double bias);

	/**
	 * Returns the gain value. The default value is 0.5.
	 * 
	 * @return the gain value
	 */
	@Property(order = 3)
	public double getGain();

	/**
	 * Sets the gain value. The default value is 0.5.
	 * 
	 * @param gain
	 *            the gain value
	 * @see <a href=http://dept-info.labri.fr/~schlick/DOC/gem2.ps.gz>C. Schlick, Fast Alternatives to Perlin's Bias and
	 *      Gain Functions</a>
	 */
	public void setGain(double gain);

	/**
	 * Returns the lookup table for displaying the intensity raster.
	 * 
	 * @return the lookup table for displaying the intensity raster
	 */
	public ColorMap getColorMap();

	/**
	 * Sets a ColorMap to lookup the intensity raster for display.
	 * 
	 * @param colorMap
	 *            the lookup table for displaying the intensity raster
	 */
	public void setColorMap(ColorMap colorMap);

}
