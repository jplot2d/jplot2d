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

import org.jplot2d.element.ElementFactory;
import org.jplot2d.element.Plot;
import org.jplot2d.env.RenderEnvironment;
import org.jplot2d.layout.BoundsConstraint;
import org.jplot2d.layout.GridConstraint;
import org.jplot2d.layout.GridLayoutDirector;
import org.jplot2d.layout.OverlayLayoutDirector;
import org.junit.Test;

import static org.jplot2d.util.TestUtils.checkDimension2D;
import static org.jplot2d.util.TestUtils.checkRectangle2D;

/**
 * Test for layout
 */
public class LayoutTest {

    private static final ElementFactory factory = ElementFactory.getInstance();

    @Test
    public void testGridLayoutNullConstraint() {
        Plot plot = factory.createPlot();
        Plot sp = factory.createSubplot();

        plot.setLayoutDirector(new GridLayoutDirector());

        plot.addSubplot(sp, null);

        RenderEnvironment env = new RenderEnvironment(false);
        env.setPlot(plot);

        checkDimension2D(plot.getSize(), 640, 480);
        checkDimension2D(plot.getContentSize(), 616, 456);
        checkDimension2D(sp.getContentSize(), 616, 456);
        checkDimension2D(sp.getSize(), 616, 456);
    }

    @Test
    public void testGridLayout() {
        Plot plot = factory.createPlot();
        Plot sp = factory.createSubplot();

        plot.setLayoutDirector(new GridLayoutDirector());

        plot.addSubplot(sp, new GridConstraint(0, 0));

        RenderEnvironment env = new RenderEnvironment(false);
        env.setPlot(plot);

        checkDimension2D(plot.getSize(), 640, 480);
        checkDimension2D(plot.getContentSize(), 616, 456);
        checkDimension2D(sp.getContentSize(), 616, 456);
        checkDimension2D(sp.getSize(), 616, 456);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGridLayoutWrongConstraint() {
        Plot plot = factory.createPlot();
        Plot sp = factory.createSubplot();

        plot.setLayoutDirector(new GridLayoutDirector());

        plot.addSubplot(sp, new Object());

        RenderEnvironment env = new RenderEnvironment(false);
        env.setPlot(plot);
    }

    @Test
    public void testOverlayLayoutNullConstraint() {
        Plot plot = factory.createPlot();
        Plot sp = factory.createSubplot();

        plot.setLayoutDirector(new OverlayLayoutDirector());

        plot.addSubplot(sp, null);

        RenderEnvironment env = new RenderEnvironment(false);
        env.setPlot(plot);

        checkDimension2D(plot.getSize(), 640, 480);
        checkDimension2D(plot.getContentSize(), 616, 456);
        checkDimension2D(sp.getContentSize(), 616, 456);
        checkRectangle2D(sp.getBounds(), 0, 0, 616, 456);
    }

    @Test
    public void testOverlayLayoutZeroBoundsConstraint() {
        Plot plot = factory.createPlot();
        Plot sp = factory.createSubplot();
        sp.getMargin().setExtraLeft(2);
        sp.getMargin().setExtraRight(2);
        sp.getMargin().setExtraTop(1);
        sp.getMargin().setExtraBottom(3);

        plot.setLayoutDirector(new OverlayLayoutDirector());

        plot.addSubplot(sp, new BoundsConstraint(0, 0, 0, 0));

        RenderEnvironment env = new RenderEnvironment(false);
        env.setPlot(plot);

        checkDimension2D(plot.getSize(), 640, 480);
        checkDimension2D(plot.getContentSize(), 616, 456);
        checkDimension2D(sp.getContentSize(), 616, 456);
        checkRectangle2D(sp.getBounds(), -2, -3, 620, 460);
    }

    @Test
    public void testOverlayLayoutBoundsConstraint() {
        Plot plot = factory.createPlot();
        Plot sp = factory.createSubplot();

        plot.setLayoutDirector(new OverlayLayoutDirector());

        plot.addSubplot(sp, new BoundsConstraint(0.1, 0.1, 0.1, 0.1));

        RenderEnvironment env = new RenderEnvironment(false);
        env.setPlot(plot);

        checkDimension2D(plot.getSize(), 640, 480);
        checkDimension2D(plot.getContentSize(), 616, 456);
        checkDimension2D(sp.getContentSize(), 492.8, 364.8);
        checkRectangle2D(sp.getBounds(), 0, 0, 492.8, 364.8);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testOverlayLayoutWrongConstraint() {
        Plot plot = factory.createPlot();
        Plot sp = factory.createSubplot();

        plot.setLayoutDirector(new OverlayLayoutDirector());

        plot.addSubplot(sp, new Object());

        RenderEnvironment env = new RenderEnvironment(false);
        env.setPlot(plot);
    }

}
