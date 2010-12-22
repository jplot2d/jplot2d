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

import java.awt.Paint;
import java.awt.Stroke;

import org.jplot2d.data.XYData;
import org.jplot2d.util.SymbolShape;

/**
 * @author Jingjing Li
 * 
 */
public interface XYDataPlot extends LayerDataPlot {

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
		 * Close the line by project the 1st point and last point to left axis,
		 * to construct a closed area.
		 */
		LEFT,
		/**
		 * Close the line by project the 1st point and last point to right axis,
		 * to construct a closed area.
		 */
		RIGHT,
		/**
		 * Close the line by project the 1st point and last point to top axis,
		 * to construct a closed area.
		 */
		TOP,
		/**
		 * Close the line by project the 1st point and last point to bottom
		 * axis, to construct a closed area.
		 */
		BOTTOM
	}

	public XYData getData();

	public void setData(XYData data);

	public boolean isSymbolsVisible();

	public void setSymbolsVisible(boolean symbolsVisible);

	public boolean isLinesVisible();

	public void setLinesVisible(boolean linesVisible);

	/**
	 * @return the chart type
	 */
	public ChartType getChartType();

	/**
	 * Sets the chart type.
	 * 
	 * @param chartType
	 *            the chart type
	 */
	public void setChartType(ChartType chartType);

	/**
	 * Returns the symbol to be used.The symbols are displays only if the type
	 * of line is MARKED or NONE.
	 * 
	 * @return the symbol
	 */
	public SymbolShape getSymbolShape();

	/**
	 * Sets the specified symbol to be used. The symbols are displayes only if
	 * the type of line is MARKED or NONE.
	 * 
	 * @param symbolShape
	 */
	public void setSymbolShape(SymbolShape symbolShape);

	/**
	 * Returns the size of the symbol used
	 * 
	 * @return the size of the symbol used
	 */
	public float getSymbolSize();

	/**
	 * Set the size of the symbol used
	 * 
	 * @param size
	 *            of the symbol used
	 */
	public void setSymbolSize(float size);

	public Stroke getLineStroke();

	public void setLineStroke(Stroke stroke);

	public boolean isFillEnabled();

	public void setFillEnabled(boolean fillEnabled);

	public Paint getFillPaint();

	public void setFillPaint(Paint paint);

	/**
	 * Returns the method type to close the line for filling.
	 * 
	 * @return the closure type
	 */
	public FillClosureType getFillClosureType();

	/**
	 * Sets the method type to close the line for filling.
	 * 
	 * @param type
	 *            the closure type
	 */
	public void setFillClosureType(FillClosureType type);

}
