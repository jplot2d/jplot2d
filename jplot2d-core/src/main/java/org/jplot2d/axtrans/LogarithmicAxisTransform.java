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
/**
 * $Id: LogTransform.java,v 1.6 2010/02/02 10:16:48 hsclib Exp $
 */
package org.jplot2d.axtrans;

import org.jplot2d.util.Range2D;

/**
 * The physical value = _a * Math.log10(u) + _b.
 * 
 * @author Jingjing Li
 */
public class LogarithmicAxisTransform extends AxisTransform {

	private double _a;

	private double _b;

	public LogarithmicAxisTransform() {
		super();
	}

	public LogarithmicAxisTransform(double p1, double p2, double u1, double u2) {
		super(new Range2D.Double(p1, p2), new Range2D.Double(u1, u2));
	}

	public LogarithmicAxisTransform(Range2D pr, Range2D ur) {
		super(pr, ur);
	}

	public LogarithmicAxisTransform copy() {
		LogarithmicAxisTransform newTransform;
		try {
			newTransform = (LogarithmicAxisTransform) clone();
		} catch (CloneNotSupportedException e) {
			newTransform = new LogarithmicAxisTransform(_pRange, _wRange);
		}
		return newTransform;
	}

	public double getTransP(double u) {
		if (!_valid) {
			throw new IllegalStateException("Transform is invalid");
		}
		if (u <= 0) {
			return Double.NEGATIVE_INFINITY * _a;
		}
		return _a * Math.log10(u) + _b;
	}

	public double getTransU(double p) {
		if (!_valid) {
			throw new IllegalStateException("Transform is invalid");
		}
		return Math.pow(10, (p - _b) / _a);
	}

	public void computeTransform() {
		double _p1 = _pRange.getStart();
		double _p2 = _pRange.getEnd();
		double _u1 = _wRange.getStart();
		double _u2 = _wRange.getEnd();

		if (Double.isNaN(_p1) || Double.isNaN(_p2) || Double.isNaN(_u1)
				|| Double.isNaN(_u2)) {
			_valid = false;
			return;
		}

		if (_u1 <= 0 || _u2 <= 0) {
			_a = Double.NaN;
			_b = Double.NaN;
			_valid = false;
			return;
		}

		double denom;
		denom = Math.log10(_u1) - Math.log10(_u2);
		if (denom == 0) {
			_a = Double.NaN;
			_b = Double.NaN;
			_valid = false;
		} else {
			_a = (_p1 - _p2) / denom;
			_b = _p1 - _a * Math.log10(_u1);
			_valid = true;
		}
	}

	public double getScale() {
		if (!_valid) {
			throw new IllegalStateException("Transform is invalid");
		}
		return _a;
	}

	@Override
	public void invert() {
		_b = _pRange.getStart() + _pRange.getEnd() - _b;
		_a = -_a;
		_wRange = _wRange.invert();
	}

}