/*
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
 * This behavior defines the destination modes when mouse moving.
 *
 * @author Jingjing Li
 */
public class MouseActivateComponentBehavior extends MouseMoveBehavior {

    public MouseActivateComponentBehavior(String name) {
        super(name);
    }

    @Override
    public MouseActivateComponentHandler createMouseBehaviorHandler(
            InteractionModeHandler ihandler) {
        return new MouseActivateComponentHandler(this, ihandler);
    }

}
