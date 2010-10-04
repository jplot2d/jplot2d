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

import java.io.Serializable;

/**
 * A range contains a start value and a end value. Immutable.
 * 
 * @author Jingjing Li
 */
public abstract class Range2D implements Cloneable {

    public static class Double extends Range2D implements Serializable {

        private static final long serialVersionUID = 2L;

        public final double start;

        public final double end;

        public final boolean startIncl, endIncl;

        public Double() {
            this(java.lang.Double.NaN, java.lang.Double.NaN);
        }

        /**
         * Construct Range2D.Double with start and end.
         * 
         * @param start
         *            first value
         * @param end
         *            last value
         */
        public Double(double start, double end) {
            this(start, true, end, true);
        }

        public Double(double start, boolean isStartIncluded, double end,
                boolean isEndIncluded) {
            startIncl = isStartIncluded;
            endIncl = isEndIncluded;
            this.start = start;
            this.end = end;
        }

        public Double(Range2D range) {
            startIncl = range.isStartIncluded();
            endIncl = range.isEndIncluded();
            start = range.getStart();
            end = range.getEnd();
        }

        public double getStart() {
            return start;
        }

        public double getEnd() {
            return end;
        }

        public boolean isStartIncluded() {
            return startIncl;
        }

        public boolean isEndIncluded() {
            return endIncl;
        }

        public double getMin() {
            return Math.min(start, end);
        }

        public double getMax() {
            return Math.max(start, end);
        }

        public double getSpan() {
            return Math.abs(end - start);
        }

        public boolean isInverted() {
            return start > end;
        }

        public boolean equals(Object obj) {
            if (obj instanceof Double) {
                Double p2d = (Double) obj;
                return start == p2d.start && startIncl == p2d.startIncl
                        && end == p2d.end && endIncl == p2d.endIncl;
            }
            return false;
        }

        /**
         * Returns the hash code for this <code>Range2D</code>.
         * 
         * @return a hash code for this <code>Range2D</code>.
         */
        public int hashCode() {
            long bits = java.lang.Double.doubleToLongBits(start);
            bits ^= java.lang.Double.doubleToLongBits(end) * 31;
            return (((int) bits) ^ ((int) (bits >> 32)));
        }

        public String toString() {
            return "Range2D.Double" + ((startIncl) ? "[" : "(") + start + ", "
                    + end + ((endIncl) ? "]" : ")");
        }

        @Override
        public Double invert() {
            return new Double(end, endIncl, start, startIncl);
        }

        @Override
        public Range2D intersect(Range2D range) {
            double min, max;
            boolean minincl, maxincl;
            if (getMin() == range.getMin()) {
                min = getMin();
                minincl = isMinIncluded() && range.isMinIncluded();
            } else if (getMin() > range.getMin()) {
                min = getMin();
                minincl = isMinIncluded();
            } else {
                min = range.getMin();
                minincl = range.isMinIncluded();
            }
            if (getMax() == range.getMax()) {
                max = getMax();
                maxincl = isMaxIncluded() && range.isMaxIncluded();
            } else if (getMax() < range.getMax()) {
                max = getMax();
                maxincl = isMaxIncluded();
            } else {
                max = range.getMax();
                maxincl = range.isMaxIncluded();
            }

            if (min > max) {
                return null;
            }
            if (min == max && !(minincl && maxincl)) {
                return null;
            }

            if (!isInverted()) {
                return new Double(min, minincl, max, maxincl);
            } else {
                return new Double(max, maxincl, min, minincl);
            }
        }

        @Override
        public Range2D union(Range2D range) {
            if (isEmpty()) {
                return range;
            }
            if (range.isEmpty()) {
                return this;
            }

            double min, max;
            boolean minincl, maxincl;
            if (getMin() == range.getMin()) {
                min = getMin();
                minincl = isMinIncluded() || range.isMinIncluded();
            } else if (getMin() < range.getMin()) {
                min = getMin();
                minincl = isMinIncluded();
            } else {
                min = range.getMin();
                minincl = range.isMinIncluded();
            }
            if (getMax() == range.getMax()) {
                max = getMax();
                maxincl = isMaxIncluded() || range.isMaxIncluded();
            } else if (getMax() > range.getMax()) {
                max = getMax();
                maxincl = isMaxIncluded();
            } else {
                max = range.getMax();
                maxincl = range.isMaxIncluded();
            }

            if (min > max) {
                return null;
            }
            if (min == max && !(minincl && maxincl)) {
                return null;
            }

            if (!isInverted()) {
                return new Double(min, minincl, max, maxincl);
            } else {
                return new Double(max, maxincl, min, minincl);
            }
        }

        @Override
        public boolean isEmpty() {
            return start == end && !(startIncl && endIncl);
        }

    }

    public static class Long extends Range2D implements Serializable {

        private static final long serialVersionUID = 1L;

        public final long start;

        public final long end;

        public Long() {
            this(0, 0);
        }

        /**
         * Construct Range2D.Long with start and end.
         * 
         * @param start
         *            first value
         * @param end
         *            last value
         */
        public Long(long start, long end) {
            this.start = start;
            this.end = end;
        }

        public Long(Range2D range) {
            if (!range.isInverted()) {
                long start = (long) Math.ceil(range.getStart());
                long end = (long) Math.floor(range.getEnd());
                if (start == range.getStart() && !range.isStartIncluded()) {
                    start++;
                }
                if (end == range.getEnd() && !range.isEndIncluded()) {
                    end--;
                }
                this.start = start;
                this.end = end;
            } else {
                long start = (long) Math.floor(range.getStart());
                long end = (long) Math.ceil(range.getStart());
                if (start == range.getStart() && !range.isStartIncluded()) {
                    start--;
                }
                if (end == range.getEnd() && !range.isEndIncluded()) {
                    end++;
                }
                this.start = start;
                this.end = end;
            }
        }

        @Override
        public boolean isStartIncluded() {
            return true;
        }

        @Override
        public boolean isEndIncluded() {
            return true;
        }

        public double getStart() {
            return start;
        }

        public double getEnd() {
            return end;
        }

        public double getMin() {
            return Math.min(start, end);
        }

        public double getMax() {
            return Math.max(start, end);
        }

        public double getSpan() {
            return Math.abs(end - start);
        }

        public boolean isInverted() {
            return start > end;
        }

        public boolean equals(Object obj) {
            if (obj instanceof Long) {
                Long p2d = (Long) obj;
                return start == p2d.start && end == p2d.end;
            }
            return false;
        }

        /**
         * Returns the hash code for this <code>Range2D.Long</code>.
         * 
         * @return a hash code for this <code>Range2D.Long</code>.
         */
        public int hashCode() {
            long bits = start;
            bits ^= end * 31;
            return (((int) bits) ^ ((int) (bits >> 32)));
        }

        public String toString() {
            return "Range2D.Long" + "[" + start + ", " + end + "]";
        }

        @Override
        public Long invert() {
            return new Long(end, start);
        }

        @Override
        public Range2D intersect(Range2D range) {
            long min, max;
            long lmin, lmax;
            Long lrange = (range instanceof Long) ? (Long) range : new Long(
                    range);
            if (!lrange.isInverted()) {
                lmin = lrange.start;
                lmax = lrange.end;
            } else {
                lmin = lrange.end;
                lmax = lrange.start;
            }
            if (!isInverted()) {
                min = Math.max(start, lmin);
                max = Math.min(end, lmax);
            } else {
                min = Math.max(end, lmin);
                max = Math.min(start, lmax);
            }
            if (min > max) {
                return null;
            } else if (!isInverted()) {
                return new Long(min, max);
            } else {
                return new Long(max, min);
            }
        }

        @Override
        public Range2D union(Range2D range) {
            long min, max;
            long lmin, lmax;
            Long lrange = (range instanceof Long) ? (Long) range : new Long(
                    range);
            if (!lrange.isInverted()) {
                lmin = lrange.start;
                lmax = lrange.end;
            } else {
                lmin = lrange.end;
                lmax = lrange.start;
            }
            if (!isInverted()) {
                min = Math.min(start, lmin);
                max = Math.max(end, lmax);
            } else {
                min = Math.min(end, lmin);
                max = Math.max(start, lmax);
            }
            if (min > max) {
                return null;
            } else if (!isInverted()) {
                return new Long(min, max);
            } else {
                return new Long(max, min);
            }
        }

        @Override
        public boolean isEmpty() {
            return false;
        }

    }

    public abstract boolean isEmpty();

    public abstract boolean isStartIncluded();

    public abstract boolean isEndIncluded();

    public boolean isMinIncluded() {
        return (!isInverted()) ? isStartIncluded() : isEndIncluded();
    }

    public boolean isMaxIncluded() {
        return (!isInverted()) ? isEndIncluded() : isStartIncluded();
    }

    public boolean contains(double v) {
        if (!isInverted()) {
            return (getStart() < v || (getStart() == v && isStartIncluded()))
                    && (v < getEnd() || (v == getEnd() && isEndIncluded()));
        } else {
            return (getEnd() < v || (getEnd() == v && isEndIncluded()))
                    && (v < getStart() || (v == getStart() && isStartIncluded()));
        }
    }

    public abstract double getStart();

    public abstract double getEnd();

    public abstract double getMin();

    public abstract double getMax();

    public abstract double getSpan();

    public abstract boolean isInverted();

    public abstract Range2D intersect(Range2D range);

    public abstract Range2D union(Range2D range);

    public abstract Range2D invert();

    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    /**
     * Create a copy of <code>Range2D</code> object.
     */
    public Range2D copy() {
        try {
            return (Range2D) clone();
        } catch (CloneNotSupportedException e) {
            throw new Error("Clone Not Supported");
        }
    }

}
