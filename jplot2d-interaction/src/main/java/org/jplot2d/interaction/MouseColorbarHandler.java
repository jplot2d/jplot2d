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

import org.jplot2d.element.Colorbar;
import org.jplot2d.element.PComponent;
import org.jplot2d.env.BatchToken;
import org.jplot2d.env.PlotEnvironment;
import org.jplot2d.notice.UINoticeType;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * Display Gain/Bias slider.
 */
public class MouseColorbarHandler extends MouseDragBehaviorHandler<MouseColorbarBehavior> implements PropertyChangeListener, VisualFeedbackDrawer {

    private final InteractiveComp icomp;
    private Colorbar colorbar;
    private boolean adjustable;
    private float gainDispLoc, biasDispLoc;

    public MouseColorbarHandler(MouseColorbarBehavior behavior, InteractionModeHandler handler) {
        super(behavior, handler);
        icomp = (InteractiveComp) handler.getValue(PlotInteractionManager.INTERACTIVE_COMP_KEY);
    }

    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals(PlotInteractionManager.ACTIVE_COMPONENT_KEY)) {
            PComponent acomp = (PComponent) evt.getNewValue();
            if (acomp instanceof Colorbar && ((Colorbar) acomp).getImageMapping() != null) {
                adjustable = true;
                colorbar = (Colorbar) acomp;
                double gain = colorbar.getImageMapping().getGain();
                double bias = colorbar.getImageMapping().getBias();
                gainDispLoc = (float) colorbar.getPaperTransform().getYPtoD(gain * colorbar.getLength());
                biasDispLoc = (float) colorbar.getPaperTransform().getYPtoD(bias * colorbar.getLength());
                icomp.setCursor(InteractiveComp.CursorStyle.OPEN_HAND_CURSOR);
            } else {
                adjustable = false;
                colorbar = null;
                icomp.setCursor(InteractiveComp.CursorStyle.DEFAULT_CURSOR);
            }
        }
    }

    @Override
    public boolean canStartDragging(int x, int y) {
        return adjustable;
    }

    @Override
    public void draggingStarted(int x, int y) {
        switch (colorbar.getPosition()) {
            case LEFT:
            case RIGHT:
                gainDispLoc = y;
                break;
            case TOP:
            case BOTTOM:
                gainDispLoc = x;
                break;
        }
        icomp.setCursor(InteractiveComp.CursorStyle.CLOSE_HAND_CURSOR);
    }

    @Override
    public void draggingTo(int x, int y) {
        switch (colorbar.getPosition()) {
            case LEFT:
            case RIGHT:
                gainDispLoc = y;
                break;
            case TOP:
            case BOTTOM:
                gainDispLoc = x;
                break;
            default:
                return;
        }
        icomp.repaint();
    }

    @Override
    public void draggingFinished(int x, int y) {
        switch (colorbar.getPosition()) {
            case LEFT:
            case RIGHT:
                gainDispLoc = y;
                break;
            case TOP:
            case BOTTOM:
                gainDispLoc = x;
                break;
            default:
                return;
        }
        icomp.repaint();

        /**
         * pan the given distance
         */
        PlotEnvironment env = (PlotEnvironment) handler.getValue(PlotInteractionManager.PLOT_ENV_KEY);
        BatchToken token = env.beginBatch("Colorbar Adjust");

        double gain = colorbar.getPaperTransform().getYDtoP(gainDispLoc) / (colorbar.getLength());
        if (gain < 0.01) {
            gain = 0.01;
        } else if (gain > 0.99) {
            gain = 0.99;
        }
        colorbar.getImageMapping().setGain(gain);
        gainDispLoc = (float) colorbar.getPaperTransform().getYPtoD(gain * colorbar.getLength());

        env.endBatch(token, UINoticeType.getInstance());

        icomp.setCursor(InteractiveComp.CursorStyle.OPEN_HAND_CURSOR);
    }

    @Override
    public void draggingCancelled() {
        icomp.setCursor(InteractiveComp.CursorStyle.OPEN_HAND_CURSOR);
    }

    public void draw(Object g) {

        if (!adjustable) {
            return;
        }

        switch (colorbar.getPosition()) {
            case LEFT:
            case RIGHT:
                double x0 = colorbar.getPaperTransform().getXPtoD(0);
                icomp.drawShape(g, Color.GRAY.getRGB(), new Ellipse2D.Float((float) x0 - 4, gainDispLoc - 4, 8, 8));
                break;
            case TOP:
            case BOTTOM:
                double y0 = colorbar.getPaperTransform().getYPtoD(0);
                icomp.drawShape(g, Color.GRAY.getRGB(), new Ellipse2D.Float(gainDispLoc - 4, (float) y0 - 4, 8, 8));
                break;
            default:
                return;
        }

    }
}