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

import java.awt.geom.Dimension2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.jplot2d.element.Axis;
import org.jplot2d.element.Element;
import org.jplot2d.element.Layer;
import org.jplot2d.element.PhysicalTransform;
import org.jplot2d.util.DoubleDimension2D;

/**
 * @author Jingjing Li
 * 
 */
public class SubplotImpl extends ContainerImpl implements SubplotEx {

	private PhysicalTransform pxf;

	private Dimension2D viewportPreferredSize = new DoubleDimension2D(4.0, 3.0);

	private final List<LayerEx> layers = new ArrayList<LayerEx>();

	private final List<AxisEx> xaxes = new ArrayList<AxisEx>();

	private final List<AxisEx> yaxes = new ArrayList<AxisEx>();

	private Rectangle2D viewportPhysicalBounds;

	public PlotEx getParent() {
		return (PlotEx) super.getParent();
	}

	public void setPhysicalLocation(double locX, double locY) {
		super.setPhysicalLocation(locX, locY);
		pxf = null;
		redraw();
	}

	public PhysicalTransform getPhysicalTransform() {
		if (pxf == null) {
			pxf = getParent().getPhysicalTransform().translate(physicalLocX,
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

	public LayerEx[] getLayers() {
		return layers.toArray(new LayerEx[layers.size()]);
	}

	public void addLayer(Layer layer) {
		layers.add((LayerEx) layer);
		((LayerEx) layer).setParent(this);
	}

	public void removeLayer(Layer layer) {
		layers.remove(layer);
	}

	public AxisEx getXAxis(int index) {
		return xaxes.get(index);
	}

	public AxisEx getYAxis(int index) {
		return xaxes.get(index);
	}

	public AxisEx[] getXAxes() {
		return xaxes.toArray(new AxisEx[xaxes.size()]);
	}

	public AxisEx[] getYAxes() {
		return yaxes.toArray(new AxisEx[yaxes.size()]);
	}

	public void addXAxis(Axis axis) {
		xaxes.add((AxisEx) axis);
		((ComponentEx) axis).setParent(this);
	}

	public void addYAxis(Axis axis) {
		yaxes.add((AxisEx) axis);
		((ComponentEx) axis).setParent(this);
	}

	public void removeAxis(Axis axis) {
		xaxes.remove(axis);
		yaxes.remove(axis);
	}

	public SubplotEx deepCopy(Map<Element, Element> orig2copyMap) {

		SubplotImpl result = new SubplotImpl();

		if (orig2copyMap != null) {
			orig2copyMap.put(this, result);
		}

		result.copyFrom(this);

		// copy layers
		for (LayerEx layer : layers) {
			LayerEx layerCopy = layer.deepCopy(orig2copyMap);
			layerCopy.setParent(result);
			result.layers.add(layerCopy);
		}

		// TODO: copy auxaxes's main
		for (AxisEx axis : xaxes) {
			AxisEx axisCopy = axis.deepCopy(orig2copyMap);
			axisCopy.setParent(result);
			result.xaxes.add(axisCopy);
		}
		for (AxisEx axis : yaxes) {
			AxisEx axisCopy = axis.deepCopy(orig2copyMap);
			axisCopy.setParent(result);
			result.yaxes.add(axisCopy);
		}

		return result;
	}

	private void copyFrom(SubplotImpl src) {
		super.copyFrom(src);
		pxf = src.pxf;
		viewportPreferredSize = src.viewportPreferredSize;
		viewportPhysicalBounds = src.viewportPhysicalBounds;
	}

}
