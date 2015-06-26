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
package org.jplot2d.element;

import org.jplot2d.data.SingleBandImageData;
import org.jplot2d.env.InterfaceInfo;
import org.junit.Test;

import static org.jplot2d.util.TestUtils.checkCollecionOrder;
import static org.jplot2d.util.TestUtils.checkPropertyInfoNames;
import static org.junit.Assert.*;

/**
 * Those test cases test methods on Layer.
 *
 * @author Jingjing Li
 */
public class LayerTest {

    private static final ElementFactory factory = ElementFactory.getInstance();

    @Test
    public void testInterfaceInfo() {
        InterfaceInfo iinfo = InterfaceInfo.loadInterfaceInfo(Layer.class);
        checkCollecionOrder(iinfo.getPropertyInfoGroupMap().keySet(), "Component");
        checkPropertyInfoNames(iinfo.getPropertyInfoGroupMap().get("Component"), "visible", "cacheable", "selectable",
                "ZOrder", "color", "fontName", "fontStyle", "fontSize", "fontScale", "location", "size", "bounds");

        checkCollecionOrder(iinfo.getProfilePropertyInfoGroupMap().keySet(), "Component");
        checkPropertyInfoNames(iinfo.getProfilePropertyInfoGroupMap().get("Component"), "visible", "cacheable",
                "selectable", "ZOrder", "color", "fontName", "fontStyle", "fontSize", "fontScale");
    }

    @Test
    public void testCreateLayer() {
        Layer layer = factory.createLayer();

        assertNull(layer.getSize());
        assertNull(layer.getBounds());
        assertNull(layer.getSelectableBounds());
    }

    @Test
    public void testAddAndRemoveXYGraph() {
        Layer layer = factory.createLayer();
        XYGraph graphA = factory.createXYGraph(new double[0], new double[0], "");
        XYGraph graphB = factory.createXYGraph(new double[0], new double[0], "");

        layer.addGraph(graphA);
        assertArrayEquals(layer.getGraphs(), new Graph[]{graphA});
        layer.addGraph(graphB);
        assertArrayEquals(layer.getGraphs(), new Graph[]{graphA, graphB});

        layer.removeGraph(graphA);
        assertArrayEquals(layer.getGraphs(), new Graph[]{graphB});
        layer.removeGraph(graphB);
        assertArrayEquals(layer.getGraphs(), new Graph[0]);

        assertNotSame(graphA.getEnvironment(), layer.getEnvironment());
        assertNotSame(graphB.getEnvironment(), layer.getEnvironment());
    }

    @Test
    public void testAddAndRemoveImageGraph() {
        Layer layer = factory.createLayer();
        ImageGraph graphA = factory.createImageGraph((SingleBandImageData) null);
        ImageGraph graphB = factory.createImageGraph((SingleBandImageData) null);
        ImageMapping mappingA = graphA.getMapping();
        ImageMapping mappingB = graphB.getMapping();
        assertNotNull(mappingA);
        assertNotNull(mappingB);

        layer.addGraph(graphA);
        assertArrayEquals(layer.getGraphs(), new Graph[]{graphA});
        layer.addGraph(graphB);
        assertArrayEquals(layer.getGraphs(), new Graph[]{graphA, graphB});

        assertSame(graphA.getMapping(), mappingA);
        assertSame(graphB.getMapping(), mappingB);
        assertSame(mappingA.getEnvironment(), layer.getEnvironment());
        assertSame(mappingB.getEnvironment(), layer.getEnvironment());

        layer.removeGraph(graphA);
        assertArrayEquals(layer.getGraphs(), new Graph[]{graphB});
        layer.removeGraph(graphB);
        assertArrayEquals(layer.getGraphs(), new Graph[0]);

        // the axis range manager and axis lock group should be removed together
        assertSame(graphA.getMapping(), mappingA);
        assertSame(graphB.getMapping(), mappingB);
        assertNotSame(graphA.getEnvironment(), layer.getEnvironment());
        assertNotSame(graphB.getEnvironment(), layer.getEnvironment());
        assertSame(graphA.getEnvironment(), mappingA.getEnvironment());
        assertSame(graphB.getEnvironment(), mappingB.getEnvironment());
    }

    @Test
    public void testAddAndRemoveRGBImageGraph() {
        Layer layer = factory.createLayer();
        RGBImageGraph graphA = factory.createRGBImageGraph(null);
        RGBImageGraph graphB = factory.createRGBImageGraph(null);
        RGBImageMapping mappingA = graphA.getMapping();
        RGBImageMapping mappingB = graphB.getMapping();
        assertNotNull(mappingA);
        assertNotNull(mappingB);

        layer.addGraph(graphA);
        assertArrayEquals(layer.getGraphs(), new Graph[]{graphA});
        layer.addGraph(graphB);
        assertArrayEquals(layer.getGraphs(), new Graph[]{graphA, graphB});

        assertSame(graphA.getMapping(), mappingA);
        assertSame(graphB.getMapping(), mappingB);
        assertSame(mappingA.getEnvironment(), layer.getEnvironment());
        assertSame(mappingB.getEnvironment(), layer.getEnvironment());

        layer.removeGraph(graphA);
        assertArrayEquals(layer.getGraphs(), new Graph[]{graphB});
        layer.removeGraph(graphB);
        assertArrayEquals(layer.getGraphs(), new Graph[0]);

        // the axis range manager and axis lock group should be removed together
        assertSame(graphA.getMapping(), mappingA);
        assertSame(graphB.getMapping(), mappingB);
        assertNotSame(graphA.getEnvironment(), layer.getEnvironment());
        assertNotSame(graphB.getEnvironment(), layer.getEnvironment());
        assertSame(graphA.getEnvironment(), mappingA.getEnvironment());
        assertSame(graphB.getEnvironment(), mappingB.getEnvironment());
    }

    /**
     * Attaching to null will throw an exception. Attaching to an axis before join an environment will throw an
     * exception.
     */
    @Test
    public void testLayerAttachAxis() {

        Layer layer = factory.createLayer();

        try {
            layer.setXAxisTransform(null);
            fail("IllegalArgumentException should be thrown.");
        } catch (IllegalArgumentException ignored) {
            // exception is expected
        }
        try {
            layer.setYAxisTransform(null);
            fail("IllegalArgumentException should be thrown.");
        } catch (IllegalArgumentException ignored) {
            // exception is expected
        }

        AxisTransform xaxis = factory.createAxisTransform();
        AxisTransform yaxis = factory.createAxisTransform();

        try {
            layer.setXAxisTransform(xaxis);
            fail("IllegalArgumentException should be thrown.");
        } catch (IllegalArgumentException ignored) {
            // exception is expected
        }
        try {
            layer.setYAxisTransform(yaxis);
            fail("IllegalArgumentException should be thrown.");
        } catch (IllegalArgumentException ignored) {
            // exception is expected
        }
    }

}
