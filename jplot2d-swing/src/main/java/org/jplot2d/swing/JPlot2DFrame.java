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

import javax.swing.JFrame;
import javax.swing.WindowConstants;

import org.jplot2d.element.Plot;
import org.jplot2d.env.RenderEnvironment;

/**
 * A dedicated JFrame, which contains a JPlot2DComponent to display a plot.
 * 
 * @author Jingjing Li
 * 
 */
public class JPlot2DFrame extends JFrame {

	private static final long serialVersionUID = 1L;

	/**
	 * Construct a JFrame to display the given plot in its center. The plot properties can be safely modified by
	 * multiple threads.
	 * 
	 * @param plot
	 *            the plot to be display
	 */
	public JPlot2DFrame(Plot plot) {
		this(plot, true);
	}

	/**
	 * Construct a JFrame to display the given plot in its center.
	 * 
	 * @param plot
	 *            the plot to be display
	 * @param threadSafe
	 *            if <code>false</code>, all plot properties can only be changed within a single thread. if
	 *            <code>true</code>, all plot properties can be safely changed by multiple threads.
	 */
	public JPlot2DFrame(Plot plot, boolean threadSafe) {
		super();
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		getContentPane().add(new JPlot2DComponent(plot, threadSafe));
	}

	/**
	 * Construct a JFrame to display a plot in its center. The plot has been assigned to the given RenderEnvironment.
	 * 
	 * @param env
	 *            the RenderEnvironment
	 */
	public JPlot2DFrame(RenderEnvironment env) {
		super();
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		getContentPane().add(new JPlot2DComponent(env));
	}

	/**
	 * Returns the JPlot2DComponent in the content pane of this JPlot2DFrame.
	 * 
	 * @return the JPlot2DComponent
	 */
	public JPlot2DComponent getPlotComponent() {
		return (JPlot2DComponent) getContentPane().getComponent(0);
	}

	/**
	 * Return the RenderEnvironment of the JPlot2DComponent. The RenderEnvironment generate plot images and display them
	 * in the JPlot2DComponent.
	 * 
	 * @return the RenderEnvironment of the JPlot2DComponen
	 */
	public RenderEnvironment getRenderEnvironment() {
		return getPlotComponent().getRenderEnvironment();
	}

}
