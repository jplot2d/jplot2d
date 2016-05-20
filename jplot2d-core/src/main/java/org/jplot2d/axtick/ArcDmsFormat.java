/*
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

import java.text.FieldPosition;
import java.text.Format;
import java.text.ParsePosition;
import java.util.Formatter;

import javax.annotation.Nonnull;

/**
 * Format a value in degree to DD°MM′SS″nnnn format.
 *
 * @author Jingjing Li
 */
public class ArcDmsFormat extends Format {

    private static final long serialVersionUID = 1L;

    private final int fraDigits;

    /**
     * Construct a ArcDmsFormat with the given fragment digits.
     *
     * @param fraDigits the fragment digits
     */
    public ArcDmsFormat(int fraDigits) {
        this.fraDigits = fraDigits;
    }

    @Override
    public StringBuffer format(Object number, @Nonnull StringBuffer toAppendTo, @Nonnull FieldPosition pos) {
        if (number instanceof Number) {
            return format(((Number) number).doubleValue(), toAppendTo);
        } else {
            throw new IllegalArgumentException("Cannot format given Object as a Number");
        }
    }

    private StringBuffer format(double number, StringBuffer result) {

        boolean negative = false;
        if (number < 0) {
            number = -number;
            negative = true;
        }

        int degree = (int) number;
        number -= degree;
        number *= 60;
        int minute = (int) number;
        number -= minute;
        number *= 60;
        int second = (int) number;
        number -= second;

        StringBuilder sb = new StringBuilder();
        new Formatter(sb).format("%." + fraDigits + "f\u2033", number);
        if (sb.charAt(0) == '1') {
            second++;
            if (second == 60) {
                second = 0;
                minute++;
                if (minute == 60) {
                    minute = 0;
                    degree++;
                }
            }
        }

        if (negative) {
            result.append('-');
        }
        Formatter formatter = new Formatter(result);
        formatter.format("%02d\u00b0%02d\u2032%02d", degree, minute, second);
        if (fraDigits > 0) {
            // remove the digit before dot
            result.append(sb, 1, sb.length());
        } else {
            result.append('\u2033');
        }

        return result;
    }

    @Override
    public Object parseObject(String source, @Nonnull ParsePosition pos) {
        // not support
        return null;
    }

    public boolean equals(Object obj) {
        return obj instanceof ArcDmsFormat && fraDigits == ((ArcDmsFormat) obj).fraDigits;
    }

}
