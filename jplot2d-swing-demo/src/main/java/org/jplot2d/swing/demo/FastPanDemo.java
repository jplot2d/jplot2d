/*
 * This file is part of Herschel Common Science System (HCSS).
 * Copyright 2001-2010 Herschel Science Ground Segment Consortium
 *
 * HCSS is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of
 * the License, or (at your option) any later version.
 *
 * HCSS is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General
 * Public License along with HCSS.
 * If not, see <http://www.gnu.org/licenses/>.
 */
package org.jplot2d.swing.demo;

import org.jplot2d.data.ArrayPair;
import org.jplot2d.element.*;
import org.jplot2d.sizing.FillContainerSizeMode;
import org.jplot2d.swing.JPlot2DFrame;
import org.jplot2d.util.Range;
import org.jplot2d.util.SymbolShape;

import javax.swing.*;

public class FastPanDemo {

    public static void main(String[] args) {
        int n = 1000;

        Plot plot = ElementFactory.getInstance().createPlot();
        plot.setSizeMode(new FillContainerSizeMode(1));

        JFrame frame = new JPlot2DFrame(plot);
        frame.setSize(640, 480);
        frame.setVisible(true);

        PlotAxis xaxis = ElementFactory.getInstance().createAxis();
        PlotAxis yaxis = ElementFactory.getInstance().createAxis();
        xaxis.getTitle().setText("x axis");
        xaxis.getTickManager().setRange(new Range.Double(0, n));
        plot.addXAxis(xaxis);
        yaxis.getTitle().setText("y axis");
        yaxis.getTickManager().setRange(new Range.Double(0, n));
        plot.addYAxis(yaxis);

        ArrayPair ap0 = new ArrayPair(new double[]{0, 0}, new double[]{n, n});
        ArrayPair ap1 = new ArrayPair(new double[]{0, n}, new double[]{n, 0});
        XYGraph plotter0 = ElementFactory.getInstance().createXYGraph(ap0, "lineA");
        XYGraph plotter1 = ElementFactory.getInstance().createXYGraph(ap1, "lineB");
        plotter0.setSymbolVisible(true);
        plotter0.setSymbolShape(SymbolShape.CIRCLE);
        plotter1.setSymbolVisible(true);
        plotter1.setSymbolShape(SymbolShape.CIRCLE);

        Layer layer0 = ElementFactory.getInstance().createLayer();
        layer0.addGraph(plotter0);
        layer0.addGraph(plotter1);
        plot.addLayer(layer0, xaxis, yaxis);

        double step = 1.0 / n;
        for (int i = 0; i < n; i++) {
            plot.zoomXRange(-step, 1 - step);
            plot.zoomYRange(-step, 1 - step);
            System.out.print(".");
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        System.out.println("done");
    }
}
