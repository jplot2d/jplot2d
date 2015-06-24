/**
 * Copyright 2010, 2011 Jingjing Li.
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
package org.jplot2d.element.impl;

import org.jplot2d.element.*;

import javax.annotation.Nonnull;
import java.awt.*;

public interface AxisEx extends Axis, ComponentEx {

    ContainerEx getParent();

    /**
     * Returns the short id of this axis. The short id is composed of series of ids concatenated with dots.
     * The 1st id is the id of this element, the 2nd id is the id of the parent of this element, etc,
     * until but not include the root plot.
     *
     * @return the short id of this axis.
     */
    String getShortId();

    AxisTickManagerEx getTickManager();

    /**
     * Set shrunk font for displaying by tick manager.
     *
     * @param font the shrunk font
     */
    void setActualFont(@Nonnull Font font);

    /**
     * Moves this plot component to a new location. The origin of the new location is given in the parent's paper coordinate space.
     *
     * @param locX the x value of the origin of the new location
     * @param locY the y value of the origin of the new location
     */
    void setLocation(double locX, double locY);

    /**
     * Called by {@link Plot#addXAxis(PlotAxis)} or {@link Plot#addYAxis(PlotAxis)} to set the orientation of this axis.
     *
     * @param orientation the orientation
     */
    void setOrientation(AxisOrientation orientation);

    /**
     * Set the length of this axis.
     *
     * @param length the length of this axis
     */
    void setLength(double length);

    double getThickness();

    double getAsc();

    double getDesc();

    void invalidateThickness();

    /**
     * calculate asc and desc of this axis.
     */
    void calcThickness();

}
