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
package org.jplot2d.renderer;

import java.awt.Color;
import java.awt.image.BufferedImage;

public interface ImageFactory {

    public static Color TRANSPARENT_COLOR = new Color(0, 0, 0, 0);

    /**
     * Create a transparent buffered image. The image is used by component renderer.
     *
     * @return a BufferedImage
     */
    public BufferedImage createTransparentImage(int width, int height);

    /**
     * Create a buffered image. The image is the final image to draw everything.
     *
     * @return a BufferedImage
     */
    public BufferedImage createImage(int width, int height);

    /**
     * Put the given buffered image into local cache for next usage.
     *
     * @param image the image to be reused
     */
    public void cacheImage(BufferedImage image);

}