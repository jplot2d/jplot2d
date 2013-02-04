/**
 * Copyright 2010-2013 Jingjing Li.
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
package org.jplot2d;

import static org.jplot2d.util.TestUtils.*;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.jplot2d.data.ArrayPair;
import org.jplot2d.data.XYGraphData;
import org.jplot2d.element.*;
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
public class AxisRangeTest {

	private static final ElementFactory factory = ElementFactory.getInstance();

	@Test
	public void testReAutoRangeWhenAddingGraph() {
		Plot plot = factory.createPlot();

		Axis xaxis = factory.createAxis();
		Axis yaxis = factory.createAxis();
		xaxis.getTitle().setText("x axis");
		plot.addXAxis(xaxis);
		yaxis.getTitle().setText("y axis");
		plot.addYAxis(yaxis);

		Layer layer0 = factory.createLayer();
		plot.addLayer(layer0, xaxis, yaxis);

		PlotEnvironment env = new PlotEnvironment(false);
		env.setPlot(plot);

		checkAxisTickRange(xaxis, -1, 1);
		checkAxisTickRange(yaxis, -1, 1);

		XYGraph graph0 = factory.createXYGraph(new double[] { 0, 1 }, new double[] { 0, 1 }, "lineA");
		layer0.addGraph(graph0);
		checkAxisTickRange(xaxis, -0.1, 1.1);
		checkAxisTickRange(yaxis, -0.1, 1.1);

		XYGraph graph1 = factory.createXYGraph(new double[] { 2, 3 }, new double[] { 2, 3 }, "lineB");
		layer0.addGraph(graph1);
		checkAxisTickRange(xaxis, -0.5, 3.5);
		checkAxisTickRange(yaxis, -0.5, 3.5);

		graph1.setData(new XYGraphData(new ArrayPair(new double[] { 4, 5 }, new double[] { 4, 5 })));
		checkAxisTickRange(xaxis, -0.5, 5.5);
		checkAxisTickRange(yaxis, -0.5, 5.5);

	}

	@Test
	public void testReAutoRangeWhenAddingLayer() {
		Plot plot = factory.createPlot();

		Axis xaxis = factory.createAxis();
		Axis yaxis = factory.createAxis();
		xaxis.getTitle().setText("x axis");
		plot.addXAxis(xaxis);
		yaxis.getTitle().setText("y axis");
		plot.addYAxis(yaxis);

		PlotEnvironment env = new PlotEnvironment(false);
		env.setPlot(plot);

		checkAxisTickRange(xaxis, -1, 1);
		checkAxisTickRange(yaxis, -1, 1);

		Layer layer0 = factory.createLayer();
		XYGraph graph0 = factory.createXYGraph(new double[] { 0, 1 }, new double[] { 0, 1 }, "lineA");
		layer0.addGraph(graph0);
		plot.addLayer(layer0, xaxis, yaxis);
		checkAxisTickRange(xaxis, -0.1, 1.1);
		checkAxisTickRange(yaxis, -0.1, 1.1);

		Layer layer1 = factory.createLayer();
		XYGraph graph1 = factory.createXYGraph(new double[] { 2, 3 }, new double[] { 2, 3 }, "lineB");
		layer1.addGraph(graph1);
		plot.addLayer(layer1, xaxis, yaxis);
		checkAxisTickRange(xaxis, -0.5, 3.5);
		checkAxisTickRange(yaxis, -0.5, 3.5);

		graph1.setData(new XYGraphData(new ArrayPair(new double[] { 4, 5 }, new double[] { 4, 5 })));
		checkAxisTickRange(xaxis, -0.5, 5.5);
		checkAxisTickRange(yaxis, -0.5, 5.5);

	}

	@Test
	public void testSwitchTransformType() {
		Plot plot = factory.createPlot();

		Axis xaxis = factory.createAxis();
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
		Plot plot = factory.createPlot();

		Axis xaxis = factory.createAxis();
		xaxis.getTitle().setText("x axis");
		plot.addXAxis(xaxis);
		Axis yaxis = factory.createAxis();
		yaxis.getTitle().setText("y axis");
		plot.addYAxis(yaxis);

		XYGraphData graphData = new XYGraphData(new ArrayPair(new double[] { 0, 2, 4, 6, 8, 10 }, new double[] { 0,
				0.6, 1, 0.4, 0.5, 0.8 }));
		XYGraph graph = factory.createXYGraph(graphData);
		Layer layer0 = factory.createLayer();

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
