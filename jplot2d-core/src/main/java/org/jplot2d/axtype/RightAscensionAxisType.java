/**
 * Copyright 2010 Jingjing Li.
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
package org.jplot2d.axtype;

import org.jplot2d.axtrans.LinearTransformType;
import org.jplot2d.axtrans.TransformType;
import org.jplot2d.axtick.RightAscensionTickAlgorithm;
import org.jplot2d.axtick.TickAlgorithm;
import org.jplot2d.element.AxisTickTransform;
import org.jplot2d.util.Range2D;

/**
 * RIGHT_ASCENSION axis type. The input data unit is decimal degree. The display
 * tick label format is hh:mm:ss.ms
 * 
 * @author Jingjing Li
 * 
 */
public class RightAscensionAxisType extends AxisType {

	private static final Range2D BOUNDARY = new Range2D.Double(
			-Double.MAX_VALUE / 2, Double.MAX_VALUE / 2);

	private static final Range2D CIRCULAR_RANGE = new Range2D.Double(0, 360);

	private static final Range2D DEFAULT_RANGE = new Range2D.Double(360, 0);

	public static RightAscensionAxisType getInstance() {
		return new RightAscensionAxisType();
	}

	private RightAscensionAxisType() {
		super("RIGHT_ASCENSION");
	}

	@Override
	public boolean canSupport(TransformType txfType) {
		if (txfType == LinearTransformType.getInstance()) {
			return true;
		}
		return false;
	}

	@Override
	public TransformType getDefaultTransformType() {
		return LinearTransformType.getInstance();
	}

	@Override
	public Range2D getBoundary(TransformType txfType) {
		return BOUNDARY;
	}

	@Override
	public Range2D getDefaultWorldRange(TransformType txfType) {
		return DEFAULT_RANGE;
	}

	@Override
	public TickAlgorithm getTickAlgorithm(TransformType txfType,
			AxisTickTransform tickTransform) {
		if (tickTransform != null) {
			return null;
		}

		return RightAscensionTickAlgorithm.getInstance();
	}

	@Override
	public Range2D getCircularRange() {
		return CIRCULAR_RANGE;
	}

}
