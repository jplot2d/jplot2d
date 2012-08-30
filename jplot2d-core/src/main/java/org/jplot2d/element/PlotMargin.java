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

import org.jplot2d.annotation.Property;
import org.jplot2d.annotation.PropertyGroup;

/**
 * Subplot has a content area in the center, surrounded by margin area. The margin area holds axes,
 * title and legend. Besides the margin, there are also extra margins. The extra margin plus the
 * margin will be the total margin.
 * 
 * @author Jingjing Li
 * 
 */
@PropertyGroup("Margin")
public interface PlotMargin extends Element {

	/**
	 * Returns <code>true</code> if the margin is a auto chose nice value.
	 * 
	 * @return <code>true</code> if the margin is a auto chose nice value
	 */
	@Property(order = 0)
	public boolean isAutoLeft();

	/**
	 * Returns <code>true</code> if the margin is a auto chose nice value.
	 * 
	 * @return <code>true</code> if the margin is a auto chose nice value
	 */
	@Property(order = 1)
	public boolean isAutoRight();

	/**
	 * Returns <code>true</code> if the margin is a auto chose nice value.
	 * 
	 * @return <code>true</code> if the margin is a auto chose nice value
	 */
	@Property(order = 2)
	public boolean isAutoBottom();

	/**
	 * Returns <code>true</code> if the margin is a auto chose nice value.
	 * 
	 * @return <code>true</code> if the margin is a auto chose nice value
	 */
	@Property(order = 3)
	public boolean isAutoTop();

	/**
	 * Set to <code>true</code> to let layout director to choose a nice value. If set to
	 * <code>false<code>, user must supply the margin value by {@link #setLeft(double)}.
	 * 
	 * @param auto
	 *            the flag
	 */
	public void setAutoLeft(boolean auto);

	/**
	 * Set to <code>true</code> to let layout director to choose a nice value. If set to
	 * <code>false<code>, user must supply the margin value by {@link #setRight(double)}.
	 * 
	 * @param auto
	 *            the flag
	 */
	public void setAutoRight(boolean auto);

	/**
	 * Set to <code>true</code> to let layout director to choose a nice value. If set to
	 * <code>false<code>, user must supply the margin value by {@link #setBottom(double)}.
	 * 
	 * @param auto
	 *            the flag
	 */
	public void setAutoBottom(boolean auto);

	/**
	 * Set to <code>true</code> to let layout director to choose a nice value. If set to
	 * <code>false<code>, user must supply the margin value by {@link #setTop(double)}.
	 * 
	 * @param auto
	 *            the flag
	 */
	public void setAutoTop(boolean auto);

	/**
	 * Returns the actual margin value.
	 * 
	 * @return the actual margin value
	 */
	@Property(order = 4)
	public double getLeft();

	/**
	 * Returns the actual margin value.
	 * 
	 * @return the actual margin value
	 */
	@Property(order = 5)
	public double getRight();

	/**
	 * Returns the actual margin value.
	 * 
	 * @return the actual margin value
	 */
	@Property(order = 6)
	public double getBottom();

	/**
	 * Returns the actual margin value.
	 * 
	 * @return the actual margin value
	 */
	@Property(order = 7)
	public double getTop();

	/**
	 * Sets the margin value. The value only take effect when {@link #isAutoLeft()} is
	 * <code>false</code>.
	 * 
	 * @param marginLeft
	 */
	public void setLeft(double marginLeft);

	/**
	 * Sets the margin value. The value only take effect when {@link #isAutoRight()} is
	 * <code>false</code>.
	 * 
	 * @param marginRight
	 */
	public void setRight(double marginRight);

	/**
	 * Sets the margin value. The value only take effect when {@link #isAutoBottom()} is
	 * <code>false</code>.
	 * 
	 * @param marginBottom
	 */
	public void setBottom(double marginBottom);

	/**
	 * Sets the margin value. The value only take effect when {@link #isAutoTop()} is
	 * <code>false</code>.
	 * 
	 * @param marginTop
	 */
	public void setTop(double marginTop);

	/**
	 * Returns the extra left margin.
	 * 
	 * @return the extra left margin
	 */
	@Property(order = 8)
	public double getExtraLeft();

	/**
	 * Returns the extra right margin.
	 * 
	 * @return the extra right margin
	 */
	@Property(order = 9)
	public double getExtraRight();

	/**
	 * Returns the extra bottom margin.
	 * 
	 * @return the extra bottom margin
	 */
	@Property(order = 10)
	public double getExtraBottom();

	/**
	 * Returns the extra top margin.
	 * 
	 * @return the extra top margin
	 */
	@Property(order = 11)
	public double getExtraTop();

	/**
	 * Sets the extra left margin.
	 * 
	 * @param marginTop
	 *            the extra left margin
	 */
	public void setExtraLeft(double marginLeft);

	/**
	 * Sets the extra right margin.
	 * 
	 * @param marginTop
	 *            the extra right margin
	 */
	public void setExtraRight(double marginRight);

	/**
	 * Sets the extra bottom margin.
	 * 
	 * @param marginTop
	 *            the extra bottom margin
	 */
	public void setExtraBottom(double marginBottom);

	/**
	 * Sets the extra top margin.
	 * 
	 * @param marginTop
	 *            the extra top margin
	 */
	public void setExtraTop(double marginTop);

}
