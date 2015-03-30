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
package org.jplot2d.interaction;

import java.awt.event.MouseEvent;

public abstract class MouseDragBehaviorHandler<T extends MouseDragBehavior> extends
        MouseBehaviorHandler<T> {

    private boolean indrag;

    public MouseDragBehaviorHandler(T behavior, InteractionModeHandler handler) {
        super(behavior, handler);
    }

    @Override
    public final boolean processMouseEvent(GenericMouseEvent e) {
        if (e.getType() == MouseEvent.MOUSE_PRESSED && canStartDragging(e.getX(), e.getY())) {
            indrag = true;
            draggingStarted(e.getX(), e.getY());
            return true;
        }
        if (e.getType() == MouseEvent.MOUSE_DRAGGED) {
            if (indrag) {
                draggingTo(e.getX(), e.getY());
                return true;
            }
        }
        if (e.getType() == MouseEvent.MOUSE_RELEASED) {
            if (indrag) {
                indrag = false;
                draggingFinished(e.getX(), e.getY());
                return true;
            }
        }
        return false;
    }

    public abstract boolean canStartDragging(int x, int y);

    public abstract void draggingStarted(int x, int y);

    public abstract void draggingTo(int x, int y);

    public abstract void draggingFinished(int x, int y);

    public abstract void draggingCancelled();

}