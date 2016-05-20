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
 * Format a value in seconds to HH:MM:SS.nnnn format.
 *
 * @author Jingjing Li
 */
public class TimeHmsFormat extends Format {

    private static final long serialVersionUID = 1L;

    private final TickUnitConverter tuf;

    private final int fraDigits;

    /**
     * Construct a TimeHmsFormat with the given fragment digits.
     *
     * @param fraDigits the fragment digits
     */
    public TimeHmsFormat(int fraDigits) {
        this(TickUnitConverter.IDENTITY, fraDigits);
    }

    public TimeHmsFormat(TickUnitConverter tuf, int fraDigits) {
        this.tuf = tuf;
        this.fraDigits = fraDigits;
    }

    @Override
    public StringBuffer format(Object number, @Nonnull StringBuffer toAppendTo, @Nonnull FieldPosition pos) {
        if (number instanceof Number) {
            double v = tuf.convertD2T(((Number) number).doubleValue());
            return format(v, toAppendTo);
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

        int hour = (int) (number / 3600);
        number -= hour * 3600;
        int minute = (int) (number / 60);
        number -= minute * 60;
        int second = (int) number;
        number -= second;

        StringBuilder sb = new StringBuilder();
        new Formatter(sb).format("%." + fraDigits + "f", number);
        if (sb.charAt(0) == '1') {
            second++;
            if (second == 60) {
                second = 0;
                minute++;
                if (minute == 60) {
                    minute = 0;
                    hour++;
                }
            }
        }

        if (negative) {
            result.append('-');
        }
        Formatter formatter = new Formatter(result);
        formatter.format("%02d:%02d:%02d", hour, minute, second);
        if (fraDigits > 0) {
            // remove the digit before dot
            result.append(sb, 1, sb.length());
        }

        return result;
    }

    @Override
    public Object parseObject(String source, @Nonnull ParsePosition pos) {
        // not support
        return null;
    }

    public boolean equals(Object obj) {
        return obj instanceof TimeHmsFormat && fraDigits == ((TimeHmsFormat) obj).fraDigits;
    }

}
