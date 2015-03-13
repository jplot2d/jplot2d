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
package org.jplot2d.axtick;

/**
 * This class represent a number in degree minute second format
 *
 * @author Jingjing Li
 */
public final class ArcDmsInterval implements DoubleInterval {

    public enum Unit {
        SECOND(1.0 / 3600), MINUTE(1.0 / 60), DEGREE(1);

        public final double angle;

        private Unit(double angle) {
            this.angle = angle;
        }
    }

    private final int _c, _e;

    private final Unit _unit;

    public ArcDmsInterval(int coefficient, int exponent) {
        _unit = Unit.SECOND;
        _c = coefficient;
        _e = exponent;
    }

    public ArcDmsInterval(Unit unit, int value) {
        _unit = unit;
        _c = value;
        _e = 0;
    }

    public Unit getUnit() {
        return _unit;
    }

    /**
     * @return the coefficient
     */
    public int getCoefficient() {
        return _c;
    }

    /**
     * @return the exponent
     */
    public int getExponent() {
        return _e;
    }

    public double doubleValue() {
        return Math.pow(10, _e) * _c * _unit.angle;
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof ArcDmsInterval)) {
            return false;
        }
        ArcDmsInterval ien = (ArcDmsInterval) obj;
        return _c == ien._c && _e == ien._e;
    }

    public int hashCode() {
        return _c ^ (_e << 16);
    }

    public String toString() {
        return _c + "e" + _e;
    }
}
