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

import java.util.ArrayList;
import java.util.List;

import org.jplot2d.element.Element;
import org.jplot2d.element.impl.ComponentEx;

/**
 * @author Jingjing Li
 * 
 */
public class DummyEnvironment extends Environment {

	public DummyEnvironment() {

	}

	/**
	 * This method is called when component factory create a component proxy.
	 * Every new created component has an associated environment. The
	 * environment must be initialized by this method.
	 * <p>
	 * All elements created must be added to environment. The add order is show
	 * order. That is, the latest added component will show at top.
	 * 
	 * @param element
	 * @param proxy
	 */
	public void registerElement(Element element, Element proxy) {
		proxyMap.put(element, proxy);

		if (element instanceof ComponentEx) {
			ComponentEx comp = (ComponentEx) element;
			if (comp.isCacheable()) {
				addOrder(cacheableComponentList, comp);
				// create a subComponentMap entry
				List<ComponentEx> subComps = new ArrayList<ComponentEx>();
				subComps.add(comp);
				subComponentMap.put(comp, subComps);
			} else if (comp.getParent() == null) {
				// create a subComponentMap entry
				List<ComponentEx> subComps = new ArrayList<ComponentEx>();
				subComps.add(comp);
				subComponentMap.put(comp, subComps);
			} else {
				ComponentEx cc = getCacheableAncestor(comp);
				// add to list in subComponentMap
				List<ComponentEx> subComps = subComponentMap.get(cc);
				addOrder(subComps, comp);
			}
		}
	}

	@Override
	protected void commit() {
		// do nothng
	}

}
