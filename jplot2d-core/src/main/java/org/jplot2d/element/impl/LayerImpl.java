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

import org.jplot2d.element.GraphPlotter;
import org.jplot2d.element.ViewportAxis;
import org.jplot2d.element.Element;
import org.jplot2d.element.Marker;
import org.jplot2d.element.PhysicalTransform;
import org.jplot2d.util.DoubleDimension2D;
import org.jplot2d.util.MathElement;
import org.jplot2d.util.TeXMathUtils;

/**
 * @author Jingjing Li
 * 
 */
public class LayerImpl extends ContainerImpl implements LayerEx {

	private PhysicalTransform pxf;

	private MathElement name;

	private List<GraphPlotterEx> plotters = new ArrayList<GraphPlotterEx>();

	private ViewportAxisEx xaxis, yaxis;

	public String getSelfId() {
		if (getParent() != null) {
			return "Layer" + getParent().indexOf(this);
		} else {
			return "Layer@" + System.identityHashCode(this);
		}
	}

	public SubplotEx getParent() {
		return (SubplotEx) super.getParent();
	}

	public Map<Element, Element> getMooringMap() {
		// never moored on axis
		return super.getMooringMap();
	}

	public void setLocation(double locX, double locY) {
		super.setLocation(locX, locY);
		pxf = null;
		redraw();
	}

	public Dimension2D getSize() {
		if (getParent() == null) {
			return null;
		} else {
			Rectangle2D rect = getParent().getContentBounds();
			return new DoubleDimension2D(rect.getWidth(), rect.getHeight());
		}
	}

	public Rectangle2D getBounds() {
		if (getParent() == null) {
			return null;
		} else {
			return getParent().getContentBounds();
		}
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
	}

	public String getName() {
		return TeXMathUtils.toString(name);
	}

	public void setName(String name) {
		this.name = TeXMathUtils.parseText(name);
	}

	public MathElement getNameModel() {
		return name;
	}

	public void setNameModel(MathElement name) {
		this.name = name;
	}

	public GraphPlotter getGraphPlotter(int index) {
		return plotters.get(index);
	}

	public GraphPlotterEx[] getGraphPlotters() {
		return plotters.toArray(new GraphPlotterEx[plotters.size()]);
	}

	public void addGraphPlotter(GraphPlotter plotter) {
		GraphPlotterEx gp = (GraphPlotterEx) plotter;
		plotters.add(gp);
		gp.setParent(this);

		if (gp.isVisible() && !gp.isCacheable()) {
			redraw();
		}
		if (gp.isVisible()) {
			if (xaxis != null && xaxis.getLockGroup().isAutoRange()) {
				xaxis.getLockGroup().reAutoRange();
			}
			if (yaxis != null && yaxis.getLockGroup().isAutoRange()) {
				yaxis.getLockGroup().reAutoRange();
			}
		}
	}

	public void removeGraphPlotter(GraphPlotter plotter) {
		GraphPlotterEx gp = (GraphPlotterEx) plotter;
		plotters.remove(gp);
		gp.setParent(null);

		if (gp.isVisible() && !gp.isCacheable()) {
			redraw();
		}
		if (gp.isVisible()) {
			if (xaxis.getLockGroup().isAutoRange()) {
				xaxis.getLockGroup().reAutoRange();
			}
			if (yaxis.getLockGroup().isAutoRange()) {
				yaxis.getLockGroup().reAutoRange();
			}
		}
	}

	public Marker getMarker(int idx) {
		// TODO Auto-generated method stub
		return null;
	}

	public void addMarker(Marker marker) {
		// TODO Auto-generated method stub

	}

	public boolean canContributeToParent() {
		if (!isVisible() || isCacheable()) {
			return false;
		}
		for (GraphPlotterEx gp : plotters) {
			if (gp.isVisible() && !gp.isCacheable()) {
				return true;
			}
		}
		return false;
	}

	public ViewportAxisEx getXViewportAxis() {
		return xaxis;
	}

	public ViewportAxisEx getYViewportAxis() {
		return yaxis;
	}

	public void setXViewportAxis(ViewportAxis axis) {
		if (this.xaxis != null) {
			this.xaxis.removeLayer(this);
		}
		this.xaxis = (ViewportAxisEx) axis;
		this.xaxis.addLayer(this);
	}

	public void setYViewportAxis(ViewportAxis axis) {
		if (this.yaxis != null) {
			this.yaxis.removeLayer(this);
		}
		this.yaxis = (ViewportAxisEx) axis;
		this.yaxis.addLayer(this);
	}

	public void setViewportAxes(ViewportAxis xaxis, ViewportAxis yaxis) {
		setXViewportAxis(xaxis);
		setYViewportAxis(yaxis);
	}

	public void detachAxes() {
		this.xaxis.removeLayer(this);
		this.yaxis.removeLayer(this);
	}

	public void copyFrom(ComponentEx src, Map<ElementEx, ElementEx> orig2copyMap) {
		super.copyFrom(src, orig2copyMap);

		LayerImpl layer = (LayerImpl) src;

		// copy plotter
		for (GraphPlotterEx plotter : layer.plotters) {
			GraphPlotterEx plotterCopy = (GraphPlotterEx) plotter
					.deepCopy(orig2copyMap);
			plotterCopy.setParent(this);
			plotters.add(plotterCopy);
		}
	}

}
