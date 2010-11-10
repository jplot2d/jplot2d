package org.jplot2d.element.impl;

import java.util.Map;

import org.jplot2d.element.Container;

public interface ContainerEx extends Container, ComponentEx {

	public ContainerEx deepCopy(Map<ElementEx, ElementEx> orig2copyMap);

}
