/**
 * Copyright 2010-2013 Jingjing Li.
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
package org.jplot2d.element.impl;

import org.jplot2d.element.PlotMargin;

/**
 * @author Jingjing Li
 */
public interface PlotMarginEx extends PlotMargin, ElementEx {

    public PlotEx getParent();

    /**
     * Called by LayoutDirector to set the top value when {@link #isAutoTop()} is true.
     */
    public void directTop(double marginTop);

    /**
     * Called by LayoutDirector to set the top value when {@link #isAutoLeft()} is true.
     */
    public void directLeft(double marginLeft);

    /**
     * Called by LayoutDirector to set the top value when {@link #isAutoBottom()} is true.
     */
    public void directBottom(double marginBottom);

    /**
     * Called by LayoutDirector to set the top value when {@link #isAutoRight()} is true.
     */
    public void directRight(double marginRight);

}
