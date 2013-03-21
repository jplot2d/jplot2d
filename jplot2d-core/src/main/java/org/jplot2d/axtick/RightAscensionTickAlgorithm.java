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

/**
 * A TickCalculator to display in hh:mm:ss.xxxx format. The input unit is
 * decimal degree.
 * 
 * @author Jingjing Li
 * 
 */
public class RightAscensionTickAlgorithm extends TickAlgorithm {

	private static final RightAscensionTickAlgorithm _instance = new RightAscensionTickAlgorithm();

	private static final TickUnitConverter tuc = new TickUnitConverter() {

		public double convertD2T(double v) {
			return v / 360 * 60 * 60 * 24;
		}

		public double convertT2D(double v) {
			return v / (60 * 60 * 24) * 360;
		}

	};

	private RightAscensionTickAlgorithm() {

	}

	public static RightAscensionTickAlgorithm getInstance() {
		return _instance;
	}

	public TickCalculator createCalculator() {
		return new TimeHmsTickCalculator(tuc);
	}

}
