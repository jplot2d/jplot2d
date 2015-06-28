/*
 * Copyright 2010-2015 Jingjing Li.
 *
 * This file is part of jplot2d.
 *
 * jplot2d is free software:
 * you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation, either version 3 of the License, or any later version.
 *
 * jplot2d is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Lesser Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with jplot2d.
 * If not, see <http://www.gnu.org/licenses/>.
 */
package org.jplot2d.transform;

import org.jplot2d.util.Range;

import javax.annotation.Nonnull;

/**
 * Immutable! Transform between value range and normalized range [0, 1].
 */
public abstract class NormalTransform {

    protected NormalTransform() {

    }

    public abstract TransformType getType();

    public abstract double getScale();

    public abstract double getOffset();

    /**
     * Derive a new NormalTransform with offset set to 0;
     *
     * @return the derived NormalTransform
     */
    public abstract NormalTransform deriveNoOffset();

    public abstract NormalTransform zoom(Range npr);

    public boolean isInverted() {
        return getScale() < 0;
    }

    public abstract NormalTransform invert();

    /**
     * Transform from value range to normalized range.
     *
     * @param u the value
     * @return normalized value
     */
    public abstract double convToNR(double u);

    /**
     * Transform a value from normalized range to value range.
     *
     * @param p normalized value
     * @return world value
     */
    public abstract double convFromNR(double p);

    public Range convToNR(Range wrange) {
        return new Range.Double(convToNR(wrange.getStart()), wrange.isStartIncluded(), convToNR(wrange.getEnd()),
                wrange.isEndIncluded());
    }

    public Range convFromNR(Range prange) {
        return new Range.Double(convFromNR(prange.getStart()), prange.isStartIncluded(), convFromNR(prange.getEnd()),
                prange.isEndIncluded());
    }

    /**
     * Returns the value range of this transform.
     *
     * @return the value range
     */
    @Nonnull
    public abstract Range getValueRange();

    public abstract double getMinPSpan4PrecisionLimit(double pLo, double pHi, double precisionLimit);

    /**
     * Create a Transform1D by assign a dest range.
     *
     * @param d1 the start value of dest range
     * @param d2 the end value of dest range
     * @return a Transform1D
     */
    public abstract Transform1D createTransform(double d1, double d2);

    public String toString() {
        return getClass().getSimpleName() + " " + getValueRange();
    }
}
