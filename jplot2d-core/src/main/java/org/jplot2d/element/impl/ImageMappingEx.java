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

import org.jplot2d.element.ImageMapping;

import javax.annotation.Nullable;

/**
 * @author Jingjing Li
 */
public interface ImageMappingEx extends ImageMapping, ElementEx, Joinable {

    ImageGraphEx getParent();

    ImageGraphEx[] getGraphs();

    void addImageGraph(ImageGraphEx graph);

    void removeImageGraph(ImageGraphEx graph);

    /**
     * Called by ImageGraphEx when its data changed, to notify the limits need to be recalculated.
     */
    void invalidateLimits();

    /**
     * Called by PlotEx.commit() to calculate limits if needed
     */
    void calcLimits();

    @Nullable
    double[] getLimits();

    /**
     * Returns the number of significant bits that the ILUT output range. When creating image, the color model bit
     * should match this value.
     *
     * @return the number of significant bits
     */
    int getILUTOutputBits();

}
