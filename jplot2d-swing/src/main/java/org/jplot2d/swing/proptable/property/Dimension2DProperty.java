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

import java.awt.geom.Dimension2D;

import org.jplot2d.env.PropertyInfo;
import org.jplot2d.util.DoubleDimension2D;

/**
 * @author Jingjing Li
 * 
 */
public class Dimension2DProperty extends PropertyDescriptorAdapter<Dimension2D> {

	private Property<?>[] subProperties;

	private Double _w, _h;

	public Dimension2DProperty(PropertyInfo descriptor) {
		super(descriptor);
		initSubProperties();
	}

	public Property<?>[] getSubProperties() {
		return subProperties;
	}

	public void readFromObject(Object object) {
		super.readFromObject(object);

		if (getValue() != null) {
			_w = getValue().getWidth();
			_h = getValue().getHeight();
		}
	}

	private void updateValue() {
		if (_w != null && _h != null) {
			setValue(new DoubleDimension2D(_w, _h));
		}
		if (_w == null && _h == null) {
			setValue(null);
		}
	}

	private void initSubProperties() {
		// initial sub-properties
		subProperties = new Property[2];
		subProperties[0] = new SubProperty<Double>(this) {

			public String getName() {
				return "width";
			}

			public Class<Double> getType() {
				return Double.class;
			}

			public Double getValue() {
				return _w;
			}

			public void setValue(Double width) {
				_w = width;
				updateValue();
			}

		};

		subProperties[1] = new SubProperty<Double>(this) {

			public String getName() {
				return "height";
			}

			public Class<Double> getType() {
				return Double.class;
			}

			public Double getValue() {
				return _h;
			}

			public void setValue(Double height) {
				_h = height;
				updateValue();
			}

		};

	}
}
