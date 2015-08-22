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

import org.jplot2d.env.Environment;
import org.jplot2d.notice.Notice;

import javax.annotation.Nonnull;
import java.util.Map;

/**
 * @author Jingjing Li
 */
public abstract class ElementImpl implements ElementEx {

    protected ElementEx parent;

    public final Environment getEnvironment() {
        throw new UnsupportedOperationException();
    }

    public ElementEx getParent() {
        return parent;
    }

    public void setParent(ElementEx parent) {
        this.parent = parent;
    }

    public boolean isDescendantOf(@Nonnull ElementEx ancestor) {
        return getParent() != null && (getParent() == ancestor || getParent().isDescendantOf(ancestor));
    }

    public String getFullId() {
        if (parent != null) {
            return getId() + "." + parent.getFullId();
        } else {
            return getId();
        }
    }

    public void notify(Notice msg) {
        if ((getParent() != null)) {
            getParent().notify(msg);
        }
    }

    public ElementEx copyStructure(@Nonnull Map<ElementEx, ElementEx> orig2copyMap) {
        ElementImpl result;

        try {
            result = this.getClass().newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        orig2copyMap.put(this, result);

        return result;
    }

    public void copyFrom(ElementEx src) {
        // copy nothing
    }

    public String toString() {
        return getFullId();
    }

}
