/**
 * Copyright 2010, 2011 Jingjing Li.
 *
 * This file is part of jplot2d.
 *
 * jplot2d is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or any later version.
 *
 * jplot2d is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Lesser Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with jplot2d. If not, see <http://www.gnu.org/licenses/>.
 */
package org.jplot2d.element;

import org.jplot2d.annotation.Property;
import org.jplot2d.annotation.PropertyGroup;

import java.awt.geom.Point2D;

/**
 * A text title of plot. A plot may has multiple title. They are always in top-down order.
 *
 * @author Jingjing Li
 */
@PropertyGroup("Title")
public interface Title extends TextComponent, MovableComponent {

    /**
     * Gets the current position of this title in a layer.
     *
     * @return the position.
     */
    @Property(order = 1)
    public TitlePosition getPosition();

    /**
     * Sets the position of this title.
     * The values can be FREE, TOPLEFT, TOPCENTER, TOPRIGHT, BOTTOMLEFT, BOTTOMCENTER, BOTTOMRIGHT.
     * Only when position is <code>FREE</code>, the title can be located by
     * {@link #setLocation(Point2D)}, {@link #setHAlign(HAlign)}, {@link #setVAlign(VAlign)} .
     *
     * @param position the position of this title.
     */
    public void setPosition(TitlePosition position);

    /**
     * Gets the location of this title.
     *
     * @return an instance of <code>Point</code> representing the base point of this title
     */
    @Property(order = 2, styleable = false, displayDigits = 4)
    public Point2D getLocation();

    /**
     * Moves this title to a new location.
     * <p/>
     * Notice: This method should be called when the position is <code>null</code>,
     * otherwise the behavior is not defined.
     *
     * @param loc the base point of the title
     */
    public void setLocation(Point2D loc);

    /**
     * Get the horizontal alignment.
     *
     * @return the horizontal alignment.
     */
    @Property(order = 3, styleable = false)
    public HAlign getHAlign();

    /**
     * Set the horizontal alignment. The alignment can be LEFT, CENTER, or RIGHT.
     * eg, LEFT means the base point is on the left of this title.
     * <p/>
     * Notice: This method should be called when the position is <code>null</code>,
     * otherwise the behavior is not defined.
     *
     * @param halign horizontal alignment.
     */
    public void setHAlign(HAlign halign);

    /**
     * Get the vertical alignment.
     *
     * @return the vertical alignment.
     */
    @Property(order = 4, styleable = false)
    public VAlign getVAlign();

    /**
     * Set the vertical alignment. The alignment can be TOP, MIDDLE, or BOTTOM.
     * eg, TOP means the base point is on the top of this title.
     * <p/>
     * Notice: This method should be called when the position is <code>null</code>,
     * otherwise the behavior is not defined.
     *
     * @param valign The vertical alignment.
     */
    public void setVAlign(VAlign valign);

    /**
     * Returns the ratio of gap to its height. The default value is 1/4.
     *
     * @return the gap factor
     */
    @Property(order = 5)
    public double getGapFactor();

    /**
     * Sets the ratio of gap to its height.
     * The gap is under the title when its position is TOPLEFT, TOPCENTER, TOPRIGHT.
     * The gap is above the title when its position is BOTTOMLEFT, BOTTOMCENTER, BOTTOMRIGHT.
     */
    public void setGapFactor(double factor);

    @Property(order = 6)
    public boolean isMovable();

    public void setMovable(boolean movable);

}
