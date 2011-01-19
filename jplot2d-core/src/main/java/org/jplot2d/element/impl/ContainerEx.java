package org.jplot2d.element.impl;

import org.jplot2d.element.Container;

public interface ContainerEx extends Container, ComponentEx {

	/**
	 * Returns <code>true</code> if this container can contribute visible parts
	 * to its parent.
	 * 
	 * @return the indicator
	 */
	public boolean canContributeToParent();

}
