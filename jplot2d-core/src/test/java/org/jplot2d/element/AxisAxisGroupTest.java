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
 * Those test cases test the relationship between MainAxis and AxisGroup.
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
	public void testCreateAxisWithAxisGroup() {
		MainAxis xaxis = factory.createMainAxis();
		AxisGroup group = xaxis.getGroup();
		assertNotNull(group);
		assertSame(group.getEnvironment(), xaxis.getEnvironment());
		assertArrayEquals(group.getAxes(), new MainAxis[] { xaxis });

		// set the same group again
		assertNotNull(group);
		xaxis.setGroup(group);
		assertSame(group.getEnvironment(), xaxis.getEnvironment());
		assertArrayEquals(group.getAxes(), new MainAxis[] { xaxis });
	}

	@Test
	public void testAddAndRemoveAxis() {
		Subplot sp = factory.createSubplot();
		MainAxis xaxis = factory.createMainAxis();
		MainAxis yaxis = factory.createMainAxis();
		AxisGroup xag = xaxis.getGroup();
		AxisGroup yag = yaxis.getGroup();

		sp.addXAxis(xaxis);
		sp.addYAxis(yaxis);

		assertSame(xaxis.getGroup(), xag);
		assertSame(yaxis.getGroup(), yag);
		assertSame(xag.getEnvironment(), sp.getEnvironment());
		assertSame(yag.getEnvironment(), sp.getEnvironment());

		sp.removeAxis(xaxis);
		sp.removeAxis(yaxis);

		// the axis group should be removed together
		assertSame(xaxis.getGroup(), xag);
		assertSame(yaxis.getGroup(), yag);
		assertNotSame(xag.getEnvironment(), sp.getEnvironment());
		assertNotSame(yag.getEnvironment(), sp.getEnvironment());
	}

	@Test
	public void testSetAxisGroup() {
		Subplot sp = factory.createSubplot();
		MainAxis xaxis = factory.createMainAxis();
		MainAxis yaxis = factory.createMainAxis();
		AxisGroup xag = xaxis.getGroup();
		AxisGroup yag = yaxis.getGroup();

		// set before adding into the same environment
		try {
			yaxis.setGroup(xaxis.getGroup());
			fail("IllegalArgumentException should be thrown.");
		} catch (IllegalArgumentException e) {
			// The axis and group not in the save environment
		}

		sp.addXAxis(xaxis);
		sp.addYAxis(yaxis);
		xaxis.setGroup(yaxis.getGroup());

		// the old x group should be removed from the environment
		assertNull(xag.getParent());
		assertArrayEquals(xag.getAxes(), new MainAxis[0]);
		assertNotSame(xag.getEnvironment(), sp.getEnvironment());

		assertSame(xaxis.getGroup(), yag);
		assertSame(yaxis.getGroup(), yag);
		assertArrayEquals(yag.getAxes(), new MainAxis[] { yaxis, xaxis });
		assertSame(yag.getEnvironment(), sp.getEnvironment());

		// remove axis before assign a new group
		try {
			sp.removeAxis(xaxis);
			fail("IllegalStateException should be thrown.");
		} catch (IllegalStateException e) {

		}

		// set xaxis a new group
		AxisGroup xnag = factory.createAxisGroup(xaxis.getEnvironment());
		xaxis.setGroup(xnag);

		assertSame(xaxis.getGroup(), xnag);
		assertSame(yaxis.getGroup(), yag);
		assertArrayEquals(xnag.getAxes(), new MainAxis[] { xaxis });
		assertArrayEquals(yag.getAxes(), new MainAxis[] { yaxis });
		assertSame(xnag.getEnvironment(), sp.getEnvironment());
		assertSame(yag.getEnvironment(), sp.getEnvironment());

	}

}
