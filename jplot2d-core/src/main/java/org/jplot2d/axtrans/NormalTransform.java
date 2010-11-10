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
/*
 * $Id: NormalTransform.java,v 1.2 2010/06/11 09:44:31 jli Exp $
 */
package org.jplot2d.axtrans;

import org.jplot2d.util.Range2D;

/**
 * Transform between world coordinate and normal coordinate.
 */
public abstract class NormalTransform implements Cloneable {

	public static final Range2D NORMAL_RANGE = new Range2D.Double(0.0, 1.0);

    protected boolean _valid = false;

    protected double _a;

    protected double _b;

    /**
     * Create a non-transformable AxisTranform.
     */
    protected NormalTransform() {

    }

    /**
     * Create a copy of this <code>NormalTransform</code>.
     * 
     * @return the copy
     */
    public abstract NormalTransform copy();

    /**
     * Derive a new NormalTransform with offset set to 0;
     * 
     * @return the derived NormalTransform
     */
    public NormalTransform deriveNoOffset() {
        NormalTransform result = this.copy();
        result._b = 0;
        return result;
    }

    /**
     * Returns if this AxisTransform is ready to transform values. Only
     * <code>true</code> after both physical range and world range are set
     * properly.
     * 
     * @return <code>true</code> if this AxisTransform is ready to transform
     *         values.
     */
    public boolean isValid() {
        return _valid;
    }

    public double getScale() {
        if (!_valid) {
            throw new IllegalStateException("Transform is invalid");
        }
        return _a;
    }

    public double getOffset() {
        if (!_valid) {
            throw new IllegalStateException("Transform is invalid");
        }
        return _b;
    }

    /**
     * Transform from world to physical coordinate.
     * 
     * @param u
     *            world value
     * @return physical value
     */
    public abstract double getTransP(double u);

    /**
     * Transform from physical to world coordinate.
     * 
     * @param p
     *            physical value
     * @return world value
     */
    public abstract double getTransU(double p);

    public Range2D getTransP(Range2D wrange) {
        return new Range2D.Double(getTransP(wrange.getStart()), wrange
                .isStartIncluded(), getTransP(wrange.getEnd()), wrange
                .isEndIncluded());
    }

    public Range2D getTransU(Range2D prange) {
        return new Range2D.Double(getTransU(prange.getStart()), prange
                .isStartIncluded(), getTransU(prange.getEnd()), prange
                .isEndIncluded());
    }

    public void zoom(Range2D npr) {
        _b = _b + _a * npr.getStart();
        _a = _a * npr.getSpan();
        // prevent overflow
        if (_a == Double.POSITIVE_INFINITY) {
            _a = Double.MAX_VALUE;
        } else if (_a == Double.NEGATIVE_INFINITY) {
            _a = -Double.MAX_VALUE;
        }
    }

    public void invert() {
        _b += _a;
        _a = -_a;
    }

    public abstract Range2D getRangeW();

    public abstract double getMinPSpan4PrecisionLimit(double pLo, double pHi,
            double precisionLimit);

}
