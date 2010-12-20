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

import org.jplot2d.util.Range2D;

/**
 * @author Jingjing Li
 * 
 */
public interface Axis extends Container {

	/**
	 * Return the position of the axis: PlotConstant.LEFT or PlotConstant.RIGHT
	 * for y axis, PlotConstant.BOTTOM or PlotConstant.TOP for x axis.
	 * 
	 * @return the position of the axis in the plot.
	 */
	public AxisPosition getPosition();

	/**
	 * Set the position of the axis: PlotConstant.LEFT or PlotConstant.RIGHT for
	 * y axis, PlotConstant.BOTTOM or PlotConstant.TOP for x axis. Only can be
	 * set when autoPosition is not True.
	 * 
	 * @param position
	 *            the position of the axis in the plot.
	 */
	public void setPosition(AxisPosition position);

	/**
	 * Returns the AxisTickTransform, which defines The relationship between
	 * user value and tick value.
	 * 
	 * @return the AxisTickTransform.
	 */
	public AxisTickTransform getTickTransform();

	/**
	 * Sets the AxisTickTransform, which defines The relationship between user
	 * value and tick value. The AuxTransform <em>must</em> be efficient
	 * immutable.
	 * 
	 * @param transform
	 *            the AxisTickTransform object.
	 */
	public void setTickTransform(AxisTickTransform transform);

	/**
	 * Returns the tick range of this axis.
	 * 
	 * @return the tick range of this axis.
	 */
	public Range2D getRange();

	/**
	 * Set the tick range of the axis.
	 * 
	 * @param range
	 *            the new tick range of the axis
	 */
	public void setRange(Range2D range);

	/**
	 * Set the tick range of the axis.
	 * 
	 * @param start
	 *            the start value of new tick range of the axis
	 * @param end
	 *            the end value of new tick range of the axis
	 */
	public void setRange(double start, double end);

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
	 * Returns if the tick mark is shown or not
	 * 
	 * @return if the tick mark is shown or not
	 */
	public boolean isTickVisible();

	/**
	 * Sets if the tick mark is shown or not
	 * 
	 * @param visible
	 */
	public void setTickVisible(boolean visible);

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

	/**
	 * Return the direction of the ticks
	 */
	public AxisTickSide getTickSide();

	/**
	 * Set the side position of the ticks
	 */
	public void setTickSide(AxisTickSide side);

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
	 * Return the physical height of the minor ticks of the axis.
	 * 
	 * @return the physical height of the minor ticks.
	 */
	public double getMinorTickHeight();

	/**
	 * Set the physical height of the minor ticks of the axis.
	 * 
	 * @param height
	 *            the physical height of the ticks
	 */
	public void setMinorTickHeight(double height);

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
	public Font getEffectiveLabelFont();

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
	 * Returns the tick of this axis.
	 * 
	 * @return the tick of this axis
	 */
	public AxisTick getTick();

	/**
	 * Returns the title of this axis.
	 * 
	 * @return the title of this axis
	 */
	public TextComponent getTitle();

}
