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
package org.jplot2d.util;

import java.awt.geom.Dimension2D;

/**
 * @author Jingjing li
 */
public class DoubleDimension2D extends Dimension2D {

    public double width, height;

    public DoubleDimension2D() {

    }

    public DoubleDimension2D(Dimension2D d) {
        this.width = d.getWidth();
        this.height = d.getHeight();
    }

    public DoubleDimension2D(double width, double height) {
        this.width = width;
        this.height = height;
    }

    @Override
    public double getWidth() {
        return width;
    }

    @Override
    public double getHeight() {
        return height;
    }

    @Override
    public void setSize(double width, double height) {
        this.width = width;
        this.height = height;
    }

    /**
     * Returns a <code>String</code> that represents the value of this class .
     *
     * @return a string representation of this class.
     */
    @Override
    public String toString() {
        return "DoubleDimension2D[" + width + ", " + height + "]";
    }

}
