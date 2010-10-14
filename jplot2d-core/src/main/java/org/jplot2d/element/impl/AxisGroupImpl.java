/**
 * Copyright 2010 Jingjing Li.
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

import java.util.ArrayList;
import java.util.List;

public class AxisGroupImpl extends ElementImpl implements AxisGroupEx {

	private boolean autoRange = true;

	private List<MainAxisEx> axes = new ArrayList<MainAxisEx>();

	public MainAxisEx[] getParents() {
		return getAxes();
	}

	public MainAxisEx[] getAxes() {
		return axes.toArray(new MainAxisEx[axes.size()]);
	}

	public void addMainAxis(MainAxisEx axis) {
		axes.add(axis);
		if (axes.size() == 1) {
			parent = axis;
		} else {
			parent = null;
		}
	}

	public void removeMainAxis(MainAxisEx axis) {
		axes.remove(axis);
		if (axes.size() == 1) {
			parent = axis;
		} else {
			parent = null;
		}
	}

	public boolean isAutoRange() {
		return autoRange;
	}

	public void setAutoRange(boolean autoRange) {
		this.autoRange = autoRange;
	}

	public void zoomRange(double start, double end) {
		// TODO Auto-generated method stub

	}

	public void autoRange() {
		// TODO Auto-generated method stub

	}

	public void clearAutoRangePending() {
		// TODO Auto-generated method stub

	}

	public boolean isAutoRangePending() {
		// TODO Auto-generated method stub
		return false;
	}

}
