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

		ViewportAxisEx xvpa = mock(ViewportAxisEx.class);
		when(xvpa.canContributeToParent()).thenReturn(true);
		when(xvpa.getAxes()).thenReturn(new AxisEx[0]);
		when(xvpa.getLayers()).thenReturn(new LayerEx[0]);
		sp.addXViewportAxis(xvpa);
		assertTrue(sp.isRedrawNeeded());
		sp.clearRedrawNeeded();

		ViewportAxisEx yvpa = mock(ViewportAxisEx.class);
		when(yvpa.canContributeToParent()).thenReturn(true);
		when(yvpa.getAxes()).thenReturn(new AxisEx[0]);
		when(yvpa.getLayers()).thenReturn(new LayerEx[0]);
		sp.addYViewportAxis(yvpa);
		assertTrue(sp.isRedrawNeeded());
		sp.clearRedrawNeeded();

		LayerEx layer0 = mock(LayerEx.class);
		when(layer0.canContributeToParent()).thenReturn(false);
		sp.addLayer(layer0, xvpa, yvpa);
		assertFalse(sp.isRedrawNeeded());

		LayerEx layer1 = mock(LayerEx.class);
		when(layer1.canContributeToParent()).thenReturn(true);
		sp.addLayer(layer1, xvpa, yvpa);
		assertTrue(sp.isRedrawNeeded());
		sp.clearRedrawNeeded();

		sp.removeLayer(layer1);
		assertTrue(sp.isRedrawNeeded());
		sp.clearRedrawNeeded();

		sp.removeLayer(layer0);
		assertFalse(sp.isRedrawNeeded());

		sp.removeXViewportAxis(xvpa);
		assertTrue(sp.isRedrawNeeded());
		sp.clearRedrawNeeded();

		sp.removeYViewportAxis(yvpa);
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

		ViewportAxisEx xvpa0 = mock(ViewportAxisEx.class);
		when(xvpa0.getAxes()).thenReturn(new AxisEx[0]);
		when(xvpa0.getLayers()).thenReturn(new LayerEx[0]);
		sp.addXViewportAxis(xvpa0);
		assertTrue(sp.isValid());
		sp.removeXViewportAxis(xvpa0);
		assertTrue(sp.isValid());

		xvpa0 = mock(ViewportAxisEx.class);
		AxisEx xa0 = mock(AxisEx.class);
		when(xvpa0.getAxes()).thenReturn(new AxisEx[] { xa0 });
		when(xvpa0.getLayers()).thenReturn(new LayerEx[0]);
		sp.addXViewportAxis(xvpa0);
		assertTrue(sp.isValid());
		sp.removeXViewportAxis(xvpa0);
		assertTrue(sp.isValid());

		ViewportAxisEx xvpa = mock(ViewportAxisEx.class);
		AxisEx xa = mock(AxisEx.class);
		when(xa.isVisible()).thenReturn(true);
		when(xvpa.getAxes()).thenReturn(new AxisEx[] { xa });
		when(xvpa.getLayers()).thenReturn(new LayerEx[0]);
		sp.addXViewportAxis(xvpa);
		assertFalse(sp.isValid());
		sp.validate();
		sp.removeXViewportAxis(xvpa);
		assertFalse(sp.isValid());
		sp.validate();

		ViewportAxisEx yvpa0 = mock(ViewportAxisEx.class);
		when(yvpa0.getAxes()).thenReturn(new AxisEx[0]);
		when(yvpa0.getLayers()).thenReturn(new LayerEx[0]);
		sp.addYViewportAxis(yvpa0);
		assertTrue(sp.isValid());
		sp.validate();
		sp.removeYViewportAxis(yvpa0);
		assertTrue(sp.isValid());

		yvpa0 = mock(ViewportAxisEx.class);
		AxisEx ya0 = mock(AxisEx.class);
		when(yvpa0.getAxes()).thenReturn(new AxisEx[] { ya0 });
		when(yvpa0.getLayers()).thenReturn(new LayerEx[0]);
		sp.addYViewportAxis(yvpa0);
		assertTrue(sp.isValid());
		sp.validate();
		sp.removeYViewportAxis(yvpa0);
		assertTrue(sp.isValid());

		ViewportAxisEx yvpa = mock(ViewportAxisEx.class);
		AxisEx ya = mock(AxisEx.class);
		when(ya.isVisible()).thenReturn(true);
		when(yvpa.getAxes()).thenReturn(new AxisEx[] { ya });
		when(yvpa.getLayers()).thenReturn(new LayerEx[0]);
		sp.addYViewportAxis(yvpa);
		assertFalse(sp.isValid());
		sp.validate();
		sp.removeYViewportAxis(yvpa);
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
