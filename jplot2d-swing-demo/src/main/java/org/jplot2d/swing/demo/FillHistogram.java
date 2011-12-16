package org.jplot2d.swing.demo;

import javax.swing.JFrame;

import org.jplot2d.element.Axis;
import org.jplot2d.element.ElementFactory;
import org.jplot2d.element.Layer;
import org.jplot2d.element.Plot;
import org.jplot2d.element.XYGraphPlotter;
import org.jplot2d.element.XYGraphPlotter.ChartType;
import org.jplot2d.element.XYGraphPlotter.FillClosureType;
import org.jplot2d.sizing.AutoPackSizeMode;
import org.jplot2d.swing.JPlot2DFrame;
import org.jplot2d.util.LineHatchPaint;
import org.jplot2d.util.SymbolShape;

public class FillHistogram {

	/**
	 * @param args
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public static void main(String[] args) {

		int n = 10;
		double[] x = new double[n];
		double[] y = new double[n];
		for (int i = 0; i < n; i++) {
			x[i] = 100.0 * (i + 1) / n;
			y[i] = Math.sin(x[i]) / x[i] + 0.03;
		}

		Plot plot = ElementFactory.getInstance().createPlot();
		plot.setSizeMode(new AutoPackSizeMode());

		Axis xaxis = ElementFactory.getInstance().createAxis();
		Axis yaxis = ElementFactory.getInstance().createAxis();
		xaxis.getTitle().setText("x axis");
		plot.addXAxis(xaxis);
		yaxis.getTitle().setText("y axis");
		plot.addYAxis(yaxis);

		XYGraphPlotter plotter = ElementFactory.getInstance().createXYGraphPlotter(x, y);
		plotter.setChartType(ChartType.HISTOGRAM);
		plotter.setSymbolVisible(true);
		plotter.setSymbolShape(SymbolShape.FCIRCLE);
		plotter.setSymbolSize(4);
		plotter.setFillEnabled(true);
		plotter.setFillClosureType(FillClosureType.BOTTOM);
		LineHatchPaint hatch = new LineHatchPaint(45);
		plotter.setFillPaint(hatch);
		Layer layer0 = ElementFactory.getInstance().createLayer();
		layer0.addGraphPlotter(plotter);
		plot.addLayer(layer0, xaxis, yaxis);

		JFrame frame = new JPlot2DFrame(plot);
		frame.setSize(640, 480);
		frame.setVisible(true);

	}
}
