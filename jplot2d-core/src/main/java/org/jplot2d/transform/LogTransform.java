/**
 * Copyright 2010, 2011 Jingjing Li.
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
package org.jplot2d.transform;

/**
 * The dest value = k * Math.log10(src value) + a.
 * 
 * @author Jingjing Li
 */
public class LogTransform implements Transform1D {

	private double k;

	private double a;

	public LogTransform() {
		super();
	}

	public LogTransform(double s1, double s2, double d1, double d2) {
		if (Double.isNaN(d1) || Double.isNaN(d2) || Double.isNaN(s1) || Double.isNaN(s2)) {
			throw new IllegalArgumentException("Transform is invalid");
		}

		if (s1 <= 0 || s2 <= 0) {
			throw new IllegalArgumentException("Transform is invalid");
		}

		double denom;
		denom = Math.log10(s1) - Math.log10(s2);
		if (denom == 0) {
			throw new IllegalArgumentException("Transform is invalid");
		} else {
			k = (d1 - d2) / denom;
			a = d1 - k * Math.log10(s1);
		}
	}

	public double convert(double u) {
		if (u <= 0) {
			return Double.NEGATIVE_INFINITY * k;
		}
		return k * Math.log10(u) + a;
	}

}