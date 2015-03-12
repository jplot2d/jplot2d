/**
 * Copyright 2010 Jingjing Li.
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
package org.jplot2d.env;

import org.jplot2d.element.impl.ElementEx;

/**
 * Interface that provide additional methods to proxy objects.
 *
 * @author Jingjing Li
 */
public interface ElementAddition {

    /**
     * Sets the environment of this component and its sub-elements.
     * <p/>
     * This method must be <em>synchronized</em> within Environment.getGlobalLock().
     *
     * @param env the new environment
     */
    public void setEnvironment(Environment env);

    /**
     * Returns the implementation element wrapped by this proxy object.
     * <p/>
     * This method must be called within a environment begin-end block.
     *
     * @return the impl
     */
    public ElementEx getImpl();

}
