package org.jplot2d.element.impl;

import java.util.Map;

import org.jplot2d.element.Axis;

public interface AxisEx extends Axis, ComponentEx {

	public double getLength();

	public void setLength(double length);

	public double getThickness();

	public double getAsc();

	public double getDesc();

	/**
	 * Mark the thickness is invalid. Changing tick height, label strings label
	 * font or orientation will call this method
	 */
	public void invalidate();

	/**
	 * Re-calculate the thickness metrics, include asc and desc.
	 */
	public void updateThickness();

	public AxisEx deepCopy(Map<ElementEx, ElementEx> orig2copyMap);

}
