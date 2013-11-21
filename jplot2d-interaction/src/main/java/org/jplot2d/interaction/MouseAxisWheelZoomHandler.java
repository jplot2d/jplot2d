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
import java.awt.geom.Point2D;

import org.jplot2d.element.Axis;
import org.jplot2d.element.PComponent;
import org.jplot2d.env.BatchToken;
import org.jplot2d.env.PlotEnvironment;
import org.jplot2d.interaction.InteractionModeHandler;
import org.jplot2d.interaction.MouseWheelBehaviorHandler;
import org.jplot2d.notice.UINoticeType;

public class MouseAxisWheelZoomHandler extends MouseWheelBehaviorHandler<MouseAxisWheelZoomBehavior> {

	public MouseAxisWheelZoomHandler(MouseAxisWheelZoomBehavior behavior, InteractionModeHandler handler) {
		super(behavior, handler);
	}

	@Override
	public boolean behaviorPerformed(int x, int y, int wheelRotation) {

		Axis axis;
		PComponent pcomp = (PComponent) handler.getValue(PlotInteractionManager.ACTIVE_COMPONENT_KEY);
		if (pcomp instanceof Axis) {
			axis = (Axis) pcomp;
		} else {
			return false;
		}

		double scale = 1;
		if (wheelRotation > 0) {
			scale = 2;
		} else if (wheelRotation < 0) {
			scale = 1.0 / 2;
		}

		Point2D pp = axis.getPaperTransform().getDtoP(new Point(x, y));

		PlotEnvironment env = (PlotEnvironment) handler.getValue(PlotInteractionManager.PLOT_ENV_KEY);
		BatchToken token = env.beginBatch("AxisWheelZoom");

		double npv = pp.getX() / axis.getLength();
		double start = npv * (1 - scale);
		double end = start + scale;

		axis.getTickManager().getAxisTransform().getLockGroup().zoomRange(start, end);

		env.endBatch(token, UINoticeType.getInstance());
		return true;
	}

}