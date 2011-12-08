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
package org.jplot2d.swing.demo;

import javax.swing.JFrame;

import org.jplot2d.element.ElementFactory;
import org.jplot2d.data.ArrayPair;
import org.jplot2d.data.XYGraph;
import org.jplot2d.element.Axis;
import org.jplot2d.element.Layer;
import org.jplot2d.element.Plot;
import org.jplot2d.element.XYGraphPlotter;
import org.jplot2d.sizing.FillContainerSizeMode;
import org.jplot2d.swing.JPlot2DFrame;
import org.jplot2d.util.Range;
import org.jplot2d.util.SymbolShape;

public class FastUpdate {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		int n = 1000;

		Plot plot = ElementFactory.getInstance().createPlot();
		plot.setSizeMode(new FillContainerSizeMode(1));

		JFrame frame = new JPlot2DFrame(plot);
		frame.setSize(640, 480);
		frame.setVisible(true);

		Axis xaxis = ElementFactory.getInstance().createAxis();
		Axis yaxis = ElementFactory.getInstance().createAxis();
		xaxis.getTitle().setText("x axis");
		xaxis.getTickManager().setRange(new Range.Double(0, n));
		plot.addXAxis(xaxis);
		yaxis.getTitle().setText("y axis");
		yaxis.getTickManager().setRange(new Range.Double(0, n));
		plot.addYAxis(yaxis);

		ArrayPair ap0 = new ArrayPair(new double[] { 0 }, new double[] { 0 });
		ArrayPair ap1 = new ArrayPair(new double[] { 0 }, new double[] { n });
		XYGraphPlotter plotter0 = ElementFactory.getInstance()
				.createXYGraphPlotter(ap0, "lineA");
		XYGraphPlotter plotter1 = ElementFactory.getInstance()
				.createXYGraphPlotter(ap1, "lineB");
		plotter0.setLineVisible(false);
		plotter0.setSymbolVisible(true);
		plotter0.setSymbolShape(SymbolShape.CIRCLE);
		plotter1.setLineVisible(false);
		plotter1.setSymbolVisible(true);
		plotter1.setSymbolShape(SymbolShape.CIRCLE);

		Layer layer0 = ElementFactory.getInstance().createLayer();
		layer0.addGraphPlotter(plotter0);
		layer0.addGraphPlotter(plotter1);
		plot.addLayer(layer0, xaxis.getTickManager().getAxisTransform(), yaxis
				.getTickManager().getAxisTransform());

		for (int i = 0; i < n; i++) {
			double[] xa = new double[1];
			double[] ya = new double[1];

			xa[0] = i;
			ya[0] = i;
			ap0 = ap0.append(xa, ya, 1);
			plotter0.setGraph(new XYGraph(ap0));
			xa[0] = i;
			ya[0] = n - i - 1;
			ap1 = ap1.append(xa, ya, 1);
			plotter1.setGraph(new XYGraph(ap1));

		}
	}
}
