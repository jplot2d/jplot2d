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
package org.jplot2d.swt.interaction;

import org.eclipse.swt.SWT;
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
import org.jplot2d.swt.JPlot2DComposite;

/**
 * The interaction listener will be set as MouseListener, MouseMoveListener, MouseTrackListener,
 * MouseWheelListener
 * 
 * @author Jingjing Li
 * 
 */
public class InteractionListener implements MouseListener, MouseMoveListener, MouseTrackListener,
		MouseWheelListener, PlotPaintListener {

	private final InteractionHandler ihandler;

	public InteractionListener(JPlot2DComposite comp, InteractionManager imanager,
			PlotEnvironment env) {
		ihandler = new InteractionHandler(imanager, new SwtInteractiveComp(comp, env));
		ihandler.putValue(InteractionHandler.PLOT_ENV_KEY, env);
		ihandler.init();
	}

	public void mouseDoubleClick(MouseEvent e) {
		// jplot2d doesn't handle double-click
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
		System.out.println("[] " + e);
		ihandler.mouseDragged(getGenericMouseEvent(GenericMouseEvent.MOUSE_DRAGGED, e));
	}

	public void mouseScrolled(MouseEvent e) {
		System.out.println("[] " + e);
		ihandler.mouseWheelMoved(getGenericMouseEvent(GenericMouseEvent.MOUSE_WHEEL, e));
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

		int modifiers = 0;
		if ((e.stateMask & SWT.BUTTON1) != 0) {
			modifiers |= GenericMouseEvent.BUTTON1_DOWN_MASK;
		}
		if ((e.stateMask & SWT.BUTTON2) != 0) {
			modifiers |= GenericMouseEvent.BUTTON2_DOWN_MASK;
		}
		if ((e.stateMask & SWT.BUTTON3) != 0) {
			modifiers |= GenericMouseEvent.BUTTON3_DOWN_MASK;
		}
		if ((e.stateMask & SWT.SHIFT) != 0) {
			modifiers |= GenericMouseEvent.SHIFT_DOWN_MASK;
		}
		if ((e.stateMask & SWT.CONTROL) != 0) {
			modifiers |= GenericMouseEvent.CTRL_DOWN_MASK;
		}
		if ((e.stateMask & SWT.COMMAND) != 0) {
			modifiers |= GenericMouseEvent.META_DOWN_MASK;
		}
		if ((e.stateMask & SWT.ALT) != 0) {
			modifiers |= GenericMouseEvent.ALT_DOWN_MASK;
		}

		int button = 0;
		switch (e.button) {
		case 1:
			button = GenericMouseEvent.BUTTON1;
			break;
		case 2:
			button = GenericMouseEvent.BUTTON2;
			break;
		case 3:
			button = GenericMouseEvent.BUTTON3;
			break;
		}

		return new GenericMouseEvent(eid, modifiers, e.x, e.y, e.count, button);
	}

}
