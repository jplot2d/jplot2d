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

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.geom.Dimension2D;
import java.awt.geom.Rectangle2D;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.jplot2d.layout.LayoutDirector;

/**
 * @author Jingjing Li
 * 
 */
public class PlotImpl extends ContainerImpl implements Plot {

	private LayoutDirector director;

	private PlotSizeMode sizeMode = PlotSizeMode.FIT_CONTAINER_SIZE;

	private Dimension containerSize;

	private double scale = 72;

	private List<SubPlot> subplots = Collections.<SubPlot> emptyList();

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
	}

	public double getScale() {
		return scale;
	}

	public Dimension2D getTargetPhySize() {
		// TODO Auto-generated method stub
		return null;
	}

	public void setTargetPhySize(Dimension2D physize) {
		// TODO Auto-generated method stub

	}

	public Dimension2D getPhySize() {
		// TODO Auto-generated method stub
		return null;
	}

	public void setPhySize(Dimension2D physize) {
		// TODO Auto-generated method stub

	}

	public Dimension2D getViewportPhySize() {
		// TODO Auto-generated method stub
		return null;
	}

	public void setViewportPhySize(Dimension2D physize) {
		// TODO Auto-generated method stub

	}

	@Override
	public Rectangle2D getBounds() {
		return new Rectangle2D.Double(0, 0, containerSize.getWidth(),
				containerSize.getHeight());
	}

	@Override
	public Rectangle2D getBoundsP() {
		// TODO Auto-generated method stub
		return null;
	}

	public SubPlot getSubPlot(int n) {
		// TODO Auto-generated method stub
		return null;
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

	public void draw(Graphics2D g, boolean drawCacheable) {
		// TODO Auto-generated method stub

	}

	public Plot deepCopy(Map<Element, Element> orig2copyMap) {
		// TODO Auto-generated method stub
		return this;
	}

	public Element[] getElements() {
		return subplots.toArray(new Element[subplots.size()]);
	}

}
