package org.jplot2d.element.impl;

import java.util.Map;

import org.jplot2d.element.Axis;

public interface AxisEx extends Axis, ComponentEx {

	public AxisTickEx getTick();

	public double getThickness();

	public double getAsc();

	public double getDesc();

	public AxisEx deepCopy(Map<ElementEx, ElementEx> orig2copyMap);

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
