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
package org.jplot2d.util;

/**
 * Immutable!<br>
 * An Insets2D object is a representation of the borders of a container. It specifies the space that a container must
 * leave at each of its edges.
 *
 * @author Jingjing Li
 */
public abstract class Insets2D {

    /**
     * Returns the inset from the top in double precision
     *
     * @return the inset from the top in double precision
     */
    public abstract double getTop();

    /**
     * Returns the inset from the left in double precision
     *
     * @return the inset from the left in double precision
     */
    public abstract double getLeft();

    /**
     * Returns the inset from the bottom in double precision
     *
     * @return the inset from the bottom in double precision
     */
    public abstract double getBottom();

    /**
     * Returns the inset from the right in double precision
     *
     * @return the inset from the right in double precision
     */
    public abstract double getRight();

    /**
     * An Insets2D instance specified with double precision
     */
    public static class Double extends Insets2D {

        private final double left;
        private final double right;
        private final double top;
        private final double bottom;

        /**
         * Construct an Insets2D instance using double precision
         *
         * @param top    the inset from the top
         * @param left   the inset from the left
         * @param bottom the inset from the bottom
         * @param right  the inset from the right
         */
        public Double(double top, double left, double bottom, double right) {
            this.top = top;
            this.left = left;
            this.bottom = bottom;
            this.right = right;
        }

        public double getTop() {
            return top;
        }

        public double getLeft() {
            return left;
        }

        public double getBottom() {
            return bottom;
        }

        public double getRight() {
            return right;
        }

        /**
         * Checks whether two insets objects are equal. Two instances of <code>Insets2D</code> are equal if the four
         * integer values of the fields <code>top</code>, <code>left</code>, <code>bottom</code>, and <code>right</code>
         * are all equal.
         *
         * @return <code>true</code> if the two insets are equal; otherwise <code>false</code>.
         */
        public boolean equals(Object obj) {
            if (obj instanceof Insets2D.Double) {
                Insets2D.Double insets = (Insets2D.Double) obj;
                return ((top == insets.top) && (left == insets.left) && (bottom == insets.bottom) && (right == insets.right));
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
            return this.getClass().getName() + "[top=" + top + ",left=" + left + ",bottom=" + bottom + ",right="
                    + right + "]";
        }

    }

    /**
     * An Insets2D instance specified with float precision
     */
    public static class Float extends Insets2D {

        public final float top;
        public final float left;
        public final float bottom;
        public final float right;

        /**
         * Construct an Insets2D instance using float precision
         *
         * @param top    the inset from the top
         * @param left   the inset from the left
         * @param bottom the inset from the bottom
         * @param right  the inset from the right
         */
        public Float(float top, float left, float bottom, float right) {
            this.top = top;
            this.left = left;
            this.bottom = bottom;
            this.right = right;
        }

        public double getTop() {
            return (double) top;
        }

        public double getLeft() {
            return (double) left;
        }

        public double getBottom() {
            return (double) bottom;
        }

        public double getRight() {
            return (double) right;
        }

        /**
         * Checks whether two insets objects are equal. Two instances of <code>Insets2D</code> are equal if the four
         * integer values of the fields <code>top</code>, <code>left</code>, <code>bottom</code>, and <code>right</code>
         * are all equal.
         *
         * @return <code>true</code> if the two insets are equal; otherwise <code>false</code>.
         */
        public boolean equals(Object obj) {
            if (obj instanceof Insets2D.Double) {
                Insets2D.Double insets = (Insets2D.Double) obj;
                return ((top == insets.top) && (left == insets.left) && (bottom == insets.bottom) && (right == insets.right));
            }
            return false;
        }

        /**
         * Returns the hash code for this Insets.
         *
         * @return a hash code for this Insets.
         */
        public int hashCode() {
            int bits = java.lang.Float.floatToIntBits(top);
            bits += java.lang.Float.floatToIntBits(left) * 37;
            bits += java.lang.Float.floatToIntBits(bottom) * 43;
            bits += java.lang.Float.floatToIntBits(right) * 47;
            return bits;
        }

        public String toString() {
            return this.getClass().getName() + "[top=" + top + ",left=" + left + ",bottom=" + bottom + ",right="
                    + right + "]";
        }

    }

}