package org.jplot2d.swt.demo;

import org.jplot2d.element.Axis;
import org.jplot2d.element.ElementFactory;
import org.jplot2d.element.Layer;
import org.jplot2d.element.Plot;
import org.jplot2d.element.Title;
import org.jplot2d.sizing.FillContainerSizeMode;
import org.jplot2d.swt.JPlot2DComposite;

import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public class FillContainerSizeDemo {

	public static void main(String[] args) {

		Plot plot = ElementFactory.getInstance().createPlot();
		plot.setSizeMode(new FillContainerSizeMode(1));

		Title title = ElementFactory.getInstance().createTitle("Title");
		title.setFontScale(2);
		plot.addTitle(title);

		Axis xaxis = ElementFactory.getInstance().createAxis();
		Axis yaxis = ElementFactory.getInstance().createAxis();
		xaxis.getTitle().setText("x axis");
		plot.addXAxis(xaxis);
		yaxis.getTitle().setText("y axis");
		plot.addYAxis(yaxis);

		Layer layer0 = ElementFactory.getInstance().createLayer(new double[] { 0, 0.1, 0.2 },
				new double[] { 0, 0.1, 0.4 }, null);
		Layer layer1 = ElementFactory.getInstance().createLayer(new double[] { 0, 0.2, 0.4 },
				new double[] { 0, 0.3, 0.4 }, "lineB");
		plot.addLayer(layer0, xaxis, yaxis);
		plot.addLayer(layer1, xaxis, yaxis);

		Display display = new Display();
		Shell shell = new Shell(display);
		shell.setLayout(new FillLayout());

		JPlot2DComposite comp = new JPlot2DComposite(shell, plot);

		shell.setSize(640, 480);
		shell.open();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		display.dispose();

	}

}
