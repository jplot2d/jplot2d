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

import org.jplot2d.data.ArrayPair;
import org.jplot2d.data.XYGraphData;
import org.jplot2d.element.ElementFactory;
import org.jplot2d.element.Axis;
import org.jplot2d.element.HLineAnnotation;
import org.jplot2d.element.HStripAnnotation;
import org.jplot2d.element.Layer;
import org.jplot2d.element.Plot;
import org.jplot2d.element.SymbolAnnotation;
import org.jplot2d.element.Title;
import org.jplot2d.element.VLineAnnotation;
import org.jplot2d.element.VStripAnnotation;
import org.jplot2d.element.XYGraph;
import org.jplot2d.sizing.AutoPackSizeMode;
import org.jplot2d.sizing.FillContainerSizeMode;
import org.jplot2d.swing.JPlot2DFrame;
import org.jplot2d.transform.TransformType;

/**
 * @author Jingjing Li
 * 
 */
public class DevDemo {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Plot plot = ElementFactory.getInstance().createPlot();
		plot.setSizeMode(new AutoPackSizeMode());

		Title title = ElementFactory.getInstance().createTitle("Title");
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
		yaxis.getTickManager().getAxisTransform().setTransform(TransformType.LOGARITHMIC);
		plot.addYAxis(yaxis);

		XYGraphData graph = new XYGraphData(new ArrayPair(new double[] { 0, 0.1, 0.2 }, new double[] { 0,
				0.1, 0.4 }), null, new ArrayPair(new double[] { 0.01, 0.01, 0.01 }, new double[] {
				0.01, 0.01, 0.01 }));
		XYGraph gp = ElementFactory.getInstance().createXYGraph(graph, "asdf");
		Layer layer0 = ElementFactory.getInstance().createLayer();

		layer0.addGraph(gp);
		plot.addLayer(layer0, xaxis, yaxis);
		
//		plot.getLegend().setPosition(null);
//		plot.getLegend().setMovable(true);
//		plot.getLegend().setLocation(0, 20);
//		
//		Title t0 = ElementFactory.getInstance().createTitle("jplot2d");
//		plot.addTitle(t0);
//		t0.setPosition(null);
//		t0.setMovable(true);
//		t0.setLocation(0, 20);

//		SymbolMarker sm = ElementFactory.getInstance().createSymbolMarker(0, 0.1, "marker");
//		sm.setAngle(45);
//		layer0.addMarker(sm);
//		HLineMarker hlm = ElementFactory.getInstance().createHLineMarker(0.1);
//		layer0.addMarker(hlm);
//		VLineMarker vlm = ElementFactory.getInstance().createVLineMarker(0.1);
//		layer0.addMarker(vlm);
//		HStripMarker hsm = ElementFactory.getInstance().createHStripMarker(0.15, 0.15);
//		layer0.addMarker(hsm);
//		VStripMarker vsm = ElementFactory.getInstance().createVStripMarker(0.13, 0.15);
//		layer0.addMarker(vsm);

	}
}
