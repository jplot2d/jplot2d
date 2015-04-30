/**
 * Copyright 2010-2015 Jingjing Li.
 * <p/>
 * This file is part of jplot2d.
 * <p/>
 * jplot2d is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or any later version.
 * <p/>
 * jplot2d is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Lesser Public License for more details.
 * <p/>
 * You should have received a copy of the GNU Lesser General Public License
 * along with jplot2d. If not, see <http://www.gnu.org/licenses/>.
 */
package org.jplot2d.element;

import org.jplot2d.annotation.Hierarchy;
import org.jplot2d.annotation.HierarchyOp;
import org.jplot2d.annotation.Property;
import org.jplot2d.annotation.PropertyGroup;

import java.awt.*;

/**
 * A component to represent an axis in a plot. It has visual properties such as line width, tick height, etc.
 * An axis also has a {@link AxisTitle title} element, which group all title related properties.
 *
 * @author Jingjing Li
 */
@PropertyGroup("Axis")
public interface Axis extends PComponent {

    @Hierarchy(HierarchyOp.GET)
    public Plot getParent();

    /**
     * Returns the title of this axis.
     *
     * @return the title of this axis
     */
    @Hierarchy(HierarchyOp.GET)
    public AxisTitle getTitle();

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
     * Orientation is a read-only property. It just show the orientation of this axis after it has been add as a X/Y
     * axis.
     *
     * @return orientation of this axis
     */
    @Property(order = 0)
    public AxisOrientation getOrientation();

    /**
     * Returns the paper length of this axis.
     *
     * @return the paper length
     */
    @Property(order = 1)
    public double getLength();

    /**
     * Return the position of the axis: NEGATIVE_SIDE, POSITIVE_SIDE.
     *
     * @return the position of the axis in the plot.
     */
    @Property(order = 2)
    public AxisPosition getPosition();

    /**
     * Set the position of the axis: NEGATIVE_SIDE, POSITIVE_SIDE.
     *
     * @param position the position of the axis in the plot.
     */
    public void setPosition(AxisPosition position);

    /**
     * Returns the axis line width is pt(1/72 inch)
     *
     * @return the axis line width
     */
    @Property(order = 3)
    public float getAxisLineWidth();

    /**
     * Sets the axis line width is pt(1/72 inch). The default line width is 1.0 pt.
     *
     * @param width the axis line width
     */
    public void setAxisLineWidth(float width);

    /**
     * Return if the grid line is displayed or not.
     *
     * @return true if the grid line is displayed
     */
    @Property(order = 4, displayName = "Grid Lines")
    public boolean isGridLines();

    /**
     * Show/hide grey lines in corresponding of major ticks of the axis.
     *
     * @param showGridLines if true show the grid lines.
     */
    public void setGridLines(boolean showGridLines);

    /**
     * Return if the minor grid line is displayed or not.
     *
     * @return true if the minor grid line is displayed
     */
    @Property(order = 5, displayName = "Minor Grid Lines")
    public boolean isMinorGridLines();

    /**
     * Show/hide grey lines in corresponding of minor ticks of the axis.
     *
     * @param showGridLines if <code>true</code> show the grid lines.
     */
    public void setMinorGridLines(boolean showGridLines);

    /**
     * Returns if the tick mark is shown or not
     *
     * @return if the tick mark is shown or not
     */
    @Property(order = 6)
    public boolean isTickVisible();

    /**
     * Sets if the tick mark is shown or not
     *
     * @param visible if <code>true</code>, shows the ticks; otherwise, hide the ticks
     */
    public void setTickVisible(boolean visible);

    /**
     * Return the side of the ticks.
     */
    @Property(order = 7)
    public AxisTickSide getTickSide();

    /**
     * Set the side of the ticks.
     */
    public void setTickSide(AxisTickSide side);

    /**
     * Returns the height of the major ticks.
     *
     * @return the height of the ticks
     */
    @Property(order = 8)
    public double getTickHeight();

    /**
     * Set the height of the major ticks.
     *
     * @param height the new height of the ticks
     */
    public void setTickHeight(double height);

    /**
     * Return the height of the minor ticks.
     *
     * @return the height of the minor ticks.
     */
    @Property(order = 9)
    public double getMinorTickHeight();

    /**
     * Set the height of the minor ticks.
     *
     * @param height the height of the ticks
     */
    public void setMinorTickHeight(double height);

    /**
     * Returns the tick line width in pt(1/72 inch)
     *
     * @return the tick line width
     */
    @Property(order = 10)
    public float getTickLineWidth();

    /**
     * Sets the line width for ticks and minor ticks. The width is in pt(1/72 inch). The default line width is 0.5 pt.
     * Setting tick line width also effect the grid line width and minor grid line width.
     * The grid line width is 1/2 of tick line width, and the minor grid line width is 1/4 of tick line width.
     *
     * @param width the tick line width
     */
    public void setTickLineWidth(float width);

    /**
     * Returns if the labels is shown or not
     *
     * @return <code>true</code>if the tick mark is shown
     */
    @Property(order = 11)
    public boolean isLabelVisible();

    /**
     * Sets if the labels is shown or not
     *
     * @param visible if <code>true</code>, shows the labels; otherwise, hide the labels
     */
    public void setLabelVisible(boolean visible);

    /**
     * Return the side of the labels
     */
    @Property(order = 12)
    public AxisLabelSide getLabelSide();

    /**
     * Set the side of the labels
     */
    public void setLabelSide(AxisLabelSide side);

    /**
     * Get the orientation of the labels.
     *
     * @return the the orientation of the labels
     */
    @Property(order = 13)
    public AxisOrientation getLabelOrientation();

    /**
     * Set the orientation of the labels.
     *
     * @param orientation HORIZONTAL/VERTICAL
     */
    public void setLabelOrientation(AxisOrientation orientation);

    /**
     * Return the color of the labels.
     *
     * @return the color of the labels
     */
    @Property(order = 14)
    public Color getLabelColor();

    /**
     * Set the color of the labels.
     *
     * @param color the color of the labels
     */
    public void setLabelColor(Color color);

}
