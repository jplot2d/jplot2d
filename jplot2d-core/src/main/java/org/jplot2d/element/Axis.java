/**
 * Copyright 2010-2012 Jingjing Li.
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

import java.awt.Color;

import org.jplot2d.annotation.Hierarchy;
import org.jplot2d.annotation.HierarchyOp;
import org.jplot2d.annotation.Property;
import org.jplot2d.annotation.PropertyGroup;

/**
 * @author Jingjing Li
 * 
 */
@PropertyGroup("Axis")
public interface Axis extends PComponent {

	@Hierarchy(HierarchyOp.GET)
	public Plot getParent();

	/**
	 * Returns the tick manager of this axis.
	 * 
	 * @return the tick manager of this axis
	 */
	@Hierarchy(HierarchyOp.GET)
	public AxisTickManager getTickManager();

	@Hierarchy(HierarchyOp.JOIN)
	public void setTickManager(AxisTickManager tickManager);

	/**
	 * Orientation is a read-only property. It just show the orientation of this axis after it has
	 * been add as a X/Y axis.
	 * 
	 * @return orientation of this axis
	 */
	@Property(order = 0)
	public AxisOrientation getOrientation();

	/**
	 * Return the position of the axis: PlotConstant.LEFT or PlotConstant.RIGHT for y axis,
	 * PlotConstant.BOTTOM or PlotConstant.TOP for x axis.
	 * 
	 * @return the position of the axis in the plot.
	 */
	@Property(order = 1)
	public AxisPosition getPosition();

	/**
	 * Set the position of the axis: PlotConstant.LEFT or PlotConstant.RIGHT for y axis,
	 * PlotConstant.BOTTOM or PlotConstant.TOP for x axis. Only can be set when autoPosition is not
	 * True.
	 * 
	 * @param position
	 *            the position of the axis in the plot.
	 */
	public void setPosition(AxisPosition position);

	/**
	 * Return if the grid line is displayed or not.
	 * 
	 * @return true if the grid line is displayed
	 */
	@Property(order = 2, displayName = "Grid Lines")
	public boolean isGridLines();

	/**
	 * Sets grid lines of grey lines in corresponding of major ticks of the axis.
	 * 
	 * @param showGridLines
	 *            if true show the grid lines.
	 */
	public void setGridLines(boolean showGridLines);

	/**
	 * Return if the minor grid line is displayed or not.
	 * 
	 * @return true if the minor grid line is displayed
	 */
	@Property(order = 3, displayName = "Minor Grid Lines")
	public boolean isMinorGridLines();

	/**
	 * Sets grid lines of grey lines in corresponding of minor ticks of the axis.
	 * 
	 * @param showGridLines
	 *            if true show the grid lines.
	 */
	public void setMinorGridLines(boolean showGridLines);

	/**
	 * Returns if the tick mark is shown or not
	 * 
	 * @return if the tick mark is shown or not
	 */
	@Property(order = 5)
	public boolean isTickVisible();

	/**
	 * Sets if the tick mark is shown or not
	 * 
	 * @param visible
	 */
	public void setTickVisible(boolean visible);

	/**
	 * Return the direction of the ticks
	 */
	@Property(order = 6)
	public AxisTickSide getTickSide();

	/**
	 * Set the side position of the ticks
	 */
	public void setTickSide(AxisTickSide side);

	/**
	 * Returns the height of the major ticks of the axis
	 * 
	 * @return the height of the ticks
	 */
	@Property(order = 7)
	public double getTickHeight();

	/**
	 * Set the height of the major ticks of the axis.
	 * 
	 * @param height
	 *            the new height of the ticks
	 */
	public void setTickHeight(double height);

	/**
	 * Return the height of the minor ticks of the axis.
	 * 
	 * @return the height of the minor ticks.
	 */
	@Property(order = 8)
	public double getMinorTickHeight();

	/**
	 * Set the height of the minor ticks of the axis.
	 * 
	 * @param height
	 *            the height of the ticks
	 */
	public void setMinorTickHeight(double height);

	/**
	 * Returns if the tick labels is shown or not
	 * 
	 * @return if the tick mark is shown or not
	 */
	@Property(order = 10)
	public boolean isLabelVisible();

	/**
	 * Sets if the tick labels is shown or not
	 * 
	 * @param visible
	 */
	public void setLabelVisible(boolean visible);

	/**
	 * Return the position of the label of the ticks
	 */
	@Property(order = 11)
	public AxisLabelSide getLabelSide();

	/**
	 * Set the position of the label of the ticks respect of the axis
	 */
	public void setLabelSide(AxisLabelSide side);

	/**
	 * Get the Orientation of the labels.
	 * 
	 * @return label orientation:<br>
	 *         0 HORIZONTAL<br>
	 *         1 VERTICAL
	 */
	@Property(order = 12)
	public AxisOrientation getLabelOrientation();

	/**
	 * Set the orientation of the labels of the ticks <br>
	 * 
	 * @param orientation
	 *            HORIZONTAL/VERTICAL
	 */
	public void setLabelOrientation(AxisOrientation orientation);

	/**
	 * Return the color of the labels of the ticks
	 * 
	 * @return the color of the labels
	 */
	@Property(order = 13)
	public Color getLabelColor();

	/**
	 * Set the color of the labels of the ticks
	 * 
	 * @param color
	 *            the color of the labels
	 */
	public void setLabelColor(Color color);

	/**
	 * Returns the title of this axis.
	 * 
	 * @return the title of this axis
	 */
	@Hierarchy(HierarchyOp.GET)
	public AxisTitle getTitle();

}
