package org.jplot2d.element.impl;

import java.awt.geom.Dimension2D;

import org.jplot2d.element.Plot;
import org.jplot2d.notice.Notifier;

public interface PlotEx extends Plot, ContainerEx {

	public PlotEx getParent();

	/**
	 * Determines whether this plot is valid. A plot is valid when it is correctly sized and
	 * positioned within its parent plot and all its axes, titles, legend and subplot are also
	 * valid.
	 * 
	 * @return <code>true</code> if the component is valid, <code>false</code> otherwise
	 * @see #validate
	 * @see #invalidate
	 */
	public boolean isValid();

	/**
	 * Invalidates this component. This component and all parents above it are marked as needing to
	 * be laid out. This method can be called often, so it needs to execute quickly.
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

	public Notifier getNotifier();

	/**
	 * Sets a notifier to receive all notice messages.
	 * 
	 * @param notifier
	 */
	public void setNotifier(Notifier notifier);

	/**
	 * Apply all pending changes on this plot. After this method is called, all axis range and
	 * layout are valid.
	 */
	public void commit();

	public PlotMarginEx getMargin();

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
	 * Sets the content size by layout director. All layers in this plot have the same viewport
	 * size.
	 * <p>
	 * The layout manager guarantee this method is called after setting plot margin, and no matter
	 * if the content size is changed.
	 * 
	 * @param csize
	 *            the content size
	 */
	public void setContentSize(Dimension2D csize);

	/**
	 * Returns the contents constraint of this plot.
	 * 
	 * @return the contents constraint
	 */
	public Dimension2D getContentConstrant();

	/**
	 * Impose contents constraint on this plot. This method is called by a plot's layout director,
	 * when laying out subplots of the plot.
	 * 
	 * @param constraint
	 *            the contents constraint
	 */
	public void setContentConstrant(Dimension2D constraint);

	public void childPreferredContentSizeChanged();

}
