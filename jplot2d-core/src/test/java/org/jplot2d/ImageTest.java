package org.jplot2d;

import org.jplot2d.element.*;
import org.jplot2d.env.RenderEnvironment;
import org.jplot2d.renderer.ImageExporter;
import org.junit.Test;

import java.awt.*;
import java.awt.image.BufferedImage;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Test for image graphics
 */
public class ImageTest {

    private static final ElementFactory factory = ElementFactory.getInstance();

    @Test
    public void testReAutoRangeWhenAddingGraph() {
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
        env.exportPlot(exporter);
        BufferedImage img0 = exporter.getImage();

        assertEquals(img0.getWidth(), 640);
        assertEquals(img0.getHeight(), 480);
        int c0 = img0.getRGB(240, 300);
        int c1 = img0.getRGB(450, 300);
        int c2 = img0.getRGB(240, 130);
        int c3 = img0.getRGB(450, 130);
        assertEquals(new Color(c0).toString(), c0, 0xFF000000);
        assertEquals(new Color(c1).toString(), c1, 0xFF555555);
        assertEquals(new Color(c2).toString(), c2, 0xFFAAAAAA);
        assertEquals(new Color(c3).toString(), c3, 0xFFFFFFFF);

    }
}
