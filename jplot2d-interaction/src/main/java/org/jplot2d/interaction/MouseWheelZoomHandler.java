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
package org.jplot2d.interaction;

import java.awt.Point;
import java.awt.geom.Dimension2D;
import java.awt.geom.Rectangle2D;

import org.jplot2d.element.Plot;
import org.jplot2d.env.BatchToken;
import org.jplot2d.env.PlotEnvironment;
import org.jplot2d.interaction.InteractionModeHandler;
import org.jplot2d.interaction.MouseWheelBehaviorHandler;
import org.jplot2d.notice.UINoticeType;

public class MouseWheelZoomHandler extends MouseWheelBehaviorHandler<MouseWheelZoomBehavior> {

	public MouseWheelZoomHandler(MouseWheelZoomBehavior behavior, InteractionModeHandler handler) {
		super(behavior, handler);
	}

	@Override
	public boolean behaviorPerformed(int x, int y, int wheelRotation) {
		PlotEnvironment env = (PlotEnvironment) handler.getValue(PlotInteractionManager.PLOT_ENV_KEY);
		Plot plot = env.getPlotAt(new Point(x, y));
		if (plot == null) {
			return false;
		}

		double scale = 1;
		if (wheelRotation > 0) {
			scale = 2;
		} else if (wheelRotation < 0) {
			scale = 1.0 / 2;
		}

		BatchToken token = env.beginBatch("WheelZoom");

		Dimension2D csize = plot.getContentSize();
		Rectangle2D cbnds = new Rectangle2D.Double(0, 0, csize.getWidth(), csize.getHeight());
		Rectangle2D plotRect = plot.getPaperTransform().getPtoD(cbnds).getBounds2D();

		double npx = (x - plotRect.getX()) / plotRect.getWidth();
		double startx = npx * (1 - scale);
		double endx = startx + scale;
		double npy = 1 - (y - plotRect.getY()) / plotRect.getHeight();
		double starty = npy * (1 - scale);
		double endy = starty + scale;

		plot.zoomXRange(startx, endx);
		plot.zoomYRange(starty, endy);

		env.endBatch(token, UINoticeType.getInstance());
		return true;
	}

}