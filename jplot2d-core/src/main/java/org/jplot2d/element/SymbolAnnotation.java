/*
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
import org.jplot2d.util.SymbolShape;

/**
 * A point annotation with a symbol and a text string.
 *
 * @author Jingjing Li
 */
@PropertyGroup("Symbol Annotation")
public interface SymbolAnnotation extends PointAnnotation, TextComponent {

    /**
     * Returns the symbol shape of this annotation
     *
     * @return the symbol shape of this annotation
     */
    @Property(order = 0, styleable = false)
    SymbolShape getSymbolShape();

    /**
     * Sets the symbol shape of this annotation
     *
     * @param symbol the symbol shape of this annotation
     */
    void setSymbolShape(SymbolShape symbol);

    /**
     * Returns the symbol size of this annotation in pt (1/72 inch).
     *
     * @return the symbol size of this annotation
     */
    @Property(order = 1)
    float getSymbolSize();

    /**
     * Sets the symbol size of this annotation in pt (1/72 inch).
     * The default size is <code>Float.NaN</code>, means to derive size by {@link #getSymbolScale()}.
     *
     * @param size the symbol size of this annotation
     */
    void setSymbolSize(float size);

    /**
     * Returns the scale apply to effective font size to derive symbol size when symbol size is <code>Float.NaN</code>.
     *
     * @return the font scale.
     */
    @Property(order = 2)
    float getSymbolScale();

    /**
     * Sets the scale apply to effective font size to derive symbol size when symbol size is <code>Float.NaN</code>.
     * The default scale is <code>1</code>.
     *
     * @param scale the scale
     */
    void setSymbolScale(float scale);

    /**
     * Returns the rotation angle of the text.
     *
     * @return the rotation angle value
     */
    @Property(order = 3, styleable = false)
    double getAngle();

    /**
     * Set the rotation angle of the text. The angle start to count from horizontal direction and grow in counter-clock
     * wise direction.
     *
     * @param angle the rotation angle
     */
    void setAngle(double angle);

    /**
     * Returns the X offset factor of text. The offset is relative to the base point of this annotation. The factor
     * apply to the half size of symbol.
     *
     * @return the X offset factor of text
     */
    @Property(order = 4)
    float getTextOffsetFactorX();

    /**
     * Sets the X offset factor of text. The offset is relative to the base point of this annotation. The factor apply
     * to the half size of symbol. The default value is <code>1.25</code>.
     *
     * @param offset the X offset factor of text
     */
    void setTextOffsetFactorX(float offset);

    /**
     * Returns the Y offset of text.The offset is relative to the base point of this annotation. The factor apply to the
     * half size of symbol.
     *
     * @return the Y offset factor of text
     */
    @Property(order = 5)
    float getTextOffsetFactorY();

    /**
     * Sets the Y offset of text.The offset is relative to the base point of this annotation. The factor apply to the
     * half size of symbol. The default value is <code>0</code>.
     *
     * @param offset the Y offset factor of text
     */
    void setTextOffsetFactorY(float offset);

}
