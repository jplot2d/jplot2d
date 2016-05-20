/*
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
package org.jplot2d.axtype;

import org.jplot2d.axtick.ArcDmsTickAlgorithm;
import org.jplot2d.axtick.TickAlgorithm;
import org.jplot2d.transform.AxisTickTransform;
import org.jplot2d.transform.TransformType;
import org.jplot2d.util.Range;

/**
 * @author Jingjing Li
 */
public class DeclinationAxisType extends AxisType {

    private static final Range DECLINATION_BOUNDARY = new Range.Double(-90, true, 90, true);

    public static DeclinationAxisType getInstance() {
        return new DeclinationAxisType();
    }

    private DeclinationAxisType() {
        super("DECLINATION");
    }

    @Override
    public boolean canSupport(TransformType txfType) {
        return txfType == TransformType.LINEAR;
    }

    @Override
    public TransformType getDefaultTransformType() {
        return TransformType.LINEAR;
    }

    @Override
    public Range getBoundary(TransformType txfType) {
        return DECLINATION_BOUNDARY;
    }

    @Override
    public Range getDefaultWorldRange(TransformType txfType) {
        return DECLINATION_BOUNDARY;
    }

    @Override
    public TickAlgorithm getTickAlgorithm(TransformType txfType, AxisTickTransform tickTransform) {
        if (tickTransform != null) {
            return null;
        }

        return ArcDmsTickAlgorithm.getInstance();
    }

}
