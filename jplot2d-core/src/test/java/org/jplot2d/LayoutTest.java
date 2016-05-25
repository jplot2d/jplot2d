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
import org.jplot2d.layout.GridLayoutDirector;
import org.jplot2d.layout.OverlayLayoutDirector;
import org.junit.Test;

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
