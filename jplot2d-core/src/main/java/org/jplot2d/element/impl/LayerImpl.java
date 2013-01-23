/**
 * Copyright 2010-2012 Jingjing Li.
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
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.jplot2d.element.Graph;
import org.jplot2d.element.AxisTransform;
import org.jplot2d.element.Element;
import org.jplot2d.element.Annotation;
import org.jplot2d.element.Plot;
import org.jplot2d.transform.PaperTransform;

/**
 * @author Jingjing Li
 * 
 */
public class LayerImpl extends ContainerImpl implements LayerEx {

	private List<GraphEx> graphs = new ArrayList<GraphEx>();

	private AxisTransformEx xarm, yarm;

	private List<AnnotationEx> annotations = new ArrayList<AnnotationEx>();

	public String getId() {
		if (getParent() != null) {
			return "Layer" + getParent().indexOf(this);
		} else {
			return "Layer@" + Integer.toHexString(System.identityHashCode(this));
		}
	}

	public InvokeStep getInvokeStepFormParent() {
		if (parent == null) {
			return null;
		}

		Method method;
		try {
			method = Plot.class.getMethod("getLayer", Integer.TYPE);
		} catch (NoSuchMethodException e) {
			throw new Error(e);
		}
		return new InvokeStep(method, getParent().indexOf(this));
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
			return new Point2D.Double(0, 0);
		}
	}

	public Dimension2D getSize() {
		if (getParent() == null) {
			return null;
		} else {
			return getParent().getContentSize();
		}
	}

	public PaperTransform getPaperTransform() {
		if (getParent() != null) {
			return getParent().getPaperTransform();
		}
		return null;
	}

	public void parentPaperTransformChanged() {
		redrawChildren();
	}

	public void transformChanged() {
		redrawChildren();
	}

	private void redrawChildren() {
		for (GraphEx graph : graphs) {
			graph.redraw();
		}
		for (AnnotationEx annotation : annotations) {
			annotation.relocate();
		}
	}

	public ComponentEx[] getComponents() {
		return graphs.toArray(new ComponentEx[graphs.size()]);
	}

	public Graph getGraph(int index) {
		return graphs.get(index);
	}

	public GraphEx[] getGraphs() {
		return graphs.toArray(new GraphEx[graphs.size()]);
	}

	public void addGraph(Graph graph) {
		GraphEx gp = (GraphEx) graph;
		graphs.add(gp);
		gp.setParent(this);

		// add legend item
		if (getParent() != null) {
			getParent().getEnabledLegend().addLegendItem(gp.getLegendItem());
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

	public void removeGraph(Graph graph) {
		GraphEx gp = (GraphEx) graph;
		graphs.remove(gp);
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

	public int indexOf(GraphEx graph) {
		return graphs.indexOf(graph);
	}

	public AnnotationEx[] getAnnotations() {
		return annotations.toArray(new AnnotationEx[annotations.size()]);
	}

	public Annotation getAnnotation(int idx) {
		return annotations.get(idx);
	}

	public void addAnnotation(Annotation annotation) {
		AnnotationEx mex = (AnnotationEx) annotation;
		annotations.add(mex);
		mex.setParent(this);

		if (mex.canContributeToParent()) {
			redraw();
		} else if (mex.canContribute()) {
			rerender();
		}
	}

	public void removeAnnotation(Annotation annotation) {
		AnnotationEx annx = (AnnotationEx) annotation;

		annotations.remove(annx);
		annx.setParent(null);

		if (annx.canContributeToParent()) {
			redraw();
		} else if (annx.canContribute()) {
			rerender();
		}
	}

	public int indexOf(AnnotationEx annotation) {
		return annotations.indexOf(annotation);
	}

	public boolean canContributeToParent() {
		if (!isVisible() || isCacheable()) {
			return false;
		}
		for (GraphEx graph : graphs) {
			if (graph.isVisible() && !graph.isCacheable()) {
				return true;
			}
		}
		return false;
	}

	public boolean canContribute() {
		if (!isVisible()) {
			return false;
		}
		for (GraphEx graph : graphs) {
			if (graph.isVisible()) {
				return true;
			}
		}
		return false;
	}

	public AxisTransformEx getXAxisTransform() {
		return xarm;
	}

	public AxisTransformEx getYAxisTransform() {
		return yarm;
	}

	public void setXAxisTransform(AxisTransform axis) {
		if (this.xarm != null) {
			this.xarm.removeLayer(this);
		}
		this.xarm = (AxisTransformEx) axis;
		if (this.xarm != null) {
			this.xarm.addLayer(this);
		}
	}

	public void setYAxisTransform(AxisTransform axis) {
		if (this.yarm != null) {
			this.yarm.removeLayer(this);
		}
		this.yarm = (AxisTransformEx) axis;
		if (this.yarm != null) {
			this.yarm.addLayer(this);
		}
	}

	public void setAxesTransform(AxisTransform xaxis, AxisTransform yaxis) {
		setXAxisTransform(xaxis);
		setYAxisTransform(yaxis);
	}

	@Override
	public LayerEx copyStructure(Map<ElementEx, ElementEx> orig2copyMap) {
		LayerImpl result = (LayerImpl) super.copyStructure(orig2copyMap);

		// copy graphs
		for (GraphEx graph : this.graphs) {
			GraphEx graphCopy = (GraphEx) graph.copyStructure(orig2copyMap);
			graphCopy.setParent(result);
			result.graphs.add(graphCopy);
		}

		// copy annotations
		for (AnnotationEx annotation : this.annotations) {
			AnnotationEx copy = (AnnotationEx) annotation.copyStructure(orig2copyMap);
			copy.setParent(result);
			result.annotations.add(copy);
		}

		return result;
	}

}
