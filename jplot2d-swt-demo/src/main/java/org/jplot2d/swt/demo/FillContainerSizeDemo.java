/**
 * Copyright 2010-2012 Jingjing Li.
 * <p/>
 * This file is part of jplot2d.
 * <p/>
 * jplot2d is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or any later version.
 * <p/>
 * jplot2d is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Lesser Public License for more details.
 * <p/>
 * You should have received a copy of the GNU Lesser General Public License
 * along with jplot2d. If not, see <http://www.gnu.org/licenses/>.
 */
package org.jplot2d.swt.demo;

import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.jplot2d.element.*;
import org.jplot2d.sizing.FillContainerSizeMode;
import org.jplot2d.swt.JPlot2DComposite;

public class FillContainerSizeDemo {

    public static void main(String[] args) {
        ElementFactory ef = ElementFactory.getInstance();

        Plot plot = ElementFactory.getInstance().createPlot();
        plot.setSizeMode(new FillContainerSizeMode(1));

        Title title = ElementFactory.getInstance().createTitle("Title");
        title.setFontScale(2);
        plot.addTitle(title);

        PlotAxis xaxis = ElementFactory.getInstance().createAxis();
        PlotAxis yaxis = ElementFactory.getInstance().createAxis();
        xaxis.getTitle().setText("x axis");
        plot.addXAxis(xaxis);
        yaxis.getTitle().setText("y axis");
        plot.addYAxis(yaxis);

        Layer layer0 = ef.createLayer(ef.createXYGraph(new double[]{0, 0.1, 0.2},
                new double[]{0, 0.1, 0.4}, null));
        Layer layer1 = ef.createLayer(ef.createXYGraph(new double[]{0, 0.2, 0.4},
                new double[]{0, 0.3, 0.4}, "lineB"));
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
