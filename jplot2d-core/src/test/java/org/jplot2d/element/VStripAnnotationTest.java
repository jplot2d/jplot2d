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

import static org.jplot2d.util.TestUtils.checkDouble;
import static org.jplot2d.util.TestUtils.checkPropertyInfoNames;
import static org.jplot2d.util.TestUtils.checkCollecionOrder;
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
public class VStripAnnotationTest {

	@Test
	public void testInterfaceInfo() {
		InterfaceInfo iinfo = InterfaceInfo.loadInterfaceInfo(VStripAnnotation.class);
		checkCollecionOrder(iinfo.getPropertyInfoGroupMap().keySet(), "Component", "Annotation",
				"Vertical Strip Annotation");
		checkPropertyInfoNames(iinfo.getPropertyInfoGroupMap().get("Component"), "visible",
				"cacheable", "selectable", "ZOrder", "color", "fontName", "fontStyle", "fontSize",
				"fontScale", "location", "size", "bounds");
		checkPropertyInfoNames(iinfo.getPropertyInfoGroupMap().get("Annotation"), "movable");
		checkPropertyInfoNames(iinfo.getPropertyInfoGroupMap().get("Vertical Strip Annotation"),
				"valueRange", "fillPaint");
	}

	@Test
	public void testCreateVstripAnnotation() {
		ElementFactory ef = ElementFactory.getInstance();

		VStripAnnotation ann = ef.createVStripAnnotation(0, 1);

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

		VStripAnnotation ann0 = ef.createVStripAnnotation(0, 1);
		layer.addAnnotation(ann0);
		VStripAnnotation ann1 = ef.createVStripAnnotation(1, 0);
		layer.addAnnotation(ann1);

		PlotEnvironment env = new PlotEnvironment(false);
		env.setPlot(p);

		checkDouble(ann0.getSize().getWidth(), ann1.getSize().getWidth());
		checkDouble(ann0.getBounds().getY(), -ann1.getBounds().getHeight());
		checkDouble(ann0.getBounds().getHeight(), ann1.getBounds().getHeight());
		checkDouble(ann1.getBounds().getY(), 0);
	}
}
