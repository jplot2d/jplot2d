package org.jplot2d.element.impl;

import org.jplot2d.element.Layer;

public interface LayerEx extends Layer, ContainerEx {

	public SubplotEx getParent();

	public ViewportAxisEx getXViewportAxis();

	public ViewportAxisEx getYViewportAxis();

	public GraphPlotterEx[] getGraphPlotters();

	public void parentPhysicalTransformChanged();

	/**
	 * This method is called when a layer is moved form a subplot.
	 */
	public void detachAxes();

}
