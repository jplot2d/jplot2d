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
 * A behavior triggered by mouse moved event. The MouseButtonCombination for this behavior:
 * <ul>
 * <li>modifiers: the modifiers while mouse moving</li>
 * <li>clickCount: 0</li>
 * <li>button: 0</li>
 * </ul>
 *
 * @author Jingjing Li
 */
public abstract class MouseMoveBehavior extends MouseBehavior {

    public MouseMoveBehavior(String name) {
        super(name);
    }

}
