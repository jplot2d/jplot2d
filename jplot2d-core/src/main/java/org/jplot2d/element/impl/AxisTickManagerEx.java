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

import org.jplot2d.element.AxisTickManager;
import org.jplot2d.tex.MathElement;
import org.jplot2d.transform.TransformType;
import org.jplot2d.util.Range;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @author Jingjing Li
 */
public interface AxisTickManagerEx extends AxisTickManager, ElementEx, Joinable {

    @Nullable
    AxisEx getParent();

    @Nonnull
    AxisEx[] getAxes();

    @Nullable
    AxisTransformEx getAxisTransform();

    void addAxis(AxisEx axis);

    void removeAxis(AxisEx axis);

    @Nonnull
    MathElement[] getLabelModels();

    /**
     * Calculate ticks when tick calculation is needed.
     */
    void calcTicks();

    /**
     * Expand the given range to major ticks. If {@link #isAutoTickValues()} is false, this method does nothing.
     * This method not change internal status of TickManager, except those values:
     * tickCalculator, tickNumber, labelFormat, labelInterval
     *
     * @param txfType transform type
     * @param range   the core range
     * @return the expanded range
     */
    @Nonnull
    Range expandRangeToTick(@Nonnull TransformType txfType, @Nonnull Range range);

    /**
     * Notified by AxisTransformEx that transform type changed.
     */
    void transformTypeChanged();

}
