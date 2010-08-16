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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.geom.Dimension2D;
import java.awt.geom.Rectangle2D;
import java.util.Collections;
import java.util.List;
import java.util.Map;

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

	private List<SubPlot> subplots = Collections.<SubPlot> emptyList();

	private Dimension2D vpPhySize = new DoubleDimension2D(4.0, 3.0);

	private Dimension2D phySize;

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
		// TODO Auto-generated method stub
		return null;
	}

	public void setTargetPhySize(Dimension2D physize) {
		// TODO Auto-generated method stub

	}

	public Dimension2D getPhySize() {
		return phySize;
	}

	public void setPhySize(Dimension2D phySize) {
		this.phySize = phySize;

	}

	public Dimension2D getViewportPhySize() {
		return vpPhySize;
	}

	public void setViewportPhySize(Dimension2D physize) {
		vpPhySize = physize;
	}

	public Rectangle2D getBounds() {
		return new Rectangle2D.Double(0, 0, (int) (phySize.getWidth() * scale),
				(int) (phySize.getHeight() * scale));
	}

	public Rectangle2D getBoundsP() {
		return new Rectangle2D.Double(0, 0, phySize.getWidth(), phySize
				.getHeight());
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
		g.setColor(Color.BLACK);
		g.drawLine(0, 0, containerSize.width, containerSize.height);
		g.drawLine(0, containerSize.height, containerSize.width, 0);
	}

	public Plot deepCopy(Map<Element, Element> orig2copyMap) {
		// TODO Auto-generated method stub
		orig2copyMap.put(this, this);
		return this;
	}

	public Element[] getElements() {
		return subplots.toArray(new Element[subplots.size()]);
	}

}
