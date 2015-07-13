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

import org.jplot2d.element.Plot;
import org.jplot2d.env.BatchToken;
import org.jplot2d.env.PlotEnvironment;
import org.jplot2d.notice.UINoticeType;

import java.awt.Point;
import java.awt.geom.Dimension2D;
import java.awt.geom.Rectangle2D;

public class MousePanHandler extends MouseDragBehaviorHandler<MousePanBehavior> {

    private final InteractiveComp icomp;
    private Plot plot;
    private int oldX, oldY;

    public MousePanHandler(MousePanBehavior behavior, InteractionModeHandler handler) {
        super(behavior, handler);
        icomp = (InteractiveComp) handler.getValue(PlotInteractionManager.INTERACTIVE_COMP_KEY);
    }

    @Override
    public boolean canStartDragging(int x, int y) {
        PlotEnvironment env = (PlotEnvironment) handler.getValue(PlotInteractionManager.PLOT_ENV_KEY);
        plot = env.getPlotAt(new Point(x, y));
        return (plot != null);
    }

    @Override
    public void draggingStarted(int x, int y) {
        oldX = x;
        oldY = y;
        icomp.setCursor(InteractiveComp.CursorStyle.MOVE_CURSOR);
    }

    @Override
    public void draggingTo(int x, int y) {
        int xoff = x - oldX;
        int yoff = y - oldY;
        oldX = x;
        oldY = y;

        /**
         * pan the given distance
         */
        PlotEnvironment env = (PlotEnvironment) handler.getValue(PlotInteractionManager.PLOT_ENV_KEY);
        BatchToken token = env.beginBatch("Pan");

        Dimension2D csize = plot.getContentSize();
        Rectangle2D cbnds = new Rectangle2D.Double(0, 0, csize.getWidth(), csize.getHeight());
        Rectangle2D plotRect = plot.getPaperTransform().getPtoD(cbnds).getBounds2D();

        double npxStart = -xoff / plotRect.getWidth();
        double npxEnd = 1 + npxStart;
        double npyStart = yoff / plotRect.getHeight();
        double npyEnd = 1 + npyStart;

        plot.zoomXRange(npxStart, npxEnd);
        plot.zoomYRange(npyStart, npyEnd);

        env.endBatch(token, UINoticeType.getInstance());
    }

    @Override
    public void draggingFinished(int x, int y) {
        icomp.setCursor(InteractiveComp.CursorStyle.DEFAULT_CURSOR);
    }

    @Override
    public void draggingCancelled() {
        // nothing to do
    }

}