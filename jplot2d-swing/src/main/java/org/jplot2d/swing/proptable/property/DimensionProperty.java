/**
 * Copyright 2010-2014 Jingjing Li.
 *
 * This file is part of jplot2d.
 *
 * jplot2d is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or any later version.
 *
 * jplot2d is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Lesser Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with jplot2d. If not, see <http://www.gnu.org/licenses/>.
 */
package org.jplot2d.swing.proptable.property;

import java.awt.Dimension;

import org.jplot2d.env.PropertyInfo;

/**
 * @author Jingjing Li
 * 
 */
public class DimensionProperty extends PropertyDescriptorAdapter<Dimension> {

	private Property<?>[] subProperties;

	public DimensionProperty(PropertyInfo descriptor) {
		super(descriptor);
		initSubProperties();
	}

	public Property<?>[] getSubProperties() {
		return subProperties;
	}

	private void setWidth(int width) {
		setValue(new Dimension(width, getValue().height));
	}

	private void setHeight(int height) {
		setValue(new Dimension(getValue().width, height));
	}

	private void initSubProperties() {
		// initial sub-properties
		subProperties = new Property[2];
		subProperties[0] = new SubProperty<Integer>(this) {

			public String getName() {
				return "width";
			}

			public Class<Integer> getType() {
				return Integer.class;
			}

			public Integer getValue() {
				return DimensionProperty.this.getValue().width;
			}

			public void setValue(Integer width) {
				setWidth(width);
			}

		};

		subProperties[1] = new SubProperty<Integer>(this) {

			public String getName() {
				return "height";
			}

			public Class<Integer> getType() {
				return Integer.class;
			}

			public Integer getValue() {
				return DimensionProperty.this.getValue().height;
			}

			public void setValue(Integer height) {
				setHeight(height);
			}

		};

	}
}
