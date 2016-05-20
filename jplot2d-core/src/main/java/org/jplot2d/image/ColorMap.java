/*
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
package org.jplot2d.image;

import java.awt.image.ColorModel;
import java.awt.image.LookupTable;

/**
 * The color map defines how to convert intensity rasters to displayable images.
 *
 * @author Jingjing Li
 */
public interface ColorMap {

    /**
     * The max number of significant bits of input range of lookup table. The max number is 15,
     * means 32768 color mapping.
     */
    public static final int MAX_INPUT_BITS = 15;

    /**
     * The number of significant bits of input range of lookup table. Usually this value is 8, means
     * 256 color mapping. The max value is 15, means 32768 color mapping.
     *
     * @return the bits array of input range
     */
    public int getInputBits();

    /**
     * The lookup table to convert intensity rasters to displayable rasters.
     *
     * @return the lookup table
     */
    public LookupTable getLookupTable();

    /**
     * The color model for the lookup result.
     *
     * @return The color model for the lookup result.
     */
    public ColorModel getColorModel();

}
