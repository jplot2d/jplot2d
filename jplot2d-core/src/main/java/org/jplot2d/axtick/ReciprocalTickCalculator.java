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

import java.text.Format;

import javax.annotation.Nonnull;

/**
 * A TickCalculator for reciprocal. A reciprocal ticks contains a primary tick zone,
 * with a end tick value is multiple of the other end. The reasonable multiplying factor is 4.
 *
 * @author Jingjing Li
 */
public class ReciprocalTickCalculator extends DoubleTickCalculator {

    static final int LINEAR_MULTIPLE_THRESHOLD = 2;

    /**
     * Always be positive
     */
    protected double interval;

    protected int minorNumber;

    protected double[] tickValues;

    protected double[] minorValues;

    public void calcValuesByTickNumber(int tickNumber, int minorTickNumber) {
        calcReciprocal(minorTickNumber);
        if (tickValues == null) {
            calcAsLinear(tickNumber, minorTickNumber);
        }
    }

    @Override
    public void calcValuesByTickInterval(double interval, double offset, int minorTickNumber) {
        calcReciprocal(minorTickNumber);
        if (tickValues == null) {
            calcAsLinear(interval, offset, minorTickNumber);
        }
    }

    private void calcReciprocal(int minorTickNumber) {
        double farv;
        double mf = 0;
        double absStart = Math.abs(start);
        double absEnd = Math.abs(end);
        if (Math.signum(start) == Math.signum(end)) {
            mf = (absStart > absEnd) ? absStart / absEnd : absEnd / absStart;
            if (mf < LINEAR_MULTIPLE_THRESHOLD) {
                tickValues = null;
                minorValues = null;
                return;
            } else {
                if (absStart > absEnd) {
                    farv = end;
                } else {
                    farv = start;
                }
            }
        } else if (Double.isNaN(start)) {
            farv = end;
        } else if (Double.isNaN(end)) {
            farv = start;
        } else {
            if (absStart > absEnd) {
                farv = end;
            } else {
                farv = start;
            }
        }

        if (mf == 0 || mf > 4) {
            mf = 4;
        }
        DoubleTickCalculator tc = LinearTickAlgorithm.getInstance().createCalculator();
        tc.setRange(farv, farv * mf);
        tc.calcValuesByTickNumber(3, minorTickNumber);
        this.minorNumber = tc.getMinorNumber();
        this.tickValues = tc.getValues();
        this.minorValues = tc.getMinorValues();

    }

    private void calcAsLinear(int tickNumber, int minorTickNumber) {
        DoubleTickCalculator tc = LinearTickAlgorithm.getInstance().createCalculator();
        tc.setRange(start, end);
        tc.calcValuesByTickNumber(tickNumber, minorTickNumber);
        this.minorNumber = tc.getMinorNumber();
        this.tickValues = tc.getValues();
        this.minorValues = tc.getMinorValues();
    }

    private void calcAsLinear(double interval, double offset, int minorTickNumber) {
        DoubleTickCalculator tc = LinearTickAlgorithm.getInstance().createCalculator();
        tc.setRange(start, end);
        tc.calcValuesByTickInterval(interval, offset, minorTickNumber);
        this.minorNumber = tc.getMinorNumber();
        this.tickValues = tc.getValues();
        this.minorValues = tc.getMinorValues();
    }

    @Override
    public double getInterval() {
        return interval;
    }

    @Override
    public int getMinorNumber() {
        return minorNumber;
    }

    @Override
    public double[] getValues() {
        return tickValues;
    }

    @Override
    public double[] getMinorValues() {
        return minorValues;
    }

    public Format calcLabelTextFormat(@Nonnull Object canonicalValues) {
        return null;
    }

    public String getLabelFormat() {
        return calcLabelFormatString(getValues());
    }

}
