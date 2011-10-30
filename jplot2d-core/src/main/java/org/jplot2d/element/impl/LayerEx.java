package org.jplot2d.element.impl;

import org.jplot2d.element.Layer;

public interface LayerEx extends Layer, ContainerEx {

	public PlotEx getParent();

	public AxisTransformEx getXAxisTransform();

	public AxisTransformEx getYAxisTransform();

	public GraphPlotterEx[] getGraphPlotters();

	public int indexOf(GraphPlotterEx plotter);

	public MarkerEx[] getMarkers();

	public int indexOf(MarkerEx marker);

	/**
	 * Called by {@link PlotEx} to notify the paper transform is changed.
	 */
	public void parentPhysicalTransformChanged();

	/**
	 * Called by {@link AxisTransformEx} to notify that x/y transform is changed.
	 */
	public void transformChanged();

}
