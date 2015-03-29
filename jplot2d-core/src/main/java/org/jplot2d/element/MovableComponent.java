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
 * Defines some methods for movable component.
 *
 * @author Jingjing Li
 */
public interface MovableComponent extends PComponent {

    /**
     * Returns <code>true</code> if the component is movable by mouse dragging.
     * Only selectable component can be movable.
     *
     * @return <code>true</code> if movable
     */
    @SuppressWarnings("EmptyMethod")
    public boolean isMovable();

    /**
     * Set if the component is movable by mouse dragging.
     *
     * @param movable <code>true</code> if movable
     */
    @SuppressWarnings("EmptyMethod")
    public void setMovable(boolean movable);

    /**
     * Moves this component to a new location.
     * The location is specified by point in the parent's paper coordinate space.
     *
     * @param loc the base point given in the paper coordinate space
     */
    public void setLocation(Point2D loc);

    /**
     * Moves this component to a new location.
     *
     * @param x the x-coordinate of the new location
     * @param y the y-coordinate of the new location
     */
    public void setLocation(double x, double y);

}
