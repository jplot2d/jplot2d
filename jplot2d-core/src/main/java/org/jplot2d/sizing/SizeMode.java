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
package org.jplot2d.sizing;

import java.awt.geom.Dimension2D;

import org.jplot2d.element.impl.PlotEx;

/**
 * Defines how the plot size is decided. The plot container size is a common input for all size
 * mode. If a plot's size mode is <code>null</code>, the plot container size is useless.
 * 
 * @author Jingjing Li
 */
public interface SizeMode {

	/**
	 * For Internal use only. User should never call this method.
	 * 
	 * @param plot
	 */
	public void setPlot(PlotEx plot);

	/**
	 * Returns <code>true</code> if this size mode has auto pack feature.
	 * 
	 * @return <code>true</code> if this size mode has auto pack feature.
	 */
	public boolean isAutoPack();

	/**
	 * Update the internal status of this size mode.
	 */
	public void update();

	/**
	 * Returns the plot size derived from this size mode .
	 * 
	 * @return the plot size
	 */
	public Dimension2D getSize();

	/**
	 * Returns the scale derived from this size mode
	 * 
	 * @return the scale
	 */
	public double getScale();

}
