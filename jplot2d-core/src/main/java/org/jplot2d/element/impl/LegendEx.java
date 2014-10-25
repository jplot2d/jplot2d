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
	 * Returns all legend items managed by this legend
	 * 
	 * @return items managed by this legend
	 */
	public LegendItemEx[] getItems();

	/**
	 * Put all items in this disabled legend to enabled upper legend
	 */
	public void putItemsToEnabledLegend();

	/**
	 * Sets the location without setting the position to Position.FREE
	 * 
	 * @param locX
	 * @param locY
	 */
	public void directLocation(double locX, double locY);

	/**
	 * Add the given item to this legend. If this legend is disabled, the item should be added to its upper level
	 * legend. This method will set the legend property of added legend item to the actual hosting legend.
	 * 
	 * @param item
	 *            the legend item to be added
	 */
	public void addLegendItem(LegendItemEx item);

	/**
	 * Remove the given item from this legend. If this legend is disabled, the item should be removed from its upper
	 * level legend.
	 * 
	 * @param item
	 *            the legend item to be removed
	 */
	public void removeLegendItem(LegendItemEx item);

	/**
	 * Called by a visible legend item to notify this legend that the item's size is changed.
	 */
	public void itemSizeChanged(LegendItemEx item);

	/**
	 * Called by a legend item to notify this legend that the item's visibility is changed. An item is shown when both
	 * visibility and contributivity are <code>true</code>.
	 */
	public void itemVisibilityChanged(LegendItemImpl item);

	/**
	 * Called by a legend item to notify that the item's contributivity is changed. An item is shown when both
	 * visibility and contributivity are <code>true</code>.
	 */
	public void itemContribitivityChanged(LegendItemImpl item);

	/**
	 * Returns the thickness of this legend. When position is on top or bottom, thickness is its height. When position
	 * is on left or right, thickness is its width.
	 * 
	 * @return the thickness of this legend
	 */
	public double getThickness();

	/**
	 * Calculate size of this legend and re-layout legend items. This method is always called by plot when committing
	 * changes.
	 */
	public void calcSize();

}
