package org.jplot2d.element.impl;

import java.awt.Font;

import org.jplot2d.element.Axis;
import org.jplot2d.element.AxisOrientation;
import org.jplot2d.transfrom.PhysicalTransform;

public interface AxisEx extends Axis, ComponentEx {

	public PlotEx getParent();

	public AxisTickManagerEx getTickManager();

	/**
	 * Set shrunk font for displaying by tick manager.
	 * 
	 * @param font
	 *            the shrunk font
	 */
	public void setActualFont(Font font);

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

	/**
	 * Called by {@link PlotEx#addXAxis(Axis)} or {@link PlotEx#addYAxis(Axis)}
	 * to set the orientation of this axis.
	 * 
	 * @param orientation
	 *            the orientation
	 */
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

	public void invalidateThickness();

	/**
	 * calculate asc and desc of this axis.
	 */
	public void calcThickness();

}
