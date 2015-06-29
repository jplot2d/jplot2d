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
import org.jplot2d.element.AxisTransform;
import org.jplot2d.transform.NormalTransform;
import org.jplot2d.util.Range;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @author Jingjing Li
 */
@SuppressWarnings("unused")
public interface AxisTransformEx extends AxisTransform, ElementEx, Joinable {

    @Nullable
    AxisTickManagerEx getParent();

    @Nonnull
    AxisTickManagerEx[] getTickManagers();

    @Nullable
    AxisRangeLockGroupEx getLockGroup();

    void setLockGroup(@Nullable AxisRangeLockGroup group);

    int indexOfTickManager(AxisTickManagerEx tickManager);

    /**
     * Sets the normal transform of this axis.
     *
     * @param ntf the normal transform
     */
    void setNormalTransform(@Nonnull NormalTransform ntf);

    void addTickManager(AxisTickManagerEx tickManager);

    void removeTickManager(AxisTickManagerEx tickManager);

    @Nonnull
    LayerEx[] getLayers();

    /**
     * Called when a layer attach to this viewport axis
     *
     * @param layer the layer
     */
    void addLayer(LayerEx layer);

    /**
     * Called when a layer detach from this viewport axis
     *
     * @param layer the layer
     */
    void removeLayer(LayerEx layer);

    void linkLayer(LayerEx layer);

    /**
     * Expand the given range to major ticks of its primary tick manager.
     *
     * @param ur the range to be expanded
     * @return the expanded range
     */
    Range expandRangeToTick(Range ur);

}
