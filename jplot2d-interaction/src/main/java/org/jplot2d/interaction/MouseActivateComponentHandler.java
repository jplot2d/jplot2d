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

import java.awt.Color;
import java.awt.Point;
import java.awt.Shape;

import org.jplot2d.element.PComponent;
import org.jplot2d.env.PlotEnvironment;

/**
 * This handler activate / deactivate component while mouse moving. It set the interaction handler's
 * mode according to the component which mouse moving on.
 * 
 * @author Jingjing Li
 * 
 */
public class MouseActivateComponentHandler extends
		MouseMoveBehaviorHandler<MouseActivateComponentBehavior> implements VisualFeedbackDrawer {

	private final InteractiveComp icomp;

	/**
	 * The selectable component that mouse over
	 */
	private PComponent activeComponent;

	private Shape activeBounds;

	public MouseActivateComponentHandler(MouseActivateComponentBehavior behavior,
			InteractionModeHandler handler) {
		super(behavior, handler);
		icomp = handler.getInteractiveComp();
	}

	@Override
	public void behaviorPerformed(int x, int y) {

		if (activeComponent != null && activeBounds.contains(x, y)) {
			return;
		}

		// the selectable component that contains the specified point.
		PlotEnvironment penv = (PlotEnvironment) handler.getValue(InteractionHandler.PLOT_ENV_KEY);
		PComponent newComp = penv.getSelectableCompnentAt(new Point(x, y));

		if (activeComponent == newComp) {
			return;
		}

		if (newComp != null) {
			activeComponent = newComp;
			activeBounds = getDeviceBounds(activeComponent);
			// activate((Graphics2D) ccomp.getGraphics());
			handler.putValue(InteractionHandler.ACTIVE_COMPONENT_KEY, activeComponent);
		} else {
			activeComponent = null;
			activeBounds = null;
			handler.putValue(InteractionHandler.ACTIVE_COMPONENT_KEY, null);
		}

		icomp.repaint();

	}

	public void draw(Object g) {
		/*
		 * Draw bounding box for the active component
		 */
		if (activeComponent != null) {
			activeBounds = getDeviceBounds(activeComponent);
			icomp.drawShape(g, Color.BLUE.getRGB(), activeBounds);
		}
	}

	private static Shape getDeviceBounds(PComponent comp) {
		return comp.getPaperTransform().getPtoD(comp.getBounds());
	}

}