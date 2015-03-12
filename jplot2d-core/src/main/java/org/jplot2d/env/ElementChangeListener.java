/**
 * Copyright 2010-2012 Jingjing Li.
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

import java.util.EventListener;

/**
 * Listen to the structure change, such as adding or removing an element. Or property changes of an element.
 *
 * @author Jingjing Li
 */
public interface ElementChangeListener extends EventListener {

    /**
     * Called after a component added to a container
     *
     * @param event the ElementChangeEvent. The element of this event is the added component
     */
    void componentAdded(ElementChangeEvent event);

    /**
     * Called before a component is removed form its container
     *
     * @param event the ElementChangeEvent. The element of this event is the removing component
     */
    void componentRemoving(ElementChangeEvent event);

    /**
     * Called after a component is removed form its container
     *
     * @param event the ElementChangeEvent. The element of this event is the removed component
     */
    void componentRemoved(ElementChangeEvent event);

    /**
     * Called after properties of an element is changed.
     *
     * @param event event the ElementChangeEvent. The element of this event is the element whoes properties is changed
     */
    void propertiesChanged(ElementChangeEvent event);

    /**
     * Called after the property changes has been processed.
     *
     * @param event the ElementChangeEvent. The element of this event is <code>null</code>
     */
    void propertyChangesProcessed(ElementChangeEvent event);
}
