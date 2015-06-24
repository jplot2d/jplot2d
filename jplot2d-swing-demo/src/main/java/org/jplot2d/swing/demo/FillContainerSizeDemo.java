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
package org.jplot2d.swing.demo;

import org.jplot2d.element.*;
import org.jplot2d.sizing.FillContainerSizeMode;
import org.jplot2d.swing.JPlot2DFrame;

import javax.swing.*;

/**
 * @author Jingjing Li
 *
 */
public class FillContainerSizeDemo {

    public static void main(String[] args) {
        ElementFactory ef = ElementFactory.getInstance();

        Plot plot = ef.createPlot();
        plot.setSizeMode(new FillContainerSizeMode(1));

        Title title = ef.createTitle("Title");
        title.setFontScale(2);
        plot.addTitle(title);

        JFrame frame = new JPlot2DFrame(plot);
        frame.setSize(640, 480);
        frame.setVisible(true);

        PlotAxis xaxis = ef.createAxis();
        PlotAxis yaxis = ef.createAxis();
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

        // plot.setPreferredContentSize(new DoubleDimension2D(600, 200));

    }

}
