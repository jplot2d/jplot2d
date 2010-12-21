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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jplot2d.axtrans.AxisTransform;
import org.jplot2d.axtrans.NormalTransform;
import org.jplot2d.axtype.AxisType;
import org.jplot2d.element.Axis;
import org.jplot2d.element.AxisLockGroup;
import org.jplot2d.element.AxisOrientation;
import org.jplot2d.element.Element;
import org.jplot2d.element.PhysicalTransform;
import org.jplot2d.util.Range2D;

public class ViewportAxisImpl extends ContainerImpl implements ViewportAxisEx {

	private AxisLockGroupEx group;

	private AxisType type;

	private AxisOrientation orientation;

	private NormalTransform ntf;

	private double offset;

	private double length;

	private AxisTransform axf;

	private final List<AxisEx> axes = new ArrayList<AxisEx>();

	private final List<LayerEx> layers = new ArrayList<LayerEx>();

	public ViewportAxisImpl() {
		super();

		group = new AxisLockGroupImpl();
		group.addViewportAxis(this);

		type = AxisType.LINEAR;
		ntf = type.getTransformType().createNormalTransform(
				type.getDefaultWorldRange());
	}

	public String getSelfId() {
		if (getParent() != null) {
			int xidx = getParent().indexOfXViewportAxis(this);
			if (xidx != -1) {
				return "X" + xidx;
			}
			int yidx = getParent().indexOfYViewportAxis(this);
			if (yidx != -1) {
				return "Y" + yidx;
			}
			return null;
		} else {
			return "AxisGroup@" + System.identityHashCode(this);
		}
	}

	public SubplotEx getParent() {
		return (SubplotEx) super.getParent();
	}

	public Map<Element, Element> getMooringMap() {
		Map<Element, Element> result = new HashMap<Element, Element>();

		if (group.getViewportAxes().length > 1) {
			result.put(this, group);
		}
		for (LayerEx layer : layers) {
			result.put(this, layer);
		}

		return result;
	}

	public PhysicalTransform getPhysicalTransform() {
		if (getParent() == null) {
			return null;
		} else {
			return getParent().getPhysicalTransform();
		}
	}

	public AxisType getType() {
		return type;
	}

	public void setType(AxisType type) {
		this.type = type;
		for (AxisEx axis : axes) {
			axis.getTick().setTickAlgorithm(type.getTickAlgorithm());
		}
	}

	public AxisOrientation getOrientation() {
		return orientation;
	}

	public void setOrientation(AxisOrientation orientation) {
		this.orientation = orientation;
	}

	public AxisLockGroupEx getLockGroup() {
		return group;
	}

	public AxisLockGroupEx setLockGroup(AxisLockGroup group) {
		AxisLockGroupEx result = this.group;
		if (this.group != null) {
			this.group.removeViewportAxis(this);
		}
		this.group = (AxisLockGroupEx) group;
		this.group.addViewportAxis(this);
		return result;
	}

	public NormalTransform getNormalTransform() {
		return ntf;
	}

	public void setNormalTransfrom(NormalTransform ntf) {
		this.ntf = ntf;
		updateAxisTransform();
	}

	public double getOffset() {
		return offset;
	}

	public void setOffset(double offset) {
		if (this.offset != offset) {
			this.offset = offset;
			updateAxisTransform();
		}
	}

	public double getLength() {
		return length;
	}

	public void setLength(double length) {
		if (this.length != length) {
			this.length = length;
			updateAxisTransform();
		}
	}

	public AxisTransform getAxisTransform() {
		if (axf == null) {
			Range2D prange = new Range2D.Double(0, getLength());
			axf = type.getTransformType().createTransform(prange,
					ntf.getRangeW());
		}
		return axf;
	}

	/**
	 * Update axis transform when normal transform or paper range changed.
	 */
	private void updateAxisTransform() {
		axf = null;
	}

	public Axis getAxis(int index) {
		return axes.get(index);
	}

	public int indexOfAxis(AxisEx axis) {
		return axes.indexOf(axis);
	}

	public AxisEx[] getAxes() {
		return axes.toArray(new AxisEx[axes.size()]);
	}

	public void addAxis(Axis axis) {
		axes.add((AxisEx) axis);
		((AxisEx) axis).setParent(this);
	}

	public void removeAxis(Axis axis) {
		axes.remove(axis);
		((AxisEx) axis).setParent(null);
	}

	public LayerEx[] getLayers() {
		return layers.toArray(new LayerEx[layers.size()]);
	}

	public void addLayer(LayerEx layer) {
		layers.add(layer);
	}

	public void removeLayer(LayerEx layer) {
		layers.remove(layer);
	}

	public void copyFrom(ComponentEx src, Map<ElementEx, ElementEx> orig2copyMap) {
		super.copyFrom(src, orig2copyMap);

		ViewportAxisImpl va = (ViewportAxisImpl) src;
		this.orientation = va.orientation;
		this.ntf = va.ntf;
		this.offset = va.offset;
		this.length = va.length;

		for (AxisEx axis : va.axes) {
			AxisEx aCopy = (AxisEx) axis.deepCopy(orig2copyMap);
			aCopy.setParent(this);
			axes.add(aCopy);
		}

	}

	public Range2D getRange() {
		return ntf.getRangeW();
	}

	public void setRange(double ustart, double uend) {
		// TODO Auto-generated method stub

	}

}
