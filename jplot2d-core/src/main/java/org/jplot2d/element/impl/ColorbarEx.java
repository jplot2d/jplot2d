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

import org.jplot2d.element.Colorbar;
import org.jplot2d.element.ImageMapping;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @author Jingjing Li
 */
public interface ColorbarEx extends Colorbar, AxisContainerEx {

    PlotEx getParent();

    @Nonnull
    AxisTransformEx getAxisTransform();

    @Nonnull
    ColorbarAxisEx getInnerAxis();

    @Nonnull
    ColorbarAxisEx getOuterAxis();

    @Nullable
    ImageMappingEx getImageMapping();

    void setImageMapping(@Nullable ImageMapping mapping);

    void linkImageMapping(ImageMappingEx imageMapping);

    /**
     * Returns the short id of this element. The short id is composed of series of ids concatenated with dots.
     * The 1st id is the id of this element, the 2nd id is the id of the parent of this element, etc, until but not include the root plot.
     *
     * @return the short id of this component.
     */
    String getShortId();

    /**
     * Sets the location by layout director. The origin point of colorbar is bottom-left corner of the bar.
     *
     * @param locX the x value of the location
     * @param locY the y value of the location
     */
    void directLocation(double locX, double locY);

    /**
     * Set the length of this colorbar.
     *
     * @param length the length of this colorbar
     */
    void setLength(double length);

    double getAsc();

    double getDesc();

    /**
     * Returns the thickness of this colorbar. When position is top or bottom, thickness is its height.
     * When position is left or right, thickness is its width.
     *
     * @return the thickness of this colorbar
     */
    double getThickness();

    /**
     * Calculate thickness of this colorbar. This method is always called by plot when committing changes.
     */
    void calcThickness();

}
