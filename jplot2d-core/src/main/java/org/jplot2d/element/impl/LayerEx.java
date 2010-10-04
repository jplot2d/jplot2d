package org.jplot2d.element.impl;

import java.util.Map;

import org.jplot2d.element.Element;
import org.jplot2d.element.Layer;

public interface LayerEx extends Layer, ContainerEx {

	public LayerEx deepCopy(Map<Element, Element> orig2copyMap);

}
