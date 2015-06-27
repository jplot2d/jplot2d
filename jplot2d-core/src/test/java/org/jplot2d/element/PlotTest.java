/**
 * Copyright 2010-2013 Jingjing Li.
 * <p/>
 * This file is part of jplot2d.
 * <p/>
 * jplot2d is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or any later version.
 * <p/>
 * jplot2d is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Lesser Public License for more details.
 * <p/>
 * You should have received a copy of the GNU Lesser General Public License
 * along with jplot2d. If not, see <http://www.gnu.org/licenses/>.
 */
package org.jplot2d.element;

import org.jplot2d.env.InterfaceInfo;
import org.junit.Test;

import static org.jplot2d.util.TestUtils.checkCollecionOrder;
import static org.jplot2d.util.TestUtils.checkPropertyInfoNames;
import static org.junit.Assert.*;

/**
 * Those test cases test methods on Title.
 *
 * @author Jingjing Li
 *
 */
public class PlotTest {

    private static final ElementFactory factory = ElementFactory.getInstance();

    @Test
    public void testInterfaceInfo() {
        InterfaceInfo iinfo = InterfaceInfo.loadInterfaceInfo(Plot.class);
        checkCollecionOrder(iinfo.getPropertyInfoGroupMap().keySet(), "Component", "Plot");
        checkPropertyInfoNames(iinfo.getPropertyInfoGroupMap().get("Component"), "visible", "cacheable", "selectable",
                "ZOrder", "color", "fontName", "fontStyle", "fontSize", "fontScale", "size", "bounds");
        checkPropertyInfoNames(iinfo.getPropertyInfoGroupMap().get("Plot"), "sizeMode", "containerSize", "scale",
                "layoutDirector", "preferredContentSize", "location", "contentSize");

        checkCollecionOrder(iinfo.getProfilePropertyInfoGroupMap().keySet(), "Component", "Plot");
        checkPropertyInfoNames(iinfo.getProfilePropertyInfoGroupMap().get("Component"), "visible", "cacheable",
                "selectable", "ZOrder", "color", "fontName", "fontStyle", "fontSize", "fontScale");
        checkPropertyInfoNames(iinfo.getProfilePropertyInfoGroupMap().get("Plot"), "preferredContentSize");
    }

    @Test
    public void testTitle() {
        Plot p = factory.createPlot();
        Title title = factory.createTitle("title");

        p.addTitle(title);
        assertSame(title.getEnvironment(), p.getEnvironment());

        p.removeTitle(title);
        assertNotSame(title.getEnvironment(), p.getEnvironment());

        // removing again throws an exception
        try {
            p.removeTitle(title);
            fail("An IllegalArgumentException should be thrown.");
        } catch (IllegalArgumentException ignored) {
            // exception is expected
        }
    }

    @Test
    public void testAddRemoveAxis() {
        Plot sp = factory.createSubplot();
        PlotAxis xaxis = factory.createAxis();
        PlotAxis yaxis = factory.createAxis();
        AxisTickManager xtm = xaxis.getTickManager();
        AxisTickManager ytm = yaxis.getTickManager();
        assertNotNull(xtm);
        assertNotNull(ytm);
        AxisTransform xarm = xtm.getAxisTransform();
        AxisTransform yarm = ytm.getAxisTransform();
        AxisRangeLockGroup xag = xarm.getLockGroup();
        AxisRangeLockGroup yag = yarm.getLockGroup();

        sp.addXAxis(xaxis);
        sp.addYAxis(yaxis);

        assertSame(xaxis.getTickManager(), xtm);
        assertSame(yaxis.getTickManager(), ytm);
        assertSame(xtm.getAxisTransform(), xarm);
        assertSame(ytm.getAxisTransform(), yarm);
        assertSame(xarm.getLockGroup(), xag);
        assertSame(yarm.getLockGroup(), yag);
        assertSame(xtm.getEnvironment(), sp.getEnvironment());
        assertSame(ytm.getEnvironment(), sp.getEnvironment());
        assertSame(xarm.getEnvironment(), sp.getEnvironment());
        assertSame(yarm.getEnvironment(), sp.getEnvironment());
        assertSame(xag.getEnvironment(), sp.getEnvironment());
        assertSame(yag.getEnvironment(), sp.getEnvironment());

        sp.removeXAxis(xaxis);
        sp.removeYAxis(yaxis);

        // the axis range manager and axis lock group should be removed together
        assertSame(xaxis.getTickManager(), xtm);
        assertSame(yaxis.getTickManager(), ytm);
        assertSame(xtm.getAxisTransform(), xarm);
        assertSame(ytm.getAxisTransform(), yarm);
        assertSame(xarm.getLockGroup(), xag);
        assertSame(yarm.getLockGroup(), yag);
        assertNotSame(xaxis.getEnvironment(), sp.getEnvironment());
        assertNotSame(yaxis.getEnvironment(), sp.getEnvironment());
        assertSame(xaxis.getEnvironment(), xtm.getEnvironment());
        assertSame(yaxis.getEnvironment(), ytm.getEnvironment());
        assertSame(xtm.getEnvironment(), xarm.getEnvironment());
        assertSame(ytm.getEnvironment(), yarm.getEnvironment());
        assertSame(xarm.getEnvironment(), xag.getEnvironment());
        assertSame(yarm.getEnvironment(), yag.getEnvironment());
    }

    @Test
    public void testAddLayerWithoutAxis() {
        Plot p = factory.createPlot();

        Layer layer = factory.createLayer();

        p.addLayer(layer);

        assertNull(layer.getXAxisTransform());
        assertNull(layer.getYAxisTransform());
    }

    @Test
    public void testAddRemoveLayer() {
        Plot p = factory.createPlot();

        PlotAxis xaxis = factory.createAxis();
        PlotAxis yaxis = factory.createAxis();
        Layer layer = factory.createLayer();
        p.addXAxis(xaxis);
        p.addYAxis(yaxis);

        p.addLayer(layer, xaxis, yaxis);
        assertArrayEquals(p.getLayers(), new Layer[]{layer});

        p.removeLayer(layer);
        assertArrayEquals(p.getLayers(), new Layer[0]);

    }

    @Test
    public void testAddAxisAndLayer() {
        Plot sp = factory.createSubplot();
        PlotAxis xaxis = factory.createAxis();
        PlotAxis yaxis = factory.createAxis();
        assertNotNull(xaxis.getTickManager());
        assertNotNull(yaxis.getTickManager());
        Layer layer = factory.createLayer();

        sp.addXAxis(xaxis);
        sp.addYAxis(yaxis);
        sp.addLayer(layer, xaxis.getTickManager().getAxisTransform(), yaxis.getTickManager().getAxisTransform());

        assertSame(xaxis.getParent(), sp);
        assertSame(yaxis.getParent(), sp);
        assertSame(layer.getParent(), sp);
        assertSame(layer.getXAxisTransform(), xaxis.getTickManager().getAxisTransform());
        assertSame(layer.getYAxisTransform(), yaxis.getTickManager().getAxisTransform());
        assertArrayEquals(xaxis.getTickManager().getAxisTransform().getLayers(), new Object[]{layer});
        assertArrayEquals(xaxis.getTickManager().getAxisTransform().getLayers(), new Object[]{layer});

        // set axis again
        layer.setAxesTransform(xaxis.getTickManager().getAxisTransform(), yaxis.getTickManager().getAxisTransform());

        assertSame(xaxis.getParent(), sp);
        assertSame(yaxis.getParent(), sp);
        assertSame(layer.getParent(), sp);
        assertSame(layer.getXAxisTransform(), xaxis.getTickManager().getAxisTransform());
        assertSame(layer.getYAxisTransform(), yaxis.getTickManager().getAxisTransform());
        assertArrayEquals(xaxis.getTickManager().getAxisTransform().getLayers(), new Object[]{layer});
        assertArrayEquals(xaxis.getTickManager().getAxisTransform().getLayers(), new Object[]{layer});
    }

    /**
     * Re-adding a removed layer back
     */
    @Test
    public void testRemoveAndAddLayerBack() {

        Plot sp = factory.createSubplot();
        PlotAxis xaxis = factory.createAxis();
        PlotAxis yaxis = factory.createAxis();
        assertNotNull(xaxis.getTickManager());
        assertNotNull(yaxis.getTickManager());
        Layer layer = factory.createLayer();

        sp.addXAxis(xaxis);
        sp.addYAxis(yaxis);
        sp.addLayer(layer, xaxis.getTickManager().getAxisTransform(), yaxis.getTickManager().getAxisTransform());

        sp.removeLayer(layer);

        assertNotSame(layer.getEnvironment(), sp.getEnvironment());
        assertNull(layer.getParent());
        assertNull(layer.getXAxisTransform());
        assertNull(layer.getYAxisTransform());
        assertArrayEquals(xaxis.getTickManager().getAxisTransform().getLayers(), new Object[0]);
        assertArrayEquals(xaxis.getTickManager().getAxisTransform().getLayers(), new Object[0]);

        sp.addLayer(layer, xaxis.getTickManager().getAxisTransform(), yaxis.getTickManager().getAxisTransform());

        assertSame(layer.getXAxisTransform(), xaxis.getTickManager().getAxisTransform());
        assertSame(layer.getYAxisTransform(), yaxis.getTickManager().getAxisTransform());
        assertArrayEquals(xaxis.getTickManager().getAxisTransform().getLayers(), new Object[]{layer});
        assertArrayEquals(xaxis.getTickManager().getAxisTransform().getLayers(), new Object[]{layer});
    }

    /**
     * An axis can't be removed when there is layer attach to it.
     */
    @Test
    public void testRemoveAxis() {

        Plot sp = factory.createSubplot();
        PlotAxis xaxis = factory.createAxis();
        PlotAxis yaxis = factory.createAxis();
        assertNotNull(xaxis.getTickManager());
        assertNotNull(yaxis.getTickManager());
        Layer layer = factory.createLayer();
        AxisTransform xva = xaxis.getTickManager().getAxisTransform();
        AxisTransform yva = yaxis.getTickManager().getAxisTransform();

        sp.addXAxis(xaxis);
        sp.addYAxis(yaxis);
        sp.addLayer(layer, xaxis.getTickManager().getAxisTransform(), yaxis.getTickManager().getAxisTransform());

        assertSame(xaxis.getEnvironment(), sp.getEnvironment());
        assertSame(yaxis.getEnvironment(), sp.getEnvironment());
        assertSame(xaxis.getParent(), sp);
        assertSame(yaxis.getParent(), sp);
        assertArrayEquals(sp.getXAxes(), new PlotAxis[]{xaxis});
        assertArrayEquals(sp.getYAxes(), new PlotAxis[]{yaxis});
        assertSame(layer.getXAxisTransform(), xva);
        assertSame(layer.getYAxisTransform(), yva);
        assertArrayEquals(xva.getLayers(), new Object[]{layer});
        assertArrayEquals(yva.getLayers(), new Object[]{layer});

        // removing axis not allowed
        try {
            sp.removeXAxis(xaxis);
            fail("IllegalStateException should be thrown");
        } catch (IllegalStateException ignored) {
            // exception is expected
        }
        try {
            sp.removeYAxis(yaxis);
            fail("IllegalStateException should be thrown");
        } catch (IllegalStateException ignored) {
            // exception is expected
        }

        // test the status after exception is thrown
        assertSame(xaxis.getEnvironment(), sp.getEnvironment());
        assertSame(yaxis.getEnvironment(), sp.getEnvironment());
        assertSame(xaxis.getParent(), sp);
        assertSame(yaxis.getParent(), sp);
        assertArrayEquals(sp.getXAxes(), new PlotAxis[]{xaxis});
        assertArrayEquals(sp.getYAxes(), new PlotAxis[]{yaxis});
        assertSame(layer.getXAxisTransform(), xva);
        assertSame(layer.getYAxisTransform(), yva);
        assertArrayEquals(xva.getLayers(), new Object[]{layer});
        assertArrayEquals(yva.getLayers(), new Object[]{layer});

        // try to detach viewport axis
        try {
            layer.setXAxisTransform(null);
            fail("IllegalArgumentException should be thrown");
        } catch (IllegalArgumentException ignored) {
            // exception is expected
        }
        try {
            layer.setYAxisTransform(null);
            fail("IllegalArgumentException should be thrown");
        } catch (IllegalArgumentException ignored) {
            // exception is expected
        }

        // remove layer will release viewport axes
        sp.removeLayer(layer);
        assertNull(layer.getXAxisTransform());
        assertNull(layer.getYAxisTransform());

        sp.removeXAxis(xaxis);
        sp.removeYAxis(yaxis);
    }

    /**
     * Remove subplot will remove axes and layers together.
     */
    @Test
    public void testRemoveSubplot() {
        Plot p = factory.createPlot();
        Plot sp = factory.createSubplot();
        PlotAxis xaxis = factory.createAxis();
        PlotAxis yaxis = factory.createAxis();
        assertNotNull(xaxis.getTickManager());
        assertNotNull(yaxis.getTickManager());
        Layer layer = factory.createLayer();

        p.addSubplot(sp, null);
        sp.addXAxis(xaxis);
        sp.addYAxis(yaxis);
        sp.addLayer(layer, xaxis.getTickManager().getAxisTransform(), yaxis.getTickManager().getAxisTransform());

        p.removeSubplot(sp);

        assertSame(layer.getXAxisTransform(), xaxis.getTickManager().getAxisTransform());
        assertSame(layer.getYAxisTransform(), yaxis.getTickManager().getAxisTransform());
        assertArrayEquals(xaxis.getTickManager().getAxisTransform().getLayers(), new Object[]{layer});
        assertArrayEquals(xaxis.getTickManager().getAxisTransform().getLayers(), new Object[]{layer});

    }

    @Test
    public void testRemoveSubplotWithAxisBeingAttached() {
        Plot p = factory.createPlot();
        Plot sp0 = factory.createSubplot();
        Plot sp1 = factory.createSubplot();
        PlotAxis xaxis = factory.createAxis();
        PlotAxis yaxis = factory.createAxis();
        assertNotNull(xaxis.getTickManager());
        assertNotNull(yaxis.getTickManager());
        Layer layer0 = factory.createLayer();
        Layer layer1 = factory.createLayer();

        p.addSubplot(sp0, null);
        p.addSubplot(sp1, null);
        sp0.addXAxis(xaxis);
        sp0.addYAxis(yaxis);
        sp0.addLayer(layer0, xaxis.getTickManager().getAxisTransform(), yaxis.getTickManager().getAxisTransform());
        sp1.addLayer(layer1, xaxis.getTickManager().getAxisTransform(), yaxis.getTickManager().getAxisTransform());

        try {
            p.removeSubplot(sp0);
            fail("IllegalStateException should be thrown");
        } catch (IllegalStateException ignored) {
            // exception is expected
        }

    }

}
