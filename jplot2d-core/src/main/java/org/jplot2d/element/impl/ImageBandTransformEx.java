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

import org.jplot2d.data.ImageDataBuffer;
import org.jplot2d.element.ImageBandTransform;
import org.jplot2d.util.Range;

import javax.annotation.Nullable;
import java.awt.Dimension;

/**
 * @author Jingjing Li
 */
public interface ImageBandTransformEx extends ImageBandTransform, ElementEx {

    RGBImageMappingEx getParent();

    /**
     * Called by RGBImageGraphEx when its data changed, to notify the limits of this band need to be recalculated.
     */
    void invalidateLimits();

    /**
     * Calculate limits if needed
     */
    void calcLimits(ImageDataBuffer[] dataBuffers, Dimension[] sizeArray);

    @Nullable
    Range getLimits();

}
