package org.jplot2d.element.impl;

import org.jplot2d.element.Axis;

public interface AxisEx extends Axis, ComponentEx {

	public void setLocation(double locX, double locY);

	public AxisTickEx getTick();

	public double getThickness();

	public double getAsc();

	public double getDesc();

	/**
	 * Called by ViewportAxis when axis transform changed.
	 */
	public void axisTransformChanged();

	/**
	 * Called by ViewportAxis when axis type changed.
	 */
	public void axisTypeChanged();

	/**
	 * Calculate ticks when tick calculation is needed.
	 */
	public void calcTicks();

}
