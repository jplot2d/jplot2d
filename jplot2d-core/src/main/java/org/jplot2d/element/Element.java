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
package org.jplot2d.element;

import org.jplot2d.annotation.Hierarchy;
import org.jplot2d.annotation.HierarchyOp;
import org.jplot2d.env.Environment;

/**
 * Common interface of all plot elements.
 * A Element must be created by {@link ElementFactory}, which attach the element to an {@link Environment}.
 * The environment will synchronize calls, and trigger events.
 * When an element is added as a child of another element, it will share the environment of its parent.
 *
 * @author Jingjing Li
 */
public interface Element {

    /**
     * Returns the id string of this element
     *
     * @return the id string
     */
    String getId();

    /**
     * Gets the parent of this component.
     *
     * @return the parent of this component
     */
    @Hierarchy(HierarchyOp.GET)
    Element getParent();

    /**
     * Return the attached environment.
     *
     * @return the attached environment
     */
    Environment getEnvironment();

}
