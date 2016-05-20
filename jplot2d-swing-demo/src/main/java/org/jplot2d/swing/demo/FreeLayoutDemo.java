/*
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

import org.jplot2d.element.ElementFactory;
import org.jplot2d.element.Layer;
import org.jplot2d.element.Plot;
import org.jplot2d.element.PlotAxis;
import org.jplot2d.sizing.FillContainerSizeMode;
import org.jplot2d.swing.JPlot2DFrame;

import javax.swing.*;

/**
 * @author Jingjing Li
 *
 */
public class FreeLayoutDemo {

    public static void main(String[] args) {
        ElementFactory ef = ElementFactory.getInstance();

        Plot plot = ef.createPlot();
        plot.setSizeMode(new FillContainerSizeMode(1));

        JFrame frame = new JPlot2DFrame(plot);
        frame.setSize(640, 480);
        frame.setVisible(true);

        PlotAxis xaxis = ef.createAxis();
        PlotAxis yaxis = ef.createAxis();
        xaxis.getTitle().setText("x axis");
        yaxis.getTitle().setText("y axis");
        plot.addXAxis(xaxis);
        plot.addYAxis(yaxis);

        Layer layer = ef.createLayer(ef.createXYGraph(new double[]{0, 0.1, 0.2},
                new double[]{0, 0.1, 0.4}, "line A"));
        plot.addLayer(layer, xaxis.getTickManager().getAxisTransform(), yaxis.getTickManager()
                .getAxisTransform());

        Plot sp1 = ef.createSubplot();
        Layer nestLayer = ef.createLayer(ef.createXYGraph(new double[]{0, 0.1, 0.2},
                new double[]{0, 0.1, 0.4}, "line B"));

        plot.addSubplot(sp1, null);
        sp1.setLocation(80, 250);
        sp1.setSize(300, 200);
        sp1.addLayer(nestLayer, xaxis.getTickManager().getAxisTransform(), yaxis.getTickManager()
                .getAxisTransform());

    }

}
