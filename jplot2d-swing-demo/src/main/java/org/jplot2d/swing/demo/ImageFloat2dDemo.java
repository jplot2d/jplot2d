/*
 * Copyright 2010-2015 Jingjing Li.
 *
 * This file is part of jplot2d.
 *
 * jplot2d is free software:
 * you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation, either version 3 of the License, or any later version.
 *
 * jplot2d is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Lesser Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with jplot2d.
 * If not, see <http://www.gnu.org/licenses/>.
 */
package org.jplot2d.swing.demo;

import org.jplot2d.element.*;
import org.jplot2d.image.ColorMap;
import org.jplot2d.sizing.AutoPackSizeMode;
import org.jplot2d.swing.JPlot2DFrame;

import javax.swing.*;
import java.awt.image.ByteLookupTable;
import java.awt.image.ColorModel;
import java.awt.image.DirectColorModel;
import java.awt.image.LookupTable;

/**
 * @author Jingjing Li
 *
 */
public class ImageFloat2dDemo {

    public static void main(String[] args) {
        plot(0.5, null);
        plot(0.5, new RgbColormap());
        plotRGB();
    }

    public static void plot(double gain, ColorMap map) {
        Plot plot = ElementFactory.getInstance().createPlot();
        plot.setSizeMode(new AutoPackSizeMode());

        JFrame frame = new JPlot2DFrame(plot);
        frame.setSize(640, 480);
        frame.setVisible(true);

        PlotAxis xaxis = ElementFactory.getInstance().createAxis();
        xaxis.getTitle().setText("x axis");
        plot.addXAxis(xaxis);
        PlotAxis yaxis = ElementFactory.getInstance().createAxis();
        yaxis.getTitle().setText("y axis");
        plot.addYAxis(yaxis);

        // create a float2d array
        float[][] f2d = new float[256][1024];
        for (int i = 0; i < 256; i++) {
            for (int j = 0; j < 1024; j++) {
                f2d[i][j] = ((i + j) & 0xff) / 1024.0f;
            }
        }

        ImageGraph graph = ElementFactory.getInstance().createImageGraph(f2d);
        Layer layer0 = ElementFactory.getInstance().createLayer();

        layer0.addGraph(graph);
        plot.addLayer(layer0, xaxis, yaxis);

        graph.getMapping().setGain(gain);
        graph.getMapping().setColorMap(map);

        Colorbar colorbar = ElementFactory.getInstance().createColorbar();
        plot.addColorbar(colorbar);
        colorbar.setImageMapping(graph.getMapping());
    }

    public static void plotRGB() {
        Plot plot = ElementFactory.getInstance().createPlot();
        plot.setSizeMode(new AutoPackSizeMode());

        JFrame frame = new JPlot2DFrame(plot);
        frame.setSize(640, 480);
        frame.setVisible(true);

        PlotAxis xaxis = ElementFactory.getInstance().createAxis();
        xaxis.getTitle().setText("x axis");
        plot.addXAxis(xaxis);
        PlotAxis yaxis = ElementFactory.getInstance().createAxis();
        yaxis.getTitle().setText("y axis");
        plot.addYAxis(yaxis);

        // create a float2d array
        float[][] r2d = new float[][]{{0, 1}, {2, 3}};
        float[][] g2d = new float[][]{{3, 0}, {1, 2}};
        float[][] b2d = new float[][]{{2, 3}, {0, 1}};

        RGBImageGraph graph = ElementFactory.getInstance().createRGBImageGraph(r2d, g2d, b2d);
        Layer layer0 = ElementFactory.getInstance().createLayer();

        layer0.addGraph(graph);
        plot.addLayer(layer0, xaxis, yaxis);

    }

    public static class RgbColormap implements ColorMap {

        private LookupTable lut;

        public RgbColormap() {
            byte[] la = new byte[256];
            for (int i = 0; i < 256; i++) {
                la[i] = (byte) i;
            }
            lut = new ByteLookupTable(0, new byte[][]{la, la, la});
        }

        public int getInputBits() {
            return 8;
        }

        public LookupTable getLookupTable() {
            return lut;
        }

        public ColorModel getColorModel() {
            return new DirectColorModel(24, 0x00ff0000, 0x0000ff00, 0x000000ff, 0x0);
        }

    }

}
