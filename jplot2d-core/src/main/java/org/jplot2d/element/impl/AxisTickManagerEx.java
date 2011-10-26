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

import org.jplot2d.axtick.TickAlgorithm;
import org.jplot2d.element.AxisTickManager;
import org.jplot2d.tex.MathElement;
import org.jplot2d.transfrom.TransformType;
import org.jplot2d.util.Range2D;

/**
 * @author Jingjing Li
 * 
 */
public interface AxisTickManagerEx extends AxisTickManager, ElementEx, Joinable {

	public AxisEx getParent();

	public AxisEx[] getAxes();

	public AxisTransformEx getAxisTransform();

	public void addAxis(AxisEx axis);

	public void removeAxis(AxisEx axis);

	public TickAlgorithm getTickAlgorithm();

	public void setTickAlgorithm(TickAlgorithm algorithm);

	public MathElement[] getLabelModels();

	/**
	 * Calculate ticks when tick calculation is needed.
	 */
	public void calcTicks();

	/**
	 * This method not change internal status of TickManager. Only get those
	 * values: tickCalculator, tickNumber, labelFormat, labelInterval
	 * 
	 * @param txfType
	 *            transform type
	 * @param range
	 *            the core range
	 * @return the expanded range
	 */
	public Range2D expandRangeToTick(TransformType txfType, Range2D range);

	/**
	 * Notified by AxisRangeManagerEx that transform type chnaged.
	 */
	public void transformTypeChanged();

}
