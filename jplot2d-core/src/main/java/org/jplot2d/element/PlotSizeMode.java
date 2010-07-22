/*
 * This file is part of Herschel Common Science System (HCSS).
 * Copyright 2001-2010 Herschel Science Ground Segment Consortium
 *
 * HCSS is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of
 * the License, or (at your option) any later version.
 *
 * HCSS is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General
 * Public License along with HCSS.
 * If not, see <http://www.gnu.org/licenses/>.
 */
/**
 * 
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