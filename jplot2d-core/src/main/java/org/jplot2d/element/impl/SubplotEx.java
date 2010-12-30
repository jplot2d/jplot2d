package org.jplot2d.element.impl;

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

}
