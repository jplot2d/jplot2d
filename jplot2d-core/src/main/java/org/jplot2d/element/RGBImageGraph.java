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
import org.jplot2d.data.MultiBandImageData;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * A image graph which can display a 3-bands data and map them to RGB color.
 *
 * @author Jingjing Li
 */
@PropertyGroup("Image")
public interface RGBImageGraph extends Graph {

    /**
     * Returns the mapping of this image.
     *
     * @return the mapping of this image
     */
    @Hierarchy(HierarchyOp.GET)
    @Nullable
    RGBImageMapping getMapping();

    @Hierarchy(HierarchyOp.JOIN)
    void setMapping(@Nonnull RGBImageMapping mapping);

    @Nullable
    MultiBandImageData getData();

    void setData(@Nullable MultiBandImageData data);

}
