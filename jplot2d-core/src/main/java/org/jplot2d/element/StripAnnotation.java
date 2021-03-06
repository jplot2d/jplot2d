/*
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
import org.jplot2d.util.Range;

import java.awt.*;

/**
 * A strip annotation to highlight a range.
 *
 * @author Jingjing Li
 */
@PropertyGroup("Strip Annotation")
public interface StripAnnotation extends Annotation {

    /**
     * Returns the value range of this annotation
     *
     * @return the value range of this annotation
     */
    @Property(order = 0, styleable = false)
    Range getValueRange();

    /**
     * Sets the value range of this annotation
     *
     * @param range the value range of this annotation
     */
    void setValueRange(Range range);

    /**
     * Returns the <code>Paint</code> to be used to fill the annotation strip.
     *
     * @return the <code>Paint</code>
     */
    @Property(order = 1)
    Paint getFillPaint();

    /**
     * Sets the <code>Paint</code> to be used to fill the annotation strip.
     * The default paint is semi-transparent grey Color(192, 192, 192, 128).
     *
     * @param stroke the <code>Paint</code> to be used to fill the annotation strip
     */
    void setFillPaint(Paint stroke);

}
