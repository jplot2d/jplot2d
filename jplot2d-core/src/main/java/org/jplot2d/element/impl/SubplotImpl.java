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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jplot2d.element.Subplot;
import org.jplot2d.element.ViewportAxis;
import org.jplot2d.element.AxisOrientation;
import org.jplot2d.element.Element;
import org.jplot2d.element.Layer;
import org.jplot2d.element.PhysicalTransform;
import org.jplot2d.layout.LayoutDirector;
import org.jplot2d.util.DoubleDimension2D;

/**
 * @author Jingjing Li
 * 
 */
public class SubplotImpl extends ContainerImpl implements SubplotEx {

	protected PhysicalTransform pxf;

	private boolean autoMarginTop = true, autoMarginLeft = true,
			autoMarginBottom = true, autoMarginRight = true;

	private double marginTop, marginLeft, marginBottom, marginRight;

	private LayoutDirector layoutDirector;

	private Dimension2D viewportPreferredSize = new DoubleDimension2D(4.0, 3.0);

	private final List<LayerEx> layers = new ArrayList<LayerEx>();

	private final List<ViewportAxisEx> xAxisGroup = new ArrayList<ViewportAxisEx>();

	private final List<ViewportAxisEx> yAxisGroup = new ArrayList<ViewportAxisEx>();

	private Rectangle2D viewportPhysicalBounds;

	protected final List<SubplotEx> subplots = new ArrayList<SubplotEx>();

	public String getSelfId() {
		if (getParent() != null) {
			return "Subplot" + getParent().indexOf(this);
		} else {
			return "Subplot@" + System.identityHashCode(this);
		}
	}

	public PlotEx getParent() {
		return (PlotEx) super.getParent();
	}

	public Map<Element, Element> getMooringMap() {
		Map<Element, Element> result = new HashMap<Element, Element>();

		for (ViewportAxisEx ag : xAxisGroup) {
			if (ag.getLockGroup().getViewportAxes().length > 1) {
				result.put(ag, ag.getLockGroup());
			}
			for (LayerEx layer : ag.getLayers()) {
				if (layer.getParent() != this) {
					result.put(ag, layer);
				}
			}
		}
		return result;
	}

	public void setLocation(double locX, double locY) {
		super.setLocation(locX, locY);
		pxf = null;
		redraw();
	}

	public PhysicalTransform getPhysicalTransform() {
		if (pxf == null) {
			pxf = getParent().getPhysicalTransform().translate(
					getLocation().getX(), getLocation().getY());
		}
		return pxf;
	}

	public void plotPhysicalTransformChanged() {
		pxf = null;
		redraw();
	}

	public boolean isAutoMarginTop() {
		return autoMarginTop;
	}

	public boolean isAutoMarginLeft() {
		return autoMarginLeft;
	}

	public boolean isAutoMarginBottom() {
		return autoMarginBottom;
	}

	public boolean isAutoMarginRight() {
		return autoMarginRight;
	}

	public void setAutoMarginTop(boolean auto) {
		autoMarginTop = auto;
	}

	public void setAutoMarginLeft(boolean auto) {
		autoMarginLeft = auto;
	}

	public void setAutoMarginBottom(boolean auto) {
		autoMarginBottom = auto;
	}

	public void setAutoMarginRight(boolean auto) {
		autoMarginRight = auto;
	}

	public double getMarginTop() {
		return marginTop;
	}

	public double getMarginLeft() {
		return marginLeft;
	}

	public double getMarginBottom() {
		return marginBottom;
	}

	public double getMarginRight() {
		return marginRight;
	}

	public void setMarginTop(double marginTop) {
		this.marginTop = marginTop;
	}

	public void setMarginLeft(double marginLeft) {
		this.marginLeft = marginLeft;
	}

	public void setMarginBottom(double marginBottom) {
		this.marginBottom = marginBottom;
	}

	public void setMarginRight(double marginRight) {
		this.marginRight = marginRight;
	}

	public LayoutDirector getLayoutDirector() {
		return layoutDirector;
	}

	public void setLayoutDirector(LayoutDirector director) {
		this.layoutDirector = director;
	}

	public Object getConstraint(Subplot subplot) {
		return layoutDirector.getConstraint((SubplotEx) subplot);
	}

	public void setConstraint(Subplot subplot, Object constraint) {
		layoutDirector.setConstraint((SubplotEx) subplot, constraint);
	}

	public void validate() {
		if (isValid())
			return;
		super.validate();
		layout();
		for (SubplotEx subplot : subplots) {
			subplot.validate();
		}
	}

	private void layout() {
		if (layoutDirector != null)
			layoutDirector.layout();
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

	public int indexOf(LayerEx layer) {
		return layers.indexOf(layer);
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
		((LayerEx) layer).setParent(null);
		layer.setViewportAxes(null, null);
	}

	public ViewportAxisEx getXViewportAxis(int index) {
		return xAxisGroup.get(index);
	}

	public ViewportAxisEx getYViewportAxis(int index) {
		return yAxisGroup.get(index);
	}

	public int indexOfXViewportAxis(ViewportAxisEx axisGroup) {
		return xAxisGroup.indexOf(axisGroup);
	}

	public int indexOfYViewportAxis(ViewportAxisEx axisGroup) {
		return yAxisGroup.indexOf(axisGroup);
	}

	public ViewportAxisEx[] getXViewportAxes() {
		return xAxisGroup.toArray(new ViewportAxisEx[xAxisGroup.size()]);
	}

	public ViewportAxisEx[] getYViewportAxes() {
		return yAxisGroup.toArray(new ViewportAxisEx[yAxisGroup.size()]);
	}

	public void addXViewportAxis(ViewportAxis axisGroup) {
		xAxisGroup.add((ViewportAxisEx) axisGroup);
		((ViewportAxisEx) axisGroup).setParent(this);
		((ViewportAxisEx) axisGroup).setOrientation(AxisOrientation.HORIZONTAL);
	}

	public void addYViewportAxis(ViewportAxis axisGroup) {
		yAxisGroup.add((ViewportAxisEx) axisGroup);
		((ViewportAxisEx) axisGroup).setParent(this);
		((ViewportAxisEx) axisGroup).setOrientation(AxisOrientation.VERTICAL);
	}

	public void removeXViewportAxis(ViewportAxis axisGroup) {
		if (axisGroup.getLayers().length > 0) {
			throw new IllegalStateException("The axis has layer attached");
		}

		((ViewportAxisEx) axisGroup).setParent(null);
		xAxisGroup.remove(axisGroup);
	}

	public void removeYViewportAxis(ViewportAxis axisGroup) {
		if (axisGroup.getLayers().length > 0) {
			throw new IllegalStateException("The axis has layer attached");
		}

		((ViewportAxisEx) axisGroup).setParent(null);
		yAxisGroup.remove(axisGroup);
	}

	public SubplotEx getSubplot(int i) {
		return subplots.get(i);
	}

	public int indexOf(SubplotEx subplot) {
		return subplots.indexOf(subplot);
	}

	public SubplotEx[] getSubplots() {
		return subplots.toArray(new SubplotEx[subplots.size()]);
	}

	public void addSubplot(Subplot subplot, Object constraint) {
		subplots.add((SubplotEx) subplot);
		((SubplotEx) subplot).setParent(this);

		LayoutDirector ld = getLayoutDirector();
		if (ld != null) {
			ld.setConstraint((SubplotEx) subplot, constraint);
		}
	}

	public void removeSubplot(Subplot subplot) {
		LayoutDirector ld = getLayoutDirector();
		if (ld != null) {
			ld.remove((SubplotEx) subplot);
		}
	}

	public SubplotEx deepCopy(Map<ElementEx, ElementEx> orig2copyMap) {

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

		for (ViewportAxisEx axisGroup : xAxisGroup) {
			ViewportAxisEx axisGroupCopy = axisGroup.deepCopy(orig2copyMap);
			axisGroupCopy.setParent(result);
			result.xAxisGroup.add(axisGroupCopy);
		}
		for (ViewportAxisEx axisGroup : yAxisGroup) {
			ViewportAxisEx axisGroupCopy = axisGroup.deepCopy(orig2copyMap);
			axisGroupCopy.setParent(result);
			result.yAxisGroup.add(axisGroupCopy);
		}

		return result;
	}

	private void copyFrom(SubplotImpl src) {
		super.copyFrom(src);
		layoutDirector = src.layoutDirector;
		pxf = src.pxf;
		viewportPreferredSize = src.viewportPreferredSize;
		viewportPhysicalBounds = src.viewportPhysicalBounds;
	}

}
