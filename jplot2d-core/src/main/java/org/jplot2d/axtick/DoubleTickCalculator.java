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

import javax.annotation.Nonnull;

/**
 * A calculator to calculate tick values and minor tick values in double precision.
 *
 * @author Jingjing Li
 */
public abstract class DoubleTickCalculator extends TickCalculator {

    protected double start, end;

    public final Range getRange() {
        return new Range.Double(start, end);
    }

    public final void setRange(Range range) {
        this.setRange(range.getStart(), range.getEnd());
    }

    /**
     * Sets the range of data for calculating ticks.
     *
     * @param start the start value
     * @param end   the end value
     */
    protected void setRange(double start, double end) {
        this.start = start;
        this.end = end;
    }

    public abstract double[] getValues();

    public abstract double[] getMinorValues();

    public int[] getInRangeValuesIdx(Object values) {
        if (values instanceof double[]) {
            return getInRangeValuesIdx((double[]) values);
        }
        throw new IllegalArgumentException();
    }

    private int[] getInRangeValuesIdx(double[] v) {
        boolean inverted = start > end;

        double lo, hi;
        if (!inverted) {
            lo = start;
            hi = end;
        } else {
            lo = end;
            hi = start;
        }
        double expandLo = lo - Math.abs(lo) * DOUBLE_PRECISION_TOLERANCE;
        double expandHi = hi + Math.abs(hi) * DOUBLE_PRECISION_TOLERANCE;

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

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public boolean isValidFormat(String format) {
        format = convFCm2e(format);
        try {
            String.format(format, 1d);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    @SuppressWarnings("ConstantConditions")
    public String calcLabelFormatString(@Nonnull Object valueArray) {

        double[] values = (double[]) valueArray;

        int minMag = Integer.MAX_VALUE;
        int maxMag = Integer.MIN_VALUE;
        /* number of significant digits */
        int maxPrec = 0;
        /* number of fraction digits */
        int maxFractionDigits = 0;
        for (double value : values) {
            if (value == 0 || Double.isNaN(value) || Double.isInfinite(value)) {
                continue;
            }

            double v = Math.abs(value);

            String s = String.format((Locale) null, "%.14e", v);
            int eidx = s.lastIndexOf('e');
            int ensi = eidx + 1; // the start index of exponent number
            if (s.charAt(ensi) == '+') {
                ensi++;
            }
            int mag = Integer.parseInt(s.substring(ensi));
            /* number of significant digits, eg. the pp1 for 1.1 is 2 */
            int pp1 = -1;
            for (int ci = eidx - 1; ci >= 0; ci--) {
                if (s.charAt(ci) != '0') {
                    pp1 = ci;
                    break;
                }
            }

            if (minMag > mag) {
                minMag = mag;
            }
            if (maxMag < mag) {
                maxMag = mag;
            }

            if (maxPrec < pp1) {
                maxPrec = pp1;
            }
            if (maxFractionDigits < pp1 - mag - 1) {
                maxFractionDigits = pp1 - mag - 1;
            }
        }

		/* the number of 0 we can save */
        int n0;
        if (minMag >= 0) {
            n0 = maxMag - maxPrec + 1;
        } else if (maxMag <= 0) {
            n0 = -minMag;
        } else {
            n0 = maxMag - minMag;
        }

        if (n0 < 4 && minMag >= -4 && maxMag < 6) {
            return "%." + maxFractionDigits + "f";
        } else {
            return "%." + (maxPrec - 1) + "m";
        }

    }

    /**
     * Convert Format Conversion 'm' to 'e'. If the the input format is not 'm' conversion, the string is return
     * untouched.
     *
     * @param format the format string.
     * @return the new format string.
     */
    public static String convFCm2e(String format) {
        int mathConversionIdx = format.indexOf("m", 1);
        if (mathConversionIdx != -1) {
            StringBuilder newFormat = new StringBuilder(format);
            newFormat.setCharAt(mathConversionIdx, 'e');
            format = newFormat.toString();
        }
        return format;
    }

}
