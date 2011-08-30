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
 * A popup behavior triggered by mousePressed or mouseRelease depend on system. The
 * MouseButtonCombination for this behavior:
 * <ul>
 * <li>modifiers: the modifiers while mouse popup triggered. Notice even the popup is triggered by
 * mousePressed, the modifiers does <i>not</i> include the trigger button.</li>
 * <li>clickCount: the click count to active this behavior. Normally 1</li>
 * <li>button: MouseButtonCombination.ANY_BUTTON</li>
 * 
 * @author Jingjing Li
 * 
 */
public abstract class MousePopupBehavior extends MouseBehavior {

	public MousePopupBehavior(String name) {
		super(name);
	}

}
