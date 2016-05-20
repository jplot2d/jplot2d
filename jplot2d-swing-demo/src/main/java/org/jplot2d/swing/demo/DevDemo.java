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

import org.jplot2d.data.ArrayPair;
import org.jplot2d.data.XYGraphData;
import org.jplot2d.element.*;
import org.jplot2d.env.SystemOutCommandLogger;
import org.jplot2d.sizing.AutoPackSizeMode;
import org.jplot2d.swing.JPlot2DFrame;
import org.jplot2d.util.SymbolShape;

import javax.swing.*;
import java.awt.*;

/**
 * @author Jingjing Li
 *
 */
public class DevDemo {

    public static void main(String[] args) throws InterruptedException {
        final Plot plot = ElementFactory.getInstance().createPlot();
        plot.setSizeMode(new AutoPackSizeMode());
        // plot.setSizeMode(new FillContainerSizeMode(1));

        Title title = ElementFactory.getInstance().createTitle("Title");
        title.setFontScale(2);
        plot.addTitle(title);

        PlotAxis[] xaxes = ElementFactory.getInstance().createAxes(2);
        xaxes[0].getTitle().setText("x axis");
        plot.addXAxes(xaxes);
        PlotAxis[] yaxes = ElementFactory.getInstance().createAxes(2);
        yaxes[0].getTitle().setText("y axis");
        // yaxis.getTickManager().getAxisTransform().setTransform(TransformType.LOGARITHMIC);
        plot.addYAxes(yaxes);

        XYGraphData graph = new XYGraphData(new ArrayPair(new double[]{0, 0.1, 0.2}, new double[]{0, 0.1, 0.4}),
                null, new ArrayPair(new double[]{0.01, 0.01, 0.01}, new double[]{0.01, 0.01, 0.01}));
        XYGraph gp = ElementFactory.getInstance().createXYGraph(graph, "asdf");
        Layer layer0 = ElementFactory.getInstance().createLayer();

        layer0.addGraph(gp);

        gp.setSymbolVisible(true);
        gp.setSymbolShape(SymbolShape.CIRCLE);

        gp.setSymbolSize(14);

        // plot.getLegend().setPosition(null);
        // plot.getLegend().setMovable(true);
        // plot.getLegend().setLocation(0, 20);
        //
        // Title t0 = ElementFactory.getInstance().createTitle("jplot2d");
        // plot.addTitle(t0);
        // t0.setPosition(null);
        // t0.setMovable(true);
        // t0.setLocation(0, 20);

        SymbolAnnotation sm = ElementFactory.getInstance().createSymbolAnnotation(0.06, 0.12, SymbolShape.VCROSS, "marker");
        sm.setAngle(60);
        layer0.addAnnotation(sm);
        SymbolAnnotation ca = ElementFactory.getInstance().createCoordinateAnnotation(0, 0.3, SymbolShape.VCROSS);
        layer0.addAnnotation(ca);
        HLineAnnotation hlm = ElementFactory.getInstance().createHLineAnnotation(0.1);
        layer0.addAnnotation(hlm);
        VLineAnnotation vlm = ElementFactory.getInstance().createVLineAnnotation(0.1);
        layer0.addAnnotation(vlm);
        HStripAnnotation hsm = ElementFactory.getInstance().createHStripAnnotation(0.15, 0.15);
        layer0.addAnnotation(hsm);
        VStripAnnotation vsm = ElementFactory.getInstance().createVStripAnnotation(0.13, 0.15);
        layer0.addAnnotation(vsm);
        RectangleAnnotation ra = ElementFactory.getInstance().createRectangleAnnotation(0, 0, 0.05, 0.05);
        layer0.addAnnotation(ra);

        plot.addLayer(layer0);
        layer0.setAxesTransform(xaxes[0].getTickManager().getAxisTransform(), yaxes[0].getTickManager().getAxisTransform());

        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                JPlot2DFrame frame = new JPlot2DFrame(plot);
                frame.getPlotComponent().setPreferredSize(new Dimension(640, 480));
                frame.pack();
                frame.setVisible(true);
                plot.getEnvironment().setCommandLogger(SystemOutCommandLogger.getInstance());
                //frame.getPlotComponent().getRenderEnvironment()
            }
        });

        Thread.sleep(5000);

        sm.setSymbolScale(2);

        // frame.getPlotComponent().setPlotBackground(Color.RED);
    }
}
