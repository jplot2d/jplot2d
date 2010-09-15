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
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import org.jplot2d.element.Component;
import org.jplot2d.element.Container;

public abstract class ComponentImpl implements Component {

	protected Container parent;

	protected volatile Boolean _visible;

	protected volatile boolean cacheable;

	protected volatile Boolean _selectable;

	protected volatile Boolean _movable;

	protected int zOrder;

	protected volatile Color _color = null;

	protected volatile String _fontName;

	protected volatile int _fontStyle = -1;

	protected volatile float _fontSize = Float.NaN;

	protected volatile Point2D _locP;

	protected volatile Point2D _loc;

	/**
	 * True when the object is valid. An invalid object needs to be layed out.
	 * This flag is set to false when the object size is changed.
	 * 
	 * @serial
	 * @see #isValid
	 * @see #validate
	 * @see #invalidate
	 */
	private boolean valid = false;

	public Container getParent() {
		return parent;
	}

	public Boolean getVisible() {
		return _visible;
	}

	public void setVisible(Boolean visible) {
		_visible = visible;
	}

	public boolean isCacheable() {
		return cacheable;
	}

	public void setCacheable(boolean cacheMode) {
		this.cacheable = cacheMode;
	}

	public Boolean getSelectable() {
		return _selectable;
	}

	public void setSelectable(Boolean selectable) {
		_selectable = selectable;
	}

	public Boolean getMovable() {
		return _movable;
	}

	public void setMovable(Boolean movable) {
		_movable = movable;
	}

	public int getZOrder() {
		return zOrder;
	}

	public void setZOrder(int z) {
		this.zOrder = z;
	}

	public Color getColor() {
		return _color;
	}

	public void setColor(Color color) {
		_color = color;
	}

	public Font getFont() {
		return new Font(_fontName, _fontStyle, (int) _fontSize)
				.deriveFont(_fontSize);
	}

	public void setFont(Font font) {
		_fontName = font.getName();
		_fontStyle = font.getStyle();
		_fontSize = font.getSize2D();
	}

	public String getFontName() {
		return _fontName;
	}

	public void setFontName(String name) {
		_fontName = name;
	}

	public int getFontStyle() {
		return _fontStyle;
	}

	public void setFontStyle(int style) {
		_fontStyle = style;
	}

	public float getFontSize() {
		return _fontSize;
	}

	public void setFontSize(float size) {
		_fontSize = size;
	}

	public Point2D getLocationP() {
		return _locP;
	}

	public void setLocationP(Point2D p) {
		_locP = (p == null) ? null : (Point2D) p.clone();
	}

	public Point2D getLocation() {
		return _loc;
	}

	public void setLocation(Point2D point) {
		_loc = point;
	}

	public abstract Rectangle2D getBoundsP();

	/**
	 * Returns the bounds in absolute device coordinate. The top container's x,y
	 * is always 0.
	 * 
	 * @return a Rectangle2D object.
	 */
	public abstract Rectangle2D getBounds();

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

	/**
	 * Replaces the properties in specified destination by the information in
	 * specified source.
	 */
	public static void adapt(Component src, Component dest) {
		if (src.getVisible() != null) {
			dest.setVisible(src.getVisible());
		}
		dest.setCacheable(src.isCacheable());
		if (src.getSelectable() != null) {
			dest.setSelectable(src.getSelectable());
		}
		if (src.getMovable() != null) {
			dest.setMovable(src.getMovable());
		}
		if (src.getColor() != null) {
			dest.setColor(src.getColor());
		}
		if (src.getFontName() != null && isValidFontStyle(src.getFontStyle())
				&& src.getFontSize() > 0) {
			dest.setFont(src.getFont());
		} else {
			if (src.getFontName() != null) {
				dest.setFontName(src.getFontName());
			}
			if (isValidFontStyle(src.getFontStyle())) {
				dest.setFontStyle(src.getFontStyle());
			}
			if (src.getFontSize() > 0) {
				dest.setFontSize(src.getFontSize());
			}
		}
		if (src.getLocationP() != null) {
			dest.setLocationP(src.getLocationP());
		} else if (src.getLocation() != null) {
			dest.setLocation(src.getLocation());
		}
	}

	public static void copy(Component src, Component dest) {
		dest.setVisible(src.getVisible());
		dest.setCacheable(src.isCacheable());
		dest.setSelectable(src.getSelectable());
		dest.setMovable(src.getMovable());
		dest.setColor(src.getColor());
		/* copy the font instead of name,style,size */
		if (src.getFontName() != null && isValidFontStyle(src.getFontStyle())) {
			dest.setFont(src.getFont());
		} else {
			dest.setFontName(src.getFontName());
			dest.setFontStyle(src.getFontStyle());
			dest.setFontSize(src.getFontSize());
		}
		dest.setLocationP(src.getLocationP());
		dest.setLocation(src.getLocation());
	}

	private static boolean isValidFontStyle(int style) {
		return (style & ~0x03) == 0;
	}

}
