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
package org.jplot2d.layout;

import org.jplot2d.util.Insets2D;

/**
 * This constraint defines bounds relative to its container's bounds.
 *
 * @author Jingjing Li
 */
public class BoundsConstraint {

    private static final Insets2D ZERO_INSETS = new Insets2D.Double(0, 0, 0, 0);

    private final Insets2D fixedInsets;

    private final Insets2D elasticInsets;

    /**
     * Construct a BoundsConstraint with the 4 elastic gaps relative to its
     * container's bounds.
     *
     * @param elasticTop    the ratio of top gap, relative to its container's height
     * @param elasticLeft   the ratio of left gap, relative to its container's width
     * @param elasticBottom the ratio of bottom gap, relative to its container's height
     * @param elasticRight  the ratio of right gap, relative to its container's width
     */
    public BoundsConstraint(double elasticTop, double elasticLeft,
                            double elasticBottom, double elasticRight) {
        this(ZERO_INSETS, new Insets2D.Double(elasticTop, elasticLeft, elasticBottom,
                elasticRight));
    }

    /**
     * Construct a BoundsConstraint with the fixed gaps and elastic gaps.
     *
     * @param fixedInsets   the fixed gap in pt
     * @param elasticInsets the elastic gap ratio.
     */
    public BoundsConstraint(Insets2D fixedInsets, Insets2D elasticInsets) {
        this.fixedInsets = fixedInsets;
        this.elasticInsets = elasticInsets;
    }

    public Insets2D getFixedInsets() {
        return fixedInsets;
    }

    public Insets2D getElasticInsets() {
        return elasticInsets;
    }
}
