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
package org.jplot2d.element.impl;

import org.jplot2d.element.Element;
import org.jplot2d.element.PComponent;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.Map;

import javax.annotation.Nonnull;

/**
 * Extended Component that all impl need to implements. Those additional methods is required by environment.
 *
 * @author Jingjing Li
 */
public interface ComponentEx extends PComponent, ElementEx {

    ComponentEx getParent();

    /**
     * Returns a map to tell why this component cannot be removed from its container. The key is the moored element,
     * required by its value.
     *
     * @return a map. The key is the element required by its value.
     */
    Map<Element, Element> getMooringMap();

    /**
     * Returns <code>true</code> if this component can contribute visible parts to plot rendering artifact when this
     * component is visible. Subcomponents are not considered.
     *
     * @return the indicator
     */
    boolean canContribute();

    /**
     * Returns the effective color of this component.
     *
     * @return the effective color of this component
     */
    Color getEffectiveColor();

    String getEffectiveFontName();

    int getEffectiveFontStyle();

    float getEffectiveFontSize();

    void parentEffectiveColorChanged();

    void parentEffectiveFontChanged();

    /**
     * Called when setColor() or parentEffectiveColorChanged() change the effective color of this component.
     */
    void thisEffectiveColorChanged();

    /**
     * Called when setFontXxx() or parentEffectiveFontChanged() change the effective font of this component.
     */
    void thisEffectiveFontChanged();

    /**
     * Returns <code>true</code> when this component need to be redrawn. The result only apply to cacheable component.
     *
     * @return the redraw status
     */
    boolean isRedrawNeeded();

    /**
     * Mark or clear the flag that indicate this component need to be redrawn, eg. when color changed.
     *
     * @param flag the flag that indicate this component need to be redrawn
     */
    void setRedrawNeeded(boolean flag);

    /**
     * Draw this component only. All its children is not drawn.
     *
     * @param g to the Graphics2D drawing. The transformation of this component is not applied.
     */
    void draw(Graphics2D g);

    /**
     * Create a deep copy of this component. The parent of the copy are not set.
     *
     * @param orig2copyMap original element to copy map
     * @return a deep copy of this component
     */
    ComponentEx copyStructure(@Nonnull Map<ElementEx, ElementEx> orig2copyMap);

}