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

import java.beans.PropertyChangeEvent;

import org.jplot2d.interaction.InteractionHandler;
import org.jplot2d.interaction.InteractionModeHandler;
import org.jplot2d.interaction.InteractiveComp;
import org.jplot2d.interaction.InteractiveComp.CursorStyle;
import org.jplot2d.interaction.ValueChangeHandler;

/**
 * @author Jingjing Li
 * 
 */
public class CursorFeedbackHandler extends ValueChangeHandler<CursorFeedbackBehavior> {

	public CursorFeedbackHandler(CursorFeedbackBehavior behavior, InteractionModeHandler handler) {
		super(behavior, handler);
	}

	public void propertyChange(PropertyChangeEvent evt) {
		InteractiveComp icomp = handler.getInteractiveComp();
		if (evt.getPropertyName().equals(InteractionHandler.ACTIVE_COMPONENT_MOVABLE_KEY)) {
			Boolean movable = (Boolean) evt.getNewValue();
			if (movable != null && movable) {
				icomp.setCursor(CursorStyle.MOVE_CURSOR);
			} else {
				icomp.setCursor(CursorStyle.DEFAULT_CURSOR);
			}
		} else if (evt.getPropertyName().equals(InteractionModeHandler.MODE_ENTERED_KEY)) {
			Boolean entered = (Boolean) evt.getNewValue();
			if (entered != null && entered) {
				icomp.setCursor(CursorStyle.DEFAULT_CURSOR);
			} else {
				icomp.setCursor(CursorStyle.DEFAULT_CURSOR);
			}
		}
	}

}
