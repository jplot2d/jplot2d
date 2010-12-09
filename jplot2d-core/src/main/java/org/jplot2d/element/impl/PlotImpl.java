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

import java.awt.Dimension;
import java.awt.geom.Dimension2D;
import java.util.Map;

import org.jplot2d.element.PhysicalTransform;
import org.jplot2d.element.PlotSizeMode;
import org.jplot2d.util.DoubleDimension2D;

/**
 * @author Jingjing Li
 * 
 */
public class PlotImpl extends SubplotImpl implements PlotEx {

	private double scale = 1;

	private PlotSizeMode sizeMode = PlotSizeMode.FIT_CONTAINER_SIZE;

	private Dimension containerSize = new Dimension();

	private Dimension2D targetSize = new DoubleDimension2D(480, 320);

	public PlotImpl() {
		super.setCacheable(true);
		pxf = new PhysicalTransform(0.0, 0.0, scale);
	}

	public void setCacheable(boolean cacheMode) {
		// ignore setting cacheable
	}

	public PlotSizeMode getSizeMode() {
		return sizeMode;
	}

	public void setSizeMode(PlotSizeMode sizeMode) {
		this.sizeMode = sizeMode;
	}

	public void setSize(double physicalWidth, double physicalHeight) {
		super.setSize(physicalWidth, physicalHeight);
		/*
		 * Calculate scale based on container size and physical size.
		 */
		Dimension2D physize = getSize();
		double scaleX = containerSize.width / physize.getWidth();
		double scaleY = containerSize.height / physize.getHeight();
		this.scale = (scaleX < scaleY) ? scaleX : scaleY;

		updatePxf();
	}

	/**
	 * set this plot a new physical transform. this will trigger a redraw
	 * notification.
	 */
	private void updatePxf() {
		pxf = new PhysicalTransform(0.0, getSize().getHeight(), scale);
		redraw();

		// notify all subplots
		for (SubplotEx sp : subplots) {
			sp.parentPhysicalTransformChanged();
		}
	}

	public Dimension getContainerSize() {
		return containerSize;
	}

	public void setContainerSize(Dimension size) {
		this.containerSize = size;

		switch (getSizeMode()) {
		case FIT_CONTAINER_WITH_TARGET_SIZE: {
			Dimension2D tcSize = getTargetPaperSize();

			double scaleX = containerSize.width / tcSize.getWidth();
			double scaleY = containerSize.height / tcSize.getHeight();
			double scale = (scaleX < scaleY) ? scaleX : scaleY;

			double phyWidth = containerSize.width / scale;
			double phyHeight = containerSize.height / scale;

			setSize(phyWidth, phyHeight);
			break;
		}
		case FIT_CONTAINER_SIZE: {
			double phyWidth = containerSize.width / scale;
			double phyHeight = containerSize.height / scale;

			setSize(phyWidth, phyHeight);
			break;
		}
		case FIXED_SIZE:
		case FIT_CONTENTS: {
			/*
			 * Calculate scale based on container size and physical size.
			 */
			Dimension2D physize = getSize();
			double scaleX = containerSize.width / physize.getWidth();
			double scaleY = containerSize.height / physize.getHeight();
			this.scale = (scaleX < scaleY) ? scaleX : scaleY;

			updatePxf();
			break;
		}
		}
	}

	public Dimension2D getTargetPaperSize() {
		return targetSize;
	}

	public void setTargetPaperSize(Dimension2D paperSize) {
		targetSize = paperSize;
	}

	public void copyFrom(ComponentEx src, Map<ElementEx, ElementEx> orig2copyMap) {
		super.copyFrom(src, orig2copyMap);

		PlotImpl plot = (PlotImpl) src;
		sizeMode = plot.sizeMode;
		containerSize = (Dimension) plot.containerSize.clone();
		targetSize = (Dimension2D) plot.targetSize.clone();
		scale = plot.scale;

		// copy subplots
		for (SubplotEx sp : plot.subplots) {
			SubplotEx spCopy = (SubplotEx) sp.deepCopy(orig2copyMap);
			((ComponentEx) spCopy).setParent(this);
			subplots.add(spCopy);
		}

	}

}
