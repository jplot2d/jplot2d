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
package org.jplot2d.element.impl;

import java.util.Map;

import org.jplot2d.axtrans.NormalTransform;
import org.jplot2d.element.AxisRangeLockGroup;
import org.jplot2d.util.Range2D;

/**
 * @author Jingjing Li
 * 
 */
public interface AxisRangeLockGroupEx extends AxisRangeLockGroup, ElementEx,
		Joinable {

	public AxisRangeManagerEx getParent();

	public AxisRangeManagerEx[] getRangeManagers();

	public int indexOfRangeManager(AxisRangeManagerEx rangeManager);

	public void addRangeManager(AxisRangeManagerEx rangeManager);

	public void removeRangeManager(AxisRangeManagerEx rangeManager);

	public AxisRangeManagerEx getPrimaryAxis();

	/**
	 * Force re-autorange this axis group. This method is called when layer data
	 * set changes, layer attach/detach to an axis of this group, or axis type
	 * change
	 */
	public void reAutoRange();

	/**
	 * Calculate auto range when necessary.
	 */
	public void calcAutoRange();

	public void zoomVirtualRange(Range2D range,
			Map<AxisRangeManagerEx, NormalTransform> vtMap);

	/**
	 * Validate axes range after axis type or axis transform type changed.
	 */
	public void validateAxesRange();

}