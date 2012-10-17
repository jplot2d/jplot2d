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
import java.util.Map;

/**
 * @author Jingjing Li
 * 
 */
public abstract class GraphImpl extends ComponentImpl implements GraphEx {

	private static Point2D LOCATION = new Point2D.Double();

	protected final LegendItemEx legendItem;

	protected GraphImpl(LegendItemEx legendItem) {
		this.legendItem = legendItem;
		legendItem.setParent(this);
	}

	public String getId() {
		if (getParent() != null) {
			return "Graph" + getParent().indexOf(this);
		} else {
			return "Graph@" + Integer.toHexString(System.identityHashCode(this));
		}
	}

	public InvokeStep getInvokeStepFormParent() {
		if (parent == null) {
			return null;
		}

		Method method;
		try {
			method = LayerEx.class.getMethod("getGraph", Integer.TYPE);
		} catch (NoSuchMethodException e) {
			throw new Error(e);
		}
		return new InvokeStep(method, getParent().indexOf(this));
	}

	public LayerEx getParent() {
		return (LayerEx) super.getParent();
	}

	public LegendItemEx getLegendItem() {
		return legendItem;
	}

	public Point2D getLocation() {
		return LOCATION;
	}

	public void setLocation(double locX, double locY) {
		throw new UnsupportedOperationException();
	}

	public Dimension2D getSize() {
		if (getParent() == null) {
			return null;
		} else {
			return getParent().getSize();
		}
	}

	@Override
	public ComponentEx copyStructure(Map<ElementEx, ElementEx> orig2copyMap) {
		GraphImpl result = (GraphImpl) super.copyStructure(orig2copyMap);

		if (orig2copyMap != null) {
			orig2copyMap.put(getLegendItem(), result.getLegendItem());
		}

		return result;
	}

}
