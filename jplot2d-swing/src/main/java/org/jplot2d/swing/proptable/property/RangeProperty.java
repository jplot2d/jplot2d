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
import org.jplot2d.util.Range;

/**
 * @author Jingjing Li
 * 
 */
public class RangeProperty extends PropertyDescriptorAdapter<Range> {

	private Property<?>[] subProperties;

	private Double _start, _end;

	public RangeProperty(PropertyInfo descriptor) {
		super(descriptor);
		initSubProperties();
	}

	public Property<?>[] getSubProperties() {
		return subProperties;
	}

	private void setStart(Double start) {
		_start = start;
		if (getValue() != null) {
			_end = getValue().getEnd();
		}
		if (_start != null && _end != null) {
			setValue(new Range.Double(_start, _end));
		}
		if (_start == null && _end == null) {
			setValue(null);
		}
	}

	private void setEnd(Double end) {
		_end = end;
		if (getValue() != null) {
			_start = getValue().getStart();
		}
		if (_start != null && _end != null) {
			setValue(new Range.Double(_start, _end));
		}
		if (_start == null && _end == null) {
			setValue(null);
		}
	}

	private void initSubProperties() {
		// initial sub-properties
		subProperties = new Property[2];
		subProperties[0] = new SubProperty<Double>() {

			public String getName() {
				return "start";
			}

			public Class<Double> getType() {
				return Double.class;
			}

			public boolean isEditable() {
				return RangeProperty.this.isEditable();
			}

			public Double getValue() {
				Range r = RangeProperty.this.getValue();
				if (r == null) {
					return _start;
				} else {
					return r.getStart();
				}
			}

			public void setValue(Double width) {
				setStart(width);
			}

		};

		subProperties[1] = new SubProperty<Double>() {

			public String getName() {
				return "end";
			}

			public Class<Double> getType() {
				return Double.class;
			}

			public boolean isEditable() {
				return RangeProperty.this.isEditable();
			}

			public Double getValue() {
				Range r = RangeProperty.this.getValue();
				if (r == null) {
					return _end;
				} else {
					return r.getEnd();
				}
			}

			public void setValue(Double end) {
				setEnd(end);
			}

		};

	}
}