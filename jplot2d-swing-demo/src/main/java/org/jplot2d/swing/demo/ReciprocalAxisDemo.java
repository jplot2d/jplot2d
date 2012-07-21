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
package org.jplot2d.swing.demo;

import javax.swing.JFrame;

import org.jplot2d.data.ArrayPair;
import org.jplot2d.data.XYGraphData;
import org.jplot2d.element.ElementFactory;
import org.jplot2d.element.Axis;
import org.jplot2d.element.Layer;
import org.jplot2d.element.Plot;
import org.jplot2d.element.Title;
import org.jplot2d.element.XYGraph;
import org.jplot2d.sizing.AutoPackSizeMode;
import org.jplot2d.swing.JPlot2DFrame;
import org.jplot2d.transform.ReciprocalAxisTickTransform;
import org.jplot2d.transform.TransformType;

/**
 * @author Jingjing Li
 * 
 */
public class ReciprocalAxisDemo {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Plot plot = ElementFactory.getInstance().createPlot();
		plot.setSizeMode(new AutoPackSizeMode());

		Title title = ElementFactory.getInstance().createTitle("Reciprocal Axis Demo");
		title.setFontScale(2);
		plot.addTitle(title);

		JFrame frame = new JPlot2DFrame(plot);
		frame.setSize(640, 480);
		frame.setVisible(true);

		Axis xaxis = ElementFactory.getInstance().createAxis();
		xaxis.getTitle().setText("x axis");
		plot.addXAxis(xaxis);
		Axis yaxis = ElementFactory.getInstance().createAxis();
		yaxis.getTitle().setText("y axis");
		plot.addYAxis(yaxis);

		Axis xaux = ElementFactory.getInstance().createAxis();
		xaux.getTitle().setText("1/x axis");
		plot.addXAxis(xaux);
		xaux.getTickManager().setAxisTransform(xaxis.getTickManager().getAxisTransform());
		xaux.getTickManager().setTickTransform(new ReciprocalAxisTickTransform(2));

		XYGraphData graphData = new XYGraphData(new ArrayPair(new double[] { 0, 2, 4, 6, 8, 10 },
				new double[] { 0, 0.6, 1, 0.4, 0.5, 0.8 }));
		XYGraph graph = ElementFactory.getInstance().createXYGraph(graphData);
		Layer layer0 = ElementFactory.getInstance().createLayer();

		layer0.addGraph(graph);
		plot.addLayer(layer0, xaxis, yaxis);

		// xaxis.getTickManager().getAxisTransform().setTransform(TransformType.LOGARITHMIC);
	}
}
