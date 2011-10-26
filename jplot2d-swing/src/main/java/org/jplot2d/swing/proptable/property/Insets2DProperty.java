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

import org.jplot2d.env.PropertyInfo;
import org.jplot2d.util.Insets2D;

/**
 * @author Jingjing Li
 * 
 */
public class Insets2DProperty extends PropertyDescriptorAdapter<Insets2D> {

	private Property<?>[] subProperties;

	public Insets2DProperty(PropertyInfo descriptor) {
		super(descriptor);
		initSubProperties();
	}

	public Property<?>[] getSubProperties() {
		return subProperties;
	}

	private void setTop(double top) {
		setValue(new Insets2D(top, getValue().getLeft(), getValue().getBottom(), getValue()
				.getRight()));
	}

	private void setLeft(double left) {
		setValue(new Insets2D(getValue().getTop(), left, getValue().getBottom(), getValue()
				.getRight()));
	}

	private void setBottom(double bottom) {
		setValue(new Insets2D(getValue().getTop(), getValue().getLeft(), bottom, getValue()
				.getRight()));
	}

	private void setRight(double right) {
		setValue(new Insets2D(getValue().getTop(), getValue().getLeft(), getValue().getBottom(),
				right));
	}

	private void initSubProperties() {
		// initial sub-properties
		subProperties = new Property[4];

		subProperties[0] = new SubProperty<Double>() {

			public String getName() {
				return "top";
			}

			public Class<Double> getType() {
				return Double.class;
			}

			public boolean isEditable() {
				return Insets2DProperty.this.isEditable();
			}

			public Double getValue() {
				return Insets2DProperty.this.getValue().getTop();
			}

			public void setValue(Double top) {
				setTop(top);
			}

		};

		subProperties[1] = new SubProperty<Double>() {

			public String getName() {
				return "left";
			}

			public Class<Double> getType() {
				return Double.class;
			}

			public boolean isEditable() {
				return Insets2DProperty.this.isEditable();
			}

			public Double getValue() {
				return Insets2DProperty.this.getValue().getLeft();
			}

			public void setValue(Double left) {
				setLeft(left);
			}

		};

		subProperties[2] = new SubProperty<Double>() {

			public String getName() {
				return "bottom";
			}

			public Class<Double> getType() {
				return Double.class;
			}

			public boolean isEditable() {
				return Insets2DProperty.this.isEditable();
			}

			public Double getValue() {
				return Insets2DProperty.this.getValue().getBottom();
			}

			public void setValue(Double bottom) {
				setBottom(bottom);
			}

		};

		subProperties[3] = new SubProperty<Double>() {

			public String getName() {
				return "right";
			}

			public Class<Double> getType() {
				return Double.class;
			}

			public boolean isEditable() {
				return Insets2DProperty.this.isEditable();
			}

			public Double getValue() {
				return Insets2DProperty.this.getValue().getRight();
			}

			public void setValue(Double right) {
				setRight(right);
			}

		};

	}
}
