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
package org.jplot2d.element.impl;

import org.jplot2d.element.Legend;

/**
 * @author Jingjing Li
 * 
 */
public interface LegendEx extends Legend, ComponentEx {

	public PlotEx getParent();

	/**
	 * Sets the location without setting the position to Position.FREE
	 * 
	 * @param locX
	 * @param locY
	 */
	public void directLocation(double locX, double locY);

	/**
	 * Add the given item to this legend. If this legend is disabled, the item should be added to
	 * its 1st enabled upper level legend. This method will set the legend property of added item to
	 * the actual hosting legend.
	 * 
	 * @param item
	 *            the legend item to be added
	 */
	public void addLegendItem(LegendItemEx item);

	/**
	 * Remove the given item from this legend. If this legend is disabled, the item should be
	 * removed from its hosting legend.
	 * 
	 * @param item
	 *            the legend item to be removed
	 */
	public void removeLegendItem(LegendItemEx item);

	/**
	 * Notify this legend that one of its item's size changed
	 */
	public void itemSizeChanged(LegendItemEx item);

	/**
	 * Notify this legend one of its item's visible changed
	 */
	public void itemVisibleChanged(LegendItemImpl item);

	/**
	 * Returns the thickness of this legend. When position is on top or bottom, thickness is its
	 * height. When position is on left or right, thickness is its width.
	 * 
	 * @return the thickness of this legend
	 */
	public double getThickness();

	/**
	 * Calculate size of this legend and re-layout legend items. This method is always called by
	 * plot when committing changes.
	 */
	public void calcSize();

}
