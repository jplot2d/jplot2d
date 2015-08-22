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
package org.jplot2d.element.impl;

import org.jplot2d.element.Element;
import org.jplot2d.notice.Notice;

import javax.annotation.Nonnull;
import java.util.Map;

/**
 * @author Jingjing Li
 */
public interface ElementEx extends Element {

    ElementEx getParent();

    /**
     * Sets the parent of this component
     *
     * @param parent the new parent
     */
    void setParent(ElementEx parent);

    /**
     * Returns <code>true</code> if this element is a descendant of the given element.
     * The descendants of an element do not include the element itself.
     *
     * @param ancestor the element
     * @return <code>true</code> if this element is a descendant of the given element
     */
    boolean isDescendantOf(@Nonnull ElementEx ancestor);

    /**
     * Returns the full id of this component. The full id is composed of series of ids concatenated
     * with dots. The 1st id is the id of this element, the 2nd id is the id of the parent of this
     * element, etc, until the root plot.
     *
     * @return the full id of this component
     */
    String getFullId();

    /**
     * Return the invoke step to get this object from its parent.
     *
     * @return the invoke step
     */
    InvokeStep getInvokeStepFormParent();

    /**
     * Element call this method to notify message to its parent.
     *
     * @param msg the notice message
     */
    void notify(Notice msg);

    /**
     * Create a structural copy of this element and put them into orig2copyMap. All properties are
     * not copied. The parent of the copy are not set by this method.
     *
     * @param orig2copyMap original element to copy map
     * @return the structural copy of this element
     */
    ElementEx copyStructure(@Nonnull Map<ElementEx, ElementEx> orig2copyMap);

    /**
     * Copies all properties from given src to this element. Notice properties of sub-elements are
     * not copied over.
     *
     * @param src the src
     */
    void copyFrom(ElementEx src);
}
