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
import org.jplot2d.annotation.PropertyGroup;
import org.jplot2d.data.SingleBandImageData;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * A graph to display a pseudo-color image. Its data is a single band intensity values.
 * The default z-order of this component is -1, so that the graph will not cover axes ticks.
 *
 * @author Jingjing Li
 */
@PropertyGroup("Image")
public interface ImageGraph extends Graph {

    /**
     * Returns the mapping of this image.
     *
     * @return the mapping of this image
     */
    @Hierarchy(HierarchyOp.GET)
    @Nullable
    ImageMapping getMapping();

    @Hierarchy(HierarchyOp.JOIN)
    void setMapping(@Nonnull ImageMapping mapping);

    @Nullable
    SingleBandImageData getData();

    void setData(@Nullable SingleBandImageData data);

}
