/**
 * Copyright 2010, 2011 Jingjing Li.
 * <p/>
 * This file is part of jplot2d.
 * <p/>
 * jplot2d is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or any later version.
 * <p/>
 * jplot2d is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Lesser Public License for more details.
 * <p/>
 * You should have received a copy of the GNU Lesser General Public License
 * along with jplot2d. If not, see <http://www.gnu.org/licenses/>.
 */
package org.jplot2d.element;

import org.jplot2d.annotation.Property;
import org.jplot2d.annotation.PropertyGroup;

import javax.annotation.Nonnull;
import java.awt.geom.Point2D;

/**
 * A text title of plot. A plot may has multiple title. They are always in top-down order.
 *
 * @author Jingjing Li
 */
@SuppressWarnings("unused")
@PropertyGroup("Title")
public interface Title extends TextComponent, MovableComponent {

    /**
     * Gets the current position of this title in a plot.
     *
     * @return the position.
     */
    @Nonnull
    @Property(order = 1)
    TitlePosition getPosition();

    /**
     * Sets the position in a plot. The default position is {@link TitlePosition#TOPCENTER}.
     * The values can be FREE, TOPLEFT, TOPCENTER, TOPRIGHT, BOTTOMLEFT, BOTTOMCENTER, BOTTOMRIGHT.
     * Only when position is <code>FREE</code>, the title can be located by
     * {@link #setLocation(Point2D)}, {@link #setHAlign(HAlign)}, {@link #setVAlign(VAlign)} .
     *
     * @param position the position of this title.
     */
    void setPosition(@Nonnull TitlePosition position);

    /**
     * Gets the location in the paper space of a plot.
     * If the position is not {@link TitlePosition#FREE}, the location is calculated by the layout director of its plot.
     *
     * @return an instance of <code>Point</code> representing the base point of this title
     */
    @Property(order = 2, styleable = false, displayDigits = 4)
    Point2D getLocation();

    /**
     * Moves this title to a new location.
     * <p/>
     * Notice: This method should be called when the position is <code>null</code>,
     * otherwise the behavior is not defined.
     *
     * @param loc the base point of the title
     */
    void setLocation(Point2D loc);

    /**
     * Get the horizontal alignment.
     *
     * @return the horizontal alignment.
     */
    @Property(order = 3, styleable = false)
    HAlign getHAlign();

    /**
     * Set the horizontal alignment. The alignment can be LEFT, CENTER, or RIGHT.
     * eg, LEFT means the base point is on the left of this title.
     * <p/>
     * Notice: This method should be called when the position is <code>null</code>,
     * otherwise the behavior is not defined.
     *
     * @param halign horizontal alignment.
     */
    void setHAlign(HAlign halign);

    /**
     * Get the vertical alignment.
     *
     * @return the vertical alignment.
     */
    @Property(order = 4, styleable = false)
    VAlign getVAlign();

    /**
     * Set the vertical alignment. The alignment can be TOP, MIDDLE, or BOTTOM.
     * eg, TOP means the base point is on the top of this title.
     * <p/>
     * Notice: This method should be called when the position is <code>null</code>,
     * otherwise the behavior is not defined.
     *
     * @param valign The vertical alignment.
     */
    void setVAlign(VAlign valign);

    /**
     * Returns the ratio of gap to its height. The default value is 1/4.
     *
     * @return the gap factor
     */
    @Property(order = 5)
    double getGapFactor();

    /**
     * Sets the ratio of gap to its height.
     * The gap is under the title when its position is TOPLEFT, TOPCENTER, TOPRIGHT.
     * The gap is above the title when its position is BOTTOMLEFT, BOTTOMCENTER, BOTTOMRIGHT.
     */
    void setGapFactor(double factor);

    @Property(order = 6)
    boolean isMovable();

    void setMovable(boolean movable);

}
