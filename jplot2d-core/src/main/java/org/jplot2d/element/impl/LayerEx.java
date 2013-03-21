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

import org.jplot2d.element.Layer;

public interface LayerEx extends Layer, ContainerEx {

	public PlotEx getParent();

	/**
	 * Returns the short id of this layer. The short id is composed of series of ids concatenated with dots. The 1st id
	 * is the id of this element, the 2nd id is the id of the parent of this element, etc, until but not include the
	 * root plot.
	 * 
	 * @return the short id of this layer.
	 */
	public String getShortId();

	public AxisTransformEx getXAxisTransform();

	public AxisTransformEx getYAxisTransform();

	public GraphEx[] getGraphs();

	public int indexOf(GraphEx graph);

	public AnnotationEx[] getAnnotations();

	public int indexOf(AnnotationEx annotation);

	/**
	 * Called by {@link AxisTransformEx} to notify that x/y transform is changed.
	 */
	public void transformChanged();

}
