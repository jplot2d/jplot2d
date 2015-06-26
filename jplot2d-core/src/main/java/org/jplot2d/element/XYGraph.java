/**
 * Copyright 2010, 2011 Jingjing Li.
 * <p/>
 * This file is part of jplot2d.
 * <p/>
 * jplot2d is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or any later version.
 * <p/>
 * jplot2d is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Lesser Public License for more details.
 * <p/>
 * You should have received a copy of the GNU Lesser General Public License
 * along with jplot2d. If not, see <http://www.gnu.org/licenses/>.
 */
package org.jplot2d.element;

import org.jplot2d.annotation.Hierarchy;
import org.jplot2d.annotation.HierarchyOp;
import org.jplot2d.annotation.Property;
import org.jplot2d.annotation.PropertyGroup;
import org.jplot2d.data.XYGraphData;
import org.jplot2d.util.SymbolShape;

import javax.annotation.Nullable;
import java.awt.*;

/**
 * A Graph to display X/Y chart.
 *
 * @author Jingjing Li
 */
@SuppressWarnings("unused")
@PropertyGroup("XY Graph")
public interface XYGraph extends Graph {

    /**
     * Returns the legend item associated with this graph.
     *
     * @return the legend item
     */
    @Hierarchy(HierarchyOp.GET)
    LegendItem getLegendItem();

    /**
     * Returns the X/Y data of this graph.
     *
     * @return the X/Y data of this graph
     */
    @Nullable
    XYGraphData getData();

    /**
     * Sets the X/Y data to be displayed in this graph
     *
     * @param data the X/Y data
     */
    void setData(@Nullable XYGraphData data);

    /**
     * Returns the text displayed in the legend item
     *
     * @return the text
     */
    @Property(order = 0, styleable = false)
    String getName();

    /**
     * Sets the text displayed in the legend item
     *
     * @param text the text displayed in the legend item
     */
    void setName(String text);

    /**
     * Returns if symbols are drawn on the location of data points.
     *
     * @return <code>true</code> if symbols are drawn on the location of data points
     */
    @Property(order = 1)
    boolean isSymbolVisible();

    /**
     * Sets if symbols are drawn on the location of data points.
     *
     * @param symbolVisible <code>true</code> to draw symbols; otherwise, hide the symbols
     */
    void setSymbolVisible(boolean symbolVisible);

    /**
     * Returns the shape to be used to draw symbols.
     *
     * @return the symbol shape
     */
    @Property(order = 2, styleable = false)
    SymbolShape getSymbolShape();

    /**
     * Sets the specified symbol shape to be used.
     *
     * @param symbolShape the symbol shape
     */
    void setSymbolShape(SymbolShape symbolShape);

    /**
     * Returns the size of symbols in pt (1/72 inch).
     *
     * @return the size of symbols
     */
    @Property(order = 4)
    float getSymbolSize();

    /**
     * Sets the size of symbols in pt (1/72 inch). The default size is 8.0 pt.
     *
     * @param size the size of symbols
     */
    void setSymbolSize(float size);

    /**
     * Returns the overall color of the symbols.
     *
     * @return the Color of the symbols, can be <code>null</code>
     */
    @Property(order = 5)
    Color getSymbolColor();

    /**
     * Set the overall color of the symbols. If the given color is <code>null</code>,
     * the color of the symbols will be the same of the color of the line.
     *
     * @param color the color of the symbols
     */
    void setSymbolColor(Color color);

    /**
     * Returns the special color for data point idx. Or <code>null</code> if the data point use the overall symbol color.
     *
     * @param idx data point idx
     * @return the color for data point idx.
     */
    Color getIndSymbolColor(int idx);

    /**
     * Sets special color to the data point index.
     * This method can be called many times to assign different color to the given index.
     *
     * @param idx   the data point index
     * @param color the symbol color
     */
    void setIndSymbolColor(int idx, Color color);

    /**
     * Sets special color to the given data point indexes.
     * This method can be called many times to assign different color to the given indexes.
     *
     * @param idxes the data point indexes
     * @param color the symbol color
     */
    void setIndSymbolColor(int[] idxes, Color color);

    /**
     * Clear individual symbol colors and reset all data point to the overall symbol color, which assigned by
     * {@link #setSymbolColor(Color)}
     */
    void clearIndSymbolColor();

    /**
     * Returns <code>true</code> if the chart line is visible.
     *
     * @return <code>true</code> if the chart line is visible
     */
    @Property(order = 10)
    boolean isLineVisible();

    /**
     * Sets to <code>true</code> to show the chart line, otherwise hide the chart line.
     *
     * @param lineVisible <code>true</code> to show the chart line
     */
    void setLineVisible(boolean lineVisible);

    /**
     * Returns the stroke of chart line.
     *
     * @return the stroke of chart line
     */
    @Property(order = 11)
    BasicStroke getLineStroke();

    /**
     * Sets the stroke of chart line. The default line width is 0.5 pt.
     *
     * @param stroke the stroke of chart line
     */
    void setLineStroke(BasicStroke stroke);

    /**
     * @return the chart type
     */
    @Property(order = 12, styleable = false)
    ChartType getChartType();

    /**
     * Sets the chart type.
     *
     * @param chartType the chart type
     */
    void setChartType(ChartType chartType);

    /**
     * Returns if the filling is enabled. The filling can be applied to a closed line, or the area between the line and an axis.
     *
     * @return <code>true</code> if the filling is enabled
     */
    @Property(order = 20, styleable = false)
    boolean isFillEnabled();

    /**
     * Sets if the filling is enabled. The filling can be applied to a closed line, or the area between the line and an axis.
     * The default value is <code>false</code>.
     *
     * @param fillEnabled the flag
     */
    void setFillEnabled(boolean fillEnabled);

    /**
     * Returns the paint object to be used to fill the area.
     *
     * @return the paint object
     */
    @Property(order = 21, styleable = false)
    Paint getFillPaint();

    /**
     * Sets the paint object to be used to fill the area. The default paint is Color(192, 192, 192, 128).
     *
     * @param paint the paint object
     */
    void setFillPaint(Paint paint);

    /**
     * Returns the method type to close the line for filling.
     *
     * @return the closure type
     */
    @Property(order = 22, styleable = false)
    FillClosureType getFillClosureType();

    /**
     * Sets the method type to close the line for filling. The default value is {@link FillClosureType#SELF FillClosureType.SELF}
     *
     * @param type the closure type
     */
    void setFillClosureType(FillClosureType type);

    /**
     * Returns the errorbar cap size in pt. If the cap size is 0, no cap is drawn.
     *
     * @return the errorbar cap size
     */
    @Property(order = 30)
    float getErrorbarCapSize();

    /**
     * Sets the errorbar cap size in pt. The default cap size is 0, means no cap is drawn.
     *
     * @param size the errorbar cap size
     */
    void setErrorbarCapSize(float size);

    enum ChartType {
        /**
         * Broken-line graph
         */
        LINECHART,
        /**
         * Histogram with data point at level center
         */
        HISTOGRAM,
        /**
         * Histogram with data point at level edge
         */
        HISTOGRAM_EDGE
    }

    /**
     * Defines how close the line for filling.
     *
     * @author Jingjing Li
     */
    enum FillClosureType {
        /**
         * Close the line by drawing a straight line back to the 1st point.
         */
        SELF,
        /**
         * Close the line by project the 1st point and last point to left axis, to construct a closed area.
         */
        LEFT,
        /**
         * Close the line by project the 1st point and last point to right axis, to construct a closed area.
         */
        RIGHT,
        /**
         * Close the line by project the 1st point and last point to top axis, to construct a closed area.
         */
        TOP,
        /**
         * Close the line by project the 1st point and last point to bottom axis, to construct a closed area.
         */
        BOTTOM
    }

}
