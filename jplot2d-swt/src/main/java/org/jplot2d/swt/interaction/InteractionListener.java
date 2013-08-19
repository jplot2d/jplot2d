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
package org.jplot2d.swt.interaction;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.MouseTrackListener;
import org.eclipse.swt.events.MouseWheelListener;
import org.jplot2d.env.PlotEnvironment;
import org.jplot2d.interaction.GenericMouseEvent;
import org.jplot2d.interaction.InteractionHandler;
import org.jplot2d.interaction.InteractionManager;
import org.jplot2d.interaction.PlotInteractionManager;
import org.jplot2d.interaction.VisualFeedbackDrawer;
import org.jplot2d.swt.JPlot2DComposite;

/**
 * The interaction listener will be set as MouseListener, MouseMoveListener, MouseTrackListener, MouseWheelListener
 * 
 * @author Jingjing Li
 * 
 */
public class InteractionListener implements KeyListener, MouseListener, MouseMoveListener, MouseTrackListener,
		MouseWheelListener, VisualFeedbackDrawer {

	private final JPlot2DComposite comp;

	private final InteractionHandler ihandler;

	public InteractionListener(JPlot2DComposite comp, InteractionManager imanager, PlotEnvironment env) {
		this.comp = comp;
		ihandler = new InteractionHandler(imanager);
		ihandler.putValue(PlotInteractionManager.PLOT_ENV_KEY, env);
		ihandler.putValue(PlotInteractionManager.INTERACTIVE_COMP_KEY, new SwtInteractiveComp(comp, env));
		ihandler.init();
	}

	public void keyPressed(KeyEvent e) {
		int keyMask = getKeyMask(e);
		if (keyMask != 0) {
			ihandler.keyPressed(getModifiersKeyMask(e) | keyMask, keyMask);
		}
	}

	public void keyReleased(KeyEvent e) {
		int keyMask = getKeyMask(e);
		if (keyMask != 0) {
			ihandler.keyReleased(getModifiersKeyMask(e) & ~keyMask, keyMask);
		}
	}

	private int getModifiersKeyMask(KeyEvent e) {
		int modifiers = 0;
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
		return modifiers;
	}

	private int getKeyMask(KeyEvent e) {
		int keyMask = 0;
		switch (e.keyCode) {
		case SWT.SHIFT:
			keyMask = GenericMouseEvent.SHIFT_DOWN_MASK;
			break;
		case SWT.CTRL:
			keyMask = GenericMouseEvent.CTRL_DOWN_MASK;
			break;
		case SWT.COMMAND:
			keyMask = GenericMouseEvent.META_DOWN_MASK;
			break;
		case SWT.ALT:
			keyMask = GenericMouseEvent.ALT_DOWN_MASK;
			break;
		}
		return keyMask;
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
		if ((e.stateMask & SWT.BUTTON_MASK) != 0) {
			ihandler.mouseDragged(getGenericMouseEvent(GenericMouseEvent.MOUSE_DRAGGED, e));
		} else {
			ihandler.mouseMoved(getGenericMouseEvent(GenericMouseEvent.MOUSE_MOVED, e));
		}
	}

	public void mouseScrolled(MouseEvent e) {
		ihandler.mouseWheelMoved(getGenericMouseEvent(GenericMouseEvent.MOUSE_WHEEL, e));
	}

	public void draw(Object graphics) {
		ihandler.draw(graphics);
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
		int buttomMask = 0;
		switch (e.button) {
		case 1:
			button = GenericMouseEvent.BUTTON1;
			buttomMask = GenericMouseEvent.BUTTON1_DOWN_MASK;
			break;
		case 2:
			button = GenericMouseEvent.BUTTON2;
			buttomMask = GenericMouseEvent.BUTTON2_DOWN_MASK;
			break;
		case 3:
			button = GenericMouseEvent.BUTTON3;
			buttomMask = GenericMouseEvent.BUTTON3_DOWN_MASK;
			break;
		}

		if (eid == GenericMouseEvent.MOUSE_PRESSED) {
			modifiers |= buttomMask;
		} else if (eid == GenericMouseEvent.MOUSE_RELEASED) {
			modifiers &= ~buttomMask;
		}

		GenericMouseEvent gme = new GenericMouseEvent(eid, modifiers, e.x - comp.getImageOffsetX(), e.y
				- comp.getImageOffsetY(), e.count, button);
		// System.out.println(gme);
		return gme;
	}

}
