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
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.util.Collections;
import java.util.Map;

import org.jplot2d.element.Element;

public abstract class ComponentImpl extends ElementImpl implements ComponentEx {

	private boolean visible = true;

	private boolean cacheable;

	private boolean selectable;

	private boolean movable;

	private int zOrder;

	private Color color = null;

	private Font font;

	private double locX, locY;

	/**
	 * True when the object is valid. An invalid object needs to be laied out.
	 * This flag is set to false when the object size is changed.
	 * 
	 * @serial
	 * @see #isValid
	 * @see #validate
	 * @see #invalidate
	 */
	protected boolean valid = false;

	private boolean redrawNeeded;

	public final String getId() {
		if (parent != null) {
			return parent.getId() + "-" + getSelfId();
		} else {
			return getSelfId();
		}
	}

	public ContainerEx getParent() {
		return (ContainerEx) parent;
	}

	public Map<Element, Element> getMooringMap() {
		return Collections.emptyMap();
	}

	public boolean isVisible() {
		return visible;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
		if (getParent() != null) {
			getParent().invalidate();
		}
		redraw();
	}

	public boolean isCacheable() {
		return cacheable;
	}

	public void setCacheable(boolean cacheMode) {
		this.cacheable = cacheMode;
		redraw();
		if (cacheable) {
			if (getParent() != null) {
				getParent().redraw();
			}
		}
	}

	public boolean isSelectable() {
		return selectable;
	}

	public void setSelectable(boolean selectable) {
		this.selectable = selectable;
	}

	public boolean isMovable() {
		return movable;
	}

	public void setMovable(boolean movable) {
		this.movable = movable;
	}

	public int getZOrder() {
		return zOrder;
	}

	public void setZOrder(int z) {
		this.zOrder = z;
	}

	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		this.color = color;
	}

	public Font getFont() {
		return font;
	}

	public void setFont(Font font) {
		this.font = font;
	}

	public Point2D getLocation() {
		return new Point2D.Double(locX, locY);
	}

	public final void setLocation(Point2D p) {
		setLocation(p.getX(), p.getY());
	}

	public void setLocation(double locX, double locY) {
		this.locX = locX;
		this.locY = locY;
	}

	/**
	 * Determines whether this component is valid. A component is valid when it
	 * is correctly sized and positioned within its parent container and all its
	 * children are also valid.
	 * 
	 * @return <code>true</code> if the component is valid, <code>false</code>
	 *         otherwise
	 * @see #validate
	 * @see #invalidate
	 */
	public boolean isValid() {
		return valid;
	}

	/**
	 * Mark this component and all of its subcomponents has a valid layout.
	 * 
	 * @see #invalidate
	 */
	public void validate() {
		valid = true;
	}

	/**
	 * Invalidates this component. This component and all parents above it are
	 * marked as needing to be laid out. This method can be called often, so it
	 * needs to execute quickly.
	 * 
	 * @see #validate
	 */
	public void invalidate() {
		if (isValid()) {
			valid = false;
			if (getParent() != null) {
				getParent().invalidate();
			}
		}
	}

	public void redraw() {
		if (cacheable) {
			redrawNeeded = true;
		} else if (getParent() != null) {
			getParent().redraw();
		}
	}

	public boolean isRedrawNeeded() {
		return redrawNeeded;
	}

	public void clearRedrawNeeded() {
		redrawNeeded = false;
	}

	public void draw(Graphics2D g) {
		// draw nothing
	}

	public final ComponentEx deepCopy(Map<ElementEx, ElementEx> orig2copyMap) {
		ComponentImpl result;

		try {
			result = this.getClass().newInstance();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		result.copyFrom(this, orig2copyMap);

		if (orig2copyMap != null) {
			orig2copyMap.put(this, result);
		}

		return result;
	}

	public void copyFrom(ComponentEx src, Map<ElementEx, ElementEx> orig2copyMap) {
		parent = src.getParent();
		setVisible(src.isVisible());
		setCacheable(src.isCacheable());
		setSelectable(src.isSelectable());
		setMovable(src.isMovable());
		setZOrder(src.getZOrder());
		setColor(src.getColor());
		setFont(src.getFont());
		setLocation(src.getLocation());

		ComponentImpl comp = (ComponentImpl) src;
		locX = comp.locX;
		locY = comp.locY;
		valid = comp.valid;
		redrawNeeded = comp.redrawNeeded;
	}

}
