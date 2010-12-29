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

import org.jplot2d.axtrans.AxisTransform;
import org.jplot2d.axtrans.NormalTransform;
import org.jplot2d.axtrans.TransformType;
import org.jplot2d.element.ViewportAxis;
import org.jplot2d.element.AxisOrientation;
import org.jplot2d.util.Range2D;

/**
 * @author Jingjing Li
 * 
 */
public interface ViewportAxisEx extends ViewportAxis, ContainerEx {

	public SubplotEx getParent();

	public void setOrientation(AxisOrientation orientation);

	/**
	 * Returns the normal transform of this axis
	 * 
	 * @return the normal transform
	 */
	public NormalTransform getNormalTransform();

	/**
	 * Sets the normal transform of this axis
	 * 
	 * @param ntf
	 *            the normal transform
	 */
	public void setNormalTransfrom(NormalTransform ntf);

	/**
	 * Returns the offset of this ViewportAxis.
	 * 
	 * @return the length
	 */
	public double getOffset();

	/**
	 * Set the offset for this ViewportAxis. All axes of a ViewportAxis have the
	 * same offset.
	 * 
	 * @param offset
	 */
	public void setOffset(double offset);

	/**
	 * Returns the length of this ViewportAxis.
	 * 
	 * @return the length
	 */
	public double getLength();

	/**
	 * Set the length for this ViewportAxis. All axes of a ViewportAxis have the
	 * same length.
	 * 
	 * @param length
	 */
	public void setLength(double length);

	public AxisLockGroupEx getLockGroup();

	public AxisEx[] getAxes();

	public int indexOfAxis(AxisEx axis);

	public LayerEx[] getLayers();

	public void addLayer(LayerEx layer);

	public void removeLayer(LayerEx layer);

	public AxisTransform getAxisTransform();

	public Range2D expandRangeToTick(Range2D ur);

	public void changeTransformType(TransformType txfType);

}
