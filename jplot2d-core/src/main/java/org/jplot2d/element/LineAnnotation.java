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
package org.jplot2d.element;

import org.jplot2d.annotation.Property;
import org.jplot2d.annotation.PropertyGroup;

import java.awt.BasicStroke;

/**
 * A line annotation to highlight a value.
 *
 * @author Jingjing Li
 */
@PropertyGroup("Line Annotation")
public interface LineAnnotation extends Annotation {

    /**
     * Returns the value of this annotation
     *
     * @return the value of this annotation
     */
    @Property(order = 0, styleable = false)
    public double getValue();

    /**
     * Sets the value of this annotation
     *
     * @param value the value of this annotation
     */
    public void setValue(double value);

    /**
     * Returns the <code>BasicStroke</code> to be used to draw the annotation line.
     *
     * @return the <code>BasicStroke</code>
     */
    @Property(order = 1)
    public BasicStroke getStroke();

    /**
     * Sets the <code>BasicStroke</code> to be used to draw the annotation line.
     *
     * @param stroke the <code>BasicStroke</code> to be used to draw the annotation line
     */
    public void setStroke(BasicStroke stroke);

}
