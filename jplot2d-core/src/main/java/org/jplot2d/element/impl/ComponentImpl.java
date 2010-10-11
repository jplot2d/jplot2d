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
import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Map;

import org.jplot2d.element.Element;
import org.jplot2d.util.DoubleDimension2D;

public class ComponentImpl implements ComponentEx {

	protected ContainerEx parent;

	protected boolean visible = true;

	protected boolean cacheable;

	protected boolean selectable;

	protected boolean movable;

	protected int zOrder;

	protected Color color = null;

	protected String fontName;

	protected int fontStyle = -1;

	protected float fontSize = Float.NaN;

	protected double physicalLocX, physicalLocY;

	protected double physicalWidth, physicalHeight;

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

	private boolean redrawNeeded;

	public ContainerEx getParent() {
		return parent;
	}

	public void setParent(ContainerEx parent) {
		this.parent = parent;
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
		return new Point2D.Double(physicalLocX, physicalLocY);
	}

	public final void setPhysicalLocation(Point2D p) {
		setPhysicalLocation(p.getX(), p.getY());
	}

	public void setPhysicalLocation(double locX, double locY) {
		physicalLocX = locX;
		physicalLocY = locY;
	}

	public Dimension2D getPhysicalSize() {
		return new DoubleDimension2D(physicalWidth, physicalHeight);
	}

	public Rectangle2D getPhysicalBounds() {
		return new Rectangle2D.Double(physicalLocX, physicalLocY,
				physicalWidth, physicalHeight);
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
	public final void validate() {
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

	public ComponentEx deepCopy(Map<Element, Element> orig2copyMap) {
		ComponentImpl result = new ComponentImpl();
		result.copyFrom(this);
		if (orig2copyMap != null) {
			orig2copyMap.put(this, result);
		}
		return result;
	}

	protected void copyFrom(ComponentImpl src) {
		parent = src.getParent();
		setVisible(src.isVisible());
		setCacheable(src.isCacheable());
		setSelectable(src.isSelectable());
		setMovable(src.isMovable());
		setZOrder(src.getZOrder());
		setColor(src.getColor());
		/* copy the font instead of name,style,size */
		if (src.getFontName() != null && isValidFontStyle(src.getFontStyle())) {
			setFont(src.getFont());
		} else {
			setFontName(src.getFontName());
			setFontStyle(src.getFontStyle());
			setFontSize(src.getFontSize());
		}
		physicalLocX = src.physicalLocX;
		physicalLocY = src.physicalLocY;
		physicalWidth = src.physicalWidth;
		physicalHeight = src.physicalHeight;
		valid = src.valid;
		redrawNeeded = src.redrawNeeded;
	}

}
