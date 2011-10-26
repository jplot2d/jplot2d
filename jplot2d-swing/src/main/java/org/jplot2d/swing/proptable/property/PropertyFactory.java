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
import java.awt.Point;
import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;

import org.jplot2d.axtype.AxisType;
import org.jplot2d.env.PropertyInfo;
import org.jplot2d.util.Insets2D;
import org.jplot2d.util.Range;
import org.jplot2d.util.SymbolShape;

/**
 * A factory to create MainProperty from PropertyInfo
 * 
 * @author Jingjing Li
 * 
 */
public class PropertyFactory {

	private PropertyFactory() {

	}

	public static MainProperty<?> createProperty(PropertyInfo descriptor) {

		Class<?> type = descriptor.getPropertyType();

		if (SymbolShape.class.isAssignableFrom(type)) {
			return new SymbolShapeProperty(descriptor);
		}
		if (AxisType.class.isAssignableFrom(type)) {
			return new AxisTypeProperty(descriptor);
		}

		if (Point.class.isAssignableFrom(type)) {
			return new PointProperty(descriptor);
		}
		if (Point2D.class.isAssignableFrom(type)) {
			return new Point2DProperty(descriptor);
		}

		if (Dimension.class.isAssignableFrom(type)) {
			return new DimensionProperty(descriptor);
		}
		if (Dimension2D.class.isAssignableFrom(type)) {
			return new Dimension2DProperty(descriptor);
		}

		if (Insets2D.class.isAssignableFrom(type)) {
			return new Insets2DProperty(descriptor);
		}

		if (Range.class.isAssignableFrom(type)) {
			return new RangeProperty(descriptor);
		}

		return new PropertyDescriptorAdapter(descriptor);
	}

}
