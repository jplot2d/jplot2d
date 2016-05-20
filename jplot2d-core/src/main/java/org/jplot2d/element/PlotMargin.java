/*
 * Copyright 2010-2012 Jingjing Li.
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

/**
 * A plot has a content area in the center, surrounded by margin area. The margin area holds axes, title and legend.
 * Besides the margin, there are also extra margins. The extra margin plus the margin will be the total margin.
 *
 * @author Jingjing Li
 */
@PropertyGroup("Margin")
public interface PlotMargin extends Element {

    /**
     * Returns <code>true</code> if the margin is a auto chose nice value.
     *
     * @return <code>true</code> if the margin is a auto chose nice value
     */
    @Property(order = 0, styleable = false)
    boolean isAutoLeft();

    /**
     * Set to <code>true</code> to let layout director to choose a nice value. If set to
     * <code>false</code>, user must supply the margin value by {@link #setLeft(double)}.
     *
     * @param auto the flag
     */
    void setAutoLeft(boolean auto);

    /**
     * Returns <code>true</code> if the margin is a auto chose nice value.
     *
     * @return <code>true</code> if the margin is a auto chose nice value
     */
    @Property(order = 1, styleable = false)
    boolean isAutoRight();

    /**
     * Set to <code>true</code> to let layout director to choose a nice value. If set to
     * <code>false</code>, user must supply the margin value by {@link #setRight(double)}.
     *
     * @param auto the flag
     */
    void setAutoRight(boolean auto);

    /**
     * Returns <code>true</code> if the margin is a auto chose nice value.
     *
     * @return <code>true</code> if the margin is a auto chose nice value
     */
    @Property(order = 2, styleable = false)
    boolean isAutoBottom();

    /**
     * Set to <code>true</code> to let layout director to choose a nice value. If set to
     * <code>false</code>, user must supply the margin value by {@link #setBottom(double)}.
     *
     * @param auto the flag
     */
    void setAutoBottom(boolean auto);

    /**
     * Returns <code>true</code> if the margin is a auto chose nice value.
     *
     * @return <code>true</code> if the margin is a auto chose nice value
     */
    @Property(order = 3, styleable = false)
    boolean isAutoTop();

    /**
     * Set to <code>true</code> to let layout director to choose a nice value. If set to
     * <code>false</code>, user must supply the margin value by {@link #setTop(double)}.
     *
     * @param auto the flag
     */
    void setAutoTop(boolean auto);

    /**
     * Returns the actual top margin value.
     *
     * @return the actual top margin value
     */
    @Property(order = 4, styleable = false)
    double getLeft();

    /**
     * Sets the left margin value. Setting a new value will disable auto margin calculation and make
     * {@link #isAutoLeft()} returns <code>false</code>.
     *
     * @param marginLeft the left margin value
     */
    void setLeft(double marginLeft);

    /**
     * Returns the actual right margin value.
     *
     * @return the actual right margin value
     */
    @Property(order = 5, styleable = false)
    double getRight();

    /**
     * Sets the right margin value. Setting a new value will disable auto margin calculation
     * and make {@link #isAutoRight()} returns <code>false</code>.
     *
     * @param marginRight the right margin value
     */
    void setRight(double marginRight);

    /**
     * Returns the actual bottom margin value.
     *
     * @return the actual bottom margin value
     */
    @Property(order = 6, styleable = false)
    double getBottom();

    /**
     * Sets the bottom margin value. Setting a new value will disable auto margin calculation
     * and make {@link #isAutoBottom()} returns <code>false</code>.
     *
     * @param marginBottom the bottom margin value
     */
    void setBottom(double marginBottom);

    /**
     * Returns the actual top margin value.
     *
     * @return the actual top margin value
     */
    @Property(order = 7, styleable = false)
    double getTop();

    /**
     * Sets the top margin value. Setting a new value will disable auto margin calculation
     * and make {@link #isAutoTop()} returns <code>false</code>.
     *
     * @param marginTop the top margin value
     */
    void setTop(double marginTop);

    /**
     * Returns the extra left margin.
     *
     * @return the extra left margin
     */
    @Property(order = 8)
    double getExtraLeft();

    /**
     * Sets the extra left margin.
     *
     * @param marginLeft the extra left margin
     */
    void setExtraLeft(double marginLeft);

    /**
     * Returns the extra right margin.
     *
     * @return the extra right margin
     */
    @Property(order = 9)
    double getExtraRight();

    /**
     * Sets the extra right margin.
     *
     * @param marginRight the extra right margin
     */
    void setExtraRight(double marginRight);

    /**
     * Returns the extra bottom margin.
     *
     * @return the extra bottom margin
     */
    @Property(order = 10)
    double getExtraBottom();

    /**
     * Sets the extra bottom margin.
     *
     * @param marginBottom the extra bottom margin
     */
    void setExtraBottom(double marginBottom);

    /**
     * Returns the extra top margin.
     *
     * @return the extra top margin
     */
    @Property(order = 11)
    double getExtraTop();

    /**
     * Sets the extra top margin.
     *
     * @param marginTop the extra top margin
     */
    void setExtraTop(double marginTop);

}
