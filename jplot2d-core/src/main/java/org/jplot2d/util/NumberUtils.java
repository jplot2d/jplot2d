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
/**
 * 
 */
package org.jplot2d.util;

/**
 * @author Jingjing Li
 * 
 */
public class NumberUtils {

    /**
     * The number of logical bits in the significand of a <code>double</code>
     * number, including the implicit bit.
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
     * @param a
     * @param b
     * @param bit
     *            the number of bits that the different is tolerated.
     * @return
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

}
