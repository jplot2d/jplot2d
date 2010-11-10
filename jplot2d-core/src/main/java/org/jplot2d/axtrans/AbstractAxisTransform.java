/*
 * This file is part of Herschel Common Science System (HCSS).
 * Copyright 2001-2010 Herschel Science Ground Segment Consortium
 *
 * HCSS is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of
 * the License, or (at your option) any later version.
 *
 * HCSS is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General
 * Public License along with HCSS.
 * If not, see <http://www.gnu.org/licenses/>.
 */
/*
 * $Id: AxisTransform.java,v 1.4 2010/02/02 10:16:48 hsclib Exp $
 */
package org.jplot2d.axtrans;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import org.jplot2d.util.Range2D;

/**
 * Transform between user value and paper value.
 */
public abstract class AbstractAxisTransform implements Cloneable {

	protected PropertyChangeSupport _changes;

	protected boolean _valid = false;

	/**
	 * Create a non-transformable AxisTranform.
	 */
	protected AbstractAxisTransform() {
		_changes = new PropertyChangeSupport(this);
	}

	protected Object clone() throws CloneNotSupportedException {
		AbstractAxisTransform clone = (AbstractAxisTransform) super.clone();
		clone._changes = new PropertyChangeSupport(clone);
		return clone;
	}

	/**
	 * Returns if this AxisTransform is ready to transform values. Only
	 * <code>true</code> after both physical range and world range are set
	 * properly.
	 * 
	 * @return <code>true</code> if this AxisTransform is ready to transform
	 *         values.
	 */
	public boolean isValid() {
		return _valid;
	}

	/**
	 * Set physical coordinate range. <BR>
	 * <B>Property Change:</B> <code>rangeP</code>.
	 * 
	 * @param p1
	 *            minimum value, physical coordinates
	 * @param p2
	 *            maximum value, physical coordinates
	 * @see LinearAxisTransform
	 */
	public void setRangeP(double p1, double p2) {
		setRangeP(new Range2D.Double(p1, p2));
	}

	/**
	 * Set physical coordinate range.
	 * 
	 * @param prange
	 *            physical coordinate range
	 * @see LinearAxisTransform
	 */
	public abstract void setRangeP(Range2D prange);

	/**
	 * Get the physical coordinate range.
	 * 
	 * @return physical coordinate range
	 */
	public abstract Range2D getRangeP();

	/**
	 * Set the world coordinate range for <code>Range2D</code> values.
	 * 
	 * @param urange
	 *            world coordinate range
	 * @see Range2D
	 * @see LinearAxisTransform
	 */
	public abstract void setRangeU(Range2D urange);

	/**
	 * Get the world coordinate range for double values.
	 * 
	 * @return world range
	 * @see Range2D
	 */
	public abstract Range2D getRangeU();

	/**
	 * Create a copy of this <code>AxisTransform</code>.
	 * 
	 * @return the copy
	 */
	public abstract AbstractAxisTransform copy();

	/**
	 * Transform from world to physical coordinate.
	 * 
	 * @param u
	 *            world value
	 * @return physical value
	 */
	public abstract double getTransP(double u);

	/**
	 * Transform from physical to world coordinate.
	 * 
	 * @param p
	 *            physical value
	 * @return world value
	 */
	public abstract double getTransU(double p);

	public abstract double getScale();

	/**
	 * Add listener to changes in <code>LinearTransform</code> properties.
	 */
	public void addPropertyChangeListener(PropertyChangeListener listener) {
		_changes.addPropertyChangeListener(listener);
	}

	/**
	 * Remove listener from list.
	 */
	public void removePropertyChangeListener(PropertyChangeListener listener) {
		_changes.removePropertyChangeListener(listener);
	}

}
