/*
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
package org.jplot2d.sizing;

import org.jplot2d.element.impl.PlotEx;

import java.awt.geom.Dimension2D;

/**
 * The plot size will automatically fit the container size. The plot content size is changed as well.
 */
public class FillContainerSizeMode extends SizeMode {

    private final Dimension2D targetSize;

    private final double scale;

    /**
     * The scale is fixed.
     *
     * @param scale the scale
     */
    public FillContainerSizeMode(double scale) {
        super(false);
        this.targetSize = null;
        this.scale = scale;
    }

    /**
     * The scale is calculated toward target size. The chart aspect ratio will automatically fit the container. The plot
     * size is changed as well. The laying out is working on outer-to-inner mode.
     *
     * @param targetSize the target size
     */
    public FillContainerSizeMode(Dimension2D targetSize) {
        super(false);
        if (targetSize.getWidth() <= 0 || targetSize.getHeight() <= 0) {
            throw new IllegalArgumentException("Target size must be positive, " + targetSize.getWidth() + "x"
                    + targetSize.getHeight() + " is invalid.");
        }
        this.targetSize = targetSize;
        this.scale = 1;
    }

    /**
     * Returns the target size. If the scale is fixed, returns <code>null</code>
     *
     * @return the target size
     */
    public Dimension2D getTargetSize() {
        return targetSize;
    }

    public Result update(PlotEx plot) {
        Dimension2D containerSize = plot.getContainerSize();

        if (targetSize != null) {
            Dimension2D tcSize = targetSize;

            double scaleX = containerSize.getWidth() / tcSize.getWidth();
            double scaleY = containerSize.getHeight() / tcSize.getHeight();
            double scale = (scaleX < scaleY) ? scaleX : scaleY;

            double width = containerSize.getWidth() / scale;
            double height = containerSize.getHeight() / scale;

            return new Result(width, height, scale);

        } else {
            double width = containerSize.getWidth() / scale;
            double height = containerSize.getHeight() / scale;

            return new Result(width, height, scale);
        }

    }

    public String toString() {
        if (targetSize == null) {
            return "Fill container with scale " + scale;
        } else {
            return "Fill container with target size " + targetSize.getWidth() + "x" + targetSize.getHeight();
        }
    }

}