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
import org.jplot2d.element.Title;
import org.jplot2d.sizing.FillContainerSizeMode;
import org.jplot2d.swing.JPlot2DFrame;

/**
 * @author Jingjing Li
 * 
 */
public class FillContainerSizeDemo {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Plot plot = ComponentFactory.getInstance().createPlot();
		plot.setSizeMode(new FillContainerSizeMode(1));

		Title title = ComponentFactory.getInstance().createTitle("Title");
		title.setFontScale(2);
		plot.addTitle(title);

		JFrame frame = new JPlot2DFrame(plot);
		frame.setSize(640, 480);
		frame.setVisible(true);

		Axis xaxis = ComponentFactory.getInstance().createAxis();
		Axis yaxis = ComponentFactory.getInstance().createAxis();
		xaxis.getTitle().setText("x axis");
		plot.addXAxis(xaxis);
		yaxis.getTitle().setText("y axis");
		plot.addYAxis(yaxis);

		Layer layer0 = ComponentFactory.getInstance().createLayer(
				new double[] { 0, 0.1, 0.2 }, new double[] { 0, 0.1, 0.4 },
				null);
		Layer layer1 = ComponentFactory.getInstance().createLayer(
				new double[] { 0, 0.2, 0.4 }, new double[] { 0, 0.3, 0.4 },
				"lineB");
		plot.addLayer(layer0, xaxis, yaxis);
		plot.addLayer(layer1, xaxis, yaxis);

		// plot.setPreferredContentSize(new DoubleDimension2D(600, 200));

	}

}
