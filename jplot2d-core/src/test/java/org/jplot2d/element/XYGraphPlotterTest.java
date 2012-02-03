/**
 * Copyright 2010 Jingjing Li.
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

import static org.jplot2d.util.TestUtils.checkPropertyInfoNames;
import static org.jplot2d.util.TestUtils.checkCollecionOrder;

import org.jplot2d.env.InterfaceInfo;
import org.junit.Test;

/**
 * Those test cases test methods on Title.
 * 
 * @author Jingjing Li
 * 
 */
public class XYGraphPlotterTest {

	@Test
	public void testInterfaceInfo() {
		InterfaceInfo iinfo = InterfaceInfo.loadInterfaceInfo(XYGraphPlotter.class);
		checkCollecionOrder(iinfo.getPropertyInfoGroupMap().keySet(), "Component",
				"XY Graph Plotter");
		checkPropertyInfoNames(iinfo.getPropertyInfoGroupMap().get("Component"), "visible",
				"cacheable", "selectable", "ZOrder", "color", "fontName", "fontStyle", "fontSize",
				"fontScale", "location", "size", "bounds");
		checkPropertyInfoNames(iinfo.getPropertyInfoGroupMap().get("XY Graph Plotter"),
				"symbolVisible", "symbolShape", "symbolSize", "symbolColor", "lineVisible",
				"lineStroke", "chartType", "fillEnabled", "fillPaint", "fillClosureType",
				"errorbarCapSize");
	}
}
