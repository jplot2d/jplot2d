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

/**
 * A TickCalculator for reciprocal. A reciprocal ticks contains a primary tick zone, with a end tick
 * value is multiple of the other end. The reasonable multiplying factor is 4.
 * 
 * @author Jingjing Li
 * 
 */
public class ReciprocalTickCalculator extends DoubleTickCalculator {

	static final int LINEAR_MULIPLE_THRESHOLD = 2;

	/**
	 * Always be positive
	 */
	protected double _interval;

	protected int _minorNumber;

	protected double[] _tickValues;

	protected double[] _minorValues;

	protected void setRange(double start, double end) {
		/* the start or end may be NaN if the main axis start/end at 0 */
		super.setRange(start, end);
	}

	public void calcValuesByTickNumber(int tickNumber, int minorTickNumber) {
		calcReciprocal(minorTickNumber);
		if (_tickValues == null) {
			calcAsLinear(tickNumber, minorTickNumber);
		}
	}

	@Override
	public void calcValuesByTickInterval(double interval, double offset, int minorTickNumber) {
		calcReciprocal(minorTickNumber);
		if (_tickValues == null) {
			calcAsLinear(interval, offset, minorTickNumber);
		}
	}

	private void calcReciprocal(int minorTickNumber) {
		double farv;
		double mf = 0;
		double absStart = Math.abs(_start);
		double absEnd = Math.abs(_end);
		if (Math.signum(_start) == Math.signum(_end)) {
			mf = (absStart > absEnd) ? absStart / absEnd : absEnd / absStart;
			if (mf < LINEAR_MULIPLE_THRESHOLD) {
				_tickValues = null;
				_minorValues = null;
				return;
			} else {
				if (absStart > absEnd) {
					farv = _end;
				} else {
					farv = _start;
				}
			}
		} else if (Double.isNaN(_start)) {
			farv = _end;
		} else if (Double.isNaN(_end)) {
			farv = _start;
		} else {
			if (absStart > absEnd) {
				farv = _end;
			} else {
				farv = _start;
			}
		}

		if (mf == 0 || mf > 4) {
			mf = 4;
		}
		DoubleTickCalculator tc = LinearTickAlgorithm.getInstance().createCalculator();
		tc.setRange(farv, farv * mf);
		tc.calcValuesByTickNumber(3, minorTickNumber);
		this._minorNumber = tc.getMinorNumber();
		this._tickValues = tc.getValues();
		this._minorValues = tc.getMinorValues();

	}

	private void calcAsLinear(int tickNumber, int minorTickNumber) {
		DoubleTickCalculator tc = LinearTickAlgorithm.getInstance().createCalculator();
		tc.setRange(_start, _end);
		tc.calcValuesByTickNumber(tickNumber, minorTickNumber);
		this._minorNumber = tc.getMinorNumber();
		this._tickValues = tc.getValues();
		this._minorValues = tc.getMinorValues();
	}

	private void calcAsLinear(double interval, double offset, int minorTickNumber) {
		DoubleTickCalculator tc = LinearTickAlgorithm.getInstance().createCalculator();
		tc.setRange(_start, _end);
		tc.calcValuesByTickInterval(interval, offset, minorTickNumber);
		this._minorNumber = tc.getMinorNumber();
		this._tickValues = tc.getValues();
		this._minorValues = tc.getMinorValues();
	}

	@Override
	public double getInterval() {
		return _interval;
	}

	@Override
	public int getMinorNumber() {
		return _minorNumber;
	}

	@Override
	public double[] getValues() {
		return _tickValues;
	}

	@Override
	public double[] getMinorValues() {
		return _minorValues;
	}

	public String calcAutoLabelFormat(Object values) {
		return TickUtils.calcLabelFormatStr((double[]) values);
	}

	public Format calcAutoLabelTextFormat(Object canonicalValues) {
		return null;
	}

	public String getLabelFormate() {
		return TickUtils.calcLabelFormatStr(getValues());
	}

}
