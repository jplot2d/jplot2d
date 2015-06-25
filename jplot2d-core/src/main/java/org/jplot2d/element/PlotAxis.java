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

import javax.annotation.Nonnull;

/**
 * A component to represent an axis in a plot. Besides the normal axis, it can draw grid lines.
 */
@SuppressWarnings("unused")
@PropertyGroup("PlotAxis")
public interface PlotAxis extends Axis {

    @Hierarchy(HierarchyOp.GET)
    Plot getParent();

    /**
     * Orientation is a read-only property. It just show the orientation of this axis after it has been add as a X/Y
     * axis.
     *
     * @return orientation of this axis
     */
    @Property(order = 0)
    AxisOrientation getOrientation();

    /**
     * Return the position of the axis: NEGATIVE_SIDE, POSITIVE_SIDE.
     *
     * @return the position of the axis in the plot.
     */
    @Property(order = 1)
    @Nonnull
    public AxisPosition getPosition();

    /**
     * Set the position of the axis: NEGATIVE_SIDE, POSITIVE_SIDE. The default value is {@link AxisPosition#NEGATIVE_SIDE}.
     *
     * @param position the position of the axis in a plot.
     */
    void setPosition(@Nonnull AxisPosition position);

    /**
     * Return if the grid line is displayed or not.
     *
     * @return true if the grid line is displayed
     */
    @Property(order = 2, displayName = "Grid Lines")
    boolean isGridLines();

    /**
     * Show/hide grey lines in corresponding of major ticks of the axis.
     *
     * @param showGridLines if true show the grid lines.
     */
    void setGridLines(boolean showGridLines);

    /**
     * Return if the minor grid line is displayed or not.
     *
     * @return true if the minor grid line is displayed
     */
    @Property(order = 3, displayName = "Minor Grid Lines")
    boolean isMinorGridLines();

    /**
     * Show/hide grey lines in corresponding of minor ticks of the axis.
     *
     * @param showGridLines if <code>true</code> show the grid lines.
     */
    void setMinorGridLines(boolean showGridLines);

}
