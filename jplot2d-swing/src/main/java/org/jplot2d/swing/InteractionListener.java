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


import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import org.jplot2d.env.PlotEnvironment;
import org.jplot2d.interaction.GenericMouseEvent;
import org.jplot2d.interaction.InteractionHandler;
import org.jplot2d.interaction.InteractionManager;
import org.jplot2d.interaction.PlotPaintEvent;
import org.jplot2d.interaction.PlotPaintListener;

/**
 * @author Jingjing Li
 * 
 */
public class InteractionListener implements MouseListener, MouseMotionListener, MouseWheelListener,
		PlotPaintListener {

	private final InteractionHandler ihandler;

	public InteractionListener(JPlot2DComponent comp, InteractionManager imanager,
			PlotEnvironment env) {
		ihandler = new InteractionHandler(imanager);
		ihandler.putValue(InteractionHandler.PLOT_KEY, env.getPlot());
		ihandler.putValue(InteractionHandler.PLOT_ENV_KEY, env);
		ihandler.putValue(InteractionHandler.COMPONENT_KEY, comp);
		ihandler.putValue(InteractionHandler.PLOT_BACKGROUND_KEY, comp.getPlotBackground());
		ihandler.init();
	}

	public void mouseClicked(MouseEvent e) {
		// ignored. The InteractionHandler will detect click by mouse down and up.
	}

	public void mousePressed(MouseEvent e) {
		System.out.println("[] " + e);
		ihandler.mousePressed(getGenericMouseEvent(e));
	}

	public void mouseReleased(MouseEvent e) {
		System.out.println("[] " + e);
		ihandler.mouseReleased(getGenericMouseEvent(e));
	}

	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	public void mouseMoved(MouseEvent e) {
		ihandler.mouseMoved(getGenericMouseEvent(e));
	}

	public void mouseDragged(MouseEvent e) {
		ihandler.mouseDragged(getGenericMouseEvent(e));
	}

	public void mouseWheelMoved(MouseWheelEvent e) {
		System.out.println("[] " + e);
		ihandler.mouseWheelMoved(new GenericMouseEvent(e.getID(), e.getModifiersEx(), e.getX(), e
				.getY(), e.getWheelRotation(), e.getButton()));
	}

	public void plotPainted(PlotPaintEvent evt) {
		ihandler.plotPainted(evt);
	}

	private GenericMouseEvent getGenericMouseEvent(MouseEvent e) {
		return new GenericMouseEvent(e.getID(), e.getModifiersEx(), e.getX(), e.getY(),
				e.getClickCount(), e.getButton());
	}
}
