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
package org.jplot2d.element;

/**
 * An auxiliary axis can attach to a main axis, and may has different units
 * other than main axis. The relationship between the aux axis and its main axis
 * can be set by {@link #setTransformer(AuxTransformer)}.
 * 
 * @author Jingjing Li
 * 
 */
public interface AuxAxis extends Axis {

	/**
	 * Returns its attached main axis.
	 * 
	 * @return its attached main axis
	 */
	public MainAxis getMainAxis();

	/**
	 * Sets the main axis that this auxiliary axis attach to.
	 * 
	 * @param axis
	 */
	public void setMainAxis(MainAxis axis);

	/**
	 * Returns the AuxTransform, which defines The relationship between this aux
	 * axis and its main axis.
	 * 
	 * @return the AuxTransform.
	 */
	public AuxTransform getTransform();

	/**
	 * Sets the AuxTransform, which defines The relationship between this aux
	 * axis and its main axis. The AuxTransform <em>must</em> be efficient
	 * immutable.
	 * 
	 * @param transform
	 *            the AuxTransform object.
	 */
	public void setTransformer(AuxTransform transform);

}
