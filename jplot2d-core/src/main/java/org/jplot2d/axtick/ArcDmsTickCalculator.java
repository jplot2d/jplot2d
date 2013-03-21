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

import org.jplot2d.axtick.ArcDmsInterval.Unit;

import java.util.Locale;

/**
 * 
 * @author Jingjing Li
 * 
 */
public class ArcDmsTickCalculator extends AbstractLinearTickCalculator {

	public ArcDmsTickCalculator() {

	}

	protected ArcDmsInterval[] calcCandidateInterval(double rough) {
		ArcDmsInterval itvA, itvB;

		if (rough < Unit.SECOND.angle) {
			rough *= 3600;
			int expn = (int) Math.floor(Math.log10(rough));
			double scale = Math.pow(10, expn);
			/* 1 <= rough/scale < 10 */
			double coeff = rough / scale;
			if (coeff < 2) {
				itvA = new ArcDmsInterval(1, expn);
				itvB = new ArcDmsInterval(2, expn);
			} else if (coeff < 5) {
				itvA = new ArcDmsInterval(2, expn);
				itvB = new ArcDmsInterval(5, expn);
			} else {
				itvA = new ArcDmsInterval(5, expn);
				itvB = new ArcDmsInterval(1, expn + 1);
			}
		} else if (rough < Unit.SECOND.angle * 2) {
			itvA = new ArcDmsInterval(Unit.SECOND, 1);
			itvB = new ArcDmsInterval(Unit.SECOND, 2);
		} else if (rough < Unit.SECOND.angle * 5) {
			itvA = new ArcDmsInterval(Unit.SECOND, 2);
			itvB = new ArcDmsInterval(Unit.SECOND, 5);
		} else if (rough < Unit.SECOND.angle * 10) {
			itvA = new ArcDmsInterval(Unit.SECOND, 5);
			itvB = new ArcDmsInterval(Unit.SECOND, 10);
		} else if (rough < Unit.SECOND.angle * 15) {
			itvA = new ArcDmsInterval(Unit.SECOND, 10);
			itvB = new ArcDmsInterval(Unit.SECOND, 15);
		} else if (rough < Unit.SECOND.angle * 30) {
			itvA = new ArcDmsInterval(Unit.SECOND, 15);
			itvB = new ArcDmsInterval(Unit.SECOND, 30);
		} else if (rough < Unit.MINUTE.angle) {
			itvA = new ArcDmsInterval(Unit.SECOND, 30);
			itvB = new ArcDmsInterval(Unit.MINUTE, 1);
		} else if (rough < Unit.MINUTE.angle * 2) {
			itvA = new ArcDmsInterval(Unit.MINUTE, 1);
			itvB = new ArcDmsInterval(Unit.MINUTE, 2);
		} else if (rough < Unit.MINUTE.angle * 5) {
			itvA = new ArcDmsInterval(Unit.MINUTE, 2);
			itvB = new ArcDmsInterval(Unit.MINUTE, 5);
		} else if (rough < Unit.MINUTE.angle * 10) {
			itvA = new ArcDmsInterval(Unit.MINUTE, 5);
			itvB = new ArcDmsInterval(Unit.MINUTE, 10);
		} else if (rough < Unit.MINUTE.angle * 15) {
			itvA = new ArcDmsInterval(Unit.MINUTE, 10);
			itvB = new ArcDmsInterval(Unit.MINUTE, 15);
		} else if (rough < Unit.MINUTE.angle * 30) {
			itvA = new ArcDmsInterval(Unit.MINUTE, 15);
			itvB = new ArcDmsInterval(Unit.MINUTE, 30);
		} else if (rough < Unit.DEGREE.angle) {
			itvA = new ArcDmsInterval(Unit.MINUTE, 30);
			itvB = new ArcDmsInterval(Unit.DEGREE, 1);
		} else if (rough < Unit.DEGREE.angle * 2) {
			itvA = new ArcDmsInterval(Unit.DEGREE, 1);
			itvB = new ArcDmsInterval(Unit.DEGREE, 2);
		} else if (rough < Unit.DEGREE.angle * 5) {
			itvA = new ArcDmsInterval(Unit.DEGREE, 2);
			itvB = new ArcDmsInterval(Unit.DEGREE, 5);
		} else if (rough < Unit.DEGREE.angle * 10) {
			itvA = new ArcDmsInterval(Unit.DEGREE, 5);
			itvB = new ArcDmsInterval(Unit.DEGREE, 10);
		} else if (rough < Unit.DEGREE.angle * 15) {
			itvA = new ArcDmsInterval(Unit.DEGREE, 10);
			itvB = new ArcDmsInterval(Unit.DEGREE, 15);
		} else if (rough < Unit.DEGREE.angle * 30) {
			itvA = new ArcDmsInterval(Unit.DEGREE, 15);
			itvB = new ArcDmsInterval(Unit.DEGREE, 30);
		} else if (rough < Unit.DEGREE.angle * 45) {
			itvA = new ArcDmsInterval(Unit.DEGREE, 30);
			itvB = new ArcDmsInterval(Unit.DEGREE, 45);
		} else if (rough < Unit.DEGREE.angle * 60) {
			itvA = new ArcDmsInterval(Unit.DEGREE, 45);
			itvB = new ArcDmsInterval(Unit.DEGREE, 60);
		} else if (rough < Unit.DEGREE.angle * 90) {
			itvA = new ArcDmsInterval(Unit.DEGREE, 60);
			itvB = new ArcDmsInterval(Unit.DEGREE, 90);
		} else if (rough < Unit.DEGREE.angle * 180) {
			itvA = new ArcDmsInterval(Unit.DEGREE, 90);
			itvB = new ArcDmsInterval(Unit.DEGREE, 180);
		} else if (rough < Unit.DEGREE.angle * 360) {
			itvA = new ArcDmsInterval(Unit.DEGREE, 180);
			itvB = new ArcDmsInterval(Unit.DEGREE, 360);
		} else {
			itvA = new ArcDmsInterval(Unit.DEGREE, 360);
			itvB = new ArcDmsInterval(Unit.DEGREE, 360);
		}

		return new ArcDmsInterval[] { itvA, itvB };
	}

	public void calcValuesByTickNumber(int tickNumber, int minorTickNumber) {
		ArcDmsInterval intv = (ArcDmsInterval) calcInterval(tickNumber);

		if (minorTickNumber == AUTO_MINORTICK_NUMBER) {
			if (intv.getCoefficient() == 1 && (intv.getUnit() == Unit.MINUTE || intv.getUnit() == Unit.DEGREE)) {
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

		int s1dInterval;

		if (interval < Unit.SECOND.angle) {
			int mag = (int) Math.floor(Math.log10(interval));
			double coefficient = interval / Math.pow(10, mag);
			double s1dcoef = Math.round(coefficient);
			/*
			 * if the coefficient contains more than 1 significant digit, ignore the minor number
			 */
			if (Math.abs(s1dcoef / coefficient - 1) > DOUBLE_PRECISION_TOLERANCE) {
				s1dInterval = 0;
			} else {
				s1dInterval = (int) s1dcoef;
			}
		} else if (interval < Unit.MINUTE.angle) {
			double secs = interval / Unit.SECOND.angle;
			double s1dcoef = Math.round(secs);
			/*
			 * if the coefficient contains more than 1 significant digit, ignore the minor number
			 */
			if (Math.abs(s1dcoef / secs - 1) > DOUBLE_PRECISION_TOLERANCE) {
				s1dInterval = 0;
			} else {
				s1dInterval = (int) s1dcoef;
			}
		} else if (interval < Unit.DEGREE.angle) {
			double minutes = interval / Unit.MINUTE.angle;
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
			double degrees = interval / Unit.DEGREE.angle;
			double s1dcoef = Math.round(degrees);
			/*
			 * if the coefficient contains more than 1 significant digit, ignore the minor number
			 */
			if (Math.abs(s1dcoef / degrees - 1) > DOUBLE_PRECISION_TOLERANCE) {
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

	public ArcDmsFormat calcLabelTextFormat(Object values) {
		if (values instanceof long[]) {
			return new ArcDmsFormat(0);
		}
		if (values instanceof double[]) {
			return new ArcDmsFormat(calcSigFraDigits((double[]) values));
		}
		return null;
	}

	private int calcSigFraDigits(double[] values) {
		/* number of fraction digits */
		int maxFractionDigits = 0;
		for (int i = 0; i < values.length; i++) {
			if (values[i] == 0) {
				continue;
			}

			double v = Math.abs(values[i]) * 3600;

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

	public String calcLabelFormatString(Object values) {
		return "";
	}

	public String getLabelFormate() {
		return "";
	}

}
