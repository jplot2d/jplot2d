package org.jplot2d.element.impl;

import java.util.Map;

import org.jplot2d.element.Plot;

public interface PlotEx extends Plot, ContainerEx {

	public int indexOf(SubplotEx subplot);

	public SubplotEx[] getSubplots();

	public PlotEx deepCopy(Map<ElementEx, ElementEx> orig2copyMap);

}
