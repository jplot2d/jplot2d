package org.jplot2d.element.impl;

import java.awt.Graphics2D;
import java.awt.geom.Point2D;
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
public interface ComponentEx extends Component, ElementEx {

	public ContainerEx getParent();

	/*
	 * Returns <code>true</code> if the component is movable by mouse dragging.
	 * Only selectable component can be movable.
	 * 
	 * @return <code>true</code> if movable
	 */
	public boolean isMovable();

	/**
	 * Set the movable property.
	 */
	public void setMovable(boolean movable);

	/**
	 * Moves this plot component to a new location. The origin of the new
	 * location is specified by point <code>p</code>. Point2D <code>p</code> is
	 * given in the parent's physical coordinate space.
	 * 
	 * @param p
	 *            the point defining the origin of the new location, given in
	 *            the coordinate space of this component's parent
	 */
	public void setLocation(Point2D loc);

	public void setLocation(double locX, double locY);

	/**
	 * Returns a map to tell why this component cannot be removed from its
	 * container. The key is the moored element, required by its value.
	 * 
	 * @return a map. The key is the element required by its value.
	 */
	public Map<Element, Element> getMooringMap();

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

	/**
	 * Returns <code>true</code> when this component need redraw.
	 * 
	 * @return the redraw status
	 */
	public boolean isRedrawNeeded();

	/**
	 * Clear the redraw needed flag.
	 */
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
	public ComponentEx deepCopy(Map<ElementEx, ElementEx> orig2copyMap);

	public void copyFrom(ComponentEx src, Map<ElementEx, ElementEx> orig2copyMap);

}