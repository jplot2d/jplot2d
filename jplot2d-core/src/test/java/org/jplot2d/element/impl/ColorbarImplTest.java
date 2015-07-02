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

import static org.junit.Assert.assertSame;

/**
 * @author Jingjing Li
 *
 */
public class ColorbarImplTest {

    @Test
    public void testCopyStructure() {
        ColorbarEx colorbar = new ColorbarImpl();
        ColorbarAxisEx lowerAxis = colorbar.getInnerAxis();
        ColorbarAxisEx upperAxis = colorbar.getOuterAxis();
        AxisTickManagerEx lowerAtm = lowerAxis.getTickManager();
        AxisTickManagerEx upperAtm = upperAxis.getTickManager();
        AxisTransformEx lowerAxf = lowerAtm.getAxisTransform();
        AxisTransformEx upperAxf = upperAtm.getAxisTransform();

        Map<ElementEx, ElementEx> orig2copyMap = new HashMap<>();
        ColorbarEx colorbarCopy = (ColorbarEx) colorbar.copyStructure(orig2copyMap);

        ColorbarAxisEx lowerAxisCopy = colorbarCopy.getInnerAxis();
        ColorbarAxisEx upperAxisCopy = colorbarCopy.getOuterAxis();
        AxisTickManagerEx lowerAtmCopy = lowerAxisCopy.getTickManager();
        AxisTickManagerEx upperAtmCopy = upperAxisCopy.getTickManager();
        AxisTransformEx lowerAxfCopy = lowerAtmCopy.getAxisTransform();
        AxisTransformEx upperAxfCopy = upperAtmCopy.getAxisTransform();

        assertSame(lowerAxisCopy, orig2copyMap.get(lowerAxis));
        assertSame(upperAxisCopy, orig2copyMap.get(upperAxis));
        assertSame(lowerAtmCopy, orig2copyMap.get(lowerAtm));
        assertSame(upperAtmCopy, orig2copyMap.get(upperAtm));
        assertSame(lowerAxfCopy, orig2copyMap.get(lowerAxf));
        assertSame(upperAxfCopy, orig2copyMap.get(upperAxf));

        for (Map.Entry<ElementEx, ElementEx> me : orig2copyMap.entrySet()) {
            ElementEx simpl = me.getKey();
            ElementEx dimpl = me.getValue();
            dimpl.copyFrom(simpl);
        }

        assertSame(lowerAxisCopy, orig2copyMap.get(lowerAxis));
        assertSame(upperAxisCopy, orig2copyMap.get(upperAxis));
        assertSame(lowerAtmCopy, orig2copyMap.get(lowerAtm));
        assertSame(upperAtmCopy, orig2copyMap.get(upperAtm));
        assertSame(lowerAxfCopy, orig2copyMap.get(lowerAxf));
        assertSame(upperAxfCopy, orig2copyMap.get(upperAxf));
    }
}
