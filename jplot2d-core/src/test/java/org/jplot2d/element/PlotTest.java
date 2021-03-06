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
import static org.junit.Assert.*;

/**
 * Those test cases test methods on Title.
 *
 * @author Jingjing Li
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
        assertNotNull(xarm);
        assertNotNull(yarm);
        AxisRangeLockGroup xag = xarm.getLockGroup();
        AxisRangeLockGroup yag = yarm.getLockGroup();
        assertNotNull(xag);
        assertNotNull(yag);

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
        assertNotNull(xaxis.getTickManager().getAxisTransform());
        assertNotNull(yaxis.getTickManager().getAxisTransform());
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
        assertArrayEquals(yaxis.getTickManager().getAxisTransform().getLayers(), new Object[]{layer});

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
        assertNotNull(xaxis.getTickManager().getAxisTransform());
        assertNotNull(yaxis.getTickManager().getAxisTransform());
        Layer layer = factory.createLayer();

        sp.addXAxis(xaxis);
        sp.addYAxis(yaxis);
        sp.addLayer(layer, xaxis.getTickManager().getAxisTransform(), yaxis.getTickManager().getAxisTransform());

        sp.removeLayer(layer);

        assertNotSame(layer.getEnvironment(), sp.getEnvironment());
        assertNull(layer.getParent());
        assertNull(layer.getXAxisTransform());
        assertNull(layer.getYAxisTransform());
        assertSame(xaxis.getTickManager().getAxisTransform().getLayers().length, 0);
        assertSame(yaxis.getTickManager().getAxisTransform().getLayers().length, 0);

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
        AxisTransform xva = xaxis.getTickManager().getAxisTransform();
        AxisTransform yva = yaxis.getTickManager().getAxisTransform();
        assertNotNull(xva);
        assertNotNull(yva);
        Layer layer = factory.createLayer();

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
            //noinspection ConstantConditions
            layer.setXAxisTransform(null);
            fail("IllegalArgumentException should be thrown");
        } catch (IllegalArgumentException ignored) {
            // exception is expected
        }
        try {
            //noinspection ConstantConditions
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
        PlotAxis[] xaxes = factory.createAxes(2);
        PlotAxis[] yaxes = factory.createAxes(2);
        assertNotNull(xaxes[0].getTickManager());
        assertNotNull(xaxes[1].getTickManager());
        assertNotNull(yaxes[0].getTickManager());
        assertNotNull(yaxes[1].getTickManager());
        AxisTransform xatf = xaxes[0].getTickManager().getAxisTransform();
        AxisTransform yatf = yaxes[0].getTickManager().getAxisTransform();
        assertNotNull(xatf);
        assertNotNull(yatf);
        AxisRangeLockGroup xrlg = xatf.getLockGroup();
        AxisRangeLockGroup yrlg = yatf.getLockGroup();
        Layer layer = factory.createLayer();

        p.addSubplot(sp, null);
        sp.addXAxes(xaxes);
        sp.addYAxes(yaxes);
        sp.addLayer(layer, xatf, yatf);

        p.removeSubplot(sp);

        assertSame(xatf, xaxes[0].getTickManager().getAxisTransform());
        assertSame(xatf, xaxes[1].getTickManager().getAxisTransform());
        assertSame(yatf, yaxes[0].getTickManager().getAxisTransform());
        assertSame(yatf, yaxes[1].getTickManager().getAxisTransform());
        assertSame(xatf, layer.getXAxisTransform());
        assertSame(yatf, layer.getYAxisTransform());
        assertArrayEquals(xatf.getLayers(), new Object[]{layer});
        assertArrayEquals(xatf.getLayers(), new Object[]{layer});
        assertArrayEquals(yatf.getLayers(), new Object[]{layer});
        assertArrayEquals(yatf.getLayers(), new Object[]{layer});

        assertSame(xrlg, xatf.getLockGroup());
        assertSame(yrlg, yatf.getLockGroup());
    }

    /**
     * Remove subplot will remove axes and shared AxisTransform.
     */
    @Test
    public void testRemoveSubplot2() {
        Plot p = factory.createPlot();
        Plot sp = factory.createSubplot();
        PlotAxis[] xaxes = factory.createAxes(2);
        PlotAxis yaxis0 = factory.createAxis();
        PlotAxis yaxis1 = factory.createAxis();
        assertNotNull(xaxes[0].getTickManager());
        assertNotNull(xaxes[1].getTickManager());
        assertNotNull(yaxis0.getTickManager());
        assertNotNull(yaxis1.getTickManager());
        AxisTransform xatf = xaxes[0].getTickManager().getAxisTransform();
        AxisTransform yatf = yaxis0.getTickManager().getAxisTransform();
        assertNotNull(xatf);
        assertNotNull(yatf);
        AxisRangeLockGroup xrlg = xatf.getLockGroup();
        AxisRangeLockGroup yrlg = yatf.getLockGroup();

        p.addSubplot(sp, null);
        sp.addXAxes(xaxes);
        sp.addYAxis(yaxis0);
        sp.addYAxis(yaxis1);
        yaxis1.getTickManager().setAxisTransform(yatf);

        Layer layer = factory.createLayer();
        sp.addLayer(layer, xatf, yatf);

        p.removeSubplot(sp);

        assertSame(xatf, xaxes[0].getTickManager().getAxisTransform());
        assertSame(xatf, xaxes[1].getTickManager().getAxisTransform());
        assertSame(yatf, yaxis0.getTickManager().getAxisTransform());
        assertSame(yatf, yaxis1.getTickManager().getAxisTransform());
        assertSame(xatf, layer.getXAxisTransform());
        assertSame(yatf, layer.getYAxisTransform());
        assertArrayEquals(xatf.getLayers(), new Object[]{layer});
        assertArrayEquals(xatf.getLayers(), new Object[]{layer});
        assertArrayEquals(yatf.getLayers(), new Object[]{layer});
        assertArrayEquals(yatf.getLayers(), new Object[]{layer});

        assertSame(xrlg, xatf.getLockGroup());
        assertSame(yrlg, yatf.getLockGroup());
    }

    /**
     * Remove subplot will remove axes and shared AxisRangeLockGroup.
     */
    @Test
    public void testRemoveSubplot3() {
        Plot p = factory.createPlot();
        Plot sp = factory.createSubplot();
        PlotAxis[] xaxes = factory.createAxes(2);
        PlotAxis yaxis0 = factory.createAxis();
        PlotAxis yaxis1 = factory.createAxis();
        assertNotNull(xaxes[0].getTickManager());
        assertNotNull(xaxes[1].getTickManager());
        assertNotNull(yaxis0.getTickManager());
        assertNotNull(yaxis1.getTickManager());
        AxisTransform xatf = xaxes[0].getTickManager().getAxisTransform();
        AxisTransform yatf0 = yaxis0.getTickManager().getAxisTransform();
        AxisTransform yatf1 = yaxis1.getTickManager().getAxisTransform();
        assertNotNull(xatf);
        assertNotNull(yatf0);
        assertNotNull(yatf1);
        AxisRangeLockGroup xrlg = xatf.getLockGroup();
        AxisRangeLockGroup yrlg = yatf0.getLockGroup();
        assertNotNull(yrlg);

        p.addSubplot(sp, null);
        sp.addXAxes(xaxes);
        sp.addYAxis(yaxis0);
        sp.addYAxis(yaxis1);
        yaxis1.getTickManager().getAxisTransform().setLockGroup(yrlg);

        Layer layer = factory.createLayer();
        sp.addLayer(layer, xatf, yatf0);

        p.removeSubplot(sp);

        assertSame(xatf, xaxes[0].getTickManager().getAxisTransform());
        assertSame(xatf, xaxes[1].getTickManager().getAxisTransform());
        assertSame(yatf0, yaxis0.getTickManager().getAxisTransform());
        assertSame(yatf1, yaxis1.getTickManager().getAxisTransform());
        assertSame(xatf, layer.getXAxisTransform());
        assertSame(yatf0, layer.getYAxisTransform());
        assertArrayEquals(xatf.getLayers(), new Object[]{layer});
        assertArrayEquals(xatf.getLayers(), new Object[]{layer});
        assertArrayEquals(yatf0.getLayers(), new Object[]{layer});
        assertArrayEquals(yatf0.getLayers(), new Object[]{layer});

        assertSame(xrlg, xatf.getLockGroup());
        assertSame(yrlg, yatf0.getLockGroup());
        assertSame(yrlg, yatf1.getLockGroup());
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
