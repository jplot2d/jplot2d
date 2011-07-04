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
package org.jplot2d.element.impl;

import static org.jplot2d.util.TestUtils.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

/**
 * @author Jingjing Li
 * 
 */
public class PlotImplTest {

	public void testConstructor() {
		PlotImpl p = new PlotImpl();
		checkDimension2D(p.getSize(), 640, 480);
		checkDouble(p.getScale(), 1);
	}

	@Test
	public void testAddXAxis() {
		PlotImpl p = new PlotImpl();
		AxisEx axis = mock(AxisEx.class);
		when(axis.canContributeToParent()).thenReturn(true);

		try {
			p.addXAxis(axis);
			fail("IllegalArgumentException should be thrown.");
		} catch (IllegalArgumentException e) {

		}

		AxisTickManagerEx atm = mock(AxisTickManagerEx.class);
		when(axis.getTickManager()).thenReturn(atm);
		try {
			p.addXAxis(axis);
			fail("IllegalArgumentException should be thrown.");
		} catch (IllegalArgumentException e) {

		}

		AxisRangeManagerEx arm = mock(AxisRangeManagerEx.class);
		when(atm.getRangeManager()).thenReturn(arm);
		try {
			p.addXAxis(axis);
			fail("IllegalArgumentException should be thrown.");
		} catch (IllegalArgumentException e) {

		}

		AxisRangeLockGroupEx alg = mock(AxisRangeLockGroupEx.class);
		when(arm.getLockGroup()).thenReturn(alg);
		p.addXAxis(axis);
	}

	@Test
	public void testAddYAxis() {
		PlotImpl p = new PlotImpl();
		AxisEx axis = mock(AxisEx.class);
		when(axis.canContributeToParent()).thenReturn(true);

		try {
			p.addYAxis(axis);
			fail("IllegalArgumentException should be thrown.");
		} catch (IllegalArgumentException e) {

		}

		AxisTickManagerEx atm = mock(AxisTickManagerEx.class);
		when(axis.getTickManager()).thenReturn(atm);
		try {
			p.addYAxis(axis);
			fail("IllegalArgumentException should be thrown.");
		} catch (IllegalArgumentException e) {

		}

		AxisRangeManagerEx arm = mock(AxisRangeManagerEx.class);
		when(atm.getRangeManager()).thenReturn(arm);
		try {
			p.addYAxis(axis);
			fail("IllegalArgumentException should be thrown.");
		} catch (IllegalArgumentException e) {

		}

		AxisRangeLockGroupEx alg = mock(AxisRangeLockGroupEx.class);
		when(arm.getLockGroup()).thenReturn(alg);
		p.addYAxis(axis);
	}

	/**
	 * Test all methods which can trigger redraw()
	 */
	@Test
	public void testRedraw() {
		PlotImpl p = new PlotImpl();
		p.setCacheable(true);
		assertTrue(p.isRedrawNeeded());
		p.clearRedrawNeeded();

		p.setLocation(0, 0);
		assertFalse(p.isRedrawNeeded());
		p.setLocation(1, 1);
		assertTrue(p.isRedrawNeeded());
		p.clearRedrawNeeded();

		p.parentPhysicalTransformChanged();
		assertTrue(p.isRedrawNeeded());
		p.clearRedrawNeeded();

		AxisEx xaxis = mock(AxisEx.class);
		AxisTickManagerEx xatm = mock(AxisTickManagerEx.class);
		AxisRangeManagerEx xarm = mock(AxisRangeManagerEx.class);
		AxisRangeLockGroupEx xalg = mock(AxisRangeLockGroupEx.class);
		when(xaxis.canContributeToParent()).thenReturn(true);
		when(xaxis.getTickManager()).thenReturn(xatm);
		when(xatm.getRangeManager()).thenReturn(xarm);
		when(xarm.getLockGroup()).thenReturn(xalg);
		p.addXAxis(xaxis);
		assertTrue(p.isRedrawNeeded());
		p.clearRedrawNeeded();

		AxisEx yaxis = mock(AxisEx.class);
		AxisTickManagerEx yatm = mock(AxisTickManagerEx.class);
		AxisRangeManagerEx yarm = mock(AxisRangeManagerEx.class);
		AxisRangeLockGroupEx yalg = mock(AxisRangeLockGroupEx.class);
		when(yaxis.canContributeToParent()).thenReturn(true);
		when(yaxis.getTickManager()).thenReturn(yatm);
		when(yatm.getRangeManager()).thenReturn(yarm);
		when(yarm.getLockGroup()).thenReturn(yalg);
		p.addYAxis(yaxis);
		assertTrue(p.isRedrawNeeded());
		p.clearRedrawNeeded();

		LayerEx layer0 = mock(LayerEx.class);
		when(layer0.canContributeToParent()).thenReturn(false);
		when(layer0.getGraphPlotters()).thenReturn(new GraphPlotterEx[0]);
		p.addLayer(layer0, xaxis.getTickManager().getRangeManager(), yaxis
				.getTickManager().getRangeManager());
		assertFalse(p.isRedrawNeeded());

		LayerEx layer1 = mock(LayerEx.class);
		when(layer1.canContributeToParent()).thenReturn(true);
		when(layer1.getGraphPlotters()).thenReturn(new GraphPlotterEx[0]);
		p.addLayer(layer1, xaxis.getTickManager().getRangeManager(), yaxis
				.getTickManager().getRangeManager());
		assertTrue(p.isRedrawNeeded());
		p.clearRedrawNeeded();

		p.removeLayer(layer1);
		assertTrue(p.isRedrawNeeded());
		p.clearRedrawNeeded();

		p.removeLayer(layer0);
		assertFalse(p.isRedrawNeeded());

		p.removeXAxis(xaxis);
		assertTrue(p.isRedrawNeeded());
		p.clearRedrawNeeded();

		p.removeYAxis(yaxis);
		assertTrue(p.isRedrawNeeded());
		p.clearRedrawNeeded();
	}

	/**
	 * Test all methods which can trigger invalidate()
	 */
	@Test
	public void testInvalidateByXAxis() {
		PlotImpl p = new PlotImpl();
		assertTrue(p.isValid());

		// add or remove invisible axis does not invalid
		AxisEx xaxis0 = mock(AxisEx.class);
		AxisTickManagerEx xatm0 = mock(AxisTickManagerEx.class);
		AxisRangeManagerEx xarm0 = mock(AxisRangeManagerEx.class);
		AxisRangeLockGroupEx xalg0 = mock(AxisRangeLockGroupEx.class);
		when(xaxis0.getTickManager()).thenReturn(xatm0);
		when(xatm0.getRangeManager()).thenReturn(xarm0);
		when(xarm0.getLockGroup()).thenReturn(xalg0);
		p.addXAxis(xaxis0);
		assertTrue(p.isValid());
		p.removeXAxis(xaxis0);
		assertTrue(p.isValid());

		// add or remove visible axis does invalid
		AxisEx xaxis = mock(AxisEx.class);
		AxisTickManagerEx xatm = mock(AxisTickManagerEx.class);
		AxisRangeManagerEx xarm = mock(AxisRangeManagerEx.class);
		AxisRangeLockGroupEx xalg = mock(AxisRangeLockGroupEx.class);
		when(xaxis.getTickManager()).thenReturn(xatm);
		when(xatm.getRangeManager()).thenReturn(xarm);
		when(xarm.getLockGroup()).thenReturn(xalg);
		when(xaxis.isVisible()).thenReturn(true);
		when(xaxis.canContribute()).thenReturn(true);
		p.addXAxis(xaxis);
		assertFalse(p.isValid());
		p.validate();
		p.removeXAxis(xaxis);
		assertFalse(p.isValid());
		p.validate();
	}

	@Test
	public void testInvalidateByYAxis() {
		PlotImpl p = new PlotImpl();
		assertTrue(p.isValid());

		AxisEx yaxis0 = mock(AxisEx.class);
		AxisTickManagerEx yatm0 = mock(AxisTickManagerEx.class);
		AxisRangeManagerEx yarm0 = mock(AxisRangeManagerEx.class);
		AxisRangeLockGroupEx yalg0 = mock(AxisRangeLockGroupEx.class);
		when(yaxis0.getTickManager()).thenReturn(yatm0);
		when(yatm0.getRangeManager()).thenReturn(yarm0);
		when(yarm0.getLockGroup()).thenReturn(yalg0);
		p.addYAxis(yaxis0);
		assertTrue(p.isValid());
		p.validate();
		p.removeYAxis(yaxis0);
		assertTrue(p.isValid());

		AxisEx yaxis = mock(AxisEx.class);
		AxisTickManagerEx yatm = mock(AxisTickManagerEx.class);
		AxisRangeManagerEx yarm = mock(AxisRangeManagerEx.class);
		AxisRangeLockGroupEx yalg = mock(AxisRangeLockGroupEx.class);
		when(yaxis.getTickManager()).thenReturn(yatm);
		when(yatm.getRangeManager()).thenReturn(yarm);
		when(yarm.getLockGroup()).thenReturn(yalg);
		when(yaxis.isVisible()).thenReturn(true);
		when(yaxis.canContribute()).thenReturn(true);
		p.addYAxis(yaxis);
		assertFalse(p.isValid());
		p.validate();
		p.removeYAxis(yaxis);
		assertFalse(p.isValid());
		p.validate();
	}

	@Test
	public void testInvalidateBySubplot() {
		PlotImpl p = new PlotImpl();
		assertTrue(p.isValid());

		PlotImpl sp0 = new PlotImpl();
		p.addSubplot(sp0, null);
		assertFalse(p.isValid());
		p.validate();
		p.removeSubplot(sp0);
		assertFalse(p.isValid());
		p.validate();

		PlotImpl spInvisble = new PlotImpl();
		spInvisble.setVisible(false);
		p.addSubplot(spInvisble, null);
		assertTrue(p.isValid());
		p.removeSubplot(spInvisble);
		assertTrue(p.isValid());
		p.validate();
	}

	@Test
	public void testCopyStructure() {
		PlotImpl p = new PlotImpl();
		Map<ElementEx, ElementEx> orig2copyMap = new HashMap<ElementEx, ElementEx>();
		PlotImpl p2 = p.copyStructure(orig2copyMap);
		assertEquals(orig2copyMap.size(), 3);
		assertSame(p2, orig2copyMap.get(p));
		assertSame(p2.getMargin(), orig2copyMap.get(p.getMargin()));
		assertSame(p2.getLegend(), orig2copyMap.get(p.getLegend()));
	}

}
