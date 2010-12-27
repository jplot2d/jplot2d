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

import java.awt.Dimension;
import java.awt.Paint;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import org.jplot2d.data.XYGraph;
import org.jplot2d.util.LineHatchPaint;
import org.jplot2d.util.NumberUtils;

/**
 * Down-sample the line data and construct a closed shape for filling.
 * 
 * @author Jingjing Li
 * 
 */
public class XYGraphPlotterFiller {

	private static final ThreadLocal<XYGraphPlotterFiller> _threadLocalBuilder = new ThreadLocal<XYGraphPlotterFiller>();

	private final Path2D.Float path = new Path2D.Float();

	private LayerEx cg;

	private XYGraphPlotterEx style;

	private XYGraph line;

	private Rectangle2D clip;

	private int startx, starty;

	private int endx, endy;

	private BufferedImage hatchImg;

	private double lastHatchScale;

	private Paint hatchPaint;

	private XYGraphPlotterFiller() {

	}

	private void setLineData(XYGraphPlotterEx dp) {
		this.line = dp.getGraph();
		this.cg = dp.getParent();
		this.style = dp;

		reset();
	}

	private void setClip(Rectangle2D clip) {
		this.clip = clip;
	}

	public Shape getShape() {
		fill();
		closeLine();

		return path;
	}

	private void reset() {
		path.reset();
	}

	private void fill() {

		boolean hasPre = false;

		for (int i = 0; i < line.size(); i++) {

			/* ignore NaN value */
			if (Double.isNaN(line.getX(i)) || Double.isNaN(line.getY(i))) {
				continue;
			}

			double x = cg.getPhysicalTransform().getXPtoD(
					cg.getXViewportAxis().getAxisTransform()
							.getTransP(line.getX(i)));
			double y = cg.getPhysicalTransform().getYPtoD(
					cg.getYViewportAxis().getAxisTransform()
							.getTransP(line.getY(i)));

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

	private void closeLine() {
		switch (style.getFillClosureType()) {
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
	 * Create a paint to fill the shape in the given clip area.
	 * 
	 * @param hp
	 * @return
	 */
	public Paint createHatchPaint(LineHatchPaint hp) {
		double scale = cg.getPhysicalTransform().getScale();
		Dimension size = new Dimension((int) clip.getMaxX(),
				(int) clip.getMaxY());

		if (hatchImg != null && size.width <= hatchImg.getWidth()
				&& size.height <= hatchImg.getHeight()) {
			if (NumberUtils.approximate(this.lastHatchScale, scale, 4)) {
				return hatchPaint;
			}
		} else {
			/*
			 * re-create the hatch image. The image is 64px larger than required
			 * size.
			 */
			int imgWidth = size.width + 64;
			int imgHeight = size.height + 64;
			hatchImg = new BufferedImage(imgWidth, imgHeight,
					BufferedImage.TYPE_INT_ARGB);
		}

		lastHatchScale = scale;
		hatchPaint = hp.createPaint(hatchImg, scale);
		return hatchPaint;
	}

	public static XYGraphPlotterFiller getInstance(XYGraphPlotterImpl dp,
			Rectangle clip) {
		XYGraphPlotterFiller builder = _threadLocalBuilder.get();
		if (builder == null) {
			builder = new XYGraphPlotterFiller();
			_threadLocalBuilder.set(builder);
		}

		builder.setLineData(dp);
		builder.setClip(clip);

		return builder;
	}

}