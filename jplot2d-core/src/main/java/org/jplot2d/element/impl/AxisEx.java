package org.jplot2d.element.impl;

import org.jplot2d.element.Axis;
import org.jplot2d.element.AxisOrientation;

public interface AxisEx extends Axis, ContainerEx {

	public SubplotEx getParent();

	public AxisRangeManagerEx getRangeManager();

	public AxisTickEx getTick();

	public void setOrientation(AxisOrientation orientation);

	/**
	 * Returns the offset of this axis.
	 * 
	 * @return the length
	 */
	public double getOffset();

	/**
	 * Set the offset for this axis.
	 * 
	 * @param offset
	 */
	public void setOffset(double offset);

	/**
	 * Returns the length of this axis.
	 * 
	 * @return the length
	 */
	public double getLength();

	/**
	 * Set the length for this axis.
	 * 
	 * @param length
	 */
	public void setLength(double length);

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
