package org.jplot2d.element.impl;

import java.awt.geom.Rectangle2D;

import org.jplot2d.element.Plot;
import org.jplot2d.util.WarningReceiver;

public interface PlotEx extends Plot, ContainerEx {

	public PlotEx getParent();

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

	public boolean isRerenderNeeded();

	public void clearRerenderNeeded();

	/**
	 * Sets a WarningReceiver to receive all warning messages.
	 * 
	 * @param warningReceiver
	 */
	public void setWarningReceiver(WarningReceiver warningReceiver);

	/**
	 * Apply all pending changes on this plot. After this method is called, all
	 * axis range and layout are valid.
	 */
	public void commit();

	public SubplotMarginEx getMargin();

	public LegendEx getLegend();

	public int indexOf(TitleEx title);

	public TitleEx[] getTitles();

	public int indexOf(LayerEx layer);

	public LayerEx[] getLayers();

	public int indexOfXAxis(AxisEx axis);

	public int indexOfYAxis(AxisEx axis);

	public AxisEx[] getXAxes();

	public AxisEx[] getYAxes();

	public int indexOf(PlotEx subplot);

	public PlotEx[] getSubplots();

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

	public void childPreferredContentSizeChanged();

}
