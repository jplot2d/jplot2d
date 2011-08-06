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
package org.jplot2d.env;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import org.jplot2d.element.PComponent;
import org.jplot2d.element.ElementFactory;
import org.jplot2d.element.PContainer;
import org.jplot2d.element.impl.ComponentEx;
import org.jplot2d.element.impl.ContainerEx;
import org.junit.Test;

/**
 * @author Jingjing Li
 * 
 */
public class DummyEnvironmentTest {

	private static final ElementFactory cf = ElementFactory.getInstance();

	/**
	 * Test method for
	 * {@link PlotEnvironment#componentAdded(org.jplot2d.element.PComponent, java.util.Map)}
	 * . When a cacheable component added, verify the order.
	 */
	@Test
	public void testRegisterComponent() {

		// register uncacheable component
		{
			DummyEnvironment denv = new DummyEnvironment(false);
			ComponentEx compA = mock(ComponentEx.class);
			PComponent proxyA = cf.proxy(compA, PComponent.class);
			denv.registerComponent(compA, proxyA);
			assertEquals(denv.proxyMap.size(), 1);
			assertEquals(denv.cacheableComponentList.size(), 0);
			assertEquals(denv.subComponentMap.size(), 1);
			assertEquals(denv.subComponentMap.get(compA).size(), 1);
			assertEquals(denv.subComponentMap.get(compA).get(0), compA);
		}

		// register cacheable component
		{
			DummyEnvironment denv = new DummyEnvironment(false);
			ComponentEx compA = mock(ComponentEx.class);
			PComponent proxyA = cf.proxy(compA, PComponent.class);
			when(compA.isCacheable()).thenReturn(true);
			denv.registerComponent(compA, proxyA);
			assertEquals(denv.proxyMap.size(), 1);
			assertEquals(denv.cacheableComponentList.size(), 1);
			assertEquals(denv.subComponentMap.size(), 1);
			assertEquals(denv.subComponentMap.get(compA).size(), 1);
			assertEquals(denv.subComponentMap.get(compA).get(0), compA);
		}

		// register cacheable component with uncacheable child
		{
			DummyEnvironment denv = new DummyEnvironment(false);
			ContainerEx compA = mock(ContainerEx.class);
			PContainer proxyA = cf.proxy(compA, PContainer.class);
			when(compA.isCacheable()).thenReturn(true);
			ComponentEx compAA = mock(ComponentEx.class);
			PComponent proxyAA = cf.proxy(compAA, PComponent.class);
			when(compAA.getParent()).thenReturn(compA);
			assertEquals(compAA.getParent(), compA);
			denv.registerComponent(compA, proxyA);
			denv.registerComponent(compAA, proxyAA);
			assertEquals(denv.proxyMap.size(), 2);
			assertEquals(denv.cacheableComponentList.size(), 1);
			assertEquals(denv.subComponentMap.size(), 1);
			assertEquals(denv.subComponentMap.get(compA).size(), 2);
			assertEquals(denv.subComponentMap.get(compA).get(0), compA);
			assertEquals(denv.subComponentMap.get(compA).get(1), compAA);
		}

		// register cacheable component which has a cacheable child
		{
			DummyEnvironment denv = new DummyEnvironment(false);
			ContainerEx compA = mock(ContainerEx.class);
			PContainer proxyA = cf.proxy(compA, PContainer.class);
			when(compA.isCacheable()).thenReturn(true);
			ComponentEx compAA = mock(ComponentEx.class);
			PComponent proxyAA = cf.proxy(compAA, PComponent.class);
			when(compAA.getParent()).thenReturn(compA);
			when(compAA.isCacheable()).thenReturn(true);
			denv.registerComponent(compA, proxyA);
			denv.registerComponent(compAA, proxyAA);
			assertEquals(denv.proxyMap.size(), 2);
			assertEquals(denv.cacheableComponentList.size(), 2);
			assertEquals(denv.subComponentMap.size(), 2);
			assertEquals(denv.subComponentMap.get(compA).size(), 1);
			assertEquals(denv.subComponentMap.get(compA).get(0), compA);
			assertEquals(denv.subComponentMap.get(compAA).size(), 1);
			assertEquals(denv.subComponentMap.get(compAA).get(0), compAA);
		}

	}

}
