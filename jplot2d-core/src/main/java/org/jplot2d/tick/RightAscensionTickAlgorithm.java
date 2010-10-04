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
