/**
 * Copyright 2010 Jingjing Li.
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

/**
 * Defines how the plot size is decided.
 * 
 * @author Jingjing Li
 */
public enum PlotSizeMode {
    /**
     * The scale is kept. The chart size will automatically fit the container
     * size. The plot size is changed as well. The laying out is working on
     * outer-to-inner mode.
     */
    FIT_CONTAINER_SIZE,
    /**
     * The scale is calculated toward target size. The chart aspect ratio will
     * automatically fit the container. The plot size is changed as well. The
     * laying out is working on outer-to-inner mode.
     */
    FIT_CONTAINER_WITH_TARGET_SIZE,
    /**
     * The chart size is fixed. Changes on axes, title or legend will cause plot
     * size changed.
     */
    FIXED_SIZE,
    /**
     * The chart size will change to follow its contents change (plot size or
     * axis, title, legend dimensions)
     */
    FIT_CONTENTS
}