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

import org.junit.Test;

/**
 * Those test cases test the relationship between layer and axis.
 * 
 * @author Jingjing Li
 * 
 */
public class LayerAxisTest {

	private static final ElementFactory factory = ElementFactory
			.getInstance();

	/**
	 * Attaching to an axis before join an environment will throw an exception
	 */
	@Test
	public void testLayerAttachAxis() {
		AxisTransform xaxis = factory.createAxisRangeManager();
		AxisTransform yaxis = factory.createAxisRangeManager();
		Layer layer = factory.createLayer();

		try {
			layer.setXRangeManager(xaxis);
			fail("IllegalArgumentException should be thrown.");
		} catch (IllegalArgumentException e) {

		}
		try {
			layer.setYRangeManager(yaxis);
			fail("IllegalArgumentException should be thrown.");
		} catch (IllegalArgumentException e) {

		}
	}

	@Test
	public void testAddAxisAndLayer() {
		Plot sp = factory.createSubplot();
		Axis xaxis = factory.createAxis();
		Axis yaxis = factory.createAxis();
		Layer layer = factory.createLayer();

		sp.addXAxis(xaxis);
		sp.addYAxis(yaxis);
		sp.addLayer(layer, xaxis.getTickManager().getAxisTransform(), yaxis
				.getTickManager().getAxisTransform());

		assertSame(xaxis.getParent(), sp);
		assertSame(yaxis.getParent(), sp);
		assertSame(layer.getParent(), sp);
		assertSame(layer.getXRangeManager(), xaxis.getTickManager()
				.getAxisTransform());
		assertSame(layer.getYRangeManager(), yaxis.getTickManager()
				.getAxisTransform());
		assertArrayEquals(xaxis.getTickManager().getAxisTransform().getLayers(),
				new Object[] { layer });
		assertArrayEquals(xaxis.getTickManager().getAxisTransform().getLayers(),
				new Object[] { layer });

		// set axis again
		layer.setRangeManager(xaxis.getTickManager().getAxisTransform(), yaxis
				.getTickManager().getAxisTransform());

		assertSame(xaxis.getParent(), sp);
		assertSame(yaxis.getParent(), sp);
		assertSame(layer.getParent(), sp);
		assertSame(layer.getXRangeManager(), xaxis.getTickManager()
				.getAxisTransform());
		assertSame(layer.getYRangeManager(), yaxis.getTickManager()
				.getAxisTransform());
		assertArrayEquals(xaxis.getTickManager().getAxisTransform().getLayers(),
				new Object[] { layer });
		assertArrayEquals(xaxis.getTickManager().getAxisTransform().getLayers(),
				new Object[] { layer });
	}

	/**
	 * When adding a removed layer back, its should forget the axis it has
	 * attached.
	 */
	@Test
	public void testRemovAndAddLayer() {

		Plot sp = factory.createSubplot();
		Axis xaxis = factory.createAxis();
		Axis yaxis = factory.createAxis();
		Layer layer = factory.createLayer();

		sp.addXAxis(xaxis);
		sp.addYAxis(yaxis);
		sp.addLayer(layer, xaxis.getTickManager().getAxisTransform(), yaxis
				.getTickManager().getAxisTransform());

		sp.removeLayer(layer);

		assertNotSame(layer.getEnvironment(), sp.getEnvironment());
		assertNull(layer.getParent());
		assertNull(layer.getXRangeManager());
		assertNull(layer.getYRangeManager());
		assertArrayEquals(xaxis.getTickManager().getAxisTransform().getLayers(),
				new Object[0]);
		assertArrayEquals(xaxis.getTickManager().getAxisTransform().getLayers(),
				new Object[0]);

		sp.addLayer(layer, xaxis.getTickManager().getAxisTransform(), yaxis
				.getTickManager().getAxisTransform());

		assertSame(layer.getXRangeManager(), xaxis.getTickManager()
				.getAxisTransform());
		assertSame(layer.getYRangeManager(), yaxis.getTickManager()
				.getAxisTransform());
		assertArrayEquals(xaxis.getTickManager().getAxisTransform().getLayers(),
				new Object[] { layer });
		assertArrayEquals(xaxis.getTickManager().getAxisTransform().getLayers(),
				new Object[] { layer });
	}

	/**
	 * An axis can't be removed when there is layer attach to it.
	 */
	@Test
	public void testRemovAxis() {

		Plot sp = factory.createSubplot();
		Axis xaxis = factory.createAxis();
		Axis yaxis = factory.createAxis();
		Layer layer = factory.createLayer();
		AxisTransform xva = xaxis.getTickManager().getAxisTransform();
		AxisTransform yva = yaxis.getTickManager().getAxisTransform();

		sp.addXAxis(xaxis);
		sp.addYAxis(yaxis);
		sp.addLayer(layer, xaxis.getTickManager().getAxisTransform(), yaxis
				.getTickManager().getAxisTransform());

		assertSame(xaxis.getEnvironment(), sp.getEnvironment());
		assertSame(yaxis.getEnvironment(), sp.getEnvironment());
		assertSame(xaxis.getParent(), sp);
		assertSame(yaxis.getParent(), sp);
		assertArrayEquals(sp.getXAxes(), new Axis[] { xaxis });
		assertArrayEquals(sp.getYAxes(), new Axis[] { yaxis });
		assertSame(layer.getXRangeManager(), xva);
		assertSame(layer.getYRangeManager(), yva);
		assertArrayEquals(xva.getLayers(), new Object[] { layer });
		assertArrayEquals(yva.getLayers(), new Object[] { layer });

		// removing axis not allowed
		try {
			sp.removeXAxis(xaxis);
			fail("IllegalStateException should be thrown");
		} catch (IllegalStateException e) {

		}
		try {
			sp.removeYAxis(yaxis);
			fail("IllegalStateException should be thrown");
		} catch (IllegalStateException e) {

		}

		// test the status after exception is thrown
		assertSame(xaxis.getEnvironment(), sp.getEnvironment());
		assertSame(yaxis.getEnvironment(), sp.getEnvironment());
		assertSame(xaxis.getParent(), sp);
		assertSame(yaxis.getParent(), sp);
		assertArrayEquals(sp.getXAxes(), new Axis[] { xaxis });
		assertArrayEquals(sp.getYAxes(), new Axis[] { yaxis });
		assertSame(layer.getXRangeManager(), xva);
		assertSame(layer.getYRangeManager(), yva);
		assertArrayEquals(xva.getLayers(), new Object[] { layer });
		assertArrayEquals(yva.getLayers(), new Object[] { layer });

		// try to detach viewport axis
		try {
			layer.setXRangeManager(null);
			fail("IllegalArgumentException should be thrown");
		} catch (IllegalArgumentException e) {

		}
		try {
			layer.setYRangeManager(null);
			fail("IllegalArgumentException should be thrown");
		} catch (IllegalArgumentException e) {

		}

		// remove layer will release viewport axes
		sp.removeLayer(layer);
		assertNull(layer.getXRangeManager());
		assertNull(layer.getYRangeManager());

		sp.removeXAxis(xaxis);
		sp.removeYAxis(yaxis);
	}

	/**
	 * Remove subplot will remove axes and layers together.
	 */
	@Test
	public void testRemovSubplot() {
		Plot p = factory.createPlot();
		Plot sp = factory.createSubplot();
		Axis xaxis = factory.createAxis();
		Axis yaxis = factory.createAxis();
		Layer layer = factory.createLayer();

		p.addSubplot(sp, null);
		sp.addXAxis(xaxis);
		sp.addYAxis(yaxis);
		sp.addLayer(layer, xaxis.getTickManager().getAxisTransform(), yaxis
				.getTickManager().getAxisTransform());

		p.removeSubplot(sp);

		assertSame(layer.getXRangeManager(), xaxis.getTickManager()
				.getAxisTransform());
		assertSame(layer.getYRangeManager(), yaxis.getTickManager()
				.getAxisTransform());
		assertArrayEquals(xaxis.getTickManager().getAxisTransform().getLayers(),
				new Object[] { layer });
		assertArrayEquals(xaxis.getTickManager().getAxisTransform().getLayers(),
				new Object[] { layer });

	}

	@Test
	public void testRemovSubplotWithAxisBeingAttached() {
		Plot p = factory.createPlot();
		Plot sp0 = factory.createSubplot();
		Plot sp1 = factory.createSubplot();
		Axis xaxis = factory.createAxis();
		Axis yaxis = factory.createAxis();
		Layer layer0 = factory.createLayer();
		Layer layer1 = factory.createLayer();

		p.addSubplot(sp0, null);
		p.addSubplot(sp1, null);
		sp0.addXAxis(xaxis);
		sp0.addYAxis(yaxis);
		sp0.addLayer(layer0, xaxis.getTickManager().getAxisTransform(), yaxis
				.getTickManager().getAxisTransform());
		sp1.addLayer(layer1, xaxis.getTickManager().getAxisTransform(), yaxis
				.getTickManager().getAxisTransform());

		try {
			p.removeSubplot(sp0);
			fail("IllegalStateException should be thrown");
		} catch (IllegalStateException e) {

		}

	}

}
