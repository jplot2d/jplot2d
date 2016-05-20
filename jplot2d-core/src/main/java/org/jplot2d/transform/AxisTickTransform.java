/*
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
package org.jplot2d.transform;

/**
 * This interface define the transformation between user value and tick value.
 *
 * @author Jingjing Li
 */
public interface AxisTickTransform {

    /**
     * Transform a user value to tick value
     *
     * @param v the user value
     * @return the tick value
     */
    double transformUser2Tick(double v);

    /**
     * Transform a tick value to user value.
     *
     * @param v the tick value
     * @return the user value
     */
    double transformTick2User(double v);

}
