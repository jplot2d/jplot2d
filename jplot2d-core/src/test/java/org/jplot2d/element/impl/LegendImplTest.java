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
package org.jplot2d.element.impl;

import static org.junit.Assert.*;

import org.junit.Test;

public class LegendImplTest {

	@Test
	public void testAddAndRemoveLegendItem() {
		LegendImpl legend = new LegendImpl();
		XYLegendItemImpl item = new XYLegendItemImpl();
		
		legend.addLegendItem(item);
		assertEquals(legend.getItems().length, 1);
		assertSame(legend.getItems()[0], item);
		assertSame(legend, item.getLegend());
		
		legend.removeLegendItem(item);
		assertEquals(legend.getItems().length, 0);
		assertNull(item.getLegend());
	}

	@Test
	public void testAddAndRemoveLegendItemWithText() {
		LegendImpl legend = new LegendImpl();
		legend.setFontSize(8);
		XYLegendItemImpl item = new XYLegendItemImpl();
		item.setText("legend item");
		item.setSymbolSize(8);
		
		legend.addLegendItem(item);
		assertEquals(legend.getItems().length, 1);
		assertSame(legend.getItems()[0], item);
		assertSame(legend, item.getLegend());
		
		legend.removeLegendItem(item);
		assertEquals(legend.getItems().length, 0);
		assertNull(item.getLegend());
	}
}
