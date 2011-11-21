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
 * Those test cases test methods of LegendItem.
 * 
 * @author Jingjing Li
 * 
 */
public class LegendItemTest {

	@Test
	public void testInterfaceInfo() {
		InterfaceInfo iinfo = InterfaceInfo.loadInterfaceInfo(LegendItem.class);
		checkCollecionOrder(iinfo.getPropertyInfoGroupMap().keySet(), "Legend Item");
		checkPropertyInfoNames(iinfo.getPropertyInfoGroupMap().get("Legend Item"), "visible",
				"text");
	}

}