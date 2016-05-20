/*
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
package org.jplot2d.element;

import org.jplot2d.annotation.Hierarchy;
import org.jplot2d.annotation.HierarchyOp;
import org.jplot2d.annotation.Property;
import org.jplot2d.annotation.PropertyGroup;
import org.jplot2d.transform.PaperTransform;

import java.awt.*;
import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

/**
 * A <em>component</em> is an object having a graphical representation that can be drawn on the renderer
 * and that can interact with the user.
 *
 * @author Jingjing Li
 */
@PropertyGroup("Component")
public interface PComponent extends Element {

    /**
     * Gets the parent of this component.
     *
     * @return the parent of this component
     */
    @Hierarchy(HierarchyOp.GET)
    PComponent getParent();

    /**
     * Determines whether this component should be visible when its parent is visible. Components are initially visible.
     *
     * @return <code>true</code> if the component is visible, <code>false</code> otherwise
     * @see #setVisible
     */
    @Property(order = 0)
    boolean isVisible();

    /**
     * Shows or hides this component depending on the value of parameter <code>b</code>.
     *
     * @param b if <code>true</code>, shows this component; otherwise, hides this component
     * @see #isVisible
     */
    void setVisible(boolean b);

    /**
     * Returns <code>true</code> if this component has its own rendering cache.
     *
     * @return the cache mode of this component
     */
    @Property(order = 1)
    boolean isCacheable();

    /**
     * Sets if this component has its own rendering cache.
     *
     * @param mode the cache mode
     */
    void setCacheable(boolean mode);

    /**
     * Returns <code>true</code> if the component is selectable by mouse. Only selectable component can be movable.
     *
     * @return <code>true</code> if selectable
     */
    @Property(order = 2)
    boolean isSelectable();

    /**
     * Set the selectable property.
     *
     * @param selectable if <code>true</code> object is selectable
     */
    void setSelectable(boolean selectable);

    /**
     * Returns the z-order of this component.
     *
     * @return the z-order of this component
     */
    @Property(order = 3)
    int getZOrder();

    /**
     * Sets the z-order of this component. The component with higher z-order is on top.
     *
     * @param z the z-order value
     */
    void setZOrder(int z);

    /**
     * Gets the foreground color of this component.
     * If this component does not have a foreground color, the foreground color of its parent is returned.
     *
     * @return this component's foreground color.
     * @see #setColor
     */
    @Property(order = 4)
    Color getColor();

    /**
     * Sets the foreground color of this component. If this parameter is <code>null</code> then
     * this component will inherit the foreground color of its parent.
     *
     * @param c this component's foreground color;
     * @see #getColor
     */
    void setColor(Color c);

    /**
     * Returns the name of the font.
     *
     * @return the name of the font.
     */
    @Property(order = 5)
    String getFontName();

    /**
     * Apply the new font with the given name
     *
     * @param name the font name.
     */
    void setFontName(String name);

    /**
     * Returns the style of the font. The style can be PLAIN, BOLD, ITALIC, or BOLD+ITALIC.
     *
     * @return the style of the font
     * @see java.awt.Font
     */
    @Property(order = 6)
    int getFontStyle();

    /**
     * Apply a new style to the font. The style can be PLAIN, BOLD, ITALIC, or BOLD+ITALIC.
     *
     * @param style the style to apply
     * @see java.awt.Font
     */
    void setFontStyle(int style);

    /**
     * Returns the Font size.
     *
     * @return the font size.
     */
    @Property(order = 7)
    float getFontSize();

    /**
     * Sets a new size of the string.
     *
     * @param size the new size of the font.
     */
    void setFontSize(float size);

    /**
     * Returns the scale apply to parent's font size when font size is NaN.
     *
     * @return the font scale.
     */
    @Property(order = 8)
    float getFontScale();

    /**
     * Sets the scale apply to parent's font size when font size is NaN.
     *
     * @param scale the scale
     */
    void setFontScale(float scale);

    /**
     * Returns the effective font of this component.
     *
     * @return the effective font of this component
     */
    Font getEffectiveFont();

    /**
     * Sets the font name, style and size for this component.
     *
     * @param font the desired <code>Font</code> for this component
     */
    void setFont(Font font);

    /**
     * Returns the location of this component.
     *
     * @return the base point in its parent's paper coordinate space
     */
    @Property(order = 9, styleable = false, displayDigits = 4)
    Point2D getLocation();

    /**
     * Returns the paper size of this component.
     *
     * @return the paper size of this component
     */
    @Property(order = 10, styleable = false, displayDigits = 4)
    Dimension2D getSize();

    /**
     * Returns the paper bounds relative to its location. The units of bounds is pt (1/72 inch)
     *
     * @return the paper bounds of this component.
     */
    @Property(order = 11, styleable = false, displayDigits = 4)
    Rectangle2D getBounds();

    /**
     * Returns the selectable bounds relative to its location.
     * The selectable bound may be slightly larger than the bounds, for easy selection by mouse.
     * The units of bounds is pt (1/72 inch)
     *
     * @return the paper bounds of this component.
     */
    Rectangle2D getSelectableBounds();

    /**
     * Returns the paper transform of this component, which can be used to convert between device coordinate and paper
     * coordinate of this component. The original point of paper coordinate is the location point of this component.
     * <p>
     * Returns <code>null</code> if the component is not added to a plot or not laid out
     *
     * @return the paper transform of this component
     */
    PaperTransform getPaperTransform();

}
