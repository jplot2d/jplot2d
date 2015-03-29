/**
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
package org.jplot2d.transform;

/**
 * This interface define the reciprocal transformation between user value and tick value.
 *
 * @author Jingjing Li
 */
public class ReciprocalAxisTickTransform implements AxisTickTransform {

    public final double _factor;

    public ReciprocalAxisTickTransform(double factor) {
        _factor = factor;
    }

    public double transformUser2Tick(double v) {
        return _factor / v;
    }

    public double transformTick2User(double v) {
        if (Double.isNaN(v)) {
            return 0;
        } else {
            return _factor / v;
        }
    }

}
