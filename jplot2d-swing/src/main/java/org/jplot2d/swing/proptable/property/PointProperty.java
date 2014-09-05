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

import java.awt.Point;

import org.jplot2d.env.PropertyInfo;

/**
 * @author Jingjing Li
 * 
 */
public class PointProperty extends PropertyDescriptorAdapter<Point> {

	private Property<?>[] subProperties;

	public PointProperty(PropertyInfo descriptor) {
		super(descriptor);
		initSubProperties();
	}

	public Property<?>[] getSubProperties() {
		return subProperties;
	}

	private void setX(int x) {
		setValue(new Point(x, getValue().y));
	}

	private void setY(int y) {
		setValue(new Point(getValue().x, y));
	}

	private void initSubProperties() {
		// initial sub-properties
		subProperties = new Property[2];
		subProperties[0] = new SubProperty<Integer>(this) {

			public String getName() {
				return "x";
			}

			public Class<Integer> getType() {
				return Integer.class;
			}

			public Integer getValue() {
				return PointProperty.this.getValue().x;
			}

			public void setValue(Integer x) {
				setX(x);
			}

		};

		subProperties[1] = new SubProperty<Integer>(this) {

			public String getName() {
				return "y";
			}

			public Class<Integer> getType() {
				return Integer.class;
			}

			public Integer getValue() {
				return PointProperty.this.getValue().y;
			}

			public void setValue(Integer y) {
				setY(y);
			}

		};

	}
}
