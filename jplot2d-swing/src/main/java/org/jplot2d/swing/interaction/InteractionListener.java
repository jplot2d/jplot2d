/*
 * Copyright 2010-2015 Jingjing Li.
 *
 * This file is part of jplot2d.
 *
 * jplot2d is free software:
 * you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation, either version 3 of the License, or any later version.
 *
 * jplot2d is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Lesser Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with jplot2d.
 * If not, see <http://www.gnu.org/licenses/>.
 */
package org.jplot2d.swing.interaction;

import org.jplot2d.env.RenderEnvironment;
import org.jplot2d.interaction.*;
import org.jplot2d.swing.JPlot2DComponent;

import java.awt.event.*;

/**
 * The interaction listener will be set as MouseListener, MouseMotionListener and MouseWheelListener
 *
 * @author Jingjing Li
 */
public class InteractionListener implements KeyListener, MouseListener, MouseMotionListener, MouseWheelListener,
        VisualFeedbackDrawer {

    private final JPlot2DComponent comp;

    private final PopupMenu popup;

    private final InteractionHandler ihandler;

    public InteractionListener(JPlot2DComponent comp, InteractionManager imanager, RenderEnvironment env) {
        this.comp = comp;
        popup = new PopupMenu(env);
        ihandler = new InteractionHandler(imanager);
        ihandler.putValue(PlotInteractionManager.PLOT_ENV_KEY, env);
        ihandler.putValue(PlotInteractionManager.INTERACTIVE_COMP_KEY, new SwingInteractiveComp(comp));
        ihandler.init();
    }

    public void keyTyped(KeyEvent e) {
        // ignored. The InteractionHandler will detect modifiers key.
    }

    public void keyPressed(KeyEvent e) {
        int keyMask = getKeyMask(e.getKeyCode());
        if (keyMask != 0) {
            ihandler.modifierKeyPressed(e.getModifiersEx(), keyMask);
        } else if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
            ihandler.escape();

        }
    }

    public void keyReleased(KeyEvent e) {
        int keyMask = getKeyMask(e.getKeyCode());
        if (keyMask != 0) {
            ihandler.modifierKeyReleased(e.getModifiersEx(), keyMask);
        }
    }

    private int getKeyMask(int keyCode) {
        switch (keyCode) {
            case KeyEvent.VK_SHIFT:
                return GenericMouseEvent.SHIFT_DOWN_MASK;
            case KeyEvent.VK_CONTROL:
                return GenericMouseEvent.CTRL_DOWN_MASK;
            case KeyEvent.VK_META:
                return GenericMouseEvent.META_DOWN_MASK;
            case KeyEvent.VK_ALT:
                return GenericMouseEvent.ALT_DOWN_MASK;
        }
        return 0;
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
        ihandler.mouseWheelMoved(getGenericMouseEvent(e));
    }

    public void draw(Object graphics) {
        ihandler.draw(graphics);
    }

    /**
     * convert the awt mouse event to GenericMouseEvent
     *
     * @param e the awt mouse event
     * @return GenericMouseEvent
     */
    private GenericMouseEvent getGenericMouseEvent(MouseEvent e) {
        int count;
        if (e instanceof MouseWheelEvent) {
            count = ((MouseWheelEvent) e).getWheelRotation();
        } else {
            count = e.getClickCount();
        }
        return new GenericMouseEvent(e.getID(), e.getModifiersEx(), e.getX() - comp.getImageOffsetX(),
                e.getY() - comp.getImageOffsetY(), count, e.getButton());
    }

    private void popupMenu(int x, int y) {
        popup.updateStatus(x, y);
        popup.show(comp, x, y);
    }

}
