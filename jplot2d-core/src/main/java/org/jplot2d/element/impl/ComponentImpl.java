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

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Collections;
import java.util.Map;

import org.jplot2d.element.Element;
import org.jplot2d.transform.PaperTransform;

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

	protected PropertyChangeSupport _changes = new PropertyChangeSupport(this);

	private boolean redrawNeeded;

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

	public boolean canContribute() {
		return true;
	}

	public boolean isVisible() {
		return visible;
	}

	public void setVisible(boolean visible) {
		redrawCascade(this);
		this.visible = visible;
		redrawCascade(this);
	}

	public boolean isCacheable() {
		return cacheable;
	}

	public void setCacheable(boolean cacheMode) {
		// uncacheable -> cacheable:
		// 1. eliminate this component from its cacheable parent, mark need redraw,
		// 2. put redraw flag on this component
		// cacheable -> uncacheable:
		// 1. put redraw flag on this component
		// 2. put redraw flag on its cacheable parent
		redraw(this);
		this.cacheable = cacheMode;
		redraw(this);
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
		Color oldEC = getEffectiveColor();
		this.color = color;
		if (!getEffectiveColor().equals(oldEC)) {
			thisEffectiveColorChanged();
		}
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

	public String getFontName() {
		return fontName;
	}

	public void setFontName(String name) {
		String oldEFN = getEffectiveFontName();
		fontName = name;
		if (!getEffectiveFontName().equals(oldEFN)) {
			thisEffectiveFontChanged();
		}
	}

	public int getFontStyle() {
		return fontStyle;
	}

	public void setFontStyle(int style) {
		int oldEFS = getEffectiveFontStyle();
		fontStyle = style;
		if (getEffectiveFontStyle() != oldEFS) {
			thisEffectiveFontChanged();
		}
	}

	public float getFontSize() {
		return fontSize;
	}

	public void setFontSize(float size) {
		float oldEFS = getEffectiveFontSize();
		fontSize = size;
		if (getEffectiveFontSize() != oldEFS) {
			thisEffectiveFontChanged();
		}
	}

	public float getFontScale() {
		return fontScale;
	}

	public void setFontScale(float scale) {
		float oldEFS = getEffectiveFontSize();
		fontScale = scale;
		if (getEffectiveFontSize() != oldEFS) {
			thisEffectiveFontChanged();
		}
	}

	public void setFont(Font font) {
		Font oldEF = getEffectiveFont();
		if (font == null) {
			fontName = null;
			fontStyle = -1;
			fontSize = Float.NaN;
		} else {
			fontName = font.getName();
			fontStyle = font.getStyle();
			fontSize = font.getSize2D();
		}
		if (!getEffectiveFont().equals(oldEF)) {
			thisEffectiveFontChanged();
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
		return new Font(getEffectiveFontName(), getEffectiveFontStyle(), (int) size).deriveFont(size);
	}

	public final void parentEffectiveColorChanged() {
		if (color == null) {
			this.thisEffectiveColorChanged();
		}
	}

	public final void parentEffectiveFontChanged() {
		if (fontName == null || (fontStyle & ~0x03) != 0 || Float.isNaN(fontSize)) {
			this.thisEffectiveFontChanged();
		}
	}

	public PaperTransform getPaperTransform() {
		PaperTransform pxf = getParent().getPaperTransform();
		if (pxf == null) {
			return null;
		} else {
			Point2D loc = getLocation();
			return pxf.translate(loc.getX(), loc.getY());
		}
	}

	public Rectangle2D getBounds() {
		Dimension2D size = getSize();
		if (size == null) {
			return null;
		} else {
			return new Rectangle2D.Double(0, 0, size.getWidth(), size.getHeight());
		}
	}

	public Rectangle2D getSelectableBounds() {
		return getBounds();
	}

	public boolean isRedrawNeeded() {
		return redrawNeeded;
	}

	public void setRedrawNeeded(boolean flag) {
		redrawNeeded = flag;
	}

	/**
	 * Mark redraw flag on cacheable parent and re-render flag on root plot when the given component is added, or to be
	 * removed. All sub-components are marked cascadedly.
	 * 
	 * @param comp
	 *            the component added, or to be removed.
	 */
	protected static void redrawCascade(ComponentEx comp) {
		if (comp.isVisible()) {
			redraw(comp);
			if (comp instanceof ContainerEx) {
				for (ComponentEx subcomp : ((ContainerEx) comp).getComponents()) {
					redrawCascade(subcomp);
				}
			}
		}
	}

	/**
	 * Mark redraw flag on cacheable parent and re-render flag on root plot when the given component is changed
	 * 
	 * @param comp
	 *            the component changed
	 */
	protected static void redraw(ComponentEx comp) {
		if (comp.isVisible() && comp.canContribute()) {
			markRedraw(comp);
		}
	}

	/**
	 * Mark redraw flag on cacheable parent and re-render flag on root plot. Only the visible component can be marked.
	 * The property canContribute is not considered.
	 * 
	 * @param comp
	 *            the component changed
	 */
	private static void markRedraw(ComponentEx comp) {
		if (comp.isVisible()) {
			if (!comp.isCacheable() && comp.getParent() != null) {
				markRedraw(comp.getParent());
			} else {
				comp.setRedrawNeeded(true);
				markRerender(comp);
			}
		}
	}

	/**
	 * Mark re-render flag on root plot of the given component.
	 * 
	 * @param comp
	 *            the component
	 */
	private static void markRerender(ComponentEx comp) {
		if (comp.getParent() != null) {
			markRerender(comp.getParent());
		} else if (comp instanceof PlotEx) {
			((PlotEx) comp).setRerenderNeeded(true);
		}
	}

	@Override
	public ComponentEx copyStructure(Map<ElementEx, ElementEx> orig2copyMap) {
		return (ComponentEx) super.copyStructure(orig2copyMap);
	}

	@Override
	public void copyFrom(ElementEx src) {
		super.copyFrom(src);

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
		redrawNeeded = comp.redrawNeeded;
	}

	protected void drawBounds(Graphics2D g) {
		g.setColor(Color.RED);
		Rectangle rect = getPaperTransform().getPtoD(getBounds()).getBounds();
		g.draw(new Rectangle(rect.x, rect.y, rect.width - 1, rect.height - 1));
		g.drawLine(rect.x, rect.y, (int) rect.getMaxX() - 1, (int) rect.getMaxY() - 1);
		g.drawLine(rect.x, (int) rect.getMaxY() - 1, (int) rect.getMaxX() - 1, rect.y);
		g.drawString(this.getClass().getSimpleName(), rect.x, (int) (rect.getMaxY() - 1));
	}

}
