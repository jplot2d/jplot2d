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

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Paint;

import org.jplot2d.annotation.Hierarchy;
import org.jplot2d.annotation.HierarchyOp;
import org.jplot2d.annotation.Property;
import org.jplot2d.annotation.PropertyGroup;
import org.jplot2d.data.XYGraphData;
import org.jplot2d.util.SymbolShape;

/**
 * @author Jingjing Li
 * 
 */
@PropertyGroup("XY Graph")
public interface XYGraph extends Graph {

	public enum ChartType {
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
	};

	/**
	 * Defines how close the line for filling.
	 * 
	 * @author Jingjing Li
	 */
	public enum FillClosureType {
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

	/**
	 * Returns the legend item associated with this graph.
	 * 
	 * @return the legend item
	 */
	@Hierarchy(HierarchyOp.GET)
	public LegendItem getLegendItem();

	public XYGraphData getData();

	public void setData(XYGraphData data);

	/**
	 * Returns the text displayed in the legend item
	 * 
	 * @return the text
	 */
	@Property(order = 0, styleable = false)
	public String getName();

	/**
	 * Sets the text displayed in the legend item
	 * 
	 * @param text
	 *            the text displayed in the legend item
	 */
	public void setName(String text);

	@Property(order = 1)
	public boolean isSymbolVisible();

	public void setSymbolVisible(boolean symbolVisible);

	/**
	 * Returns the symbol shape.
	 * 
	 * @return the symbol shape
	 */
	@Property(order = 2, styleable = false)
	public SymbolShape getSymbolShape();

	/**
	 * Sets the specified symbol shape to be used.
	 * 
	 * @param symbolShape
	 *            the symbol shape
	 */
	public void setSymbolShape(SymbolShape symbolShape);

	/**
	 * Returns the size of the symbol used
	 * 
	 * @return the size of the symbol used
	 */
	@Property(order = 4)
	public float getSymbolSize();

	/**
	 * Set the size of symbols
	 * 
	 * @param size
	 *            the size of symbols
	 */
	public void setSymbolSize(float size);

	/**
	 * Returns the overall color of the symbols.
	 * 
	 * @return the Color of the symbols, can be <code>null</code>
	 */
	@Property(order = 5)
	public Color getSymbolColor();

	/**
	 * Set the overall color of the symbols. If the given color is <code>null</code>, the color of the symbols will be
	 * the same of the color of the line.
	 * 
	 * @param color
	 *            the color of the symbols
	 */
	public void setSymbolColor(Color color);

	/**
	 * Returns the special color for data point idx. Or <code>null</code> if the data point use the overall symbol
	 * color.
	 * 
	 * @param idx
	 *            data point idx
	 * @return the color for data point idx.
	 */
	public Color getIndSymbolColor(int idx);

	/**
	 * Sets special color to data point index. This method can be called many times to assign different color to the
	 * given index.
	 * 
	 * @param idx
	 * @param color
	 */
	public void setIndSymbolColor(int idx, Color color);

	/**
	 * Sets special color to data point indexes. This method can be called many times to assign different color to the
	 * given indexes.
	 * 
	 * @param idxes
	 * @param color
	 */
	public void setIndSymbolColor(int[] idxes, Color color);

	/**
	 * Clear individual symbol colors and reset all data point to the overall symbol color, which assigned by
	 * {@link #setSymbolColor(Color)}
	 */
	public void clearIndSymbolColor();

	@Property(order = 10)
	public boolean isLineVisible();

	public void setLineVisible(boolean lineVisible);

	@Property(order = 11)
	public BasicStroke getLineStroke();

	public void setLineStroke(BasicStroke stroke);

	/**
	 * @return the chart type
	 */
	@Property(order = 12, styleable = false)
	public ChartType getChartType();

	/**
	 * Sets the chart type.
	 * 
	 * @param chartType
	 *            the chart type
	 */
	public void setChartType(ChartType chartType);

	@Property(order = 20, styleable = false)
	public boolean isFillEnabled();

	public void setFillEnabled(boolean fillEnabled);

	@Property(order = 21, styleable = false)
	public Paint getFillPaint();

	public void setFillPaint(Paint paint);

	/**
	 * Returns the method type to close the line for filling.
	 * 
	 * @return the closure type
	 */
	@Property(order = 22, styleable = false)
	public FillClosureType getFillClosureType();

	/**
	 * Sets the method type to close the line for filling.
	 * 
	 * @param type
	 *            the closure type
	 */
	public void setFillClosureType(FillClosureType type);

	/**
	 * Returns the errorbar cap size in pt. If the cap size is 0, no cap is drawn.
	 * 
	 * @return the errorbar cap size
	 */
	@Property(order = 30)
	public float getErrorbarCapSize();

	/**
	 * Sets the errorbar cap size in pt. The default cap size is 0, means no cap is drawn.
	 * 
	 * @param size
	 *            the errorbar cap size
	 */
	public void setErrorbarCapSize(float size);

}
