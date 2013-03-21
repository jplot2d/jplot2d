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

import org.jplot2d.util.NumberArrayUtils;

/**
 * 
 * @author Jingjing Li
 * 
 */
public abstract class AbstractLinearTickCalculator extends DoubleTickCalculator implements RangeAdvisor {

	/**
	 * Always be positive
	 */
	protected double interval;

	protected int minorNumber;

	protected double[] tickValues;

	protected double[] minorValues;

	/**
	 * @param start
	 * @param end
	 */
	protected void setRange(double start, double end) {
		if (Double.isNaN(start) || Double.isInfinite(start) || Double.isNaN(end) || Double.isInfinite(end)) {
			throw new IllegalArgumentException("Invalid range [" + start + "," + end + "].");
		}
		super.setRange(start, end);
	}

	/**
	 * Calculate the nice interval based on the range and proposed tick number. Besides the return value, this method
	 * also update the internal interval variable.
	 * 
	 * @param tickNumber
	 * @return
	 */
	protected DoubleInterval calcInterval(int tickNumber) {

		if (start == end) {
			throw new IllegalArgumentException("The range span must be great than zero");
		}
		if (tickNumber <= 0) {
			throw new IllegalArgumentException("The ticks number must be great than zero");
		} else if (tickNumber == 1) {
			tickNumber = 2;
		}

		double lo, hi;
		if (end > start) {
			lo = start;
			hi = end;
		} else {
			lo = end;
			hi = start;
		}
		tickNumber = Math.abs(tickNumber);

		double rough = (hi - lo) / (tickNumber - 1);
		DoubleInterval[] candidates = calcCandidateInterval(rough);
		DoubleInterval itvA = candidates[0];
		DoubleInterval itvB = candidates[1];

		double expandLo = lo - Math.abs(lo) * DOUBLE_PRECISION_TOLERANCE;
		double expandHi = hi + Math.abs(hi) * DOUBLE_PRECISION_TOLERANCE;
		long iLoA = (long) Math.ceil(expandLo / itvA.doubleValue());
		long iHiA = (long) Math.floor(expandHi / itvA.doubleValue());
		int tickNumA = (int) (iHiA - iLoA + 1);
		long iLoB = (long) Math.ceil(expandLo / itvB.doubleValue());
		long iHiB = (long) Math.floor(expandHi / itvB.doubleValue());
		int tickNumB = (int) (iHiB - iLoB + 1);

		/* tickNumB < tickNumber < tickNumA */
		if (tickNumA - tickNumber <= tickNumber - tickNumB) {
			interval = itvA.doubleValue();
			return itvA;
		} else {
			interval = itvB.doubleValue();
			return itvB;
		}

	}

	/**
	 * Calculate 2 candidate interval around the given rough interval.
	 * 
	 * @param rough
	 *            the rough interval
	 * @return a array contains 2 DoubleInterval objects.
	 */
	protected abstract DoubleInterval[] calcCandidateInterval(double rough);

	public void calcValuesByTickInterval(double interval, double offset, int minorTickNumber) {
		interval = Math.abs(interval);

		if (minorTickNumber == AUTO_MINORTICK_NUMBER) {
			minorNumber = calcMinorNumber(interval);
		} else {
			minorNumber = minorTickNumber;
		}

		calcValues(interval, offset, minorNumber);
	}

	/**
	 * Calculate a proper minor ticks number for AUTO_MINORTICK_NUMBER
	 * 
	 * @param interval
	 * @return
	 */
	protected abstract int calcMinorNumber(double interval);

	/**
	 * @param interval
	 *            must be positive
	 * @param offset
	 * @param minorTickNumber
	 */
	protected void calcValues(double interval, double offset, int minorTickNumber) {

		if (interval == 0 || Double.isNaN(interval))
			throw new IllegalArgumentException("delta cannot be zero or NaN");

		boolean inverted = start > end;

		double lo, hi;
		if (!inverted) {
			lo = start;
			hi = end;
		} else {
			lo = end;
			hi = start;
		}
		double d = interval;

		double expandLo = lo - Math.abs(lo) * DOUBLE_PRECISION_TOLERANCE - offset;
		double expandHi = hi + Math.abs(hi) * DOUBLE_PRECISION_TOLERANCE - offset;
		long iLo = (long) Math.ceil(expandLo / d);
		long iHi = (long) Math.floor(expandHi / d);
		int tickNum = (int) (iHi - iLo + 1);
		tickValues = new double[tickNum];

		double minorInterval = d / (minorTickNumber + 1);

		if (tickNum == 0) {
			long miLo = (long) Math.ceil(expandLo / minorInterval);
			long miHi = (long) Math.floor(expandHi / minorInterval);
			int mTickNum = (int) (miHi - miLo + 1);
			minorValues = new double[mTickNum];
			int mvi = 0;
			for (long mi = miLo; mi <= miHi; mi++) {
				minorValues[mvi++] = minorInterval * mi + offset;
			}
		} else {
			double lowMargine = -expandLo + d * iLo;
			double hiMargine = expandHi - d * iHi;
			int minorNumBeforeLow = (int) (lowMargine / minorInterval);
			int minorNumAfterHi = (int) (hiMargine / minorInterval);
			minorValues = new double[(minorNumBeforeLow + (tickNum - 1) * minorTickNumber + minorNumAfterHi)];
			int mvi = 0;
			int tvi = 0;
			double v = d * iLo + offset;
			for (int im = minorNumBeforeLow; im > 0; im--) {
				minorValues[mvi++] = v - minorInterval * im;
			}
			for (long i = iLo;;) {
				v = d * i + offset;
				tickValues[tvi++] = v;
				if (i++ == iHi) {
					break;
				}
				for (int im = 1; im <= minorTickNumber; im++) {
					minorValues[mvi++] = v + minorInterval * im;
				}
			}
			for (int im = 1; im <= minorNumAfterHi; im++) {
				minorValues[mvi++] = v + minorInterval * im;
			}

			if (inverted) {
				tickValues = NumberArrayUtils.reverse(tickValues);
				minorValues = NumberArrayUtils.reverse(minorValues);
			}
		}
	}

	public void expandRangeByTickNumber(int tickNumber) {

		if (tickNumber == 0) {
			throw new IllegalArgumentException("nticks cannot be zero");
		} else if (tickNumber == 1) {
			tickNumber = 2;
		}

		double lo, hi;
		if (end > start) {
			lo = start;
			hi = end;
		} else {
			lo = end;
			hi = start;
		}
		tickNumber = Math.abs(tickNumber);

		double rough = (hi - lo) / (tickNumber - 1);
		DoubleInterval[] candidates = calcCandidateInterval(rough);
		DoubleInterval itvA = candidates[0];
		DoubleInterval itvB = candidates[1];

		double shrinkLo = lo + Math.abs(lo) * DOUBLE_PRECISION_TOLERANCE;
		double shrinkHi = hi - Math.abs(hi) * DOUBLE_PRECISION_TOLERANCE;
		long iLoA = (long) Math.floor(shrinkLo / itvA.doubleValue());
		long iHiA = (long) Math.ceil(shrinkHi / itvA.doubleValue());
		int tickNumA = (int) (iHiA - iLoA + 1);
		long iLoB = (long) Math.floor(shrinkLo / itvB.doubleValue());
		long iHiB = (long) Math.ceil(shrinkHi / itvB.doubleValue());
		int tickNumB = (int) (iHiB - iLoB + 1);

		double xLo, xHi;
		/* tickNumB < tickNumber < tickNumA */
		if (tickNumA - tickNumber <= tickNumber - tickNumB) {
			interval = itvA.doubleValue();
			xLo = interval * iLoA;
			xHi = interval * iHiA;
		} else {
			interval = itvB.doubleValue();
			xLo = interval * iLoB;
			xHi = interval * iHiB;
		}

		if (end > start) {
			start = xLo;
			end = xHi;
		} else {
			start = xHi;
			end = xLo;
		}

	}

	public void expandRangeByTickInterval(double interval) {

		double lo, hi;
		if (end > start) {
			lo = start;
			hi = end;
		} else {
			lo = end;
			hi = start;
		}
		double d = Math.abs(interval);

		double shrinkLo = lo + Math.abs(lo) * DOUBLE_PRECISION_TOLERANCE;
		double shrinkHi = hi - Math.abs(hi) * DOUBLE_PRECISION_TOLERANCE;
		long iLo = (long) Math.floor(shrinkLo / d);
		long iHi = (long) Math.ceil(shrinkHi / d);
		double xLo = d * iLo;
		double xHi = d * iHi;

		interval = d;
		if (end > start) {
			start = xLo;
			end = xHi;
		} else {
			start = xHi;
			end = xLo;
		}

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

}
