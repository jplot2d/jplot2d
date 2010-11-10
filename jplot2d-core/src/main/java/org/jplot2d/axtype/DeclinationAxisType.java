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
 * 
 */
package org.jplot2d.axtype;

import org.jplot2d.axtrans.LinearTransformType;
import org.jplot2d.tick.ArcDmsTickAlgorithm;
import org.jplot2d.util.Range2D;

/**
 * @author Jingjing Li
 * 
 */
public class DeclinationAxisType extends AxisType {

	private static final Range2D DECLINATION_BOUNDARY = new Range2D.Double(-90,
			true, 90, true);

	public DeclinationAxisType() {
		super("DECLINATION", LinearTransformType.getInstance(),
				ArcDmsTickAlgorithm.getInstance());
	}

	public Range2D getBoundary() {
		return DECLINATION_BOUNDARY;
	}

	public Range2D getDefaultWorldRange() {
		return DECLINATION_BOUNDARY;
	}

}
