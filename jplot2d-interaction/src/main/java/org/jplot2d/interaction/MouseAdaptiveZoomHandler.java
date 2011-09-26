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

import org.jplot2d.element.Plot;
import org.jplot2d.env.BatchToken;
import org.jplot2d.env.PlotEnvironment;
import org.jplot2d.interaction.InteractionHandler;
import org.jplot2d.interaction.InteractionModeHandler;
import org.jplot2d.interaction.MouseClickBehaviorHandler;
import org.jplot2d.warning.UIWarningType;

public class MouseAdaptiveZoomHandler extends MouseClickBehaviorHandler<MouseAdaptiveZoomBehavior> {

	public MouseAdaptiveZoomHandler(MouseAdaptiveZoomBehavior behavior,
			InteractionModeHandler handler) {
		super(behavior, handler);
	}

	@Override
	public boolean behaviorPerformed(int x, int y) {
		PlotEnvironment env = (PlotEnvironment) handler.getValue(InteractionHandler.PLOT_ENV_KEY);
		Plot plot = env.getPlotAt(new Point(x, y));

		if (plot == null) {
			return false;
		}

		/**
		 * Zoom-in the given marquee rectangle.
		 */
		BatchToken token = env.beginBatch("Adaptive Zoom");

		plot.adaptiveZoomX();
		plot.adaptiveZoomY();

		env.endBatch(token, UIWarningType.getInstance());

		return true;
	}

}