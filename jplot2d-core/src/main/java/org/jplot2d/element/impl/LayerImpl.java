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

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.Dimension2D;
import java.awt.geom.Rectangle2D;
import java.util.Map;

import org.jplot2d.element.DataModel;
import org.jplot2d.element.Element;
import org.jplot2d.element.Marker;
import org.jplot2d.element.PhysicalTransform;
import org.jplot2d.util.MathElement;

/**
 * @author Jingjing Li
 * 
 */
public class LayerImpl extends ContainerImpl implements LayerEx {

	private NormalTransform xNormalTransform, yNormalTransform;

	public SubplotEx getParent() {
		return (SubplotEx) super.getParent();
	}

	public void setPhysicalLocation(double locX, double locY) {
		throw new UnsupportedOperationException(
				"Layer's physical location fix on (0,0)");
	}

	public Dimension2D getPhysicalSize() {
		return getParent().getPhysicalSize();
	}

	public void setPhysicalSize(double physicalWidth, double physicalHeight) {
		throw new UnsupportedOperationException(
				"Layer's physical size is exactly its subplot's physical size");
	}

	public Rectangle2D getPhysicalBounds() {
		Dimension2D size = getPhysicalSize();
		return new Rectangle2D.Double(0, 0, size.getWidth(), size.getHeight());
	}

	public PhysicalTransform getPhysicalTransform() {
		return getParent().getPhysicalTransform();
	}

	public MathElement getName() {
		// TODO Auto-generated method stub
		return null;
	}

	public void setName(MathElement name) {
		// TODO Auto-generated method stub

	}

	public DataModel getDataModel() {
		// TODO Auto-generated method stub
		return null;
	}

	public void setDataModel(DataModel dataModel) {
		// TODO Auto-generated method stub

	}

	public Marker getMarker(int idx) {
		// TODO Auto-generated method stub
		return null;
	}

	public void addMarker(Marker marker) {
		// TODO Auto-generated method stub

	}

	public NormalTransform getXNormalTransform() {
		return xNormalTransform;
	}

	public void setXNormalTransform(NormalTransform ntf) {
		this.xNormalTransform = ntf;
	}

	public NormalTransform getYNormalTransform() {
		return yNormalTransform;
	}

	public void setYNormalTransform(NormalTransform ntf) {
		this.yNormalTransform = ntf;
	}

	public void draw(Graphics2D g) {
		Shape oldClip = g.getClip();
		Rectangle2D clip = getPhysicalTransform().getPtoD(
				getParent().getViewportBounds());
		g.setClip(clip);

		g.setColor(Color.BLACK);
		Rectangle rect = this.getBounds().getBounds();
		g.drawLine(rect.x, rect.y, (int) rect.getMaxX(), (int) rect.getMaxY());
		g.drawLine(rect.x, (int) rect.getMaxY(), (int) rect.getMaxX(), rect.y);

		g.setClip(oldClip);
	}

	public LayerImpl deepCopy(Map<Element, Element> orig2copyMap) {
		LayerImpl result = new LayerImpl();
		if (orig2copyMap != null) {
			orig2copyMap.put(this, result);
		}

		result.copyFrom(this);

		return result;
	}

	private void copyFrom(LayerImpl src) {
		super.copyFrom(src);
		xNormalTransform = src.xNormalTransform;
		yNormalTransform = src.yNormalTransform;
	}

}
