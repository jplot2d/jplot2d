/*
 * Copyright 2010-2015 Jingjing Li.
 *
 * This file is part of jplot2d.
 *
 * jplot2d is free software:
 * you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation, either version 3 of the License, or any later version.
 *
 * jplot2d is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Lesser Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with jplot2d.
 * If not, see <http://www.gnu.org/licenses/>.
 */
package org.jplot2d.interaction;

/**
 * Provide the mouse preference.
 *
 * @author Jingjing Li
 */
public interface MousePreference {

    Integer getClickThreshold();

    InteractionMode[] getModes();

    MouseBehavior[] getBehaviorsInMode(InteractionMode mode);

    /**
     * Returns the MouseButtonCombinationEnablity for the given behavior in the mode.
     *
     * @param mode     the  InteractionMode
     * @param behavior the MouseBehavior
     * @return the MouseButtonCombinationEnablity for the given behavior in the mode
     */
    MouseButtonCombinationEnablity[] getMouseButtonCombinationEnablity(InteractionMode mode, MouseBehavior behavior);

}
