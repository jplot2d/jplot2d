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
package org.jplot2d.swt.demo;

import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.jplot2d.element.*;
import org.jplot2d.image.ColorMap;
import org.jplot2d.sizing.AutoPackSizeMode;
import org.jplot2d.swt.JPlot2DComposite;

import java.awt.image.ByteLookupTable;
import java.awt.image.ColorModel;
import java.awt.image.DirectColorModel;
import java.awt.image.LookupTable;

/**
 * @author Jingjing Li
 */
public class ImageFloat2dDemo {

    private static int row = 4000;
    private static int col = 8000;
    private static float zmax = 1000;

    public static void main(String[] args) {
        Display display = new Display();

        plot(display, 0.5, null);
        plot(display, 0.5, new RgbColormap());
        plotRGB(display);

        while (display.getShells().length > 0) {
            if (!display.readAndDispatch()) {
                display.sleep();
            }
        }
        display.dispose();
    }

    public static void plot(Display display, double gain, ColorMap map) {
        Plot plot = ElementFactory.getInstance().createPlot();
        plot.setSizeMode(new AutoPackSizeMode());

        PlotAxis xaxis = ElementFactory.getInstance().createAxis();
        xaxis.getTitle().setText("x axis");
        plot.addXAxis(xaxis);
        PlotAxis yaxis = ElementFactory.getInstance().createAxis();
        yaxis.getTitle().setText("y axis");
        plot.addYAxis(yaxis);

        // create a float2d array
        float[][] f2d = new float[row][col];
        for (int i = 0; i < row; i++) {
            for (int j = 0; j < col; j++) {
                f2d[i][j] = zmax * (j % (i + 1)) / (i + 1);
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

        show(display, plot);
    }

    public static void plotRGB(Display display) {
        Plot plot = ElementFactory.getInstance().createPlot();
        plot.setSizeMode(new AutoPackSizeMode());

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

        show(display, plot);
    }

    private static void show(Display display, Plot plot) {
        Shell shell = new Shell(display);
        shell.setLayout(new FillLayout());

        JPlot2DComposite comp = new JPlot2DComposite(shell, plot);

        shell.setSize(640, 480);
        shell.open();
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
