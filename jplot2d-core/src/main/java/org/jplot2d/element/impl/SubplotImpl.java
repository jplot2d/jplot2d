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

	private Dimension2D viewportPreferredSize = new DoubleDimension2D(400, 300);

	private final List<LayerEx> layers = new ArrayList<LayerEx>();

	private final List<ViewportAxisEx> xViewportAxis = new ArrayList<ViewportAxisEx>();

	private final List<ViewportAxisEx> yViewportAxis = new ArrayList<ViewportAxisEx>();

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

		for (ViewportAxisEx ag : xViewportAxis) {
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

	public void parentPhysicalTransformChanged() {
		pxf = null;
		redraw();

		// notify all subplots
		for (SubplotEx sp : subplots) {
			sp.parentPhysicalTransformChanged();
		}
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
		if (isValid()) {
			return;
		}

		layout();
		for (SubplotEx subplot : subplots) {
			subplot.validate();
		}
		super.validate();
	}

	public void invalidate() {
		if (isValid()) {
			valid = false;
			/*
			 * Don't call getParent().invalidate(), let layoutDirector decide
			 * it.
			 */
			if (layoutDirector != null)
				layoutDirector.invalidateLayout(this);
		}
	}

	private void layout() {
		if (layoutDirector != null)
			layoutDirector.layout(this);
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
		return xViewportAxis.get(index);
	}

	public ViewportAxisEx getYViewportAxis(int index) {
		return yViewportAxis.get(index);
	}

	public int indexOfXViewportAxis(ViewportAxisEx axisGroup) {
		return xViewportAxis.indexOf(axisGroup);
	}

	public int indexOfYViewportAxis(ViewportAxisEx axisGroup) {
		return yViewportAxis.indexOf(axisGroup);
	}

	public ViewportAxisEx[] getXViewportAxes() {
		return xViewportAxis.toArray(new ViewportAxisEx[xViewportAxis.size()]);
	}

	public ViewportAxisEx[] getYViewportAxes() {
		return yViewportAxis.toArray(new ViewportAxisEx[yViewportAxis.size()]);
	}

	public void addXViewportAxis(ViewportAxis axisGroup) {
		xViewportAxis.add((ViewportAxisEx) axisGroup);
		((ViewportAxisEx) axisGroup).setParent(this);
		((ViewportAxisEx) axisGroup).setOrientation(AxisOrientation.HORIZONTAL);
	}

	public void addYViewportAxis(ViewportAxis axisGroup) {
		yViewportAxis.add((ViewportAxisEx) axisGroup);
		((ViewportAxisEx) axisGroup).setParent(this);
		((ViewportAxisEx) axisGroup).setOrientation(AxisOrientation.VERTICAL);
	}

	public void removeXViewportAxis(ViewportAxis axisGroup) {
		if (axisGroup.getLayers().length > 0) {
			throw new IllegalStateException("The axis has layer attached");
		}

		((ViewportAxisEx) axisGroup).setParent(null);
		xViewportAxis.remove(axisGroup);
	}

	public void removeYViewportAxis(ViewportAxis axisGroup) {
		if (axisGroup.getLayers().length > 0) {
			throw new IllegalStateException("The axis has layer attached");
		}

		((ViewportAxisEx) axisGroup).setParent(null);
		yViewportAxis.remove(axisGroup);
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

	public void copyFrom(ComponentEx src, Map<ElementEx, ElementEx> orig2copyMap) {
		super.copyFrom(src, orig2copyMap);

		SubplotImpl sp = (SubplotImpl) src;
		layoutDirector = sp.layoutDirector;
		pxf = sp.pxf;
		viewportPreferredSize = sp.viewportPreferredSize;
		viewportPhysicalBounds = sp.viewportPhysicalBounds;

		// copy layers
		for (LayerEx layer : sp.layers) {
			LayerEx layerCopy = (LayerEx) layer.deepCopy(orig2copyMap);
			layerCopy.setParent(this);
			layers.add(layerCopy);
		}

		// copy axes
		for (ViewportAxisEx va : sp.xViewportAxis) {
			ViewportAxisEx vaCopy = (ViewportAxisEx) va.deepCopy(orig2copyMap);
			vaCopy.setParent(this);
			xViewportAxis.add(vaCopy);
		}
		for (ViewportAxisEx va : sp.yViewportAxis) {
			ViewportAxisEx vaCopy = (ViewportAxisEx) va.deepCopy(orig2copyMap);
			vaCopy.setParent(this);
			yViewportAxis.add(vaCopy);
		}

	}

}
