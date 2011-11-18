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
package org.jplot2d.axtick;

import java.text.Format;
import java.util.Formatter;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jplot2d.tex.MathElement;
import org.jplot2d.tex.TeXMathUtils;

/**
 * Static methods to format tick labels.
 * 
 * @author Jingjing Li
 * 
 */
public class TickUtils {

	/**
	 * Find a proper minor tick number according to the given major interval and proposal minor tick
	 * number.
	 * 
	 * @param majorInterval
	 * @param minorNumber
	 *            the proposed minor tick number
	 * @return the proper minor tick number
	 */
	public static int calcMinorNumber(int majorInterval, int minorNumber) {

		/* significant digits of interval */
		int sdi = majorInterval;
		/* the scale in 10^n */
		int scale = 1;
		while (sdi > 10 && sdi % 10 == 0) {
			sdi /= 10;
			scale *= 10;
		}

		int minorInterval = sdi / (minorNumber + 1);
		if (minorInterval == 0) {
			minorInterval++;
		}
		int itvA = 1, itvB = sdi;
		for (int itv = minorInterval; itv > 1; itv--) {
			if (sdi % itv == 0) {
				itvA = itv;
				break;
			}
		}
		for (int itv = minorInterval + 1; itv < sdi; itv++) {
			if (sdi % itv == 0) {
				itvB = itv;
				break;
			}
		}
		int tickNumA = sdi / itvA - 1;
		int tickNumB = sdi / itvB - 1;

		if (tickNumB == 0) {
			return tickNumA;
		}
		/* tickNumB < tickNumber < tickNumA, tend to less */
		if (tickNumA - minorNumber < minorNumber - tickNumB) {
			return tickNumA;
		} else {
			return tickNumB;
		}
	}

	public static String calcLabelFormatStr(double[] values) {
		int minMag = Integer.MAX_VALUE;
		int maxMag = Integer.MIN_VALUE;
		/* number of significant digits */
		int maxPrec = 0;
		/* number of fraction digits */
		int maxFractionDigits = 0;
		for (int i = 0; i < values.length; i++) {
			if (values[i] == 0) {
				continue;
			}

			double v = Math.abs(values[i]);

			int mag = (int) Math.floor(Math.log10(v));
			if (minMag > mag) {
				minMag = mag;
			}
			if (maxMag < mag) {
				maxMag = mag;
			}

			double scale = Math.pow(10, mag);
			double coefficient = v / scale;
			String s = String.format((Locale) null, "%.14f", coefficient);
			/* number of significant digits, eg. the pp1 for 1.1 is 2 */
			int pp1 = lastNon0Idx(s);
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

	public static String calcLabelFormatStr(long[] values) {
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
			int pp1 = lastNon0Idx(s) + 1;
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

	/**
	 * Returns the idx that last non-zero. -1 means all 0
	 * 
	 * @param s
	 * @return the idx
	 */
	private static int lastNon0Idx(String s) {
		for (int i = s.length() - 1; i >= 0; i--) {
			if (s.charAt(i) != '0') {
				return i;
			}
		}
		return -1;
	}

	/**
	 * Convert Format Conversion 'm' to 'e'. If the the input format is not 'm' conversion, the
	 * string is return untouched.
	 * 
	 * @param format
	 *            the format string.
	 * @return the new format string.
	 */
	public static String convFCm2e(String format) {
		int mathConversionIdx = format.indexOf("m", 1);
		if (mathConversionIdx != -1) {
			StringBuffer newFormat = new StringBuffer(format);
			newFormat.setCharAt(mathConversionIdx, 'e');
			format = newFormat.toString();
		}
		return format;
	}

	// %[argument_index$][flags][width][.precision]m_conversion
	private static Pattern formatPattern = Pattern
			.compile("(%(\\d+\\$)?([-#+ 0,(\\<]*)?(\\d+)?(\\.\\d+)?)(m)");

	private static Pattern eResultPattern = Pattern
			.compile("-->>([+-]?[0-9](?:[.][0-9]+)?)e([+-]?([0-9])+)<<--");

	/**
	 * Returns a formatted string using the specified format string on the given value.
	 * 
	 * @param format
	 *            A format string
	 * @param v
	 *            the value to be formatted.
	 * @return A formatted string.
	 */
	public static MathElement format(String format, Object v) {

		boolean hasMConversion = false;
		StringBuffer convFormatBuffer = new StringBuffer();
		Matcher formatMatcher = formatPattern.matcher(format);
		while (formatMatcher.find()) {
			hasMConversion = true;
			formatMatcher.appendReplacement(convFormatBuffer, "-->>$1e<<--");
		}
		formatMatcher.appendTail(convFormatBuffer);

		String texString;
		if (!hasMConversion) {
			texString = new Formatter(Locale.US).format(format, v).toString();
		} else {
			format = convFormatBuffer.toString();
			String intermediatResult = new Formatter(Locale.US).format(format, v).toString();

			int lastEnd = 0;
			Matcher resultMatcher = eResultPattern.matcher(intermediatResult);
			StringBuffer finalResultBuffer = new StringBuffer();
			while (resultMatcher.find()) {
				String s = intermediatResult.substring(lastEnd, resultMatcher.start());
				String a = resultMatcher.group(1);
				String m = resultMatcher.group(2);

				finalResultBuffer.append(s);

				boolean mathPart = false;
				if (m.startsWith("+")) {
					m = m.substring(1);
				}
				int mv = Integer.parseInt(m);
				if (mv != 0) {
					mathPart = true;
				}

				if (!mathPart) {
					finalResultBuffer.append(a);
				} else {
					if (!a.equals("1")) {
						finalResultBuffer.append(a);
						finalResultBuffer.append(" ");
					}
					finalResultBuffer.append("$10^{");
					finalResultBuffer.append(mv);
					finalResultBuffer.append("}$");
				}

				lastEnd = resultMatcher.end();
			}

			texString = finalResultBuffer.toString();
		}

		if (texString.indexOf('$') == -1) {
			return new MathElement.Mtext(texString);
		} else {
			return TeXMathUtils.parseText(texString);
		}
	}

	public static MathElement format(Format format, Object v) {
		String s = format.format(v);
		if (s.indexOf('$') == -1) {
			return new MathElement.Mtext(s);
		} else {
			return TeXMathUtils.parseText(s);
		}
	}

}
