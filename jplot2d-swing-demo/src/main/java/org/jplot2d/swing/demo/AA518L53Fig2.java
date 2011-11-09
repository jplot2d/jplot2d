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
package org.jplot2d.swing.demo;

import javax.swing.JFrame;

import org.jplot2d.element.ElementFactory;
import org.jplot2d.element.Axis;
import org.jplot2d.element.Layer;
import org.jplot2d.element.Plot;
import org.jplot2d.element.Title;
import org.jplot2d.sizing.AutoPackSizeMode;
import org.jplot2d.sizing.FillContainerSizeMode;
import org.jplot2d.swing.JPlot2DFrame;
import org.jplot2d.transform.TransformType;
import org.jplot2d.util.Range;

/**
 * @author Jingjing Li
 * 
 */
public class AA518L53Fig2 {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Plot plot = ElementFactory.getInstance().createPlot();
		plot.setSizeMode(new AutoPackSizeMode());

		JFrame frame = new JPlot2DFrame(plot);
		frame.setSize(640, 480);
		frame.setVisible(true);

		Plot sp0 = ElementFactory.getInstance().createSubplot();
		Plot sp1 = ElementFactory.getInstance().createSubplot();

		Axis[] xaxes = ElementFactory.getInstance().createAxes(2);
		Axis[] yaxes = ElementFactory.getInstance().createAxes(2);
		xaxes[0].getTitle().setText("wavelength $\\mathrm{\\lambda}$ [$\\mathrm{\\micro}$m]");
		xaxes[0].getTickManager().getAxisTransform().setRange(new Range.Double(10, 2e6));
		xaxes[0].getTickManager().getAxisTransform().setType(TransformType.LOGARITHMIC);
		xaxes[1].setLabelVisible(false);

		yaxes[0].getTitle().setText("flux density [Jy]");
		yaxes[0].getTickManager().getAxisTransform().setRange(new Range.Double(0.05, 1200));
		yaxes[0].getTickManager().getAxisTransform().setType(TransformType.LOGARITHMIC);
		yaxes[1].setLabelVisible(false);

		plot.addXAxes(xaxes);
		plot.addYAxes(yaxes);

	}

}
