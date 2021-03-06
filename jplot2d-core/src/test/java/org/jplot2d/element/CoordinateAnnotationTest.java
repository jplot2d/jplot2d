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

import static org.jplot2d.util.TestUtils.checkPropertyInfoNames;
import static org.jplot2d.util.TestUtils.checkCollecionOrder;
import static org.junit.Assert.*;

import org.jplot2d.env.InterfaceInfo;
import org.junit.Test;

/**
 * Those test cases test methods on Title.
 * 
 * @author Jingjing Li
 * 
 */
public class CoordinateAnnotationTest {

	@Test
	public void testInterfaceInfo() {
		InterfaceInfo iinfo = InterfaceInfo.loadInterfaceInfo(CoordinateAnnotation.class);
		checkCollecionOrder(iinfo.getPropertyInfoGroupMap().keySet(), "Component", "Annotation", "Point Annotation",
				"Text", "Symbol Annotation", "Coordinate Annotation");
		checkPropertyInfoNames(iinfo.getPropertyInfoGroupMap().get("Component"), "visible", "cacheable", "selectable",
				"ZOrder", "color", "fontName", "fontStyle", "fontSize", "fontScale", "location", "size", "bounds");
		checkPropertyInfoNames(iinfo.getPropertyInfoGroupMap().get("Annotation"), "movable");
		checkPropertyInfoNames(iinfo.getPropertyInfoGroupMap().get("Point Annotation"), "valuePoint");
		checkPropertyInfoNames(iinfo.getPropertyInfoGroupMap().get("Text"), "HAlign", "VAlign");
		checkPropertyInfoNames(iinfo.getPropertyInfoGroupMap().get("Symbol Annotation"), "symbolShape", "symbolSize",
				"symbolScale", "angle", "textOffsetFactorX", "textOffsetFactorY");
		checkPropertyInfoNames(iinfo.getPropertyInfoGroupMap().get("Coordinate Annotation"), "text");

		checkCollecionOrder(iinfo.getProfilePropertyInfoGroupMap().keySet(), "Component", "Annotation", "Text",
				"Symbol Annotation");
		checkPropertyInfoNames(iinfo.getProfilePropertyInfoGroupMap().get("Component"), "visible", "cacheable",
				"selectable", "ZOrder", "color", "fontName", "fontStyle", "fontSize", "fontScale");
		checkPropertyInfoNames(iinfo.getProfilePropertyInfoGroupMap().get("Annotation"), "movable");
		checkPropertyInfoNames(iinfo.getProfilePropertyInfoGroupMap().get("Text"), "HAlign", "VAlign");
		checkPropertyInfoNames(iinfo.getProfilePropertyInfoGroupMap().get("Symbol Annotation"), "symbolSize",
				"symbolScale", "textOffsetFactorX", "textOffsetFactorY");

		assertTrue(iinfo.getPropertyInfo("text").isReadOnly());
	}

}
