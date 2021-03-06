/*
 * Copyright 2010, 2011 Jingjing Li.
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
public class MarginTest {

	@Test
	public void testInterfaceInfo() {
		InterfaceInfo iinfo = InterfaceInfo.loadInterfaceInfo(PlotMargin.class);
		checkCollecionOrder(iinfo.getPropertyInfoGroupMap().keySet(), "Margin");
		checkPropertyInfoNames(iinfo.getPropertyInfoGroupMap().get("Margin"), "autoLeft", "autoRight", "autoBottom",
				"autoTop", "left", "right", "bottom", "top", "extraLeft", "extraRight", "extraBottom", "extraTop");

		checkCollecionOrder(iinfo.getProfilePropertyInfoGroupMap().keySet(), "Margin");
		checkPropertyInfoNames(iinfo.getProfilePropertyInfoGroupMap().get("Margin"), "extraLeft", "extraRight",
				"extraBottom", "extraTop");
	}
}
