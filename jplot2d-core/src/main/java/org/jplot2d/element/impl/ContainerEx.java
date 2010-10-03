package org.jplot2d.element.impl;

import java.util.Map;

import org.jplot2d.element.Container;
import org.jplot2d.element.Element;

public interface ContainerEx extends Container, ComponentEx {

	public ContainerEx deepCopy(Map<Element, Element> orig2copyMap);

}
