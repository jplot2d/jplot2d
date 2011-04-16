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

import org.jplot2d.annotation.Hierarchy;
import org.jplot2d.annotation.HierarchyOp;
import org.jplot2d.util.MathElement;

/**
 * An item in legend to show a short line and a text. It will be hosted by the
 * legend of the plot which host the GraphPlotter. If the hosting legend is
 * disabled, it will be hosted by legend of parent plot.
 * 
 * @author Jingjing Li
 * 
 */
public interface LegendItem extends Element {

	@Hierarchy(HierarchyOp.GET)
	public GraphPlotter getParent();

	/**
	 * Returns the legend who show this item
	 * 
	 * @return the legend who show this item
	 */
	@Hierarchy(HierarchyOp.GET)
	public Legend getLegend();

	/**
	 * returns <code>true</code> if this item is displayed in legend; otherwise,
	 * returns <code>false</code>.
	 * 
	 * @return the flag
	 */
	public boolean isVisible();

	/**
	 * Sets a flag indicating whether this item should be displayed in the
	 * legend. The default value is <code>true</code>, even if the GraphPlotter
	 * is invisible.
	 * 
	 * @param show
	 *            the flag
	 */
	public void setVisible(boolean show);

	/**
	 * Returns the text displayed in the legend item
	 * 
	 * @return the text
	 */
	public String getText();

	/**
	 * Sets the text displayed in the legend item
	 * 
	 * @param text
	 *            the text displayed in the legend item
	 */
	public void setText(String text);

	/**
	 * Returns the text displayed in the legend item
	 * 
	 * @return the text
	 */
	public MathElement getTextModel();

	/**
	 * Sets the text displayed in the legend item
	 * 
	 * @param text
	 *            the text displayed in the legend
	 */
	public void setTextModel(MathElement model);

}