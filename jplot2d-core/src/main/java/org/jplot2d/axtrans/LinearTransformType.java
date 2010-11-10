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
package org.jplot2d.axtrans;

import org.jplot2d.util.Range2D;

/**
 * @author Jingjing Li
 * 
 */
public class LinearTransformType implements TransformType {

	private static LinearTransformType _instance = new LinearTransformType();

	public static LinearTransformType getInstance() {
		return _instance;
	}

	public AxisTransform createTransform(Range2D pr, Range2D ur) {
		AxisTransform result = new LinearAxisTransform(pr, ur);
		if (!result.isValid()) {
			throw new IllegalArgumentException("Physcal " + pr + " World " + ur);
		}
		return result;
	}

	public boolean isTransformType(AxisTransform trf) {
		return trf instanceof LinearAxisTransform;
	}

	public NormalTransform createNormalTransform(Range2D wrange) {
		NormalTransform result = new LinearNormalTransform(wrange);
		if (!result.isValid()) {
			throw new IllegalArgumentException("The world range is invalid: "
					+ wrange);
		}
		return result;

	}

	public boolean isTransformType(NormalTransform nptrf) {
		return nptrf instanceof LinearNormalTransform;
	}

}
