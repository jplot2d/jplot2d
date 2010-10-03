package org.jplot2d.element.impl;

import java.util.Map;

import org.jplot2d.element.Element;
import org.jplot2d.element.Subplot;

public interface SubplotEx extends Subplot, ContainerEx {

	public AxisEx[] getAxes();
	
	public SubplotEx deepCopy(Map<Element, Element> orig2copyMap);

}
