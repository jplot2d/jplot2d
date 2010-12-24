/*
 * This file is part of Herschel Common Science System (HCSS).
 * Copyright 2001-2010 Herschel Science Ground Segment Consortium
 *
 * HCSS is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of
 * the License, or (at your option) any later version.
 *
 * HCSS is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General
 * Public License along with HCSS.
 * If not, see <http://www.gnu.org/licenses/>.
 */
package org.jplot2d.element.impl;

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Iterator;
import java.util.NoSuchElementException;

import org.jplot2d.data.XYGraph;

/**
 * Supply graph data points in iterable chunks.
 * 
 * @author Jingjing Li
 */
public class XYGraphPlotterDataChunker implements
		Iterable<XYGraphPlotterDataChunker.ChunkData> {

	private static final ThreadLocal<XYGraphPlotterDataChunker> _threadLocalBuilder = new ThreadLocal<XYGraphPlotterDataChunker>();

	private static final int CHUNK_SIZE = 1000;

	public static class ChunkData {

		public int size;

		public float[] xBuf;

		public float[] yBuf;

		public Color[] markColorBuf;

		public int xErrorSize;

		public float[] xLowBuf;

		public float[] xHighBuf;

		public int yErrorSize;

		public float[] yLowBuf;

		public float[] yHighBuf;

		private ChunkData(int bufferSize) {
			xBuf = new float[bufferSize];
			yBuf = new float[bufferSize];
			markColorBuf = new Color[bufferSize];
			xLowBuf = new float[bufferSize];
			xHighBuf = new float[bufferSize];
			yLowBuf = new float[bufferSize];
			yHighBuf = new float[bufferSize];
			size = 0;
			xErrorSize = 0;
			yErrorSize = 0;
		}

		/**
		 * reset this ChunkData for next use.
		 */
		private void reset() {
			size = 0;
			xErrorSize = 0;
			yErrorSize = 0;
		}

		private void addPoint(float x, float y) {
			xBuf[size] = x;
			yBuf[size] = y;
			markColorBuf[size] = null;
			xLowBuf[size] = xHighBuf[size] = 0;
			yLowBuf[size] = yHighBuf[size] = 0;
			/*
			 * error size need not change because it always equals the last
			 * valid data.size + 1
			 */
			size++;
		}
	}

	private class DataChunkIterator implements Iterator<ChunkData> {

		/**
		 * The chunk to be returned by next()
		 */
		private ChunkData _chunk = new ChunkData(CHUNK_SIZE);

		/**
		 * The chunk next to _chunk
		 */
		private ChunkData _nextChunk = new ChunkData(CHUNK_SIZE);

		/**
		 * The current data point index
		 */
		private int i;

		/**
		 * Reset this iterator states.
		 */
		private void reset() {
			i = 0;
			prepareNextChunk(_chunk);
		}

		public boolean hasNext() {
			return _chunk != null && _chunk.size > 0;
		}

		public ChunkData next() {
			if (_chunk.size == 0) {
				throw new NoSuchElementException();
			}

			prepareNextChunk(_nextChunk);

			ChunkData result = _chunk;
			_chunk = _nextChunk;
			_nextChunk = result;

			return result;
		}

		public void remove() {
			throw new UnsupportedOperationException();
		}

		/**
		 * After forwarding the current to next, calculating the next chunk.
		 * After this method, the ChunkData.size() may be 0 if there is no more
		 * chunk.
		 */
		private void prepareNextChunk(ChunkData data) {
			// fill the next chunk, non-empty
			data.reset();
			while (i < line.size()) {
				fillChunk(data);
				if (data.size > 0) {
					break;
				}
			}
		}

		/**
		 * calculating the next chunk from the current position till the NaN
		 * data or chunk full. After this method, the ChunkData.size() may be 0
		 * if there is no valid data in this chunk.
		 */
		private void fillChunk(ChunkData data) {

			/* eat off the beginning NaNs */
			while (i < line.size()) {
				if (Double.isNaN(line.getX(i)) || Double.isNaN(line.getY(i))) {
					i++;
				} else {
					break;
				}
			}
			if (i >= line.size()) {
				return;
			}

			boolean drawMark = style.isSymbolsVisible();

			boolean haspre = false;
			double prex = Double.NaN;
			double prey = Double.NaN;
			boolean isPreBigNumber = false;

			while (i < line.size()) {

				double x = getXUtoD2(line.getX(i));
				double y = getYUtoD2(line.getY(i));

				/* break at NaN value */
				if (Double.isNaN(line.getX(i)) || Double.isNaN(line.getY(i))) {
					i++;
					break;
				}

				boolean isThisBigNumber = isBigNumber(x) || (isBigNumber(y));

				if (haspre) {
					// if previous is a big number bring to the clip border.
					if (isPreBigNumber) {
						Point2D a = new Point2D.Double(x, y);
						Point2D b = new Point2D.Double(prex, prey);
						Point2D newb = intersecate(clip, b, a);
						if (newb != null) {
							data.addPoint((float) newb.getX(),
									(float) newb.getY());
						}
					}

					/* bring this to the clip border */
					if (isThisBigNumber) {
						Point2D a = new Point2D.Double(x, y);
						Point2D b = new Point2D.Double(prex, prey);
						Point2D newa = intersecate(clip, a, b);
						if (newa != null) {
							data.addPoint((float) newa.getX(),
									(float) newa.getY());
						}
					}
				} else {
					haspre = true;
				}
				prex = x;
				prey = y;
				isPreBigNumber = isThisBigNumber;

				if (!isThisBigNumber) {
					/* data buffer */
					data.xBuf[data.size] = (float) x;
					data.yBuf[data.size] = (float) y;

					/* markColorBuf */
					if (drawMark) {
						data.markColorBuf[data.size] = style
								.getActualSymbolColor(i);
					}

					/* error buffer */
					if (line.getXError() != null && i < line.getXError().size()) {
						boolean vxlow = !Double.isNaN(line.getXErrorLow(i));
						boolean vxhigh = !Double.isNaN(line.getXErrorHigh(i));
						if (vxlow || vxhigh) {
							data.xErrorSize = data.size + 1;
							if (vxlow) {
								data.xLowBuf[data.size] = (float) getXUtoD2(line
										.getX(i) - line.getXErrorLow(i));
							} else {
								data.xLowBuf[data.size] = data.xBuf[data.size];
							}
							if (vxhigh) {
								data.xHighBuf[data.size] = (float) getXUtoD2(line
										.getX(i) + line.getXErrorHigh(i));
							} else {
								data.xHighBuf[data.size] = data.xBuf[data.size];
							}
						}
					}

					if (line.getYError() != null && i < line.getYError().size()) {
						boolean vylow = !Double.isNaN(line.getYErrorLow(i));
						boolean vyhigh = !Double.isNaN(line.getYErrorHigh(i));
						if (vylow || vyhigh) {
							data.yErrorSize = data.size + 1;
							if (vylow) {
								data.yLowBuf[data.size] = (float) getYUtoD2(line
										.getY(i) - line.getYErrorLow(i));
							} else {
								data.yLowBuf[data.size] = data.yBuf[data.size];
							}
							if (vyhigh) {
								data.yHighBuf[data.size] = (float) getYUtoD2(line
										.getY(i) + line.getYErrorHigh(i));
							} else {
								data.yHighBuf[data.size] = data.yBuf[data.size];
							}
						}
					}

					data.size++;
				}

				/*
				 * the next data point may generate 2 points by bring the big
				 * number to clip border if both this and next point are big
				 * number. So an extra array space must be reserved
				 */
				if (data.size >= CHUNK_SIZE - 1) {
					/*
					 * Do not forward the i to make next chunk starts from the
					 * line data in last chunk (need overlap).
					 */
					break;
				} else {
					i++;
				}
			}

		}

		private double getXUtoD2(double v) {
			return cg.getPhysicalTransform().getXPtoD(
					cg.getXViewportAxis().getAxisTransform().getTransP(v));
		}

		private double getYUtoD2(double v) {
			return cg.getPhysicalTransform().getYPtoD(
					cg.getYViewportAxis().getAxisTransform().getTransP(v));
		}

	}

	private LayerEx cg;

	private XYGraphPlotterEx style;

	private XYGraph line;

	private Rectangle2D clip;

	private final DataChunkIterator ite = new DataChunkIterator();

	private XYGraphPlotterDataChunker() {

	}

	private void setLineData(XYGraphPlotterEx dp) {
		this.line = dp.getGraph();
		this.cg = dp.getParent();
		this.style = dp;
	}

	private void setClip(Rectangle2D clip) {
		this.clip = clip;
	}

	/*
	 * Warning: Only one iterator allowed!
	 */
	public DataChunkIterator iterator() {
		ite.reset();
		return ite;
	}

	/**
	 * On some system, the underlayer graphic only process short int.
	 * 
	 * @param utoD2
	 * @return boolean
	 */
	private static final boolean isBigNumber(double utoD2) {
		return (utoD2 <= Short.MIN_VALUE || utoD2 >= Short.MAX_VALUE);
	}

	/**
	 * Calculate the intersection between the the line AB and the clip border.
	 * Return null if there is not intersection.
	 * 
	 * @param clip
	 *            the clip
	 * @param a
	 *            the last point
	 * @param b
	 *            the previous point
	 * @return the point on the intersection between the the line AB and the
	 *         clip border and closer to A.
	 */
	private static Point2D intersecate(Rectangle2D clip, Point2D a, Point2D b) {
		Line2D line = new Line2D.Double(a, b);
		Point2D result = null;
		double offset = 10;
		// Rectangle2D clip = g.getClip().getBounds2D();
		double slope = (b.getY() - a.getY()) / (b.getX() - a.getX());
		if (clip.intersectsLine(line)) {

			if (a.getX() < clip.getX() - offset) {

				result = new Point2D.Double(clip.getX() - offset, slope
						* (clip.getX() - offset - b.getX()) + b.getY());
			}
			if (a.getX() > clip.getMaxX() + offset) {
				result = new Point2D.Double(clip.getMaxX() + offset, slope
						* (clip.getMaxX() + offset - b.getX()) + b.getY());

			}
			if (a.getY() < clip.getY() - offset) {
				result = new Point2D.Double((clip.getY() - offset - b.getY())
						/ slope + b.getX(), clip.getY() - offset);
			}
			if (a.getY() > clip.getMaxY() + offset) {
				result = new Point2D.Double(
						(clip.getMaxY() + offset - b.getY()) / slope + b.getX(),
						clip.getMaxY() + offset);
			}

		}

		return result;
	}

	public static XYGraphPlotterDataChunker getInstance(XYGraphPlotterEx dp,
			Rectangle clip) {
		XYGraphPlotterDataChunker builder = _threadLocalBuilder.get();
		if (builder == null) {
			builder = new XYGraphPlotterDataChunker();
			_threadLocalBuilder.set(builder);
		}

		builder.setLineData(dp);
		builder.setClip(clip);

		return builder;
	}

}
