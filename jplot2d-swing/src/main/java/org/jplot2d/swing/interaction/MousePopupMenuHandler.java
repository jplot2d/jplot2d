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

import java.awt.Component;

import org.jplot2d.env.RenderEnvironment;
import org.jplot2d.interaction.InteractionHandler;
import org.jplot2d.interaction.InteractionModeHandler;
import org.jplot2d.interaction.MousePopupBehaviorHandler;

public class MousePopupMenuHandler extends MousePopupBehaviorHandler<MousePopupMenuBehavior> {

	private PopupMenu popup;

	public MousePopupMenuHandler(MousePopupMenuBehavior behavior, InteractionModeHandler handler) {
		super(behavior, handler);
		RenderEnvironment env = (RenderEnvironment) handler
				.getValue(InteractionHandler.PLOT_ENV_KEY);
		popup = new PopupMenu(env);
	}

	@Override
	public void behaviorPerformed(int x, int y) {
		Component comp = (Component) handler.getValue(InteractionHandler.COMPONENT_KEY);
		popup.updateStatus(x, y);
		popup.show(comp, x, y);
	}

}
