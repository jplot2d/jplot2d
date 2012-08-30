/**
 * Copyright 2010-2012 Jingjing Li.
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
package org.jplot2d.element;

import static org.jplot2d.util.TestUtils.checkPropertyInfoNames;
import static org.jplot2d.util.TestUtils.checkCollecionOrder;
import static org.jplot2d.util.TestUtils.checkRange;
import static org.junit.Assert.*;

import org.jplot2d.data.ArrayPair;
import org.jplot2d.data.XYGraphData;
import org.jplot2d.env.InterfaceInfo;
import org.jplot2d.env.PlotEnvironment;
import org.jplot2d.transform.TransformType;
import org.jplot2d.util.Range;
import org.junit.Test;

/**
 * Those test cases test methods on Title.
 * 
 * @author Jingjing Li
 * 
 */
public class AxisTransformTest {

	@Test
	public void testInterfaceInfo() {
		InterfaceInfo iinfo = InterfaceInfo.loadInterfaceInfo(AxisTransform.class);
		checkCollecionOrder(iinfo.getPropertyInfoGroupMap().keySet(), "Axis Transform");
		checkPropertyInfoNames(iinfo.getPropertyInfoGroupMap().get("Axis Transform"), "type",
				"transform", "inverted", "autoMargin", "marginFactor", "coreRange", "range");
	}

	@Test
	public void testSwitchTransformType() {
		Plot plot = ElementFactory.getInstance().createPlot();

		Axis xaxis = ElementFactory.getInstance().createAxis();
		xaxis.getTitle().setText("x axis");
		plot.addXAxis(xaxis);

		PlotEnvironment env = new PlotEnvironment(false);
		// RenderEnvironment env = new RenderEnvironment(false);
		env.setPlot(plot);

		AxisTransform axf = xaxis.getTickManager().getAxisTransform();

		assertTrue(axf.getLockGroup().isAutoRange());

		// Notice: All axes contain no valid data, range set to default range.
		axf.setTransform(TransformType.LOGARITHMIC);
		checkRange(axf.getRange(), 0.1, 10);
		assertTrue(axf.getLockGroup().isAutoRange());

		axf.setTransform(TransformType.LINEAR);
		checkRange(axf.getRange(), 0.1, 10);
		assertTrue(axf.getLockGroup().isAutoRange());

		axf.setRange(new Range.Double(2.0, 5.0));
		assertFalse(axf.getLockGroup().isAutoRange());
		axf.setTransform(TransformType.LOGARITHMIC);
		checkRange(axf.getRange(), 2.0, 5.0);
		assertFalse(axf.getLockGroup().isAutoRange());

		axf.setTransform(TransformType.LINEAR);
		axf.setRange(new Range.Double(-2.0, 5.0));
		assertFalse(axf.getLockGroup().isAutoRange());
		// Notice: All axes contain no valid data, range set to default range.
		axf.setTransform(TransformType.LOGARITHMIC);
		checkRange(axf.getRange(), 0.1, 10);
		assertFalse(axf.getLockGroup().isAutoRange());

	}

	/**
	 * Test find nice range
	 */
	@Test
	public void testSwitchTransformType2() {
		Plot plot = ElementFactory.getInstance().createPlot();

		Axis xaxis = ElementFactory.getInstance().createAxis();
		xaxis.getTitle().setText("x axis");
		plot.addXAxis(xaxis);
		Axis yaxis = ElementFactory.getInstance().createAxis();
		yaxis.getTitle().setText("y axis");
		plot.addYAxis(yaxis);

		XYGraphData graphData = new XYGraphData(new ArrayPair(new double[] { 0, 2, 4, 6, 8, 10 },
				new double[] { 0, 0.6, 1, 0.4, 0.5, 0.8 }));
		XYGraph graph = ElementFactory.getInstance().createXYGraph(graphData);
		Layer layer0 = ElementFactory.getInstance().createLayer();

		layer0.addGraph(graph);
		plot.addLayer(layer0, xaxis, yaxis);

		PlotEnvironment env = new PlotEnvironment(false);
		env.setPlot(plot);

		AxisTransform axf = xaxis.getTickManager().getAxisTransform();
		AxisTransform yaxf = yaxis.getTickManager().getAxisTransform();

		axf.setTransform(TransformType.LOGARITHMIC);
		checkRange(axf.getRange(), 1, 100);
		assertTrue(axf.getLockGroup().isAutoRange());
		checkRange(yaxf.getRange(), 0.3, 1.1);
		assertTrue(yaxf.getLockGroup().isAutoRange());
	}

}
