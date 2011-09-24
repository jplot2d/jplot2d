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

import java.awt.event.MouseEvent;

/**
 * Generic mouse event for AWT and SWT.
 * 
 * @author Jingjing Li
 * 
 */
public class GenericMouseEvent {

	public static final int MOUSE_CLICKED = MouseEvent.MOUSE_CLICKED;

	public static final int MOUSE_PRESSED = MouseEvent.MOUSE_PRESSED;

	public static final int MOUSE_RELEASED = MouseEvent.MOUSE_RELEASED;

	public static final int MOUSE_MOVED = MouseEvent.MOUSE_MOVED;

	public static final int MOUSE_ENTERED = MouseEvent.MOUSE_ENTERED;

	public static final int MOUSE_EXITED = MouseEvent.MOUSE_EXITED;

	public static final int MOUSE_DRAGGED = MouseEvent.MOUSE_DRAGGED;

	public static final int MOUSE_WHEEL = MouseEvent.MOUSE_WHEEL;

	public static final int BUTTON1 = MouseEvent.BUTTON1;

	public static final int BUTTON2 = MouseEvent.BUTTON2;

	public static final int BUTTON3 = MouseEvent.BUTTON3;

	public static final int BUTTON1_DOWN_MASK = MouseEvent.BUTTON1_DOWN_MASK;

	public static final int BUTTON2_DOWN_MASK = MouseEvent.BUTTON2_DOWN_MASK;

	public static final int BUTTON3_DOWN_MASK = MouseEvent.BUTTON3_DOWN_MASK;

	public static final int SHIFT_DOWN_MASK = MouseEvent.SHIFT_DOWN_MASK;

	public static final int CTRL_DOWN_MASK = MouseEvent.CTRL_DOWN_MASK;

	public static final int META_DOWN_MASK = MouseEvent.META_DOWN_MASK;

	public static final int ALT_DOWN_MASK = MouseEvent.ALT_DOWN_MASK;

	private final int type;
	private final int modifiers;
	private final int x;
	private final int y;
	private final int count;
	private final int button;

	public GenericMouseEvent(int type, int modifiers, int x, int y, int count, int button) {
		this.type = type;
		this.modifiers = modifiers;
		this.x = x;
		this.y = y;
		this.count = count;
		this.button = button;
	}

	public int getType() {
		return type;
	}

	public int getModifiers() {
		return modifiers;
	}

	public int getCount() {
		return count;
	}

	public int getButton() {
		return button;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

}
