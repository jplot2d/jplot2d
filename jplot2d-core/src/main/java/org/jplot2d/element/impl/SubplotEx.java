package org.jplot2d.element.impl;

import java.awt.geom.Rectangle2D;

import org.jplot2d.element.Subplot;

public interface SubplotEx extends Subplot, ContainerEx {

	public SubplotEx getParent();

	/**
	 * Determines whether this component is valid. A component is valid when it
	 * is correctly sized and positioned within its parent container and all its
	 * children are also valid. In order to account for peers' size
	 * requirements, components are invalidated before they are first shown on
	 * the screen. By the time the parent container is fully realized, all its
	 * components will be valid.
	 * 
	 * @return <code>true</code> if the component is valid, <code>false</code>
	 *         otherwise
	 * @see #validate
	 * @see #invalidate
	 */
	public boolean isValid();

	/**
	 * Invalidates this component. This component and all parents above it are
	 * marked as needing to be laid out. This method can be called often, so it
	 * needs to execute quickly.
	 * 
	 * @see #validate
	 */
	public void invalidate();

	/**
	 * Mark this component has a valid layout.
	 * 
	 * @see #invalidate
	 */
	public void validate();

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
