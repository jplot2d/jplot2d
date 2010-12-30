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
 * Subplot has a content area in the center, surrounded by margin area. The
 * margin area holds axes, title and legend.
 * 
 * @author Jingjing Li
 * 
 */
public interface SubplotMargin extends Element {

	/**
	 * Returns <code>true</code> if the margin is a auto chose nice value.
	 * 
	 * @return <code>true</code> if the margin is a auto chose nice value
	 */
	public boolean isAutoMarginTop();

	/**
	 * Returns <code>true</code> if the margin is a auto chose nice value.
	 * 
	 * @return <code>true</code> if the margin is a auto chose nice value
	 */
	public boolean isAutoMarginLeft();

	/**
	 * Returns <code>true</code> if the margin is a auto chose nice value.
	 * 
	 * @return <code>true</code> if the margin is a auto chose nice value
	 */
	public boolean isAutoMarginBottom();

	/**
	 * Returns <code>true</code> if the margin is a auto chose nice value.
	 * 
	 * @return <code>true</code> if the margin is a auto chose nice value
	 */
	public boolean isAutoMarginRight();

	/**
	 * Set to <code>true</code> to let layout director to choose a nice value,
	 * plus the extra value, as the actual margin. If set to
	 * <code>false<code>, user must supply the margin value by {@link #setMarginTop(double)}.
	 * 
	 * @param auto
	 *            the flag
	 */
	public void setAutoMarginTop(boolean auto);

	/**
	 * Set to <code>true</code> to let layout director to choose a nice value,
	 * plus the extra value, as the actual margin. If set to
	 * <code>false<code>, user must supply the margin value by {@link #setMarginLeft(double)}.
	 * 
	 * @param auto
	 *            the flag
	 */
	public void setAutoMarginLeft(boolean auto);

	/**
	 * Set to <code>true</code> to let layout director to choose a nice value,
	 * plus the extra value, as the actual margin. If set to
	 * <code>false<code>, user must supply the margin value by {@link #setMarginBottom(double)}.
	 * 
	 * @param auto
	 *            the flag
	 */
	public void setAutoMarginBottom(boolean auto);

	/**
	 * Set to <code>true</code> to let layout director to choose a nice value,
	 * plus the extra value, as the actual margin. If set to
	 * <code>false<code>, user must supply the margin value by {@link #setMarginRight(double)}.
	 * 
	 * @param auto
	 *            the flag
	 */
	public void setAutoMarginRight(boolean auto);

	/**
	 * Returns the actual margin value.
	 * 
	 * @return the actual margin value
	 */
	public double getMarginTop();

	/**
	 * Returns the actual margin value.
	 * 
	 * @return the actual margin value
	 */
	public double getMarginLeft();

	/**
	 * Returns the actual margin value.
	 * 
	 * @return the actual margin value
	 */
	public double getMarginBottom();

	/**
	 * Returns the actual margin value.
	 * 
	 * @return the actual margin value
	 */
	public double getMarginRight();

	/**
	 * Sets the margin value. The value only take effect when
	 * {@link #isAutoMarginTop()} is <code>false</code>.
	 * 
	 * @param marginTop
	 */
	public void setMarginTop(double marginTop);

	/**
	 * Sets the margin value. The value only take effect when
	 * {@link #isAutoMarginLeft()} is <code>false</code>.
	 * 
	 * @param marginTop
	 */
	public void setMarginLeft(double marginLeft);

	/**
	 * Sets the margin value. The value only take effect when
	 * {@link #isAutoMarginBottom()} is <code>false</code>.
	 * 
	 * @param marginTop
	 */
	public void setMarginBottom(double marginBottom);

	/**
	 * Sets the margin value. The value only take effect when
	 * {@link #isAutoMarginRight()} is <code>false</code>.
	 * 
	 * @param marginTop
	 */
	public void setMarginRight(double marginRight);

	/**
	 * Returns the extra top margin plus to the auto margin value.
	 * 
	 * @return the extra top margin
	 */
	public double getExtraTop();

	/**
	 * Returns the extra left margin plus to the auto margin value.
	 * 
	 * @return the extra left margin
	 */
	public double getExtraLeft();

	/**
	 * Returns the extra top margin plus to the auto margin value.
	 * 
	 * @return the extra top margin
	 */
	public double getExtraBottom();

	/**
	 * Returns the extra top margin plus to the auto margin value.
	 * 
	 * @return the extra top margin
	 */
	public double getExtraRight();

	/**
	 * Sets the extra top margin plus to the auto margin value.
	 * 
	 * @param marginTop
	 *            the extra top margin
	 */
	public void setExtraTop(double marginTop);

	/**
	 * Sets the extra top margin plus to the auto margin value.
	 * 
	 * @param marginTop
	 *            the extra top margin
	 */
	public void setExtraLeft(double marginLeft);

	/**
	 * Sets the extra top margin plus to the auto margin value.
	 * 
	 * @param marginTop
	 *            the extra top margin
	 */
	public void setExtraBottom(double marginBottom);

	/**
	 * Sets the extra top margin plus to the auto margin value.
	 * 
	 * @param marginTop
	 *            the extra top margin
	 */
	public void setExtraRight(double marginRight);

}
