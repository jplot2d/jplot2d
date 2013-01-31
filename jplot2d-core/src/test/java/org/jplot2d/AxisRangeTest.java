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
package org.jplot2d;

import static org.jplot2d.util.TestUtils.*;

import org.jplot2d.element.*;
import org.jplot2d.env.PlotEnvironment;
import org.junit.Test;

/**
 * Those test cases test methods on Title.
 * 
 * @author Jingjing Li
 * 
 */
public class AxisRangeTest {

	@Test
	public void testReAutoRangeWhenAddingGraph() {
		ElementFactory ef = ElementFactory.getInstance();

		Plot plot = ef.createPlot();

		Axis xaxis = ef.createAxis();
		Axis yaxis = ef.createAxis();
		xaxis.getTitle().setText("x axis");
		plot.addXAxis(xaxis);
		yaxis.getTitle().setText("y axis");
		plot.addYAxis(yaxis);

		Layer layer0 = ef.createLayer();
		plot.addLayer(layer0, xaxis, yaxis);

		PlotEnvironment env = new PlotEnvironment(false);
		env.setPlot(plot);

		checkAxisTickRange(xaxis, -1, 1);
		checkAxisTickRange(yaxis, -1, 1);

		XYGraph graph0 = ef.createXYGraph(new double[] { 0, 1 }, new double[] { 0, 1 }, "lineA");
		layer0.addGraph(graph0);
		checkAxisTickRange(xaxis, -0.1, 1.1);
		checkAxisTickRange(yaxis, -0.1, 1.1);

		XYGraph graph1 = ef.createXYGraph(new double[] { 2, 3 }, new double[] { 2, 3 }, "lineB");
		layer0.addGraph(graph1);
		checkAxisTickRange(xaxis, -0.5, 3.5);
		checkAxisTickRange(yaxis, -0.5, 3.5);
	}

	@Test
	public void testReAutoRangeWhenAddingLayer() {
		ElementFactory ef = ElementFactory.getInstance();

		Plot plot = ef.createPlot();

		Axis xaxis = ef.createAxis();
		Axis yaxis = ef.createAxis();
		xaxis.getTitle().setText("x axis");
		plot.addXAxis(xaxis);
		yaxis.getTitle().setText("y axis");
		plot.addYAxis(yaxis);

		PlotEnvironment env = new PlotEnvironment(false);
		env.setPlot(plot);

		checkAxisTickRange(xaxis, -1, 1);
		checkAxisTickRange(yaxis, -1, 1);

		Layer layer0 = ef.createLayer();
		XYGraph graph0 = ef.createXYGraph(new double[] { 0, 1 }, new double[] { 0, 1 }, "lineA");
		layer0.addGraph(graph0);
		plot.addLayer(layer0, xaxis, yaxis);
		checkAxisTickRange(xaxis, -0.1, 1.1);
		checkAxisTickRange(yaxis, -0.1, 1.1);

		Layer layer1 = ef.createLayer();
		XYGraph graph1 = ef.createXYGraph(new double[] { 2, 3 }, new double[] { 2, 3 }, "lineB");
		layer1.addGraph(graph1);
		plot.addLayer(layer1, xaxis, yaxis);
		checkAxisTickRange(xaxis, -0.5, 3.5);
		checkAxisTickRange(yaxis, -0.5, 3.5);
	}
}
