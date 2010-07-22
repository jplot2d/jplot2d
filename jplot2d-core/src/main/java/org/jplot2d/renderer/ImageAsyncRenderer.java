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
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.jplot2d.element.Component;

/**
 * @author Jingjing Li
 * 
 */
public class ImageAsyncRenderer extends AsyncRenderer<BufferedImage> {

	private final int imageType;

	private final Color bgColor;

	/**
	 * @param imageType
	 * @param bgColor
	 */
	public ImageAsyncRenderer(int imageType, Color bgColor) {
		this.imageType = imageType;
		this.bgColor = bgColor;
	}

	@Override
	protected CompRenderCallable createCompRenderCallable(Component comp) {
		Rectangle bounds = comp.getBounds().getBounds();
		BufferedImage image = new BufferedImage(bounds.width, bounds.height,
				BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = image.createGraphics();
		g.translate(-bounds.x, -bounds.y);
		return new CompRenderCallable(comp, g, image, bounds);
	}

	protected BufferedImage assambleResult(AssemblyInfo<BufferedImage> ainfo,
			Dimension size) {

		int width = size.width;
		int height = size.height;
		BufferedImage image = new BufferedImage(width, height, imageType);
		Graphics2D g = (Graphics2D) image.getGraphics();
		if (image.getTransparency() == Transparency.OPAQUE) {
			g.setColor(bgColor);
			g.fillRect(0, 0, width, height);
		}

		for (Component c : ainfo.componentSet()) {
			Point loc = ainfo.getBounds(c).getLocation();
			Future<BufferedImage> f = ainfo.getFuture(c);
			try {
				BufferedImage bi = f.get();
				g.drawImage(bi, loc.x, loc.y, null);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return image;
	}

}
