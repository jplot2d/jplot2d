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
package org.jplot2d.swing;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.logging.Logger;

import javax.swing.JComponent;

import org.jplot2d.env.RenderEnvironment;
import org.jplot2d.renderer.ImageAsyncRenderer;

/**
 * @author Jingjing Li
 * 
 */
public class JPlot2DComponent extends JComponent {

	private static final long serialVersionUID = 1L;

	static Logger logger = Logger.getLogger("org.jplot2d.swing");

	private final RenderEnvironment env;

	private final ImageAsyncRenderer r;

	private volatile BufferedImage image;

	public JPlot2DComponent(RenderEnvironment env) {
		this.env = env;
		setDoubleBuffered(false);
		setBackground(Color.GRAY);
		setOpaque(true);

		r = new ImageAsyncRenderer(BufferedImage.TYPE_INT_RGB,
				getPlotBackground()) {
			private long ifsn;

			@Override
			protected void renderingComplete(long fsn, BufferedImage image) {
				if (fsn < ifsn) {
					logger
							.info("[R] Rendering finished in wrong order, drop F."
									+ fsn + " Current frame is " + ifsn);
					return;
				}

				ifsn = fsn;
				repaint();
			}

		};
		env.addRenderer(r);
	}

	/**
	 * @return
	 */
	private Color getPlotBackground() {
		return Color.WHITE;
	}

	public void setBounds(int x, int y, int width, int height) {
		super.setBounds(x, y, width, height);
		env.getPlot().setContainerSize(new Dimension(width, height));
	}

	public void paintComponent(Graphics g) {
		if (image != null) {
			int width = image.getWidth();
			int height = image.getHeight();
			int x = (getWidth() - width) / 2;
			int y = (getHeight() - height) / 2;
			g.drawImage(image, x, y, this);
		}
	}

}
