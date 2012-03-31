/**
 * Copyright 2010-2012 Jingjing Li.
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
import java.util.HashSet;
import java.util.Set;

import org.jplot2d.element.Axis;
import org.jplot2d.element.AxisTransform;
import org.jplot2d.element.Plot;
import org.jplot2d.env.PlotEnvironment;
import org.jplot2d.interaction.InteractiveComp.CursorStyle;

/**
 * This handler shows a tooltip to display the coordinates on where the cursor is.
 * 
 * @author Jingjing Li
 * 
 */
public class MouseCoordinatesTooltipHandler extends
		MouseMoveBehaviorHandler<MouseCoordinatesTooltipBehavior> implements VisualFeedbackDrawer {

	private final InteractiveComp icomp;

	private boolean show;

	public MouseCoordinatesTooltipHandler(MouseCoordinatesTooltipBehavior behavior,
			InteractionModeHandler handler) {
		super(behavior, handler);
		icomp = handler.getInteractiveComp();
	}

	@Override
	public boolean enterModifiersKey() {
		show = true;
		icomp.setCursor(CursorStyle.CROSSHAIR_CURSOR);
		// repaint to show the tooltip
		icomp.repaint();
		return true;
	}

	@Override
	public boolean exitModifiersKey() {
		show = false;
		icomp.setCursor(CursorStyle.DEFAULT_CURSOR);
		// repaint to hide the tooltip
		icomp.repaint();
		return true;
	}

	@Override
	public boolean behaviorPerformed(int x, int y) {
		// repaint to update the tooltip
		icomp.repaint();
		return true;
	}

	public void draw(Object g) {

		if (!show) {
			return;
		}

		/*
		 * Draw tooltip for the current coordinate
		 */
		InteractiveComp icomp = handler.getInteractiveComp();
		Point p = icomp.getCursorLocation();

		// the selectable component that contains the specified point.
		PlotEnvironment env = (PlotEnvironment) handler.getValue(InteractionHandler.PLOT_ENV_KEY);
		Plot plot = env.getPlotAt(p);

		if (plot == null) {
			return;
		}

		double paperX = plot.getPaperTransform().getXDtoP(p.x);
		double paperY = plot.getPaperTransform().getYDtoP(p.y);

		if (paperX < 0 || paperX > plot.getContentSize().getWidth()) {
			return;
		}
		if (paperY < 0 || paperY > plot.getContentSize().getHeight()) {
			return;
		}

		Set<AxisTransform> xats = new HashSet<AxisTransform>();
		for (Axis axis : plot.getXAxes()) {
			xats.add(axis.getTickManager().getAxisTransform());
		}
		Set<AxisTransform> yats = new HashSet<AxisTransform>();
		for (Axis axis : plot.getYAxes()) {
			yats.add(axis.getTickManager().getAxisTransform());
		}

		if (xats.size() > 1 || yats.size() > 1) {

		}

		icomp.drawTooltip(g, "tooltip", p.x, p.y);

	}

}