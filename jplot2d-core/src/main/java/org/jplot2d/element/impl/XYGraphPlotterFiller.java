/**
 * Copyright 2010,2011 Jingjing Li.
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

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;

import org.jplot2d.data.XYGraph;
import org.jplot2d.util.LineHatchPaint;

/**
 * Down-sample the line data and construct a closed shape for filling.
 * 
 * @author Jingjing Li
 * 
 */
public class XYGraphPlotterFiller {

	private final Path2D.Float path = new Path2D.Float();

	private LayerEx layer;

	private XYGraphPlotterEx plotter;

	private XYGraph graph;

	private Rectangle2D clip;

	private int startx, starty;

	private int endx, endy;

	/**
	 * @param plotter
	 * @param clip
	 * @return
	 */
	public static XYGraphPlotterFiller getInstance(XYGraphPlotterEx plotter, Rectangle clip) {
		XYGraphPlotterFiller builder = new XYGraphPlotterFiller();

		builder.setLineData(plotter);
		builder.setClip(clip);

		return builder;
	}

	private XYGraphPlotterFiller() {

	}

	private void setLineData(XYGraphPlotterEx dp) {
		this.graph = dp.getGraph();
		this.layer = dp.getParent();
		this.plotter = dp;
	}

	private void setClip(Rectangle2D clip) {
		this.clip = clip;
	}

	private Shape getShape() {
		switch (plotter.getChartType()) {
		case LINECHART:
			fill();
			break;
		case HISTOGRAM:
			fillHistogram();
			break;
		case HISTOGRAM_EDGE:
			fillEdgeHistogram();
			break;
		}
		closeLine();

		return path;
	}

	private void fill() {

		boolean hasPre = false;

		for (int i = 0; i < graph.size(); i++) {

			/* ignore NaN value */
			if (Double.isNaN(graph.getX(i)) || Double.isNaN(graph.getY(i))) {
				continue;
			}

			double x = getDeviceX(i);
			double y = getDeviceY(i);

			int ix = (int) (x + 0.5);
			int iy = (int) (y + 0.5);

			if (hasPre) {
				if (endx != ix || endy != iy) {
					path.lineTo(ix, iy);
				}
			} else {
				path.moveTo(ix, iy);
				startx = ix;
				starty = iy;
				hasPre = true;
			}

			endx = ix;
			endy = iy;
		}
	}

	/**
     * 
     */
	private void fillHistogram() {

		boolean hasPre = false;
		// the x coordinate of preIdx
		double preDotX = 0;

		for (int i = 0; i < graph.size(); i++) {

			/* ignore NaN value */
			if (Double.isNaN(graph.getX(i)) || Double.isNaN(graph.getY(i))) {
				continue;
			}

			double x = getDeviceX(i);
			double y = getDeviceY(i);
			int iy = (int) (y + 0.5);

			if (hasPre) {
				// the middle x
				int imx = (int) (preDotX + x + 1) / 2;
				if (endx != imx || endy != iy) {
					path.lineTo(imx, endy);
					path.lineTo(imx, iy);
					endx = imx;
					endy = iy;
				}
			} else {
				int ix = (int) (x + 0.5);
				path.moveTo(ix, iy);
				startx = ix;
				starty = iy;
				hasPre = true;
				endx = ix;
				endy = iy;
			}

			preDotX = x;
		}

		// the preDotX is the last x point
		if (hasPre) {
			path.lineTo(preDotX, endy);
		}
	}

	private void fillEdgeHistogram() {

		boolean hasPre = false;

		for (int i = 0; i < graph.size(); i++) {

			/* ignore NaN value */
			if (Double.isNaN(graph.getX(i)) || Double.isNaN(graph.getY(i))) {
				continue;
			}

			double x = getDeviceX(i);
			double y = getDeviceY(i);

			int ix = (int) (x + 0.5);
			int iy = (int) (y + 0.5);

			if (hasPre) {
				if (endx != ix || endy != iy) {
					path.lineTo(ix, endy);
					path.lineTo(ix, iy);
				}
			} else {
				path.moveTo(ix, iy);
				startx = ix;
				starty = iy;
				hasPre = true;
			}

			endx = ix;
			endy = iy;
		}

	}

	private double getDeviceX(int i) {
		return layer.getPaperTransform().getXPtoD(
				layer.getXAxisTransform().getNormalTransform().getTransP(graph.getX(i))
						* layer.getSize().getWidth());
	}

	private double getDeviceY(int i) {
		return layer.getPaperTransform().getYPtoD(
				layer.getYAxisTransform().getNormalTransform().getTransP(graph.getY(i))
						* layer.getSize().getHeight());
	}

	private void closeLine() {
		switch (plotter.getFillClosureType()) {
		case SELF:
			path.closePath();
			break;
		case LEFT:
			path.lineTo(-Short.MAX_VALUE, endy);
			path.lineTo(-Short.MAX_VALUE, starty);
			path.closePath();
			break;
		case RIGHT:
			path.lineTo(Short.MAX_VALUE, endy);
			path.lineTo(Short.MAX_VALUE, starty);
			path.closePath();
			break;
		case TOP:
			path.lineTo(endx, -Short.MAX_VALUE);
			path.lineTo(startx, -Short.MAX_VALUE);
			path.closePath();
			break;
		case BOTTOM:
			path.lineTo(endx, Short.MAX_VALUE);
			path.lineTo(startx, Short.MAX_VALUE);
			path.closePath();
			break;
		}
	}

	/**
	 * fill paint on the given graphics.
	 * 
	 * @param g
	 * @param p
	 */
	public void fill(Graphics2D g, Paint p) {
		if (p instanceof LineHatchPaint) {
			LineHatchPaint lhp = (LineHatchPaint) p;
			Path2D hatch = createHatchLines(lhp);

			g.setColor(lhp.getColor());
			g.setStroke(lhp.getStroke());
			g.draw(hatch);
		} else {
			g.setPaint(p);
			g.fill(getShape());
		}
	}

	/**
	 * Create a Path2D which contains all hatch lines
	 * 
	 * @param hp
	 * @return
	 */
	private Path2D createHatchLines(LineHatchPaint hp) {
		double scale = layer.getPaperTransform().getScale();
		Dimension size = new Dimension((int) clip.getMaxX(), (int) clip.getMaxY());

		return null;
	}

}
