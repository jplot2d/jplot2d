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

import org.jplot2d.util.Range2D;

/**
 * @author Jingjing Li
 * 
 */
public interface Axis extends Component {

	/**
	 * Orientation is not a set-able property. It just show the orientation of
	 * this axis after it has been add as a X/Y axis.
	 * 
	 * @return orientation of this axis
	 */
	public AxisOrientation getOrientation();

	/**
	 * Return the position of the axis: PlotConstant.LEFT or PlotConstant.RIGHT
	 * for y axis, PlotConstant.BOTTOM or PlotConstant.TOP for x axis.
	 * 
	 * @return the position of the axis in the plot.
	 */
	public AxisPosition getPosition();

	/**
	 * Set the position of the axis: PlotConstant.LEFT or PlotConstant.RIGHT for
	 * y axis, PlotConstant.BOTTOM or PlotConstant.TOP for x axis. Only can be
	 * set when autoPosition is not True.
	 * 
	 * @param position
	 *            the position of the axis in the plot.
	 */
	public void setPosition(AxisPosition position);

	/**
	 * Returns the range of this axis.
	 * 
	 * @return the range of this axis.
	 */
	public Range2D getRange();

	/**
	 * Set the value range of the axis.
	 * 
	 * @param range
	 *            the new range of the axis
	 */
	public void setRange(Range2D range);

	/**
	 * Set the value range of the axis.
	 * 
	 * @param low
	 *            the low value of new range of the axis
	 * @param high
	 *            the high value of new range of the axis
	 */
	public void setRange(double low, double high);

	/**
	 * Returns the tick of this axis.
	 * 
	 * @return the tick of this axis
	 */
	public AxisTick getTick();

	/**
	 * Returns the title of this axis.
	 * 
	 * @return the title of this axis
	 */
	public TextComponent getTitle();

}
