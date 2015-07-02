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
import org.jplot2d.annotation.Property;
import org.jplot2d.annotation.PropertyGroup;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * A bar to show a color mapping of image graphs. A colorbar contains 2 axes, innerAxis and outerAxis.
 *
 * @author Jingjing Li
 */
@SuppressWarnings("unused")
@PropertyGroup("Colorbar")
public interface Colorbar extends PComponent {

    @Hierarchy(HierarchyOp.GET)
    Plot getParent();

    /**
     * Returns the ImageMapping that shown in this colorbar.
     *
     * @return the ImageMapping
     */
    @Hierarchy(HierarchyOp.GET)
    @Nullable
    ImageMapping getImageMapping();

    /**
     * Sets the ImageMapping that will be show in this colorbar.
     *
     * @param mapping the ImageMapping
     */
    @Hierarchy(HierarchyOp.REF)
    void setImageMapping(@Nonnull ImageMapping mapping);

    /**
     * Return the AxisTransform of this colorbar. The returned AxisTransform is NUMBER-LINEAR, and cannot be changed.
     * The range of returned AxisTransform will be auto-update when the limits of mapping changed.
     *
     * @return the AxisTransform of this colorbar
     */
    @Hierarchy(HierarchyOp.GET)
    @Nonnull
    AxisTransform getAxisTransform();

    /**
     * Returns the axis which close to the plot contents.
     *
     * @return the lower axis of this colorbar
     */
    @Hierarchy(HierarchyOp.GET)
    @Nonnull
    ColorbarAxis getInnerAxis();

    /**
     * Returns the axis which away from the plot contents.
     *
     * @return the upper axis of this colorbar
     */
    @Hierarchy(HierarchyOp.GET)
    @Nonnull
    ColorbarAxis getOuterAxis();

    /**
     * Returns the paper length of this colorbar.
     *
     * @return the paper length
     */
    double getLength();

    /**
     * Return the position of the colorbar: TOP, BOTTOM, LEFT, RIGHT.
     *
     * @return the position of the colorbar in the plot.
     */
    @Nonnull
    @Property(order = 1)
    ColorbarPosition getPosition();

    /**
     * Set the position of the colorbar: TOP, BOTTOM, LEFT, RIGHT. The default position is {@link ColorbarPosition#RIGHT}.
     *
     * @param position the position of the colorbar in the plot.
     */
    void setPosition(@Nonnull ColorbarPosition position);

    /**
     * Returns the gap between the colorbar and plot content.
     *
     * @return the gap
     */
    @Property(order = 2)
    double getGap();

    /**
     * Sets the gap between the colorbar and plot content. The default value is 8 pt (1 pt = 1/72 inch).
     */
    void setGap(double gap);

    /**
     * Returns the width of colorbar.
     *
     * @return the gap
     */
    @Property(order = 3)
    double getBarWidth();

    /**
     * Sets the width of colorbar. The default value is 16 pt (1 pt = 1/72 inch).
     */
    void setBarWidth(double width);

    /**
     * Returns the border line width in pt(1/72 inch).
     *
     * @return the axis line width
     */
    @Property(order = 5)
    float getBorderLineWidth();

    /**
     * Sets the border line width in pt(1/72 inch). The default line width is 1.0 pt.
     *
     * @param width the axis line width
     */
    void setBorderLineWidth(float width);

}
