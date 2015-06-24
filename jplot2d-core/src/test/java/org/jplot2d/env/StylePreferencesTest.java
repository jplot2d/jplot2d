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
package org.jplot2d.env;

import org.jplot2d.element.Layer;
import org.jplot2d.element.Plot;
import org.jplot2d.element.PlotAxis;
import org.jplot2d.element.impl.LayerImpl;
import org.jplot2d.element.impl.PlotAxisImpl;
import org.jplot2d.element.impl.PlotImpl;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author Jingjing Li
 *
 */
public class StylePreferencesTest {

    /**
     * Test loadInterfaceInfo for all interfaces will throw no exception.
     *
     * @throws ClassNotFoundException
     */
    @Test
    public void testloadAllInterface() throws ClassNotFoundException {
        assertEquals(StylePreferences.getElementInterface(PlotImpl.class), Plot.class);
        assertEquals(StylePreferences.getElementInterface(PlotAxisImpl.class), PlotAxis.class);
        assertEquals(StylePreferences.getElementInterface(LayerImpl.class), Layer.class);
    }
}
