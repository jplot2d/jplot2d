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
package org.jplot2d.tick;

import java.text.Format;

import org.jplot2d.util.NumberArrayUtils;

/**
 * The Log tick calculate its exponent with linear tick calculator, with this
 * exceptions:
 * <ul>
 * <li>The minimal exponent interval is 1. In this case, the minor tick number
 * is always 9</li>
 * </ul>
 * 
 * 
 * @author Jingjing Li
 * 
 */
class LogTickCalculator extends DoubleTickCalculator implements RangeAdvisor {

	private LinearTickCalculator expCalculator = new LinearTickCalculator();

	private double[] _tickValues;

	private double[] _minorValues;

	public LogTickCalculator() {

	}

	/**
	 * @param start
	 * @param end
	 */
	protected void setRange(double start, double end) {
		if (Double.isNaN(start) || Double.isInfinite(start)
				|| Double.isNaN(end) || Double.isInfinite(end) || start <= 0
				|| end <= 0) {
			throw new IllegalArgumentException("Invalid range [" + start + ","
					+ end + "].");
		}
		super.setRange(start, end);

		double expStart = Math.log10(start);
		double expEnd = Math.log10(end);
		expCalculator.setRange(expStart, expEnd);
	}

	/**
	 * Calculate the nice interval based on the range and proposed tick number.
	 * 
	 * @param start
	 * @param end
	 * @param tickNumber
	 * @return the exponent interval
	 */
	protected int calcInterval(int tickNumber) {
		if (tickNumber <= 0) {
			throw new IllegalArgumentException(
					"tickNumber must be great than zero");
		} else if (tickNumber == 1) {
			tickNumber = 2;
		}

		expCalculator.calcInterval(tickNumber);

		if (expCalculator.getInterval() < 1) {
			return 1;
		} else {
			return (int) expCalculator.getInterval();
		}

	}

	/**
	 * Calculate values on the long exp1 rules.
	 */
	private void calcValuesExp1() {

		boolean inverted = _start > _end;

		double lo, hi;
		if (!inverted) {
			lo = _start;
			hi = _end;
		} else {
			lo = _end;
			hi = _start;
		}

		double expLo = Math.log10(lo);
		expLo -= Math.abs(expLo) * DOUBLE_PRECISION_TOLERANCE;
		double expHi = Math.log10(hi);
		expHi += Math.abs(expHi) * DOUBLE_PRECISION_TOLERANCE;

		/* the 1st and last exponent */
		int expStart = (int) Math.ceil(expLo);
		int expEnd = (int) Math.floor(expHi);

		int length = expEnd - expStart + 1;
		_tickValues = new double[length];

		if (length == 0) {
			/*
			 * we should use linear tick strategy, so calculating _minorValues
			 * does not make sense
			 */
			_minorValues = new double[0];
			return;
		}

		int minorStart = (int) Math.ceil(Math.pow(10, expLo - expStart + 1));
		int minorEnd = (int) Math.floor(Math.pow(10, expHi - expEnd));
		int minorNum = (10 - minorStart) + (length - 1) * 8 + (minorEnd - 1);

		if (minorNum == 0) {
			/* length == 1 in this case */
			_tickValues[0] = Math.pow(10, expStart);
			_minorValues = new double[0];
			return;
		}

		_minorValues = new double[minorNum];
		int mi = 0;
		for (int coef = minorStart; coef <= 9; coef++) {
			_minorValues[mi++] = Math.pow(10, expStart - 1) * coef;
		}
		for (int i = 0;;) {
			int exp = expStart + i;
			double v = Math.pow(10, exp);
			_tickValues[i++] = v;
			if (i == length) {
				break;
			}
			for (int coef = 2; coef <= 9; coef++) {
				_minorValues[mi++] = v * coef;
			}
		}
		for (int coef = 2; coef <= minorEnd; coef++) {
			_minorValues[mi++] = Math.pow(10, expEnd) * coef;
		}

	}

	/**
	 * The caller guarantee the interval >= 1
	 * 
	 * @param interval
	 *            the interval.getExponent() must great than 1
	 * @param minorTickNumber
	 */
	private void calcValues(int expInterval, int minorTickNumber) {
		if (expInterval == 1) {
			calcValuesExp1();
		} else {
			expCalculator.calcValuesByTickInterval(expInterval, 0,
					minorTickNumber);
			_tickValues = new double[expCalculator.getValues().length];
			_minorValues = new double[expCalculator.getMinorValues().length];
			for (int i = 0; i < _tickValues.length; i++) {
				_tickValues[i] = Math.pow(10, expCalculator.getValues()[i]);
			}
			for (int i = 0; i < _minorValues.length; i++) {
				_minorValues[i] = Math.pow(10,
						expCalculator.getMinorValues()[i]);
			}
		}
	}

	private void calcAsLinear(int tickNumber, int minorTickNumber) {
		DoubleTickCalculator tc = LinearTickAlgorithm.getInstance()
				.createCalculator();
		tc.setRange(_start, _end);
		tc.calcValuesByTickNumber(tickNumber, minorTickNumber);
		this._tickValues = tc.getValues();
		this._minorValues = tc.getMinorValues();
	}

	private void calcAsLinear(double interval, double offset,
			int minorTickNumber) {
		DoubleTickCalculator tc = LinearTickAlgorithm.getInstance()
				.createCalculator();
		tc.setRange(_start, _end);
		tc.calcValuesByTickInterval(interval, offset, minorTickNumber);
		this._tickValues = tc.getValues();
		this._minorValues = tc.getMinorValues();
	}

	public void expandRangeByTickNumber(int tickNumber) {

		if (tickNumber <= 0) {
			throw new IllegalArgumentException(
					"tickNumber must be great than zero");
		} else if (tickNumber == 1) {
			tickNumber = 2;
		}

		expCalculator.expandRangeByTickNumber(tickNumber);
		if (expCalculator.getInterval() < 1) {
			expCalculator.expandRangeByTickInterval(1);
		}

		_start = Math.pow(10, expCalculator.getRange().getStart());
		_end = Math.pow(10, expCalculator.getRange().getEnd());

	}

	public void expandRangeByTickInterval(double interval) {

		if (interval < 0) {
			throw new IllegalArgumentException(
					"LOG axis only accept a positive interval.");
		}

		int expi = Math.abs(new Int10expn(interval).getExponent());
		if (expi == 0) {
			expi = 1;
		}

		expCalculator.expandRangeByTickInterval(expi);

		_start = Math.pow(10, expCalculator.getRange().getStart());
		_end = Math.pow(10, expCalculator.getRange().getEnd());

	}

	public void calcValuesByTickNumber(int tickNumber, int minorTickNumber) {

		int expInterval = calcInterval(tickNumber);

		calcValues(expInterval, minorTickNumber);

		if (_tickValues.length == 0) {
			calcAsLinear(tickNumber, minorTickNumber);
			return;
		}

		if (minorTickNumber == 0) {
			_minorValues = new double[0];
		}
		if (_start > _end) {
			_tickValues = NumberArrayUtils.reverse(_tickValues);
			_minorValues = NumberArrayUtils.reverse(_minorValues);
		}
	}

	@Override
	public void calcValuesByTickInterval(double interval, double offset,
			int minorTickNumber) {
		if (interval < 0) {
			throw new IllegalArgumentException(
					"LOG axis only accept a positive interval.");
		}

		int expInterval = new Int10expn(interval).getExponent();
		expInterval = Math.abs(expInterval);
		if (expInterval == 0) {
			expInterval = 1;
		}

		calcValues(expInterval, minorTickNumber);

		if (_tickValues.length == 0
				|| (_tickValues.length == 1 && _minorValues.length == 0)) {
			calcAsLinear(interval, offset, minorTickNumber);
		} else {
			if (minorTickNumber == 0) {
				_minorValues = new double[0];
			}
			if (_start > _end) {
				_tickValues = NumberArrayUtils.reverse(_tickValues);
				_minorValues = NumberArrayUtils.reverse(_minorValues);
			}
		}
	}

	@Override
	public double getInterval() {
		return Math.pow(10, expCalculator.getInterval());
	}

	@Override
	public int getMinorNumber() {
		return (expCalculator.getInterval() == 1) ? 9 : expCalculator
				.getMinorNumber();
	}

	@Override
	public double[] getValues() {
		return _tickValues;
	}

	@Override
	public double[] getMinorValues() {
		return _minorValues;
	}

	public String getLabelFormate() {
		return TickUtils.calcLabelFormatStr(getValues());
	}

	public Format calcAutoLabelTextFormat(Object canonicalValues) {
		return null;
	}

	/**
	 * Calculate a proper format string to format the given values.
	 * 
	 * @param values
	 * @return
	 */
	public String calcAutoLabelFormat(Object values) {
		return TickUtils.calcLabelFormatStr((double[]) values);
	}

}
