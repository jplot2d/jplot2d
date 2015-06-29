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

import org.jplot2d.element.AxisOrientation;
import org.jplot2d.element.Plot;
import org.jplot2d.element.PlotAxis;

import javax.annotation.Nonnull;

/**
 *
 */
public interface PlotAxisEx extends PlotAxis, AxisEx {

    PlotEx getParent();

    /**
     * Called by {@link Plot#addXAxis(PlotAxis)} or {@link Plot#addYAxis(PlotAxis)} to set the orientation of this axis.
     *
     * @param orientation the orientation
     */
    void setOrientation(@Nonnull AxisOrientation orientation);


}
