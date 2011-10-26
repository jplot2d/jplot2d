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
import org.jplot2d.interaction.MouseDragBehaviorHandler;
import org.jplot2d.notice.UINoticeType;

public class MouseAxisPanHandler extends MouseDragBehaviorHandler<MouseAxisPanBehavior> {

	private Axis axis;

	private int oldv;

	public MouseAxisPanHandler(MouseAxisPanBehavior behavior, InteractionModeHandler handler) {
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

	@Override
	public void draggingStarted(int x, int y) {
		if (axis.getOrientation() == AxisOrientation.HORIZONTAL) {
			oldv = x;
		} else if (axis.getOrientation() == AxisOrientation.VERTICAL) {
			oldv = y;
		} else {
			throw new Error();
		}
	}

	@Override
	public void draggingTo(int x, int y) {
		int offset;
		if (axis.getOrientation() == AxisOrientation.HORIZONTAL) {
			offset = x - oldv;
			oldv = x;
		} else if (axis.getOrientation() == AxisOrientation.VERTICAL) {
			offset = y - oldv;
			oldv = y;
		} else {
			throw new Error();
		}

		/**
		 * pan the given distance
		 */
		PlotEnvironment env = (PlotEnvironment) handler.getValue(InteractionHandler.PLOT_ENV_KEY);
		BatchToken token = env.beginBatch("MarqueeZoom");

		Plot plot = axis.getParent();

		Dimension2D csize = plot.getContentSize();
		Rectangle2D cbnds = new Rectangle2D.Double(0, 0, csize.getWidth(), csize.getHeight());
		Rectangle2D plotRect = plot.getPhysicalTransform().getPtoD(cbnds).getBounds2D();

		double npxStart = -offset / plotRect.getWidth();
		double npxEnd = 1 + npxStart;
		axis.getTickManager().getAxisTransform().getLockGroup().zoomRange(npxStart, npxEnd);

		env.endBatch(token, UINoticeType.getInstance());
	}

	@Override
	public void draggingFinished(int x, int y) {
		// nothing to do

	}

	@Override
	public void draggingCancelled() {
		// nothing to do

	}

}