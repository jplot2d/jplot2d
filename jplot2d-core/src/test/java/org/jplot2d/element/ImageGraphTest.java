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
import org.jplot2d.env.ElementAddition;
import org.jplot2d.env.InterfaceInfo;
import org.junit.Test;

import static org.jplot2d.util.TestUtils.checkCollecionOrder;
import static org.jplot2d.util.TestUtils.checkPropertyInfoNames;
import static org.junit.Assert.*;

/**
 * Those test cases test methods on ImageGraph and ImageMapping.
 *
 * @author Jingjing Li
 */
public class ImageGraphTest {

    private static final ElementFactory factory = ElementFactory.getInstance();

    @Test
    public void testInterfaceInfo() {
        InterfaceInfo iinfo = InterfaceInfo.loadInterfaceInfo(ImageGraph.class);
        checkCollecionOrder(iinfo.getPropertyInfoGroupMap().keySet(), "Component");
        checkPropertyInfoNames(iinfo.getPropertyInfoGroupMap().get("Component"), "visible", "cacheable", "selectable",
                "ZOrder", "color", "fontName", "fontStyle", "fontSize", "fontScale", "location", "size", "bounds");

        checkCollecionOrder(iinfo.getProfilePropertyInfoGroupMap().keySet(), "Component");
        checkPropertyInfoNames(iinfo.getProfilePropertyInfoGroupMap().get("Component"), "visible", "cacheable",
                "selectable", "ZOrder", "color", "fontName", "fontStyle", "fontSize", "fontScale");
    }

    /**
     * Create ImageGraph will auto create an ImageMapping.
     */
    @Test
    public void testCreateImageGraph() {
        ImageGraph graph = factory.createImageGraph((SingleBandImageData) null);

        assertNull(graph.getSize());
        assertNull(graph.getBounds());
        assertNull(graph.getSelectableBounds());

        ImageMapping mapping = graph.getMapping();
        assertTrue(graph instanceof ElementAddition);
        assertTrue(mapping instanceof ElementAddition);

        assertSame(mapping.getEnvironment(), graph.getEnvironment());

        assertSame(mapping.getParent(), graph);

        assertArrayEquals(mapping.getGraphs(), new ImageGraph[]{graph});

        // set the same mapping again
        graph.setMapping(mapping);
        assertSame(mapping.getEnvironment(), graph.getEnvironment());
        assertSame(mapping.getParent(), graph);
        assertArrayEquals(mapping.getGraphs(), new ImageGraph[]{graph});
    }

    @Test
    public void testSetMapping() {
        Plot p = factory.createSubplot();
        PlotAxis xaxis = factory.createAxis();
        PlotAxis yaxis = factory.createAxis();
        p.addXAxis(xaxis);
        p.addYAxis(yaxis);
        Layer layer = factory.createLayer();
        p.addLayer(layer, xaxis, yaxis);

        ImageGraph graph0 = factory.createImageGraph((SingleBandImageData) null);
        assertNotNull(graph0.getMapping());
        assertArrayEquals(graph0.getMapping().getGraphs(), new ImageGraph[]{graph0});

        // set mapping before added to a layer
        ImageMapping mapping1 = factory.createImageMapping();
        graph0.setMapping(mapping1);
        assertArrayEquals(graph0.getMapping().getGraphs(), new ImageGraph[]{graph0});
        assertSame(graph0.getMapping().getEnvironment(), graph0.getEnvironment());

        layer.addGraph(graph0);

        // set mapping after added to a layer
        ImageMapping mapping2 = factory.createImageMapping();
        graph0.setMapping(mapping2);
        assertArrayEquals(graph0.getMapping().getGraphs(), new ImageGraph[]{graph0});
        assertSame(graph0.getMapping().getEnvironment(), p.getEnvironment());
    }

    @Test
    public void testShareMapping() {
        Plot p = factory.createSubplot();
        PlotAxis xaxis = factory.createAxis();
        PlotAxis yaxis = factory.createAxis();
        p.addXAxis(xaxis);
        p.addYAxis(yaxis);
        Layer layer = factory.createLayer();
        p.addLayer(layer, xaxis, yaxis);

        ImageGraph graph0 = factory.createImageGraph((SingleBandImageData) null);
        ImageGraph graph1 = factory.createImageGraph((SingleBandImageData) null);
        assertNotNull(graph0.getMapping());
        assertNotNull(graph1.getMapping());

        // set before adding into the same environment
        try {
            graph1.setMapping(graph0.getMapping());
            fail("IllegalArgumentException should be thrown.");
        } catch (IllegalArgumentException e) {
            // The mapping not in the save environment
        }

        // this is allowed if the mapping has no parent
        ImageMapping mapping1 = factory.createImageMapping();
        graph1.setMapping(mapping1);

        layer.addGraph(graph0);
        layer.addGraph(graph1);

        // share the mapping0
        graph1.setMapping(graph0.getMapping());

        // the old mapping1 should be removed from the environment
        assertNull(mapping1.getParent());
        assertEquals(mapping1.getGraphs().length, 0);
        assertNotSame(mapping1.getEnvironment(), p.getEnvironment());

        assertSame(graph0.getMapping(), graph1.getMapping());
        assertArrayEquals(graph0.getMapping().getGraphs(), new ImageGraph[]{graph0, graph1});
        assertSame(graph0.getMapping().getEnvironment(), p.getEnvironment());

        // remove graph1
        layer.removeGraph(graph1);
        assertNull(graph1.getMapping());
        assertArrayEquals(graph0.getMapping().getGraphs(), new ImageGraph[]{graph0});
        assertSame(graph0.getMapping().getEnvironment(), p.getEnvironment());

        // adding an graph with null mapping
        layer.addGraph(graph1);
        assertNull(graph1.getMapping());

        // set mapping
        graph1.setMapping(mapping1);
        assertArrayEquals(graph1.getMapping().getGraphs(), new ImageGraph[]{graph1});
        assertSame(graph1.getMapping().getEnvironment(), p.getEnvironment());
    }


}
