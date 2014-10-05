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

import org.jplot2d.env.PropertyInfo;
import org.jplot2d.util.Insets2D;

/**
 * @author Jingjing Li
 * 
 */
public class Insets2DProperty extends PropertyDescriptorAdapter<Insets2D> {

	private Property<?>[] subProperties;

	private Double _top, _left, _bottom, _right;

	public Insets2DProperty(PropertyInfo descriptor) {
		super(descriptor);
		initSubProperties();
	}

	public Property<?>[] getSubProperties() {
		return subProperties;
	}

	public void readFromObject(Object object) {
		super.readFromObject(object);

		if (getValue() != null) {
			_top = getValue().getTop();
			_left = getValue().getLeft();
			_bottom = getValue().getBottom();
			_right = getValue().getRight();
		}
	}

	private void updateValue() {
		if (_top != null && _left != null & _bottom != null && _right != null) {
			setValue(new Insets2D.Double(_top, _left, _bottom, _right));
		}
		if (_top == null && _left == null & _bottom == null && _right == null) {
			setValue(null);
		}
	}

	private void initSubProperties() {
		// initial sub-properties
		subProperties = new Property[4];

		subProperties[0] = new SubProperty<Double>(this) {

			public String getName() {
				return "top";
			}

			public Class<Double> getType() {
				return Double.class;
			}

			public Double getValue() {
				return _top;
			}

			public void setValue(Double top) {
				_top = top;
				updateValue();
			}

		};

		subProperties[1] = new SubProperty<Double>(this) {

			public String getName() {
				return "left";
			}

			public Class<Double> getType() {
				return Double.class;
			}

			public Double getValue() {
				return _left;
			}

			public void setValue(Double left) {
				_left = left;
				updateValue();
			}

		};

		subProperties[2] = new SubProperty<Double>(this) {

			public String getName() {
				return "bottom";
			}

			public Class<Double> getType() {
				return Double.class;
			}

			public Double getValue() {
				return _bottom;
			}

			public void setValue(Double bottom) {
				_bottom = bottom;
				updateValue();
			}

		};

		subProperties[3] = new SubProperty<Double>(this) {

			public String getName() {
				return "right";
			}

			public Class<Double> getType() {
				return Double.class;
			}

			public Double getValue() {
				return _right;
			}

			public void setValue(Double right) {
				_right = right;
				updateValue();
			}

		};

	}
}
