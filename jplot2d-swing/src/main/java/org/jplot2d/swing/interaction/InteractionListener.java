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
package org.jplot2d.swing.interaction;

import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import org.jplot2d.env.RenderEnvironment;
import org.jplot2d.interaction.GenericMouseEvent;
import org.jplot2d.interaction.InteractionHandler;
import org.jplot2d.interaction.InteractionManager;
import org.jplot2d.interaction.VisualFeedbackDrawer;
import org.jplot2d.swing.JPlot2DComponent;

/**
 * The interaction listener will be set as MouseListener, MouseMotionListener and MouseWheelListener
 * 
 * @author Jingjing Li
 * 
 */
public class InteractionListener implements MouseListener, MouseMotionListener, MouseWheelListener,
		VisualFeedbackDrawer {

	private final JPlot2DComponent comp;

	private final PopupMenu popup;

	private final InteractionHandler ihandler;

	public InteractionListener(JPlot2DComponent comp, InteractionManager imanager,
			RenderEnvironment env) {
		this.comp = comp;
		popup = new PopupMenu(env);
		ihandler = new InteractionHandler(imanager, new SwingInteractiveComp(comp, env));
		ihandler.putValue(InteractionHandler.PLOT_ENV_KEY, env);
		ihandler.init();
	}

	public void mouseClicked(MouseEvent e) {
		// ignored. The InteractionHandler will detect click by mouse down and up.
	}

	public void mousePressed(MouseEvent e) {
		if (e.isPopupTrigger()) {
			popupMenu(e.getX(), e.getY());
		} else {
			ihandler.mousePressed(getGenericMouseEvent(e));
		}
	}

	public void mouseReleased(MouseEvent e) {
		if (e.isPopupTrigger()) {
			popupMenu(e.getX(), e.getY());
		} else {
			ihandler.mouseReleased(getGenericMouseEvent(e));
		}
	}

	public void mouseEntered(MouseEvent e) {
		// do nothing
	}

	public void mouseExited(MouseEvent e) {
		// do nothing
	}

	public void mouseMoved(MouseEvent e) {
		ihandler.mouseMoved(getGenericMouseEvent(e));
	}

	public void mouseDragged(MouseEvent e) {
		ihandler.mouseDragged(getGenericMouseEvent(e));
	}

	public void mouseWheelMoved(MouseWheelEvent e) {
		ihandler.mouseWheelMoved(new GenericMouseEvent(e.getID(), e.getModifiersEx(), e.getX(), e
				.getY(), e.getWheelRotation(), e.getButton()));
	}

	public void draw(Graphics2D graphics) {
		ihandler.draw(graphics);
	}

	/**
	 * convert the awt mouse event to GenericMouseEvent
	 * 
	 * @param e
	 *            the awt mouse event
	 * @return GenericMouseEvent
	 */
	private GenericMouseEvent getGenericMouseEvent(MouseEvent e) {
		GenericMouseEvent gme = new GenericMouseEvent(e.getID(), e.getModifiersEx(), e.getX(),
				e.getY(), e.getClickCount(), e.getButton());
		// System.out.println(gme);
		return gme;
	}

	private void popupMenu(int x, int y) {
		popup.updateStatus(x, y);
		popup.show(comp, x, y);
	}

}
