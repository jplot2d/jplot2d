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
package org.jplot2d.interaction;

import org.jplot2d.element.MovableComponent;
import org.jplot2d.element.PComponent;
import org.jplot2d.env.BatchToken;
import org.jplot2d.env.PlotEnvironment;
import org.jplot2d.notice.UINoticeType;
import org.jplot2d.transform.PaperTransform;

import java.awt.geom.Point2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class MouseMoveComponentHandler extends MouseDragBehaviorHandler<MouseMoveComponentBehavior> implements PropertyChangeListener {

    private final InteractiveComp icomp;
    private boolean movable;
    private MovableComponent pcomp;
    private int oldX, oldY;

    public MouseMoveComponentHandler(MouseMoveComponentBehavior behavior, InteractionModeHandler handler) {
        super(behavior, handler);
        icomp = (InteractiveComp) handler.getValue(PlotInteractionManager.INTERACTIVE_COMP_KEY);
    }

    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals(PlotInteractionManager.ACTIVE_COMPONENT_KEY)) {
            PComponent acomp = (PComponent) evt.getNewValue();
            if (acomp instanceof MovableComponent && ((MovableComponent) acomp).isMovable()) {
                movable = true;
                icomp.setCursor(InteractiveComp.CursorStyle.OPEN_HAND_CURSOR);
            } else {
                movable = false;
            }
        }
    }

    @Override
    public boolean canStartDragging(int x, int y) {
        return movable;
    }

    @Override
    public void draggingStarted(int x, int y) {
        pcomp = (MovableComponent) handler.getValue(PlotInteractionManager.ACTIVE_COMPONENT_KEY);
        oldX = x;
        oldY = y;
        icomp.setCursor(InteractiveComp.CursorStyle.CLOSE_HAND_CURSOR);
    }

    @Override
    public void draggingTo(int x, int y) {
        double xoff = x - oldX;
        double yoff = y - oldY;
        oldX = x;
        oldY = y;

        if (xoff == 0 && yoff == 0) {
            return;
        }

        /**
         * pan the given distance
         */
        PlotEnvironment env = (PlotEnvironment) handler.getValue(PlotInteractionManager.PLOT_ENV_KEY);
        BatchToken token = env.beginBatch("Move Component");

        PaperTransform pxf = pcomp.getParent().getPaperTransform();
        Point2D dloc = pxf.getPtoD(pcomp.getLocation());
        dloc.setLocation(dloc.getX() + xoff, dloc.getY() + yoff);
        Point2D newLoc = pxf.getDtoP(dloc);
        pcomp.setLocation(newLoc);

        env.endBatch(token, UINoticeType.getInstance());
    }

    @Override
    public void draggingFinished(int x, int y) {
        icomp.setCursor(InteractiveComp.CursorStyle.OPEN_HAND_CURSOR);
    }

    @Override
    public void draggingCancelled() {
        // nothing to do
    }

}