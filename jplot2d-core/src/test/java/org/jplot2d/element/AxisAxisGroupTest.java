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
		assertNotNull(xaxis.getGroup());
	}

	@Test
	public void testSetAxisGroup() {
		MainAxis xaxis = factory.createMainAxis();
		MainAxis yaxis = factory.createMainAxis();

		try {
			yaxis.setGroup(xaxis.getGroup());
			fail("IllegalArgumentException should be thrown.");
		} catch (IllegalArgumentException e) {
			// The axis and group not in the save environment
		}
	}

	@Test
	public void testAddAxis() {
		Subplot sp = factory.createSubplot();
		MainAxis xaxis = factory.createMainAxis();
		MainAxis yaxis = factory.createMainAxis();
		AxisGroup xag = xaxis.getGroup();
		AxisGroup yag = yaxis.getGroup();

		sp.addXAxis(xaxis);
		sp.addYAxis(yaxis);

		assertSame(xaxis.getGroup(), xag);
		assertSame(yaxis.getGroup(), yag);
		assertSame(((ElementAddition) xag).getEnvironment(),
				((ElementAddition) sp).getEnvironment());
		assertSame(((ElementAddition) yag).getEnvironment(),
				((ElementAddition) sp).getEnvironment());

	}

}
