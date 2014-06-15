/**
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
package org.jplot2d.swing;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.HierarchyEvent;
import java.awt.event.HierarchyListener;
import java.awt.event.MouseWheelListener;
import java.awt.image.BufferedImage;

import javax.swing.JComponent;

import org.jplot2d.element.Plot;
import org.jplot2d.env.RenderEnvironment;
import org.jplot2d.interaction.InteractionManager;
import org.jplot2d.interaction.MousePreference;
import org.jplot2d.interaction.PlotDefaultMousePreference;
import org.jplot2d.interaction.PlotInteractionManager;
import org.jplot2d.notice.DefaultNotifier;
import org.jplot2d.renderer.AsyncImageRenderer;
import org.jplot2d.renderer.GraphicsConfigurationCompatibleImageFactory;
import org.jplot2d.renderer.ImageRenderer;
import org.jplot2d.renderer.RenderingFinishedEvent;
import org.jplot2d.renderer.RenderingFinishedListener;
import org.jplot2d.swing.interaction.InteractionListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A JComponent that display a plot in its center.
 * 
 * @author Jingjing Li
 * 
 */
public class JPlot2DComponent extends JComponent implements HierarchyListener, RenderingFinishedListener {

	public static class DefaultImageRendererFactory implements ImageRendererFactory {

		private final JPlot2DComponent comp;

		public DefaultImageRendererFactory(JPlot2DComponent comp) {
			this.comp = comp;
		}

		@Override
		public AsyncImageRenderer createImageRenderer() {
			return new AsyncImageRenderer(new GraphicsConfigurationCompatibleImageFactory(
					comp.getGraphicsConfiguration(), comp.getPlotBackground()));
		}

	}

	private static final long serialVersionUID = 1L;

	static Logger logger = LoggerFactory.getLogger("org.jplot2d.swing");

	private final RenderEnvironment env;

	private Color bgColor = Color.WHITE;

	private ImageRendererFactory rendererFactory;

	private ImageRenderer r;

	private Object renderLock = new Object();

	private long ifsn = -1;

	private Object fsnLock = new Object();

	private volatile BufferedImage image;

	private int xoff, yoff;

	private InteractionManager imanager;

	private final InteractionListener ial;

	private boolean iaEnabled;

	/**
	 * Construct a JComponent to display the given plot in its center. The plot properties can be safely by multiple
	 * threads.
	 * 
	 * @param plot
	 *            the plot to be display
	 */
	public JPlot2DComponent(Plot plot) {
		this(plot, true);
	}

	/**
	 * Construct a JComponent to display the given plot in its center.
	 * 
	 * @param plot
	 *            the plot to be display
	 * @param threadSafe
	 *            if <code>false</code>, all plot properties can only be changed within a single thread. if
	 *            <code>true</code>, all plot properties can be safely changed by multiple threads.
	 */
	public JPlot2DComponent(Plot plot, boolean threadSafe) {
		this(createRenderEnvironment(threadSafe));
		env.setPlot(plot, new DefaultNotifier(this));
	}

	/**
	 * create a RenderEnvironment with the given plot.
	 * 
	 * @param plot
	 * @param threadSafe
	 * @return
	 */
	private static RenderEnvironment createRenderEnvironment(boolean threadSafe) {
		RenderEnvironment env = new RenderEnvironment(threadSafe);
		return env;
	}

	/**
	 * Construct a JComponent to display a plot in its center. The plot has been assigned to the given
	 * RenderEnvironment.
	 * 
	 * @param env
	 *            the RenderEnvironment
	 */
	public JPlot2DComponent(RenderEnvironment env) {
		this.env = env;

		rendererFactory = new DefaultImageRendererFactory(this);

		setBackground(Color.GRAY);
		setOpaque(true);
		addHierarchyListener(this);

		imanager = createInteractionManager();

		ial = new InteractionListener(this, imanager, env);
		setFocusable(true);
		requestFocusInWindow();
		setInteractionEnabled(true);
	}

	/**
	 * Return the RenderEnvironment of this JPlot2DComponent. The RenderEnvironment generate plot images and display
	 * them in this component.
	 * 
	 * @return the RenderEnvironment of this JPlot2DComponen
	 */
	public RenderEnvironment getRenderEnvironment() {
		return env;
	}

	/**
	 * Returns the background color of plot. The default color is Color.WHITE.
	 * 
	 * @return the background color of plot
	 */
	public Color getPlotBackground() {
		return bgColor;
	}

	/**
	 * Sets the background color of plot. The default color is Color.WHITE.
	 * 
	 * @param bgcolor
	 *            the new background color of plot
	 */
	public void setPlotBackground(Color bgColor) {
		this.bgColor = bgColor;
		replaceRenderer();
	}

	/**
	 * Returns the renderer factory used to create image renderers.
	 * 
	 * @return the current renderer factory
	 */
	public ImageRendererFactory getImageRendererFactory() {
		return rendererFactory;
	}

	/**
	 * Sets the renderer factory used to create image renderers.
	 * 
	 * @param rendererFactory
	 *            the new renderer factory
	 */
	public void setImageRendererFactory(ImageRendererFactory rendererFactory) {
		this.rendererFactory = rendererFactory;
		replaceRenderer();
	}

	/**
	 * Recreate a new renderer and re-render the plot. Only when a renderer already exist.
	 */
	private void replaceRenderer() {
		synchronized (renderLock) {
			if (r != null) {
				env.removeRenderer(r);
				r = createAndSetRenderer();
			}
		}
	}

	/**
	 * Create a new image renderer and add it to render environment.
	 * 
	 * @return
	 */
	private ImageRenderer createAndSetRenderer() {
		synchronized (fsnLock) {
			ifsn = -1;
		}
		ImageRenderer ir = rendererFactory.createImageRenderer();
		ir.addRenderingFinishedListener(this);
		env.addRenderer(ir);
		env.exportPlot(ir);
		return ir;
	}

	public void hierarchyChanged(HierarchyEvent event) {
		if ((event.getChangeFlags() & HierarchyEvent.DISPLAYABILITY_CHANGED) != 0) {
			synchronized (renderLock) {
				if (this.isDisplayable()) {
					r = createAndSetRenderer();
				} else {
					env.removeRenderer(r);
					r = null;
				}
			}
		}
	}

	public void setBounds(int x, int y, int width, int height) {
		super.setBounds(x, y, width, height);
		env.getPlot().setContainerSize(new Dimension(width, height));
	}

	public void paintComponent(Graphics g) {
		// clear background
		((Graphics2D) g).setBackground(this.getBackground());
		g.clearRect(0, 0, getWidth(), getHeight());

		if (image != null) {
			int width = image.getWidth();
			int height = image.getHeight();
			xoff = (getWidth() - width) / 2;
			yoff = (getHeight() - height) / 2;
			g.drawImage(image, xoff, yoff, this);

			ial.draw((Graphics2D) g);
		}
	}

	/**
	 * Returns the current image displayed in this component.
	 * 
	 * @return the current image
	 */
	public BufferedImage getImage() {
		return image;
	}

	/**
	 * Returns the x offset where the plot image draw.
	 * 
	 * @return the x offset of the plot image
	 */
	public int getImageOffsetX() {
		return xoff;
	}

	/**
	 * Returns the y offset where the plot image draw.
	 * 
	 * @return the y offset of the plot image
	 */
	public int getImageOffsetY() {
		return yoff;
	}

	public void renderingFinished(RenderingFinishedEvent event) {
		long sn = event.getSN();
		boolean ok;
		synchronized (fsnLock) {
			if (sn > ifsn) {
				ok = true;
				ifsn = sn;
			} else {
				ok = false;
			}
		}

		if (!ok) {
			logger.trace("[R] Rendering finished in wrong order, drop R." + sn + " Current result is " + ifsn);
			return;
		}

		image = (BufferedImage) event.getResult();
		repaint();
	}

	/**
	 * Subclass can return it's own InteractionManager, initiated with a MousePreference
	 * 
	 * @return an InteractionManager
	 */
	protected InteractionManager createInteractionManager() {
		InteractionManager result = PlotInteractionManager.getInstance();
		result.setMousePreference(PlotDefaultMousePreference.getInstance());
		return result;
	}

	public void setMousePreference(MousePreference prefs) {
		imanager.setMousePreference(prefs);
	}

	public void setInteractionEnabled(boolean enabled) {
		if (iaEnabled == enabled) {
			return;
		}

		if (enabled) {
			addKeyListener(ial);
			addMouseListener(ial);
			addMouseMotionListener(ial);
			addMouseWheelListener((MouseWheelListener) ial);
		} else {
			removeKeyListener(ial);
			removeMouseListener(ial);
			removeMouseMotionListener(ial);
			removeMouseWheelListener((MouseWheelListener) ial);
		}
		iaEnabled = enabled;
	}

}
