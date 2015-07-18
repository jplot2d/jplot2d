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

import org.jplot2d.element.impl.ComponentEx;

import java.util.List;

/**
 * A block holds sub-components of a cacheable component.
 *
 * @author Jingjing Li
 */
public class CacheableBlock {
    /**
     * The original component, act as uid
     */
    private final ComponentEx uid;

    /**
     * The component copy for rendering
     */
    private final ComponentEx comp;

    /**
     * The sub-component copies for rendering, contains comp
     */
    private final List<ComponentEx> subcomps;

    /**
     * @param comp     the original component, act as uid
     * @param copy     the component copy for rendering
     * @param subcomps the sub-component copies for rendering, contains comp
     */
    public CacheableBlock(ComponentEx comp, ComponentEx copy, List<ComponentEx> subcomps) {
        this.uid = comp;
        this.comp = copy;
        this.subcomps = subcomps;
    }

    public ComponentEx getUid() {
        return uid;
    }

    public ComponentEx getComp() {
        return comp;
    }

    public List<ComponentEx> getSubcomps() {
        return subcomps;
    }
}