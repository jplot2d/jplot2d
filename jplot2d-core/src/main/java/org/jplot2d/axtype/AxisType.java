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
package org.jplot2d.axtype;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import org.jplot2d.axtrans.TransformType;
import org.jplot2d.axtick.TickAlgorithm;
import org.jplot2d.util.Range2D;

/**
 * An axis type defines a viewport axis transform type(LINEAR/LOGARITHMIC) and a
 * preferred tick algorithm.
 * 
 * @author Jingjing Li
 * 
 */
public abstract class AxisType {

	private static final Map<String, AxisType> axisTypeMap = Collections
			.synchronizedMap(new LinkedHashMap<String, AxisType>());

	public static final AxisType LINEAR = new LinearAxisType();

	public static final AxisType LOG = new LogAxisType();

	public static final AxisType DATE = new DateAxisType();

	public static final AxisType RIGHT_ASCENSION = new RightAscensionAxisType();

	public static final AxisType DECLINATION = new DeclinationAxisType();

	private final String name;

	private final TransformType transformType;

	private final TickAlgorithm tickAlgorithm;

	public AxisType(String name, TransformType att, TickAlgorithm ta) {
		this.name = name;
		transformType = att;
		tickAlgorithm = ta;
		axisTypeMap.put(name, this);
	}

	public String getName() {
		return name;
	}

	public TransformType getTransformType() {
		return transformType;
	}

	public TickAlgorithm getTickAlgorithm() {
		return tickAlgorithm;
	}

	/**
	 * The returned boundary never is inverted.
	 * 
	 * @return
	 */
	public abstract Range2D getBoundary();

	/**
	 * @return the default world range when auto-range a axis that contains no
	 *         valid data
	 */
	public abstract Range2D getDefaultWorldRange();

	/**
	 * 
	 * @param values
	 *            the values in an array object
	 * @return a array of canonical values
	 */
	public double getCircle() {
		return Double.POSITIVE_INFINITY;
	}

	public String toString() {
		return getName();
	}

	public static AxisType valueOf(String name) {
		return axisTypeMap.get(name);
	}

	public static AxisType[] values() {
		return axisTypeMap.values().toArray(new AxisType[0]);
	}

}
