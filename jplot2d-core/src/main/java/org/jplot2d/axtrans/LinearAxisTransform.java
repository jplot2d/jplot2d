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
/*
 * $Id: LinearTransform.java,v 1.5 2010/02/02 10:16:48 hsclib Exp $
 */
package org.jplot2d.axtrans;

import org.jplot2d.util.Range2D;

/**
 * Performs a linear transformation on Cartesian axes. The equation is phys = a
 * * user + b
 */
public class LinearAxisTransform extends AxisTransform {

	private double _a;

	private double _b;

	public LinearAxisTransform() {
		super();
	}

	public LinearAxisTransform(double p1, double p2, double u1, double u2) {
		super(new Range2D.Double(p1, p2), new Range2D.Double(u1, u2));
	}

	public LinearAxisTransform(Range2D pr, Range2D ur) {
		super(pr, ur);
	}

	public LinearAxisTransform copy() {
		LinearAxisTransform newTransform;
		try {
			newTransform = (LinearAxisTransform) clone();
		} catch (CloneNotSupportedException e) {
			newTransform = new LinearAxisTransform(_pRange, _wRange);
		}
		return newTransform;
	}

	/**
	 * Transform from user to physical coordinates.
	 * 
	 * @param u
	 *            user value
	 * @return physical value
	 */
	public double getTransP(double u) {
		if (!_valid) {
			throw new IllegalStateException("Transform is invalid");
		}
		return _a * u + _b;
	}

	public double getTransU(double p) {
		if (!_valid) {
			throw new IllegalStateException("Transform is invalid");
		}
		return (p - _b) / _a;
	}

	protected void computeTransform() {
		double _p1 = _pRange.getStart();
		double _p2 = _pRange.getEnd();
		double _u1 = _wRange.getStart();
		double _u2 = _wRange.getEnd();

		if (Double.isNaN(_p1) || Double.isNaN(_p2) || Double.isNaN(_u1)
				|| Double.isNaN(_u2)) {
			_valid = false;
			return;
		}

		double denom;
		denom = _u1 - _u2;
		if (denom == 0) {
			_a = Double.NaN;
			_b = Double.NaN;
			_valid = false;
		} else {
			_a = (_p1 - _p2) / denom;
			_b = _p1 - _a * _u1;
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
