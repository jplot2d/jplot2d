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

import org.jplot2d.element.Axis;
import org.jplot2d.element.PComponent;
import org.jplot2d.env.BatchToken;
import org.jplot2d.env.PlotEnvironment;
import org.jplot2d.interaction.InteractionHandler;
import org.jplot2d.interaction.InteractionModeHandler;
import org.jplot2d.interaction.MouseClickBehaviorHandler;
import org.jplot2d.notice.UINoticeType;

public class MouseAxisAdaptiveZoomHandler extends
		MouseClickBehaviorHandler<MouseAxisAdaptiveZoomBehavior> {

	public MouseAxisAdaptiveZoomHandler(MouseAxisAdaptiveZoomBehavior behavior,
			InteractionModeHandler handler) {
		super(behavior, handler);
	}

	@Override
	public boolean behaviorPerformed(int x, int y) {

		Axis axis;
		PComponent pcomp = (PComponent) handler.getValue(InteractionHandler.ACTIVE_COMPONENT_KEY);
		if (pcomp instanceof Axis) {
			axis = (Axis) pcomp;
		} else {
			return false;
		}

		PlotEnvironment env = (PlotEnvironment) handler.getValue(InteractionHandler.PLOT_ENV_KEY);
		BatchToken token = env.beginBatch("Adaptive Zoom");

		axis.getTickManager().getAxisTransform().getLockGroup().setAutoRange(true);

		env.endBatch(token, UINoticeType.getInstance());

		return true;
	}

}