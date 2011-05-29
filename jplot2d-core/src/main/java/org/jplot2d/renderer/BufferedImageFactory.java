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
package org.jplot2d.renderer;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Transparency;
import java.awt.image.BufferedImage;

/**
 * A factory to create buffered images.
 * 
 * @author Jingjing Li
 */
public class BufferedImageFactory implements ImageFactory {

	private final int imageType;

	private final Color bgColor;

	/**
	 * @param imageType
	 *            type of the created image
	 * @param bgColor
	 *            background color
	 */
	public BufferedImageFactory(int imageType, Color bgColor) {
		this.imageType = imageType;
		if (bgColor == null) {
			this.bgColor = TRANSPARENT_COLOR;
		} else {
			this.bgColor = bgColor;
		}
	}

	/**
	 * Create a transparent buffered image. The image is used by component
	 * renderer.
	 * 
	 * @return a BufferedImage
	 */
	public BufferedImage createTransparentImage(int width, int height) {
		return new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
	}

	/**
	 * Create a buffered image. The image is the final image to draw everything.
	 * 
	 * @return a BufferedImage
	 */
	public BufferedImage createImage(int width, int height) {
		BufferedImage image = new BufferedImage(width, height, imageType);

		Graphics2D g = (Graphics2D) image.getGraphics();
		if (image.getTransparency() == Transparency.OPAQUE) {
			g.setBackground(bgColor);
			g.clearRect(0, 0, width, height);
		}
		g.dispose();

		return image;
	}

}
