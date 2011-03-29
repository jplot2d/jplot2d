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
public interface LegendEx extends Legend, ContainerEx {

	public SubplotEx getParent();

	/**
	 * Add the given item to this legend. If this legend is disabled, the item
	 * should be added to its 1st enabled upper level legend. This method will
	 * set the legend property of added item to the actual hosting legend.
	 * 
	 * @param item
	 *            the legend item to be added
	 */
	public void addLegendItem(LegendItemEx item);

	/**
	 * Remove the given item from this legend. If this legend is disabled, the
	 * item should be removed from its hosting legend.
	 * 
	 * @param item
	 *            the legend item to be removed
	 */
	public void removeLegendItem(LegendItemEx item);

	/**
	 * Returns the constraint of length.
	 * 
	 * @return the length
	 */
	public double getLengthConstraint();

	/**
	 * Set the constraint of length
	 * 
	 * @param length
	 */
	public void setLengthConstraint(double length);

	/**
	 * Notify this legend one of its item's size changed
	 */
	public void itemSizeChanged(LegendItemEx item);

	/**
	 * Notify this legend one of its item's visible changed
	 */
	public void itemVisibleChanged(LegendItemImpl item);

	/**
	 * This method is used to calculate size of this legend. If the the size
	 * changed and this legend is visible, it'll call invalidate() to notify
	 * subplot its layout is invalid.
	 */
	public void calcSize();

}
