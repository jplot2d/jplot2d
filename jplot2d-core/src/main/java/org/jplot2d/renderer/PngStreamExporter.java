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
package org.jplot2d.renderer;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.IOException;
import java.io.OutputStream;

import javax.imageio.ImageIO;

/**
 * Export plot image to a png output stream.
 * 
 * @author Jingjing Li
 * 
 */
public class PngStreamExporter extends ImageRenderer {

	private static final ImageFactory INT_RGB_IMAGEFACTORY = new BufferedImageFactory(
			BufferedImage.TYPE_INT_RGB, Color.WHITE);

	public PngStreamExporter(OutputStream out) {
		this(INT_RGB_IMAGEFACTORY, out);
	}

	public PngStreamExporter(ImageFactory imageFactory, final OutputStream out) {
		super(imageFactory);

		addRenderingFinishedListener(new RenderingFinishedListener() {

			public void renderingFinished(RenderingFinishedEvent event) {
				try {
					ImageIO.write((RenderedImage) event.getResult(), "PNG", out);
				} catch (IOException e) {
					throw new RuntimeException("Png file I/O exception", e);
				}

			}
		});
	}

}
