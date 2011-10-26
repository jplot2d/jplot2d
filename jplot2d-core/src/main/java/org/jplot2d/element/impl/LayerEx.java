package org.jplot2d.element.impl;

import org.jplot2d.element.Layer;

public interface LayerEx extends Layer, ContainerEx {

	public PlotEx getParent();

	public AxisTransformEx getXRangeManager();

	public AxisTransformEx getYRangeManager();

	public GraphPlotterEx[] getGraphPlotters();

	public int indexOf(GraphPlotterEx plotter);

	public MarkerEx[] getMarkers();
	
	public int indexOf(MarkerEx marker);

	public void parentPhysicalTransformChanged();

}
