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
import java.awt.Graphics2D;
import java.awt.geom.Dimension2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.jplot2d.element.Element;
import org.jplot2d.element.PhysicalTransform;
import org.jplot2d.element.PlotSizeMode;
import org.jplot2d.element.Subplot;
import org.jplot2d.layout.LayoutDirector;
import org.jplot2d.util.DoubleDimension2D;

/**
 * @author Jingjing Li
 * 
 */
public class PlotImpl extends ContainerImpl implements PlotEx {

	private double scale = 72;

	private PhysicalTransform pxf = new PhysicalTransform(0.0, 0.0, scale);

	private LayoutDirector director;

	private PlotSizeMode sizeMode = PlotSizeMode.FIT_CONTAINER_SIZE;

	private Dimension containerSize = new Dimension();

	private Dimension2D targetPhySize = new DoubleDimension2D(4.0, 3.0);

	private final List<SubplotEx> subplots = new ArrayList<SubplotEx>();

	public PlotImpl() {
		cacheable = true;
	}

	public void setCacheable(boolean cacheMode) {
		// ignore setting cacheable
	}

	public LayoutDirector getLayoutDirector() {
		return director;
	}

	public void setLayoutDirector(LayoutDirector director) {
		this.director = director;
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
		Dimension2D physize = getPhysicalSize();
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
		pxf = new PhysicalTransform(0.0, physicalHeight, scale);
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
			Dimension2D tcSize = getTargetPhySize();

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
			Dimension2D physize = getPhysicalSize();
			double scaleX = containerSize.width / physize.getWidth();
			double scaleY = containerSize.height / physize.getHeight();
			this.scale = (scaleX < scaleY) ? scaleX : scaleY;

			updatePxf();
			break;
		}
		}
	}

	public Dimension2D getTargetPhySize() {
		return targetPhySize;
	}

	public void setTargetPhySize(Dimension2D physize) {
		targetPhySize = physize;
	}

	public Rectangle2D getBounds() {
		return new Rectangle2D.Double(0, 0, getPhysicalSize().getWidth()
				* scale, getPhysicalSize().getHeight() * scale);
	}

	public SubplotEx getSubPlot(int i) {
		return subplots.get(i);
	}

	public SubplotEx[] getSubPlots() {
		return subplots.toArray(new SubplotEx[subplots.size()]);
	}

	public void addSubPlot(Subplot subplot, Object constraint) {
		subplots.add((SubplotEx) subplot);
		((SubplotEx) subplot).setParent(this);

		LayoutDirector ld = getLayoutDirector();
		if (ld != null) {
			ld.setConstraint(subplot, constraint);
		}
	}

	public void removeSubPlot(Subplot subplot) {
		LayoutDirector ld = getLayoutDirector();
		if (ld != null) {
			ld.remove(subplot);
		}
	}

	public void validate() {
		if (!isValid()) {
			doLayout();
			for (Subplot subplot : subplots) {
				((ComponentEx) subplot).validate();
			}
			super.validate();
		}
	}

	private void doLayout() {
		LayoutDirector director = this.director;
		if (director != null) {
			director.layout(this);
		}
	}

	public void draw(Graphics2D g) {
		// draw title and legend

		// draw subplots
		for (Subplot sp : subplots) {
			((SubplotEx) sp).draw(g);
		}
	}

	public PlotEx deepCopy(Map<Element, Element> orig2copyMap) {
		PlotImpl result = new PlotImpl();

		result.copyFrom(this);
		if (orig2copyMap != null) {
			orig2copyMap.put(this, result);
		}

		// copy subplots
		for (SubplotEx sp : subplots) {
			SubplotEx csp = ((SubplotEx) sp).deepCopy(orig2copyMap);
			((ComponentEx) csp).setParent(result);
			result.subplots.add(csp);
		}

		return this;
	}

	void copyFrom(PlotImpl src) {
		super.copyFrom(src);

		director = src.director;
		sizeMode = src.sizeMode;
		containerSize = (Dimension) src.containerSize.clone();
		targetPhySize = (Dimension2D) src.targetPhySize.clone();
		physicalWidth = src.physicalWidth;
		physicalHeight = src.physicalHeight;
		scale = src.scale;
		pxf = src.pxf;
	}

}
