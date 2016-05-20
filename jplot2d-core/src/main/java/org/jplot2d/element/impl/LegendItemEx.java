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
package org.jplot2d.element.impl;

import org.jplot2d.element.LegendItem;

import java.awt.Graphics2D;
import java.awt.geom.Dimension2D;

/**
 * @author Jingjing Li
 */
public interface LegendItemEx extends LegendItem, ElementEx {

    GraphEx getParent();

    LegendEx getLegend();

    void setLegend(LegendEx legend);

    /**
     * Returns the text displayed in the legend item
     *
     * @return the text
     */
    String getText();

    /**
     * Sets the text displayed in the legend item
     *
     * @param text the text displayed in the legend item
     */
    void setText(String text);

    /**
     * Returns <code>true</code> if the item has a text can be displayed in legend.
     *
     * @return the indicator
     */
    boolean canContribute();

    /**
     * Called by legend when its effective font changed
     */
    void legendEffectiveFontChanged();

    Dimension2D getSize();

    /**
     * Set location in legend
     */
    void setLocation(double locx, double locy);

    /**
     * This method is called by LegendImpl. The given g has been transformed to legend's paper
     * space.
     *
     * @param g the graphic to draw.
     */
    void draw(Graphics2D g);

}
