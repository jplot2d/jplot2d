/**
 * Copyright 2010-2013 Jingjing Li.
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
package org.jplot2d.data;

/**
 * A interface can returns a boolean to determines for the associated element whether the value is valid or not.
 * When an element of the mask is False, the corresponding element is valid and is said to be unmasked.
 * When an element of the mask is True, the corresponding element is said to be masked (invalid).
 *
 * @author Jingjing Li
 */
public interface ImageMaskBuffer {

    /**
     * returns a boolean to determines for the associated element whether the value is valid or not.
     *
     * @param x The X coordinate of the pixel location
     * @param y The Y coordinate of the pixel location
     * @return an boolean
     */
    public boolean isMasked(int x, int y);

}