package org.jplot2d.element.impl;

import java.awt.Graphics2D;
import java.util.Map;

import org.jplot2d.element.Component;
import org.jplot2d.element.Element;

/**
 * Extended Component that all impl need to implements. Those additional methods
 * is required by environment.
 * 
 * @author Jingjing Li
 * 
 */
public interface ComponentEx extends Component {

	public ContainerEx getParent();

	/**
	 * Sets the parent of this component
	 * 
	 * @param parent
	 *            the new parent
	 */
	public void setParent(ContainerEx parent);

	/**
	 * Determines whether this component is valid. A component is valid when it
	 * is correctly sized and positioned within its parent container and all its
	 * children are also valid. In order to account for peers' size
	 * requirements, components are invalidated before they are first shown on
	 * the screen. By the time the parent container is fully realized, all its
	 * components will be valid.
	 * 
	 * @return <code>true</code> if the component is valid, <code>false</code>
	 *         otherwise
	 * @see #validate
	 * @see #invalidate
	 */
	public boolean isValid();

	/**
	 * Invalidates this component. This component and all parents above it are
	 * marked as needing to be laid out. This method can be called often, so it
	 * needs to execute quickly.
	 * 
	 * @see #validate
	 */
	public void invalidate();

	/**
	 * Mark this component has a valid layout.
	 * 
	 * @see #invalidate
	 */
	public void validate();

	/**
	 * Mark this component need to redraw.
	 */
	public void redraw();

	public boolean isRedrawNeeded();

	public void clearRedrawNeeded();

	/**
	 * Draw this component only. All its children is not drawn.
	 * 
	 * @param g
	 *            to the Graphics2D drawing
	 */
	void draw(Graphics2D g);

	/**
	 * @param orig2copyMap
	 *            original element to copy map
	 * @return
	 */
	public ComponentEx deepCopy(Map<Element, Element> orig2copyMap);

}