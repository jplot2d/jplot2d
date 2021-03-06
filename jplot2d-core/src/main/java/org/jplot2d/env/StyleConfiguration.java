/*
 * Copyright 2010-2013 Jingjing Li.
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

import org.jplot2d.element.Element;

/**
 * Define default properties which can be used to initialize the new created plot component.
 *
 * @author Jingjing Li
 */
public interface StyleConfiguration {

    /**
     * Apply style to the given element
     *
     * @param element the element to be configured
     */
    public void applyTo(Element element);

    /**
     * Returns a element instance who can proxy get/set values from/to this profile.
     *
     * @param elementInterface the element interface
     * @return the proxy element
     */
    @SuppressWarnings("SameReturnValue")
    public <T extends Element> T getProxyBean(Class<T> elementInterface);

}
