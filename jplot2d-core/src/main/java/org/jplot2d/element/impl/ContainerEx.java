package org.jplot2d.element.impl;

import java.awt.geom.Dimension2D;

import org.jplot2d.element.Container;

public interface ContainerEx extends Container, ComponentEx {

	/**
	 * Sets the paper size of this container.
	 * 
	 * @param paper
	 *            size
	 */
	public void setSize(Dimension2D size);

	public void setSize(double width, double height);

	/**
	 * Returns <code>true</code> if this container can contribute visible parts
	 * to its parent.
	 * 
	 * @return the indicator
	 */
	public boolean canContributeToParent();

}
