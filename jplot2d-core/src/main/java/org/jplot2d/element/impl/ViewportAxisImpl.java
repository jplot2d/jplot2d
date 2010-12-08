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
import org.jplot2d.util.Range2D;

public class ViewportAxisImpl extends ContainerImpl implements ViewportAxisEx {

	private AxisType type;

	private AxisOrientation orientation;

	private AxisLockGroupEx group;

	private NormalTransform ntf;

	private double length;

	private AxisTransform axf;

	private List<AxisEx> axes = new ArrayList<AxisEx>();

	private final List<LayerEx> layers = new ArrayList<LayerEx>();

	public ViewportAxisImpl() {
		super();

		group = new AxisLockGroupImpl();
		group.addViewportAxis(this);

	}

	public String getSelfId() {
		if (getParent() != null) {
			int xidx = getParent().indexOfXViewportAxis(this);
			if (xidx != -1) {
				return "XG" + xidx;
			}
			int yidx = getParent().indexOfYViewportAxis(this);
			if (yidx != -1) {
				return "YG" + yidx;
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

	public AxisType getType() {
		return type;
	}

	public void setType(AxisType type) {
		this.type = type;
	}

	/**
	 * Change type by lock group
	 * 
	 * @param type
	 */
	void changeType(AxisType type) {
		this.type = type;
		for (AxisEx axis : axes) {
			axis.getTick().axisTypeChanged();

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
		axf = null;
	}

	public double getLength() {
		return length;
	}

	public void setLength(double length) {
		this.length = length;
		axf = null;
	}

	public AxisTransform getAxisTransform() {
		if (axf == null) {
			double offset = 0;
			switch (this.getOrientation()) {
			case HORIZONTAL:
				offset = this.getLocation().getX();
				break;
			case VERTICAL:
				offset = this.getLocation().getY();
				break;
			}

			Range2D prange = new Range2D.Double(offset, offset + getLength());
			axf = type.getTransformType().createTransform(prange,
					ntf.getRangeW());
		}
		return axf;
	}

	public int indexOfAxis(AxisEx axis) {
		return axes.indexOf(axis);
	}

	public AxisEx[] getAxes() {
		return axes.toArray(new AxisEx[axes.size()]);
	}

	public void addAxis(Axis axis) {
		axes.add((AxisEx) axis);
	}

	public void removeAxis(Axis axis) {
		axes.remove(axis);
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

	public ViewportAxisEx deepCopy(Map<ElementEx, ElementEx> orig2copyMap) {
		ViewportAxisImpl result = new ViewportAxisImpl();
		result.copyFrom(this);
		if (orig2copyMap != null) {
			orig2copyMap.put(this, result);
		}
		return result;
	}

	protected void copyFrom(AxisImpl src) {
		super.copyFrom(src);
	}

}
