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

import org.junit.Test;

/**
 * @author Jingjing Li
 * 
 */
public class SubplotImplTest {

	@Test
	public void testAddXAxis() {
		SubplotImpl sp = new SubplotImpl();
		AxisEx axis = mock(AxisEx.class);
		when(axis.canContributeToParent()).thenReturn(true);

		try {
			sp.addXAxis(axis);
			fail("IllegalArgumentException should be thrown.");
		} catch (IllegalArgumentException e) {

		}

		AxisRangeManagerEx arm = mock(AxisRangeManagerEx.class);
		when(axis.getRangeManager()).thenReturn(arm);
		try {
			sp.addXAxis(axis);
			fail("IllegalArgumentException should be thrown.");
		} catch (IllegalArgumentException e) {

		}

		AxisLockGroupEx alg = mock(AxisLockGroupEx.class);
		when(arm.getLockGroup()).thenReturn(alg);
		sp.addXAxis(axis);
	}

	@Test
	public void testAddYAxis() {
		SubplotImpl sp = new SubplotImpl();
		AxisEx axis = mock(AxisEx.class);
		when(axis.canContributeToParent()).thenReturn(true);

		try {
			sp.addYAxis(axis);
			fail("IllegalArgumentException should be thrown.");
		} catch (IllegalArgumentException e) {

		}

		AxisRangeManagerEx arm = mock(AxisRangeManagerEx.class);
		when(axis.getRangeManager()).thenReturn(arm);
		try {
			sp.addYAxis(axis);
			fail("IllegalArgumentException should be thrown.");
		} catch (IllegalArgumentException e) {

		}

		AxisLockGroupEx alg = mock(AxisLockGroupEx.class);
		when(arm.getLockGroup()).thenReturn(alg);
		sp.addYAxis(axis);
	}

	/**
	 * Test all methods which can trigger redraw()
	 */
	@Test
	public void testRedraw() {
		SubplotImpl sp = new SubplotImpl();
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
		AxisRangeManagerEx xarm = mock(AxisRangeManagerEx.class);
		AxisLockGroupEx xalg = mock(AxisLockGroupEx.class);
		when(xaxis.canContributeToParent()).thenReturn(true);
		when(xaxis.getRangeManager()).thenReturn(xarm);
		when(xarm.getLockGroup()).thenReturn(xalg);
		sp.addXAxis(xaxis);
		assertTrue(sp.isRedrawNeeded());
		sp.clearRedrawNeeded();

		AxisEx yaxis = mock(AxisEx.class);
		AxisRangeManagerEx yarm = mock(AxisRangeManagerEx.class);
		AxisLockGroupEx yalg = mock(AxisLockGroupEx.class);
		when(yaxis.canContributeToParent()).thenReturn(true);
		when(yaxis.getRangeManager()).thenReturn(yarm);
		when(yarm.getLockGroup()).thenReturn(yalg);
		sp.addYAxis(yaxis);
		assertTrue(sp.isRedrawNeeded());
		sp.clearRedrawNeeded();

		LayerEx layer0 = mock(LayerEx.class);
		when(layer0.canContributeToParent()).thenReturn(false);
		sp.addLayer(layer0, xaxis.getRangeManager(), yaxis.getRangeManager());
		assertFalse(sp.isRedrawNeeded());

		LayerEx layer1 = mock(LayerEx.class);
		when(layer1.canContributeToParent()).thenReturn(true);
		sp.addLayer(layer1, xaxis.getRangeManager(), yaxis.getRangeManager());
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
	public void testInvalidate() {
		SubplotImpl sp = new SubplotImpl();
		assertTrue(sp.isValid());

		// add or remove invisible axis does not invalid
		AxisEx xaxis0 = mock(AxisEx.class);
		AxisRangeManagerEx xarm0 = mock(AxisRangeManagerEx.class);
		AxisLockGroupEx xalg0 = mock(AxisLockGroupEx.class);
		when(xaxis0.getRangeManager()).thenReturn(xarm0);
		when(xarm0.getLockGroup()).thenReturn(xalg0);
		sp.addXAxis(xaxis0);
		assertTrue(sp.isValid());
		sp.removeXAxis(xaxis0);
		assertTrue(sp.isValid());

		AxisEx xaxis = mock(AxisEx.class);
		AxisRangeManagerEx xarm = mock(AxisRangeManagerEx.class);
		AxisLockGroupEx xalg = mock(AxisLockGroupEx.class);
		when(xaxis.getRangeManager()).thenReturn(xarm);
		when(xarm.getLockGroup()).thenReturn(xalg);
		when(xaxis.isVisible()).thenReturn(true);
		sp.addXAxis(xaxis);
		assertFalse(sp.isValid());
		sp.validate();
		sp.removeXAxis(xaxis);
		assertFalse(sp.isValid());
		sp.validate();

		AxisEx yaxis0 = mock(AxisEx.class);
		AxisRangeManagerEx yarm0 = mock(AxisRangeManagerEx.class);
		AxisLockGroupEx yalg0 = mock(AxisLockGroupEx.class);
		when(yaxis0.getRangeManager()).thenReturn(yarm0);
		when(yarm0.getLockGroup()).thenReturn(yalg0);
		sp.addYAxis(yaxis0);
		assertTrue(sp.isValid());
		sp.validate();
		sp.removeYAxis(yaxis0);
		assertTrue(sp.isValid());

		AxisEx yaxis = mock(AxisEx.class);
		AxisRangeManagerEx yarm = mock(AxisRangeManagerEx.class);
		AxisLockGroupEx yalg = mock(AxisLockGroupEx.class);
		when(yaxis.getRangeManager()).thenReturn(yarm);
		when(yarm.getLockGroup()).thenReturn(yalg);
		when(yaxis.isVisible()).thenReturn(true);
		sp.addYAxis(yaxis);
		assertFalse(sp.isValid());
		sp.validate();
		sp.removeYAxis(yaxis);
		assertFalse(sp.isValid());
		sp.validate();

		SubplotImpl sp0 = new SubplotImpl();
		sp.addSubplot(sp0, null);
		assertFalse(sp.isValid());
		sp.validate();
		sp.removeSubplot(sp0);
		assertFalse(sp.isValid());
		sp.validate();

		SubplotImpl spInvisble = new SubplotImpl();
		spInvisble.setVisible(false);
		sp.addSubplot(spInvisble, null);
		assertTrue(sp.isValid());
		sp.removeSubplot(spInvisble);
		assertTrue(sp.isValid());
		sp.validate();

	}

}
