/*
 * Copyright 2010, 2011 Jingjing Li.
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
package org.jplot2d.util;

import java.util.Locale;

/**
 * @author Jingjing Li
 */
public class NumberUtils {

    /**
     * The number of logical bits in the significand of a <code>double</code> number, including the implicit bit.
     */
    private static final int SIGNIFICAND_WIDTH = 53;

    /**
     * Bias used in representing a <code>double</code> exponent.
     */
    private static final int EXP_BIAS = 1023;

    /**
     * Bit mask to isolate the sign bit of a <code>double</code>.
     */
    private static final long SIGN_BIT_MASK = 0x8000000000000000L;

    /**
     * Bit mask to isolate the exponent field of a <code>double</code>.
     */
    private static final long EXP_BIT_MASK = 0x7FF0000000000000L;

    /**
     * Bit mask to isolate the significand field of a <code>double</code>.
     */
    private static final long SIGNIF_BIT_MASK = 0x000FFFFFFFFFFFFFL;

    /**
     * Returns <code>true</code> if the given values are approximately equal.
     *
     * @param a   the value a
     * @param b   the value b
     * @param bit the number of bits that the different is tolerated.
     * @return <code>true</code> if the given values are approximately equal
     */
    public static boolean approximate(double a, double b, int bit) {
        if (a == b) {
            return true;
        } else if (bit == 0) {
            return false;
        }

        long lba = Double.doubleToLongBits(a);
        long lbb = Double.doubleToLongBits(b);
        if ((lba & SIGN_BIT_MASK) != (lbb & SIGN_BIT_MASK)) {
            return false;
        }
        int expa = (int) (((lba & EXP_BIT_MASK) >> (SIGNIFICAND_WIDTH - 1)) - EXP_BIAS);
        int expb = (int) (((lbb & EXP_BIT_MASK) >> (SIGNIFICAND_WIDTH - 1)) - EXP_BIAS);
        if (expa - expb > 1 || expa - expb < -1) {
            return false;
        }
        long ma = (lba & SIGNIF_BIT_MASK) | 0x10000000000000L;
        long mb = (lbb & SIGNIF_BIT_MASK) | 0x10000000000000L;
        if (expa > expb) {
            ma <<= 1;
        }
        if (expa < expb) {
            mb <<= 1;
        }
        long delta = Math.abs(ma - mb);
        long mask = (1L << bit) - 1L;
        return (delta & ~mask) == 0;
    }

    /**
     * Find a proper format for the given significant digits limit.
     *
     * @param value  the value
     * @param digits significant digits
     * @return the format string for java Formatter
     */
    public static String calcFormatStr(double value, int digits) {

        if (value == 0 || Double.isNaN(value) || Double.isInfinite(value)) {
            return "%.0f";
        }

		/* number of significant digits */
        int maxPrec = 0;
        /* number of fraction digits */
        int maxFractionDigits = 0;

        double v = Math.abs(value);

        String s = String.format((Locale) null, "%." + (digits - 1) + "e", v);
        int eidx = s.lastIndexOf('e');
        int ensi = eidx + 1; // the start index of exponent number
        if (s.charAt(ensi) == '+') {
            ensi++;
        }
        int mag = Integer.parseInt(s.substring(ensi));
        /* number of significant digits, eg. the pp1 for 1.1 is 2 */
        int pp1 = -1;
        for (int i = eidx - 1; i >= 0; i--) {
            if (s.charAt(i) != '0') {
                pp1 = i;
                break;
            }
        }

        if (maxPrec < pp1) {
            maxPrec = pp1;
        }
        if (maxFractionDigits < pp1 - mag - 1) {
            maxFractionDigits = pp1 - mag - 1;
        }

		/* the number of 0 we can save */
        int n0;
        if (mag >= 0) {
            n0 = mag - maxPrec + 1;
        } else if (mag <= 0) {
            n0 = -mag;
        } else {
            n0 = 0;
        }

        String format;
        if (n0 < 4 && mag >= -4 && mag < 6) {
            format = "%." + maxFractionDigits + "f";
        } else {
            format = "%." + (maxPrec - 1) + "e";
        }
        return format;

    }

    /**
     * Format the value to avoid the round error. The max precision is 6 significant digits.
     */
    public static String toString(float v) {
        return toString(v, 6);
    }

    /**
     * Format the value to the given significant digits limit. If the limit is 0, to avoid the round error, 6
     * significant digits is applied.
     */
    public static String toString(float v, int digits) {
        if (digits == 0) {
            digits = 6;
        }
        return String.format((Locale) null, calcFormatStr(v, digits), v);
    }

    /**
     * Format the value to avoid the round error. The max precision is 15 significant digits.
     */
    public static String toString(double v) {
        return toString(v, 15);
    }

    /**
     * Format the value to the given significant digits limit. If the limit is 0, to avoid the round error, 15
     * significant digits is applied.
     */
    public static String toString(double v, int digits) {
        if (digits == 0) {
            digits = 15;
        }
        return String.format((Locale) null, calcFormatStr(v, digits), v);
    }

    /**
     * Find a proper format to show the delta digit.
     *
     * @param v     the value
     * @param delta the delta
     * @return a format string for java Formatter
     */
    public static String calcDeltaFormatStr(double v, double delta) {

        if (v == 0 || Double.isNaN(v) || Double.isInfinite(v)) {
            return "%.0f";
        }

        int deltaExp = (int) Math.floor(Math.log10(delta));
        int aExp = (int) Math.floor(Math.log10(Math.abs(v)));
        // Significant digits - 1
        int precision = aExp - deltaExp;

        if (precision >= 0) {
            if (-4 <= aExp && aExp < 6) {
                if (deltaExp >= 0) {
                    return "%.0f";
                } else {
                    return "%." + -deltaExp + "f";
                }
            } else {
                return "%." + precision + "e";
            }
        } else {
            if (-4 <= deltaExp && deltaExp < 0) {
                return "%." + -deltaExp + "f";
            } else {
                return "0";
            }
        }
    }

}
