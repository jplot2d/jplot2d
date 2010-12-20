package org.jplot2d.element.impl;

import org.jplot2d.element.Axis;

public interface AxisEx extends Axis, ContainerEx {

	public ViewportAxisEx getParent();

	public AxisTickEx getTick();

	public double getThickness();

	public double getAsc();

	public double getDesc();

	/**
	 * Calculate ticks when tick calculation is needed.
	 */
	public void calcTicks();

	public void calcThickness();
}
