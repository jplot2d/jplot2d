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
import org.jplot2d.layout.BoundsConstraint;
import org.jplot2d.layout.OverlayLayoutDirector;
import org.jplot2d.sizing.FillContainerSizeMode;
import org.jplot2d.swing.JPlot2DFrame;
import org.jplot2d.util.Insets2D;

import javax.swing.*;

/**
 * @author Jingjing Li
 *
 */
public class OverlayLayoutDemo {

    public static void main(String[] args) {

        ElementFactory ef = ElementFactory.getInstance();

        Plot plot = ef.createPlot();
        plot.setSizeMode(new FillContainerSizeMode(1));
        plot.setLayoutDirector(new OverlayLayoutDirector());

        JFrame frame = new JPlot2DFrame(plot);
        frame.setSize(640, 480);
        frame.setVisible(true);

        PlotAxis[] xaxes = ef.createAxes(2);
        PlotAxis[] yaxes = ef.createAxes(2);
        xaxes[0].getTitle().setText("x axis");
        yaxes[0].getTitle().setText("y axis");
        plot.addXAxes(xaxes);
        plot.addYAxes(yaxes);

        Layer layer = ef.createLayer(ef.createXYGraph(new double[]{0, 0.1, 0.2}, new double[]{0, 0.1, 0.4},
                "line A"));
        plot.addLayer(layer, xaxes[0], yaxes[0]);

        Plot sp1 = ef.createSubplot();
        Layer nestLayer = ef.createLayer(ef.createXYGraph(new double[]{0, 0.1, 0.2}, new double[]{0, 0.1, 0.4},
                "line B"));

        plot.addSubplot(sp1, new BoundsConstraint(new Insets2D.Double(0, 0, 0, 0), new Insets2D.Double(0.05, 0.05,
                0.45, 0.45)));
        sp1.setLocation(80, 250);
        sp1.setSize(300, 200);
        PlotAxis[] p1x = ef.createAxes(2);
        PlotAxis[] p1y = ef.createAxes(2);
        p1x[0].getTitle().setText("x axis");
        p1y[0].getTitle().setText("y axis");
        p1x[1].setTickVisible(false);
        p1x[1].setLabelVisible(false);
        p1y[1].setTickVisible(false);
        p1y[1].setLabelVisible(false);
        sp1.addXAxes(p1x);
        sp1.addYAxes(p1y);
        sp1.addLayer(nestLayer, p1x[0], p1y[0]);

    }

}
