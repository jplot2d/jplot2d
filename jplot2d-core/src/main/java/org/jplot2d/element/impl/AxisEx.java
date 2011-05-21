package org.jplot2d.element.impl;

import org.jplot2d.element.Axis;
import org.jplot2d.element.AxisOrientation;
import org.jplot2d.element.PhysicalTransform;

public interface AxisEx extends Axis, ComponentEx {

	public PlotEx getParent();

	public AxisRangeManagerEx getRangeManager();

	public AxisTickEx getTick();

	/**
	 * Returns the PhysicalTransform of this component.
	 * 
	 * @return
	 */
	public PhysicalTransform getPhysicalTransform();

	/**
	 * Moves this plot component to a new location. The origin of the new
	 * location is specified by point <code>p</code>. Point2D <code>p</code> is
	 * given in the parent's physical coordinate space.
	 * 
	 * @param p
	 *            the point defining the origin of the new location, given in
	 *            the coordinate space of this component's parent
	 */
	public void setLocation(double locX, double locY);

	public void setOrientation(AxisOrientation orientation);

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

	public void invalidateThickness();

	/**
	 * calculate asc and desc of this axis. If the the asc or desc changed, and
	 * this axis is visible, it'll call invalidate() to notify plot its layout
	 * is invalid.
	 */
	public void calcThickness();

}
