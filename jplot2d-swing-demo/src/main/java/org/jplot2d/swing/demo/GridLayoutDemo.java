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
import org.jplot2d.layout.GridLayoutDirector;
import org.jplot2d.layout.GridConstraint;
import org.jplot2d.sizing.AutoPackSizeMode;
import org.jplot2d.swing.JPlot2DFrame;

/**
 * @author Jingjing Li
 * 
 */
public class GridLayoutDemo {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Plot plot = ElementFactory.getInstance().createPlot();
		plot.setSizeMode(new AutoPackSizeMode());
		plot.setLayoutDirector(new GridLayoutDirector());

		JFrame frame = new JPlot2DFrame(plot);
		frame.setSize(640, 480);
		frame.setVisible(true);

		Axis xaxis = ElementFactory.getInstance().createAxis();
		Axis yaxis = ElementFactory.getInstance().createAxis();
		xaxis.getTitle().setText("x axis");
		yaxis.getTitle().setText("y axis");

		Plot sp0 = ElementFactory.getInstance().createSubplot();
		Layer l0 = ElementFactory.getInstance().createLayer(
				new double[] { 0, 0.1, 0.2 }, new double[] { 0, 0.1, 0.4 });
		sp0.addXAxis(xaxis);
		sp0.addYAxis(yaxis);
		sp0.addLayer(l0, xaxis.getTickManager().getRangeManager(), yaxis
				.getTickManager().getRangeManager());

		Axis xaxis1 = ElementFactory.getInstance().createAxis();
		Axis yaxis1 = ElementFactory.getInstance().createAxis();
		xaxis1.getTitle().setText("x axis");
		yaxis1.getTitle().setText("y axis");

		Plot sp1 = ElementFactory.getInstance().createSubplot();
		Layer nestLayer = ElementFactory.getInstance().createLayer(
				new double[] { 0, 0.1, 0.2 }, new double[] { 0, 0.1, 0.4 });
		sp1.addXAxis(xaxis1);
		sp1.addYAxis(yaxis1);
		sp1.addLayer(nestLayer, xaxis1.getTickManager().getRangeManager(),
				yaxis1.getTickManager().getRangeManager());

		plot.addSubplot(sp0, new GridConstraint(0, 0));
		plot.addSubplot(sp1, new GridConstraint(1, 0));

	}

}
