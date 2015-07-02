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
 * Those test cases test methods of Legend.
 *
 * @author Jingjing Li
 */
public class ColorbarTest {

    private static final ElementFactory factory = ElementFactory.getInstance();

    @Test
    public void testInterfaceInfo() {
        InterfaceInfo iinfo = InterfaceInfo.loadInterfaceInfo(Colorbar.class);
        checkCollecionOrder(iinfo.getPropertyInfoGroupMap().keySet(), "Component", "Colorbar");
        checkPropertyInfoNames(iinfo.getPropertyInfoGroupMap().get("Component"), "visible", "cacheable", "selectable",
                "ZOrder", "color", "fontName", "fontStyle", "fontSize", "fontScale", "location", "size", "bounds");
        checkPropertyInfoNames(iinfo.getPropertyInfoGroupMap().get("Colorbar"), "position", "gap", "barWidth", "borderLineWidth");

        checkCollecionOrder(iinfo.getProfilePropertyInfoGroupMap().keySet(), "Component", "Colorbar");
        checkPropertyInfoNames(iinfo.getProfilePropertyInfoGroupMap().get("Component"), "visible", "cacheable",
                "selectable", "ZOrder", "color", "fontName", "fontStyle", "fontSize", "fontScale");
        checkPropertyInfoNames(iinfo.getProfilePropertyInfoGroupMap().get("Colorbar"), "position", "gap", "barWidth", "borderLineWidth");
    }

    @Test
    public void testCreateColorbar() {
        Colorbar colorbar = factory.createColorbar();
        assertNotNull(colorbar.getInnerAxis());
        assertNotNull(colorbar.getOuterAxis());
        assertNotNull(colorbar.getInnerAxis().getTickManager());
        assertNotNull(colorbar.getOuterAxis().getTickManager());
        assertSame(colorbar.getInnerAxis().getTickManager(), colorbar.getOuterAxis().getTickManager());
        assertNotNull(colorbar.getAxisTransform());
        assertSame(colorbar.getInnerAxis().getTickManager().getAxisTransform(), colorbar.getAxisTransform());
        assertArrayEquals(colorbar.getAxisTransform().getTickManagers(), new AxisTickManager[]{colorbar.getInnerAxis().getTickManager()});
    }

    @Test
    public void testAddRemoveColorbar() {
        Plot p = factory.createPlot();
        Colorbar colorbar = factory.createColorbar();

        p.addColorbar(colorbar);
        assertSame(colorbar.getEnvironment(), p.getEnvironment());
        assertArrayEquals(p.getColorbars(), new Colorbar[]{colorbar});

        p.removeColorbar(colorbar);
        assertNotSame(colorbar.getEnvironment(), p.getEnvironment());

        // removing again throws an exception
        try {
            p.removeColorbar(colorbar);
            fail("An IllegalArgumentException should be thrown.");
        } catch (IllegalArgumentException ignored) {
            // exception is expected
        }

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

        layer.addGraph(graph0);

        Colorbar colorbar = factory.createColorbar();
        p.addColorbar(colorbar);
        colorbar.setImageMapping(graph0.getMapping());

        assertSame(colorbar.getImageMapping(), graph0.getMapping());
    }


}
