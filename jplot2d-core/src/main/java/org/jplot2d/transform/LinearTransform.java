/*
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
package org.jplot2d.transform;

/**
 * Performs a linear transformation. The equation is <code>dest value = src value * k + a</code>
 */
public class LinearTransform implements Transform1D {

    private double k;

    private double a;

    /**
     * @param offset the offset in LinearNormalTransform
     * @param scale  the scale in LinearNormalTransform
     * @param d1     the start value in destination
     * @param d2     the end value in destination
     */
    public LinearTransform(double offset, double scale, double d1, double d2) {

        if (Double.isNaN(d1) || Double.isNaN(d2)) {
            throw new IllegalArgumentException("Transform is invalid");
        }

        k = (d2 - d1) / scale;
        a = d1 - k * offset;
    }

    /**
     * Transform from src to dest coordinates.
     *
     * @param u src value
     * @return dest value
     */
    public double convert(double u) {
        return k * u + a;
    }

}
