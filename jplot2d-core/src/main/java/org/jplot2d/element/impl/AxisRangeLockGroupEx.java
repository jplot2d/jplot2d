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
package org.jplot2d.element.impl;

import org.jplot2d.element.AxisRangeLockGroup;
import org.jplot2d.transform.NormalTransform;
import org.jplot2d.util.Range;

import javax.annotation.Nonnull;
import java.util.Map;

/**
 * @author Jingjing Li
 */
public interface AxisRangeLockGroupEx extends AxisRangeLockGroup, ElementEx, Joinable {

    AxisTransformEx getParent();

    @Nonnull
    AxisTransformEx[] getAxisTransforms();

    int indexOfAxisTransform(AxisTransformEx axisTransform);

    void addAxisTransform(AxisTransformEx axisTransform);

    void removeAxisTransform(AxisTransformEx axisTransform);

    AxisTransformEx getPrimaryAxis();

    /**
     * Force re-autorange this axis group.
     * This method is called when layer data set changes, layer attach/detach to an axis of this group, or axis type change.
     */
    void reAutoRange();

    /**
     * Calculate auto range when necessary.
     *
     * @return <code>true</code> if auto range is calculated for this group
     */
    boolean calcAutoRange();

    void zoomVirtualRange(Range range, Map<AxisTransformEx, NormalTransform> vtMap);

    /**
     * Validate axes range after axis type or axis transform type changed.
     */
    void validateAxesRange();

    /**
     * Zoom the given normalized range to entire axis. All axes in this lock group are changed.
     * If the orthogonal axes are autoRange, they need to be re-autoRange.
     *
     * @param npRange the normalized range which has been validated
     */
    void zoomNormalRange(Range npRange);
}
