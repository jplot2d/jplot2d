/*
 * Copyright 2010-2012 Jingjing Li.
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
import org.jplot2d.sizing.AutoPackSizeMode;
import org.jplot2d.swing.JPlot2DFrame;
import org.jplot2d.transform.TransformType;
import org.jplot2d.util.Range;

import javax.swing.*;
import java.awt.*;

/**
 * @author Jingjing Li
 *
 */
public class AA518L8Fig3 {

    public static void main(String[] args) {

        // ElementFactory
        ElementFactory ef = ElementFactory.getInstance();

        // Create plot
        Plot p = ef.createPlot();
        p.setPreferredContentSize(300, 200);
        p.setSizeMode(new AutoPackSizeMode());
        p.getLegend().setVisible(false);
        p.setFontSize(16);

        JFrame frame = new JPlot2DFrame(p);
        frame.setSize(480, 360);
        frame.setVisible(true);

        // Axes
        PlotAxis xaxis = ef.createAxis();
        xaxis.getTitle().setText("r (cm)");
        xaxis.getTitle().setFontScale(1);
        xaxis.getTickManager().getAxisTransform().setTransform(TransformType.LOGARITHMIC);
        xaxis.getTickManager().getAxisTransform().setRange(new Range.Double(5e13, 2.5e17));

        PlotAxis xaxisTop = ef.createAxis();
        xaxisTop.setPosition(AxisPosition.POSITIVE_SIDE);
        xaxisTop.getTitle().setText("r(\u2033)");
        xaxisTop.getTitle().setFontScale(1);
        xaxisTop.getTickManager().getAxisTransform().setTransform(TransformType.LOGARITHMIC);
        xaxisTop.getTickManager().getAxisTransform().setRange(new Range.Double(2.8e-2, 2e2));
        xaxisTop.getTickManager().setLabelFormat("%.0m");

        PlotAxis yaxisLeft = ef.createAxis();
        yaxisLeft.setColor(Color.RED);
        yaxisLeft.getTitle().setText("X");
        yaxisLeft.getTitle().setFontScale(1);
        yaxisLeft.getTickManager().getAxisTransform().setTransform(TransformType.LOGARITHMIC);
        yaxisLeft.getTickManager().getAxisTransform().setRange(new Range.Double(3e-10, 7e-7));

        PlotAxis yaxisRight = ef.createAxis();
        yaxisRight.setColor(Color.BLUE);
        yaxisRight.setPosition(AxisPosition.POSITIVE_SIDE);
        yaxisRight.getTitle().setText("$\\mathrm{T_K}$");
        yaxisRight.getTitle().setFontScale(1);
        yaxisRight.getTickManager().getAxisTransform().setTransform(TransformType.LINEAR);
        yaxisRight.getTickManager().getAxisTransform().setRange(new Range.Double(0, 1200));
        yaxisRight.getTickManager().setTickInterval(500);

        p.addXAxis(xaxis);
        p.addXAxis(xaxisTop);
        p.addYAxis(yaxisLeft);
        p.addYAxis(yaxisRight);

        // Tk Layer
        double[] tkx = new double[]{1.1e14, 1e15, 1e16, 1e17};
        double[] tky = new double[]{1200, 360, 80, 0};
        Layer tkl = ef.createLayer(ef.createXYGraph(tkx, tky));
        tkl.setColor(Color.BLUE);
        Annotation tka = ef.createSymbolAnnotation(5e14, 600, "T$\\mathrm{_K}$");
        tka.setColor(Color.BLUE);
        tka.setFontScale(1.2f);
        tkl.addAnnotation(tka);
        p.addLayer(tkl, xaxis, yaxisRight);

        // SiC2
        double[] scx = new double[]{5e13, 1e16, 2e16, 7e16, 1e17, 2.1e17};
        double[] scy = new double[]{2e-7, 2e-7, 5e-7, 2e-7, 5e-8, 3e-10};
        Layer scl = ef.createLayer(ef.createXYGraph(scx, scy));
        scl.setColor(Color.RED);
        Annotation sca = ef.createSymbolAnnotation(2e16, 1e-7, "SiC$\\mathrm{_2}$");
        sca.setColor(Color.RED);
        sca.setFontScale(1.2f);
        scl.addAnnotation(sca);
        p.addLayer(scl, xaxis, yaxisLeft);

        // SiC2 LTE
        double[] ltex = new double[]{5e13, 6e13, 9e13, 1.3e14, 2e14, 2.1e14, 2.7e14};
        double[] ltey = new double[]{5e-8, 3e-7, 1.8e-7, 3e-7, 4e-8, 6e-8, 3e-10};
        Layer ltel = ef.createLayer(ef.createXYGraph(ltex, ltey));
        ltel.setColor(Color.GREEN);
        Annotation ltea = ef.createSymbolAnnotation(7e13, 3e-9, "SiC$\\mathrm{_2}$\nLTE");
        ltea.setColor(Color.GREEN);
        ltea.setFontScale(1.2f);
        ltel.addAnnotation(ltea);
        p.addLayer(ltel, xaxis, yaxisLeft);
    }
}
