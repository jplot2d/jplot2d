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
		subProperties[0] = new SubProperty<Integer>() {

			public String getName() {
				return "width";
			}

			public Class<Integer> getType() {
				return Integer.class;
			}

			public boolean isEditable() {
				return DimensionProperty.this.isEditable();
			}

			public Integer getValue() {
				return DimensionProperty.this.getValue().width;
			}

			public void setValue(Integer width) {
				setWidth(width);
			}

		};

		subProperties[1] = new SubProperty<Integer>() {

			public String getName() {
				return "height";
			}

			public Class<Integer> getType() {
				return Integer.class;
			}

			public boolean isEditable() {
				return DimensionProperty.this.isEditable();
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
