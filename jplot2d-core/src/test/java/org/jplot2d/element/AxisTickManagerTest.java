/*
 * Copyright 2010-2014 Jingjing Li.
 *
 * This file is part of jplot2d.
 *
 * jplot2d is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or any later version.
 *
 * jplot2d is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Lesser Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with jplot2d. If not, see <http://www.gnu.org/licenses/>.
 */
package org.jplot2d.element;

import org.jplot2d.env.InterfaceInfo;
import org.junit.Test;

import static org.jplot2d.util.TestUtils.checkCollecionOrder;
import static org.jplot2d.util.TestUtils.checkPropertyInfoNames;

/**
 * Those test cases test methods on Title.
 *
 * @author Jingjing Li
 */
public class AxisTickManagerTest {

    @Test
    public void testInterfaceInfo() {
        InterfaceInfo iinfo = InterfaceInfo.loadInterfaceInfo(AxisTickManager.class);
        checkCollecionOrder(iinfo.getPropertyInfoGroupMap().keySet(), "Axis Tick Manager");
        checkPropertyInfoNames(iinfo.getPropertyInfoGroupMap().get("Axis Tick Manager"), "tickTransform", "range",
                "autoTickValues", "fixedTickValues", "fixedMinorTickValues", "autoTickInterval", "tickInterval", "tickOffset",
                "tickNumber", "autoReduceTickNumber", "autoMinorTicks", "minorTickNumber", "actualMinorTickNumber",
                "tickValues", "minorTickValues", "labelInterval", "autoLabelFormat", "labelTextFormat", "labelFormat",
                "fixedLabelStrings", "labelStrings");

        checkCollecionOrder(iinfo.getProfilePropertyInfoGroupMap().keySet());
    }
}
