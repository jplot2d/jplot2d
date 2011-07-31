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

import org.jplot2d.env.ElementAddition;
import org.junit.Test;

/**
 * Those test cases test methods on Axis and AxisRangeManager.
 * 
 * @author Jingjing Li
 * 
 */
public class AxisTest {

	private static final ElementFactory factory = ElementFactory
			.getInstance();

	/**
	 * Create axis will auto create an viewport axis and axis group.
	 */
	@Test
	public void testCreateAxis() {
		Axis axis = factory.createAxis();
		AxisTitle title = axis.getTitle();
		AxisTickManager tm = axis.getTickManager();
		AxisRangeManager arm = tm.getRangeManager();
		AxisRangeLockGroup group = arm.getLockGroup();
		assertTrue(axis instanceof ElementAddition);
		assertTrue(title instanceof ElementAddition);
		assertTrue(tm instanceof ElementAddition);
		assertTrue(arm instanceof ElementAddition);
		assertTrue(group instanceof ElementAddition);

		assertSame(tm.getEnvironment(), axis.getEnvironment());
		assertSame(arm.getEnvironment(), axis.getEnvironment());
		assertSame(group.getEnvironment(), axis.getEnvironment());

		assertSame(tm.getParent(), axis);
		assertSame(arm.getParent(), tm);
		assertSame(group.getParent(), arm);

		assertArrayEquals(tm.getAxes(), new Axis[] { axis });
		assertArrayEquals(arm.getTickManagers(), new AxisTickManager[] { tm });
		assertArrayEquals(group.getRangeManagers(),
				new AxisRangeManager[] { arm });

		// set the same tick manager again
		axis.setTickManager(tm);
		assertSame(tm.getEnvironment(), axis.getEnvironment());
		assertSame(tm.getParent(), axis);
		assertArrayEquals(tm.getAxes(), new Axis[] { axis });

		// set the same range manager again
		tm.setRangeManager(arm);
		assertSame(arm.getEnvironment(), axis.getEnvironment());
		assertSame(arm.getParent(), tm);
		assertArrayEquals(arm.getTickManagers(), new AxisTickManager[] { tm });

		// set the same group again
		arm.setLockGroup(group);
		assertSame(group.getEnvironment(), arm.getEnvironment());
		assertSame(group.getParent(), arm);
		assertArrayEquals(group.getRangeManagers(),
				new AxisRangeManager[] { arm });
	}

	@Test
	public void testAddAndRemoveAxis() {
		Plot sp = factory.createSubplot();
		Axis xaxis = factory.createAxis();
		Axis yaxis = factory.createAxis();
		AxisTickManager xtm = xaxis.getTickManager();
		AxisTickManager ytm = yaxis.getTickManager();
		AxisRangeManager xarm = xtm.getRangeManager();
		AxisRangeManager yarm = ytm.getRangeManager();
		AxisRangeLockGroup xag = xarm.getLockGroup();
		AxisRangeLockGroup yag = yarm.getLockGroup();

		sp.addXAxis(xaxis);
		sp.addYAxis(yaxis);

		assertSame(xaxis.getTickManager(), xtm);
		assertSame(yaxis.getTickManager(), ytm);
		assertSame(xtm.getRangeManager(), xarm);
		assertSame(ytm.getRangeManager(), yarm);
		assertSame(xarm.getLockGroup(), xag);
		assertSame(yarm.getLockGroup(), yag);
		assertSame(xtm.getEnvironment(), sp.getEnvironment());
		assertSame(ytm.getEnvironment(), sp.getEnvironment());
		assertSame(xarm.getEnvironment(), sp.getEnvironment());
		assertSame(yarm.getEnvironment(), sp.getEnvironment());
		assertSame(xag.getEnvironment(), sp.getEnvironment());
		assertSame(yag.getEnvironment(), sp.getEnvironment());

		sp.removeXAxis(xaxis);
		sp.removeYAxis(yaxis);

		// the axis range manager and axis lock group should be removed together
		assertSame(xaxis.getTickManager(), xtm);
		assertSame(yaxis.getTickManager(), ytm);
		assertSame(xtm.getRangeManager(), xarm);
		assertSame(ytm.getRangeManager(), yarm);
		assertSame(xarm.getLockGroup(), xag);
		assertSame(yarm.getLockGroup(), yag);
		assertNotSame(xaxis.getEnvironment(), sp.getEnvironment());
		assertNotSame(yaxis.getEnvironment(), sp.getEnvironment());
		assertSame(xaxis.getEnvironment(), xtm.getEnvironment());
		assertSame(yaxis.getEnvironment(), ytm.getEnvironment());
		assertSame(xtm.getEnvironment(), xarm.getEnvironment());
		assertSame(ytm.getEnvironment(), yarm.getEnvironment());
		assertSame(xarm.getEnvironment(), xag.getEnvironment());
		assertSame(yarm.getEnvironment(), yag.getEnvironment());
	}

	@Test
	public void testSetAxisTickManager() {
		Plot sp = factory.createSubplot();
		Axis xaxis = factory.createAxis();
		Axis yaxis = factory.createAxis();

		// set before adding into the same environment
		try {
			yaxis.setTickManager(xaxis.getTickManager());
			fail("IllegalArgumentException should be thrown.");
		} catch (IllegalArgumentException e) {
			// The axis and group not in the save environment
		}

		// this is allowed if the viewport axis has no parent
		yaxis.setTickManager(factory.createAxisTickManager());
		AxisTickManager ytm = yaxis.getTickManager();

		sp.addXAxis(xaxis);
		sp.addYAxis(yaxis);
		yaxis.setTickManager(xaxis.getTickManager());

		// the old y viewport axis should be removed from the environment
		assertNull(ytm.getParent());
		assertArrayEquals(ytm.getAxes(), new AxisRangeManager[0]);
		assertNotSame(ytm.getEnvironment(), sp.getEnvironment());

		assertSame(yaxis.getTickManager(), xaxis.getTickManager());
		assertArrayEquals(xaxis.getTickManager().getAxes(), new Axis[] { xaxis,
				yaxis });
		assertSame(xaxis.getTickManager().getEnvironment(), sp.getEnvironment());

		// remove x axis
		sp.removeXAxis(xaxis);
		assertNull(xaxis.getTickManager());
		assertArrayEquals(yaxis.getTickManager().getAxes(),
				new Axis[] { yaxis });
		assertSame(yaxis.getTickManager().getEnvironment(), sp.getEnvironment());

		// adding an axis with null range manager throws exception
		try {
			sp.addXAxis(xaxis);
			fail("IllegalArgumentException should be thrown.");
		} catch (IllegalArgumentException e) {

		}

		// set xaxis a new range manager
		AxisTickManager xntm = factory.createAxisTickManager();
		xaxis.setTickManager(xntm);

		assertSame(xaxis.getTickManager(), xntm);
		assertArrayEquals(xntm.getAxes(), new Axis[] { xaxis });
		assertSame(xntm.getEnvironment(), xaxis.getEnvironment());

	}

	@Test
	public void testSetAxisRangeManager() {
		Plot sp = factory.createSubplot();
		Axis xaxis = factory.createAxis();
		Axis yaxis = factory.createAxis();
		AxisTickManager xtm = xaxis.getTickManager();
		AxisTickManager ytm = yaxis.getTickManager();

		// set before adding into the same environment
		try {
			yaxis.getTickManager().setRangeManager(
					xaxis.getTickManager().getRangeManager());
			fail("IllegalArgumentException should be thrown.");
		} catch (IllegalArgumentException e) {
			// The axis and group not in the save environment
		}

		// this is allowed if the viewport axis has no parent
		yaxis.getTickManager()
				.setRangeManager(factory.createAxisRangeManager());
		AxisRangeManager yva = yaxis.getTickManager().getRangeManager();

		sp.addXAxis(xaxis);
		sp.addYAxis(yaxis);
		ytm.setRangeManager(xtm.getRangeManager());

		// the old y viewport axis should be removed from the environment
		assertNull(yva.getParent());
		assertArrayEquals(yva.getTickManagers(), new AxisTickManager[0]);
		assertNotSame(yva.getEnvironment(), sp.getEnvironment());

		assertSame(ytm.getRangeManager(), xtm.getRangeManager());
		assertArrayEquals(xtm.getRangeManager().getTickManagers(),
				new AxisTickManager[] { xtm, ytm });
		assertSame(xtm.getRangeManager().getEnvironment(), sp.getEnvironment());

		// remove x axis
		sp.removeXAxis(xaxis);
		assertNull(xtm.getRangeManager());
		assertArrayEquals(ytm.getRangeManager().getTickManagers(),
				new AxisTickManager[] { ytm });
		assertSame(ytm.getRangeManager().getEnvironment(), sp.getEnvironment());

		// adding an axis with null range manager throws exception
		try {
			sp.addXAxis(xaxis);
			fail("IllegalArgumentException should be thrown.");
		} catch (IllegalArgumentException e) {

		}

		// set xaxis a new range manager
		AxisRangeManager xnag = factory.createAxisRangeManager();
		xtm.setRangeManager(xnag);

		assertSame(xtm.getRangeManager(), xnag);
		assertArrayEquals(xnag.getTickManagers(), new AxisTickManager[] { xtm });
		assertSame(xnag.getEnvironment(), xaxis.getEnvironment());

	}

	@Test
	public void testSetAxisLockGroup() {
		Plot sp = factory.createSubplot();
		Axis xaxis = factory.createAxis();
		Axis yaxis = factory.createAxis();
		AxisRangeManager xva = xaxis.getTickManager().getRangeManager();
		AxisRangeManager yva = yaxis.getTickManager().getRangeManager();
		AxisRangeLockGroup xag = xva.getLockGroup();
		AxisRangeLockGroup yag = yva.getLockGroup();

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
		AxisRangeLockGroup xnag = factory.createAxisRangeLockGroup();
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
