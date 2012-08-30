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
import static org.junit.Assert.*;

import org.jplot2d.env.InterfaceInfo;
import org.junit.Test;

/**
 * Those test cases test methods on Layer.
 * 
 * @author Jingjing Li
 * 
 */
public class LayerTest {

	@Test
	public void testInterfaceInfo() {
		InterfaceInfo iinfo = InterfaceInfo.loadInterfaceInfo(Layer.class);
		checkCollecionOrder(iinfo.getPropertyInfoGroupMap().keySet(), "Component");
		checkPropertyInfoNames(iinfo.getPropertyInfoGroupMap().get("Component"), "visible",
				"cacheable", "selectable", "ZOrder", "color", "fontName", "fontStyle", "fontSize",
				"fontScale", "location", "size", "bounds");
	}

	@Test
	public void testCreateLayer() {
		ElementFactory ef = ElementFactory.getInstance();

		Layer layer = ef.createLayer();

		assertNull(layer.getSize());
		assertNull(layer.getBounds());
		assertNull(layer.getSelectableBounds());
	}

	@Test
	public void testAddLayer() {
		ElementFactory ef = ElementFactory.getInstance();
		Plot p = ef.createPlot();

		Axis xaxis = ef.createAxis();
		Axis yaxis = ef.createAxis();
		Layer layer = ef.createLayer();
		p.addXAxis(xaxis);
		p.addYAxis(yaxis);
		p.addLayer(layer, xaxis, yaxis);

		System.out.println(layer.getBounds());

	}

}
