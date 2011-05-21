/**
 * Copyright 2010, 2011 Jingjing Li.
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
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.jplot2d.element.GraphPlotter;
import org.jplot2d.element.AxisRangeManager;
import org.jplot2d.element.Element;
import org.jplot2d.element.Marker;
import org.jplot2d.element.PhysicalTransform;
import org.jplot2d.util.DoubleDimension2D;

/**
 * @author Jingjing Li
 * 
 */
public class LayerImpl extends ContainerImpl implements LayerEx {

	private PhysicalTransform pxf;

	private List<GraphPlotterEx> plotters = new ArrayList<GraphPlotterEx>();

	private AxisRangeManagerEx xarm, yarm;

	protected String getSelfId() {
		if (getParent() != null) {
			return "Layer" + getParent().indexOf(this);
		} else {
			return "Layer@"
					+ Integer.toHexString(System.identityHashCode(this));
		}
	}

	public PlotEx getParent() {
		return (PlotEx) super.getParent();
	}

	public Map<Element, Element> getMooringMap() {
		// never moored on axis
		return super.getMooringMap();
	}

	public Point2D getLocation() {
		if (getParent() == null) {
			return null;
		} else {
			Rectangle2D rect = getParent().getContentBounds();
			return new Point2D.Double(rect.getX(), rect.getY());
		}
	}

	public void updateLocation() {
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
		if (pxf == null && getParent() != null) {
			Rectangle2D rect = getParent().getContentBounds();
			pxf = getParent().getPhysicalTransform().translate(rect.getX(),
					rect.getY());
		}
		return pxf;
	}

	public void parentPhysicalTransformChanged() {
		pxf = null;
		redraw();
	}

	public int getComponentCount() {
		return plotters.size();
	}

	public ComponentEx getComponent(int index) {
		return plotters.get(index);
	}

	public int getIndexOfComponent(ComponentEx comp) {
		return plotters.indexOf(comp);
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

		// add legend item
		if (getParent() != null) {
			getParent().getLegend().addLegendItem(gp.getLegendItem());
		}

		if (gp.canContributeToParent()) {
			redraw();
		} else if (gp.canContribute()) {
			rerender();
		}

		if (gp.isVisible()) {
			if (xarm != null && xarm.getLockGroup().isAutoRange()) {
				xarm.getLockGroup().reAutoRange();
			}
			if (yarm != null && yarm.getLockGroup().isAutoRange()) {
				yarm.getLockGroup().reAutoRange();
			}
		}
	}

	public void removeGraphPlotter(GraphPlotter plotter) {
		GraphPlotterEx gp = (GraphPlotterEx) plotter;
		plotters.remove(gp);
		gp.setParent(null);

		// remove legend item
		if (getParent() != null) {
			getParent().getLegend().removeLegendItem(gp.getLegendItem());
		}

		if (gp.canContributeToParent()) {
			redraw();
		} else if (gp.canContribute()) {
			rerender();
		}

		if (gp.isVisible()) {
			if (xarm.getLockGroup().isAutoRange()) {
				xarm.getLockGroup().reAutoRange();
			}
			if (yarm.getLockGroup().isAutoRange()) {
				yarm.getLockGroup().reAutoRange();
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

	public boolean canContribute() {
		if (!isVisible()) {
			return false;
		}
		for (GraphPlotterEx gp : plotters) {
			if (gp.isVisible()) {
				return true;
			}
		}
		return false;
	}

	public AxisRangeManagerEx getXRangeManager() {
		return xarm;
	}

	public AxisRangeManagerEx getYRangeManager() {
		return yarm;
	}

	public void setXRangeManager(AxisRangeManager axis) {
		if (this.xarm != null) {
			this.xarm.removeLayer(this);
		}
		this.xarm = (AxisRangeManagerEx) axis;
		if (this.xarm != null) {
			this.xarm.addLayer(this);
		}
	}

	public void setYRangeManager(AxisRangeManager axis) {
		if (this.yarm != null) {
			this.yarm.removeLayer(this);
		}
		this.yarm = (AxisRangeManagerEx) axis;
		if (this.yarm != null) {
			this.yarm.addLayer(this);
		}
	}

	public void setRangeManager(AxisRangeManager xaxis, AxisRangeManager yaxis) {
		setXRangeManager(xaxis);
		setYRangeManager(yaxis);
	}

	@Override
	public LayerEx copyStructure(Map<ElementEx, ElementEx> orig2copyMap) {
		LayerImpl result = (LayerImpl) super.copyStructure(orig2copyMap);

		// copy plotter
		for (GraphPlotterEx plotter : this.plotters) {
			GraphPlotterEx plotterCopy = (GraphPlotterEx) plotter
					.copyStructure(orig2copyMap);
			plotterCopy.setParent(this);
			result.plotters.add(plotterCopy);
		}

		return result;
	}

	@Override
	public void copyFrom(ElementEx src) {
		LayerImpl layer = (LayerImpl) src;
		this.pxf = layer.pxf;
	}

}
