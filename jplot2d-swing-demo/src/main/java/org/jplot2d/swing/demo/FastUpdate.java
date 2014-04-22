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

import org.jplot2d.element.ElementFactory;
import org.jplot2d.data.ArrayPair;
import org.jplot2d.data.XYGraphData;
import org.jplot2d.element.Axis;
import org.jplot2d.element.Layer;
import org.jplot2d.element.Plot;
import org.jplot2d.element.XYGraph;
import org.jplot2d.renderer.AsyncImageRenderer;
import org.jplot2d.renderer.AsyncImageRenderer.RendererCancelPolicy;
import org.jplot2d.sizing.FillContainerSizeMode;
import org.jplot2d.swing.ImageRendererFactory;
import org.jplot2d.swing.JPlot2DComponent;
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

		JPlot2DFrame frame = new JPlot2DFrame(plot);
		frame.setSize(640, 480);
		frame.setVisible(true);

		JPlot2DComponent comp = frame.getPlotComponent();
		ImageRendererFactory irf = new JPlot2DComponent.DefaultImageRendererFactory(frame.getPlotComponent()) {
			@Override
			public AsyncImageRenderer createImageRenderer() {
				AsyncImageRenderer r = super.createImageRenderer();
				r.setRendererCancelPolicy(RendererCancelPolicy.CANCEL_AFTER_NEWER_DONE);
				return r;
			}
		};
		comp.setImageRendererFactory(irf);

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
		XYGraph graph0 = ElementFactory.getInstance().createXYGraph(ap0, "lineA");
		XYGraph graph1 = ElementFactory.getInstance().createXYGraph(ap1, "lineB");
		graph0.setLineVisible(false);
		graph0.setSymbolVisible(true);
		graph0.setSymbolShape(SymbolShape.SQUARE);
		graph1.setLineVisible(false);
		graph1.setSymbolVisible(true);
		graph1.setSymbolShape(SymbolShape.SQUARE);

		// graph0.setCacheable(true);
		// graph1.setCacheable(true);

		Layer layer0 = ElementFactory.getInstance().createLayer();
		layer0.addGraph(graph0);
		layer0.addGraph(graph1);
		plot.addLayer(layer0, xaxis.getTickManager().getAxisTransform(), yaxis.getTickManager().getAxisTransform());

		for (int i = 0; i < n; i++) {
			double[] xa = new double[1];
			double[] ya = new double[1];

			xa[0] = i;
			ya[0] = i;
			ap0 = ap0.append(xa, ya, 1);
			graph0.setData(new XYGraphData(ap0));
			xa[0] = i;
			ya[0] = n - i - 1;
			ap1 = ap1.append(xa, ya, 1);
			graph1.setData(new XYGraphData(ap1));

		}
	}
}
