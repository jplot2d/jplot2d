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
 * Those test cases test the relationship between ViewportAxis and AxisGroup.
 * 
 * @author Jingjing Li
 * 
 */
public class AxisAxisGroupTest {

	private static final ComponentFactory factory = ComponentFactory
			.getInstance();

	/**
	 * Create axis will auto create an axis group.
	 */
	@Test
	public void testCreateAxisWithAxisLockGroup() {
		ViewportAxis xaxis = factory.createViewportAxis();
		AxisLockGroup group = xaxis.getLockGroup();
		assertNotNull(group);
		assertSame(group.getEnvironment(), xaxis.getEnvironment());
		assertArrayEquals(group.getViewportAxes(), new ViewportAxis[] { xaxis });

		// set the same group again
		assertNotNull(group);
		xaxis.setLockGroup(group);
		assertSame(group.getEnvironment(), xaxis.getEnvironment());
		assertArrayEquals(group.getViewportAxes(), new ViewportAxis[] { xaxis });
	}

	@Test
	public void testAddAndRemoveAxis() {
		Subplot sp = factory.createSubplot();
		ViewportAxis xaxis = factory.createViewportAxis();
		ViewportAxis yaxis = factory.createViewportAxis();
		AxisLockGroup xag = xaxis.getLockGroup();
		AxisLockGroup yag = yaxis.getLockGroup();

		sp.addXViewportAxis(xaxis);
		sp.addYViewportAxis(yaxis);

		assertSame(xaxis.getLockGroup(), xag);
		assertSame(yaxis.getLockGroup(), yag);
		assertSame(xag.getEnvironment(), sp.getEnvironment());
		assertSame(yag.getEnvironment(), sp.getEnvironment());

		sp.removeXViewportAxis(xaxis);
		sp.removeYViewportAxis(yaxis);

		// the axis group should be removed together
		assertSame(xaxis.getLockGroup(), xag);
		assertSame(yaxis.getLockGroup(), yag);
		assertNotSame(xag.getEnvironment(), sp.getEnvironment());
		assertNotSame(yag.getEnvironment(), sp.getEnvironment());
	}

	@Test
	public void testSetAxisGroup() {
		Subplot sp = factory.createSubplot();
		ViewportAxis xaxis = factory.createViewportAxis();
		ViewportAxis yaxis = factory.createViewportAxis();
		AxisLockGroup xag = xaxis.getLockGroup();
		AxisLockGroup yag = yaxis.getLockGroup();

		// set before adding into the same environment
		try {
			yaxis.setLockGroup(xaxis.getLockGroup());
			fail("IllegalArgumentException should be thrown.");
		} catch (IllegalArgumentException e) {
			// The axis and group not in the save environment
		}

		sp.addXViewportAxis(xaxis);
		sp.addYViewportAxis(yaxis);
		xaxis.setLockGroup(yaxis.getLockGroup());

		// the old x group should be removed from the environment
		assertNull(xag.getParent());
		assertArrayEquals(xag.getViewportAxes(), new ViewportAxis[0]);
		assertNotSame(xag.getEnvironment(), sp.getEnvironment());

		assertSame(xaxis.getLockGroup(), yag);
		assertSame(yaxis.getLockGroup(), yag);
		assertArrayEquals(yag.getViewportAxes(), new ViewportAxis[] { yaxis, xaxis });
		assertSame(yag.getEnvironment(), sp.getEnvironment());

		// remove axis before assign a new group
		try {
			sp.removeXViewportAxis(xaxis);
			fail("IllegalStateException should be thrown.");
		} catch (IllegalStateException e) {

		}

		// set xaxis a new group
		AxisLockGroup xnag = factory.createAxisLockGroup(xaxis.getEnvironment());
		xaxis.setLockGroup(xnag);

		assertSame(xaxis.getLockGroup(), xnag);
		assertSame(yaxis.getLockGroup(), yag);
		assertArrayEquals(xnag.getViewportAxes(), new ViewportAxis[] { xaxis });
		assertArrayEquals(yag.getViewportAxes(), new ViewportAxis[] { yaxis });
		assertSame(xnag.getEnvironment(), sp.getEnvironment());
		assertSame(yag.getEnvironment(), sp.getEnvironment());

	}

}
