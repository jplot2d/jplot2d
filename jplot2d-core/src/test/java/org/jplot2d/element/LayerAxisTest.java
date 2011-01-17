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

import static org.junit.Assert.*;

import org.jplot2d.env.ComponentFactory;
import org.junit.Test;

/**
 * Those test cases test the relationship between layer and axis.
 * 
 * @author Jingjing Li
 * 
 */
public class LayerAxisTest {

	private static final ComponentFactory factory = ComponentFactory
			.getInstance();

	/**
	 * Attaching to an axis before join an environment will throw an exception
	 */
	@Test
	public void testLayerAttachAxis() {
		ViewportAxis xaxis = factory.createViewportAxis();
		ViewportAxis yaxis = factory.createViewportAxis();
		Layer layer = factory.createLayer();

		try {
			layer.setXViewportAxis(xaxis);
			fail("IllegalArgumentException should be thrown.");
		} catch (IllegalArgumentException e) {

		}
		try {
			layer.setYViewportAxis(yaxis);
			fail("IllegalArgumentException should be thrown.");
		} catch (IllegalArgumentException e) {

		}
	}

	@Test
	public void testAddAxisAndLayer() {
		Subplot sp = factory.createSubplot();
		ViewportAxis xaxis = factory.createViewportAxis();
		ViewportAxis yaxis = factory.createViewportAxis();
		Layer layer = factory.createLayer();

		sp.addXViewportAxis(xaxis);
		sp.addYViewportAxis(yaxis);
		sp.addLayer(layer, xaxis, yaxis);

		assertSame(xaxis.getParent(), sp);
		assertSame(yaxis.getParent(), sp);
		assertSame(layer.getParent(), sp);
		assertSame(layer.getXViewportAxis(), xaxis);
		assertSame(layer.getYViewportAxis(), yaxis);
		assertArrayEquals(xaxis.getLayers(), new Object[] { layer });
		assertArrayEquals(xaxis.getLayers(), new Object[] { layer });

		// set axis again
		layer.setViewportAxes(xaxis, yaxis);

		assertSame(xaxis.getParent(), sp);
		assertSame(yaxis.getParent(), sp);
		assertSame(layer.getParent(), sp);
		assertSame(layer.getXViewportAxis(), xaxis);
		assertSame(layer.getYViewportAxis(), yaxis);
		assertArrayEquals(xaxis.getLayers(), new Object[] { layer });
		assertArrayEquals(xaxis.getLayers(), new Object[] { layer });
	}

	/**
	 * When adding a removed layer back, its should forget the axis it has
	 * attached.
	 */
	@Test
	public void testRemovAndAddLayer() {

		Subplot sp = factory.createSubplot();
		ViewportAxis xaxis = factory.createViewportAxis();
		ViewportAxis yaxis = factory.createViewportAxis();
		Layer layer = factory.createLayer();

		sp.addXViewportAxis(xaxis);
		sp.addYViewportAxis(yaxis);
		sp.addLayer(layer, xaxis, yaxis);

		sp.removeLayer(layer);

		assertNotSame(layer.getEnvironment(), sp.getEnvironment());
		assertNull(layer.getParent());
		assertNull(layer.getXViewportAxis());
		assertNull(layer.getYViewportAxis());
		assertArrayEquals(xaxis.getLayers(), new Object[0]);
		assertArrayEquals(xaxis.getLayers(), new Object[0]);

		sp.addLayer(layer, xaxis, yaxis);

		assertSame(layer.getXViewportAxis(), xaxis);
		assertSame(layer.getYViewportAxis(), yaxis);
		assertArrayEquals(xaxis.getLayers(), new Object[] { layer });
		assertArrayEquals(xaxis.getLayers(), new Object[] { layer });
	}

	/**
	 * An axis can't be removed when there is layer attach to it.
	 */
	@Test
	public void testRemovAxis() {

		Subplot sp = factory.createSubplot();
		ViewportAxis xaxis = factory.createViewportAxis();
		ViewportAxis yaxis = factory.createViewportAxis();
		Layer layer = factory.createLayer();

		sp.addXViewportAxis(xaxis);
		sp.addYViewportAxis(yaxis);
		sp.addLayer(layer, xaxis, yaxis);

		assertSame(xaxis.getEnvironment(), sp.getEnvironment());
		assertSame(yaxis.getEnvironment(), sp.getEnvironment());
		assertSame(xaxis.getParent(), sp);
		assertSame(yaxis.getParent(), sp);
		assertArrayEquals(sp.getXViewportAxes(), new ViewportAxis[] { xaxis });
		assertArrayEquals(sp.getYViewportAxes(), new ViewportAxis[] { yaxis });
		assertSame(layer.getXViewportAxis(), xaxis);
		assertSame(layer.getYViewportAxis(), yaxis);
		assertArrayEquals(xaxis.getLayers(), new Object[] { layer });
		assertArrayEquals(xaxis.getLayers(), new Object[] { layer });

		try {
			sp.removeXViewportAxis(xaxis);
			fail("IllegalStateException should be thrown");
		} catch (IllegalStateException e) {

		}
		try {
			sp.removeYViewportAxis(yaxis);
			fail("IllegalStateException should be thrown");
		} catch (IllegalStateException e) {

		}

		// test the status after exception is thrown
		assertSame(xaxis.getEnvironment(), sp.getEnvironment());
		assertSame(yaxis.getEnvironment(), sp.getEnvironment());
		assertSame(xaxis.getParent(), sp);
		assertSame(yaxis.getParent(), sp);
		assertArrayEquals(sp.getXViewportAxes(), new ViewportAxis[] { xaxis });
		assertArrayEquals(sp.getYViewportAxes(), new ViewportAxis[] { yaxis });
		assertSame(layer.getXViewportAxis(), xaxis);
		assertSame(layer.getYViewportAxis(), yaxis);
		assertArrayEquals(xaxis.getLayers(), new Object[] { layer });
		assertArrayEquals(xaxis.getLayers(), new Object[] { layer });

		// detach axes
		layer.setViewportAxes(null, null);

		sp.removeXViewportAxis(xaxis);
		sp.removeYViewportAxis(yaxis);

	}

	@Test
	public void testRemovSubplot() {
		Plot p = factory.createPlot();
		Subplot sp = factory.createSubplot();
		ViewportAxis xaxis = factory.createViewportAxis();
		ViewportAxis yaxis = factory.createViewportAxis();
		Layer layer = factory.createLayer();

		p.addSubplot(sp, null);
		sp.addXViewportAxis(xaxis);
		sp.addYViewportAxis(yaxis);
		sp.addLayer(layer, xaxis, yaxis);

		p.removeSubplot(sp);

		assertSame(layer.getXViewportAxis(), xaxis);
		assertSame(layer.getYViewportAxis(), yaxis);
		assertArrayEquals(xaxis.getLayers(), new Object[] { layer });
		assertArrayEquals(xaxis.getLayers(), new Object[] { layer });

	}

	@Test
	public void testRemovSubplotWithAxisBeingAttached() {
		Plot p = factory.createPlot();
		Subplot sp0 = factory.createSubplot();
		Subplot sp1 = factory.createSubplot();
		ViewportAxis xaxis = factory.createViewportAxis();
		ViewportAxis yaxis = factory.createViewportAxis();
		Layer layer0 = factory.createLayer();
		Layer layer1 = factory.createLayer();

		p.addSubplot(sp0, null);
		p.addSubplot(sp1, null);
		sp0.addXViewportAxis(xaxis);
		sp0.addYViewportAxis(yaxis);
		sp0.addLayer(layer0, xaxis, yaxis);
		sp1.addLayer(layer1, xaxis, yaxis);

		try {
			p.removeSubplot(sp0);
			fail("IllegalStateException should be thrown");
		} catch (IllegalStateException e) {

		}

	}

}
