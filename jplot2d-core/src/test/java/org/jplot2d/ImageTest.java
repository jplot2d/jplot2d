/*
 * Copyright 2010-2016 Jingjing Li.
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
package org.jplot2d;

import org.jplot2d.element.*;
import org.jplot2d.env.RenderEnvironment;
import org.jplot2d.image.FixedLimitsAlgorithm;
import org.jplot2d.renderer.ImageExporter;
import org.junit.Test;

import java.awt.*;
import java.awt.image.BufferedImage;

import static org.junit.Assert.assertEquals;

/**
 * Test for image graphics
 */
public class ImageTest {

    private static final ElementFactory factory = ElementFactory.getInstance();

    @Test
    public void testDefaultColor() {
        Plot plot = factory.createPlot();

        PlotAxis xaxis = factory.createAxis();
        PlotAxis yaxis = factory.createAxis();
        xaxis.getTitle().setText("x axis");
        plot.addXAxis(xaxis);
        yaxis.getTitle().setText("y axis");
        plot.addYAxis(yaxis);

        Layer layer0 = factory.createLayer();
        plot.addLayer(layer0, xaxis, yaxis);

        RenderEnvironment env = new RenderEnvironment(false);
        env.setPlot(plot);

        ImageGraph graph = factory.createImageGraph(new int[][]{{0, 1}, {2, 3}});
        layer0.addGraph(graph);

        ImageExporter exporter = new ImageExporter(BufferedImage.TYPE_INT_ARGB, null);
        env.exportComponent(graph, exporter);
        BufferedImage img0 = exporter.getImage();

        assertEquals(img0.getWidth(), 572);
        assertEquals(img0.getHeight(), 419);
        int c0 = img0.getRGB(140, 310);
        int c1 = img0.getRGB(430, 310);
        int c2 = img0.getRGB(140, 100);
        int c3 = img0.getRGB(430, 100);
        assertEquals(new Color(c0).toString(), c0, 0xFF000000);
        assertEquals(new Color(c1).toString(), c1, 0xFF555555);
        assertEquals(new Color(c2).toString(), c2, 0xFFAAAAAA);
        assertEquals(new Color(c3).toString(), c3, 0xFFFFFFFF);
    }

    @Test
    public void testBias() {
        Plot plot = factory.createPlot();

        PlotAxis xaxis = factory.createAxis();
        PlotAxis yaxis = factory.createAxis();
        xaxis.getTitle().setText("x axis");
        plot.addXAxis(xaxis);
        yaxis.getTitle().setText("y axis");
        plot.addYAxis(yaxis);

        Layer layer0 = factory.createLayer();
        plot.addLayer(layer0, xaxis, yaxis);

        RenderEnvironment env = new RenderEnvironment(false);
        env.setPlot(plot);

        ImageGraph graph = factory.createImageGraph(new int[][]{{0, 1}, {2, 3}});
        layer0.addGraph(graph);
        //noinspection ConstantConditions
        graph.getMapping().setBias(0.7);

        ImageExporter exporter = new ImageExporter(BufferedImage.TYPE_INT_ARGB, null);
        env.exportComponent(graph, exporter);
        BufferedImage img0 = exporter.getImage();

        assertEquals(img0.getWidth(), 572);
        assertEquals(img0.getHeight(), 419);
        int c0 = img0.getRGB(140, 310);
        int c1 = img0.getRGB(430, 310);
        int c2 = img0.getRGB(140, 100);
        int c3 = img0.getRGB(430, 100);
        assertEquals(new Color(c0).toString(), c0, 0xFF000000);
        assertEquals(new Color(c1).toString(), c1, 0xFF898989);
        assertEquals(new Color(c2).toString(), c2, 0xFFD2D2D2);
        assertEquals(new Color(c3).toString(), c3, 0xFFFFFFFF);
    }

    @Test
    public void testGain() {
        Plot plot = factory.createPlot();

        PlotAxis xaxis = factory.createAxis();
        PlotAxis yaxis = factory.createAxis();
        xaxis.getTitle().setText("x axis");
        plot.addXAxis(xaxis);
        yaxis.getTitle().setText("y axis");
        plot.addYAxis(yaxis);

        Layer layer0 = factory.createLayer();
        plot.addLayer(layer0, xaxis, yaxis);

        RenderEnvironment env = new RenderEnvironment(false);
        env.setPlot(plot);

        ImageGraph graph = factory.createImageGraph(new int[][]{{0, 1}, {2, 3}});
        layer0.addGraph(graph);
        //noinspection ConstantConditions
        graph.getMapping().setGain(0.7);

        ImageExporter exporter = new ImageExporter(BufferedImage.TYPE_INT_ARGB, null);
        env.exportComponent(graph, exporter);
        BufferedImage img0 = exporter.getImage();

        assertEquals(img0.getWidth(), 572);
        assertEquals(img0.getHeight(), 419);
        int c0 = img0.getRGB(140, 310);
        int c1 = img0.getRGB(430, 310);
        int c2 = img0.getRGB(140, 100);
        int c3 = img0.getRGB(430, 100);
        assertEquals(new Color(c0).toString(), c0, 0xFF000000);
        assertEquals(new Color(c1).toString(), c1, 0xFF696969);
        assertEquals(new Color(c2).toString(), c2, 0xFF969696);
        assertEquals(new Color(c3).toString(), c3, 0xFFFFFFFF);
    }

    @Test
    public void testLimits() {
        Plot plot = factory.createPlot();

        PlotAxis xaxis = factory.createAxis();
        PlotAxis yaxis = factory.createAxis();
        xaxis.getTitle().setText("x axis");
        plot.addXAxis(xaxis);
        yaxis.getTitle().setText("y axis");
        plot.addYAxis(yaxis);

        Layer layer0 = factory.createLayer();
        plot.addLayer(layer0, xaxis, yaxis);

        RenderEnvironment env = new RenderEnvironment(false);
        env.setPlot(plot);

        ImageGraph graph = factory.createImageGraph(new int[][]{{0, 1}, {2, 3}});
        layer0.addGraph(graph);
        //noinspection ConstantConditions
        graph.getMapping().setLimitsAlgorithm(new FixedLimitsAlgorithm(-1, 4));

        ImageExporter exporter = new ImageExporter(BufferedImage.TYPE_INT_ARGB, null);
        env.exportComponent(graph, exporter);
        BufferedImage img0 = exporter.getImage();

        assertEquals(img0.getWidth(), 572);
        assertEquals(img0.getHeight(), 419);
        int c0 = img0.getRGB(140, 310);
        int c1 = img0.getRGB(430, 310);
        int c2 = img0.getRGB(140, 100);
        int c3 = img0.getRGB(430, 100);
        assertEquals(new Color(c0).toString(), c0, 0xFF333333);
        assertEquals(new Color(c1).toString(), c1, 0xFF666666);
        assertEquals(new Color(c2).toString(), c2, 0xFF999999);
        assertEquals(new Color(c3).toString(), c3, 0xFFCCCCCC);
    }


}
