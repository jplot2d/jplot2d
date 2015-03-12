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

import org.jplot2d.element.Element;
import org.jplot2d.element.impl.ElementEx;
import org.jplot2d.element.impl.PlotEx;

import java.util.Map;

/**
 * A memento to store undo data.
 *
 * @author Jingjing Li
 */
public class UndoMemento {

    private final PlotEx plot;

    private final Map<ElementEx, Element> proxyMap;

    /**
     * Construct an undo memento.
     *
     * @param plot     the plot
     * @param proxyMap the key is the impl, the value is proxy.
     */
    UndoMemento(PlotEx plot, Map<ElementEx, Element> proxyMap) {
        this.plot = plot;
        this.proxyMap = proxyMap;
    }

    public PlotEx getPlot() {
        return plot;
    }

    /**
     * Returns the proxy map of this undo memento.
     *
     * @return the proxy map of this undo memento
     */
    public Map<ElementEx, Element> getProxyMap() {
        return proxyMap;
    }

}
