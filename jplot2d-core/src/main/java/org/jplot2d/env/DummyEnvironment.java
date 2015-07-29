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

import org.jplot2d.element.Element;
import org.jplot2d.element.impl.ElementEx;

/**
 * A environment to host new created elements implementation and their proxies.
 *
 * @author Jingjing Li
 */
public class DummyEnvironment extends Environment {

    public DummyEnvironment(boolean threadSafe) {
        super(threadSafe);
    }

    /**
     * Register a new created element implementation and its proxy to this environment.
     * This method is called by element factory's create methods.
     * Every new created element implementation has a proxy object, which will associate with a dummy environment.
     *
     * @param element the element to be registered
     * @param proxy   the proxy object of the element
     */
    public void registerElement(ElementEx element, Element proxy) {
        ((ElementAddition) proxy).setEnvironment(this);
        proxyMap.put(element, proxy);
    }

    @Override
    protected void commit() {
        // do nothing
    }

}
