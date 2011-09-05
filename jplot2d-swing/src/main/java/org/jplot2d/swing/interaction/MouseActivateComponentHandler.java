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
package org.jplot2d.swing.interaction;


import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Shape;

import org.jplot2d.element.PComponent;
import org.jplot2d.env.PlotEnvironment;
import org.jplot2d.interaction.InteractionHandler;
import org.jplot2d.interaction.InteractionModeHandler;
import org.jplot2d.interaction.MouseMoveBehaviorHandler;
import org.jplot2d.interaction.PlotPaintEvent;
import org.jplot2d.interaction.PlotPaintListener;

/**
 * This handler activate / deactivate component while mouse moving. It set the interaction handler's
 * mode according to the component which mouse moving on.
 * 
 * @author Jingjing Li
 * 
 */
public class MouseActivateComponentHandler extends
		MouseMoveBehaviorHandler<MouseActivateComponentBehavior> implements PlotPaintListener {

	/**
	 * The selectable component that mouse over
	 */
	private PComponent activeComponent;

	private Shape activeBounds;

	private Color background;

	public MouseActivateComponentHandler(MouseActivateComponentBehavior behavior,
			InteractionModeHandler handler) {
		super(behavior, handler);
	}

	@Override
	public void behaviorPerformed(int x, int y) {

		Component ccomp = (Component) handler.getValue(InteractionHandler.COMPONENT_KEY);

		background = (Color) handler.getValue(InteractionHandler.PLOT_BACKGROUND_KEY);

		if (activeComponent != null && activeBounds.contains(x, y)) {
			return;
		}

		// the selectable component that contains the specified point.
		PlotEnvironment penv = (PlotEnvironment) handler.getValue(InteractionHandler.PLOT_ENV_KEY);
		PComponent newComp = penv.getSelectableCompnentAt(new Point(x, y));

		if (activeComponent != null) {
			if (activeComponent == newComp) {
				return;
			}
			deactivate((Graphics2D) ccomp.getGraphics());
			activeComponent = null;
		}

		if (newComp != null) {
			activeComponent = newComp;
			activeBounds = getDeviceBounds(activeComponent);
			activate((Graphics2D) ccomp.getGraphics());
			handler.putValue(InteractionHandler.ACTIVE_COMPONENT_KEY, activeComponent);
		} else {
			handler.putValue(InteractionHandler.ACTIVE_COMPONENT_KEY, null);
		}
	}

	public void plotPainted(PlotPaintEvent evt) {
		/*
		 * Painting plot will lost the active bounds (a blue rectangle). However, The current active
		 * component may has been removed or invisible before plot painted. We must re-add the
		 * active bounds.
		 */
		if (activeComponent != null) {
			activate((Graphics2D) evt.getGraphics());
		}
	}

	/**
	 * Draw bounding box for the active component
	 */
	private void activate(Graphics2D g) {
		g.setColor(Color.BLUE);
		g.setXORMode(background);
		g.draw(getDeviceBounds(activeComponent));
		g.setPaintMode();
	}

	/**
	 * Clear the bounding box for the active component
	 */
	private void deactivate(Graphics2D g) {
		g.setColor(Color.BLUE);
		g.setXORMode(background);
		g.draw(activeBounds);
		g.setPaintMode();
	}

	private static Shape getDeviceBounds(PComponent comp) {
		return comp.getPhysicalTransform().getPtoD(comp.getBounds());
	}

}