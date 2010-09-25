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
package org.jplot2d.element.impl;

import java.util.Map;

import org.jplot2d.element.Container;
import org.jplot2d.element.Element;
import org.jplot2d.element.PhysicalTransform;

public class ContainerImpl extends ComponentImpl implements Container {

	public PhysicalTransform getPhysicalTransform() {
		throw new UnsupportedOperationException();
	}

	public ContainerImpl deepCopy(Map<Element, Element> orig2copyMap) {
		ContainerImpl result = new ContainerImpl();
		result.copyFrom(this);
		if (orig2copyMap != null) {
			orig2copyMap.put(this, result);
		}
		return result;
	}
}
