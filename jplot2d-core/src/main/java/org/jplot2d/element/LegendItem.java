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
package org.jplot2d.element;

import org.jplot2d.annotation.Hierarchy;
import org.jplot2d.annotation.HierarchyOp;
import org.jplot2d.annotation.Property;
import org.jplot2d.annotation.PropertyGroup;

/**
 * An item in a legend to show a short line and a text. It will be hosted by the legend of the plot which host the Graph.
 * If the hosting legend is disabled, it will be hosted by the legend of parent plot.
 *
 * @author Jingjing Li
 */
@PropertyGroup("Legend Item")
public interface LegendItem extends Element {

    @Hierarchy(HierarchyOp.GET)
    Graph getParent();

    /**
     * Returns the legend who show this item
     *
     * @return the legend who show this item
     */
    @Hierarchy(HierarchyOp.GET)
    Legend getLegend();

    /**
     * returns <code>true</code> if this item is displayed in legend; otherwise, returns <code>false</code>.
     *
     * @return the flag
     */
    @Property(order = 0, styleable = false)
    boolean isVisible();

    /**
     * Sets a flag indicating whether this item should be displayed in the legend.
     * The default value is <code>true</code>, even if the Graph is invisible.
     *
     * @param show the flag
     */
    void setVisible(boolean show);

    /**
     * Returns the size of the symbol draw in legend item.
     * The default symbol size is <code>Float.NaN</code>, means the same size as the symbol size of XYGraph.
     *
     * @return the size of the symbol draw in legend
     */
    @Property(order = 1)
    float getSymbolSize();

    /**
     * Set the size of symbol draw in legend item.
     * Setting to <code>Float.NaN</code> means the same size as the symbol size of XYGraph.
     *
     * @param size the size of symbols draw in legend
     */
    void setSymbolSize(float size);

}
