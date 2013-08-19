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

import org.jplot2d.interaction.InteractionManager;
import org.jplot2d.interaction.InteractionMode;
import org.jplot2d.interaction.MouseBehavior;

/**
 * The interaction manager for PlotXY. Other applications that using plot as their renderer engine should not extends
 * this manager, they should write their own interaction manager.
 * 
 * @author Jingjing Li
 * 
 */
public final class PlotInteractionManager extends InteractionManager {

	public static final String PLOT_ENV_KEY = "PLOT_ENV";

	public static final String INTERACTIVE_COMP_KEY = "INTERACTIVE_COMP";

	public static final String ACTIVE_COMPONENT_KEY = "ACTIVE_COMPONENT";

	public static final String ACTIVE_COMPONENT_MOVABLE_KEY = "ACTIVE_COMPONENT_MOVABLE";

	private static final PlotInteractionManager instance = new PlotInteractionManager();

	protected final MouseBehavior activeComponentBehavior;

	protected final MouseBehavior showCoordinatesTooltipBehavior;

	protected final MouseBehavior moveComponentBehavior;

	protected final MouseBehavior axisAdaptiveZoomBehavior;

	protected final MouseBehavior adaptiveZoomBehavior;

	protected final MouseBehavior axisRangeZoomBehavior;

	protected final MouseBehavior marqueeZoomBehavior;

	protected final MouseBehavior axisWheelZoomBehavior;

	protected final MouseBehavior wheelZoomBehavior;

	protected final MouseBehavior axisWheelFinerZoomBehavior;

	protected final MouseBehavior wheelFinerZoomBehavior;

	protected final MouseBehavior axisPanBehavior;

	protected final MouseBehavior panBehavior;

	final InteractionMode defaultMode;

	public static PlotInteractionManager getInstance() {
		return instance;
	}

	private PlotInteractionManager() {

		// define modes
		defaultMode = new InteractionMode("DEFAULT");

		// define behaviors

		activeComponentBehavior = new MouseActivateComponentBehavior("ActivateComponent");
		showCoordinatesTooltipBehavior = new MouseCoordinatesTooltipBehavior("UpdateCoordinatesTooltip");
		moveComponentBehavior = new MouseMoveComponentBehavior("MoveComponent");

		axisPanBehavior = new MouseAxisPanBehavior("Pan on axis");
		panBehavior = new MousePanBehavior("Pan");

		axisAdaptiveZoomBehavior = new MouseAxisAdaptiveZoomBehavior("AdaptiveZoom on axis");
		adaptiveZoomBehavior = new MouseAdaptiveZoomBehavior("AdaptiveZoom");

		axisRangeZoomBehavior = new MouseAxisRangeZoomBehavior("Zoom on axis");
		marqueeZoomBehavior = new MouseMarqueeZoomBehavior("Zoom");

		axisWheelZoomBehavior = new MouseAxisWheelZoomBehavior("WheelZoom on axis");
		wheelZoomBehavior = new MouseWheelZoomBehavior("WheelZoom");
		axisWheelFinerZoomBehavior = new MouseAxisWheelFinerZoomBehavior("WheelFinerZoom on axis");
		wheelFinerZoomBehavior = new MouseWheelFinerZoomBehavior("WheelFinerZoom");

		// set available behaviors for mode
		defaultMode.setAvailableMouseBehaviors(activeComponentBehavior, showCoordinatesTooltipBehavior,
				moveComponentBehavior, axisPanBehavior, panBehavior, axisAdaptiveZoomBehavior, adaptiveZoomBehavior,
				axisRangeZoomBehavior, marqueeZoomBehavior, axisWheelZoomBehavior, wheelZoomBehavior,
				axisWheelFinerZoomBehavior, wheelFinerZoomBehavior);

		defaultMode.setValueChangeBehaviors(new CursorFeedbackBehavior());

		registerMode(defaultMode);
		setDefaultMode(defaultMode);

	}

}
