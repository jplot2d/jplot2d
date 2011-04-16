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

/**
 * A text title of plot.
 * 
 * @author Jingjing Li
 * 
 */
public interface Title extends TextComponent {

	public static enum Position {
		TOPLEFT, TOPCENTER, TOPRIGHT, BOTTOMLEFT, BOTTOMCENTER, BOTTOMRIGHT
	};

	/**
	 * Gets the current position of this title in a layer.
	 * 
	 * @return the position.
	 */
	public Position getPosition();

	/**
	 * Sets the position of this title. Possible value is TOPLEFT, TOPCENTER,
	 * TOPRIGHT, BOTTOMLEFT, BOTTOMCENTER, BOTTOMRIGHT. Only when position is
	 * <code>null</code>, the title can be located by
	 * {@link #setLocation(Point2D)}, {@link #setHAlign()}, {@link #setVAlign()}
	 * .
	 * 
	 * @param position
	 *            the position of this title.
	 */
	public void setPosition(Position position);

	/**
	 * Moves this title to a new location.
	 * <p>
	 * Notice: This method should be called when the position is
	 * <code>null</code>, otherwise the behavior is not defined.
	 * 
	 * @param loc
	 *            the location of the title's base point.
	 */
	public void setLocation(Point2D loc);

}
