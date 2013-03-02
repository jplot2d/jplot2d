/**
 * Copyright 2013 Jingjing Li.
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

import org.jplot2d.element.Axis;
import org.jplot2d.element.ElementFactory;
import org.jplot2d.element.ImageGraph;
import org.jplot2d.element.Layer;
import org.jplot2d.element.Plot;
import org.jplot2d.sizing.AutoPackSizeMode;
import org.jplot2d.swing.JPlot2DFrame;

/**
 * @author Jingjing Li
 * 
 */
public class ImageFloat2dDemo {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		plot(0.4);
		plot(0.5);
		plot(0.6);
	}
	
	public static void plot(double gain) {
		Plot plot = ElementFactory.getInstance().createPlot();
		plot.setSizeMode(new AutoPackSizeMode());

		JFrame frame = new JPlot2DFrame(plot);
		frame.setSize(640, 480);
		frame.setVisible(true);

		Axis xaxis = ElementFactory.getInstance().createAxis();
		xaxis.getTitle().setText("x axis");
		plot.addXAxis(xaxis);
		Axis yaxis = ElementFactory.getInstance().createAxis();
		yaxis.getTitle().setText("y axis");
		plot.addYAxis(yaxis);

		// create a float2d array
		float[][] f2d = new float[1024][1024];
		for (int i = 0; i < 1024; i++) {
			for (int j = 0; j < 1024; j++) {
				f2d[i][j] = ((i + j) & 0xff) / 1024.0f;
			}
		}

		ImageGraph graph = ElementFactory.getInstance().createImageGraph(f2d);
		Layer layer0 = ElementFactory.getInstance().createLayer();

		layer0.addGraph(graph);
		plot.addLayer(layer0, xaxis, yaxis);

		graph.getMapping().setGain(gain);
	}
}
