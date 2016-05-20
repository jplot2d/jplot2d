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
import java.awt.geom.Point2D;

/**
 * Every plot has a legend to display legend items. If there is no visible item in a legend, it will not show.
 *
 * @author Jingjing Li
 */
@SuppressWarnings("unused")
@PropertyGroup("Legend")
public interface Legend extends MovableComponent {

    /**
     * Returns <code>true</code> if this legend is enabled. By default, the legend is enabled.
     *
     * @return the enabled flag.
     */
    @Property(order = 0)
    boolean isEnabled();

    /**
     * Set to <code>true</code> to let this legend to host items.
     * Otherwise the items will be displayed in parent legend. The disabled legend will not show.
     *
     * @param enabled the flag
     */
    void setEnabled(boolean enabled);

    /**
     * Gets the current position in its plot.
     *
     * @return the position.
     */
    @Nonnull
    @Property(order = 1)
    LegendPosition getPosition();

    /**
     * Sets the position in its plot. The default position is {@link LegendPosition#BOTTOMCENTER}.
     * Only when position is {@link LegendPosition#FREE}, the legend can be located by
     * {@link #setLocation(Point2D)}, {@link #setHAlign(HAlign)} , {@link #setVAlign(VAlign)}.
     *
     * @param position the position of this legend. <code>null</code> means FREE.
     */
    void setPosition(@Nullable LegendPosition position);

    /**
     * Gets the location in the paper space of its plot.
     * If the position is not {@link LegendPosition#FREE}, the location is calculated by the layout director of its plot.
     *
     * @return an instance of <code>Point</code> representing the base point of this legend
     */
    @Property(order = 2, styleable = false, displayDigits = 4)
    Point2D getLocation();

    /**
     * Moves this legend to a new location, in the paper space of its plot.
     * Setting a new location will change legend position to {@link LegendPosition#FREE}
     *
     * @param loc the base point of this legend
     */
    void setLocation(Point2D loc);

    /**
     * Moves this legend to a new location.
     * Setting legend to a new location will change legend position to {@link LegendPosition#FREE}
     *
     * @param x the x-coordinate of the new location
     * @param y the y-coordinate of the new location
     */
    void setLocation(double x, double y);

    /**
     * Get the horizontal alignment.
     * If the position is not {@link LegendPosition#FREE}, the align is calculated by the layout director of its plot.
     *
     * @return the horizontal alignment.
     */
    @Property(order = 3, styleable = false)
    HAlign getHAlign();

    /**
     * Set the horizontal alignment. The alignment can be LEFT, CENTER, or RIGHT.
     * eg, LEFT means the base point is on the left of this legend.
     * <p>
     * Notice: This method should be called when the position is {@link LegendPosition#FREE}, otherwise the behavior is not defined.
     *
     * @param halign the horizontal alignment.
     */
    void setHAlign(HAlign halign);

    /**
     * Get the vertical alignment.
     * If the position is not {@link LegendPosition#FREE}, the align is calculated by the layout director of its plot.
     *
     * @return the vertical alignment.
     */
    @Property(order = 4, styleable = false)
    VAlign getVAlign();

    /**
     * Set the vertical alignment. The alignment can be TOP, MIDDLE, or BOTTOM.
     * eg, TOP means the base point is on the top of this legend.
     * <p>
     * Notice: This method should be called when the position is {@link LegendPosition#FREE}, otherwise the behavior is not defined.
     *
     * @param valign The vertical alignment.
     */
    void setVAlign(VAlign valign);

    /**
     * Returns the number of columns to arrange the legend items.
     * If the position is not {@link LegendPosition#FREE}, the columns is calculated by the layout director of its plot.
     *
     * @return the number of columns
     */
    @Property(order = 5, styleable = false)
    int getColumns();

    /**
     * Sets the number of columns. This method only take effect when position is {@link LegendPosition#FREE}.
     * Otherwise the columns is auto selected.
     *
     * @param columns number of columns
     */
    void setColumns(int columns);

    /**
     * Returns the row spacing factor. The row spacing is factor * row height.
     *
     * @return the row spacing factor
     */
    @Property(order = 6)
    double getRowSpacingFactor();

    /**
     * Sets the row spacing factor. The row spacing is factor * row height. The default factor is 0.125
     *
     * @param factor the row spacing factor
     */
    void setRowSpacingFactor(double factor);

    /**
     * Returns <code>true</code> if the legend border is visible
     *
     * @return <code>true</code> if the legend border is visible
     */
    @Property(order = 7)
    boolean isBorderVisible();

    /**
     * Sets if the legend border is visible. By default, the legend border is visible.
     *
     * @param visible the flag to indicate if the legend border is visible
     */
    void setBorderVisible(boolean visible);

    @Property(order = 8)
    boolean isMovable();

    void setMovable(boolean movable);

    /**
     * Returns all legend items managed by this legend
     *
     * @return items managed by this legend
     */
    @Hierarchy(HierarchyOp.GETARRAY)
    LegendItem[] getItems();

}
