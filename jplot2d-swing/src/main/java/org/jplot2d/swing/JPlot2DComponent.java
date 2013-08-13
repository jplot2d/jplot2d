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
package org.jplot2d.swing;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.HierarchyEvent;
import java.awt.event.HierarchyListener;
import java.awt.event.MouseWheelListener;
import java.awt.image.BufferedImage;
import java.util.logging.Logger;

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

/**
 * A JComponent that display a plot in its center.
 * 
 * @author Jingjing Li
 * 
 */
public class JPlot2DComponent extends JComponent implements HierarchyListener, RenderingFinishedListener {

	private static final long serialVersionUID = 1L;

	static Logger logger = Logger.getLogger("org.jplot2d.swing");

	private final RenderEnvironment env;

	private ImageRenderer r;

	private int ifsn;

	private volatile BufferedImage image;

	private int xoff, yoff;

	private InteractionManager imanager;

	private final InteractionListener ial;

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

		setBackground(Color.GRAY);
		setOpaque(true);
		addHierarchyListener(this);

		imanager = getInteractionManager();

		ial = new InteractionListener(this, imanager, env);
		setFocusable(true);
		requestFocusInWindow();
		addKeyListener(ial);
		addMouseListener(ial);
		addMouseMotionListener(ial);
		addMouseWheelListener((MouseWheelListener) ial);
	}

	/**
	 * Returns the background color of plot. The default color is Color.WHITE.
	 * 
	 * @return
	 */
	public Color getPlotBackground() {
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

	public void hierarchyChanged(HierarchyEvent event) {
		if ((event.getChangeFlags() & HierarchyEvent.DISPLAYABILITY_CHANGED) != 0) {
			if (this.isDisplayable()) {
				r = createImageRenderer();
				r.addRenderingFinishedListener(this);
				env.addRenderer(r);
			} else {
				env.removeRenderer(r);
			}
		}
	}

	public void renderingFinished(RenderingFinishedEvent event) {
		int sn = event.getSN();
		if (sn < ifsn) {
			logger.info("[R] Rendering finished in wrong order, drop R." + sn + " Current result is " + ifsn);
			return;
		}

		ifsn = sn;
		image = (BufferedImage) event.getResult();
		repaint();
	}

	protected ImageRenderer createImageRenderer() {
		return new AsyncImageRenderer(new GraphicsConfigurationCompatibleImageFactory(this.getGraphicsConfiguration(),
				getPlotBackground()));
	}

	/**
	 * Subclass can return it's own InteractionManager, initiated with a MousePreference
	 * 
	 * @return an InteractionManager
	 */
	protected InteractionManager getInteractionManager() {
		InteractionManager result = PlotInteractionManager.getInstance();
		result.setMousePreference(PlotDefaultMousePreference.getInstance());
		return result;
	}

	public void setMousePreference(MousePreference prefs) {
		imanager.setMousePreference(prefs);
	}
}
