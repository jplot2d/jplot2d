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

/**
 * This class defines LimitsAlgorithm and ImageBandTransform for R,G,B bands.
 * 
 * @author Jingjing Li
 * 
 */
public interface RGBImageMapping extends Element {

	/**
	 * Returns all ImageGraph whose mapping are controlled by this ImageMapping.
	 * 
	 * @return all ImageGraph whose mapping are controlled by this ImageMapping
	 */
	@Hierarchy(HierarchyOp.GETARRAY)
	public RGBImageGraph[] getGraphs();

	/**
	 * Returns the BandTransform of R band.
	 * 
	 * @return the BandTransform of R band
	 */
	@Hierarchy(HierarchyOp.GET)
	public ImageBandTransform getRedTransform();

	/**
	 * Returns the BandTransform of G band.
	 * 
	 * @return the BandTransform of G band
	 */
	@Hierarchy(HierarchyOp.GET)
	public ImageBandTransform getGreenTransform();

	/**
	 * Returns the BandTransform of B band.
	 * 
	 * @return the BandTransform of B band
	 */
	@Hierarchy(HierarchyOp.GET)
	public ImageBandTransform getBlueTransform();

}
