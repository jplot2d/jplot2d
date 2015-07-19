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
package org.jplot2d.env;

import org.jplot2d.element.impl.ElementEx;

/**
 * For internal use only!
 * Interface that provide additional methods to proxy objects.
 *
 * @author Jingjing Li
 */
public interface ElementAddition {

    /**
     * Sets the environment of this component.
     * This method must be <em>synchronized</em> within Environment.getGlobalLock(),
     * and usally should hold the ReentrantLock of both old environment and new environment.
     * There is an exception, registering element to dummy environment from element factory,
     * the original environment is null and lock on new environment is not required.
     *
     * @param env the new environment
     */
    void setEnvironment(Environment env);

    /**
     * Returns the implementation element wrapped by this proxy object.
     * Usually This method should be called within a environment begin-end block,
     * because undo/redo will replace the impl of an element proxy.
     *
     * @return the impl
     */
    ElementEx getImpl();

}
