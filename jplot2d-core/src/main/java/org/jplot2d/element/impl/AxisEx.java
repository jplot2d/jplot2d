/**
 * Copyright 2010, 2011 Jingjing Li.
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

import java.awt.Font;

import org.jplot2d.element.Axis;
import org.jplot2d.element.AxisOrientation;
import org.jplot2d.transform.PaperTransform;

public interface AxisEx extends Axis, ComponentEx {

	public PlotEx getParent();

	public AxisTickManagerEx getTickManager();

	/**
	 * Set shrunk font for displaying by tick manager.
	 * 
	 * @param font
	 *            the shrunk font
	 */
	public void setActualFont(Font font);

	/**
	 * Returns the paper transform of this component.
	 * 
	 * @return
	 */
	public PaperTransform getPaperTransform();

	/**
	 * Moves this plot component to a new location. The origin of the new location is specified by
	 * point <code>p</code>. Point2D <code>p</code> is given in the parent's paper coordinate space.
	 * 
	 * @param p
	 *            the point defining the origin of the new location, given in the coordinate space
	 *            of this component's parent
	 */
	public void setLocation(double locX, double locY);

	/**
	 * Called by {@link PlotEx#addXAxis(Axis)} or {@link PlotEx#addYAxis(Axis)} to set the
	 * orientation of this axis.
	 * 
	 * @param orientation
	 *            the orientation
	 */
	public void setOrientation(AxisOrientation orientation);

	/**
	 * Returns the length of this axis.
	 * 
	 * @return the length
	 */
	public double getLength();

	/**
	 * Set the length for this axis.
	 * 
	 * @param length
	 */
	public void setLength(double length);

	public double getThickness();

	public double getAsc();

	public double getDesc();

	public void invalidateThickness();

	/**
	 * calculate asc and desc of this axis.
	 */
	public void calcThickness();

}
