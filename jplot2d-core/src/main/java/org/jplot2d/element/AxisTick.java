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

import java.awt.Color;
import java.awt.Font;
import java.text.Format;

/**
 * @author Jingjing Li
 * 
 */
public interface AxisTick extends Element {

	public static final int DEFAULT_TICKS_NUMBER = 11;

	public Axis getParent();

	public boolean isTickCalcNeeded();

	/**
	 * Calculate ticks when tick calculation is needed.
	 */
	public void calcTicks();

	/**
	 * Returns if the tick mark is shown or not
	 * 
	 * @return if the tick mark is shown or not
	 */
	public boolean getVisible();

	/**
	 * Sets if the tick mark is shown or not
	 * 
	 * @param visible
	 */
	public void setVisible(boolean visible);

	/**
	 * Returns true if the tick number is allowed to be automatically reduced to
	 * avoid tick label overlaps.
	 * 
	 * @return if the tick number is auto calculated.
	 */
	public boolean getAutoAdjustNumber();

	/**
	 * Set to true to allow the tick number to be automatically reduced to avoid
	 * tick label overlaps.
	 * 
	 * @param flag
	 */
	public void setAutoAdjustNumber(boolean flag);

	/**
	 * Returns the proposed tick number.
	 * 
	 * @return the number of ticks
	 */
	public int getNumber();

	/**
	 * Set the proposed tick number in the axis. The actual tick number may be
	 * different from the given number, to get nice tick values. If the ticks
	 * may cause the tick interval close to precision limit, the actual ticks
	 * may be less than the given value without throw PrecisionException.
	 * 
	 * @param tickNumber
	 *            the number of ticks.
	 */
	public void setNumber(int tickNumber);

	/**
	 * Returns true if the tick interval is auto calculated from tick number and
	 * range, or false if the tick interval is assigned by
	 * {@link #setTickInterval(double)}
	 * 
	 * @return if the tick number is auto calculated.
	 */
	public boolean getAutoInterval();

	/**
	 * Set to true to indicate the tick interval need to be auto calculated or
	 * false to use user assigned or null to use the default value of
	 * implementor.
	 * <p>
	 * Protocol: The caller <b>must</b> call this method before calling
	 * {@link #setInterval(double, double)}
	 * 
	 * @param ati
	 *            the flag
	 */
	public void setAutoInterval(boolean ati);

	/**
	 * Return the interval in axis units between two ticks. For LOG axis,
	 * interval is the multiplying factor between two ticks.
	 * 
	 * @return the interval between two ticks
	 */
	public double getInterval();

	/**
	 * Return the offset of ticks.
	 * 
	 * @return the offset.
	 */
	public double getOffset();

	/**
	 * Set the interval in axis units between two ticks. It change the number of
	 * ticks displayed in the axis. For LOG axis, interval is the multiplying
	 * factor between two ticks.<br>
	 * If the given number is 0, the actual interval will be calculated from
	 * tick number, and the {@link #getAutoInterval()} will returns true.
	 * <p>
	 * Protocol: Before calling this method, the caller <b>must</b> call
	 * setAutoTickInterval(<code>false</code>) first.
	 * 
	 * @param interval
	 *            the interval between two major ticks
	 * @see #setAutoInterval(Boolean)
	 */
	public void setInterval(double interval);

	/**
	 * @param offset
	 *            the offset of ticks
	 */
	public void setOffset(double offset);

	/**
	 * Return an array of <code>double</code> representing the tick positions
	 * shown in the axis.
	 * 
	 * @return the labels of the ticks
	 */
	public Object getValues();

	/**
	 * Return an array of <code>double</code> representing the minor tick
	 * positions shown in the axis.
	 * 
	 * @return the labels of the ticks
	 */
	public Object getMinorValues();

	/**
	 * Returns <code>true</code> if tick values is auto calculated from
	 * interval, or <code>false</code> if tick values is assigned by
	 * {@link #setFixedValues(double[])}.
	 * 
	 * @return <code>true</code> if tick values is auto calculated.
	 */
	public boolean isAutoValues();

	/**
	 * Set to <code>true</code> to indicate the tick values need to be auto
	 * calculated or <code>false</code> to use user assigned values or
	 * <code>null</code> to use the default value of implementor.
	 * <p>
	 * Protocol: The caller <b>must</b> call this method before calling
	 * {@link #setFixedValues(double[])}
	 * 
	 * @param atv
	 *            the flag
	 */
	public void setAutoValues(boolean atv);

	/**
	 * Return an array of <code>double</code> representing the fixed tick
	 * positions set by {@link #setFixedValues(Object)}
	 * 
	 * @return the labels of the ticks
	 */
	public Object getFixedValues();

	/**
	 * Set an array of <code>double</code>. If the given values is null or a
	 * empty array, no tick will be drawn.
	 * <p>
	 * Protocol: The autoValues <b>must</b> be false when this method is called.
	 * The minor values wil be cleared by this method.
	 * 
	 * @param values
	 *            the values in user unit where ticks are displayed.
	 */
	public void setFixedValues(Object values);

	/**
	 * Return an array of <code>double</code> representing the fixed minor tick
	 * positions set by {@link #setFixedMinorValues(Object)}
	 * 
	 * @return the labels of the ticks
	 */
	public Object getFixedMinorValues();

	/**
	 * Set an array of <code>double</code>. If the given values is null or a
	 * empty array, no tick will be drawn.
	 * <p>
	 * Protocol: The autoValues <b>must</b> be false when this method is called.
	 * {@link #setFixedValues(double[])} calling will clear the minor values.
	 * 
	 * @param values
	 *            the values in user unit where minor ticks are displayed.
	 */
	public void setFixedMinorValues(Object minorValues);

	/**
	 * Return if the grid line is displayed or not.
	 * 
	 * @return true if the grid line is displayed
	 */
	public boolean isGridLines();

	/**
	 * Sets grid lines of grey lines in corresponding of major ticks of the
	 * axis.
	 * 
	 * @param showGridLines
	 *            if true show the grid lines.
	 */
	public void setGridLines(boolean showGridLines);

	/**
	 * Returns the physical height of the major ticks of the axis
	 * 
	 * @return the height of the ticks
	 */
	public double getTickHeight();

	/**
	 * Set the physical height of the major ticks of the axis.
	 * 
	 * @param height
	 *            the new height of the ticks
	 */
	public void setTickHeight(double height);

	/**
	 * Return the direction of the ticks
	 */
	public AxisTickSide getSide();

	/**
	 * Set the side position of the ticks
	 */
	public void setSide(AxisTickSide side);

	/**
	 * Returns <code>true</code> is the minor ticks number is derived from tick
	 * interval.
	 * 
	 * @return <code>true</code> is the minor ticks number is derived from tick
	 *         interval.
	 */
	public boolean isAutoMinorNumber();

	/**
	 * When autoTickInterval is set to true:
	 * <ul>
	 * <li>if tick interval is 1, the minor interval is 0.2, create 4 minor
	 * ticks</li>
	 * <li>if tick interval is 2, the minor interval is 0.5, create 3 minor
	 * ticks</li>
	 * <li>if tick interval is 3, the minor interval is 1, create 2 minor ticks</li>
	 * <li>if tick interval is 4, the minor interval is 1, create 3 minor ticks</li>
	 * <li>if tick interval is 5, the minor interval is 1, create 4 minor ticks</li>
	 * <li>if tick interval is 6, the minor interval is 2, create 2 minor ticks</li>
	 * <li>if tick interval is 7, the minor interval is 1, create 6 minor ticks</li>
	 * <li>if tick interval is 8, the minor interval is 2, create 3 minor ticks</li>
	 * <li>if tick interval is 9, the minor interval is 3, create 2 minor ticks</li>
	 * <li>if tick interval is not integer, create 0 minor ticks</li>
	 * </ul>
	 * when autoTickInterval is set to false: minor ticks is exactly the user
	 * set by {@link #setMinorNumber(Integer)}. The Default number is zero,
	 * means not shown by default.
	 * 
	 * @param flag
	 */
	public void setAutoMinorNumber(boolean flag);

	/**
	 * Return the number of minor ticks displayed between two major ticks. When
	 * autoMinorTicks is <code>true</code>, this method returns the actual minor
	 * ticks number, otherwise returns the number set by
	 * {@link #setMinorNumber(Integer)}, no matter if the actual number has been
	 * adjusted.
	 * 
	 * @return the number of minor ticks displayed
	 */
	public int getMinorNumber();

	/**
	 * Set the number of minor ticks displayed between two major ticks, only
	 * when autoMinorTicks is <code>false</code>. The actual number may be
	 * adjusted (for LOG axis), and not equal the number set by this method.
	 * <p>
	 * Protocol: autoMinorTicks must be set to <code>false</code>, before
	 * calling this method, otherwise an exception will be thrown.
	 * 
	 * @param minors
	 *            the number of minor ticks displayed
	 */
	public void setMinorNumber(int minors);

	/**
	 * Return the physical height of the minor ticks of the axis.
	 * 
	 * @return the physical height of the minor ticks.
	 */
	public double getMinorHeight();

	/**
	 * Set the physical height of the minor ticks of the axis.
	 * 
	 * @param height
	 *            the physical height of the ticks
	 */
	public void setMinorHeight(double height);

	/* =========================== Labels ============================= */

	/**
	 * Returns if the tick labels is shown or not
	 * 
	 * @return if the tick mark is shown or not
	 */
	public boolean isLabelVisible();

	/**
	 * Sets if the tick labels is shown or not
	 * 
	 * @param visible
	 */
	public void setLabelVisible(boolean visible);

	/**
	 * Returns <code>true</code> if labels format is auto calculated or assigned
	 * by {@link #setLabelFormat(String)}
	 * 
	 * @return <code>true</code> if labels format is auto calculated
	 */
	public boolean isAutoLabelFormat();

	/**
	 * Sets <code>true</code> to auto calculate proper labels format. Or sets
	 * <code>false</code> to assign a label format by
	 * {@link #setLabelFormat(String)}
	 * 
	 * @param alf
	 *            the flag
	 */
	public void setAutoLabelFormat(boolean alf);

	/**
	 * Returns the format of the labels of ticks.
	 * 
	 * @return the format of the labels.
	 */
	public Format getLabelTextFormat();

	/**
	 * Sets the format object that is used to format the tick labels from tick
	 * values. If this text format is null, the printf-style format will be used
	 * to format the labels.
	 * 
	 * @param format
	 *            the format object
	 */
	public void setLabelTextFormat(Format format);

	/**
	 * Returns the printf-style format of the labels of ticks.
	 * 
	 * @return the format of the labels.
	 */
	public String getLabelFormat();

	/**
	 * Sets the printf-style format that is used to format the tick labels from
	 * tick values. The syntax of format string is described in
	 * {@link java.util.Formatter}. Beside the standard Java formatter
	 * conversions, a special conversion 'm' can be used to produce labels in
	 * n_10^e format. The usage of conversion 'm' is like the standard
	 * conversion 'e'.
	 * <p>
	 * Protocol: This method must be called when AutoLabelFormat is set to
	 * <code>false</code>, otherwise an exception will be thrown.
	 * 
	 * @param format
	 *            the format string, or null to used the auto format.
	 */
	public void setLabelFormat(String format);

	/**
	 * Returns the user assigned substitute labels. <code>null</code> has a
	 * special meaning of "undecided".
	 * 
	 * @return the user assigned substitute labels.
	 */
	public String[] getFixedLabelStrings();

	/**
	 * Set an array of String to substitute the labels of the fixed ticks. If
	 * the length of the array is shorter then the number of fixed ticks, only
	 * the first ticks are changed, if the length of the array is longer then
	 * the number of ticks only the first labels are used. null in the array
	 * means not substitute the label. The given labels can be null or a empty
	 * array.
	 * <p>
	 * If the tick is autoValues mode (a.k.a. no fixedValues assigned), the
	 * given fixedLabels will substitute the auto labels of visible ticks.
	 * 
	 * @see #setFixedValues(Object)
	 * @param labels
	 *            the new labels to be displayed in the ticks
	 */
	public void setFixedLabelStrings(String[] labels);

	/**
	 * Return the color of the labels of the ticks
	 * 
	 * @return the color of the labels
	 */
	public Color getLabelColor();

	/**
	 * Set the color of the labels of the ticks
	 * 
	 * @param color
	 *            the color of the labels
	 */
	public void setLabelColor(Color color);

	/**
	 * Returns the font of this component.
	 * 
	 * @return the font of this component
	 * @see #setFont
	 */
	public Font getLabelFont();

	/**
	 * Sets the font for this component.
	 * 
	 * @param font
	 *            the desired <code>Font</code> for this component
	 * @see #getFont
	 */
	public void setLabelFont(Font font);

	/**
	 * Returns the name of the font.
	 * 
	 * @return the name of the font.
	 */
	public String getLabelFontName();

	/**
	 * Apply the new font with the given name
	 * 
	 * @param name
	 *            the font name.
	 */
	public void setLabelFontName(String name);

	/**
	 * Returns the style of the font. The style can be PLAIN, BOLD, ITALIC, or
	 * BOLD+ITALIC.
	 * 
	 * @return the style of the font
	 * @see java.awt.Font
	 */
	public int getLabelFontStyle();

	/**
	 * Apply a new style to the font. The style can be PLAIN, BOLD, ITALIC, or
	 * BOLD+ITALIC.
	 * 
	 * @param style
	 *            the style to apply
	 * @see java.awt.Font
	 */
	public void setLabelFontStyle(int style);

	/**
	 * Returns the Font size.
	 * 
	 * @return the font size.
	 */
	public float getLabelFontSize();

	/**
	 * Sets a new size of the string.
	 * 
	 * @param size
	 *            the new size of the font.
	 */
	public void setLabelFontSize(float size);

	/**
	 * Return the position of the label of the ticks
	 */
	public AxisLabelSide getLabelSide();

	/**
	 * Set the position of the label of the ticks respect of the axis
	 */
	public void setLabelSide(AxisLabelSide side);

	/**
	 * Get the label interval. The tick labels are displayed after that number
	 * of ticks
	 * 
	 * @return label interval
	 */
	public int getLabelInterval();

	/**
	 * Set the tick labels displayed after n ticks
	 * 
	 * @param n
	 *            label displayed each n ticks .
	 */
	public void setLabelInterval(int n);

	/**
	 * Get the Orientation of the labels.
	 * 
	 * @return label orientation:<br>
	 *         0 HORIZONTAL<br>
	 *         1 VERTICAL
	 */
	public AxisOrientation getLabelOrientation();

	/**
	 * Set the orientation of the labels of the ticks <br>
	 * 
	 * @param orientation
	 *            HORIZONTAL/VERTICAL
	 */
	public void setLabelOrientation(AxisOrientation orientation);

	public String[] getLabelStrings();

}
