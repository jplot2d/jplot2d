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
import org.jplot2d.transform.AxisTickTransform;
import org.jplot2d.util.Range;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.text.Format;

/**
 * Manages tick values and labels for axes. Axes may have the same tick manager. Their ticks are exactly same.
 * <p/>
 * The tick decision rules
 * <ol>
 * <li>if autoTickValues is false, the values can be set by {@link #setFixedTickValues(Object)}</li>
 * <li>if autoInterval is false, the interval can be set by {@link #setTickInterval(double)}</li>
 * <li>the interval is calculated according to tick number. If autoAdjustNumber is true, the number can be adjusted to
 * derive better values.</li>
 * </ol>
 *
 * @author Jingjing Li
 */
@SuppressWarnings("unused")
@PropertyGroup("Axis Tick Manager")
public interface AxisTickManager extends Element {

    int DEFAULT_TICKS_NUMBER = 11;

    /**
     * Returns the AxisTransform of this AxisTickManager.
     * <p>
     * In a plot, a plot axis must has a AxisTransform, which includes this AxisTickManager at least.
     * After removing an axis with shared AxisTransform, the AxisTransform of the removed axis is <code>null</code>.
     * Such an axis cannot be added to a plot, unless set a AxisTransform first.
     * </p>
     *
     * @return the lock group of this AxisTickManager
     */
    @Hierarchy(HierarchyOp.GET)
    @Nullable
    AxisTransform getAxisTransform();

    @Hierarchy(HierarchyOp.JOIN)
    void setAxisTransform(@Nonnull AxisTransform axisTransform);

    /**
     * Returns all axes whose ticks are controlled by this tick manager.
     *
     * @return all axes whose ticks are controlled by this tick manager
     */
    @Hierarchy(HierarchyOp.GETARRAY)
    @Nonnull
    Axis[] getAxes();

    /**
     * Returns the AxisTickTransform, which defines the transformation between user value and tick value.
     * <code>null</code> means there is no transformation between user value and tick value.
     *
     * @return the AxisTickTransform.
     */
    @Property(order = 0, styleable = false)
    @Nullable
    AxisTickTransform getTickTransform();

    /**
     * Sets the AxisTickTransform, which defines The transformation between user value and tick value.
     * The default value is <code>null</code>, means there is no transformation between user value and tick value.
     * The AxisTickTransform <em>must</em> be efficient immutable.
     *
     * @param transform the AxisTickTransform object.
     */
    void setTickTransform(@Nullable AxisTickTransform transform);

    /**
     * Returns the tick range of this axis. Return <code>null</code> if this AxisTickManager has no AxisTransform.
     *
     * @return the tick range of this axis.
     */
    @Property(order = 1, styleable = false)
    @Nullable
    Range getRange();

    /**
     * Set the tick range of the axis.
     *
     * @param range the new tick range of the axis
     */
    void setRange(@Nonnull Range range);

    /**
     * Returns <code>true</code> if tick values is auto calculated from interval,
     * or <code>false</code> if tick values is assigned by {@link #setFixedTickValues(Object)}.
     *
     * @return <code>true</code> if tick values is auto calculated.
     */
    @Property(order = 10, styleable = false)
    boolean isAutoTickValues();

    /**
     * Set to <code>true</code> to indicate the tick values need to be auto calculated from interval
     * or <code>false</code> to use user assigned values by {@link #setFixedTickValues(Object)}.
     *
     * @param atv the flag
     */
    void setAutoTickValues(boolean atv);

    /**
     * Return an array of numbers to represent the fixed tick positions set by {@link #setFixedTickValues(Object)}
     *
     * @return the labels of the ticks
     */
    @Property(order = 11, styleable = false)
    @Nullable
    Object getFixedTickValues();

    /**
     * Set an array of values that ticks will show. If the given values is null or a empty array, no tick will be drawn.
     * The autoValues will be set to false when this method is called. The minor values will be cleared by this method.
     *
     * @param values the values in user unit where ticks are displayed.
     */
    void setFixedTickValues(@Nullable Object values);

    /**
     * Return an array of numbers to represent the fixed minor tick positions set by {@link #setFixedMinorTickValues(Object)}
     *
     * @return the labels of the ticks
     */
    @Property(order = 12, styleable = false)
    @Nullable
    Object getFixedMinorTickValues();

    /**
     * Set an array of <code>double</code>. If the given values is null or a empty array, no tick will be drawn.
     * <p/>
     * This method <b>must</b> be called after {@link #setFixedTickValues(Object)} is called.
     * {@link #setFixedTickValues(Object)} calling will clear the minor values.
     *
     * @param minorValues the values in user unit where minor ticks are displayed.
     */
    void setFixedMinorTickValues(@Nullable Object minorValues);

    /**
     * Returns true if the tick interval is auto calculated from tick number and range,
     * or false if the tick interval is assigned by {@link #setTickInterval(double)}
     *
     * @return if the tick number is auto calculated.
     */
    @Property(order = 13, styleable = false)
    boolean isAutoTickInterval();

    /**
     * Set to <code>true</code> to indicate the tick interval need to be auto calculated, or <code>false</code> to
     * disable auto-calculation and keep using the current interval.
     *
     * @param ati the flag
     */
    void setAutoTickInterval(boolean ati);

    /**
     * Return the interval between two ticks. For LOG axis, interval is the multiplying factor between two ticks.
     *
     * @return the interval between two ticks
     */
    @Property(order = 14, styleable = false)
    double getTickInterval();

    /**
     * Set the interval between two ticks. It changes the number of ticks displayed in the axis.
     * For LOG axis, interval is the multiplying factor between two ticks.
     * <p/>
     * Setting a new interval will set autoInterval to <code>false</code>.
     *
     * @param interval the interval between two major ticks
     * @see #setAutoTickInterval(boolean)
     */
    void setTickInterval(double interval);

    /**
     * Return the offset from the default tick values.
     *
     * @return the offset of ticks
     */
    @Property(order = 15, styleable = false)
    double getTickOffset();

    /**
     * Sets the the offset from the default tick values.
     *
     * @param offset the offset of ticks
     */
    void setTickOffset(double offset);

    /**
     * Returns the proposed number of ticks. The actual number may be different from the given number, to get nice tick
     * values.
     *
     * @return the number of ticks
     */
    @Property(order = 16, styleable = false)
    int getTickNumber();

    /**
     * Set the proposed number of ticks in the axis. The actual number may be different from the given number, to get
     * nice tick values. If the ticks may cause the tick interval close to precision limit, the actual ticks may be less
     * than the given value without throw PrecisionException. The default value is 11.
     *
     * @param tickNumber the number of ticks.
     */
    void setTickNumber(int tickNumber);

    /**
     * Returns if the tick number is allowed to be automatically reduced to avoid tick labels overlap.
     *
     * @return <code>true</code> if the tick number is auto calculated.
     */
    @Property(order = 17, styleable = false)
    boolean isAutoReduceTickNumber();

    /**
     * Set to true to allow the tick number to be automatically reduced to avoid tick labels overlap.
     * Otherwise the font size of labels will be reduced to avoid overlapping. It's enabled by default.
     *
     * @param flag the flag
     */
    void setAutoReduceTickNumber(boolean flag);

    /**
     * Returns if the minor ticks number is derived from tick interval.
     *
     * @return <code>true</code> if the minor ticks number is derived from tick interval.
     */
    @Property(order = 18, styleable = false)
    boolean isAutoMinorTicks();

    /**
     * When autoTickInterval is set to true:
     * <ul>
     * <li>if tick interval is 1, the minor interval is 0.2, create 4 minor ticks</li>
     * <li>if tick interval is 2, the minor interval is 0.5, create 3 minor ticks</li>
     * <li>if tick interval is 3, the minor interval is 1, create 2 minor ticks</li>
     * <li>if tick interval is 4, the minor interval is 1, create 3 minor ticks</li>
     * <li>if tick interval is 5, the minor interval is 1, create 4 minor ticks</li>
     * <li>if tick interval is 6, the minor interval is 2, create 2 minor ticks</li>
     * <li>if tick interval is 7, the minor interval is 1, create 6 minor ticks</li>
     * <li>if tick interval is 8, the minor interval is 2, create 3 minor ticks</li>
     * <li>if tick interval is 9, the minor interval is 3, create 2 minor ticks</li>
     * <li>if tick interval is not integer, create 0 minor ticks</li>
     * </ul>
     * when autoTickInterval is set to false: minor ticks is exactly the user set by {@link #setMinorTickNumber(int)}.
     * The Default number is zero, means not shown by default.
     *
     * @param flag the flag
     */
    void setAutoMinorTicks(boolean flag);

    /**
     * Return the number of minor ticks displayed between two major ticks.
     * This method always returns the number set by {@link #setMinorTickNumber(int)}, no matter if the actual number has been adjusted.
     *
     * @return the number of minor ticks displayed
     */
    @Property(order = 19, styleable = false)
    int getMinorTickNumber();

    /**
     * Set the number of minor ticks displayed between two major ticks.
     * The actual number may be adjusted, and not equal the number set by this method. The default value is 0.
     * <p/>
     * Setting a new minor number will set autoMinornumber to <code>false</code>.
     *
     * @param minors the number of minor ticks displayed
     */
    void setMinorTickNumber(int minors);

    /**
     * Return the number of minor ticks displayed between two major ticks. This method returns the actual number, might
     * be different form the number set by {@link #setMinorTickNumber(int)}.
     *
     * @return the number of minor ticks displayed
     */
    @Property(order = 20, styleable = false)
    int getActualMinorTickNumber();

    /**
     * Return an array of number representing the tick positions shown in the axis.
     *
     * @return the values of the ticks
     */
    @Property(order = 21, styleable = false)
    @Nonnull
    Object getTickValues();

    /**
     * Return an array of number representing the minor tick positions shown in the axis.
     *
     * @return the values of the ticks
     */
    @Property(order = 22, styleable = false)
    @Nonnull
    Object getMinorTickValues();

	/* =========================== Labels ============================= */

    /**
     * Get the labels interval. The tick labels are displayed after the number of ticks.
     *
     * @return labels interval
     */
    @Property(order = 30, styleable = false)
    int getLabelInterval();

    /**
     * Set the tick labels displayed after the given number of ticks.
     *
     * @param n labels displayed every n ticks
     */
    void setLabelInterval(int n);

    /**
     * Returns if labels format is auto calculated or keep using the current format.
     *
     * @return <code>true</code> if labels format is auto calculated
     */
    @Property(order = 40, styleable = false)
    boolean isAutoLabelFormat();

    /**
     * Sets <code>true</code> to auto calculate a proper labels format. Or sets <code>false</code> to keep using the current label format.
     *
     * @param alf the flag
     */
    void setAutoLabelFormat(boolean alf);

    /**
     * Returns the java.text.Format object that is used to format the tick labels.
     *
     * @return the java.text.Format object
     */
    @Property(order = 41, styleable = false)
    @Nullable
    Format getLabelTextFormat();

    /**
     * Sets the java.text.Format object that is used to format the tick labels from tick values.
     * If this text format is null, the printf-style format will be used to format the labels.
     * <p/>
     * Setting a new labelTextFormat will set autoLabelFormat to <code>false</code>
     *
     * @param format the format object
     */
    void setLabelTextFormat(@Nullable Format format);

    /**
     * Returns the printf-style format of the labels of ticks.
     *
     * @return the format of the labels.
     */
    @Property(order = 42, styleable = false)
    @Nullable
    String getLabelFormat();

    /**
     * Sets the printf-style format that is used to format the tick labels from tick values.
     * The syntax of format string is described in {@link java.util.Formatter}.
     * Beside the standard Java formatter conversions, a special conversion 'm' can be used to produce labels in n_10^e format.
     * The usage of conversion 'm' is like the standard conversion 'e'.
     * <p/>
     * Setting a new labelFormat will set autoLabelFormat to <code>false</code>
     *
     * @param format the format string, or null to used the auto format.
     */
    void setLabelFormat(@Nullable String format);

    /**
     * Returns the user assigned substitute labels. <code>null</code> in the array has a special meaning of "undecided".
     *
     * @return the user assigned substitute labels.
     */
    @Property(order = 43, styleable = false)
    @Nonnull
    String[] getFixedLabelStrings();

    /**
     * Set an array of String to substitute the labels of the fixed ticks.
     * If the length of the array is shorter then the number of fixed ticks, only the first ticks are changed,
     * if the length of the array is longer then the number of ticks only the first labels are used.
     * <code>null</code> in the array means not substitute the label. The given labels can be null or a empty array.
     * <p/>
     * If the autoTickValues is enabled(a.k.a. no fixedValues assigned), the given fixedLabels will substitute the auto
     * labels of visible ticks.
     *
     * @param labels the new labels to be displayed in the ticks
     * @see #setFixedTickValues(Object)
     */
    void setFixedLabelStrings(@Nullable String[] labels);

    /**
     * Return an array of string representing the labels shown in the axis.
     *
     * @return the labels of the ticks
     */
    @Property(order = 44, styleable = false)
    @Nonnull
    String[] getLabelStrings();

}
