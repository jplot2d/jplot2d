/**
 * Copyright 2010-2012 Jingjing Li.
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

import static org.jplot2d.util.TestUtils.*;
import static org.junit.Assert.assertNull;

import org.jplot2d.env.InterfaceInfo;
import org.jplot2d.env.PlotEnvironment;
import org.junit.Test;

/**
 * Those test cases test methods on Title.
 * 
 * @author Jingjing Li
 * 
 */
public class RectangleAnnotationTest {

	@Test
	public void testInterfaceInfo() {
		InterfaceInfo iinfo = InterfaceInfo.loadInterfaceInfo(RectangleAnnotation.class);
		checkCollecionOrder(iinfo.getPropertyInfoGroupMap().keySet(), "Component", "Annotation", "Rectangle Annotation");
		checkPropertyInfoNames(iinfo.getPropertyInfoGroupMap().get("Component"), "visible", "cacheable", "selectable",
				"ZOrder", "color", "fontName", "fontStyle", "fontSize", "fontScale", "location", "size", "bounds");
		checkPropertyInfoNames(iinfo.getPropertyInfoGroupMap().get("Annotation"), "movable");
		checkPropertyInfoNames(iinfo.getPropertyInfoGroupMap().get("Rectangle Annotation"), "XValueRange",
				"YValueRange", "fillPaint");

		checkCollecionOrder(iinfo.getProfilePropertyInfoGroupMap().keySet(), "Component", "Annotation",
				"Rectangle Annotation");
		checkPropertyInfoNames(iinfo.getProfilePropertyInfoGroupMap().get("Component"), "visible", "cacheable",
				"selectable", "ZOrder", "color", "fontName", "fontStyle", "fontSize", "fontScale");
		checkPropertyInfoNames(iinfo.getProfilePropertyInfoGroupMap().get("Annotation"), "movable");
		checkPropertyInfoNames(iinfo.getProfilePropertyInfoGroupMap().get("Rectangle Annotation"), "fillPaint");
	}

	@Test
	public void testRectangleAnnotation() {
		ElementFactory ef = ElementFactory.getInstance();

		RectangleAnnotation ann = ef.createRectangleAnnotation(0, 1, 0, 1);

		assertNull(ann.getSize());
		assertNull(ann.getBounds());
		assertNull(ann.getSelectableBounds());
	}

	@Test
	public void testReverseRange() {
		ElementFactory ef = ElementFactory.getInstance();
		Plot p = ef.createPlot();

		Axis xaxis = ef.createAxis();
		Axis yaxis = ef.createAxis();
		Layer layer = ef.createLayer();
		p.addXAxis(xaxis);
		p.addYAxis(yaxis);
		p.addLayer(layer, xaxis, yaxis);

		RectangleAnnotation ann0 = ef.createRectangleAnnotation(0, 1, 0, 1);
		layer.addAnnotation(ann0);
		RectangleAnnotation ann1 = ef.createRectangleAnnotation(1, 0, 1, 0);
		layer.addAnnotation(ann1);

		PlotEnvironment env = new PlotEnvironment(false);
		env.setPlot(p);

		checkDimension2D(ann0.getSize(), ann1.getSize());
		checkDouble(ann0.getBounds().getX(), 0);
		checkDouble(ann0.getBounds().getY(), 0);
		checkDouble(ann0.getBounds().getWidth(), ann1.getBounds().getWidth());
		checkDouble(ann0.getBounds().getHeight(), ann1.getBounds().getHeight());
		checkDouble(ann1.getBounds().getX(), -ann1.getBounds().getWidth());
		checkDouble(ann1.getBounds().getY(), -ann1.getBounds().getHeight());
	}

}
