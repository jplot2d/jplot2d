package org.jplot2d.element.impl;

import java.awt.Color;
import java.awt.Font;
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
	 * Returns a map to tell why this component cannot be removed from its
	 * container. The key is the moored element, required by its value.
	 * 
	 * @return a map. The key is the element required by its value.
	 */
	public Map<Element, Element> getMooringMap();

	/**
	 * Returns <code>true</code> if this component can contribute visible parts
	 * to its parent. Notice cacheable component does not contribute to its
	 * parent.
	 * 
	 * @return the indicator
	 */
	public boolean canContributeToParent();

	/**
	 * Returns <code>true</code> if this component can contribute visible parts
	 * to plot rendering artifact.
	 * 
	 * @return the indicator
	 */
	public boolean canContribute();

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

	public void parentEffectiveColorChanged();

	public void parentEffectiveFontChanged();

	/**
	 * Called when setColor() or parentEffectiveColorChanged() change the
	 * effective color of this component.
	 */
	public void thisEffectiveColorChanged();

	/**
	 * Called when setFontXxx() or parentEffectiveFontChanged() change the
	 * effective font of this component.
	 */
	public void thisEffectiveFontChanged();

	/**
	 * Returns <code>true</code> when this component need to be redrawn. The
	 * result only apply to cacheable component.
	 * 
	 * @return the redraw status
	 */
	public boolean isRedrawNeeded();

	/**
	 * Mark this component need to be redrawn. If this component is not
	 * cacheable, this method will called on its parent.
	 */
	public void redraw();

	/**
	 * Clear the redraw needed flag.
	 */
	public void clearRedrawNeeded();

	/**
	 * Mark the plot artifact need to be re-rendered. This method is called on
	 * its parent, until reach the top plot.
	 */
	public void rerender();

	/**
	 * Draw this component only. All its children is not drawn.
	 * 
	 * @param g
	 *            to the Graphics2D drawing. The transformation of this
	 *            component is not applied.
	 */
	public void draw(Graphics2D g);

	/**
	 * Create a deep copy of this component. The parent of the copy are not set.
	 * 
	 * @param orig2copyMap
	 *            original element to copy map
	 * @return a deep copy of this component
	 */
	public ComponentEx copyStructure(Map<ElementEx, ElementEx> orig2copyMap);

}