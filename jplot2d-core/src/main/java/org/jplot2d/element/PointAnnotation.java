/**
 * Copyright 2010-2014 Jingjing Li.
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
package org.jplot2d.element;

import org.jplot2d.annotation.Property;
import org.jplot2d.annotation.PropertyGroup;

import java.awt.geom.Point2D;

/**
 * A annotation which can highlight a point.
 *
 * @author Jingjing Li
 */
@PropertyGroup("Point Annotation")
public interface PointAnnotation extends Annotation {

    /**
     * returns the x,y values location in layer's world coordinate system.
     *
     * @return the x,y values location
     */
    @Property(order = 1, styleable = false)
    public Point2D getValuePoint();

    /**
     * Sets the x,y values location in layer's world coordinate system
     *
     * @param point the x,y values location
     */
    public void setValuePoint(Point2D point);

    /**
     * Sets the x,y values location in layer's world coordinate system
     *
     * @param x the x-values of the new location
     * @param y the y-values of the new location
     */
    public void setValuePoint(double x, double y);

}
