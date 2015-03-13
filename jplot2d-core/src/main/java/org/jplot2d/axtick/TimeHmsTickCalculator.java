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

import org.jplot2d.axtick.TimeHmsInterval.Unit;

import java.lang.reflect.Array;
import java.util.Locale;

/**
 * This calculator works on seconds, and accept a TickUnitConverter, in case of the input data is not in seconds.
 *
 * @author Jingjing Li
 */
public class TimeHmsTickCalculator extends AbstractLinearTickCalculator {

    private final TickUnitConverter tuc;

    public TimeHmsTickCalculator(TickUnitConverter tuf) {
        if (tuf == null) {
            this.tuc = TickUnitConverter.IDENTITY;
        } else {
            this.tuc = tuf;
        }
    }

    protected TimeHmsInterval[] calcCandidateInterval(double rough) {
        double itv = tuc.convertD2T(rough);
        TimeHmsInterval itvA, itvB;
        if (itv < Unit.SECOND.time) {
            int expn = (int) Math.floor(Math.log10(itv));
            double scale = Math.pow(10, expn);
            /* 1 <= rough/scale < 10 */
            double coeff = itv / scale;
            if (coeff < 2) {
                itvA = new TimeHmsInterval(tuc, 1, expn);
                itvB = new TimeHmsInterval(tuc, 2, expn);
            } else if (coeff < 5) {
                itvA = new TimeHmsInterval(tuc, 2, expn);
                itvB = new TimeHmsInterval(tuc, 5, expn);
            } else {
                itvA = new TimeHmsInterval(tuc, 5, expn);
                itvB = new TimeHmsInterval(tuc, 1, expn + 1);
            }
        } else if (itv < Unit.SECOND.time * 2) {
            itvA = new TimeHmsInterval(tuc, Unit.SECOND, 1);
            itvB = new TimeHmsInterval(tuc, Unit.SECOND, 2);
        } else if (itv < Unit.SECOND.time * 5) {
            itvA = new TimeHmsInterval(tuc, Unit.SECOND, 2);
            itvB = new TimeHmsInterval(tuc, Unit.SECOND, 5);
        } else if (itv < Unit.SECOND.time * 10) {
            itvA = new TimeHmsInterval(tuc, Unit.SECOND, 5);
            itvB = new TimeHmsInterval(tuc, Unit.SECOND, 10);
        } else if (itv < Unit.SECOND.time * 15) {
            itvA = new TimeHmsInterval(tuc, Unit.SECOND, 10);
            itvB = new TimeHmsInterval(tuc, Unit.SECOND, 15);
        } else if (itv < Unit.SECOND.time * 30) {
            itvA = new TimeHmsInterval(tuc, Unit.SECOND, 15);
            itvB = new TimeHmsInterval(tuc, Unit.SECOND, 30);
        } else if (itv < Unit.MINUTE.time) {
            itvA = new TimeHmsInterval(tuc, Unit.SECOND, 30);
            itvB = new TimeHmsInterval(tuc, Unit.MINUTE, 1);
        } else if (itv < Unit.MINUTE.time * 2) {
            itvA = new TimeHmsInterval(tuc, Unit.MINUTE, 1);
            itvB = new TimeHmsInterval(tuc, Unit.MINUTE, 2);
        } else if (itv < Unit.MINUTE.time * 5) {
            itvA = new TimeHmsInterval(tuc, Unit.MINUTE, 2);
            itvB = new TimeHmsInterval(tuc, Unit.MINUTE, 5);
        } else if (itv < Unit.MINUTE.time * 10) {
            itvA = new TimeHmsInterval(tuc, Unit.MINUTE, 5);
            itvB = new TimeHmsInterval(tuc, Unit.MINUTE, 10);
        } else if (itv < Unit.MINUTE.time * 15) {
            itvA = new TimeHmsInterval(tuc, Unit.MINUTE, 10);
            itvB = new TimeHmsInterval(tuc, Unit.MINUTE, 15);
        } else if (itv < Unit.MINUTE.time * 30) {
            itvA = new TimeHmsInterval(tuc, Unit.MINUTE, 15);
            itvB = new TimeHmsInterval(tuc, Unit.MINUTE, 30);
        } else if (itv < Unit.HOUR.time) {
            itvA = new TimeHmsInterval(tuc, Unit.MINUTE, 30);
            itvB = new TimeHmsInterval(tuc, Unit.HOUR, 1);
        } else if (itv < Unit.HOUR.time * 2) {
            itvA = new TimeHmsInterval(tuc, Unit.HOUR, 1);
            itvB = new TimeHmsInterval(tuc, Unit.HOUR, 2);
        } else if (itv < Unit.HOUR.time * 3) {
            itvA = new TimeHmsInterval(tuc, Unit.HOUR, 2);
            itvB = new TimeHmsInterval(tuc, Unit.HOUR, 3);
        } else if (itv < Unit.HOUR.time * 6) {
            itvA = new TimeHmsInterval(tuc, Unit.HOUR, 3);
            itvB = new TimeHmsInterval(tuc, Unit.HOUR, 6);
        } else if (itv < Unit.HOUR.time * 12) {
            itvA = new TimeHmsInterval(tuc, Unit.HOUR, 6);
            itvB = new TimeHmsInterval(tuc, Unit.HOUR, 12);
        } else if (itv < Unit.HOUR.time * 24) {
            itvA = new TimeHmsInterval(tuc, Unit.HOUR, 12);
            itvB = new TimeHmsInterval(tuc, Unit.HOUR, 24);
        } else {
            itvA = new TimeHmsInterval(tuc, Unit.HOUR, 24);
            itvB = new TimeHmsInterval(tuc, Unit.HOUR, 24);
        }

        return new TimeHmsInterval[]{itvA, itvB};
    }

    public void calcValuesByTickNumber(int tickNumber, int minorTickNumber) {
        TimeHmsInterval intv = (TimeHmsInterval) calcInterval(tickNumber);

        if (minorTickNumber == AUTO_MINORTICK_NUMBER) {
            if (intv.getCoefficient() == 1 && (intv.getUnit() == Unit.MINUTE || intv.getUnit() == Unit.HOUR)) {
                minorNumber = 0;
            } else {
                minorNumber = calcMinorNumber(intv.getCoefficient(), 3);
            }
        } else {
            minorNumber = minorTickNumber;
        }
        calcValues(interval, 0, minorNumber);
    }

    @Override
    protected int calcMinorNumber(double interval) {

        double itv = tuc.convertD2T(interval);

		/* the interval with only the highest 1 digit */
        int s1dInterval;

        if (itv < Unit.SECOND.time) {
            int mag = (int) Math.floor(Math.log10(itv));
            double coefficient = itv / Math.pow(10, mag);
            double s1dcoef = Math.round(coefficient);
			/*
			 * if the coefficient contains more than 1 significant digit, ignore the minor number
			 */
            if (Math.abs(s1dcoef / coefficient - 1) > DOUBLE_PRECISION_TOLERANCE) {
                s1dInterval = 0;
            } else {
                s1dInterval = (int) s1dcoef;
            }
        } else if (itv < Unit.MINUTE.time) {
            double secs = itv;
            double s1dcoef = Math.round(secs);
			/*
			 * if the coefficient contains more than 1 significant digit, ignore the minor number
			 */
            if (Math.abs(s1dcoef / secs - 1) > DOUBLE_PRECISION_TOLERANCE) {
                s1dInterval = 0;
            } else {
                s1dInterval = (int) s1dcoef;
            }
        } else if (itv < Unit.HOUR.time) {
            double minutes = itv / Unit.MINUTE.time;
            double s1dcoef = Math.round(minutes);
			/*
			 * if the coefficient contains more than 1 significant digit, ignore the minor number
			 */
            if (Math.abs(s1dcoef / minutes - 1) > DOUBLE_PRECISION_TOLERANCE) {
                s1dInterval = 0;
            } else {
                s1dInterval = (int) s1dcoef;
            }
        } else {
            double hours = itv / Unit.HOUR.time;
            double s1dcoef = Math.round(hours);
			/*
			 * if the coefficient contains more than 1 significant digit, ignore the minor number
			 */
            if (Math.abs(s1dcoef / hours - 1) > DOUBLE_PRECISION_TOLERANCE) {
                s1dInterval = 0;
            } else {
                s1dInterval = (int) s1dcoef;
            }
        }

        if (s1dInterval == 0) {
            return 0;
        } else {
            return calcMinorNumber(s1dInterval, 3);
        }

    }

    public TimeHmsFormat calcLabelTextFormat(Object values) {
        return new TimeHmsFormat(tuc, calcSigFraDigits(values));
    }

    /**
     * Calculate the digit number after the second point.
     *
     * @param values the values
     * @return the digit number after the second point
     */
    private int calcSigFraDigits(Object values) {
		/* number of fraction digits */
        int maxFractionDigits = 0;
        for (int i = 0; i < Array.getLength(values); i++) {
            double vi = Array.getDouble(values, i);
            if (vi == 0) {
                continue;
            }

            double v = Math.abs(tuc.convertD2T(vi));

            int mag = (int) Math.floor(Math.log10(v));

            double scale = Math.pow(10, mag);
            double coefficient = v / scale;
            String s = String.format((Locale) null, "%.14f", coefficient);
			/* number of significant digits, eg. the pp1 for 1.1 is 2 */
            int pp1 = lastNon0Idx(s);
            if (maxFractionDigits < pp1 - mag - 1) {
                maxFractionDigits = pp1 - mag - 1;
            }
        }

        return maxFractionDigits;
    }

    public String calcLabelFormatString(Object values) {
        return "";
    }

    public String getLabelFormate() {
        return "";
    }

}
