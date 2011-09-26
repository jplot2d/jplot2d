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


import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;

import org.jplot2d.element.Plot;
import org.jplot2d.env.BatchToken;
import org.jplot2d.env.PlotEnvironment;
import org.jplot2d.interaction.InteractionHandler;
import org.jplot2d.interaction.InteractionModeHandler;
import org.jplot2d.warning.UIWarningType;

public class MouseMarqueeZoomHandler extends MouseMarqueeHandler<MouseMarqueeZoomBehavior> {

	public MouseMarqueeZoomHandler(MouseMarqueeZoomBehavior behavior, InteractionModeHandler handler) {
		super(behavior, handler);
	}

	@Override
	public boolean canStartDargging(int x, int y) {
		return true;
	}

	/**
	 * Perform the zoom action when mouse up event ends zoom. This method only called when plot is
	 * zoomable, and the size of zoom rectangle is at least 2x2 pixel.
	 * 
	 * @param startPoint
	 *            the mouse point that the zoom starts
	 * @param endPoint
	 *            the mouse point that the zoom ends
	 */
	protected void handleMarquee(Point startPoint, Point endPoint) {

		Rectangle zrect = new Rectangle();
		zrect.setFrameFromDiagonal(startPoint, endPoint);

		PlotEnvironment env = (PlotEnvironment) handler.getValue(InteractionHandler.PLOT_ENV_KEY);
		Plot plot = env.getPlotAt(startPoint);

		if (plot == null) {
			return;
		}

		/**
		 * Zoom-in the given marquee rectangle.
		 */
		BatchToken token = env.beginBatch("MarqueeZoom");

		Rectangle2D plotRect = plot.getPhysicalTransform().getPtoD(plot.getContentBounds()).getBounds2D();

		double npxStart = (zrect.getX() - plotRect.getX()) / plotRect.getWidth();
		double npxEnd = (zrect.getMaxX() - plotRect.getX()) / plotRect.getWidth();
		plot.zoomXRange(npxStart, npxEnd);

		double npyStart = 1 - (zrect.getMaxY() - plotRect.getY()) / plotRect.getHeight();
		double npyEnd = 1 - (zrect.getMinY() - plotRect.getY()) / plotRect.getHeight();
		plot.zoomYRange(npyStart, npyEnd);

		env.endBatch(token, UIWarningType.getInstance());
	}

}