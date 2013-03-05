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
import org.jplot2d.image.ColorMap;
import org.jplot2d.image.IntensityTransform;
import org.jplot2d.image.LimitsAlgorithm;

/**
 * @author Jingjing Li
 * 
 */
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
	public LimitsAlgorithm getLimitsAlgorithm();

	/**
	 * Sets the LimitsAlgorithm
	 * 
	 * @param algo
	 *            the LimitsAlgorithm
	 */
	public void setLimitsAlgorithm(LimitsAlgorithm algo);

	public IntensityTransform getIntensityTransform();

	public void setIntensityTransform(IntensityTransform it);

	public double getBias();

	public void setBias(double bias);

	public double getGain();

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
