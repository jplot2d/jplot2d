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
/**
 * 
 */
package org.jplot2d.swing.proptable.property;

import java.awt.geom.Point2D;

import org.jplot2d.env.PropertyInfo;

/**
 * @author Jingjing Li
 * 
 */
public class Point2DProperty extends PropertyDescriptorAdapter<Point2D> {

	private Property<?>[] subProperties;

	private Double _x, _y;

	public Point2DProperty(PropertyInfo descriptor) {
		super(descriptor);
		initSubProperties();
	}

	public Property<?>[] getSubProperties() {
		return subProperties;
	}

	private void setX(Double x) {
		_x = x;
		if (getValue() != null) {
			_y = getValue().getY();
		}
		if (_x != null && _y != null) {
			setValue(new Point2D.Double(_x, _y));
		}
		if (_x == null && _y == null) {
			setValue(null);
		}
	}

	private void setY(Double y) {
		_y = y;
		if (getValue() != null) {
			_x = getValue().getX();
		}
		if (_x != null && _y != null) {
			setValue(new Point2D.Double(_x, _y));
		}
		if (_x == null && _y == null) {
			setValue(null);
		}
	}

	private void initSubProperties() {
		// initial sub-properties
		subProperties = new Property[2];
		subProperties[0] = new SubProperty<Double>(this) {

			public String getName() {
				return "x";
			}

			public Class<Double> getType() {
				return Double.class;
			}

			public Double getValue() {
				Point2D p = Point2DProperty.this.getValue();
				if (p == null) {
					return null;
				} else {
					return p.getX();
				}
			}

			public void setValue(Double x) {
				setX(x);
			}

		};

		subProperties[1] = new SubProperty<Double>(this) {

			public String getName() {
				return "y";
			}

			public Class<Double> getType() {
				return Double.class;
			}

			public Double getValue() {
				Point2D p = Point2DProperty.this.getValue();
				if (p == null) {
					return null;
				} else {
					return p.getY();
				}
			}

			public void setValue(Double y) {
				setY(y);
			}

		};

	}
}
