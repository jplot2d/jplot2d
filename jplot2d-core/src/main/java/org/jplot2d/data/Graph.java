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
package org.jplot2d.data;

import org.jplot2d.util.Range;

public interface Graph {

	public Graph setXBoundary(Range xboundary);

	public Graph setYBoundary(Range yboundary);

	public XYGraph setBoundary(Range xboundary, Range yboundary);

	/**
	 * Returns x range. If there is no valid data in x data, the empty property of returned Range2D
	 * will be set to <code>true</code>
	 */
	public Range getXRange();

	/**
	 * Returns y range. If there is no valid data in y data, the empty property of returned Range2D
	 * will be set to <code>true</code>
	 */
	public Range getYRange();

	public boolean hasPointOutsideXBounds();

	public boolean hasPointOutsideYBounds();

}