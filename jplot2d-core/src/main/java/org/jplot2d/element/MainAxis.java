/**
 * Copyright 2010 Jingjing Li.
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

/**
 * Main axis associate with an axis transform. axis transform defines the axis
 * value range.
 * 
 * @author Jingjing Li
 * 
 */
public interface MainAxis extends Axis {

	public AxisGroup getGroup();

	/**
	 * Sets axis group by adapting all properties
	 * 
	 * @throws WarningException
	 */
	public void setGroup(AxisGroup lockGroup);

	/**
	 * Returns all layers attaching to this axis.
	 * 
	 * @return
	 */
	public Layer[] getLayers();

	/**
	 * Returns all auxiliary axes attaching to this axis.
	 * 
	 * @return
	 */
	public AuxAxis[] getAuxAxes();

}
