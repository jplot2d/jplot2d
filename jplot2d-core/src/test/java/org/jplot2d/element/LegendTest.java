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

import org.jplot2d.env.InterfaceInfo;
import org.junit.Test;

import static org.jplot2d.util.TestUtils.checkCollecionOrder;
import static org.jplot2d.util.TestUtils.checkPropertyInfoNames;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

/**
 * Those test cases test methods of Legend.
 *
 * @author Jingjing Li
 */
public class LegendTest {

    private static final ElementFactory factory = ElementFactory.getInstance();

    @Test
    public void testInterfaceInfo() {
        InterfaceInfo iinfo = InterfaceInfo.loadInterfaceInfo(Legend.class);
        checkCollecionOrder(iinfo.getPropertyInfoGroupMap().keySet(), "Component", "Legend");
        checkPropertyInfoNames(iinfo.getPropertyInfoGroupMap().get("Component"), "visible", "cacheable", "selectable",
                "ZOrder", "color", "fontName", "fontStyle", "fontSize", "fontScale", "size", "bounds");
        checkPropertyInfoNames(iinfo.getPropertyInfoGroupMap().get("Legend"), "enabled", "position", "location",
                "HAlign", "VAlign", "columns", "rowSpacingFactor", "borderVisible", "movable");

        checkCollecionOrder(iinfo.getProfilePropertyInfoGroupMap().keySet(), "Component", "Legend");
        checkPropertyInfoNames(iinfo.getProfilePropertyInfoGroupMap().get("Component"), "visible", "cacheable",
                "selectable", "ZOrder", "color", "fontName", "fontStyle", "fontSize", "fontScale");
        checkPropertyInfoNames(iinfo.getProfilePropertyInfoGroupMap().get("Legend"), "enabled", "position",
                "rowSpacingFactor", "borderVisible", "movable");
    }

    @Test
    public void testEnableDisableLegend() {
        Plot p = factory.createPlot();
        Plot sp0 = factory.createSubplot();
        Plot sp1 = factory.createSubplot();
        PlotAxis xaxis0 = factory.createAxis();
        PlotAxis yaxis0 = factory.createAxis();
        PlotAxis xaxis1 = factory.createAxis();
        PlotAxis yaxis1 = factory.createAxis();
        Layer layer0 = factory.createLayer();
        Layer layer1 = factory.createLayer();
        XYGraph graph0 = factory.createXYGraph(new double[0], new double[0], "G0");
        XYGraph graph1 = factory.createXYGraph(new double[0], new double[0], "G1");

        p.addSubplot(sp0, null);
        p.addSubplot(sp1, null);
        sp0.addXAxis(xaxis0);
        sp0.addYAxis(yaxis0);
        sp0.addLayer(layer0, xaxis0, yaxis0);
        layer0.addGraph(graph0);
        sp1.addXAxis(xaxis1);
        sp1.addYAxis(yaxis1);
        sp1.addLayer(layer1, xaxis1, yaxis1);
        layer1.addGraph(graph1);

        assertEquals(p.getLegend().getItems().length, 0);
        assertEquals(sp0.getLegend().getItems().length, 1);
        assertSame(sp0.getLegend().getItems()[0], graph0.getLegendItem());
        assertSame(sp0.getLegend(), graph0.getLegendItem().getLegend());
        assertEquals(sp1.getLegend().getItems().length, 1);
        assertSame(sp1.getLegend().getItems()[0], graph1.getLegendItem());
        assertSame(sp1.getLegend(), graph1.getLegendItem().getLegend());

        // disable legend of sp0
        sp0.getLegend().setEnabled(false);
        assertEquals(p.getLegend().getItems().length, 1);
        assertSame(p.getLegend().getItems()[0], graph0.getLegendItem());
        assertSame(p.getLegend(), graph0.getLegendItem().getLegend());
        assertEquals(sp0.getLegend().getItems().length, 0);
        assertEquals(sp1.getLegend().getItems().length, 1);
        assertSame(sp1.getLegend().getItems()[0], graph1.getLegendItem());
        assertSame(sp1.getLegend(), graph1.getLegendItem().getLegend());

        // disable legend of sp1
        sp1.getLegend().setEnabled(false);
        assertEquals(p.getLegend().getItems().length, 2);
        assertSame(p.getLegend().getItems()[0], graph0.getLegendItem());
        assertSame(p.getLegend(), graph0.getLegendItem().getLegend());
        assertSame(p.getLegend().getItems()[1], graph1.getLegendItem());
        assertSame(p.getLegend(), graph1.getLegendItem().getLegend());
        assertEquals(sp0.getLegend().getItems().length, 0);
        assertEquals(sp1.getLegend().getItems().length, 0);

        // enable legend of sp1
        sp1.getLegend().setEnabled(true);
        assertEquals(p.getLegend().getItems().length, 1);
        assertSame(p.getLegend().getItems()[0], graph0.getLegendItem());
        assertSame(p.getLegend(), graph0.getLegendItem().getLegend());
        assertEquals(sp0.getLegend().getItems().length, 0);
        assertEquals(sp1.getLegend().getItems().length, 1);
        assertSame(sp1.getLegend().getItems()[0], graph1.getLegendItem());
        assertSame(sp1.getLegend(), graph1.getLegendItem().getLegend());
    }

    @Test
    public void testRemoveSubplotWithDisabledLegend() {
        Plot p = factory.createPlot();
        Plot sp0 = factory.createSubplot();
        Plot sp1 = factory.createSubplot();
        PlotAxis xaxis0 = factory.createAxis();
        PlotAxis yaxis0 = factory.createAxis();
        PlotAxis xaxis1 = factory.createAxis();
        PlotAxis yaxis1 = factory.createAxis();
        Layer layer0 = factory.createLayer();
        Layer layer1 = factory.createLayer();
        XYGraph graph0 = factory.createXYGraph(new double[0], new double[0], "G0");
        XYGraph graph1 = factory.createXYGraph(new double[0], new double[0], "G1");

        p.addSubplot(sp0, null);
        p.addSubplot(sp1, null);
        sp0.addXAxis(xaxis0);
        sp0.addYAxis(yaxis0);
        sp0.addLayer(layer0, xaxis0, yaxis0);
        layer0.addGraph(graph0);
        sp1.addXAxis(xaxis1);
        sp1.addYAxis(yaxis1);
        sp1.addLayer(layer1, xaxis1, yaxis1);
        layer1.addGraph(graph1);

        assertEquals(p.getLegend().getItems().length, 0);
        assertEquals(sp0.getLegend().getItems().length, 1);
        assertSame(sp0.getLegend().getItems()[0], graph0.getLegendItem());
        assertSame(sp0.getLegend(), graph0.getLegendItem().getLegend());
        assertEquals(sp1.getLegend().getItems().length, 1);
        assertSame(sp1.getLegend().getItems()[0], graph1.getLegendItem());
        assertSame(sp1.getLegend(), graph1.getLegendItem().getLegend());

        // disable legend of sp0
        sp0.getLegend().setEnabled(false);
        assertEquals(p.getLegend().getItems().length, 1);
        assertSame(p.getLegend().getItems()[0], graph0.getLegendItem());
        assertSame(p.getLegend(), graph0.getLegendItem().getLegend());
        assertEquals(sp0.getLegend().getItems().length, 0);
        assertEquals(sp1.getLegend().getItems().length, 1);
        assertSame(sp1.getLegend().getItems()[0], graph1.getLegendItem());
        assertSame(sp1.getLegend(), graph1.getLegendItem().getLegend());

        // disable legend of sp1
        sp1.getLegend().setEnabled(false);
        assertEquals(p.getLegend().getItems().length, 2);
        assertSame(p.getLegend().getItems()[0], graph0.getLegendItem());
        assertSame(p.getLegend(), graph0.getLegendItem().getLegend());
        assertSame(p.getLegend().getItems()[1], graph1.getLegendItem());
        assertSame(p.getLegend(), graph1.getLegendItem().getLegend());
        assertEquals(sp0.getLegend().getItems().length, 0);
        assertEquals(sp1.getLegend().getItems().length, 0);

        // remove sp1
        p.removeSubplot(sp1);
        assertEquals(p.getLegend().getItems().length, 1);
        assertSame(p.getLegend().getItems()[0], graph0.getLegendItem());
        assertSame(p.getLegend(), graph0.getLegendItem().getLegend());
        assertEquals(sp0.getLegend().getItems().length, 0);
        assertEquals(sp1.getLegend().getItems().length, 1);
        assertSame(sp1.getLegend().getItems()[0], graph1.getLegendItem());
        assertSame(sp1.getLegend(), graph1.getLegendItem().getLegend());
    }

}
