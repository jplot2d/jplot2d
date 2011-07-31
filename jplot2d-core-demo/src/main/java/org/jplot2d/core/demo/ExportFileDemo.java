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

import java.io.FileNotFoundException;

import org.jplot2d.element.ElementFactory;
import org.jplot2d.element.Axis;
import org.jplot2d.element.Layer;
import org.jplot2d.element.Plot;
import org.jplot2d.element.Title;
import org.jplot2d.env.RenderEnvironment;
import org.jplot2d.renderer.PdfExporter;
import org.jplot2d.renderer.PngFileExporter;

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
		Plot plot = ElementFactory.getInstance().createPlot();

		Title title = ElementFactory.getInstance().createTitle("Title");
		title.setFontScale(2);
		plot.addTitle(title);

		Axis xaxis = ElementFactory.getInstance().createAxis();
		Axis yaxis = ElementFactory.getInstance().createAxis();
		xaxis.getTitle().setText("x axis");
		plot.addXAxis(xaxis);
		yaxis.getTitle().setText("y axis");
		plot.addYAxis(yaxis);

		Layer layer0 = ElementFactory.getInstance().createLayer(
				new double[] { 0, 0.1, 0.2 }, new double[] { 0, 0.1, 0.4 },
				"lineA");
		Layer layer1 = ElementFactory.getInstance().createLayer(
				new double[] { 0, 0.2, 0.4 }, new double[] { 0, 0.3, 0.4 },
				"lineB");
		plot.addLayer(layer0, xaxis.getTickManager().getRangeManager(), yaxis
				.getTickManager().getRangeManager());
		plot.addLayer(layer1, xaxis.getTickManager().getRangeManager(), yaxis
				.getTickManager().getRangeManager());

		RenderEnvironment env = new RenderEnvironment(false);
		env.setPlot(plot);
		env.exportPlot(new PdfExporter("demo.pdf"));
		env.exportPlot(new PngFileExporter("demo.png"));
	}

}
