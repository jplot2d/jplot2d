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

import org.jplot2d.element.ImageMapping;

/**
 * @author Jingjing Li
 * 
 */
public interface ImageMappingEx extends ImageMapping, ElementEx {

	public ImageGraphEx getParent();

	public ImageGraphEx[] getGraphs();

	public void addImageGraph(ImageGraphEx graph);

	public void removeImageGraph(ImageGraphEx graph);

	/**
	 * Called by ImageGraphEx when its data changed, to notify the limits need to be recalculated.
	 */
	public void recalcLimits();

	/**
	 * Called by PlotEx.commit() to calculate limits if needed
	 */
	public void calcLimits();

	public double[] getLimits();

	/**
	 * Returns the number of significant bits that the ILUT index should match. When applying the limits, the generated
	 * values should match the ILUT indexes.
	 * 
	 * @return the number of significant bits
	 */
	public int getILUTInputBits();

	/**
	 * Returns the number of significant bits that the ILUT output range. When creating image, the color model bit
	 * should match this value.
	 * 
	 * @return the number of significant bits
	 */
	public int getILUTOutputBits();

	/**
	 * Returns the ILUT for processing data, for applying intensity transform and bias/gain.
	 * 
	 * @return
	 */
	public short[] getILUT();

}
