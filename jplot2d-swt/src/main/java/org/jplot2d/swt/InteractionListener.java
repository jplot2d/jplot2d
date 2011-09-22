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
package org.jplot2d.swt;

import org.eclipse.swt.events.MenuDetectEvent;
import org.eclipse.swt.events.MenuDetectListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.MouseTrackListener;
import org.eclipse.swt.events.MouseWheelListener;
import org.jplot2d.env.PlotEnvironment;
import org.jplot2d.interaction.GenericMouseEvent;
import org.jplot2d.interaction.InteractionHandler;
import org.jplot2d.interaction.InteractionManager;
import org.jplot2d.interaction.PlotPaintEvent;
import org.jplot2d.interaction.PlotPaintListener;

/**
 * The interaction listener will be set as MenuDetectListener, MouseListener, MouseMoveListener,
 * MouseTrackListener, MouseWheelListener
 * 
 * @author Jingjing Li
 * 
 */
public class InteractionListener implements MenuDetectListener, MouseListener, MouseMoveListener,
		MouseTrackListener, MouseWheelListener, PlotPaintListener {

	private final InteractionHandler ihandler;

	public InteractionListener(JPlot2DComposite comp, InteractionManager imanager,
			PlotEnvironment env) {
		ihandler = new InteractionHandler(imanager);
		ihandler.putValue(InteractionHandler.PLOT_KEY, env.getPlot());
		ihandler.putValue(InteractionHandler.PLOT_ENV_KEY, env);
		ihandler.putValue(InteractionHandler.COMPONENT_KEY, comp);
		ihandler.putValue(InteractionHandler.PLOT_BACKGROUND_KEY, comp.getPlotBackground());
		ihandler.init();
	}

	public void mouseDoubleClick(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	public void mouseDown(MouseEvent e) {
		ihandler.mousePressed(getGenericMouseEvent(GenericMouseEvent.MOUSE_PRESSED, e));
	}

	public void mouseUp(MouseEvent e) {
		ihandler.mouseReleased(getGenericMouseEvent(GenericMouseEvent.MOUSE_RELEASED, e));
	}

	public void mouseEnter(MouseEvent e) {
		// do nothing
	}

	public void mouseExit(MouseEvent e) {
		// do nothing
	}

	public void mouseHover(MouseEvent e) {
		// do nothing
	}

	public void mouseMove(MouseEvent e) {
		ihandler.mouseMoved(getGenericMouseEvent(GenericMouseEvent.MOUSE_MOVED, e));
	}

	public void mouseDragged(MouseEvent e) {
		ihandler.mouseDragged(getGenericMouseEvent(GenericMouseEvent.MOUSE_DRAGGED, e));
	}

	public void mouseScrolled(MouseEvent e) {
		System.out.println("[] " + e);
		ihandler.mouseWheelMoved(getGenericMouseEvent(GenericMouseEvent.MOUSE_WHEEL, e));
	}

	public void menuDetected(MenuDetectEvent e) {
		ihandler.mouseWheelMoved(new GenericMouseEvent(GenericMouseEvent.MOUSE_MENU, 0, e.x, e.y,
				0, 0));
	}

	public void plotPainted(PlotPaintEvent evt) {
		ihandler.plotPainted(evt);
	}

	/**
	 * convert the awt mouse event to GenericMouseEvent
	 * 
	 * @param e
	 *            the awt mouse event
	 * @return GenericMouseEvent
	 */
	private GenericMouseEvent getGenericMouseEvent(int eid, MouseEvent e) {
		return new GenericMouseEvent(eid, e.stateMask, e.x, e.y, e.count, e.button);
	}

}
