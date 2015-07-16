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
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * Display Gain/Bias slider.
 */
public class MouseColorbarHandler extends MouseDragBehaviorHandler<MouseColorbarBehavior> implements PropertyChangeListener, VisualFeedbackDrawer {

    private final InteractiveComp icomp;
    private Colorbar colorbar;
    private boolean adjustGain, adjustBias;
    private int gainX, gainY, biasX, biasY;
    private double gain, bias;

    public MouseColorbarHandler(MouseColorbarBehavior behavior, InteractionModeHandler handler) {
        super(behavior, handler);
        icomp = (InteractiveComp) handler.getValue(PlotInteractionManager.INTERACTIVE_COMP_KEY);
    }

    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals(PlotInteractionManager.ACTIVE_COMPONENT_KEY)) {
            PComponent acomp = (PComponent) evt.getNewValue();
            if (acomp instanceof Colorbar && ((Colorbar) acomp).getImageMapping() != null) {
                colorbar = (Colorbar) acomp;
                init();
            } else {
                colorbar = null;
            }
        }
    }

    private void init() {
        gain = colorbar.getImageMapping().getGain();
        bias = colorbar.getImageMapping().getBias();
        switch (colorbar.getPosition()) {
            case LEFT:
            case RIGHT:
                gainX = (int) colorbar.getPaperTransform().getXPtoD(0);
                gainY = (int) colorbar.getPaperTransform().getYPtoD(gain * colorbar.getLength());
                biasX = (int) colorbar.getPaperTransform().getXPtoD(colorbar.getBarWidth());
                biasY = (int) colorbar.getPaperTransform().getYPtoD(bias * colorbar.getLength());
                break;
            case TOP:
            case BOTTOM:
                gainX = (int) colorbar.getPaperTransform().getXPtoD(gain * colorbar.getLength());
                gainY = (int) colorbar.getPaperTransform().getYPtoD(colorbar.getBarWidth());
                biasX = (int) colorbar.getPaperTransform().getXPtoD(bias * colorbar.getLength());
                biasY = (int) colorbar.getPaperTransform().getYPtoD(0);
                break;
        }
    }

    @Override
    public boolean moveTo(int x, int y) {
        if (colorbar == null) {
            return false;
        }

        adjustGain = (x - gainX) * (x - gainX) + (y - gainY) * (y - gainY) <= 50;
        adjustBias = (x - biasX) * (x - biasX) + (y - biasY) * (y - biasY) <= 50;
        if (adjustGain || adjustBias) {
            icomp.setCursor(InteractiveComp.CursorStyle.OPEN_HAND_CURSOR);
        } else {
            icomp.setCursor(InteractiveComp.CursorStyle.DEFAULT_CURSOR);
        }
        // consume the move event
        return true;
    }

    @Override
    public boolean canStartDragging(int x, int y) {
        return colorbar != null;
    }

    @Override
    public void draggingStarted(int x, int y) {
        if (adjustGain || adjustBias) {
            icomp.setCursor(InteractiveComp.CursorStyle.CLOSE_HAND_CURSOR);
            icomp.repaint();
        }
    }

    @Override
    public void draggingTo(int x, int y) {
        if (adjustGain) {
            adjustGain(x, y);
            icomp.repaint();
        }
        if (adjustBias) {
            adjustBias(x, y);
            icomp.repaint();
        }
    }

    private void adjustGain(int x, int y) {
        switch (colorbar.getPosition()) {
            case LEFT:
            case RIGHT:
                gainY = y;
                gain = colorbar.getPaperTransform().getYDtoP(gainY) / (colorbar.getLength());
                break;
            case TOP:
            case BOTTOM:
                gainX = x;
                gain = colorbar.getPaperTransform().getXDtoP(gainX) / (colorbar.getLength());
                break;
            default:
                gain = 0.5;
        }
        boolean adj = false;
        if (gain < 0.01) {
            gain = 0.01;
            adj = true;
        } else if (gain > 0.99) {
            gain = 0.99;
            adj = true;
        }
        if (adj) {
            switch (colorbar.getPosition()) {
                case LEFT:
                case RIGHT:
                    gainY = (int) colorbar.getPaperTransform().getYPtoD(gain * colorbar.getLength());
                    break;
                case TOP:
                case BOTTOM:
                    gainX = (int) colorbar.getPaperTransform().getXPtoD(gain * colorbar.getLength());
                    break;
            }
        }

    }

    private void adjustBias(int x, int y) {
        switch (colorbar.getPosition()) {
            case LEFT:
            case RIGHT:
                biasY = y;
                bias = colorbar.getPaperTransform().getYDtoP(biasY) / (colorbar.getLength());
                break;
            case TOP:
            case BOTTOM:
                biasX = x;
                bias = colorbar.getPaperTransform().getXDtoP(biasX) / (colorbar.getLength());
                break;
            default:
                bias = 0.5;
        }
        boolean adj = false;
        if (bias < 0.01) {
            bias = 0.01;
            adj = true;
        } else if (bias > 0.99) {
            bias = 0.99;
            adj = true;
        }
        if (adj) {
            switch (colorbar.getPosition()) {
                case LEFT:
                case RIGHT:
                    biasY = (int) colorbar.getPaperTransform().getYPtoD(bias * colorbar.getLength());
                    break;
                case TOP:
                case BOTTOM:
                    biasX = (int) colorbar.getPaperTransform().getXPtoD(bias * colorbar.getLength());
                    break;
            }
        }
    }

    @Override
    public void draggingFinished(int x, int y) {
        if (adjustGain) {
            PlotEnvironment env = (PlotEnvironment) handler.getValue(PlotInteractionManager.PLOT_ENV_KEY);
            BatchToken token = env.beginBatch("Adjust Gain");
            colorbar.getImageMapping().setGain(gain);
            env.endBatch(token, UINoticeType.getInstance());

        }
        if (adjustBias) {
            PlotEnvironment env = (PlotEnvironment) handler.getValue(PlotInteractionManager.PLOT_ENV_KEY);
            BatchToken token = env.beginBatch("Adjust Bias");
            colorbar.getImageMapping().setBias(bias);
            env.endBatch(token, UINoticeType.getInstance());
        }
        if (adjustGain || adjustBias) {
            icomp.setCursor(InteractiveComp.CursorStyle.OPEN_HAND_CURSOR);
        }
    }

    @Override
    public void draggingCancelled() {
        if (adjustGain || adjustBias) {
            init();
            icomp.repaint();
            icomp.setCursor(InteractiveComp.CursorStyle.OPEN_HAND_CURSOR);
        }
    }

    public void draw(Object g) {
        if (colorbar == null) {
            return;
        }

        switch (colorbar.getPosition()) {
            case LEFT:
            case RIGHT:
                drawSliderV(g, gainX, gainY);
                drawSliderV(g, biasX, biasY);
                break;
            case TOP:
            case BOTTOM:
                drawSliderH(g, gainX, gainY);
                drawSliderH(g, biasX, biasY);
                break;
        }

        if (adjustGain) {
            icomp.drawTooltip(g, "gain\n" + String.format("%.2f", gain), gainX, gainY);
        }
        if (adjustBias) {
            icomp.drawTooltip(g, "bias\n" + String.format("%.2f", bias), biasX, biasY);
        }
    }

    private void drawSliderV(Object g, int x, int y) {
        icomp.drawLine(g, Color.BLACK.getRGB(), x - 4, y - 2, x + 4, y - 2);
        icomp.drawLine(g, Color.BLACK.getRGB(), x - 4, y, x + 4, y);
        icomp.drawLine(g, Color.BLACK.getRGB(), x - 4, y + 2, x + 4, y + 2);
        icomp.drawRectangle(g, Color.GRAY.getRGB(), x - 6, y - 4, 12, 8);
    }

    private void drawSliderH(Object g, int x, int y) {
        icomp.drawLine(g, Color.BLACK.getRGB(), x - 2, y - 4, x - 2, y + 4);
        icomp.drawLine(g, Color.BLACK.getRGB(), x, y - 4, x, y + 4);
        icomp.drawLine(g, Color.BLACK.getRGB(), x + 2, y - 4, x + 2, y + 4);
        icomp.drawRectangle(g, Color.GRAY.getRGB(), x - 4, y - 6, 8, 12);
    }
}