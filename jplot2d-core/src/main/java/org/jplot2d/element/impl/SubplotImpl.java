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
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.Dimension2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.jplot2d.element.Axis;
import org.jplot2d.element.AxisOrientation;
import org.jplot2d.element.Element;
import org.jplot2d.element.Layer;
import org.jplot2d.element.PhysicalTransform;
import org.jplot2d.element.Plot;
import org.jplot2d.util.DoubleDimension2D;

/**
 * @author Jingjing Li
 * 
 */
public class SubplotImpl extends ContainerImpl implements SubplotEx {

	private PhysicalTransform pxf;

	private Dimension2D viewportPreferredSize = new DoubleDimension2D(4.0, 3.0);

	private final List<Layer> layers = new ArrayList<Layer>();

	private final List<Axis> axes = new ArrayList<Axis>();

	private Rectangle2D viewportPhysicalBounds;

	private Plot getPlot() {
		return (Plot) getParent();
	}

	public void setPhysicalLocation(double locX, double locY) {
		super.setPhysicalLocation(locX, locY);
		pxf = null;
		redraw();
	}

	public PhysicalTransform getPhysicalTransform() {
		if (pxf == null) {
			pxf = getPlot().getPhysicalTransform().translate(physicalLocX,
					physicalLocY);
		}
		return pxf;
	}

	public void plotPhysicalTransformChanged() {
		pxf = null;
		redraw();
	}

	public Dimension2D getViewportPreferredSize() {
		return viewportPreferredSize;
	}

	public void setViewportPreferredSize(Dimension2D physize) {
		viewportPreferredSize = physize;
	}

	public Rectangle2D getViewportBounds() {
		return viewportPhysicalBounds;
	}

	public void setViewportBounds(Rectangle2D bounds) {
		this.viewportPhysicalBounds = bounds;
	}

	public Layer getLayer(int index) {
		return layers.get(index);
	}

	public Layer[] getLayers() {
		return layers.toArray(new Layer[layers.size()]);
	}

	public void addLayer(Layer layer) {
		layers.add(layer);
		((ComponentEx) layer).setParent(this);
	}

	public void removeLayer(Layer layer) {
		layers.remove(layer);
	}

	public Axis getAxis(int index) {
		return axes.get(index);
	}

	public AxisEx[] getAxes() {
		return axes.toArray(new AxisEx[axes.size()]);
	}

	public void addAxis(Axis axis) {
		axes.add(axis);
		((ComponentEx) axis).setParent(this);
	}

	public void removeAxis(Axis axis) {
		axes.remove(axis);
	}

	public void validate() {
		for (Layer layer : layers) {
			layer.getViewport().setPhysicalBounds();
		}
		for (Axis axis : axes) {
			if (axis.getOrientation() == AxisOrientation.HORIZONTAL) {
				axis.setLength(viewportPhysicalBounds.getWidth());
			} else {
				axis.setLength(viewportPhysicalBounds.getHeight());
			}
		}
		super.validate();
	}

	public void draw(Graphics2D g) {
		// TODO Auto-generated method stub
		g.setColor(Color.BLACK);
		Rectangle rect = this.getBounds().getBounds();
		g.drawLine(rect.x, rect.y, (int) rect.getMaxX(), (int) rect.getMaxY());
		g.drawLine(rect.x, (int) rect.getMaxY(), (int) rect.getMaxX(), rect.y);

	}

	public SubplotEx deepCopy(Map<Element, Element> orig2copyMap) {

		SubplotImpl result = new SubplotImpl();

		if (orig2copyMap != null) {
			orig2copyMap.put(this, result);
		}

		result.copyFrom(this);

		return result;
	}

	void copyFrom(SubplotImpl src) {
		super.copyFrom(src);
		pxf = src.pxf;
		viewportPreferredSize = src.viewportPreferredSize;
		viewportPhysicalBounds = src.viewportPhysicalBounds;
	}

}
