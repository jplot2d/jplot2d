package org.jplot2d.element.impl;

import org.jplot2d.element.Container;

public interface ContainerEx extends Container, ComponentEx {

	/**
	 * Returns the number of component of this container. Returns 0 if this
	 * component has no children.
	 * 
	 * @return the number of component of this container
	 */
	public int getComponentCount();

	/**
	 * Returns the component at index <code>index</code> in this container.
	 * 
	 * @return the child at index <code>index</code>
	 */
	public ComponentEx getComponent(int index);

	/**
	 * Returns the index of component in this container. If <code>comp</code> is
	 * <code>null</code>, returns -1. If <code>comp</code> don't belong to this
	 * component, returns -1.
	 * 
	 * @param comp
	 *            the component
	 * @return the index of the component in this container
	 */
	public int getIndexOfComponent(ComponentEx comp);

}
