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

import org.jplot2d.element.ComponentFactory;
import org.jplot2d.element.Axis;
import org.jplot2d.element.Layer;
import org.jplot2d.element.Plot;
import org.jplot2d.layout.BoundsConstraint;
import org.jplot2d.layout.OverlayLayoutDirector;
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
		Plot plot = ComponentFactory.getInstance().createPlot();
		plot.setLayoutDirector(new OverlayLayoutDirector());

		JFrame frame = new JPlot2DFrame(plot);
		frame.setSize(640, 480);
		frame.setVisible(true);

		Axis xaxis = ComponentFactory.getInstance().createAxis();
		Axis yaxis = ComponentFactory.getInstance().createAxis();
		xaxis.getTitle().setText("x axis");
		yaxis.getTitle().setText("y axis");
		plot.addXAxis(xaxis);
		plot.addYAxis(yaxis);

		Layer layer = ComponentFactory.getInstance().createLayer(
				new double[] { 0, 0.1, 0.2 }, new double[] { 0, 0.1, 0.4 });
		plot.addLayer(layer, xaxis.getTickManager().getRangeManager(), yaxis
				.getTickManager().getRangeManager());

		Plot sp1 = ComponentFactory.getInstance().createSubplot();
		Layer nestLayer = ComponentFactory.getInstance().createLayer(
				new double[] { 0, 0.1, 0.2 }, new double[] { 0, 0.1, 0.4 });

		plot.addSubplot(sp1, new BoundsConstraint(new Insets2D(0, 0, 0, 0),
				new Insets2D(0.05, 0.05, 0.45, 0.45)));
		sp1.setLocation(80, 250);
		sp1.setSize(300, 200);
		sp1.addLayer(nestLayer, xaxis.getTickManager().getRangeManager(), yaxis
				.getTickManager().getRangeManager());

	}

}