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

import java.text.Format;

import javax.annotation.Nonnull;

/**
 * @author Jingjing Li
 */
public class LinearTickCalculator extends AbstractLinearTickCalculator {

    public LinearTickCalculator() {

    }

    protected DoubleInterval[] calcCandidateInterval(double rough) {
        int expn = (int) Math.floor(Math.log10(rough));
        double scale = Math.pow(10, expn);
        /* 1 <= rough/scale < 10 */
        double coeff = rough / scale;
        Int10expn itvA, itvB;
        if (coeff < 2) {
            itvA = new Int10expn(1, expn);
            itvB = new Int10expn(2, expn);
        } else if (coeff < 5) {
            itvA = new Int10expn(2, expn);
            itvB = new Int10expn(5, expn);
        } else {
            itvA = new Int10expn(5, expn);
            itvB = new Int10expn(1, expn + 1);
        }

        return new Int10expn[]{itvA, itvB};
    }

    public void calcValuesByTickNumber(int tickNumber, int minorTickNumber) {
        Int10expn intv = (Int10expn) calcInterval(tickNumber);

        if (minorTickNumber == AUTO_MINOR_TICK_NUMBER) {
            switch (intv.getCoefficient()) {
                case 1: /* the minor interval is 0.2 */
                    minorNumber = 4;
                    break;
                case 2: /* the minor interval is 0.5 */
                    minorNumber = 3;
                    break;
                case 5: /* the minor interval is 1 */
                    minorNumber = 4;
                    break;
                default:
                    throw new java.lang.IllegalStateException();
            }
        } else {
            minorNumber = minorTickNumber;
        }

        calcValues(interval, 0, minorNumber);
    }

    @Override
    protected int calcMinorNumber(double interval) {

        int mag = (int) Math.floor(Math.log10(interval));
        double coefficient = interval / Math.pow(10, mag);
        double s1dcoef = Math.round(coefficient);
        /*
         * if the coefficient contains more than 1 significant digit, ignore the minor number
		 */
        if (Math.abs(s1dcoef / coefficient - 1) > DOUBLE_PRECISION_TOLERANCE) {
            return 0;
        } else {
            switch ((int) s1dcoef) {
                case 1: /* the minor interval is 0.2 */
                case 10: /* the minor interval is 2 */
                    return 4;
                case 2: /* the minor interval is 0.5 */
                    return 3;
                case 3: /* the minor interval is 1 */
                    return 2;
                case 4: /* the minor interval is 1 */
                    return 3;
                case 5: /* the minor interval is 1 */
                    return 4;
                case 6: /* the minor interval is 2 */
                    return 2;
                case 7: /* the minor interval is 1 */
                    return 6;
                case 8: /* the minor interval is 2 */
                    return 3;
                case 9: /* the minor interval is 3 */
                    return 2;
                default:
                    return 0;
            }
        }
    }

    public String getLabelFormat() {
        return calcLabelFormatString(getValues());
    }

    public Format calcLabelTextFormat(@Nonnull Object canonicalValues) {
        return null;
    }

}
