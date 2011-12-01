/**
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
 * 
 */
public class NumberUtils {

	/**
	 * The number of logical bits in the significand of a <code>double</code> number, including the
	 * implicit bit.
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

	/**
	 * Find a proper format to avoid the round error for float value. The max precision is 6
	 * significant digits.
	 * 
	 * @param value
	 *            the value
	 * @return the format string for java Formatter
	 */
	public static String calcFormatStr(float value) {

		if (value == 0) {
			return "%.0f";
		}

		/* number of significant digits */
		int maxPrec = 0;
		/* number of fraction digits */
		int maxFractionDigits = 0;

		double v = Math.abs(value);

		String s = String.format(Locale.US, "%.5e", v);
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
	 * Find a proper format to avoid the round error for float value. The max precision is 15
	 * significant digits.
	 * 
	 * @param value
	 *            the value
	 * @return the format string for java Formatter
	 */
	public static String calcFormatStr(double value) {

		if (value == 0) {
			return "%.0f";
		}

		/* number of significant digits */
		int maxPrec = 0;
		/* number of fraction digits */
		int maxFractionDigits = 0;

		double v = Math.abs(value);

		String s = String.format(Locale.US, "%.14e", v);
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

}
