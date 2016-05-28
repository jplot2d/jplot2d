/*
 * Copyright 2010-2016 Jingjing Li.
 *
 * This file is part of jplot2d.
 *
 * jplot2d is free software:
 * you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation, either version 3 of the License, or any later version.
 *
 * jplot2d is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Lesser Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with jplot2d.
 * If not, see <http://www.gnu.org/licenses/>.
 */
package org.jplot2d.layout;

import org.jplot2d.element.impl.PlotEx;

import javax.annotation.Nonnull;
import java.awt.geom.Dimension2D;

/**
 * The interface to layout a plot. All methods of LayoutDirector will be called inside the plot
 * engine. User should never call them directly. Every plot should have its own LayoutDirector.
 * Re-using a LayoutDirector among multiple plot is not recommended. Re-using a LayoutDirector by
 * plots in different environment will fail due to thread problem.
 * <p>
 * A plot's subplots are laid out in the content area. All layers in a plot are stacked over and
 * have the same size of the content area.
 * <p>
 * The axis is a special component. Its length can be set when laid out, but its height is fixed and
 * derived from its internal status, such as tick height and labels.
 * <p>
 * The layout director has 2 working mode.
 * <ul>
 * <li>For root plot, the size is known, and the content size will be decided by layout director</li>
 * <li>For subplot, the content constraint has been set by its container's layout director. The
 * content size will be the constraint, and size is calculated by layout director</li>
 * </ul>
 *
 * @author Jingjing Li
 */
public interface LayoutDirector {

    /**
     * Returns the constraint for the given plot.
     *
     * @param plot The plot
     * @return The constraint
     */
    Object getConstraint(PlotEx plot);

    /**
     * Sets the constraint for the given plot.
     *
     * @param plot       the plot
     * @param constraint the constraint to be applied
     */
    void setConstraint(PlotEx plot, Object constraint);

    /**
     * Removes the given child from this layout.
     *
     * @param plot the plot
     */
    void remove(PlotEx plot);

    /**
     * Invalidates the layout, indicating that if the layout manager has cached information it
     * should be discarded.
     *
     * @param plot the plot has been invalidate.
     */
    void invalidateLayout(PlotEx plot);

    /**
     * Layout the plot. Normally the plot contents size is calculated by subtracting margin from
     * plot size. If the plot has contents size constraint, the plot size is calculated by adding
     * margin to plot content size.
     */
    void layout(PlotEx plot);

    /**
     * Returns the preferred content size of the given plot.
     *
     * @param plot The plot
     * @return The preferred size
     */
    @Nonnull
    Dimension2D getPreferredContentSize(PlotEx plot);

    /**
     * Returns the preferred size of the given plot.
     *
     * @param plot The plot
     * @return The preferred size
     */
    @Nonnull
    Dimension2D getPreferredSize(PlotEx plot);

}
