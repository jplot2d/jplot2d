/*
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
package org.jplot2d.interaction;

import org.jplot2d.element.PComponent;
import org.jplot2d.element.PlotAxis;
import org.jplot2d.env.BatchToken;
import org.jplot2d.env.PlotEnvironment;
import org.jplot2d.notice.UINoticeType;

import java.awt.*;
import java.awt.geom.Point2D;

public class MouseAxisRangeZoomHandler extends MouseMarqueeHandler<MouseAxisRangeZoomBehavior> {

    private PlotAxis axis;

    public MouseAxisRangeZoomHandler(MouseAxisRangeZoomBehavior behavior, InteractionModeHandler handler) {
        super(behavior, handler);
    }

    @Override
    public boolean canStartDragging(int x, int y) {
        PComponent pcomp = (PComponent) handler.getValue(PlotInteractionManager.ACTIVE_COMPONENT_KEY);
        if (pcomp instanceof PlotAxis) {
            axis = (PlotAxis) pcomp;
            return true;
        } else {
            return false;
        }
    }

    /**
     * Perform the zoom action when mouse up event ends zoom. This method only called when plot is zoomable, and the
     * size of zoom rectangle is at least 2x2 pixel.
     *
     * @param startPoint the mouse point that the zoom starts
     * @param endPoint   the mouse point that the zoom ends
     */
    protected void handleMarquee(Point startPoint, Point endPoint) {

        Point2D spp = axis.getPaperTransform().getDtoP(startPoint);
        Point2D epp = axis.getPaperTransform().getDtoP(endPoint);

        double start = spp.getX();
        double end = epp.getX();
        if (start > end) {
            double temp = end;
            end = start;
            start = temp;
        }

        /**
         * Zoom-in the given marquee rectangle.
         */
        PlotEnvironment env = (PlotEnvironment) handler.getValue(PlotInteractionManager.PLOT_ENV_KEY);
        BatchToken token = env.beginBatch("Axis Range Zoom");

        double length = axis.getLength();
        axis.getTickManager().getAxisTransform().getLockGroup().zoomRange(start / length, end / length);

        env.endBatch(token, UINoticeType.getInstance());

    }

}