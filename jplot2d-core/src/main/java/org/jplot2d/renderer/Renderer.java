/**
 * Copyright 2010, 2011 Jingjing Li.
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

import java.awt.Rectangle;
import java.awt.geom.Dimension2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.jplot2d.element.impl.ComponentEx;
import org.jplot2d.element.impl.PlotEx;

/**
 * A renderer can be added to {@link RenderEnvironment} to generate a result for plot. When a
 * command is committed, the {@link #render(PlotEx, Map, Collection, Map)} method is called.
 * 
 * @author Jingjing Li
 * 
 */
public abstract class Renderer<T> {

	static Logger logger = Logger.getLogger("org.jplot2d.renderer");

	private final List<RenderingFinishedListener> renderingFinishedListenerList = Collections
			.synchronizedList(new ArrayList<RenderingFinishedListener>());

	/**
	 * Render the given plot. This method is protected by environment lock.
	 * 
	 * @param plot
	 *            the plot to be rendered
	 * @param cacheableCompMap
	 *            A map contains all cacheable components which can iterate in z-order. The value is
	 *            cacheable component will be rendered. The key is unique identifier of every value.
	 *            The map contains the top plot, even if the plot is uncacheable.
	 * @param unmodifiedCacheableComps
	 *            A collection of unique identifier of unmodified cacheable components
	 * @param subcompsMap
	 *            the key is cacheable component, include uncacheable top plot. the value is all
	 *            key's sub-components in z-order, include the key itself.
	 */
	public abstract void render(PlotEx plot, Map<ComponentEx, ComponentEx> cacheableCompMap,
			Collection<ComponentEx> unmodifiedCacheableComps,
			Map<ComponentEx, ComponentEx[]> subcompsMap);

	/**
	 * Returns a rectangle that completely enclose the given component.
	 * 
	 * @param comp
	 * @return
	 */
	protected Rectangle getDeviceBounds(ComponentEx comp) {
		if (comp instanceof PlotEx) {
			double scale = ((PlotEx) comp).getPaperTransform().getScale();
			Dimension2D size = ((PlotEx) comp).getSize();
			return new Rectangle2D.Double(0, 0, size.getWidth() * scale, size.getHeight() * scale)
					.getBounds();
		} else {
			Rectangle2D pbounds = comp.getBounds();
			return comp.getPaperTransform().getPtoD(pbounds).getBounds();
		}
	}

	/**
	 * @param fsn
	 * @param t
	 */
	protected void fireRenderingFinished(long fsn, T t) {
		RenderingFinishedListener[] ls = renderingFinishedListenerList
				.toArray(new RenderingFinishedListener[0]);
		for (RenderingFinishedListener lsnr : ls) {
			lsnr.renderingFinished(new RenderingFinishedEvent(fsn, t));
		}

	}

	public void addRenderingFinishedListener(RenderingFinishedListener listener) {
		renderingFinishedListenerList.add(listener);
	}

	public void removeRenderingFinishedListener(RenderingFinishedListener listener) {
		renderingFinishedListenerList.remove(listener);
	}

}
