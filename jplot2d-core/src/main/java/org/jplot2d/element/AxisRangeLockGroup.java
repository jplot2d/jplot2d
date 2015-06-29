/*
 * Copyright 2010-2015 Jingjing Li.
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
package org.jplot2d.element;

import org.jplot2d.annotation.Hierarchy;
import org.jplot2d.annotation.HierarchyOp;
import org.jplot2d.annotation.Property;
import org.jplot2d.annotation.PropertyGroup;

import javax.annotation.Nonnull;

/**
 * A group of {@link AxisTransform} who are pinned together.
 * When one AxisTransform changes its range, all AxisTransform will follow the change together.
 *
 * @author Jingjing Li
 */
@PropertyGroup("Axis Range Lock Group")
public interface AxisRangeLockGroup extends Element {

    /**
     * Return the autorange status of this axis group.
     *
     * @return true if the axis is in autorange status
     */
    @Property(order = 0, styleable = false)
    boolean isAutoRange();

    /**
     * If true, the range of the axis group is calculate automatically to display all valid values of all Layers.
     *
     * @param autoRange the flag.
     */
    void setAutoRange(boolean autoRange);

    /**
     * Returns <code>true</code> if this AxisRangeLockGroup can be zoomed by {@link Plot#zoomXRange(double, double)} or
     * {@link Plot#zoomYRange(double, double)}. By default an AxisRangeLockGroup is zoomable.
     *
     * @return if this AxisRangeLockGroup can be zoomed
     */
    @Property(order = 1, styleable = false)
    boolean isZoomable();

    /**
     * Sets if this AxisRangeLockGroup can be zoomed by {@link Plot#zoomXRange(double, double)} or
     * {@link Plot#zoomYRange(double, double)}.
     *
     * @param zoomable the flag
     */
    void setZoomable(boolean zoomable);

    /**
     * Zoom the given normalized range to entire axis. All axes in this lock group are changed.
     * If the orthogonal axes are autoRange, they need to be re-autoRange.
     *
     * @param start the normalized start
     * @param end   the normalized end
     */
    void zoomRange(double start, double end);

    /**
     * Returns all axes belongs to this group.
     *
     * @return all axes belongs to this group
     */
    @Hierarchy(HierarchyOp.GETARRAY)
    @Nonnull
    AxisTransform[] getAxisTransforms();

}
