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

import org.jplot2d.element.impl.LayerImpl;
import org.jplot2d.element.impl.PlotAxisImpl;
import org.jplot2d.element.impl.PlotImpl;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author Jingjing Li
 */
public class ElementFactoryTest {

    /**
     * Test getElementInterface(Class)
     */
    @Test
    public void testGetElementInterface() {
        assertEquals(ElementFactory.getElementInterface(PlotImpl.class), Plot.class);
        assertEquals(ElementFactory.getElementInterface(PlotAxisImpl.class), PlotAxis.class);
        assertEquals(ElementFactory.getElementInterface(LayerImpl.class), Layer.class);

        ElementFactory ef = ElementFactory.getInstance();
        assertEquals(ElementFactory.getElementInterface(ef.createPlot().getClass()), Plot.class);
        assertEquals(ElementFactory.getElementInterface(ef.createAxis().getClass()), PlotAxis.class);
        assertEquals(ElementFactory.getElementInterface(ef.createLayer().getClass()), Layer.class);
    }

}
