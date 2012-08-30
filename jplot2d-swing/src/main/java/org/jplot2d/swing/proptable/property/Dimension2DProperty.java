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

import java.awt.geom.Dimension2D;

import org.jplot2d.env.PropertyInfo;
import org.jplot2d.util.DoubleDimension2D;

/**
 * @author Jingjing Li
 * 
 */
public class Dimension2DProperty extends PropertyDescriptorAdapter<Dimension2D> {

	private Property<?>[] subProperties;

	public Dimension2DProperty(PropertyInfo descriptor) {
		super(descriptor);
		initSubProperties();
	}

	public Property<?>[] getSubProperties() {
		return subProperties;
	}

	private void setWidth(double width) {
		setValue(new DoubleDimension2D(width, getValue().getHeight()));
	}

	private void setHeight(double height) {
		setValue(new DoubleDimension2D(getValue().getWidth(), height));
	}

	private void initSubProperties() {
		// initial sub-properties
		subProperties = new Property[2];
		subProperties[0] = new SubProperty<Double>() {

			public String getName() {
				return "width";
			}

			public Class<Double> getType() {
				return Double.class;
			}

			public boolean isEditable() {
				return Dimension2DProperty.this.isEditable();
			}

			public Double getValue() {
				return Dimension2DProperty.this.getValue().getWidth();
			}

			public void setValue(Double width) {
				setWidth(width);
			}

		};

		subProperties[1] = new SubProperty<Double>() {

			public String getName() {
				return "height";
			}

			public Class<Double> getType() {
				return Double.class;
			}

			public boolean isEditable() {
				return Dimension2DProperty.this.isEditable();
			}

			public Double getValue() {
				return Dimension2DProperty.this.getValue().getHeight();
			}

			public void setValue(Double height) {
				setHeight(height);
			}

		};

	}
}
