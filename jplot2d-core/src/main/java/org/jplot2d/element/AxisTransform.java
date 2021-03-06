/*
 * Copyright 2010-2015 Jingjing Li.
 *
 * This file is part of jplot2d.
 *
 * jplot2d is free software:
 * you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation, either version 3 of the License, or any later version.
 *
 * jplot2d is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Lesser Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with jplot2d.
 * If not, see <http://www.gnu.org/licenses/>.
 */
package org.jplot2d.element;

import org.jplot2d.annotation.Hierarchy;
import org.jplot2d.annotation.HierarchyOp;
import org.jplot2d.annotation.Property;
import org.jplot2d.annotation.PropertyGroup;
import org.jplot2d.axtype.AxisType;
import org.jplot2d.transform.NormalTransform;
import org.jplot2d.transform.TransformType;
import org.jplot2d.util.Range;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * An axis transform define the transformation between world space and paper space along the X/Y direction.
 * It can be shared by a group of {@link AxisTickManager}, which are in the same user range.
 *
 * @author Jingjing Li
 */
@SuppressWarnings("unused")
@PropertyGroup("Axis Transform")
public interface AxisTransform extends Element {

    /**
     * Return the type of this axis.
     *
     * @return the type of this axis
     */
    @Property(order = 0, styleable = false)
    @Nonnull
    AxisType getType();

    /**
     * Set the type of the axis. An axis type can only be changed when it doesn't lock with other axes.
     *
     * @param type the axis type
     */
    void setType(@Nonnull AxisType type);

    /**
     * Returns the transform type
     *
     * @return the transform type
     */
    @Property(order = 1, styleable = false)
    @Nonnull
    TransformType getTransform();

    /**
     * Sets the transform type
     *
     * @param txfType the transform type
     */
    void setTransform(@Nonnull TransformType txfType);

    /**
     * Returns <code>true</code> if this AxisTransform is inverted.
     *
     * @return <code>true</code> if this AxisTransform is inverted
     */
    @Property(order = 2, styleable = false)
    boolean isInverted();

    /**
     * Sets <code>false</code> to make this AxisTransform have "normal" displaying,
     * or <code>true</code> to have "inverted" displaying.
     *
     * @param flag if flag is <code>true</code> this AxisTransform is inverted,
     *             if flag is <code>false</code> this AxisTransform is normal
     */
    void setInverted(boolean flag);

    /**
     * Returns <code>true</code> if 2 margins are appended automatically to extend the range to a pair of axis major ticks.
     * The minimal margin is controlled by {@link #getMarginFactor()}
     *
     * @return <code>true</code> if the margin is auto-selected
     */
    @Property(order = 3, styleable = false)
    boolean isAutoMargin();

    /**
     * Controls if 2 margins are appended automatically to extend the range to a pair of axis major ticks.
     * The default value is <code>true</code>.
     *
     * @param autoMargin the switch
     */
    void setAutoMargin(boolean autoMargin);

    /**
     * Returns the factor that the 2 margins will be appended to range on the both ends.
     *
     * @return the margin factor
     */
    @Property(order = 4, styleable = false)
    double getMarginFactor();

    /**
     * Sets the factor that the 2 margins will be appended to range on the both ends. The default value is 1/32(of the axis range).
     *
     * @param factor the margin factor
     */
    void setMarginFactor(double factor);

    /**
     * Return the core range of the AxisTransform.
     * The returned range will be negative ({@code start > end}) if this AxisTransform is inverted.
     *
     * @return the core range
     */
    @Property(order = 5, styleable = false)
    @Nullable
    Range getCoreRange();

    /**
     * Set the core range of the AxisTransform. The range will expand according to the settings of autoMargin and marginFactor, and derive an actual range.
     * The given range can be positive ({@code start < end}) or negative ({@code start > end}). It has not effect on the property "inverted".
     * All locked axes will follow the change of this axis.
     * <p>
     * If user want set actual range directly by {@link #setRange(Range)}, the coreRange will be set to <code>null</code> automatically.
     *
     * @param range the core range to be set
     */
    void setCoreRange(@Nullable Range range);

    /**
     * Return the range of the AxisTransform. The returned range will be negative ({@code start > end})
     * if this AxisTransform is inverted.
     *
     * @return the actual range displayed
     */
    @Property(order = 6, styleable = false)
    @Nonnull
    Range getRange();

    /**
     * Set the actual range displayed in the AxisTransform.
     * The given range can be positive ({@code start < end}) or negative ({@code start > end}).
     * It has not effect on the property "inverted". All locked axes will follow the change of this axis.
     * <p>
     * The coreRange is set to <code>null</code> after calling this method.
     *
     * @param range the actual range to be set.
     */
    void setRange(@Nonnull Range range);

    /**
     * Returns the normal transform of this AxisTransform
     *
     * @return the normal transform
     */
    @Nonnull
    NormalTransform getNormalTransform();

    /**
     * Returns the lock group of this AxisTransform.
     * <p>
     * In a plot, a plot axis must has a lock group, which includes this AxisTransform at least.
     * After removing an axis with shared lock group, the lock group of the removed axis is <code>null</code>.
     * Such an axis cannot be added to a plot, unless set a lock group first.
     * </p>
     * <p>
     * The lock group of color bar axis is always <code>null</code>.
     * </p>
     *
     * @return the lock group of this AxisTransform
     */
    @Hierarchy(HierarchyOp.GET)
    @Nullable
    AxisRangeLockGroup getLockGroup();

    /**
     * Join an axis lock group.
     * The lock group must exist in the same environment, otherwise an exception will be thrown.
     *
     * @param group the lock group to join to.
     */
    @Hierarchy(HierarchyOp.JOIN)
    void setLockGroup(@Nonnull AxisRangeLockGroup group);

    /**
     * Returns all axis tick managers belongs to this range manager.
     *
     * @return all axes tick managers belongs to this range manager.
     */
    @Hierarchy(HierarchyOp.GETARRAY)
    @Nonnull
    AxisTickManager[] getTickManagers();

    /**
     * Returns all layers attaching to this AxisTransform.
     *
     * @return all layers attaching to this AxisTransform
     */
    @Hierarchy(HierarchyOp.GETARRAY)
    @Nonnull
    Layer[] getLayers();

}
