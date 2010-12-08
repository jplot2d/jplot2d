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
package org.jplot2d.axtrans;

import org.jplot2d.axtrans.AbstractAxisTransform;
import org.jplot2d.axtrans.AxisTransform;
import org.jplot2d.element.AxisTickTransform;
import org.jplot2d.util.Range2D;

/**
 * Concatenate AxisTransform with AxisTickTransform
 * 
 * @author Jingjing Li
 * 
 */
public class AxisTickPaperTransform extends AbstractAxisTransform {

	private AxisTransform axisTransform;

	private AxisTickTransform tickTransform;

	public AxisTickPaperTransform(AxisTransform axisTransform,
			AxisTickTransform tickTransform) {
		this.axisTransform = axisTransform;
		this.tickTransform = tickTransform;
	}

	public AxisTransform getAxisTransform() {
		return axisTransform;
	}

	public void setAxisTransform(AxisTransform axisTransform) {
		this.axisTransform = axisTransform;
	}

	public AxisTickTransform getTickform() {
		return tickTransform;
	}

	public void setTickTransform(AxisTickTransform transformer) {
		tickTransform = transformer;
	}

	public void setMainRangeU(Range2D wrange) {
		axisTransform.setRangeU(wrange);
	}

	public void invertMainAxis() {
		axisTransform.invert();
	}

	void computeTransform() {
		axisTransform.computeTransform();
	}

	@Override
	public AxisTickPaperTransform copy() {
		return new AxisTickPaperTransform(axisTransform.copy(), tickTransform);
	}

	public Range2D getRangeU() {
		Range2D mainRange = axisTransform.getRangeU();
		double start = tickTransform.transformUser2Tick(mainRange.getStart());
		double end = tickTransform.transformUser2Tick(mainRange.getEnd());
		return new Range2D.Double(start, mainRange.isStartIncluded(), end,
				mainRange.isEndIncluded());
	}

	public void setRangeU(Range2D wrange) {
		// FIXME
		throw new java.lang.UnsupportedOperationException();
	}

	@Override
	public double getScale() {
		return axisTransform.getScale();
	}

	@Override
	public double getTransP(double u) {
		return axisTransform.getTransP(tickTransform.transformTick2User(u));
	}

	@Override
	public double getTransU(double p) {
		return tickTransform.transformUser2Tick(axisTransform.getTransU(p));
	}

	@Override
	public Range2D getRangeP() {
		return axisTransform.getRangeP();
	}

	@Override
	public void setRangeP(Range2D prange) {
		axisTransform.setRangeP(prange);
	}

}
