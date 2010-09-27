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
package org.jplot2d.element;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Map;

/**
 * A <em>component</em> is an object having a graphical representation that can
 * be drawn on the renderer and that can interact with the user.
 * 
 * @author Jingjing Li
 * 
 */
public interface Component extends Element {

	public static enum HAlign {
		LEFT, CENTER, RIGHT
	}

	public static enum VAlign {
		TOP, MIDDLE, BOTTOM
	}

	public static HAlign LEFT = HAlign.LEFT;

	public static HAlign CENTER = HAlign.CENTER;

	public static HAlign RIGHT = HAlign.RIGHT;

	public static VAlign TOP = VAlign.TOP;

	public static VAlign MIDDLE = VAlign.MIDDLE;

	public static VAlign BOTTOM = VAlign.BOTTOM;

	/**
	 * Gets the parent of this component.
	 * 
	 * @return the parent of this component
	 */
	public Component getParent();

	/**
	 * Determines whether this component should be visible when its parent is
	 * visible. Components are initially visible.
	 * 
	 * @return <code>true</code> if the component is visible, <code>false</code>
	 *         otherwise
	 * @see #setVisible
	 */
	public Boolean getVisible();

	/**
	 * Shows or hides this component depending on the value of parameter
	 * <code>b</code>.
	 * 
	 * @param b
	 *            if <code>true</code>, shows this component; otherwise, hides
	 *            this component
	 * @see #isVisible
	 */
	public void setVisible(Boolean b);

	/**
	 * Returns <code>true</code> if this component has its own rendering cache.
	 * 
	 * @return the cache mode of this component
	 */
	public boolean isCacheable();

	/**
	 * Sets if this component has its own rendering cache.
	 * 
	 * @param mode
	 *            the cache mode
	 */
	public void setCacheable(boolean mode);

	/**
	 * Returns <code>true</code> if the component is selectable by mouse. Only
	 * selectable component can be movable.
	 * 
	 * @return <code>true</code> if selectable
	 */
	public Boolean getSelectable();

	/**
	 * Set the selectable property.
	 * 
	 * @param select
	 *            if <code>true</code> object is selectable
	 */
	public void setSelectable(Boolean selectable);

	/**
	 * Returns <code>true</code> if the component is movable by mouse dragging.
	 * Only selectable component can be movable.
	 * 
	 * @return <code>true</code> if movable
	 */
	public Boolean getMovable();

	/**
	 * Set the movable property.
	 */
	public void setMovable(Boolean movable);

	/**
	 * Returns the z-order of this component.
	 * 
	 * @return the z-order of this component
	 */
	public int getZOrder();

	/**
	 * Sets the z-order of this component. The component with higher z-order is
	 * on top.
	 * 
	 * @param z
	 *            the z-order value
	 */
	public void setZOrder(int z);

	/**
	 * Gets the foreground color of this component.
	 * 
	 * @return this component's foreground color; if this component does not
	 *         have a foreground color, the foreground color of its parent is
	 *         returned
	 * @see #setColor
	 */
	public Color getColor();

	/**
	 * Sets the foreground color of this component.
	 * 
	 * @param c
	 *            the color to become this component's foreground color; if this
	 *            parameter is <code>null</code> then this component will
	 *            inherit the foreground color of its parent
	 * @see #getColor
	 */
	public void setColor(Color c);

	/**
	 * Returns the font of this component.
	 * 
	 * @return the font of this component
	 * @see #setFont
	 */
	public Font getFont();

	/**
	 * Sets the font for this component.
	 * 
	 * @param font
	 *            the desired <code>Font</code> for this component
	 * @see #getFont
	 */
	public void setFont(Font font);

	/**
	 * Returns the name of the font.
	 * 
	 * @return the name of the font.
	 */
	public String getFontName();

	/**
	 * Apply the new font with the given name
	 * 
	 * @param name
	 *            the font name.
	 */
	public void setFontName(String name);

	/**
	 * Returns the style of the font. The style can be PLAIN, BOLD, ITALIC, or
	 * BOLD+ITALIC.
	 * 
	 * @return the style of the font
	 * @see java.awt.Font
	 */
	public int getFontStyle();

	/**
	 * Apply a new style to the font. The style can be PLAIN, BOLD, ITALIC, or
	 * BOLD+ITALIC.
	 * 
	 * @param style
	 *            the style to apply
	 * @see java.awt.Font
	 */
	public void setFontStyle(int style);

	/**
	 * Returns the Font size.
	 * 
	 * @return the font size.
	 */
	public float getFontSize();

	/**
	 * Sets a new size of the string.
	 * 
	 * @param size
	 *            the new size of the font.
	 */
	public void setFontSize(float size);

	/**
	 * Returns the x,y origin of this plot component in its parent's physical
	 * coordinate.
	 * 
	 * @return the x,y origin of this plot component
	 */
	public Point2D getPhysicalLocation();

	/**
	 * Moves this plot component to a new location. The origin of the new
	 * location is specified by point <code>p</code>. Point2D <code>p</code> is
	 * given in the parent's physical coordinate space.
	 * 
	 * @param p
	 *            the point defining the origin of the new location, given in
	 *            the coordinate space of this component's parent
	 */
	public void setPhysicalLocation(Point2D loc);

	/**
	 * Gets the location in device coordinates.
	 * 
	 * @return location
	 */
	public Point2D getLocation();

	/**
	 * Sets the location in device coordinates.
	 */
	public void setLocation(Point2D point);

	public Dimension2D getPhysicalSize();

	/**
	 * Returns the physical bounds relative to its container. The units of
	 * bounds is pt (1/72 inch)
	 * 
	 * @return the physical bounds of this component.
	 */
	public Rectangle2D getPhysicalBounds();

	/**
	 * Returns the bounds in absolute device coordinate.
	 * 
	 * @return
	 */
	public Rectangle2D getBounds();

	/**
	 * Determines whether this component is valid. A component is valid when it
	 * is correctly sized and positioned within its parent container and all its
	 * children are also valid. In order to account for peers' size
	 * requirements, components are invalidated before they are first shown on
	 * the screen. By the time the parent container is fully realized, all its
	 * components will be valid.
	 * 
	 * @return <code>true</code> if the component is valid, <code>false</code>
	 *         otherwise
	 * @see #validate
	 * @see #invalidate
	 */
	public boolean isValid();

	/**
	 * Mark this component has a valid layout.
	 * 
	 * @see #invalidate
	 */
	public void validate();

	/**
	 * Invalidates this component. This component and all parents above it are
	 * marked as needing to be laid out. This method can be called often, so it
	 * needs to execute quickly.
	 * 
	 * @see #validate
	 */
	public void invalidate();

	/**
	 * Draw this component only. All its children is not drawn.
	 * 
	 * @param g
	 *            to the Graphics2D drawing
	 */
	void draw(Graphics2D g);

	/**
	 * @param orig2copyMap
	 *            original element to copy map
	 * @return
	 */
	public Component deepCopy(Map<Element, Element> orig2copyMap);

}
