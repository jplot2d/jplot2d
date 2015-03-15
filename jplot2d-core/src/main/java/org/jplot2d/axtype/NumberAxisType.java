/**
 * Copyright 2010-2012 Jingjing Li.
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

import org.jplot2d.axtick.LinearTickAlgorithm;
import org.jplot2d.axtick.LogTickAlgorithm;
import org.jplot2d.axtick.ReciprocalTickAlgorithm;
import org.jplot2d.axtick.TickAlgorithm;
import org.jplot2d.transform.AxisTickTransform;
import org.jplot2d.transform.ReciprocalAxisTickTransform;
import org.jplot2d.transform.TransformType;
import org.jplot2d.util.Range;

/**
 * An axis for displaying numerical data. It support both LinearTransformType and
 * LogarithmicTransformType.
 * 
 * @author Jingjing Li
 * 
 */
public class NumberAxisType extends AxisType {

	private static final Range LINEAR_BOUNDARY = new Range.Double(-Double.MAX_VALUE / 2,
			Double.MAX_VALUE / 2);

	private static final Range LINEAR_DEFAULT_RANGE = new Range.Double(-1, 1);

	private static final Range POSITIVE_BOUNDARY = new Range.Double(Double.MIN_VALUE, true,
			Double.MAX_VALUE / 2, true);

	private static final Range LOG_DEFAULT_RANGE = new Range.Double(0.1, 10);

	public static NumberAxisType getInstance() {
		return new NumberAxisType();
	}

	private NumberAxisType() {
		super("NUMBER");
	}

	public boolean canSupport(TransformType txfType) {
        return txfType == TransformType.LINEAR || txfType == TransformType.LOGARITHMIC;
    }

	public Range getBoundary(TransformType txfType) {
		if (txfType == TransformType.LINEAR) {
			return LINEAR_BOUNDARY;
		}
		if (txfType == TransformType.LOGARITHMIC) {
			return POSITIVE_BOUNDARY;
		}
		return null;
	}

	public Range getDefaultWorldRange(TransformType txfType) {
		if (txfType == TransformType.LINEAR) {
			return LINEAR_DEFAULT_RANGE;
		}
		if (txfType == TransformType.LOGARITHMIC) {
			return LOG_DEFAULT_RANGE;
		}
		return null;
	}

	public TransformType getDefaultTransformType() {
		return TransformType.LINEAR;
	}

	public TickAlgorithm getTickAlgorithm(TransformType txfType, AxisTickTransform tickTransform) {
		if (tickTransform == null) {
			if (txfType == TransformType.LINEAR) {
				return LinearTickAlgorithm.getInstance();
			}
			if (txfType == TransformType.LOGARITHMIC) {
				return LogTickAlgorithm.getInstance();
			}
		} else if (tickTransform instanceof ReciprocalAxisTickTransform) {
			if (txfType == TransformType.LINEAR) {
				return ReciprocalTickAlgorithm.getInstance();
			}
			if (txfType == TransformType.LOGARITHMIC) {
				return LogTickAlgorithm.getInstance();
			}
		}
		return null;
	}

}
