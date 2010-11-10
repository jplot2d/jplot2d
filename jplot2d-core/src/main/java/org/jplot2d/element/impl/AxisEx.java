package org.jplot2d.element.impl;

import java.util.Map;

import org.jplot2d.element.Axis;

public interface AxisEx extends Axis, ComponentEx {

	public AxisTickEx getTick();

	public double getThickness();

	public double getAsc();

	public double getDesc();

	/**
	 * Re-calculate the thickness metrics, include asc and desc.
	 */
	public void calcThickness();

	public AxisEx deepCopy(Map<ElementEx, ElementEx> orig2copyMap);

}
