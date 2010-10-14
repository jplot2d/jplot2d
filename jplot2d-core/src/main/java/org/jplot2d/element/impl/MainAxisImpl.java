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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jplot2d.element.AxisGroup;
import org.jplot2d.element.Element;

/**
 * @author Jingjing Li
 * 
 */
public class MainAxisImpl extends AxisImpl implements MainAxisEx {

	private AxisGroupEx group;

	private final List<LayerEx> layers = new ArrayList<LayerEx>();

	private final List<AuxAxisEx> auxAxes = new ArrayList<AuxAxisEx>();

	public MainAxisImpl() {
		group = new AxisGroupImpl();
		group.addMainAxis(this);
	}

	public Map<Element, Element> getMooringMap() {
		Map<Element, Element> result = new HashMap<Element, Element>();

		if (group.getAxes().length > 1) {
			result.put(this, group);
		}
		for (LayerEx layer : layers) {
			result.put(this, layer);
		}
		for (AuxAxisEx aux : auxAxes) {
			result.put(this, aux);
		}

		return result;
	}

	public AxisGroupEx getGroup() {
		return group;
	}

	public AxisGroupEx setGroup(AxisGroup group) {
		AxisGroupEx result = this.group;
		if (this.group != null) {
			this.group.removeMainAxis(this);
		}
		this.group = (AxisGroupEx) group;
		this.group.addMainAxis(this);
		return result;
	}

	public LayerEx[] getLayers() {
		return layers.toArray(new LayerEx[layers.size()]);
	}

	public void addLayer(LayerEx layer) {
		layers.add(layer);
	}

	public void removeLayer(LayerEx layer) {
		layers.remove(layer);
	}

	public AuxAxisEx[] getAuxAxes() {
		return auxAxes.toArray(new AuxAxisEx[auxAxes.size()]);
	}

}
