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
 * $Id: Insets2D.java,v 1.3 2010/02/02 10:16:43 hsclib Exp $
 */
package org.jplot2d.util;

/**
 * Immutable!<br>
 * An Insets2D object is a representation of the borders of a container. It
 * specifies the space that a container must leave at each of its edges.
 * 
 * @author Jingjing Li
 */
public class Insets2D {
    private double left, right, top, bottom;

    public Insets2D(double top, double left, double bottom, double right) {
        this.top = top;
        this.left = left;
        this.bottom = bottom;
        this.right = right;
    }

    public double getBottom() {
        return bottom;
    }

    public double getLeft() {
        return left;
    }

    public double getRight() {
        return right;
    }

    public double getTop() {
        return top;
    }

    /**
     * Checks whether two insets objects are equal. Two instances of
     * <code>Insets2D</code> are equal if the four integer values of the
     * fields <code>top</code>, <code>left</code>, <code>bottom</code>,
     * and <code>right</code> are all equal.
     * 
     * @return <code>true</code> if the two insets are equal; otherwise
     *         <code>false</code>.
     */
    public boolean equals(Object obj) {
        if (obj instanceof Insets2D) {
            Insets2D insets = (Insets2D) obj;
            return ((top == insets.top) && (left == insets.left)
                    && (bottom == insets.bottom) && (right == insets.right));
        }
        return false;
    }

    /**
     * Returns the hash code for this Insets.
     * 
     * @return a hash code for this Insets.
     */
    public int hashCode() {
        long bits = java.lang.Double.doubleToLongBits(top);
        bits += java.lang.Double.doubleToLongBits(left) * 37;
        bits += java.lang.Double.doubleToLongBits(bottom) * 43;
        bits += java.lang.Double.doubleToLongBits(right) * 47;
        return (((int) bits) ^ ((int) (bits >> 32)));
    }

    public String toString() {
        return this.getClass().getName() + "[top=" + top + ",left=" + left
                + ",bottom=" + bottom + ",right=" + right + "]";
    }

}