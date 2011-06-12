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
import java.awt.event.HierarchyEvent;
import java.awt.event.HierarchyListener;
import java.awt.image.BufferedImage;
import java.util.logging.Logger;

import javax.swing.JComponent;

import org.jplot2d.element.Plot;
import org.jplot2d.env.RenderEnvironment;
import org.jplot2d.renderer.AsyncImageRenderer;
import org.jplot2d.renderer.GraphicsConfigurationCompatibleImageFactory;
import org.jplot2d.renderer.ImageRenderer;
import org.jplot2d.renderer.RenderingFinishedEvent;
import org.jplot2d.renderer.RenderingFinishedListener;

/**
 * A JComponent that display a plot in its center.
 * 
 * @author Jingjing Li
 * 
 */
public class JPlot2DComponent extends JComponent implements HierarchyListener {

	private static final long serialVersionUID = 1L;

	static Logger logger = Logger.getLogger("org.jplot2d.swing");

	private final RenderEnvironment env;

	private ImageRenderer r;

	private volatile BufferedImage image;

	/**
	 * Construct a JComponent to display the given plot in its center. The plot
	 * properties can be safely by multiple threads.
	 * 
	 * @param plot
	 *            the plot to be display
	 */
	public JPlot2DComponent(Plot plot) {
		this(new RenderEnvironment(plot, true));
	}

	/**
	 * Construct a JComponent to display the given plot in its center.
	 * 
	 * @param plot
	 *            the plot to be display
	 * @param threadSafe
	 *            if <code>false</code>, all plot properties can only be changed
	 *            within a single thread. if <code>true</code>, all plot
	 *            properties can be safely changed by multiple threads.
	 */
	public JPlot2DComponent(Plot plot, boolean threadSafe) {
		this(new RenderEnvironment(plot, threadSafe));
	}

	/**
	 * Construct a JComponent to display a plot in its center. The plot has been
	 * assigned to the given RenderEnvironment.
	 * 
	 * @param env
	 *            the RenderEnvironment
	 */
	public JPlot2DComponent(RenderEnvironment env) {
		this.env = env;

		setBackground(Color.GRAY);
		setOpaque(true);
		addHierarchyListener(this);
	}

	/**
	 * @return
	 */
	protected Color getPlotBackground() {
		return Color.WHITE;
	}

	public void setBounds(int x, int y, int width, int height) {
		super.setBounds(x, y, width, height);
		env.getPlot().setContainerSize(new Dimension(width, height));
	}

	public void paintComponent(Graphics g) {
		// clear background
		g.clearRect(0, 0, getWidth(), getHeight());

		if (image != null) {
			int width = image.getWidth();
			int height = image.getHeight();
			int x = (getWidth() - width) / 2;
			int y = (getHeight() - height) / 2;
			g.drawImage(image, x, y, this);
		}
	}

	public void hierarchyChanged(HierarchyEvent event) {
		if ((event.getChangeFlags() & HierarchyEvent.DISPLAYABILITY_CHANGED) != 0) {
			if (this.isDisplayable()) {
				r = createImageRenderer();

				r.addRenderingFinishedListener(new RenderingFinishedListener() {
					private long ifsn;

					public void renderingFinished(RenderingFinishedEvent event) {
						long fsn = event.getFsn();
						if (fsn < ifsn) {
							logger.info("[R] Rendering finished in wrong order, drop F."
									+ fsn + " Current frame is " + ifsn);
							return;
						}
						ifsn = fsn;

						image = (BufferedImage) event.getResult();
						repaint();
					}

				});
				env.addRenderer(r);
			} else {
				env.removeRenderer(r);
			}
		}
	}

	protected ImageRenderer createImageRenderer() {
		return new AsyncImageRenderer(
				new GraphicsConfigurationCompatibleImageFactory(
						this.getGraphicsConfiguration(), getPlotBackground()));
	}

}
