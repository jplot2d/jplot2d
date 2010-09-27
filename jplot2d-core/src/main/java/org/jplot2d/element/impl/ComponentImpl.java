/*
 * This file is part of Herschel Common Science System (HCSS).
 * Copyright 2001-2010 Herschel Science Ground Segment Consortium
 *
 * HCSS is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of
 * the License, or (at your option) any later version.
 *
 * HCSS is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General
 * Public License along with HCSS.
 * If not, see <http://www.gnu.org/licenses/>.
 */
package org.jplot2d.element.impl;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Map;

import org.jplot2d.element.Component;
import org.jplot2d.element.Element;

public class ComponentImpl implements Component {

	protected ContainerImpl parent;

	protected Boolean visible;

	protected boolean cacheable;

	protected Boolean selectable;

	protected Boolean movable;

	protected int zOrder;

	protected Color color = null;

	protected String fontName;

	protected int fontStyle = -1;

	protected float fontSize = Float.NaN;

	protected Point2D physicalLocation;

	protected Dimension2D physicalSize;

	protected Point2D deviceLocation;

	private boolean redrawNeeded;

	/**
	 * True when the object is valid. An invalid object needs to be laied out.
	 * This flag is set to false when the object size is changed.
	 * 
	 * @serial
	 * @see #isValid
	 * @see #validate
	 * @see #invalidate
	 */
	private boolean valid = false;

	public ContainerImpl getParent() {
		return parent;
	}

	public void setParent(ContainerImpl parent) {
		this.parent = parent;
	}

	public Boolean getVisible() {
		return visible;
	}

	public void setVisible(Boolean visible) {
		this.visible = visible;
		if (parent != null) {
			parent.invalidate();
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
			if (parent != null) {
				parent.redraw();
			}
		}
	}

	public Boolean getSelectable() {
		return selectable;
	}

	public void setSelectable(Boolean selectable) {
		this.selectable = selectable;
	}

	public Boolean getMovable() {
		return movable;
	}

	public void setMovable(Boolean movable) {
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
		return new Font(fontName, fontStyle, (int) fontSize)
				.deriveFont(fontSize);
	}

	public void setFont(Font font) {
		fontName = font.getName();
		fontStyle = font.getStyle();
		fontSize = font.getSize2D();
	}

	public String getFontName() {
		return fontName;
	}

	public void setFontName(String name) {
		fontName = name;
	}

	public int getFontStyle() {
		return fontStyle;
	}

	public void setFontStyle(int style) {
		fontStyle = style;
	}

	public float getFontSize() {
		return fontSize;
	}

	public void setFontSize(float size) {
		fontSize = size;
	}

	public Point2D getPhysicalLocation() {
		return physicalLocation;
	}

	public void setPhysicalLocation(Point2D p) {
		physicalLocation = (p == null) ? null : (Point2D) p.clone();
	}

	public Point2D getLocation() {
		return deviceLocation;
	}

	public void setLocation(Point2D point) {
		deviceLocation = point;
	}

	public Dimension2D getPhysicalSize() {
		return physicalSize;
	}

	public Rectangle2D getPhysicalBounds() {
		return new Rectangle2D.Double(physicalLocation.getX(), physicalLocation
				.getY(), physicalSize.getWidth(), physicalSize.getHeight());
	}

	/**
	 * Returns the bounds in absolute device coordinate. The top container's x,y
	 * is always 0.
	 * 
	 * @return a Rectangle2D object.
	 */
	public Rectangle2D getBounds() {
		throw new UnsupportedOperationException();
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

	private static boolean isValidFontStyle(int style) {
		return (style & ~0x03) == 0;
	}

	/**
	 * Call this method to mark this method need to update.
	 */
	public void redraw() {
		if (cacheable) {
			redrawNeeded = true;
		} else if (parent != null) {
			parent.redraw();
		}
	}

	public boolean isRedrawNeeded() {
		return redrawNeeded;
	}

	public void clearRedrawNeeded() {
		redrawNeeded = false;
	}

	public void draw(Graphics2D g) {
		throw new UnsupportedOperationException();
	}

	public ComponentImpl deepCopy(Map<Element, Element> orig2copyMap) {
		ComponentImpl result = new ComponentImpl();
		result.copyFrom(this);
		if (orig2copyMap != null) {
			orig2copyMap.put(this, result);
		}
		return result;
	}

	void copyFrom(Component src) {
		copy(src, this);
	}

	private static void copy(Component src, Component dest) {
		dest.setVisible(src.getVisible());
		dest.setCacheable(src.isCacheable());
		dest.setSelectable(src.getSelectable());
		dest.setMovable(src.getMovable());
		dest.setZOrder(src.getZOrder());
		dest.setColor(src.getColor());
		/* copy the font instead of name,style,size */
		if (src.getFontName() != null && isValidFontStyle(src.getFontStyle())) {
			dest.setFont(src.getFont());
		} else {
			dest.setFontName(src.getFontName());
			dest.setFontStyle(src.getFontStyle());
			dest.setFontSize(src.getFontSize());
		}
		dest.setPhysicalLocation(src.getPhysicalLocation());
		dest.setLocation(src.getLocation());
	}

}
