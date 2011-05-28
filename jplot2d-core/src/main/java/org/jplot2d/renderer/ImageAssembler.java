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
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.jplot2d.element.impl.ComponentEx;

/**
 * Assembler is a service in rendering. It can create CompRenderCallable to a
 * renderer, and assemble rendered component into a final BufferedImage.
 * 
 * @author Jingjing Li
 * 
 */
public class ImageAssembler {

	private final int imageType;

	private final Color bgColor;

	/**
	 * @param imageType
	 * @param bgColor
	 */
	public ImageAssembler(int imageType, Color bgColor) {
		this.imageType = imageType;
		this.bgColor = bgColor;
	}

	/**
	 * Create a CompRenderCallable for the given component. The
	 * CompRenderCallable is used by component renderer.
	 * 
	 * @return a CompRenderCallable
	 */
	public CompRenderCallable<BufferedImage> createCompRenderCallable(
			Rectangle bounds, ComponentEx[] comps) {
		BufferedImage image = new BufferedImage(bounds.width, bounds.height,
				BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = image.createGraphics();
		g.translate(-bounds.x, -bounds.y);

		return new CompRenderCallable<BufferedImage>(comps, g, image, bounds);
	}

	/**
	 * Assemble the rendered component given in AssemblyInfo into a result.
	 * 
	 * @param size
	 *            the result size
	 * @param ainfo
	 *            the AssemblyInfo
	 * @return the assembled result
	 */
	public BufferedImage assembleResult(Dimension size, ImageAssemblyInfo ainfo) {
		int width = size.width;
		int height = size.height;
		BufferedImage image = new BufferedImage(width, height, imageType);
		Graphics2D g = (Graphics2D) image.getGraphics();
		if (image.getTransparency() == Transparency.OPAQUE) {
			g.setColor(bgColor);
			g.fillRect(0, 0, width, height);
		}

		for (ComponentEx c : ainfo.componentSet()) {
			Rectangle bounds = ainfo.getBounds(c);
			Future<BufferedImage> f = ainfo.getFuture(c);
			try {
				BufferedImage bi = f.get();
				g.drawImage(bi, bounds.x, bounds.y, null);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		g.dispose();
		return image;
	}

}
