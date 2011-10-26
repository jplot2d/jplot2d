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
import org.jplot2d.annotation.Property;
import org.jplot2d.annotation.PropertyGroup;
import org.jplot2d.axtype.AxisType;
import org.jplot2d.transfrom.TransformType;
import org.jplot2d.util.Range;

/**
 * An axis transform define a X or Y transformation of a viewport. It can be shared by a group of
 * axes, which represent the same user range.
 * 
 * @author Jingjing Li
 * 
 */
@PropertyGroup("Axis Transform")
public interface AxisTransform extends Element {

	/**
	 * Return the type of this axis.
	 * 
	 * @return the type of this axis
	 */
	@Property(order = 0)
	public AxisType getAxisType();

	/**
	 * Set the type of the axis. An axis type can only be changed when it dosn't lock with other
	 * axes.
	 * 
	 * @param type
	 *            the axis type
	 */
	public void setAxisType(AxisType type);

	/**
	 * Returns the transform type
	 * 
	 * @return the transform type
	 */
	@Property(order = 1)
	public TransformType getType();

	/**
	 * Sets the transform type
	 * 
	 * @param txfType
	 *            the transform type
	 */
	public void setType(TransformType txfType);

	/**
	 * Returns if the displaying is "inverted": (right-left) for abscissa, (top-bottom) for
	 * ordinate.
	 * 
	 * @return if flag=true the displaying is "inverted" , if flag=false the displaying is "normal"
	 */
	@Property(order = 2)
	public boolean isInverted();

	/**
	 * Sets <code>false</code> to make this axis (and data) have "normal" displaying (left-right)
	 * for abscissa, (bottom-top) for ordinate, or <code>true</code> to have "inverted" displaying
	 * (right-left) for abscissa, (top-bottom) for ordinate.
	 * 
	 * @param flag
	 *            if flag is <code>true</code> the displaying is "inverted", if flag is
	 *            <code>false</code> the displaying is "normal"
	 */
	public void setInverted(boolean flag);

	/**
	 * Returns <code>true</code> if the margin is extended to axis major tick automatically. The
	 * minimal margin is controlled by {@link #getMarginFactor()}
	 * 
	 * @return <code>true</code> if the margin is auto-selected
	 */
	@Property(order = 3)
	public boolean isAutoMargin();

	/**
	 * Controls if the the margin is extended to axis major tick automatically.
	 * 
	 * @param autoMargin
	 *            the switch
	 */
	public void setAutoMargin(boolean autoMargin);

	/**
	 * Returns the factor that the margin will be appended to range
	 * 
	 * @return the margin factor
	 */
	@Property(order = 4)
	public double getMarginFactor();

	/**
	 * Sets the factor that the margin will be appended to range
	 * 
	 * @param factor
	 *            the margin factor
	 */
	public void setMarginFactor(double factor);

	/**
	 * Return the core range of the axis.
	 * 
	 * @return the core range
	 */
	@Property(order = 5)
	public Range getCoreRange();

	/**
	 * Set the core range of the axis. The range will expand according to the settings of autoMargin
	 * and marginFactor, and derive an actual range. All locked axes will change with this axis.
	 * <p>
	 * If user want set actual range directly by {@link #setRange(Range)}, The coreRange will be
	 * set to <code>null</code> automatically.
	 * 
	 * @param range
	 *            the core range to be set
	 */
	public void setCoreRange(Range range);

	/**
	 * Return the range of the axis. The range must be positive (start < end) even if the axis is
	 * inverted.
	 * 
	 * @return the actual range displayed
	 */
	@Property(order = 6)
	public Range getRange();

	/**
	 * Set the actual range displayed in the axis. All locked axes will change with this axis.
	 * <p>
	 * The coreRange is set to <code>null</code> after calling this method.
	 * 
	 * @param range
	 *            the actual range to be set. The range must be positive (start < end) even if the
	 *            axis is inverted.
	 */
	public void setRange(Range range);

	/**
	 * Returns the lock group to that this axis group belongs. A axis group must has a lock group,
	 * which have this axis group at least.
	 * 
	 * @return
	 */
	@Hierarchy(HierarchyOp.GET)
	public AxisRangeLockGroup getLockGroup();

	/**
	 * Join an axis lock group. The lock group must exist in the same environment, otherwise an
	 * exception will be thrown.
	 * 
	 * @param group
	 *            the lock group to join to.
	 */
	@Hierarchy(HierarchyOp.JOIN)
	public void setLockGroup(AxisRangeLockGroup group);

	/**
	 * Returns all axis tick managers belongs to this range manager.
	 * 
	 * @return all axes tick managers belongs to this range manager.
	 */
	@Hierarchy(HierarchyOp.GETARRAY)
	public AxisTickManager[] getTickManagers();

	/**
	 * Returns all layers attaching to this axis group.
	 * 
	 * @return
	 */
	@Hierarchy(HierarchyOp.GETARRAY)
	public Layer[] getLayers();

}
