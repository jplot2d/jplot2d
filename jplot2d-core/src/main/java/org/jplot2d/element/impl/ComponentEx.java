package org.jplot2d.element.impl;

import java.awt.Color;
import java.awt.Font;
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
	 * Returns the effective color of this component.
	 * 
	 * @return the effective color of this component
	 */
	public Color getEffectiveColor();

	public String getEffectiveFontName();

	public int getEffectiveFontStyle();

	public float getEffectiveFontSize();

	/**
	 * Returns the effective font of this component.
	 * 
	 * @return the effective font of this component
	 */
	public Font getEffectiveFont();

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
	 *            to the Graphics2D drawing. The transformation of this
	 *            component is not applied.
	 */
	void draw(Graphics2D g);

	/**
	 * Create a deep copy of this component. The parent of the copy are not set.
	 * 
	 * @param orig2copyMap
	 *            original element to copy map
	 * @return a deep copy of this component
	 */
	public ComponentEx deepCopy(Map<ElementEx, ElementEx> orig2copyMap);

	/**
	 * Copies all properties from given src to this component. Notice properties
	 * of sub-components are not copied over.
	 * 
	 * @param src
	 * @param orig2copyMap
	 */
	public void copyFrom(ComponentEx src, Map<ElementEx, ElementEx> orig2copyMap);

}