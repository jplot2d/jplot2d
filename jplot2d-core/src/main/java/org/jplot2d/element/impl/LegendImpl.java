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
import java.util.Collection;

import org.jplot2d.element.HAlign;
import org.jplot2d.element.PhysicalTransform;
import org.jplot2d.element.VAlign;
import org.jplot2d.util.DoubleDimension2D;

/**
 * @author Jingjing Li
 * 
 */
public class LegendImpl extends ContainerImpl implements LegendEx {

	private double locX, locY;

	private PhysicalTransform pxf;

	private Position position = Position.BOTTOMCENTER;

	private HAlign halign;

	private VAlign valign;

	private boolean enabled = true;

	private final Collection<LegendItemEx> items = new ArrayList<LegendItemEx>();

	private double lengthConstraint = Double.NaN;

	private boolean relayoutItemsNeeded;

	public String getSelfId() {
		if (getParent() != null) {
			return "Legend";
		} else {
			return "Legend@"
					+ Integer.toHexString(System.identityHashCode(this));
		}
	}

	public SubplotEx getParent() {
		return (SubplotEx) super.getParent();
	}

	public Dimension2D getSize() {
		// TODO Auto-generated method stub
		return new DoubleDimension2D();
	}

	public Rectangle2D getBounds() {
		// TODO Auto-generated method stub
		return null;
	}

	public PhysicalTransform getPhysicalTransform() {
		if (pxf == null) {
			pxf = getParent().getPhysicalTransform().translate(
					getLocation().getX(), getLocation().getY());
		}
		return pxf;
	}

	public Point2D getLocation() {
		return new Point2D.Double(locX, locY);
	}

	public void setLocation(Point2D loc) {
		setLocation(loc.getX(), loc.getY());
	}

	public void setLocation(double locX, double locY) {
		if (getLocation().getX() != locX || getLocation().getY() != locY) {
			this.locX = locX;
			this.locY = locY;
			pxf = null;
			redraw();
		}
	}

	public Position getPosition() {
		return position;
	}

	public void setPosition(Position position) {
		this.position = position;
	}

	public HAlign getHAlign() {
		return halign;
	}

	public void setHAlign(HAlign halign) {
		this.halign = halign;
	}

	public VAlign getVAlign() {
		return valign;
	}

	public void setVAlign(VAlign valign) {
		this.valign = valign;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public void addLegendItem(LegendItemEx item) {
		items.add(item);
	}

	public void removeLegendItem(LegendItemEx item) {
		items.remove(item);
	}

	public double getLengthConstraint() {
		return lengthConstraint;
	}

	public void setLengthConstraint(double length) {
		if (lengthConstraint != length) {
			lengthConstraint = length;
			relayoutItemsNeeded = true;
		}
	}

	/*
	 * Only contribute contents when it has items.
	 */
	public boolean canContribute() {
		return false;
	}

	public void calcSize() {
		if (!relayoutItemsNeeded) {
			return;
		}

		relayoutItemsNeeded = true;
	}

}
