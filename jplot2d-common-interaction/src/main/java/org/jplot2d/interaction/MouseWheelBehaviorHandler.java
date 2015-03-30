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
 * @author Jingjing Li
 */
public abstract class MouseWheelBehaviorHandler<B extends MouseWheelBehavior> extends
        MouseBehaviorHandler<B> {

    public MouseWheelBehaviorHandler(B behavior, InteractionModeHandler handler) {
        super(behavior, handler);
    }

    public final boolean processMouseEvent(GenericMouseEvent e) {
        return behaviorPerformed(e.getX(), e.getY(), e.getCount());
    }

    /**
     * @param x             TODO
     * @param y             TODO
     * @param wheelRotation On Windows/Linux, always 1. On MacOSX, may be any number due to its wheel
     *                      acceleration.
     */
    public abstract boolean behaviorPerformed(int x, int y, int wheelRotation);

}