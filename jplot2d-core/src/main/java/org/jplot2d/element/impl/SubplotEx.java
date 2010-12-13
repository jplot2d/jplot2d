package org.jplot2d.element.impl;

import java.awt.geom.Dimension2D;

import org.jplot2d.element.Subplot;

public interface SubplotEx extends Subplot, ContainerEx {

	public SubplotEx getParent();

	/**
	 * Sets the paper size of this container.
	 * 
	 * @param paper
	 *            size
	 */
	public void setSize(Dimension2D size);

	public int indexOf(LayerEx layer);

	public LayerEx[] getLayers();

	public int indexOfXViewportAxis(ViewportAxisEx axisGroup);

	public int indexOfYViewportAxis(ViewportAxisEx axisGroup);

	public ViewportAxisEx[] getXViewportAxes();

	public ViewportAxisEx[] getYViewportAxes();

	public int indexOf(SubplotEx subplot);

	public SubplotEx[] getSubplots();

	public void parentPhysicalTransformChanged();

}
