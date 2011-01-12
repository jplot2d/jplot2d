package org.jplot2d.element.impl;

import java.awt.geom.Rectangle2D;

import org.jplot2d.element.Subplot;

public interface SubplotEx extends Subplot, ContainerEx {

	public SubplotEx getParent();

	public SubplotMarginEx getMargin();

	public int indexOf(LayerEx layer);

	public LayerEx[] getLayers();

	public int indexOfXViewportAxis(ViewportAxisEx axisGroup);

	public int indexOfYViewportAxis(ViewportAxisEx axisGroup);

	public ViewportAxisEx[] getXViewportAxes();

	public ViewportAxisEx[] getYViewportAxes();

	public int indexOf(SubplotEx subplot);

	public SubplotEx[] getSubplots();

	public void parentPhysicalTransformChanged();

	/**
	 * Sets the rectangle of contents by layout director. All layers in this
	 * subplot have the same viewport bounds.
	 * 
	 * @param bounds
	 *            the rectangle of viewport
	 */
	public void setContentBounds(Rectangle2D bounds);

	/**
	 * Returns the contents constraint of this subplot.
	 * 
	 * @return the contents constraint
	 */
	public Rectangle2D getContentConstrant();

	/**
	 * Impose contents constraint on this subplot
	 * 
	 * @param constraint
	 *            the contents constraint
	 */
	public void setContentConstrant(Rectangle2D constraint);

}
