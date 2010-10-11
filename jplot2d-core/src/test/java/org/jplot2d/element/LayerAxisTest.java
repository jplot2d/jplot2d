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
		MainAxis xaxis = factory.createMainAxis();
		MainAxis yaxis = factory.createMainAxis();
		Layer layer = factory.createLayer();

		try {
			layer.setXAxis(xaxis);
			fail("IllegalArgumentException should be thrown.");
		} catch (IllegalArgumentException e) {

		}
		try {
			layer.setYAxis(yaxis);
			fail("IllegalArgumentException should be thrown.");
		} catch (IllegalArgumentException e) {

		}
	}

	@Test
	public void testAddAxisAndLayer() {
		Subplot sp = factory.createSubplot();
		MainAxis xaxis = factory.createMainAxis();
		MainAxis yaxis = factory.createMainAxis();
		Layer layer = factory.createLayer();

		sp.addXAxis(xaxis);
		sp.addYAxis(yaxis);
		sp.addLayer(layer);
		layer.setAxes(xaxis, yaxis);

		assertSame(layer.getXAxis(), xaxis);
		assertSame(layer.getYAxis(), yaxis);
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
		MainAxis xaxis = factory.createMainAxis();
		MainAxis yaxis = factory.createMainAxis();
		Layer layer = factory.createLayer();

		sp.addXAxis(xaxis);
		sp.addYAxis(yaxis);
		sp.addLayer(layer);
		layer.setAxes(xaxis, yaxis);

		sp.removeLayer(layer);

		assertNull(layer.getXAxis());
		assertNull(layer.getYAxis());
		assertArrayEquals(xaxis.getLayers(), new Object[0]);
		assertArrayEquals(xaxis.getLayers(), new Object[0]);

		sp.addLayer(layer);

		assertNull(layer.getXAxis());
		assertNull(layer.getYAxis());
		assertArrayEquals(xaxis.getLayers(), new Object[0]);
		assertArrayEquals(xaxis.getLayers(), new Object[0]);

	}

	@Test
	public void testRemovAxis() {

		Subplot sp = factory.createSubplot();
		MainAxis xaxis = factory.createMainAxis();
		MainAxis yaxis = factory.createMainAxis();
		Layer layer = factory.createLayer();

		sp.addXAxis(xaxis);
		sp.addYAxis(yaxis);
		sp.addLayer(layer);
		layer.setAxes(xaxis, yaxis);

		try {
			sp.removeLayer(layer);
			fail("IllegalStateException should be thrown");
		} catch (IllegalStateException e) {

		}

		// detach axes
		layer.setAxes(null, null);

		try {
			sp.removeLayer(layer);
		} catch (IllegalStateException e) {
			fail("IllegalStateException should not be thrown");
		}

	}

	@Test
	public void testRemovSubplot() {
		Plot p = factory.createPlot();
		Subplot sp = factory.createSubplot();
		MainAxis xaxis = factory.createMainAxis();
		MainAxis yaxis = factory.createMainAxis();
		Layer layer = factory.createLayer();

		p.addSubplot(sp, null);
		sp.addXAxis(xaxis);
		sp.addYAxis(yaxis);
		sp.addLayer(layer);
		layer.setAxes(xaxis, yaxis);

		p.removeSubplot(sp);

		assertSame(layer.getXAxis(), xaxis);
		assertSame(layer.getYAxis(), yaxis);
		assertArrayEquals(xaxis.getLayers(), new Object[] { layer });
		assertArrayEquals(xaxis.getLayers(), new Object[] { layer });

	}

	@Test
	public void testRemovSubplotWithAxisBeingAttached() {
		Plot p = factory.createPlot();
		Subplot sp0 = factory.createSubplot();
		Subplot sp1 = factory.createSubplot();
		MainAxis xaxis = factory.createMainAxis();
		MainAxis yaxis = factory.createMainAxis();
		Layer layer0 = factory.createLayer();
		Layer layer1 = factory.createLayer();

		p.addSubplot(sp0, null);
		p.addSubplot(sp1, null);
		sp0.addXAxis(xaxis);
		sp0.addYAxis(yaxis);
		sp0.addLayer(layer0);
		layer0.setAxes(xaxis, yaxis);
		sp1.addLayer(layer1);
		layer1.setAxes(xaxis, yaxis);

		try {
			p.removeSubplot(sp0);
			fail("IllegalStateException should be thrown");
		} catch (IllegalStateException e) {

		}

	}

}
