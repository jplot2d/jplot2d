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
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.Transparency;
import java.awt.image.BufferedImage;

/**
 * A factory to create GraphicsConfiguration compatible images.
 * 
 * @author Jingjing Li
 * 
 */
public class GraphicsConfigurationCompatibleImageFactory implements ImageFactory {

	private static final ThreadLocal<BufferedImage> tlImage = new ThreadLocal<BufferedImage>();

	private final GraphicsConfiguration gconf;

	private final Color bgColor;

	/**
	 * @param gconf
	 *            a GraphicsConfiguration associated with an AWT Component
	 * @param bgColor
	 *            background color
	 */
	public GraphicsConfigurationCompatibleImageFactory(GraphicsConfiguration gconf, Color bgColor) {
		this.gconf = gconf;
		if (bgColor == null) {
			this.bgColor = TRANSPARENT_COLOR;
		} else {
			this.bgColor = bgColor;
		}
	}

	public BufferedImage createTransparentImage(int width, int height) {
		return gconf.createCompatibleImage(width, height, Transparency.TRANSLUCENT);
	}

	public BufferedImage createImage(int width, int height) {
		BufferedImage image = tlImage.get();
		if (image == null || image.getWidth() != width || image.getHeight() != height) {
			image = gconf.createCompatibleImage(width, height);
		} else {
			tlImage.remove();
		}

		Graphics2D g = (Graphics2D) image.getGraphics();
		g.setBackground(bgColor);
		g.clearRect(0, 0, width, height);
		g.dispose();

		return image;
	}

	public void cacheImage(BufferedImage image) {
		tlImage.set(image);
	}

}
