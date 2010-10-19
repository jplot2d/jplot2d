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
package org.jplot2d.renderer;

import java.awt.Rectangle;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Future;

import org.jplot2d.element.impl.ComponentEx;

/**
 * This class maintains a map to associate a component to its bounds and
 * rendered result.
 * 
 * @author Jingjing Li
 * 
 */
public class AssemblyInfo<T> {

	private static class InfoItem<T> {

		private Rectangle bounds;
		private Future<T> future;

		private InfoItem(Rectangle bounds, Future<T> future) {
			this.bounds = bounds;
			this.future = future;
		}
	}

	private Map<ComponentEx, InfoItem<T>> map = new LinkedHashMap<ComponentEx, InfoItem<T>>();

	public AssemblyInfo() {

	}

	/**
	 * Test if this AssemblyInfo contains the given component.
	 * 
	 * @param comp
	 * @return
	 */
	public boolean contains(ComponentEx comp) {
		return map.containsKey(comp);
	}

	/**
	 * The returned set is an ordered set that follow the z-order.
	 * 
	 * @return
	 */
	public Set<ComponentEx> componentSet() {
		return map.keySet();
	}

	public Rectangle getBounds(ComponentEx comp) {
		return map.get(comp).bounds;
	}

	public Future<T> getFuture(ComponentEx comp) {
		return map.get(comp).future;
	}

	/**
	 * Z-order: The 1st put component is on the bottom. The later is on top.
	 * 
	 * @param comp
	 * @param bounds
	 * @param future
	 */
	public void put(ComponentEx comp, Rectangle bounds, Future<T> future) {
		map.put(comp, new InfoItem<T>(bounds, future));
	}

}