/**
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
package org.jplot2d.renderer;

import org.jplot2d.element.impl.ComponentEx;
import org.jplot2d.element.impl.PlotEx;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.Rectangle;
import java.awt.geom.Dimension2D;
import java.awt.geom.Rectangle2D;
import java.util.List;

/**
 * A renderer can generate a result for a plot component.
 *
 * @author Jingjing Li
 */
public abstract class Renderer {

    protected static final Logger logger = LoggerFactory.getLogger("org.jplot2d.renderer");

    /**
     * Render the given component. This method is protected by environment lock.
     *
     * @param comp           the component to be rendered
     * @param cacheBlockList A list of CacheBlock in z-order, contains the top component, even if the component is uncacheable.
     *                       The CacheBlock contains cacheable components that will be rendered.
     */
    public abstract void render(ComponentEx comp, List<CacheableBlock> cacheBlockList);

    /**
     * Returns a rectangle that completely enclose the given component.
     * The rectangle is on device space and relative to the device original (top-left corner of screen).
     *
     * @param comp the component to be measured
     * @return a rectangle that completely enclose the given component
     */
    protected static Rectangle getDeviceBounds(ComponentEx comp) {
        if (comp instanceof PlotEx) {
            double scale = comp.getPaperTransform().getScale();
            Dimension2D size = comp.getSize();
            return new Rectangle2D.Double(0, 0, size.getWidth() * scale, size.getHeight() * scale).getBounds();
        } else {
            Rectangle2D pbounds = comp.getBounds();
            return comp.getPaperTransform().getPtoD(pbounds).getBounds();
        }
    }

}
