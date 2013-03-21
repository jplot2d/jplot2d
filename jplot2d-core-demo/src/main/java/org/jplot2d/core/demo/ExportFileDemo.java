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
package org.jplot2d.core.demo;

import java.awt.Color;
import java.io.FileNotFoundException;

import org.jplot2d.element.ElementFactory;
import org.jplot2d.element.Axis;
import org.jplot2d.element.Layer;
import org.jplot2d.element.Plot;
import org.jplot2d.element.Title;
import org.jplot2d.element.XYGraph;
import org.jplot2d.env.RenderEnvironment;
import org.jplot2d.renderer.EpsExporter;
import org.jplot2d.renderer.PdfExporter;
import org.jplot2d.renderer.PngFileExporter;
import org.jplot2d.util.LineHatchPaint;

/**
 * Demo for exporting plot to pdf and png file.
 * 
 * @author Jingjing Li
 */
public class ExportFileDemo {

	/**
	 * @param args
	 * @throws FileNotFoundException
	 */
	public static void main(String[] args) throws FileNotFoundException {

		int n = 21;
		double[] x = new double[n];
		double[] y = new double[n];
		for (int i = 0; i < n; i++) {
			x[i] = i / 10.0 - 1;
			y[i] = x[i] * x[i];
		}

		ElementFactory ef = ElementFactory.getInstance();

		Plot plot = ef.createPlot();
		plot.setScale(0.5);
		Title title = ef.createTitle("Title");
		title.setFontScale(2);
		plot.addTitle(title);

		Axis xaxis = ef.createAxis();
		Axis yaxis = ef.createAxis();
		xaxis.getTitle().setText("x axis");
		plot.addXAxis(xaxis);
		yaxis.getTitle().setText("y axis");
		plot.addYAxis(yaxis);

		XYGraph graph = ef.createXYGraph(x, y, "lineA");
		float[] dash = new float[] { 6, 2, 1, 2, 1, 2 };
		graph.setLineStroke(ef.createStroke(1, dash));
		graph.setFillEnabled(true);
		LineHatchPaint hatch = new LineHatchPaint(Color.RED, ElementFactory.getInstance()
				.createStroke(1, dash), 45, 10);
		graph.setFillPaint(hatch);
		Layer layer0 = ef.createLayer();
		layer0.addGraph(graph);
		plot.addLayer(layer0, xaxis.getTickManager().getAxisTransform(), yaxis.getTickManager()
				.getAxisTransform());

		RenderEnvironment env = new RenderEnvironment(false);
		env.setPlot(plot);
		env.exportPlot(new EpsExporter("/tmp/demo.eps"));
		env.exportPlot(new PdfExporter("/tmp/demo.pdf"));
		env.exportPlot(new PngFileExporter("/tmp/demo.png"));
	}
}
