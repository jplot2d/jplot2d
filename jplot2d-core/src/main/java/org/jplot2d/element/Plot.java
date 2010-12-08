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

import java.awt.Dimension;
import java.awt.geom.Dimension2D;

/**
 * A Plot contains titles and subplots.
 * 
 * @author Jingjing Li
 * 
 */
public interface Plot extends Subplot {

	/**
	 * @return
	 */
	public PlotSizeMode getSizeMode();

	public void setSizeMode(PlotSizeMode smode);

	/**
	 * Returns the container size.
	 * 
	 * @return the container size
	 */
	public Dimension getContainerSize();

	/**
	 * Sets the container size.
	 * 
	 * @param size
	 */
	public void setContainerSize(Dimension size);

	public Dimension2D getTargetPaperSize();

	public void setTargetPaperSize(Dimension2D paperSize);

}
