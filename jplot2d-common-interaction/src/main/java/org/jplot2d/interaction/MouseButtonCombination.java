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

import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;

/**
 * Button combinations that can be configured to trigger a behavior. This class contains mouse
 * button and modifiers.
 *
 * @author Jingjing Li
 */
public class MouseButtonCombination {

    private static final int modifiersAllMask = InputEvent.SHIFT_DOWN_MASK | InputEvent.CTRL_DOWN_MASK
            | InputEvent.META_DOWN_MASK | InputEvent.ALT_DOWN_MASK | InputEvent.BUTTON1_DOWN_MASK
            | InputEvent.BUTTON2_DOWN_MASK | InputEvent.BUTTON3_DOWN_MASK
            | InputEvent.ALT_GRAPH_DOWN_MASK;

    public static final int ANY_CLICK_COUNT = 0x07;

    public static final int ANY_BUTTON = 0x0f;

    private final int modifiersOnMask;

    private final int modifiersOffMask;

    private final int clickCount;

    private final int button;

    /**
     * @param modifiers  The modifier keys down during event
     * @param clickCount The number of mouse clicks associated with event
     * @param button     An integer that indicates, which of the mouse buttons has changed its state.
     *                   can be {@link MouseEvent#NOBUTTON} {@link MouseEvent#BUTTON1}
     *                   {@link MouseEvent#BUTTON2} {@link MouseEvent#BUTTON3} or {@link #ANY_BUTTON}
     */
    public MouseButtonCombination(int modifiers, int clickCount, int button) {
        this.modifiersOnMask = modifiers;
        this.modifiersOffMask = ~modifiers & modifiersAllMask;
        this.clickCount = clickCount;
        this.button = button;
    }

    /**
     * @param modifiersOnMask The modifier keys down during event
     * @param clickCount      The number of mouse clicks associated with event
     * @param button          An integer that indicates, which of the mouse buttons has changed its state.
     *                        can be {@link MouseEvent#NOBUTTON} {@link MouseEvent#BUTTON1}
     *                        {@link MouseEvent#BUTTON2} {@link MouseEvent#BUTTON3} or {@link #ANY_BUTTON}
     */
    public MouseButtonCombination(int modifiersOnMask, int modifiersOffMask, int clickCount,
                                  int button) {
        this.modifiersOnMask = modifiersOnMask;
        this.modifiersOffMask = modifiersOffMask;
        this.clickCount = clickCount;
        this.button = button;
    }

    public int getModifiers() {
        return modifiersOnMask;
    }

    public int getButton() {
        return button;
    }

    public int getClickCount() {
        return clickCount;
    }

    public boolean equals(Object obj) {
        if (obj instanceof MouseButtonCombination) {
            MouseButtonCombination a = (MouseButtonCombination) obj;
            return modifiersOnMask == a.modifiersOnMask && clickCount == a.clickCount
                    && button == a.button;
        }
        return false;
    }

    public int hashCode() {
        return button + modifiersOnMask + (clickCount << 16);
    }

    public boolean match(GenericMouseEvent e) {
        return match(e.getModifiers(), e.getCount(), e.getButton());
    }

    public boolean match(int modifiers, int clickCount, int button) {
        return ((modifiers & (modifiersOnMask | modifiersOffMask)) == modifiersOnMask)
                && (this.clickCount == ANY_CLICK_COUNT || this.clickCount == clickCount)
                && ((this.button == ANY_BUTTON) || this.button == button);
    }

}