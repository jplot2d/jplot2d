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
package org.jplot2d.element.impl;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.jplot2d.data.XYGraph;
import org.jplot2d.element.impl.XYGraphPlotterDataChunker.ChunkData;
import org.jplot2d.util.LineHatchPaint;
import org.jplot2d.util.NumberArrayUtils;
import org.jplot2d.util.SymbolShape;

/**
 * @author Jingjing Li
 * 
 */
public class XYGraphPlotterImpl extends GraphPlotterImpl implements
		XYGraphPlotterEx {

	/**
	 * The error arrow length in pt
	 */
	private static final float ARROW_LENGTH = 12;

	private static final float ARROW_HEAD_LENGTH = ARROW_LENGTH / 4;

	private XYGraph graph;

	private boolean symbolVisible = true;

	private boolean lineVisible = true;

	private ChartType chartType = ChartType.LINECHART;

	private SymbolShape symbolShape;

	private float symbolSize;

	private Color symbolColor;

	private final Map<Integer, Color> _symbolColorMap = new HashMap<Integer, Color>();

	private Stroke lineStroke = new BasicStroke(0.5f);

	private Color lineColor;

	private boolean fillEnabled;

	private Paint fillPaint;

	private FillClosureType fillClosureType;

	public XYGraphPlotterImpl() {
		super(new LegendItemImpl());
	}

	public XYGraph getGraph() {
		return graph;
	}

	public void setGraph(XYGraph graph) {
		this.graph = graph;
	}

	public boolean isSymbolsVisible() {
		return symbolVisible;
	}

	public void setSymbolsVisible(boolean symbolsVisible) {
		this.symbolVisible = symbolsVisible;
	}

	public boolean isLinesVisible() {
		return lineVisible;
	}

	public void setLinesVisible(boolean linesVisible) {
		this.lineVisible = linesVisible;
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
	}

	public SymbolShape getSymbolShape() {
		return symbolShape;
	}

	public void setSymbolShape(SymbolShape symbolShape) {
		this.symbolShape = symbolShape;
	}

	public float getSymbolSize() {
		return symbolSize;
	}

	public void setSymbolSize(float size) {
		this.symbolSize = size;
	}

	public Color getSymbolsColor() {
		return symbolColor;
	}

	public Color getActualSymbolColor(int idx) {
		Color icolor = null;
		synchronized (_symbolColorMap) {
			if (_symbolColorMap.size() > 0) {
				icolor = _symbolColorMap.get(idx);
			}
		}
		if (icolor == null) {
			icolor = this.getSymbolsColor();
		}
		if (icolor == null) {
			icolor = this.getColor();
		}
		return icolor;
	}

	public Stroke getLineStroke() {
		return lineStroke;
	}

	public void setLineStroke(Stroke stroke) {
		this.lineStroke = stroke;
	}

	public boolean isFillEnabled() {
		return fillEnabled;
	}

	public void setFillEnabled(boolean fillEnabled) {
		this.fillEnabled = fillEnabled;
	}

	public Paint getFillPaint() {
		return fillPaint;
	}

	public void setFillPaint(Paint paint) {
		this.fillPaint = paint;
	}

	public FillClosureType getFillClosureType() {
		return fillClosureType;
	}

	public void setFillClosureType(FillClosureType type) {
		this.fillClosureType = type;
	}

	@Override
	public void copyFrom(ElementEx src) {
		super.copyFrom(src);

		XYGraphPlotterImpl dp = (XYGraphPlotterImpl) src;

		this.graph = dp.graph;
		this.symbolVisible = dp.symbolVisible;
		this.lineVisible = dp.lineVisible;
		this.chartType = dp.chartType;
		this.symbolShape = dp.symbolShape;
		this.symbolSize = dp.symbolSize;
		this.symbolColor = dp.symbolColor;
		this.lineStroke = dp.lineStroke;
		this.lineColor = dp.lineColor;
	}

	public void draw(Graphics2D graphics) {

		if (getGraph() == null) {
			return;
		}

		Graphics2D g = (Graphics2D) graphics.create();
		Rectangle2D clip = getParent().getPhysicalTransform().getPtoD(
				new Rectangle2D.Double(0, 0, getParent().getSize().getWidth(),
						getParent().getSize().getHeight()));
		g.setClip(clip);

		if (isFillEnabled()) {
			fillLineAera((Graphics2D) g);
		}

		drawLineAndSymbol((Graphics2D) g);

	}

	private void fillLineAera(Graphics2D g) {
		XYGraphPlotterFiller filler = XYGraphPlotterFiller.getInstance(this,
				g.getClipBounds());
		Shape shape = filler.getShape();

		Paint p = getFillPaint();
		if (p instanceof LineHatchPaint) {
			g.setPaint(filler.createHatchPaint((LineHatchPaint) p));
		} else {
			g.setPaint(p);
		}
		g.fill(shape);
	}

	/**
	 * Drawing lines by iterating over DataChunker.
	 * 
	 * @param g
	 */
	private void drawLineAndSymbol(Graphics2D g) {

		double scale = getParent().getPhysicalTransform().getScale();

		XYGraphPlotterDataChunker chunker = XYGraphPlotterDataChunker
				.getInstance(this, g.getClipBounds());

		for (ChunkData data : chunker) {
			if (Thread.interrupted()) {
				throw new PlottingAbortException();
			}

			// draw lines
			g.setColor(getEffectiveColor());

			if (isLinesVisible()) {
				switch (getChartType()) {
				case LINECHART:
					drawLine(g, data.xBuf, data.yBuf, data.size, this, scale);
					break;
				case HISTOGRAM:
					drawHistogram(g, data, this, scale);
					break;
				case HISTOGRAM_EDGE:
					drawEdgeHistogram(g, data.xBuf, data.yBuf, data.size, this,
							scale);
					break;
				}
			}

			// draw error bar with line color and new stroke
			g.setStroke(scaleStroke(getLineStroke(), scale / 2));

			if (data.xErrorSize != 0) {
				for (int i = 0; i < data.xErrorSize; i++) {
					drawErrorBarX(g, data.xBuf[i], data.yBuf[i],
							data.xLowBuf[i], data.xHighBuf[i], (float) scale);
				}
			}
			if (data.yErrorSize != 0) {
				for (int i = 0; i < data.yErrorSize; i++) {
					drawErrorBarY(g, data.xBuf[i], data.yBuf[i],
							data.yLowBuf[i], data.yHighBuf[i], (float) scale);

				}
			}

			// drawing marks may change color and stroke
			if (isSymbolsVisible()) {
				drawMarks(g, data.xBuf, data.yBuf, data.size, this,
						data.markColorBuf, scale);
			}

		}

	}

	private static void drawErrorBarX(Graphics2D g, float x, float y,
			float low, float high, float scale) {
		if (low == high) {
			// ignore?
			return;
		}

		// error bar
		if (!Float.isInfinite(low) && !Float.isInfinite(high)) {
			g.draw(new Line2D.Float(low, y, high, y));
			return;
		}

		// error arrow
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

	/**
	 * Draw an error bar for the given point x,y
	 * 
	 * @param g
	 * @param x
	 * @param y
	 * @param low
	 * @param high
	 * @param scale
	 */
	private static void drawErrorBarY(Graphics2D g, float x, float y,
			float low, float high, float scale) {
		if (low == high) {
			// ignore?
			return;
		}

		// error bar
		if (!Float.isInfinite(low) && !Float.isInfinite(high)) {
			g.draw(new Line2D.Float(x, low, x, high));
			return;
		}

		// error arrow
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

	static void drawLine(Graphics2D g2, float[] xout, float[] yout, int lsize,
			XYGraphPlotterEx plotter, double scale) {

		// set line stroke
		g2.setStroke(scaleStroke(plotter.getLineStroke(), scale));

		// draw lines
		Path2D.Float gp = new Path2D.Float();
		gp.moveTo(xout[0], yout[0]);
		for (int i = 1; i < lsize; i++) {
			gp.lineTo(xout[i], yout[i]);
		}
		g2.draw(gp);

	}

	private static Stroke scaleStroke(Stroke stroke, double scale) {
		if (stroke instanceof BasicStroke) {
			BasicStroke bstroke = (BasicStroke) stroke;
			float[] dashArray = (bstroke.getDashArray() == null) ? null
					: NumberArrayUtils.multiply(bstroke.getDashArray(), scale);
			return new BasicStroke((float) (bstroke.getLineWidth() * scale),
					BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 10f,
					dashArray, 0f);
		} else {
			return stroke;
		}
	}

	/**
	 * Draw Histogram lines that data points is on the level center.
	 */
	private static void drawHistogram(Graphics2D g, ChunkData data,
			XYGraphPlotterEx plotter, double scale) {

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

		drawLine(g, xout, yout, nsize, plotter, scale);
	}

	/**
	 * Draw Histogram lines that data points is on the level edge.
	 * 
	 * @param g
	 * @param x
	 * @param y
	 * @param lsize
	 *            the number of points
	 * @param plotter
	 */
	private static void drawEdgeHistogram(Graphics2D g, float[] x, float[] y,
			int lsize, XYGraphPlotterEx plotter, double scale) {
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

		drawLine(g, xout, yout, nsize, plotter, scale);
	}

	/**
	 * Draw a mark at the requested location. This routine is used by
	 * LineCartesianGraph and LineKey.
	 * 
	 * @param g
	 *            Graphics object
	 * @param xp
	 *            horizontal coordinate
	 * @param yp
	 *            vertical coordinate
	 * @param plotter
	 *            line attribute
	 */
	static void drawMarks(Graphics2D g, float[] xp, float[] yp, int npoints,
			XYGraphPlotterEx plotter, Color[] colors, double scale) {

		if (plotter.getSymbolShape() == SymbolShape.DOT) {
			// use 0 width stroke to draw dot marks
			BasicStroke markStroke = new BasicStroke(0, BasicStroke.CAP_BUTT,
					BasicStroke.JOIN_BEVEL, 10f);
			g.setStroke(markStroke);

			for (int i = 0; i < npoints; i++) {
				/* not draw mark if the point is brought to clip border */
				if (colors[i] == null) {
					continue;
				}
				g.setColor(colors[i]);
				Shape dot = new Line2D.Float(xp[i], yp[i], xp[i], yp[i]);
				g.draw(dot);
			}
		} else {
			// use half of line stroke to draw marks
			g.setStroke(scaleStroke(plotter.getLineStroke(), scale / 2));

			SymbolShape ss = plotter.getSymbolShape();
			for (int i = 0; i < npoints; i++) {
				/* not draw mark if the point is brought to clip border */
				if (colors[i] == null) {
					continue;
				}

				g.setColor(colors[i]);

				double safScale = scale * plotter.getSymbolSize();
				AffineTransform maf = AffineTransform.getTranslateInstance(
						xp[i], yp[i]);
				maf.scale(safScale, safScale);
				maf.concatenate(ss.getSymbolTransform());

				Iterator<Shape> dit = ss.getDrawShapeIterator();
				while (dit.hasNext()) {
					Shape s = maf.createTransformedShape(dit.next());
					g.draw(s);
				}

				Iterator<Shape> fit = ss.getFillShapeIterator();
				while (fit.hasNext()) {
					Shape s = maf.createTransformedShape(fit.next());
					g.fill(s);
				}

			}
		}

	}

}
