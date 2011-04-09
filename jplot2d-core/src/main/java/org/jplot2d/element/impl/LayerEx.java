package org.jplot2d.element.impl;

import org.jplot2d.element.Layer;

public interface LayerEx extends Layer, ContainerEx {

	public PlotEx getParent();

	public AxisRangeManagerEx getXRangeManager();

	public AxisRangeManagerEx getYRangeManager();

	public GraphPlotterEx[] getGraphPlotters();

	public void parentPhysicalTransformChanged();

	/**
	 * notify from its parent subplot that this layer's location is changed.
	 */
	public void updateLocation();

}
