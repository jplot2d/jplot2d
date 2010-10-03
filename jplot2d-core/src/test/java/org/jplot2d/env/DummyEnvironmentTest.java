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

import org.jplot2d.element.impl.ComponentEx;
import org.jplot2d.element.impl.ContainerEx;
import org.junit.Test;

/**
 * @author Jingjing Li
 * 
 */
public class DummyEnvironmentTest {

	/**
	 * Test method for
	 * {@link PlotEnvironment#componentAdded(org.jplot2d.element.Component, java.util.Map)}
	 * . When a cacheable component added, verify the order.
	 */
	@Test
	public void testRegisterComponent() {

		// register uncacheable component
		{
			DummyEnvironment denv = new DummyEnvironment();
			ComponentEx compA = mock(ComponentEx.class);
			ComponentEx proxyA = mock(ComponentEx.class);
			denv.registerElement(compA, proxyA);
			assertEquals(denv.proxyMap.size(), 1);
			assertEquals(denv.cacheableComponentList.size(), 0);
			assertEquals(denv.subComponentMap.size(), 1);
			assertEquals(denv.subComponentMap.get(compA).size(), 1);
			assertEquals(denv.subComponentMap.get(compA).get(0), compA);
		}

		// register cacheable component
		{
			DummyEnvironment denv = new DummyEnvironment();
			ComponentEx compA = mock(ComponentEx.class);
			ComponentEx proxyA = mock(ComponentEx.class);
			when(compA.isCacheable()).thenReturn(true);
			when(proxyA.isCacheable()).thenReturn(true);
			denv.registerElement(compA, proxyA);
			assertEquals(denv.proxyMap.size(), 1);
			assertEquals(denv.cacheableComponentList.size(), 1);
			assertEquals(denv.subComponentMap.size(), 1);
			assertEquals(denv.subComponentMap.get(compA).size(), 1);
			assertEquals(denv.subComponentMap.get(compA).get(0), compA);
		}

		// register cacheable component with uncacheable child
		{
			DummyEnvironment denv = new DummyEnvironment();
			ContainerEx compA = mock(ContainerEx.class);
			ContainerEx proxyA = mock(ContainerEx.class);
			when(compA.isCacheable()).thenReturn(true);
			when(proxyA.isCacheable()).thenReturn(true);
			ComponentEx compAA = mock(ComponentEx.class);
			ComponentEx proxyAA = mock(ComponentEx.class);
			when(compAA.getParent()).thenReturn(compA);
			when(proxyAA.getParent()).thenReturn(proxyA);
			assertEquals(compAA.getParent(), compA);
			denv.registerElement(compA, proxyA);
			denv.registerElement(compAA, proxyAA);
			assertEquals(denv.proxyMap.size(), 2);
			assertEquals(denv.cacheableComponentList.size(), 1);
			assertEquals(denv.subComponentMap.size(), 1);
			assertEquals(denv.subComponentMap.get(compA).size(), 2);
			assertEquals(denv.subComponentMap.get(compA).get(0), compA);
			assertEquals(denv.subComponentMap.get(compA).get(1), compAA);
		}

		// register cacheable component which has a cacheable child
		{
			DummyEnvironment denv = new DummyEnvironment();
			ContainerEx compA = mock(ContainerEx.class);
			ContainerEx proxyA = mock(ContainerEx.class);
			when(compA.isCacheable()).thenReturn(true);
			when(proxyA.isCacheable()).thenReturn(true);
			ComponentEx compAA = mock(ComponentEx.class);
			ComponentEx proxyAA = mock(ComponentEx.class);
			when(compAA.getParent()).thenReturn(compA);
			when(proxyAA.getParent()).thenReturn(proxyA);
			when(compAA.isCacheable()).thenReturn(true);
			when(proxyAA.isCacheable()).thenReturn(true);
			denv.registerElement(compA, proxyA);
			denv.registerElement(compAA, proxyAA);
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
