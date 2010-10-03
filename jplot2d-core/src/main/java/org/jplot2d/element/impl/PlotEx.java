package org.jplot2d.element.impl;

import java.util.Map;

import org.jplot2d.element.Element;
import org.jplot2d.element.Plot;

public interface PlotEx extends Plot, ContainerEx {

	public SubplotEx[] getSubPlots();

	public PlotEx deepCopy(Map<Element, Element> orig2copyMap);

}
