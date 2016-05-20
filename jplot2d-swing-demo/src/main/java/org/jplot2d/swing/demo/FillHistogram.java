/*
 * Copyright 2010-2014 Jingjing Li.
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

import org.jplot2d.element.*;
import org.jplot2d.element.XYGraph.ChartType;
import org.jplot2d.element.XYGraph.FillClosureType;
import org.jplot2d.sizing.AutoPackSizeMode;
import org.jplot2d.swing.JPlot2DFrame;
import org.jplot2d.util.LineHatchPaint;
import org.jplot2d.util.SymbolShape;

import javax.swing.*;

public class FillHistogram {

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

        PlotAxis xaxis = ElementFactory.getInstance().createAxis();
        PlotAxis yaxis = ElementFactory.getInstance().createAxis();
        xaxis.getTitle().setText("x axis");
        plot.addXAxis(xaxis);
        yaxis.getTitle().setText("y axis");
        plot.addYAxis(yaxis);

        XYGraph graph = ElementFactory.getInstance().createXYGraph(x, y);
        graph.setChartType(ChartType.HISTOGRAM);
        graph.setSymbolVisible(true);
        graph.setSymbolShape(SymbolShape.FCIRCLE);
        graph.setSymbolSize(4);
        graph.setFillEnabled(true);
        graph.setFillClosureType(FillClosureType.BOTTOM);
        LineHatchPaint hatch = new LineHatchPaint(45);
        graph.setFillPaint(hatch);
        Layer layer0 = ElementFactory.getInstance().createLayer();
        layer0.addGraph(graph);
        plot.addLayer(layer0, xaxis, yaxis);

        JFrame frame = new JPlot2DFrame(plot);
        frame.setSize(640, 480);
        frame.setVisible(true);

    }
}
