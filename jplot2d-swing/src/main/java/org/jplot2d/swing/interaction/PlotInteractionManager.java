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

import org.jplot2d.gui.interaction.MouseAdaptiveZoomBehavior;
import org.jplot2d.gui.interaction.MouseAxisAdaptiveZoomBehavior;
import org.jplot2d.gui.interaction.MouseAxisPanBehavior;
import org.jplot2d.gui.interaction.MouseAxisWheelFinerZoomBehavior;
import org.jplot2d.gui.interaction.MouseAxisWheelZoomBehavior;
import org.jplot2d.gui.interaction.MousePanBehavior;
import org.jplot2d.gui.interaction.MouseWheelFinerZoomBehavior;
import org.jplot2d.gui.interaction.MouseWheelZoomBehavior;
import org.jplot2d.interaction.InteractionManager;
import org.jplot2d.interaction.InteractionMode;
import org.jplot2d.interaction.MouseBehavior;


/**
 * The interaction manager for PlotXY. Other applications that using plot as their renderer engine
 * should not extends this manager, they should write their own interaction manager.
 * 
 * @author Jingjing Li
 * 
 */
public final class PlotInteractionManager extends InteractionManager {

	private static final PlotInteractionManager _instance = new PlotInteractionManager();

	protected final MouseBehavior _popupBehavior;

	protected final MouseBehavior _activeComponentBehavior;

	protected final MouseBehavior _moveComponentBehavior;

	protected final MouseBehavior _axisAdaptiveZoomBehavior;

	protected final MouseBehavior _adaptiveZoomBehavior;

	protected final MouseBehavior _axisRangeZoomBehavior;

	protected final MouseBehavior _marqueeZoomBehavior;

	protected final MouseBehavior _axisWheelZoomBehavior;

	protected final MouseBehavior _wheelZoomBehavior;

	protected final MouseBehavior _axisWheelFinerZoomBehavior;

	protected final MouseBehavior _wheelFinerZoomBehavior;

	protected final MouseBehavior _axisPanBehavior;

	protected final MouseBehavior _panBehavior;

	final InteractionMode _defaultMode;

	public static PlotInteractionManager getInstance() {
		return _instance;
	}

	private PlotInteractionManager() {

		// define modes
		_defaultMode = new InteractionMode("DEFAULT");

		// define behaviors
		_activeComponentBehavior = new MouseActivateComponentBehavior("ActivateComponent");
		_popupBehavior = new MousePopupMenuBehavior("Popup");
		_moveComponentBehavior = new MouseMoveComponentBehavior("MoveComponent");

		_axisPanBehavior = new MouseAxisPanBehavior("Pan on axis");
		_panBehavior = new MousePanBehavior("Pan");

		_axisAdaptiveZoomBehavior = new MouseAxisAdaptiveZoomBehavior("AdaptiveZoom on axis");
		_adaptiveZoomBehavior = new MouseAdaptiveZoomBehavior("AdaptiveZoom");

		_axisRangeZoomBehavior = new MouseAxisRangeZoomBehavior("Zoom on axis");
		_marqueeZoomBehavior = new MouseMarqueeZoomBehavior("Zoom");

		_axisWheelZoomBehavior = new MouseAxisWheelZoomBehavior("WheelZoom on axis");
		_wheelZoomBehavior = new MouseWheelZoomBehavior("WheelZoom");
		_axisWheelFinerZoomBehavior = new MouseAxisWheelFinerZoomBehavior("WheelFinerZoom on axis");
		_wheelFinerZoomBehavior = new MouseWheelFinerZoomBehavior("WheelFinerZoom");

		// set available behaviors for mode
		_defaultMode.setAvailableMouseBehaviors(_popupBehavior, _activeComponentBehavior,
				_moveComponentBehavior, _axisPanBehavior, _panBehavior, _axisAdaptiveZoomBehavior,
				_adaptiveZoomBehavior, _axisRangeZoomBehavior, _marqueeZoomBehavior,
				_axisWheelZoomBehavior, _wheelZoomBehavior, _axisWheelFinerZoomBehavior,
				_wheelFinerZoomBehavior);
		_defaultMode.setValueChangeBehaviors(new CursorFeedbackBehavior());
		registerMode(_defaultMode);

		setDefaultMode(_defaultMode);

	}

}
