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

import org.jplot2d.element.RGBImageMapping;

import javax.annotation.Nonnull;

/**
 * @author Jingjing Li
 */
public interface RGBImageMappingEx extends RGBImageMapping, ElementEx, Joinable {

    RGBImageGraphEx getParent();

    RGBImageGraphEx[] getGraphs();

    void addImageGraph(RGBImageGraphEx graph);

    void removeImageGraph(RGBImageGraphEx graph);

    @Nonnull
    ImageBandTransformEx getRedTransform();

    @Nonnull
    ImageBandTransformEx getGreenTransform();

    @Nonnull
    ImageBandTransformEx getBlueTransform();

    void invalidateLimits();

    /**
     * Called by PlotEx.commit() to calculate limits for bands if needed
     */
    void calcLimits();

}
