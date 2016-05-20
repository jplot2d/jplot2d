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
package org.jplot2d.axtick;

/**
 * An interface to convert the input values to the values in the unit that the tick calculator accepted.
 *
 * @author Jingjing Li
 */
public interface TickUnitConverter {

    public final TickUnitConverter IDENTITY = new TickUnitConverter() {
        public double convertD2T(double v) {
            return v;
        }

        public double convertT2D(double v) {
            return v;
        }
    };

    /**
     * Convert the input value to tick value
     *
     * @param v the data value
     * @return the tick value
     */
    public double convertD2T(double v);

    /**
     * Convert the tick value to input value
     *
     * @param v the tick value
     * @return the data value
     */
    public double convertT2D(double v);

}
