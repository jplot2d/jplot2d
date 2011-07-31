/**
 * Copyright 2010 Jingjing Li.
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

import org.jplot2d.element.ElementFactory;
import org.jplot2d.element.Axis;
import org.jplot2d.element.Layer;
import org.jplot2d.element.Plot;
import org.jplot2d.layout.BoundsConstraint;
import org.jplot2d.layout.OverlayLayoutDirector;
import org.jplot2d.sizing.FillContainerSizeMode;
import org.jplot2d.swing.JPlot2DFrame;
import org.jplot2d.util.Insets2D;

/**
 * @author Jingjing Li
 * 
 */
public class OverlayLayoutDemo {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Plot plot = ElementFactory.getInstance().createPlot();
		plot.setSizeMode(new FillContainerSizeMode(1));
		plot.setLayoutDirector(new OverlayLayoutDirector());

		JFrame frame = new JPlot2DFrame(plot);
		frame.setSize(640, 480);
		frame.setVisible(true);

		Axis xaxis = ElementFactory.getInstance().createAxis();
		Axis yaxis = ElementFactory.getInstance().createAxis();
		xaxis.getTitle().setText("x axis");
		yaxis.getTitle().setText("y axis");
		plot.addXAxis(xaxis);
		plot.addYAxis(yaxis);

		Layer layer = ElementFactory.getInstance().createLayer(
				new double[] { 0, 0.1, 0.2 }, new double[] { 0, 0.1, 0.4 },
				"line A");
		plot.addLayer(layer, xaxis.getTickManager().getRangeManager(), yaxis
				.getTickManager().getRangeManager());

		Plot sp1 = ElementFactory.getInstance().createSubplot();
		Layer nestLayer = ElementFactory.getInstance().createLayer(
				new double[] { 0, 0.1, 0.2 }, new double[] { 0, 0.1, 0.4 },
				"line B");

		plot.addSubplot(sp1, new BoundsConstraint(new Insets2D(0, 0, 0, 0),
				new Insets2D(0.05, 0.05, 0.45, 0.45)));
		sp1.setLocation(80, 250);
		sp1.setSize(300, 200);
		sp1.addLayer(nestLayer, xaxis.getTickManager().getRangeManager(), yaxis
				.getTickManager().getRangeManager());

	}

}
