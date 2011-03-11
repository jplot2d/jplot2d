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
package org.jplot2d.element.impl;

import java.awt.Color;
import java.util.Map;

/**
 * @author Jingjing Li
 * 
 */
public abstract class GraphPlotterImpl extends ElementImpl implements
		GraphPlotterEx {

	private final LegendItemEx legendItem;

	private Color color;

	private boolean visible;

	protected GraphPlotterImpl(LegendItemEx legendItem) {
		this.legendItem = legendItem;
		legendItem.setParent(this);
	}

	public LayerEx getParent() {
		return (LayerEx) super.getParent();
	}

	public LegendItemEx getLegendItem() {
		return legendItem;
	}

	public boolean isVisible() {
		return visible;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
		if (getParent() != null) {
			getParent().redraw();
		}
	}

	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		this.color = color;
	}

	public Color getEffectiveColor() {
		if (color != null) {
			return color;
		} else if (getParent() != null) {
			return getParent().getEffectiveColor();
		} else {
			return null;
		}
	}

	@Override
	public ElementEx copyStructure(Map<ElementEx, ElementEx> orig2copyMap) {
		GraphPlotterImpl result = (GraphPlotterImpl) super
				.copyStructure(orig2copyMap);

		if (orig2copyMap != null) {
			orig2copyMap.put(getLegendItem(), result.getLegendItem());
		}

		return result;
	}

}
