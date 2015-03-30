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
 * A composite of MouseButtonCombination and an enabled property.
 *
 * @author Jingjing Li
 */
public class MouseButtonCombinationEnablity {

    private static final int CLICK_COUNT_MASK = 0x070000;

    private static final int BUTTON_MASK = 0x0f;

    private boolean enabled;

    private MouseButtonCombination mbc;

    /**
     * Create a enabled MouseButtonCombination with the given arguments.
     */
    public MouseButtonCombinationEnablity(int modifiersOnMask, int clickCount, int button) {
        this(modifiersOnMask, clickCount, button, true);
    }

    /**
     * Create a MouseButtonCombination with the given arguments and the given enabled argument.
     */
    public MouseButtonCombinationEnablity(int modifiersOnMask, int clickCount, int button,
                                          boolean enabled) {
        this.enabled = enabled;
        this.mbc = new MouseButtonCombination(modifiersOnMask, clickCount, button);
    }

    /**
     * Create with the given MouseButtonCombination and the given enabled argument.
     *
     * @param mbc     the MouseButtonCombination, can be null, means the binding in unconfigured
     * @param enabled determines if the MouseButtonCombination is enabled
     */
    public MouseButtonCombinationEnablity(MouseButtonCombination mbc, boolean enabled) {
        this.enabled = enabled;
        this.mbc = mbc;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public MouseButtonCombination getMouseButtonCombination() {
        return mbc;
    }

    public void setMouseButtonCombination(MouseButtonCombination mbc) {
        this.mbc = mbc;
    }

    private int intValue() {
        if (mbc == null) {
            return -1;
        }
        // modifiers bit 6-13
        // clickCount << 16
        // button bit 0-3
        // disabled bit 31
        return mbc.getButton() + mbc.getModifiers() + (mbc.getClickCount() << 16)
                + ((enabled) ? 0 : 0x80000000);
    }

    public String toString() {
        return (mbc == null) ? "" : String.valueOf(intValue());
    }

    public static MouseButtonCombinationEnablity valueOf(String s) {
        int v = Integer.parseInt(s);
        int button = v & BUTTON_MASK;
        int modifiers = v & 0x3fc0;
        int clickCount = (v & CLICK_COUNT_MASK) >> 16;
        boolean enabled = (v & 0x80000000) == 0;
        return new MouseButtonCombinationEnablity(modifiers, clickCount, button, enabled);
    }

    public boolean equals(Object obj) {
        if (obj instanceof MouseButtonCombinationEnablity) {
            MouseButtonCombinationEnablity mbce = (MouseButtonCombinationEnablity) obj;

            return (enabled == mbce.enabled)
                    && ((mbc == mbce.mbc) || (mbc != null && mbc.equals(mbce.mbc)));
        }
        return false;
    }

    public int hashCode() {
        return intValue();
    }
}
