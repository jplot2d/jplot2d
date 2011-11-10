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
package org.jplot2d.layout;

/**
 * The constraint defines the x,y indexes in the grid of grid layout. The x is the index from left
 * to right. The y is the index from bottom to top.
 * 
 * @author Jingjing Li
 * 
 */
public class GridConstraint {

	private final int x, y;

	public GridConstraint(int gridx, int gridy) {
		this.x = gridx;
		this.y = gridy;
	}

	public int getGridX() {
		return x;
	}

	public int getGridY() {
		return y;
	}

	public boolean equals(Object obj) {
		if (obj instanceof GridConstraint) {
			GridConstraint spgc = (GridConstraint) obj;
			return (x == spgc.x) && (y == spgc.y);
		}
		return false;
	}

}