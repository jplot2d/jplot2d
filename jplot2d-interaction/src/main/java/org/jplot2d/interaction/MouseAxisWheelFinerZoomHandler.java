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
import org.jplot2d.interaction.MouseWheelBehaviorHandler;
import org.jplot2d.notice.UINoticeType;

public class MouseAxisWheelFinerZoomHandler extends
		MouseWheelBehaviorHandler<MouseAxisWheelFinerZoomBehavior> {

	public MouseAxisWheelFinerZoomHandler(MouseAxisWheelFinerZoomBehavior behavior,
			InteractionModeHandler handler) {
		super(behavior, handler);
	}

	@Override
	public boolean behaviorPerformed(int x, int y, int wheelRotation) {

		Axis axis;
		PComponent pcomp = (PComponent) handler.getValue(InteractionHandler.ACTIVE_COMPONENT_KEY);
		if (pcomp instanceof Axis) {
			axis = (Axis) pcomp;
		} else {
			return false;
		}

		double factor = Math.sqrt(Math.sqrt(2));
		double scale = 1;
		if (wheelRotation > 0) {
			scale = factor;
		} else if (wheelRotation < 0) {
			scale = 1.0 / factor;
		}

		PlotEnvironment env = (PlotEnvironment) handler.getValue(InteractionHandler.PLOT_ENV_KEY);
		BatchToken token = env.beginBatch("AxisWheelZoom");

		Plot plot = axis.getParent();

		Dimension2D csize = plot.getContentSize();
		Rectangle2D cbnds = new Rectangle2D.Double(0, 0, csize.getWidth(), csize.getHeight());
		Rectangle2D plotRect = plot.getPhysicalTransform().getPtoD(cbnds).getBounds2D();

		double npv;
		if (axis.getOrientation() == AxisOrientation.HORIZONTAL) {
			npv = (x - plotRect.getX()) / plotRect.getWidth();
		} else if (axis.getOrientation() == AxisOrientation.VERTICAL) {
			npv = 1 - (y - plotRect.getY()) / plotRect.getHeight();
		} else {
			throw new Error();
		}
		double start = npv * (1 - scale);
		double end = start + scale;

		axis.getTickManager().getRangeManager().getLockGroup().zoomRange(start, end);

		env.endBatch(token, UINoticeType.getInstance());
		return true;
	}

}