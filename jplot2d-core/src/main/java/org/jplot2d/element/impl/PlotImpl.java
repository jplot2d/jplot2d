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
import java.awt.geom.Rectangle2D;
import java.util.Map;

import org.jplot2d.element.PhysicalTransform;
import org.jplot2d.element.PlotSizeMode;
import org.jplot2d.util.DoubleDimension2D;

/**
 * @author Jingjing Li
 * 
 */
public class PlotImpl extends SubplotImpl implements PlotEx {

	private double scale = 72;

	private PlotSizeMode sizeMode = PlotSizeMode.FIT_CONTAINER_SIZE;

	private Dimension containerSize = new Dimension();

	private Dimension2D targetPhySize = new DoubleDimension2D(4.0, 3.0);

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

	public void setPhysicalSize(double physicalWidth, double physicalHeight) {
		super.setPhysicalSize(physicalWidth, physicalHeight);
		/*
		 * Calculate scale based on container size and physical size.
		 */
		Dimension2D physize = getSize();
		double scaleX = containerSize.width / physize.getWidth();
		double scaleY = containerSize.height / physize.getHeight();
		this.scale = (scaleX < scaleY) ? scaleX : scaleY;

		updatePxf();
	}

	public PhysicalTransform getPhysicalTransform() {
		return pxf;
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
			sp.plotPhysicalTransformChanged();
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

			setPhysicalSize(phyWidth, phyHeight);
			break;
		}
		case FIT_CONTAINER_SIZE: {
			double phyWidth = containerSize.width / scale;
			double phyHeight = containerSize.height / scale;

			setPhysicalSize(phyWidth, phyHeight);
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
		return targetPhySize;
	}

	public void setTargetPaperSize(Dimension2D paperSize) {
		targetPhySize = paperSize;
	}

	public PlotEx deepCopy(Map<ElementEx, ElementEx> orig2copyMap) {
		PlotImpl result = new PlotImpl();

		result.copyFrom(this);
		if (orig2copyMap != null) {
			orig2copyMap.put(this, result);
		}

		// copy subplots
		for (SubplotEx sp : subplots) {
			SubplotEx spCopy = ((SubplotEx) sp).deepCopy(orig2copyMap);
			((ComponentEx) spCopy).setParent(result);
			result.subplots.add(spCopy);
		}

		return this;
	}

	void copyFrom(PlotImpl src) {
		super.copyFrom(src);

		sizeMode = src.sizeMode;
		containerSize = (Dimension) src.containerSize.clone();
		targetPhySize = (Dimension2D) src.targetPhySize.clone();
		scale = src.scale;
	}

}
