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
package org.jplot2d.element;

import java.awt.geom.Point2D;

import org.jplot2d.annotation.Property;
import org.jplot2d.annotation.PropertyGroup;

/**
 * Legend display legend items. If there is no item in a legend, it will not show.
 * 
 * @author Jingjing Li
 * 
 */
@PropertyGroup("Legend")
public interface Legend extends MovableComponent {

	public enum Position {
		TOPLEFT, TOPCENTER, TOPRIGHT, BOTTOMLEFT, BOTTOMCENTER, BOTTOMRIGHT, LEFTTOP, LEFTMIDDLE, LEFTBOTTOM, RIGHTTOP, RIGHTMIDDLE, RIGHTBOTTOM
	};

	/**
	 * Returns <code>true</code> if this legend is enabled. By default, the legend is enabled.
	 * 
	 * @return the enabled flag.
	 */
	@Property(order = 0)
	public boolean isEnabled();

	/**
	 * Set to <code>true</code> to let this legend to host items. Otherwise the items will be
	 * displayed in parent legend. The disabled legend will not show.
	 * 
	 * @param enabled
	 */
	public void setEnabled(boolean enabled);

	/**
	 * Gets the current position of this legend.
	 * 
	 * @return the position.
	 */
	@Property(order = 1)
	public Position getPosition();

	/**
	 * Sets the position of this legend. Only when position is <code>null</code> , the legend can be
	 * located by {@link #setLocation(Point2D)}, {@link #setHAlign()} , {@link #setVAlign()}.
	 * 
	 * @param position
	 *            the position of this legend.
	 */
	public void setPosition(Position position);

	/**
	 * Gets the location of this legend.
	 * 
	 * @return an instance of <code>Point</code> representing the base point of this legend
	 */
	@Property(order = 2)
	public Point2D getLocation();

	/**
	 * Moves this legend to a new location.
	 * <p>
	 * Notice: This method should be called when the position is <code>null</code>, otherwise the
	 * behavior is not defined.
	 * 
	 * @param loc
	 *            the base point of this legend
	 */
	public void setLocation(Point2D loc);

	/**
	 * Moves this legend to a new location.
	 * <p>
	 * Notice: This method should be called when the position is <code>null</code>, otherwise the
	 * behavior is not defined.
	 * 
	 * @param x
	 *            the x-coordinate of the new location
	 * @param y
	 *            the y-coordinate of the new location
	 */
	public void setLocation(double x, double y);

	/**
	 * Get the horizontal alignment.
	 * 
	 * @return the horizontal alignment.
	 */
	@Property(order = 3)
	public HAlign getHAlign();

	/**
	 * Set the horizontal alignment. The alignment can be LEFT, CENTER, or RIGHT. eg, LEFT means the
	 * base point is on the left of this legend.
	 * <p>
	 * Notice: This method should be called when the position is <code>null</code>, otherwise the
	 * behavior is not defined.
	 * 
	 * @param halign
	 *            horizontal alignment.
	 */
	public void setHAlign(HAlign halign);

	/**
	 * Get the vertical alignment.
	 * 
	 * @return the vertical alignment.
	 */
	@Property(order = 4)
	public VAlign getVAlign();

	/**
	 * Set the vertical alignment. The alignment can be TOP, MIDDLE, or BOTTOM. eg, TOP means the
	 * base point is on the top of this legend.
	 * <p>
	 * Notice: This method should be called when the position is <code>null</code>, otherwise the
	 * behavior is not defined.
	 * 
	 * @param valign
	 *            The vertical alignment.
	 */
	public void setVAlign(VAlign valign);

	/**
	 * Returns the number of columns in the legend item.
	 * 
	 * @return the number of columns
	 */
	@Property(order = 5)
	public int getColumns();

	/**
	 * Sets the number of columns. This method only take effect when position is
	 * <code>null<code>. Otherwise the columns is auto selected.
	 * 
	 * @param cols
	 *            number of columns
	 */
	public void setColumns(int columns);

	@Property(order = 6)
	public boolean isMovable();

	public void setMovable(boolean movable);

}
