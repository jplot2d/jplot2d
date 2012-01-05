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

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.concurrent.Callable;

import org.jplot2d.element.impl.ComponentEx;

public class CompRenderCallable implements Callable<BufferedImage> {

	private final ComponentEx[] comps;

	private final ImageFactory imageFactory;

	private final Rectangle bounds;

	/**
	 * @param comps
	 *            the components in z-order
	 * @param imageFactory
	 * @param bounds
	 */
	public CompRenderCallable(ComponentEx[] comps, ImageFactory imageFactory, Rectangle bounds) {
		this.comps = comps;
		this.imageFactory = imageFactory;
		this.bounds = bounds;
	}

	public BufferedImage call() throws Exception {
		if (Thread.interrupted()) {
			return null;
		}

		BufferedImage image = imageFactory.createTransparentImage(bounds.width, bounds.height);
		Graphics2D g = image.createGraphics();
		g.translate(-bounds.x, -bounds.y);

		for (ComponentEx comp : comps) {
			if (Thread.interrupted()) {
				g.dispose();
				return null;
			}
			comp.draw(g);
		}

		g.dispose();

		return image;
	}

}