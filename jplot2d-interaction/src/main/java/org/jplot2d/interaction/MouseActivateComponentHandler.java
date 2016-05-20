/*
 * Copyright 2010-2014 Jingjing Li.
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

import org.jplot2d.element.PComponent;
import org.jplot2d.env.PlotEnvironment;
import org.jplot2d.transform.PaperTransform;

import java.awt.Color;
import java.awt.Point;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;

/**
 * This handler activate / deactivate component while mouse moving. It set the interaction handler's mode according to
 * the component which mouse moving on.
 *
 * @author Jingjing Li
 */
public class MouseActivateComponentHandler extends MouseMoveBehaviorHandler<MouseActivateComponentBehavior> implements
        VisualFeedbackDrawer {

    private final InteractiveComp icomp;

    /**
     * The selectable component that mouse over
     */
    private PComponent activeComponent;

    public MouseActivateComponentHandler(MouseActivateComponentBehavior behavior, InteractionModeHandler handler) {
        super(behavior, handler);
        icomp = (InteractiveComp) handler.getValue(PlotInteractionManager.INTERACTIVE_COMP_KEY);
    }

    @Override
    public boolean behaviorPerformed(int x, int y) {

        PlotEnvironment penv = (PlotEnvironment) handler.getValue(PlotInteractionManager.PLOT_ENV_KEY);

        // quick check
        if (activeComponent != null && activeComponent.getEnvironment() == penv) {
            Shape activeBounds = getDeviceBounds(activeComponent);
            if (activeBounds != null && activeBounds.contains(x, y)) {
                return false;
            }
        }

        // the selectable component that contains the specified point.
        PComponent newComp = penv.getSelectableComponentAt(new Point(x, y));

        if (activeComponent == newComp) {
            return false;
        }

        if (newComp != null) {
            activeComponent = newComp;
            // activate((Graphics2D) ccomp.getGraphics());
            handler.putValue(PlotInteractionManager.ACTIVE_COMPONENT_KEY, activeComponent);
        } else {
            activeComponent = null;
            icomp.setCursor(InteractiveComp.CursorStyle.DEFAULT_CURSOR);
            handler.putValue(PlotInteractionManager.ACTIVE_COMPONENT_KEY, null);
        }

        icomp.repaint();

        return false;
    }

    public void draw(Object g) {
        /*
		 * Draw bounding box for the active component
		 */
        if (activeComponent != null) {
            Shape activeBounds = getDeviceBounds(activeComponent);
            if (activeBounds != null) {
                icomp.drawShape(g, Color.BLUE.getRGB(), activeBounds);
            }
        }
    }

    private static Shape getDeviceBounds(PComponent comp) {
        PaperTransform pxf = comp.getPaperTransform();
        Rectangle2D sb = comp.getSelectableBounds();
        if (pxf == null || sb == null) {
            return null;
        } else {
            return comp.getPaperTransform().getPtoD(comp.getSelectableBounds());
        }
    }

}