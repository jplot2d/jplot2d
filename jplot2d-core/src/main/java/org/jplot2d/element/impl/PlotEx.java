package org.jplot2d.element.impl;

import java.util.Map;

import org.jplot2d.element.Plot;

public interface PlotEx extends Plot, SubplotEx {

	public PlotEx deepCopy(Map<ElementEx, ElementEx> orig2copyMap);

}
