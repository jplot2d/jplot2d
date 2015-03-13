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

import org.jplot2d.util.Range;

import java.util.Locale;

/**
 * A calculator to calculate tick values and minor tick values
 *
 * @author Jingjing Li
 */
public abstract class LongTickCalculator extends TickCalculator {

    protected long lo, hi;

    protected boolean inverted;

    public Range getRange() {
        if (!inverted) {
            return new Range.Long(lo, hi);
        } else {
            return new Range.Long(hi, lo);
        }
    }

    public void setRange(Range range) {
        if (range instanceof Range.Long) {
            long start = ((Range.Long) range).start;
            long end = ((Range.Long) range).end;
            this.setRange(start, end);
        } else {
            double start = range.getStart();
            double end = range.getEnd();
            if (Double.isNaN(start) || Double.isInfinite(start) || Double.isNaN(end) || Double.isInfinite(end)) {
                throw new IllegalArgumentException("Range cannot start or end on NaN/Infinite value");
            }
            if (start < end) {
                this.setRange((long) Math.floor(start), (long) Math.ceil(end));
            } else {
                this.setRange((long) Math.ceil(start), (long) Math.floor(end));
            }
        }
    }

    protected void setRange(long start, long end) {
        boolean inverted = start > end;

        if (!inverted) {
            lo = start;
            hi = end;
        } else {
            lo = end;
            hi = start;
        }
    }

    public abstract long[] getValues();

    public abstract long[] getMinorValues();

    public void calcValuesByTickInterval(double interval, double offset, int minorTickNumber) {
        calcValuesByTickInterval(Math.round(interval), Math.round(offset), minorTickNumber);
    }

    public abstract void calcValuesByTickInterval(long interval, long offset, int minorTickNumber);

    public int[] getInRangeValuesIdx(Object values) {
        if (values instanceof long[]) {
            return getInRangeValuesIdx((long[]) values);
        }
        throw new IllegalArgumentException();
    }

    private int[] getInRangeValuesIdx(long[] v) {
        long expandLo = lo;
        long expandHi = hi;

        int[] m = new int[v.length];
        int j = 0;
        for (int i = 0; i < v.length; i++) {
            if (expandLo <= v[i] && v[i] <= expandHi) {
                m[j++] = i;
            }
        }

        if (j == v.length) {
            // all in range selection
            return m;
        } else {
            int[] result = new int[j];
            System.arraycopy(m, 0, result, 0, j);
            return result;
        }
    }

    public String calcLabelFormatString(Object valueArray) {

        long[] values = (long[]) valueArray;

        int maxMag = Integer.MIN_VALUE;
        /* number of significant digits */
        int maxPrec = 0;

        for (int i = 0; i < values.length; i++) {
            if (values[i] == 0) {
                continue;
            }

            long v = Math.abs(values[i]);

            int mag = (int) Math.floor(Math.log10(v));
            if (maxMag < mag) {
                maxMag = mag;
            }

            String s = String.format((Locale) null, "%d", v);
			/* number of significant digits. eg. pp1 for 1100 is 2 */
            int pp1 = 0;
            for (int ci = s.length() - 1; ci >= 0; ci--) {
                if (s.charAt(ci) != '0') {
                    pp1 = ci + 1;
                    break;
                }
            }

            if (maxPrec < pp1) {
                maxPrec = pp1;
            }
        }

		/* the number of 0 we can save */
        int n0 = maxMag - maxPrec + 1;
        if (n0 < 4 && maxMag < 6) {
            return "%d";
        } else {
            return "%." + (maxPrec - 1) + "m";
        }

    }

}
