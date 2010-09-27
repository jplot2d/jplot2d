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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.geom.Dimension2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.jplot2d.element.Element;
import org.jplot2d.element.Plot;
import org.jplot2d.element.PlotSizeMode;
import org.jplot2d.element.SubPlot;
import org.jplot2d.layout.LayoutDirector;
import org.jplot2d.util.DoubleDimension2D;

/**
 * @author Jingjing Li
 * 
 */
public class PlotImpl extends ContainerImpl implements Plot {

	private LayoutDirector director;

	private PlotSizeMode sizeMode = PlotSizeMode.FIT_CONTAINER_SIZE;

	private Dimension containerSize = new Dimension();

	private double scale = 72;

	private Dimension2D targetPhySize = new DoubleDimension2D(4.0, 3.0);

	private final List<SubPlotImpl> subplots = new ArrayList<SubPlotImpl>();

	private Dimension2D viewportPhySize = new DoubleDimension2D(4.0, 3.0);

	public PlotImpl() {
		cacheable = true;
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

	public Dimension getContainerSize() {
		return containerSize;
	}

	public void setContainerSize(Dimension size) {
		this.containerSize = size;
		invalidate();
	}

	public double getScale() {
		return scale;
	}

	public Dimension2D getTargetPhySize() {
		return targetPhySize;
	}

	public void setTargetPhySize(Dimension2D physize) {
		targetPhySize = physize;
	}

	public Dimension2D getViewportPhySize() {
		return viewportPhySize;
	}

	public void setViewportPhySize(Dimension2D physize) {
		viewportPhySize = physize;
	}

	public Rectangle2D getBounds() {
		return new Rectangle2D.Double(0, 0, getPhysicalSize().getWidth()
				* scale, getPhysicalSize().getHeight() * scale);
	}

	public SubPlot getSubPlot(int i) {
		return subplots.get(i);
	}

	public void addSubPlot(SubPlot subplot, Object constraint) {
		LayoutDirector ld = getLayoutDirector();
		if (ld != null) {
			ld.setConstraint(subplot, constraint);
		}
	}

	public void removeSubPlot(SubPlot subplot) {
		LayoutDirector ld = getLayoutDirector();
		if (ld != null) {
			ld.remove(subplot);
		}
	}

	public void validate() {
		if (!isValid()) {
			super.validate();
			for (SubPlot subplot : subplots) {
				subplot.validate();
			}
		}
	}

	public void draw(Graphics2D g) {
		g.setColor(Color.BLACK);
		g.drawLine(0, 0, containerSize.width, containerSize.height);
		g.drawLine(0, containerSize.height, containerSize.width, 0);
	}

	public PlotImpl deepCopy(Map<Element, Element> orig2copyMap) {
		PlotImpl result = new PlotImpl();

		result.copyFrom(this);
		if (orig2copyMap != null) {
			orig2copyMap.put(this, result);
		}

		// copy subplots
		for (SubPlotImpl sp : subplots) {
			SubPlotImpl csp = sp.deepCopy(orig2copyMap);
			csp.setParent(result);
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
		physicalSize = (Dimension2D) src.physicalSize.clone();
		scale = src.scale;
	}

}
