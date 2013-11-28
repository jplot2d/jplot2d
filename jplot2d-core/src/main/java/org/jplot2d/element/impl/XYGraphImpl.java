/**
 * Copyright 2010-2012 Jingjing Li.
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
package org.jplot2d.element.impl;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.util.HashMap;
import java.util.Map;

import org.jplot2d.data.XYGraphData;
import org.jplot2d.element.impl.XYGraphDataChunker.ChunkData;
import org.jplot2d.util.GraphicsUtil;
import org.jplot2d.util.SymbolShape;

/**
 * @author Jingjing Li
 * 
 */
public class XYGraphImpl extends GraphImpl implements XYGraphEx {

	private static Paint DEFAULT_FILL_PAINT = new Color(192, 192, 192, 128);

	/**
	 * The error arrow length in pt
	 */
	private static final float ARROW_LENGTH = 12;

	private static final float ARROW_HEAD_LENGTH = ARROW_LENGTH / 4;

	private final LegendItemEx legendItem;

	private XYGraphData data;

	private boolean symbolVisible = false;

	private boolean lineVisible = true;

	private ChartType chartType = ChartType.LINECHART;

	private SymbolShape symbolShape = SymbolShape.CIRCLE;

	private float symbolSize = 8.0f;

	private Color symbolColor;

	private final Map<Integer, Color> _symbolColorMap = new HashMap<Integer, Color>();

	private BasicStroke lineStroke = new BasicStroke(0.5f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER);

	private boolean fillEnabled;

	private Paint fillPaint = DEFAULT_FILL_PAINT;

	private FillClosureType fillClosureType = FillClosureType.SELF;

	private float errorbarCapSize;

	public XYGraphImpl() {
		legendItem = new XYLegendItemImpl();
		legendItem.setParent(this);
	}

	public String getId() {
		if (getParent() != null) {
			return "XYGraph" + getParent().indexOf(this);
		} else {
			return "XYGraph@" + Integer.toHexString(System.identityHashCode(this));
		}
	}

	public XYLegendItemEx getLegendItem() {
		return (XYLegendItemEx) legendItem;
	}

	public String getName() {
		return ((XYLegendItemImpl) getLegendItem()).getText();
	}

	public void setName(String text) {
		((XYLegendItemImpl) getLegendItem()).setText(text);
	}

	public XYGraphData getData() {
		return data;
	}

	public void setData(XYGraphData data) {
		this.data = data;

		redraw(this);

		if (getParent() != null && this.canContribute()) {
			AxisTransformEx xarm = getParent().getXAxisTransform();
			AxisTransformEx yarm = getParent().getYAxisTransform();

			if (xarm != null && xarm.getLockGroup().isAutoRange()) {
				xarm.getLockGroup().reAutoRange();
			}
			if (yarm != null && yarm.getLockGroup().isAutoRange()) {
				yarm.getLockGroup().reAutoRange();
			}
		}
	}

	public boolean isSymbolVisible() {
		return symbolVisible;
	}

	public void setSymbolVisible(boolean symbolVisible) {
		this.symbolVisible = symbolVisible;
		redraw(this);
	}

	public boolean isLineVisible() {
		return lineVisible;
	}

	public void setLineVisible(boolean lineVisible) {
		this.lineVisible = lineVisible;
		redraw(this);
	}

	/**
	 * @return the chart type
	 */
	public ChartType getChartType() {
		return chartType;
	}

	/**
	 * Sets the chart type.
	 * 
	 * @param chartType
	 *            the chart type
	 */
	public void setChartType(ChartType chartType) {
		this.chartType = chartType;
		redraw(this);
	}

	public SymbolShape getSymbolShape() {
		return symbolShape;
	}

	public void setSymbolShape(SymbolShape symbolShape) {
		this.symbolShape = symbolShape;
		redraw(this);
	}

	public float getSymbolSize() {
		return symbolSize;
	}

	public void setSymbolSize(float size) {
		this.symbolSize = size;
		redraw(this);
		getLegendItem().graphSymbolSizeChanged();
	}

	public Color getSymbolColor() {
		return symbolColor;
	}

	public void setSymbolColor(Color color) {
		symbolColor = color;
		redraw(this);
	}

	public Color getEffectiveSymbolColor() {
		if (getSymbolColor() != null) {
			return getSymbolColor();
		} else {
			return getEffectiveColor();
		}
	}

	public Color getEffectiveSymbolColor(int idx) {
		Color icolor = null;
		synchronized (_symbolColorMap) {
			if (_symbolColorMap.size() > 0) {
				icolor = _symbolColorMap.get(idx);
			}
		}
		if (icolor != null) {
			return icolor;
		} else {
			return getEffectiveSymbolColor();
		}
	}

	public void thisEffectiveColorChanged() {
		if (isLineVisible() || (isSymbolVisible() && getSymbolColor() == null)) {
			redraw(this);
		}
	}

	public BasicStroke getLineStroke() {
		return lineStroke;
	}

	public void setLineStroke(BasicStroke stroke) {
		this.lineStroke = stroke;
		redraw(this);
	}

	public boolean isFillEnabled() {
		return fillEnabled;
	}

	public void setFillEnabled(boolean fillEnabled) {
		this.fillEnabled = fillEnabled;
		redraw(this);
	}

	public Paint getFillPaint() {
		return fillPaint;
	}

	public void setFillPaint(Paint paint) {
		this.fillPaint = paint;
		redraw(this);
	}

	public FillClosureType getFillClosureType() {
		return fillClosureType;
	}

	public void setFillClosureType(FillClosureType type) {
		this.fillClosureType = type;
		redraw(this);
	}

	public float getErrorbarCapSize() {
		return errorbarCapSize;
	}

	public void setErrorbarCapSize(float size) {
		errorbarCapSize = size;
		redraw(this);
	}

	@Override
	public ComponentEx copyStructure(Map<ElementEx, ElementEx> orig2copyMap) {
		XYGraphImpl result = (XYGraphImpl) super.copyStructure(orig2copyMap);

		if (orig2copyMap != null) {
			orig2copyMap.put(getLegendItem(), result.getLegendItem());
		}

		return result;
	}

	@Override
	public void copyFrom(ElementEx src) {
		super.copyFrom(src);

		XYGraphImpl dp = (XYGraphImpl) src;

		this.data = dp.data;
		this.symbolVisible = dp.symbolVisible;
		this.lineVisible = dp.lineVisible;
		this.chartType = dp.chartType;
		this.symbolShape = dp.symbolShape;
		this.symbolSize = dp.symbolSize;
		this.symbolColor = dp.symbolColor;
		this.lineStroke = dp.lineStroke;
		this.fillEnabled = dp.fillEnabled;
		this.fillPaint = dp.fillPaint;
		this.fillClosureType = dp.fillClosureType;
		this.errorbarCapSize = dp.errorbarCapSize;
	}

	public void draw(Graphics2D graphics) {

		if (getData() == null) {
			return;
		}

		Graphics2D g = (Graphics2D) graphics.create();
		Shape clip = getPaperTransform().getPtoD(getBounds());
		g.setClip(clip);

		if (isFillEnabled()) {
			if (Thread.interrupted()) {
				g.dispose();
				return;
			}
			fillLineAera((Graphics2D) g);
		}

		if (Thread.interrupted()) {
			g.dispose();
			return;
		}
		drawLineAndSymbol((Graphics2D) g);

		g.dispose();
	}

	private void fillLineAera(Graphics2D g) {
		XYGraphFiller filler = XYGraphFiller.getInstance(this, g.getClipBounds());

		Paint p = getFillPaint();
		filler.fill(g, p);
	}

	/**
	 * Drawing lines by iterating over DataChunker.
	 * 
	 * @param g
	 */
	private void drawLineAndSymbol(Graphics2D g) {

		double scale = getParent().getPaperTransform().getScale();

		XYGraphDataChunker chunker = XYGraphDataChunker.getInstance(this, g.getClipBounds());

		for (ChunkData data : chunker) {
			if (Thread.interrupted()) {
				return;
			}

			// draw lines
			g.setColor(getEffectiveColor());

			if (isLineVisible()) {
				switch (getChartType()) {
				case LINECHART:
					drawLine(g, data.xBuf, data.yBuf, data.size, this, scale);
					break;
				case HISTOGRAM:
					drawHistogram(g, data, this, scale);
					break;
				case HISTOGRAM_EDGE:
					drawEdgeHistogram(g, data.xBuf, data.yBuf, data.size, this, scale);
					break;
				}
			}

			// draw error bar with line color and new stroke
			g.setStroke(GraphicsUtil.scaleStroke(getLineStroke(), scale / 2));

			if (data.xErrorSize != 0) {
				for (int i = 0; i < data.xErrorSize; i++) {
					drawErrorBarX(g, data.xBuf[i], data.yBuf[i], data.xLowBuf[i], data.xHighBuf[i], (float) scale);
				}
			}
			if (data.yErrorSize != 0) {
				for (int i = 0; i < data.yErrorSize; i++) {
					drawErrorBarY(g, data.xBuf[i], data.yBuf[i], data.yLowBuf[i], data.yHighBuf[i], (float) scale);

				}
			}

			// drawing marks may change color and stroke
			if (isSymbolVisible()) {
				drawMarks(g, data.xBuf, data.yBuf, data.size, this, data.markColorBuf, scale);
			}

		}

	}

	/**
	 * Draw a horizontal errorbar for the given point (x,y)
	 * 
	 * @param g
	 *            the Graphics2D
	 * @param x
	 *            the point x
	 * @param y
	 *            the point y
	 * @param low
	 *            the low x of the errorbar
	 * @param high
	 *            the high x of the errorbar
	 * @param scale
	 *            the scale
	 */
	private void drawErrorBarX(Graphics2D g, float x, float y, float low, float high, float scale) {

		// errorbar with arrow
		if (!Float.isInfinite(low) && !Float.isInfinite(high)) {
			g.draw(new Line2D.Float(low, y, high, y));
		} else {
			if (low == Float.NEGATIVE_INFINITY) {
				float head, tail;
				if (high == Float.POSITIVE_INFINITY) {
					tail = x;
				} else {
					tail = high;
				}
				if (x < high) {
					head = x - ARROW_LENGTH * scale;
				} else {
					head = high - ARROW_LENGTH * scale;
				}
				// draw left arrow
				g.draw(new Line2D.Float(tail, y, head, y));
				float ah = ARROW_HEAD_LENGTH * scale;
				g.draw(new Line2D.Float(head, y, head + ah, y - ah));
				g.draw(new Line2D.Float(head, y, head + ah, y + ah));
			}
			if (high == Float.POSITIVE_INFINITY) {
				float head, tail;
				if (low == Float.NEGATIVE_INFINITY) {
					tail = x;
				} else {
					tail = low;
				}
				if (x > low) {
					head = x + ARROW_LENGTH * scale;
				} else {
					head = low + ARROW_LENGTH * scale;
				}
				// draw right arrow
				g.draw(new Line2D.Float(tail, y, head, y));
				float ah = ARROW_HEAD_LENGTH * scale;
				g.draw(new Line2D.Float(head, y, head - ah, y - ah));
				g.draw(new Line2D.Float(head, y, head - ah, y + ah));
			}
		}

		// errorbar cap
		if (errorbarCapSize > 0) {
			float halfcap = errorbarCapSize * scale / 2;
			if (low != x && !Float.isInfinite(low)) {
				g.draw(new Line2D.Float(low, y - halfcap, low, y + halfcap));
			}
			if (high != x && !Float.isInfinite(high)) {
				g.draw(new Line2D.Float(high, y - halfcap, high, y + halfcap));
			}
		}
	}

	/**
	 * Draw a vertical error bar for the given point (x,y)
	 * 
	 * @param g
	 *            the Graphics2D
	 * @param x
	 *            the point x
	 * @param y
	 *            the point y
	 * @param low
	 *            the low y of the errorbar
	 * @param high
	 *            the high y of the errorbar
	 * @param scale
	 *            the scale
	 */
	private void drawErrorBarY(Graphics2D g, float x, float y, float low, float high, float scale) {

		// errorbar with arrow
		if (!Float.isInfinite(low) && !Float.isInfinite(high)) {
			g.draw(new Line2D.Float(x, low, x, high));
		} else {
			if (low == Float.POSITIVE_INFINITY) {
				float head, tail;
				if (high == Float.NEGATIVE_INFINITY) {
					tail = y;
				} else {
					tail = high;
				}
				if (y > high) {
					head = y + ARROW_LENGTH * scale;
				} else {
					head = high + ARROW_LENGTH * scale;
				}
				// draw down arrow
				g.draw(new Line2D.Float(x, tail, x, head));
				float ah = ARROW_HEAD_LENGTH * scale;
				g.draw(new Line2D.Float(x, head, x - ah, head - ah));
				g.draw(new Line2D.Float(x, head, x + ah, head - ah));
			}
			if (high == Float.NEGATIVE_INFINITY) {
				float head, tail;
				if (low == Float.POSITIVE_INFINITY) {
					tail = y;
				} else {
					tail = low;
				}
				if (y < low) {
					head = y - ARROW_LENGTH * scale;
				} else {
					head = low - ARROW_LENGTH * scale;
				}
				// draw up arrow
				g.draw(new Line2D.Float(x, tail, x, head));
				float ah = ARROW_HEAD_LENGTH * scale;
				g.draw(new Line2D.Float(x, head, x - ah, head + ah));
				g.draw(new Line2D.Float(x, head, x + ah, head + ah));
			}
		}

		// errorbar cap
		if (errorbarCapSize > 0) {
			float halfcap = errorbarCapSize * scale / 2;
			if (low != y && !Float.isInfinite(low)) {
				g.draw(new Line2D.Float(x - halfcap, low, x + halfcap, low));
			}
			if (high != y && !Float.isInfinite(high)) {
				g.draw(new Line2D.Float(x - halfcap, high, x + halfcap, high));
			}
		}
	}

	static void drawLine(Graphics2D g, float[] xout, float[] yout, int lsize, XYGraphEx graph, double scale) {
		// set line stroke
		g.setStroke(GraphicsUtil.scaleStroke(graph.getLineStroke(), scale));
		// draw lines
		Path2D.Float gp = new Path2D.Float();
		gp.moveTo(xout[0], yout[0]);
		for (int i = 1; i < lsize; i++) {
			gp.lineTo(xout[i], yout[i]);
		}
		g.draw(gp);
	}

	/**
	 * Draw Histogram lines that data points is on the level center.
	 */
	private static void drawHistogram(Graphics2D g, ChunkData data, XYGraphEx graph, double scale) {

		float[] x = data.xBuf;
		float[] y = data.yBuf;
		int lsize = data.size;

		int nsize = lsize * 2;
		float[] xout = new float[nsize];
		float[] yout = new float[nsize];

		xout[0] = x[0];
		yout[0] = y[0];
		int j = 1; // the next idx of xout/yout
		/* the 2 idx of (x,y) array */
		for (int a = 0, b = 1; b < lsize; a++, b++) {
			float xm = (x[a] + x[b]) / 2;
			xout[j] = xm;
			yout[j] = y[a];
			j++;
			xout[j] = xm;
			yout[j] = y[b];
			j++;
		}
		xout[j] = x[lsize - 1];
		yout[j] = y[lsize - 1];

		drawLine(g, xout, yout, nsize, graph, scale);
	}

	/**
	 * Draw Histogram lines that data points is on the level edge.
	 * 
	 * @param g
	 * @param x
	 * @param y
	 * @param lsize
	 *            the number of points
	 * @param graph
	 */
	private static void drawEdgeHistogram(Graphics2D g, float[] x, float[] y, int lsize, XYGraphEx graph, double scale) {
		int nsize = lsize * 2 - 1;
		float[] xout = new float[nsize];
		float[] yout = new float[nsize];

		int hi = 0;
		for (int i = 0; i < lsize - 1; i++) {
			xout[hi] = x[i];
			yout[hi] = y[i];
			hi++;
			xout[hi] = x[i + 1];
			yout[hi] = y[i];
			hi++;
		}
		xout[hi] = x[lsize - 1];
		yout[hi] = y[lsize - 1];

		drawLine(g, xout, yout, nsize, graph, scale);
	}

	/**
	 * Draw a mark at the requested location. This routine is used by LineCartesianGraph and LineKey.
	 * 
	 * @param g
	 *            Graphics object
	 * @param xp
	 *            horizontal coordinate
	 * @param yp
	 *            vertical coordinate
	 * @param graph
	 *            line attribute
	 */
	static void drawMarks(Graphics2D g, float[] xp, float[] yp, int npoints, XYGraphEx graph, Color[] colors,
			double scale) {

		// use half of line stroke to draw marks
		double lw = graph.getLineStroke().getLineWidth() * scale / 2;
		g.setStroke(new BasicStroke((float) lw, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER));

		SymbolShape ss = graph.getSymbolShape();
		for (int i = 0; i < npoints; i++) {
			/* not draw mark if the point is brought to clip border */
			if (colors[i] == null) {
				continue;
			}

			g.setColor(colors[i]);

			double safScale = scale * graph.getSymbolSize();
			AffineTransform maf = AffineTransform.getTranslateInstance(xp[i], yp[i]);
			maf.scale(safScale, -safScale);

			ss.draw(g, maf);
		}
	}

}
