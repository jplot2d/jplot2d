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

import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.jplot2d.element.LayerDataPlot;
import org.jplot2d.element.ViewportAxis;
import org.jplot2d.element.Element;
import org.jplot2d.element.Marker;
import org.jplot2d.element.PhysicalTransform;
import org.jplot2d.util.MathElement;
import org.jplot2d.util.TeXMathUtils;

/**
 * @author Jingjing Li
 * 
 */
public class LayerImpl extends ContainerImpl implements LayerEx {

	private MathElement name;

	private List<LayerDataPlotEx> plotters = new ArrayList<LayerDataPlotEx>();

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

	public PhysicalTransform getPhysicalTransform() {
		return getParent().getPhysicalTransform();
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

	public LayerDataPlot getDataPlotter(int index) {
		return plotters.get(index);
	}

	public LayerDataPlotEx[] getDataPlotters() {
		return plotters.toArray(new LayerDataPlotEx[plotters.size()]);
	}

	public void addDataPlotter(LayerDataPlot plotter) {
		plotters.add((LayerDataPlotEx) plotter);
	}

	public void removeDataPlotter(LayerDataPlot plotter) {
		plotters.remove(plotter);
	}

	public Marker getMarker(int idx) {
		// TODO Auto-generated method stub
		return null;
	}

	public void addMarker(Marker marker) {
		// TODO Auto-generated method stub

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
		if (this.xaxis != null) {
			this.xaxis.addLayer(this);
		}
	}

	public void setYViewportAxis(ViewportAxis axis) {
		if (this.yaxis != null) {
			this.yaxis.removeLayer(this);
		}
		this.yaxis = (ViewportAxisEx) axis;
		if (this.xaxis != null) {
			this.yaxis.addLayer(this);
		}
	}

	public void setViewportAxes(ViewportAxis xaxis, ViewportAxis yaxis) {
		if (this.xaxis != null) {
			this.xaxis.removeLayer(this);
		}
		this.xaxis = (ViewportAxisEx) xaxis;
		if (this.xaxis != null) {
			this.xaxis.addLayer(this);
		}

		if (this.yaxis != null) {
			this.yaxis.removeLayer(this);
		}
		this.yaxis = (ViewportAxisEx) yaxis;
		if (this.xaxis != null) {
			this.yaxis.addLayer(this);
		}
	}

	public void draw(Graphics2D g) {
		for (LayerDataPlotEx plotter : plotters) {
			plotter.draw(g);
		}
	}

	public void copyFrom(ComponentEx src, Map<ElementEx, ElementEx> orig2copyMap) {
		super.copyFrom(src, orig2copyMap);

		LayerImpl layer = (LayerImpl) src;

		// copy plotter
		for (LayerDataPlotEx plotter : layer.plotters) {
			LayerDataPlotEx plotterCopy = (LayerDataPlotEx) plotter
					.deepCopy(orig2copyMap);
			plotterCopy.setParent(this);
			plotters.add(plotterCopy);
		}
	}

}
