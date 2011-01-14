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

	/**
	 * This method is used to calculate asc and desc of this axis. If the the
	 * asc or desc changed, it'll call invalidate() to notify subplot the layout
	 * is invalid.
	 */
	public void calcThickness();

}
