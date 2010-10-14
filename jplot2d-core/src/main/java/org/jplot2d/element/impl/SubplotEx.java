package org.jplot2d.element.impl;

import java.util.Map;

import org.jplot2d.element.Subplot;

public interface SubplotEx extends Subplot, ContainerEx {

	public PlotEx getParent();

	public int indexOf(LayerEx layer);

	public LayerEx[] getLayers();

	public int indexOfXAxis(AxisEx axis);

	public int indexOfYAxis(AxisEx axis);

	public AxisEx[] getXAxes();

	public AxisEx[] getYAxes();

	public void plotPhysicalTransformChanged();

	public SubplotEx deepCopy(Map<ElementEx, ElementEx> orig2copyMap);

}
