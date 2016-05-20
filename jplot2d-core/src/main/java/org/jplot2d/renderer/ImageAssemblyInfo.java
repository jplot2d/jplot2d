/*
 * Copyright 2010-2014 Jingjing Li.
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
package org.jplot2d.renderer;

import org.jplot2d.element.impl.ComponentEx;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Future;

/**
 * This class maintains a map to associate components to their bounds and future rendered images.
 *
 * @author Jingjing Li
 */
public class ImageAssemblyInfo {

    private static class InfoItem {

        private final Rectangle bounds;
        private final Future<BufferedImage> future;

        private InfoItem(Rectangle bounds, Future<BufferedImage> future) {
            this.bounds = bounds;
            this.future = future;
        }
    }

    private final Map<ComponentEx, InfoItem> map = new LinkedHashMap<>();

    public ImageAssemblyInfo() {

    }

    /**
     * Test if this AssemblyInfo contains the given component.
     *
     * @param comp the component
     * @return <code>true</code> if this AssemblyInfo contains the given component
     */
    public boolean contains(ComponentEx comp) {
        return map.containsKey(comp);
    }

    /**
     * The returned set is an ordered set that follow the z-order.
     *
     * @return a set of components
     */
    public Set<ComponentEx> componentSet() {
        return map.keySet();
    }

    public Rectangle getBounds(ComponentEx comp) {
        return map.get(comp).bounds;
    }

    public Future<BufferedImage> getFuture(ComponentEx comp) {
        return map.get(comp).future;
    }

    /**
     * Puts a component and its future into this assembly info.
     * Z-order: The 1st put component is on the bottom. The later is on top.
     *
     * @param comp   the component
     * @param bounds the bounds of the given component
     * @param future the future of the given component
     */
    public void put(ComponentEx comp, Rectangle bounds, Future<BufferedImage> future) {
        map.put(comp, new InfoItem(bounds, future));
    }

}
