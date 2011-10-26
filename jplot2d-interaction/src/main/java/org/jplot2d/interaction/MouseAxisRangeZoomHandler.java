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
import java.awt.geom.Dimension2D;
import java.awt.geom.Rectangle2D;

import org.jplot2d.element.Axis;
import org.jplot2d.element.AxisOrientation;
import org.jplot2d.element.PComponent;
import org.jplot2d.element.Plot;
import org.jplot2d.env.BatchToken;
import org.jplot2d.env.PlotEnvironment;
import org.jplot2d.interaction.InteractionHandler;
import org.jplot2d.interaction.InteractionModeHandler;
import org.jplot2d.notice.UINoticeType;

public class MouseAxisRangeZoomHandler extends MouseMarqueeHandler<MouseAxisRangeZoomBehavior> {

	private Axis axis;

	public MouseAxisRangeZoomHandler(MouseAxisRangeZoomBehavior behavior,
			InteractionModeHandler handler) {
		super(behavior, handler);
	}

	@Override
	public boolean canStartDargging(int x, int y) {
		PComponent pcomp = (PComponent) handler.getValue(InteractionHandler.ACTIVE_COMPONENT_KEY);
		if (pcomp instanceof Axis) {
			axis = (Axis) pcomp;
			return true;
		} else {
			return false;
		}
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

		double start, end;
		if (axis.getOrientation() == AxisOrientation.HORIZONTAL) {
			start = startPoint.x;
			end = endPoint.x;
		} else if (axis.getOrientation() == AxisOrientation.VERTICAL) {
			start = startPoint.y;
			end = endPoint.y;
		} else {
			throw new Error();
		}

		if (start > end) {
			double temp = end;
			end = start;
			start = temp;
		}

		/**
		 * Zoom-in the given marquee rectangle.
		 */
		PlotEnvironment env = (PlotEnvironment) handler.getValue(InteractionHandler.PLOT_ENV_KEY);
		BatchToken token = env.beginBatch("Axis Range Zoom");

		Plot plot = axis.getParent();

		Dimension2D csize = plot.getContentSize();
		Rectangle2D cbnds = new Rectangle2D.Double(0, 0, csize.getWidth(), csize.getHeight());
		Rectangle2D plotRect = plot.getPhysicalTransform().getPtoD(cbnds).getBounds2D();

		if (axis.getOrientation() == AxisOrientation.HORIZONTAL) {
			double npxStart = (start - plotRect.getX()) / plotRect.getWidth();
			double npxEnd = (end - plotRect.getX()) / plotRect.getWidth();
			axis.getTickManager().getAxisTransform().getLockGroup().zoomRange(npxStart, npxEnd);
		} else if (axis.getOrientation() == AxisOrientation.VERTICAL) {
			double npyStart = 1 - (end - plotRect.getY()) / plotRect.getHeight();
			double npyEnd = 1 - (start - plotRect.getY()) / plotRect.getHeight();
			axis.getTickManager().getAxisTransform().getLockGroup().zoomRange(npyStart, npyEnd);
		}

		env.endBatch(token, UINoticeType.getInstance());

	}

}