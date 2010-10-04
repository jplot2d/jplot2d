/**
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
package org.jplot2d.layout;

import org.jplot2d.element.impl.SubplotEx;

/**
 * The interface for jplot2d to layout subplots and other components. The way to
 * layout subplots can be customized, but the layers in subplot are stacked over
 * and cannot be changed.
 * <p>
 * The axis is a special component. Its length can be set when laid out, but its
 * height is fixed and derived from its internal status, such as tick height and
 * labels.
 * 
 * @author Jingjing Li
 * 
 */
public interface LayoutDirector {

	/**
	 * Returns the constraint for the given subplot.
	 * 
	 * @param subplot
	 *            The subplot
	 * @return The constraint
	 */
	public Object getConstraint(SubplotEx subplot);

	/**
	 * Sets the constraint for the given subplot.
	 * 
	 * @param subplot
	 * @param constraint
	 */
	public void setConstraint(SubplotEx subplot, Object constraint);

	/**
	 * Removes the given child from this layout.
	 * 
	 * @param subplot
	 */
	public void remove(SubplotEx subplot);

	/**
	 * Layout the plot.
	 * 
	 */
	public void layout();

}
