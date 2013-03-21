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

/**
 * A high-level abstraction of mouse action. User application only response to mouse command.
 * 
 * @author Jingjing Li
 * 
 */
public abstract class MouseBehaviorHandler<B extends MouseBehavior> {

	protected final B behavior;

	protected final InteractionModeHandler handler;

	public MouseBehaviorHandler(B behavior, InteractionModeHandler handler) {
		this.behavior = behavior;
		this.handler = handler;
	}

	public String getName() {
		return behavior.getName();
	}

	/**
	 * Processes the key pressed event. Returns true if the event is processed correctly and should
	 * not be passed on to other behaviors. Returns false if this behavior does not process the
	 * event. returns true.
	 * 
	 * @return
	 * 
	 */
	public boolean enterModifiersKey() {
		return false;
	}

	/**
	 * Processes the key released event. Returns true if the event is processed correctly and should
	 * not be passed on to other behaviors. Returns false if this behavior does not process the
	 * event. returns true.
	 * 
	 * @return
	 */
	public boolean exitModifiersKey() {
		return false;
	}

	/**
	 * Processes the mouse event. Returns true if the event is processed correctly and should not be
	 * passed on to other behaviors. Returns false if this behavior does not process the event.
	 * returns true.
	 * 
	 * @param e
	 *            the mouse event
	 */
	public abstract boolean processMouseEvent(GenericMouseEvent e);

}
