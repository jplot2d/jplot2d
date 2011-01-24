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
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
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

	private String fontName;

	private int fontStyle = -1;

	private float fontSize = Float.NaN;

	private float fontScale = 1;

	private double locX, locY;

	protected PropertyChangeSupport _changes = new PropertyChangeSupport(this);

	private boolean redrawNeeded = true;;

	public void addPropertyChangeListener(PropertyChangeListener l) {
		_changes.addPropertyChangeListener(l);
	}

	public void removePropertyChangeListener(PropertyChangeListener l) {
		_changes.removePropertyChangeListener(l);
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

	public String getFontName() {
		return fontName;
	}

	public void setFontName(String name) {
		fontName = name;
	}

	public Color getEffectiveColor() {
		if (color != null) {
			return color;
		} else if (getParent() != null) {
			return getParent().getEffectiveColor();
		} else {
			return null;
		}
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

	public float getFontScale() {
		return fontScale;
	}

	public void setFontScale(float scale) {
		fontScale = scale;
	}

	public void setFont(Font font) {
		if (font == null) {
			fontName = null;
			fontStyle = -1;
			fontSize = Float.NaN;
		} else {
			fontName = font.getName();
			fontStyle = font.getStyle();
			fontSize = font.getSize2D();
		}
	}

	public String getEffectiveFontName() {
		if (fontName != null) {
			return fontName;
		} else if (getParent() != null) {
			return getParent().getEffectiveFontName();
		} else {
			return null;
		}
	}

	public int getEffectiveFontStyle() {
		if ((fontStyle & ~0x03) == 0) {
			return fontStyle;
		} else if (getParent() != null) {
			return getParent().getEffectiveFontStyle();
		} else {
			return -1;
		}
	}

	public float getEffectiveFontSize() {
		if (!Float.isNaN(fontSize)) {
			return fontSize;
		} else if (getParent() != null) {
			return getParent().getEffectiveFontSize() * fontScale;
		} else {
			return Float.NaN;
		}
	}

	public Font getEffectiveFont() {
		float size = getEffectiveFontSize();
		return new Font(getEffectiveFontName(), getEffectiveFontStyle(),
				(int) size).deriveFont(size);
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

	public ComponentEx deepCopy(Map<ElementEx, ElementEx> orig2copyMap) {
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

		// do not copy parent

		ComponentImpl comp = (ComponentImpl) src;

		visible = comp.visible;
		cacheable = comp.cacheable;
		selectable = comp.selectable;
		movable = comp.movable;
		zOrder = comp.zOrder;
		color = comp.color;
		fontName = comp.fontName;
		fontStyle = comp.fontStyle;
		fontSize = comp.fontSize;
		fontScale = comp.fontScale;
		locX = comp.locX;
		locY = comp.locY;
		redrawNeeded = comp.redrawNeeded;
	}

	protected void drawBounds(Graphics2D g) {
		if (getParent() == null) {
			return;
		}
		g.setColor(Color.BLACK);
		Rectangle rect = getParent().getPhysicalTransform()
				.getPtoD(getBounds()).getBounds();
		g.draw(new Rectangle(rect.x, rect.y, rect.width - 1, rect.height - 1));
		g.drawLine(rect.x, rect.y, (int) rect.getMaxX() - 1,
				(int) rect.getMaxY() - 1);
		g.drawLine(rect.x, (int) rect.getMaxY() - 1, (int) rect.getMaxX() - 1,
				rect.y);

	}

}
