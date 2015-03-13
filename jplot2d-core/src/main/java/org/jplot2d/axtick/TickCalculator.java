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

import org.jplot2d.tex.MathElement;
import org.jplot2d.tex.TeXMathUtils;
import org.jplot2d.util.Range;

import java.lang.reflect.Array;
import java.text.Format;
import java.util.Formatter;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A calculator to calculate tick values and minor tick values
 *
 * @author Jingjing Li
 */
public abstract class TickCalculator {

    public static final int AUTO_MINORTICK_NUMBER = -1;

    /**
     * The Tolerance error for double computing. doublePrecisionLimit is 0x1.0p-52
     */
    public static final double DOUBLE_PRECISION_TOLERANCE = 0x1.0p-40;

    public abstract Range getRange();

    public abstract void setRange(Range range);

    /**
     * Calculate the tick values by the given tick number and minor ticks number.
     * The minor ticks number is a proposed value, and may be different from the actual minor ticks number returned by {@link #getMinorNumber()}.
     *
     * @param tickNumber      the number of ticks
     * @param minorTickNumber the number of minor ticks.
     *                        if the given number is {@link #AUTO_MINORTICK_NUMBER}, the tick number is automatically chosen.
     */
    public abstract void calcValuesByTickNumber(int tickNumber, int minorTickNumber);

    /**
     * Calculate the tick values by the given interval and minor ticks number.
     * The minor ticks number is a proposed value, and may be different from actual minor ticks number returned by {@link #getMinorNumber()}.
     *
     * @param interval        the interval between 2 tick values
     * @param offset          the offset from the default tick values
     * @param minorTickNumber if the given number is {@link #AUTO_MINORTICK_NUMBER}, the tick number is derived from interval.
     */
    public abstract void calcValuesByTickInterval(double interval, double offset, int minorTickNumber);

    /**
     * Returns the interval between 2 tick values.
     *
     * @return the tick interval
     */
    public abstract double getInterval();

    /**
     * Returns the actual minor tick number.
     *
     * @return the actual minor tick number
     */
    public abstract int getMinorNumber();

    /**
     * Find a proper minor tick number according to the given tick interval and proposal minor tick number.
     *
     * @param majorInterval the tick interval
     * @param minorNumber   the proposed minor tick number
     * @return the proper minor tick number
     */
    protected int calcMinorNumber(int majorInterval, int minorNumber) {

		/* significant digits of interval */
        int sdi = majorInterval;
        /* the scale in 10^n */
        while (sdi > 10 && sdi % 10 == 0) {
            sdi /= 10;
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

    /**
     * Returns an array of tick values.
     *
     * @return the tick values
     */
    public abstract Object getValues();

    /**
     * Returns an array of minor tick values.
     *
     * @return the minor tick values
     */
    public abstract Object getMinorValues();

    /**
     * Return all index of the given values, which the values are in the range of this calculator.
     *
     * @param values the array of values
     * @return a array of index
     */
    public abstract int[] getInRangeValuesIdx(Object values);

    /**
     * Calculate a proper text format to format the labels on given values.
     * A calculator can just returns a text format and do not provide a format string (returns a empty string).
     * If The returned text format is <code>null</code>, a proper format string must be provided.
     *
     * @param values the values to be formatted
     * @return a text format object
     */
    public abstract Format calcLabelTextFormat(Object values);

    /**
     * Calculate a proper format string to format the labels on given values.
     *
     * @param values the values to be formatted
     * @return a format string
     */
    public abstract String calcLabelFormatString(Object values);

    /**
     * Returns a proper format string to format the labels on tick values returned by {@link #getValues()}.
     * Some calculator can derive format from its internal status directly.
     */
    public abstract String getLabelFormate();

    /**
     * Returns <code>true</code> if the given format is valid for this tick calculator
     *
     * @param format the format to be validated
     */
    public abstract boolean isValidFormat(String format);

    // %[argument_index$][flags][width][.precision]m_conversion
    private static final Pattern M_CONVERSION_PATTERN = Pattern
            .compile("(%(\\d+\\$)?([-#+ 0,(\\<]*)?(\\d+)?(\\.\\d+)?)(m)");

    private static final Pattern M_RESULT_PATTERN = Pattern
            .compile("-->>([+-]?[0-9](?:[.][0-9]+)?)e([+-]?([0-9])+)<<--");

    /**
     * Returns a formatted string using the specified format string on the given value array.
     *
     * @param format A format string
     * @param values the value array to be formatted.
     * @return A formatted string.
     */
    public MathElement[] formatValues(String format, Object values) {
        int n = Array.getLength(values);
        MathElement[] labels = new MathElement[n];
        for (int i = 0; i < n; i++) {
            labels[i] = format(format, Array.get(values, i));
        }
        return labels;
    }

    /**
     * Returns a formatted string using the specified format string on the given value.
     *
     * @param format A format string
     * @param v      the value to be formatted.
     * @return A formatted string.
     */
    @SuppressWarnings("resource")
    protected MathElement format(String format, Object v) {

        boolean hasMConversion = false;
        StringBuffer convFormatBuffer = new StringBuffer();
        Matcher formatMatcher = M_CONVERSION_PATTERN.matcher(format);
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
            String intermediateResult = new Formatter(Locale.US).format(format, v).toString();

            int lastEnd = 0;
            Matcher resultMatcher = M_RESULT_PATTERN.matcher(intermediateResult);
            StringBuilder finalResultBuffer = new StringBuilder();
            while (resultMatcher.find()) {
                String s = intermediateResult.substring(lastEnd, resultMatcher.start());
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

    /**
     * Returns an array of MathElement using the specified format on the given value array.
     *
     * @param format A text format
     * @param values the value array to be formatted.
     * @return a array of MathElement
     */
    public MathElement[] formatValues(Format format, Object values) {
        int n = Array.getLength(values);
        MathElement[] labels = new MathElement[n];
        for (int i = 0; i < n; i++) {
            labels[i] = format(format, Array.get(values, i));
        }
        return labels;
    }

    /**
     * Returns a MathElement using the specified format on the given value.
     *
     * @param format A text format
     * @param v      the value to be formatted.
     * @return aMathElement
     */
    protected MathElement format(Format format, Object v) {
        String s = format.format(v);
        if (s.indexOf('$') == -1) {
            return new MathElement.Mtext(s);
        } else {
            return TeXMathUtils.parseText(s);
        }
    }

}
