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
package org.jplot2d.element.impl;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.jplot2d.util.TestUtils.checkDimension2D;
import static org.jplot2d.util.TestUtils.checkDouble;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Jingjing Li
 */
public class PlotImplTest {

    @Test
    public void testConstructor() {
        PlotImpl p = new PlotImpl();
        checkDimension2D(p.getSize(), 640, 480);
        checkDouble(p.getScale(), 1);
    }

    @Test
    public void testAddXAxis() {
        PlotImpl p = new PlotImpl();
        PlotAxisEx axis = mock(PlotAxisEx.class);
        when(axis.canContribute()).thenReturn(true);

        try {
            p.addXAxis(axis);
            fail("IllegalArgumentException should be thrown.");
        } catch (IllegalArgumentException ignored) {
            // exception is expected. catch and ignore
        }

        AxisTickManagerEx atm = mock(AxisTickManagerEx.class);
        when(axis.getTickManager()).thenReturn(atm);
        try {
            p.addXAxis(axis);
            fail("IllegalArgumentException should be thrown.");
        } catch (IllegalArgumentException ignored) {
            // exception is expected. catch and ignore
        }

        AxisTransformEx arm = mock(AxisTransformEx.class);
        when(atm.getAxisTransform()).thenReturn(arm);
        try {
            p.addXAxis(axis);
            fail("IllegalArgumentException should be thrown.");
        } catch (IllegalArgumentException ignored) {
            // exception is expected. catch and ignore
        }

        AxisRangeLockGroupEx alg = mock(AxisRangeLockGroupEx.class);
        when(arm.getLockGroup()).thenReturn(alg);
        p.addXAxis(axis);
    }

    @Test
    public void testAddYAxis() {
        PlotImpl p = new PlotImpl();
        PlotAxisEx axis = mock(PlotAxisEx.class);
        when(axis.canContribute()).thenReturn(true);

        try {
            p.addYAxis(axis);
            fail("IllegalArgumentException should be thrown.");
        } catch (IllegalArgumentException ignored) {
            // exception is expected. catch and ignore
        }

        AxisTickManagerEx atm = mock(AxisTickManagerEx.class);
        when(axis.getTickManager()).thenReturn(atm);
        try {
            p.addYAxis(axis);
            fail("IllegalArgumentException should be thrown.");
        } catch (IllegalArgumentException ignored) {
            // exception is expected. catch and ignore
        }

        AxisTransformEx arm = mock(AxisTransformEx.class);
        when(atm.getAxisTransform()).thenReturn(arm);
        try {
            p.addYAxis(axis);
            fail("IllegalArgumentException should be thrown.");
        } catch (IllegalArgumentException ignored) {
            // exception is expected. catch and ignore
        }

        AxisRangeLockGroupEx alg = mock(AxisRangeLockGroupEx.class);
        when(arm.getLockGroup()).thenReturn(alg);
        p.addYAxis(axis);
    }

    /**
     * Test all methods which can trigger redraw()
     */
    @Test
    public void testRedraw() {
        PlotImpl p = new PlotImpl();
        p.setCacheable(true);
        assertFalse(p.isRedrawNeeded());

        // set location on an empty plot does not redraw
        p.setLocation(0, 0);
        assertFalse(p.isRedrawNeeded());
        p.setLocation(1, 1);
        assertFalse(p.isRedrawNeeded());

        // parent paper transform changed on an empty plot does not redraw
        p.parentPaperTransformChanged();
        assertFalse(p.isRedrawNeeded());

        TitleEx title = mock(TitleEx.class);
        when(title.isVisible()).thenReturn(true);
        when(title.canContribute()).thenReturn(true);
        when(((ComponentEx) title).getParent()).thenReturn(p);
        p.addTitle(title);
        assertTrue(p.isRedrawNeeded());
        p.setRedrawNeeded(false);

        p.setLocation(1, 2);
        assertTrue(p.isRedrawNeeded());
        p.setRedrawNeeded(false);

        p.parentPaperTransformChanged();
        assertTrue(p.isRedrawNeeded());
        p.setRedrawNeeded(false);

        PlotAxisEx xaxis = mock(PlotAxisEx.class);
        AxisTickManagerEx xatm = mock(AxisTickManagerEx.class);
        AxisTransformEx xarm = mock(AxisTransformEx.class);
        AxisRangeLockGroupEx xalg = mock(AxisRangeLockGroupEx.class);
        when(xaxis.isVisible()).thenReturn(true);
        when(xaxis.canContribute()).thenReturn(true);
        when(xaxis.getTickManager()).thenReturn(xatm);
        when(xatm.getAxisTransform()).thenReturn(xarm);
        when(xarm.getLockGroup()).thenReturn(xalg);
        when(((ComponentEx) xaxis).getParent()).thenReturn(p);
        assertNotNull(xaxis.getTickManager());

        p.addXAxis(xaxis);
        assertTrue(p.isRedrawNeeded());
        p.setRedrawNeeded(false);

        PlotAxisEx yaxis = mock(PlotAxisEx.class);
        AxisTickManagerEx yatm = mock(AxisTickManagerEx.class);
        AxisTransformEx yarm = mock(AxisTransformEx.class);
        AxisRangeLockGroupEx yalg = mock(AxisRangeLockGroupEx.class);
        when(yaxis.isVisible()).thenReturn(true);
        when(yaxis.canContribute()).thenReturn(true);
        when(yaxis.getTickManager()).thenReturn(yatm);
        when(yatm.getAxisTransform()).thenReturn(yarm);
        when(yarm.getLockGroup()).thenReturn(yalg);
        when(((ComponentEx) yaxis).getParent()).thenReturn(p);
        assertNotNull(yaxis.getTickManager());

        p.addYAxis(yaxis);
        assertTrue(p.isRedrawNeeded());
        p.setRedrawNeeded(false);

        // add an empty layer
        LayerEx layer0 = mock(LayerEx.class);
        when(layer0.isVisible()).thenReturn(true);
        when(layer0.canContribute()).thenReturn(false);
        when(layer0.getGraphs()).thenReturn(new GraphEx[0]);
        when(layer0.getComponents()).thenReturn(new ComponentEx[0]);
        when(((ComponentEx) layer0).getParent()).thenReturn(p);
        p.addLayer(layer0, xaxis.getTickManager().getAxisTransform(), yaxis.getTickManager().getAxisTransform());
        assertFalse(p.isRedrawNeeded());

        GraphEx graph1 = mock(GraphEx.class);
        when(graph1.isVisible()).thenReturn(true);
        when(graph1.canContribute()).thenReturn(true);
        LayerEx layer1 = mock(LayerEx.class);
        when(layer1.isVisible()).thenReturn(true);
        when(layer1.canContribute()).thenReturn(false);
        when(layer1.getGraphs()).thenReturn(new GraphEx[]{graph1});
        when(layer1.getComponents()).thenReturn(new ComponentEx[]{graph1});
        when(((ComponentEx) graph1).getParent()).thenReturn(layer1);
        when(((ComponentEx) layer1).getParent()).thenReturn(p);
        p.addLayer(layer1, xaxis.getTickManager().getAxisTransform(), yaxis.getTickManager().getAxisTransform());
        assertTrue(p.isRedrawNeeded());
        p.setRedrawNeeded(false);

        p.removeLayer(layer1);
        assertTrue(p.isRedrawNeeded());
        p.setRedrawNeeded(false);

        p.removeLayer(layer0);
        assertFalse(p.isRedrawNeeded());

        p.removeXAxis(xaxis);
        assertTrue(p.isRedrawNeeded());
        p.setRedrawNeeded(false);

        p.removeYAxis(yaxis);
        assertTrue(p.isRedrawNeeded());
        p.setRedrawNeeded(false);

        p.removeTitle(title);
        assertTrue(p.isRedrawNeeded());
        p.setRedrawNeeded(false);
    }

    /**
     * Test all methods which can trigger invalidate()
     */
    @Test
    public void testInvalidateByXAxis() {
        PlotImpl p = new PlotImpl();
        assertTrue(p.isValid());

        // add or remove invisible axis does not invalid
        PlotAxisEx xaxis0 = mock(PlotAxisEx.class);
        AxisTickManagerEx xatm0 = mock(AxisTickManagerEx.class);
        AxisTransformEx xarm0 = mock(AxisTransformEx.class);
        AxisRangeLockGroupEx xalg0 = mock(AxisRangeLockGroupEx.class);
        when(xaxis0.getTickManager()).thenReturn(xatm0);
        when(xatm0.getAxisTransform()).thenReturn(xarm0);
        when(xarm0.getLockGroup()).thenReturn(xalg0);
        p.addXAxis(xaxis0);
        assertTrue(p.isValid());
        p.removeXAxis(xaxis0);
        assertTrue(p.isValid());

        // add or remove visible axis does invalid
        PlotAxisEx xaxis = mock(PlotAxisEx.class);
        AxisTickManagerEx xatm = mock(AxisTickManagerEx.class);
        AxisTransformEx xarm = mock(AxisTransformEx.class);
        AxisRangeLockGroupEx xalg = mock(AxisRangeLockGroupEx.class);
        when(xaxis.getTickManager()).thenReturn(xatm);
        when(xatm.getAxisTransform()).thenReturn(xarm);
        when(xarm.getLockGroup()).thenReturn(xalg);
        when(xaxis.isVisible()).thenReturn(true);
        when(xaxis.canContribute()).thenReturn(true);
        p.addXAxis(xaxis);
        assertFalse(p.isValid());
        p.validate();
        p.removeXAxis(xaxis);
        assertFalse(p.isValid());
        p.validate();
    }

    @Test
    public void testInvalidateByYAxis() {
        PlotImpl p = new PlotImpl();
        assertTrue(p.isValid());

        PlotAxisEx yaxis0 = mock(PlotAxisEx.class);
        AxisTickManagerEx yatm0 = mock(AxisTickManagerEx.class);
        AxisTransformEx yarm0 = mock(AxisTransformEx.class);
        AxisRangeLockGroupEx yalg0 = mock(AxisRangeLockGroupEx.class);
        when(yaxis0.getTickManager()).thenReturn(yatm0);
        when(yatm0.getAxisTransform()).thenReturn(yarm0);
        when(yarm0.getLockGroup()).thenReturn(yalg0);
        p.addYAxis(yaxis0);
        assertTrue(p.isValid());
        p.validate();
        p.removeYAxis(yaxis0);
        assertTrue(p.isValid());

        PlotAxisEx yaxis = mock(PlotAxisEx.class);
        AxisTickManagerEx yatm = mock(AxisTickManagerEx.class);
        AxisTransformEx yarm = mock(AxisTransformEx.class);
        AxisRangeLockGroupEx yalg = mock(AxisRangeLockGroupEx.class);
        when(yaxis.getTickManager()).thenReturn(yatm);
        when(yatm.getAxisTransform()).thenReturn(yarm);
        when(yarm.getLockGroup()).thenReturn(yalg);
        when(yaxis.isVisible()).thenReturn(true);
        when(yaxis.canContribute()).thenReturn(true);
        p.addYAxis(yaxis);
        assertFalse(p.isValid());
        p.validate();
        p.removeYAxis(yaxis);
        assertFalse(p.isValid());
        p.validate();
    }

    @Test
    public void testInvalidateBySubplot() {
        PlotImpl p = new PlotImpl();
        assertTrue(p.isValid());

        PlotImpl sp0 = new PlotImpl();
        p.addSubplot(sp0, null);
        assertFalse(p.isValid());
        p.validate();
        p.removeSubplot(sp0);
        assertFalse(p.isValid());
        p.validate();

        PlotImpl spInvisible = new PlotImpl();
        spInvisible.setVisible(false);
        p.addSubplot(spInvisible, null);
        assertTrue(p.isValid());
        p.removeSubplot(spInvisible);
        assertTrue(p.isValid());
        p.validate();
    }

    /**
     * Test the copy map and parent relationship in copied plot
     */
    @Test
    public void testCopyStructure() {
        PlotImpl p = new PlotImpl();
        PlotImpl sp = new PlotImpl();

        p.addSubplot(sp, null);

        AxisRangeLockGroupImpl xgroup = new AxisRangeLockGroupImpl();
        AxisTransformImpl xrm = new AxisTransformImpl();
        xrm.setLockGroup(xgroup);
        AxisTickManagerImpl xtm = new AxisTickManagerImpl();
        xtm.setAxisTransform(xrm);
        PlotAxisImpl x = new PlotAxisImpl();
        x.setTickManager(xtm);

        AxisRangeLockGroupImpl ygroup = new AxisRangeLockGroupImpl();
        AxisTransformImpl yrm = new AxisTransformImpl();
        yrm.setLockGroup(ygroup);
        AxisTickManagerImpl ytm = new AxisTickManagerImpl();
        ytm.setAxisTransform(yrm);
        PlotAxisImpl y = new PlotAxisImpl();
        y.setTickManager(ytm);

        p.addXAxis(x);
        p.addYAxis(y);
        LayerImpl layer = new LayerImpl();
        p.addLayer(layer, x, y);
        XYGraphImpl gp = new XYGraphImpl();
        layer.addGraph(gp);

        Map<ElementEx, ElementEx> orig2copyMap = new HashMap<>();
        PlotImpl p2 = p.copyStructure(orig2copyMap);
        // copy properties
        for (Map.Entry<ElementEx, ElementEx> me : orig2copyMap.entrySet()) {
            me.getValue().copyFrom(me.getKey());
        }

        PlotAxisEx x2 = p2.getXAxis(0);
        PlotAxisEx y2 = p2.getYAxis(0);

        assertNotNull(x.getTickManager());
        assertNotNull(x.getTickManager().getAxisTransform());
        assertNotNull(x.getTickManager().getAxisTransform().getLockGroup());
        assertNotNull(y.getTickManager());
        assertNotNull(y.getTickManager().getAxisTransform());
        assertNotNull(y.getTickManager().getAxisTransform().getLockGroup());
        assertNotNull(x2.getTickManager());
        assertNotNull(x2.getTickManager().getAxisTransform());
        assertNotNull(x2.getTickManager().getAxisTransform().getLockGroup());
        assertNotNull(y2.getTickManager());
        assertNotNull(y2.getTickManager().getAxisTransform());
        assertNotNull(y2.getTickManager().getAxisTransform().getLockGroup());

        // check copy map
        assertEquals(orig2copyMap.size(), 19);
        assertSame(p2, orig2copyMap.get(p));
        assertSame(p2.getMargin(), orig2copyMap.get(p.getMargin()));
        assertSame(p2.getLegend(), orig2copyMap.get(p.getLegend()));
        assertSame(p2.getSubplot(0), orig2copyMap.get(p.getSubplot(0)));
        assertSame(x2, orig2copyMap.get(x));
        assertSame(x2.getTickManager(), orig2copyMap.get(p.getXAxis(0).getTickManager()));
        assertSame(x2.getTickManager().getAxisTransform(), orig2copyMap.get(x.getTickManager().getAxisTransform()));
        assertSame(x2.getTickManager().getAxisTransform().getLockGroup(), orig2copyMap.get(x.getTickManager().getAxisTransform().getLockGroup()));
        assertSame(y2, orig2copyMap.get(y));
        assertSame(y2.getTickManager(), orig2copyMap.get(p.getYAxis(0).getTickManager()));
        assertSame(y2.getTickManager().getAxisTransform(), orig2copyMap.get(y.getTickManager().getAxisTransform()));
        assertSame(y2.getTickManager().getAxisTransform().getLockGroup(), orig2copyMap.get(y.getTickManager().getAxisTransform().getLockGroup()));

        assertSame(p2.getLayer(0), orig2copyMap.get(p.getLayer(0)));
        assertSame(p2.getLayer(0).getGraph(0), orig2copyMap.get(p.getLayer(0).getGraph(0)));

        // check parent
        assertSame(p2, p2.getMargin().getParent());
        assertSame(p2, p2.getLegend().getParent());
        assertSame(p2, p2.getSubplot(0).getParent());
        assertSame(p2, x2.getParent());
        assertSame(x2, x2.getTickManager().getParent());
        assertSame(x2.getTickManager(), x2.getTickManager().getAxisTransform().getParent());
        assertSame(x2.getTickManager().getAxisTransform(), x2.getTickManager().getAxisTransform().getLockGroup().getParent());
        assertSame(p2, y2.getParent());

        assertSame(p2, p2.getLayer(0).getParent());
        assertSame(p2.getLayer(0), p2.getLayer(0).getGraph(0).getParent());
        assertSame(p2.getLayer(0).getGraph(0), ((XYGraphEx) p2.getLayer(0).getGraph(0)).getLegendItem().getParent());

        // check legend item
        assertSame(p2.getLegend().getItems()[0], ((XYGraphEx) p2.getLayer(0).getGraph(0)).getLegendItem());
        assertSame(p2.getLegend(), ((XYGraphEx) p2.getLayer(0).getGraph(0)).getLegendItem().getLegend());

        // check link
        assertSame(p2.getLayer(0).getXAxisTransform(), x2.getTickManager().getAxisTransform());
        assertSame(p2.getLayer(0).getYAxisTransform(), y2.getTickManager().getAxisTransform());

    }

}
