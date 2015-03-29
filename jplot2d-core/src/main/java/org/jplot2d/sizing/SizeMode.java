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
package org.jplot2d.sizing;

import org.jplot2d.element.impl.PlotEx;
import org.jplot2d.util.DoubleDimension2D;

import java.awt.geom.Dimension2D;

/**
 * Defines how the plot size is decided. The plot container size is a common input for all size
 * mode. If a plot's size mode is <code>null</code>, the plot container size is useless.
 *
 * @author Jingjing Li
 */
public abstract class SizeMode {

    public static class Result {

        protected final double width, height;

        protected double scale = 1;

        public Result(double width, double height, double scale) {
            this.width = width;
            this.height = height;
            this.scale = scale;
        }

        /**
         * Returns the plot size derived from this size mode .
         *
         * @return the plot size
         */
        public Dimension2D getSize() {
            return new DoubleDimension2D(width, height);
        }

        /**
         * Returns the scale derived from this size mode
         *
         * @return the scale
         */
        public double getScale() {
            return scale;
        }

    }

    protected final boolean autoPack;

    protected SizeMode(boolean autoPack) {
        this.autoPack = autoPack;
    }

    /**
     * Returns <code>true</code> if this size mode has auto pack feature.
     *
     * @return <code>true</code> if this size mode has auto pack feature.
     */
    public boolean isAutoPack() {
        return autoPack;
    }

    /**
     * Update the internal status of this size mode.
     * <p/>
     * For Internal use only. User should never call this method.
     */
    public abstract Result update(PlotEx plot);

}
