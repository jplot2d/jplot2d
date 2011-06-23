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

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

/**
 * @author Jingjing Li
 * 
 */
public class SubplotImplTest {

	@Test
	public void testAddXAxis() {
		PlotImpl sp = new PlotImpl();
		AxisEx axis = mock(AxisEx.class);
		when(axis.canContributeToParent()).thenReturn(true);

		try {
			sp.addXAxis(axis);
			fail("IllegalArgumentException should be thrown.");
		} catch (IllegalArgumentException e) {

		}

		AxisTickManagerEx atm = mock(AxisTickManagerEx.class);
		when(axis.getTickManager()).thenReturn(atm);
		try {
			sp.addXAxis(axis);
			fail("IllegalArgumentException should be thrown.");
		} catch (IllegalArgumentException e) {

		}

		AxisRangeManagerEx arm = mock(AxisRangeManagerEx.class);
		when(atm.getRangeManager()).thenReturn(arm);
		try {
			sp.addXAxis(axis);
			fail("IllegalArgumentException should be thrown.");
		} catch (IllegalArgumentException e) {

		}

		AxisRangeLockGroupEx alg = mock(AxisRangeLockGroupEx.class);
		when(arm.getLockGroup()).thenReturn(alg);
		sp.addXAxis(axis);
	}

	@Test
	public void testAddYAxis() {
		PlotImpl sp = new PlotImpl();
		AxisEx axis = mock(AxisEx.class);
		when(axis.canContributeToParent()).thenReturn(true);

		try {
			sp.addYAxis(axis);
			fail("IllegalArgumentException should be thrown.");
		} catch (IllegalArgumentException e) {

		}

		AxisTickManagerEx atm = mock(AxisTickManagerEx.class);
		when(axis.getTickManager()).thenReturn(atm);
		try {
			sp.addYAxis(axis);
			fail("IllegalArgumentException should be thrown.");
		} catch (IllegalArgumentException e) {

		}

		AxisRangeManagerEx arm = mock(AxisRangeManagerEx.class);
		when(atm.getRangeManager()).thenReturn(arm);
		try {
			sp.addYAxis(axis);
			fail("IllegalArgumentException should be thrown.");
		} catch (IllegalArgumentException e) {

		}

		AxisRangeLockGroupEx alg = mock(AxisRangeLockGroupEx.class);
		when(arm.getLockGroup()).thenReturn(alg);
		sp.addYAxis(axis);
	}

	/**
	 * Test all methods which can trigger redraw()
	 */
	@Test
	public void testRedraw() {
		PlotImpl sp = new PlotImpl();
		sp.setCacheable(true);
		assertTrue(sp.isRedrawNeeded());
		sp.clearRedrawNeeded();

		sp.setLocation(0, 0);
		assertFalse(sp.isRedrawNeeded());
		sp.setLocation(1, 1);
		assertTrue(sp.isRedrawNeeded());
		sp.clearRedrawNeeded();

		sp.parentPhysicalTransformChanged();
		assertTrue(sp.isRedrawNeeded());
		sp.clearRedrawNeeded();

		AxisEx xaxis = mock(AxisEx.class);
		AxisTickManagerEx xatm = mock(AxisTickManagerEx.class);
		AxisRangeManagerEx xarm = mock(AxisRangeManagerEx.class);
		AxisRangeLockGroupEx xalg = mock(AxisRangeLockGroupEx.class);
		when(xaxis.canContributeToParent()).thenReturn(true);
		when(xaxis.getTickManager()).thenReturn(xatm);
		when(xatm.getRangeManager()).thenReturn(xarm);
		when(xarm.getLockGroup()).thenReturn(xalg);
		sp.addXAxis(xaxis);
		assertTrue(sp.isRedrawNeeded());
		sp.clearRedrawNeeded();

		AxisEx yaxis = mock(AxisEx.class);
		AxisTickManagerEx yatm = mock(AxisTickManagerEx.class);
		AxisRangeManagerEx yarm = mock(AxisRangeManagerEx.class);
		AxisRangeLockGroupEx yalg = mock(AxisRangeLockGroupEx.class);
		when(yaxis.canContributeToParent()).thenReturn(true);
		when(yaxis.getTickManager()).thenReturn(yatm);
		when(yatm.getRangeManager()).thenReturn(yarm);
		when(yarm.getLockGroup()).thenReturn(yalg);
		sp.addYAxis(yaxis);
		assertTrue(sp.isRedrawNeeded());
		sp.clearRedrawNeeded();

		LayerEx layer0 = mock(LayerEx.class);
		when(layer0.canContributeToParent()).thenReturn(false);
		when(layer0.getGraphPlotters()).thenReturn(new GraphPlotterEx[0]);
		sp.addLayer(layer0, xaxis.getTickManager().getRangeManager(), yaxis
				.getTickManager().getRangeManager());
		assertFalse(sp.isRedrawNeeded());

		LayerEx layer1 = mock(LayerEx.class);
		when(layer1.canContributeToParent()).thenReturn(true);
		when(layer1.getGraphPlotters()).thenReturn(new GraphPlotterEx[0]);
		sp.addLayer(layer1, xaxis.getTickManager().getRangeManager(), yaxis
				.getTickManager().getRangeManager());
		assertTrue(sp.isRedrawNeeded());
		sp.clearRedrawNeeded();

		sp.removeLayer(layer1);
		assertTrue(sp.isRedrawNeeded());
		sp.clearRedrawNeeded();

		sp.removeLayer(layer0);
		assertFalse(sp.isRedrawNeeded());

		sp.removeXAxis(xaxis);
		assertTrue(sp.isRedrawNeeded());
		sp.clearRedrawNeeded();

		sp.removeYAxis(yaxis);
		assertTrue(sp.isRedrawNeeded());
		sp.clearRedrawNeeded();
	}

	/**
	 * Test all methods which can trigger invalidate()
	 */
	@Test
	public void testInvalidateByXAxis() {
		PlotImpl sp = new PlotImpl();
		assertTrue(sp.isValid());

		// add or remove invisible axis does not invalid
		AxisEx xaxis0 = mock(AxisEx.class);
		AxisTickManagerEx xatm0 = mock(AxisTickManagerEx.class);
		AxisRangeManagerEx xarm0 = mock(AxisRangeManagerEx.class);
		AxisRangeLockGroupEx xalg0 = mock(AxisRangeLockGroupEx.class);
		when(xaxis0.getTickManager()).thenReturn(xatm0);
		when(xatm0.getRangeManager()).thenReturn(xarm0);
		when(xarm0.getLockGroup()).thenReturn(xalg0);
		sp.addXAxis(xaxis0);
		assertTrue(sp.isValid());
		sp.removeXAxis(xaxis0);
		assertTrue(sp.isValid());

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
		sp.addXAxis(xaxis);
		assertFalse(sp.isValid());
		sp.validate();
		sp.removeXAxis(xaxis);
		assertFalse(sp.isValid());
		sp.validate();
	}

	@Test
	public void testInvalidateByYAxis() {
		PlotImpl sp = new PlotImpl();
		assertTrue(sp.isValid());

		AxisEx yaxis0 = mock(AxisEx.class);
		AxisTickManagerEx yatm0 = mock(AxisTickManagerEx.class);
		AxisRangeManagerEx yarm0 = mock(AxisRangeManagerEx.class);
		AxisRangeLockGroupEx yalg0 = mock(AxisRangeLockGroupEx.class);
		when(yaxis0.getTickManager()).thenReturn(yatm0);
		when(yatm0.getRangeManager()).thenReturn(yarm0);
		when(yarm0.getLockGroup()).thenReturn(yalg0);
		sp.addYAxis(yaxis0);
		assertTrue(sp.isValid());
		sp.validate();
		sp.removeYAxis(yaxis0);
		assertTrue(sp.isValid());

		AxisEx yaxis = mock(AxisEx.class);
		AxisTickManagerEx yatm = mock(AxisTickManagerEx.class);
		AxisRangeManagerEx yarm = mock(AxisRangeManagerEx.class);
		AxisRangeLockGroupEx yalg = mock(AxisRangeLockGroupEx.class);
		when(yaxis.getTickManager()).thenReturn(yatm);
		when(yatm.getRangeManager()).thenReturn(yarm);
		when(yarm.getLockGroup()).thenReturn(yalg);
		when(yaxis.isVisible()).thenReturn(true);
		when(yaxis.canContribute()).thenReturn(true);
		sp.addYAxis(yaxis);
		assertFalse(sp.isValid());
		sp.validate();
		sp.removeYAxis(yaxis);
		assertFalse(sp.isValid());
		sp.validate();
	}

	@Test
	public void testInvalidateBySubplot() {
		PlotImpl sp = new PlotImpl();
		assertTrue(sp.isValid());

		PlotImpl sp0 = new PlotImpl();
		sp.addSubplot(sp0, null);
		assertFalse(sp.isValid());
		sp.validate();
		sp.removeSubplot(sp0);
		assertFalse(sp.isValid());
		sp.validate();

		PlotImpl spInvisble = new PlotImpl();
		spInvisble.setVisible(false);
		sp.addSubplot(spInvisble, null);
		assertTrue(sp.isValid());
		sp.removeSubplot(spInvisble);
		assertTrue(sp.isValid());
		sp.validate();
	}

	@Test
	public void testCopyStructure() {
		PlotImpl sp = new PlotImpl();
		Map<ElementEx, ElementEx> orig2copyMap = new HashMap<ElementEx, ElementEx>();
		PlotImpl sp2 = sp.copyStructure(orig2copyMap);
		assertEquals(orig2copyMap.size(), 3);
		assertSame(sp2, orig2copyMap.get(sp));
		assertSame(sp2.getMargin(), orig2copyMap.get(sp.getMargin()));
		assertSame(sp2.getLegend(), orig2copyMap.get(sp.getLegend()));
	}

}
