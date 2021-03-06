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

import org.jplot2d.env.ElementAddition;
import org.jplot2d.env.InterfaceInfo;
import org.junit.Test;

import static org.jplot2d.util.TestUtils.checkCollecionOrder;
import static org.jplot2d.util.TestUtils.checkPropertyInfoNames;
import static org.junit.Assert.*;

/**
 * Those test cases test methods on Axis and AxisRangeManager.
 *
 * @author Jingjing Li
 */
public class AxisTest {

    private static final ElementFactory factory = ElementFactory.getInstance();

    @Test
    public void testInterfaceInfo() {
        InterfaceInfo iinfo = InterfaceInfo.loadInterfaceInfo(PlotAxis.class);
        checkCollecionOrder(iinfo.getPropertyInfoGroupMap().keySet(), "Component", "Axis", "PlotAxis");
        checkPropertyInfoNames(iinfo.getPropertyInfoGroupMap().get("Component"), "visible", "cacheable", "selectable",
                "ZOrder", "color", "fontName", "fontStyle", "fontSize", "fontScale", "location", "size", "bounds");
        checkPropertyInfoNames(iinfo.getPropertyInfoGroupMap().get("Axis"), "length", "axisLineWidth",
                "tickVisible", "tickSide", "tickHeight", "minorTickHeight", "tickLineWidth",
                "labelVisible", "labelSide", "labelOrientation", "labelColor");
        checkPropertyInfoNames(iinfo.getPropertyInfoGroupMap().get("PlotAxis"), "orientation", "position", "gridLines", "minorGridLines");

        checkCollecionOrder(iinfo.getProfilePropertyInfoGroupMap().keySet(), "Component", "Axis", "PlotAxis");
        checkPropertyInfoNames(iinfo.getProfilePropertyInfoGroupMap().get("Component"), "visible", "cacheable",
                "selectable", "ZOrder", "color", "fontName", "fontStyle", "fontSize", "fontScale");
        checkPropertyInfoNames(iinfo.getProfilePropertyInfoGroupMap().get("Axis"), "axisLineWidth",
                "tickVisible", "tickSide", "tickHeight", "minorTickHeight", "tickLineWidth",
                "labelVisible", "labelSide", "labelOrientation", "labelColor");
        checkPropertyInfoNames(iinfo.getProfilePropertyInfoGroupMap().get("PlotAxis"), "position", "gridLines", "minorGridLines");
    }

    /**
     * Create axis will auto create an viewport axis and axis group.
     */
    @Test
    public void testCreateAxis() {
        PlotAxis axis = factory.createAxis();

        assertNull(axis.getSize());
        assertNull(axis.getBounds());
        assertNull(axis.getSelectableBounds());

        AxisTitle title = axis.getTitle();
        AxisTickManager tm = axis.getTickManager();
        assertNotNull(tm);
        AxisTransform arm = tm.getAxisTransform();
        assertNotNull(arm);
        AxisRangeLockGroup group = arm.getLockGroup();
        assertTrue(axis instanceof ElementAddition);
        assertTrue(title instanceof ElementAddition);
        assertTrue(tm instanceof ElementAddition);
        assertTrue(arm instanceof ElementAddition);
        assertTrue(group instanceof ElementAddition);

        assertSame(tm.getEnvironment(), axis.getEnvironment());
        assertSame(arm.getEnvironment(), axis.getEnvironment());
        assertSame(group.getEnvironment(), axis.getEnvironment());

        assertSame(tm.getParent(), axis);
        assertSame(arm.getParent(), tm);
        assertSame(group.getParent(), arm);

        assertArrayEquals(tm.getAxes(), new PlotAxis[]{axis});
        assertArrayEquals(arm.getTickManagers(), new AxisTickManager[]{tm});
        assertArrayEquals(group.getAxisTransforms(), new AxisTransform[]{arm});

        // set the same tick manager again
        axis.setTickManager(tm);
        assertSame(tm.getEnvironment(), axis.getEnvironment());
        assertSame(tm.getParent(), axis);
        assertArrayEquals(tm.getAxes(), new PlotAxis[]{axis});

        // set the same range manager again
        tm.setAxisTransform(arm);
        assertSame(arm.getEnvironment(), axis.getEnvironment());
        assertSame(arm.getParent(), tm);
        assertArrayEquals(arm.getTickManagers(), new AxisTickManager[]{tm});

        // set the same group again
        arm.setLockGroup(group);
        assertSame(group.getEnvironment(), arm.getEnvironment());
        assertSame(group.getParent(), arm);
        assertArrayEquals(group.getAxisTransforms(), new AxisTransform[]{arm});
    }

    @Test
    public void testSetAxisTickManager() {
        Plot sp = factory.createSubplot();
        PlotAxis xaxis = factory.createAxis();
        PlotAxis yaxis = factory.createAxis();
        assertNotNull(xaxis.getTickManager());

        // set before adding into the same environment
        try {
            yaxis.setTickManager(xaxis.getTickManager());
            fail("IllegalArgumentException should be thrown.");
        } catch (IllegalArgumentException e) {
            // The axis and group not in the save environment
        }

        // this is allowed if the viewport axis has no parent
        yaxis.setTickManager(factory.createAxisTickManager());
        AxisTickManager ytm = yaxis.getTickManager();
        assertNotNull(ytm);

        sp.addXAxis(xaxis);
        sp.addYAxis(yaxis);
        yaxis.setTickManager(xaxis.getTickManager());

        // the old y viewport axis should be removed from the environment
        assertNull(ytm.getParent());
        assertArrayEquals(ytm.getAxes(), new AxisTransform[0]);
        assertNotSame(ytm.getEnvironment(), sp.getEnvironment());

        assertSame(yaxis.getTickManager(), xaxis.getTickManager());
        assertArrayEquals(xaxis.getTickManager().getAxes(), new PlotAxis[]{xaxis, yaxis});
        assertSame(xaxis.getTickManager().getEnvironment(), sp.getEnvironment());

        // remove x axis
        sp.removeXAxis(xaxis);
        assertNull(xaxis.getTickManager());
        assertArrayEquals(yaxis.getTickManager().getAxes(), new PlotAxis[]{yaxis});
        assertSame(yaxis.getTickManager().getEnvironment(), sp.getEnvironment());

        // adding an axis with null range manager throws exception
        try {
        sp.addXAxis(xaxis);
            fail("IllegalArgumentException should be thrown.");
        } catch (IllegalArgumentException ignored) {
            // exception is expected
        }

        // set xaxis a new range manager
        AxisTickManager xntm = factory.createAxisTickManager();
        xaxis.setTickManager(xntm);

        assertSame(xaxis.getTickManager(), xntm);
        assertArrayEquals(xntm.getAxes(), new PlotAxis[]{xaxis});
        assertSame(xntm.getEnvironment(), xaxis.getEnvironment());

    }

    @Test
    public void testSetAxisRangeManager() {
        Plot sp = factory.createSubplot();
        PlotAxis xaxis = factory.createAxis();
        PlotAxis yaxis = factory.createAxis();
        AxisTickManager xtm = xaxis.getTickManager();
        AxisTickManager ytm = yaxis.getTickManager();
        assertNotNull(xtm);
        assertNotNull(ytm);
        assertNotNull(xtm.getAxisTransform());

        // set before adding into the same environment
        try {
            yaxis.getTickManager().setAxisTransform(xtm.getAxisTransform());
            fail("IllegalArgumentException should be thrown.");
        } catch (IllegalArgumentException e) {
            // The axis and group not in the save environment
        }

        // this is allowed if the viewport axis has no parent
        yaxis.getTickManager().setAxisTransform(factory.createAxisTransform());
        AxisTransform yva = yaxis.getTickManager().getAxisTransform();
        assertNotNull(xtm.getAxisTransform());
        assertNotNull(yva);

        sp.addXAxis(xaxis);
        sp.addYAxis(yaxis);
        ytm.setAxisTransform(xtm.getAxisTransform());

        // the old y viewport axis should be removed from the environment
        assertNull(yva.getParent());
        assertArrayEquals(yva.getTickManagers(), new AxisTickManager[0]);
        assertNotSame(yva.getEnvironment(), sp.getEnvironment());

        assertSame(ytm.getAxisTransform(), xtm.getAxisTransform());
        assertArrayEquals(xtm.getAxisTransform().getTickManagers(), new AxisTickManager[]{xtm, ytm});
        assertSame(xtm.getAxisTransform().getEnvironment(), sp.getEnvironment());

        // remove x axis
        sp.removeXAxis(xaxis);
        assertNull(xtm.getAxisTransform());
        assertNotNull(ytm.getAxisTransform());
        assertNotNull(ytm.getAxisTransform().getTickManagers());
        assertArrayEquals(ytm.getAxisTransform().getTickManagers(), new AxisTickManager[]{ytm});
        assertSame(ytm.getAxisTransform().getEnvironment(), sp.getEnvironment());

        // adding an axis with null range manager throws exception
        try {
            sp.addXAxis(xaxis);
            fail("IllegalArgumentException should be thrown.");
        } catch (IllegalArgumentException ignored) {
            // exception is expected
        }

        // set xaxis a new range manager
        AxisTransform xnag = factory.createAxisTransform();
        xtm.setAxisTransform(xnag);

        assertSame(xtm.getAxisTransform(), xnag);
        assertArrayEquals(xnag.getTickManagers(), new AxisTickManager[]{xtm});
        assertSame(xnag.getEnvironment(), xaxis.getEnvironment());

    }

    @Test
    public void testSetAxisLockGroup() {
        Plot sp = factory.createSubplot();
        PlotAxis xaxis = factory.createAxis();
        PlotAxis yaxis = factory.createAxis();
        assertNotNull(xaxis.getTickManager());
        assertNotNull(yaxis.getTickManager());
        AxisTransform xva = xaxis.getTickManager().getAxisTransform();
        AxisTransform yva = yaxis.getTickManager().getAxisTransform();
        assertNotNull(xva);
        assertNotNull(yva);
        AxisRangeLockGroup xag = xva.getLockGroup();
        AxisRangeLockGroup yag = yva.getLockGroup();
        assertNotNull(xag);
        assertNotNull(yag);

        // set before adding into the same environment
        try {
            yva.setLockGroup(xva.getLockGroup());
            fail("IllegalArgumentException should be thrown.");
        } catch (IllegalArgumentException e) {
            // The axis and group not in the save environment
        }

        sp.addXAxis(xaxis);
        sp.addYAxis(yaxis);
        xva.setLockGroup(yva.getLockGroup());

        // the old x group should be removed from the environment
        assertNull(xag.getParent());
        assertArrayEquals(xag.getAxisTransforms(), new AxisTransform[0]);
        assertNotSame(xag.getEnvironment(), sp.getEnvironment());

        assertSame(xva.getLockGroup(), yag);
        assertSame(yva.getLockGroup(), yag);
        assertArrayEquals(yag.getAxisTransforms(), new AxisTransform[]{yva, xva});
        assertSame(yag.getEnvironment(), sp.getEnvironment());

        // remove xaxis with its range manager
        sp.removeXAxis(xaxis);
        assertNull(xva.getLockGroup());
        assertArrayEquals(yag.getAxisTransforms(), new AxisTransform[]{yva});
        assertSame(xaxis.getEnvironment(), xva.getEnvironment());

        // adding an axis with null lock group throws exception
        try {
            sp.addXAxis(xaxis);
            fail("IllegalArgumentException should be thrown.");
        } catch (IllegalArgumentException e) {
            // exception is expected
        }

        // set xaxis a new group
        AxisRangeLockGroup xnag = factory.createAxisRangeLockGroup();
        xva.setLockGroup(xnag);
        assertSame(xva.getLockGroup(), xnag);
        assertArrayEquals(xnag.getAxisTransforms(), new AxisTransform[]{xva});
        assertSame(xnag.getEnvironment(), xaxis.getEnvironment());

        // add axis with new lock group
        sp.addXAxis(xaxis);
        assertSame(xva.getLockGroup(), xnag);
        assertArrayEquals(xnag.getAxisTransforms(), new AxisTransform[]{xva});
        assertSame(xnag.getEnvironment(), xaxis.getEnvironment());
        assertSame(xaxis.getEnvironment(), sp.getEnvironment());

    }

}
