/**
 * Copyright 2010, 2011 Jingjing Li.
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
package org.jplot2d.axtype;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import org.jplot2d.axtick.TickAlgorithm;
import org.jplot2d.transform.AxisTickTransform;
import org.jplot2d.transform.TransformType;
import org.jplot2d.util.Range;

/**
 * An axis type defines a viewport axis transform type(LINEAR/LOGARITHMIC) and a preferred tick
 * algorithm.
 * 
 * @author Jingjing Li
 * 
 */
public abstract class AxisType {

	private static final Map<String, AxisType> axisTypeMap = Collections
			.synchronizedMap(new LinkedHashMap<String, AxisType>());

	public static final AxisType NUMBER = NumberAxisType.getInstance();

	public static final AxisType DATE = DateAxisType.getInstance();

	public static final AxisType RIGHT_ASCENSION = RightAscensionAxisType.getInstance();

	public static final AxisType DECLINATION = DeclinationAxisType.getInstance();

	private final String name;

	public AxisType(String name) {
		this.name = name;
		axisTypeMap.put(name, this);
	}

	public String getName() {
		return name;
	}

	public abstract boolean canSupport(TransformType txfType);

	public abstract TransformType getDefaultTransformType();

	/**
	 * The boundary of the axis nature. The returned boundary is never inverted. For circular axis,
	 * this boundary is valid limit for data values, not for displayed label values.
	 * 
	 * @return
	 */
	public abstract Range getBoundary(TransformType txfType);

	/**
	 * @return the default world range when the axis contains no valid data
	 */
	public abstract Range getDefaultWorldRange(TransformType txfType);

	/**
	 * Returns a TickAlgorithm by the given transform type and tick transform, or false if the given
	 * tick transform is not allowed.
	 * 
	 * @param txfType
	 *            the transform type
	 * @param tickTransform
	 *            the tick transform, can be <code>null</code>
	 * @return a TickAlgorithm
	 */
	public abstract TickAlgorithm getTickAlgorithm(TransformType txfType,
			AxisTickTransform tickTransform);

	/**
	 * Some axis has a circular nature, such as angle. This range is a canonical range that all
	 * values should displayed in the range.
	 * 
	 * @return a range to represent this axis' circular nature, or
	 *         <code>null<code> if this axis is not circular
	 */
	public Range getCircularRange() {
		return null;
	}

	public String toString() {
		return getName();
	}

	public static AxisType valueOf(String name) {
		return axisTypeMap.get(name);
	}

	/**
	 * Returns all axis types in a array.
	 * 
	 * @return all axis types
	 */
	public static AxisType[] values() {
		return axisTypeMap.values().toArray(new AxisType[0]);
	}

}
