/**
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

import java.awt.Color;

import org.jplot2d.annotation.Hierarchy;
import org.jplot2d.annotation.HierarchyOp;
import org.jplot2d.data.Graph;

/**
 * A plotter to draw Graph data in a viewport of layer.
 * 
 * @author Jingjing Li
 * 
 */
public interface GraphPlotter extends Element {

	@Hierarchy(HierarchyOp.GET)
	public Layer getParent();

	/**
	 * Returns the legend item associated with this graph plotter.
	 * 
	 * @return the legend item
	 */
	@Hierarchy(HierarchyOp.GET)
	public LegendItem getLegendItem();

	/**
	 * Returns the graph data that this plotter will draw.
	 * 
	 * @return
	 */
	public Graph getGraph();

	/**
	 * Determines whether this plotter should plot the graph. plotter are
	 * initially visible.
	 * 
	 * @return <code>true</code> if the plotter is visible, <code>false</code>
	 *         otherwise
	 * @see #setVisible
	 */
	public boolean isVisible();

	/**
	 * Plot or not the graph depending on the value of parameter <code>b</code>.
	 * 
	 * @param b
	 *            if <code>true</code>, plot the graph
	 * @see #isVisible
	 */
	public void setVisible(boolean b);

	/**
	 * Gets the default color of this plotter.
	 * 
	 * @return the default color of this plotter.
	 * @see #setColor
	 */
	public Color getColor();

	/**
	 * Sets the default color of this plotter.
	 * 
	 * @param c
	 *            the default color of this plotter.
	 * @see #getColor
	 */
	public void setColor(Color c);

}
