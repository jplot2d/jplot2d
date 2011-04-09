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
import org.jplot2d.env.ElementAddition;
import org.junit.Test;

/**
 * Those test cases test methods on Axis and AxisRangeManager.
 * 
 * @author Jingjing Li
 * 
 */
public class AxisTest {

	private static final ComponentFactory factory = ComponentFactory
			.getInstance();

	/**
	 * Create axis will auto create an viewport axis and axis group.
	 */
	@Test
	public void testCreateAxis() {
		Axis axis = factory.createAxis();
		AxisTick tick = axis.getTick();
		AxisTitle title = axis.getTitle();
		AxisRangeManager arm = axis.getRangeManager();
		AxisLockGroup group = arm.getLockGroup();
		assertTrue(axis instanceof ElementAddition);
		assertTrue(tick instanceof ElementAddition);
		assertTrue(title instanceof ElementAddition);
		assertTrue(arm instanceof ElementAddition);
		assertTrue(group instanceof ElementAddition);
		assertSame(arm.getEnvironment(), axis.getEnvironment());
		assertSame(group.getEnvironment(), axis.getEnvironment());
		assertSame(arm.getParent(), axis);
		assertArrayEquals(arm.getAxes(), new Axis[] { axis });
		assertSame(group.getParent(), arm);
		assertArrayEquals(group.getRangeManagers(),
				new AxisRangeManager[] { arm });

		// set the same range manager again
		axis.setRangeManager(arm);
		assertSame(arm.getEnvironment(), axis.getEnvironment());
		assertArrayEquals(arm.getAxes(), new Axis[] { axis });

		// set the same group again
		arm.setLockGroup(group);
		assertSame(group.getEnvironment(), arm.getEnvironment());
		assertArrayEquals(group.getRangeManagers(),
				new AxisRangeManager[] { arm });
	}

	@Test
	public void testAddAndRemoveAxis() {
		Plot sp = factory.createSubplot();
		Axis xaxis = factory.createAxis();
		Axis yaxis = factory.createAxis();
		AxisRangeManager xva = xaxis.getRangeManager();
		AxisRangeManager yva = yaxis.getRangeManager();
		AxisLockGroup xag = xva.getLockGroup();
		AxisLockGroup yag = yva.getLockGroup();

		sp.addXAxis(xaxis);
		sp.addYAxis(yaxis);

		assertSame(xaxis.getRangeManager(), xva);
		assertSame(yaxis.getRangeManager(), yva);
		assertSame(xva.getLockGroup(), xag);
		assertSame(yva.getLockGroup(), yag);
		assertSame(xva.getEnvironment(), sp.getEnvironment());
		assertSame(yva.getEnvironment(), sp.getEnvironment());
		assertSame(xag.getEnvironment(), xva.getEnvironment());
		assertSame(yag.getEnvironment(), yva.getEnvironment());

		sp.removeXAxis(xaxis);
		sp.removeYAxis(yaxis);

		// the viewport axis and axis group should be removed together
		assertSame(xaxis.getRangeManager(), xva);
		assertSame(yaxis.getRangeManager(), yva);
		assertSame(xva.getLockGroup(), xag);
		assertSame(yva.getLockGroup(), yag);
		assertNotSame(xva.getEnvironment(), sp.getEnvironment());
		assertNotSame(yva.getEnvironment(), sp.getEnvironment());
		assertSame(xag.getEnvironment(), xva.getEnvironment());
		assertSame(yag.getEnvironment(), yva.getEnvironment());
	}

	@Test
	public void testSetAxisRangeManager() {
		Plot sp = factory.createSubplot();
		Axis xaxis = factory.createAxis();
		Axis yaxis = factory.createAxis();

		// set before adding into the same environment
		try {
			yaxis.setRangeManager(xaxis.getRangeManager());
			fail("IllegalArgumentException should be thrown.");
		} catch (IllegalArgumentException e) {
			// The axis and group not in the save environment
		}

		// this is allowed if the viewport axis has no parent
		yaxis.setRangeManager(factory.createAxisRangeManager());
		AxisRangeManager yva = yaxis.getRangeManager();

		sp.addXAxis(xaxis);
		sp.addYAxis(yaxis);
		yaxis.setRangeManager(xaxis.getRangeManager());

		// the old y viewport axis should be removed from the environment
		assertNull(yva.getParent());
		assertArrayEquals(yva.getAxes(), new AxisRangeManager[0]);
		assertNotSame(yva.getEnvironment(), sp.getEnvironment());

		assertSame(yaxis.getRangeManager(), xaxis.getRangeManager());
		assertArrayEquals(xaxis.getRangeManager().getAxes(), new Axis[] {
				xaxis, yaxis });
		assertSame(xaxis.getRangeManager().getEnvironment(),
				sp.getEnvironment());

		// remove x axis
		sp.removeXAxis(xaxis);
		assertNull(xaxis.getRangeManager());
		assertArrayEquals(yaxis.getRangeManager().getAxes(),
				new Axis[] { yaxis });
		assertSame(yaxis.getRangeManager().getEnvironment(),
				sp.getEnvironment());

		// adding an axis with null range manager throws exception
		try {
			sp.addXAxis(xaxis);
			fail("IllegalArgumentException should be thrown.");
		} catch (IllegalArgumentException e) {

		}

		// set xaxis a new range manager
		AxisRangeManager xnag = factory.createAxisRangeManager();
		xaxis.setRangeManager(xnag);

		assertSame(xaxis.getRangeManager(), xnag);
		assertArrayEquals(xnag.getAxes(), new Axis[] { xaxis });
		assertSame(xnag.getEnvironment(), xaxis.getEnvironment());

	}

	@Test
	public void testSetAxisLockGroup() {
		Plot sp = factory.createSubplot();
		Axis xaxis = factory.createAxis();
		Axis yaxis = factory.createAxis();
		AxisRangeManager xva = xaxis.getRangeManager();
		AxisRangeManager yva = yaxis.getRangeManager();
		AxisLockGroup xag = xva.getLockGroup();
		AxisLockGroup yag = yva.getLockGroup();

		// set before adding into the same environment
		try {
			yva.setLockGroup(xva.getLockGroup());
			fail("IllegalArgumentException should be thrown.");
		} catch (IllegalArgumentException e) {
			// The axis and group not in the save environment
		}

		sp.addXAxis(xaxis);
		sp.addYAxis(yaxis);
		xva.setLockGroup(yva.getLockGroup());

		// the old x group should be removed from the environment
		assertNull(xag.getParent());
		assertArrayEquals(xag.getRangeManagers(), new AxisRangeManager[0]);
		assertNotSame(xag.getEnvironment(), sp.getEnvironment());

		assertSame(xva.getLockGroup(), yag);
		assertSame(yva.getLockGroup(), yag);
		assertArrayEquals(yag.getRangeManagers(), new AxisRangeManager[] { yva,
				xva });
		assertSame(yag.getEnvironment(), sp.getEnvironment());

		// remove xaxis with its range manager
		sp.removeXAxis(xaxis);
		assertNull(xva.getLockGroup());
		assertArrayEquals(yag.getRangeManagers(),
				new AxisRangeManager[] { yva });
		assertSame(xaxis.getEnvironment(), xva.getEnvironment());

		// adding an axis with null lock group throws exception
		try {
			sp.addXAxis(xaxis);
			fail("IllegalArgumentException should be thrown.");
		} catch (IllegalArgumentException e) {

		}

		// set xaxis a new group
		AxisLockGroup xnag = factory.createAxisLockGroup();
		xva.setLockGroup(xnag);
		assertSame(xva.getLockGroup(), xnag);
		assertArrayEquals(xnag.getRangeManagers(),
				new AxisRangeManager[] { xva });
		assertSame(xnag.getEnvironment(), xaxis.getEnvironment());

		// add axis with new lock group
		sp.addXAxis(xaxis);
		assertSame(xva.getLockGroup(), xnag);
		assertArrayEquals(xnag.getRangeManagers(),
				new AxisRangeManager[] { xva });
		assertSame(xnag.getEnvironment(), xaxis.getEnvironment());
		assertSame(xaxis.getEnvironment(), sp.getEnvironment());

	}

}
