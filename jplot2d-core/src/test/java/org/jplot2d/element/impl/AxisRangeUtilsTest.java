/*
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
package org.jplot2d.element.impl;

import static org.jplot2d.util.TestUtils.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.Collection;

import org.jplot2d.axtype.AxisType;
import org.jplot2d.data.ArrayPair;
import org.jplot2d.data.XYGraphData;
import org.jplot2d.element.XYGraph;
import org.jplot2d.transform.LinearNormalTransform;
import org.jplot2d.transform.TransformType;
import org.jplot2d.util.Range;
import org.junit.Test;

/**
 * @author Jingjing Li
 * 
 */
public class AxisRangeUtilsTest {

	@Test
	public void testValidateNormalRangeForSwitchToLog() {

		double[] x = new double[] { 0, 1, 10 };
		double[] y = new double[] { 0, 1, 10 };
		XYGraphData data = new XYGraphData(new ArrayPair(x, y));
		XYGraph graph = new XYGraphImpl();
		graph.setData(data);

		LayerEx layer = new LayerImpl();
		layer.addGraph(graph);

		AxisTransformEx axf = mock(AxisTransformEx.class);
		when(axf.getNormalTransform()).thenReturn(new LinearNormalTransform(0, 10));
		when(axf.getType()).thenReturn(AxisType.NUMBER);
		when(axf.getTransform()).thenReturn(TransformType.LOGARITHMIC);
		when(axf.getMarginFactor()).thenReturn(AxisTransformImpl.DEFAULT_MARGIN_FACTOR);
		when(axf.getLayers()).thenReturn(new LayerEx[] { layer });

		layer.setXAxisTransform(axf);

		Collection<AxisTransformEx> axes = new ArrayList<>();
		axes.add(axf);

		Range range = new Range.Double(0.0, 1.0);

		Range r = AxisRangeUtils.validateNormalRange(range, axes, true);
		checkRange(r, 0.09305720409297, 1.0);
	}

}
