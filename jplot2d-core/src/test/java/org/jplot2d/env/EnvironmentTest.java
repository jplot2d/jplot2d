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

import java.util.ArrayList;
import java.util.List;

import org.jplot2d.element.Component;
import org.jplot2d.element.Element;
import org.jplot2d.element.impl.ComponentImpl;
import org.jplot2d.element.impl.ContainerImpl;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author Jingjing Li
 * 
 */
public class EnvironmentTest {

	private class EnvironmentStub extends Environment {

		@Override
		void requireRedraw(Element impl) {
			// TODO Auto-generated method stub

		}

		@Override
		protected void commit() {
			// TODO Auto-generated method stub

		}

	};

	private EnvironmentStub env;

	private ContainerImpl compA, compA1, compA2;

	private ContainerImpl proxyA, proxyA1, proxyA2;

	private ComponentImpl compA1a, compA1b, compA2a, compA2b;

	private ComponentImpl proxyA1a, proxyA1b, proxyA2a, proxyA2b;

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		env = new EnvironmentStub();

		compA = new ContainerImpl();
		compA1 = new ContainerImpl();
		compA1a = new ComponentImpl();
		compA1b = new ComponentImpl();
		compA2 = new ContainerImpl();
		compA2a = new ComponentImpl();
		compA2b = new ComponentImpl();

		compA.setCacheable(true);

		compA1.setParent(compA);
		compA1b.setCacheable(true);
		compA1a.setParent(compA1);
		compA1b.setParent(compA1);

		compA2.setCacheable(true);
		compA2.setParent(compA);
		compA2b.setCacheable(true);
		compA2a.setParent(compA2);
		compA2b.setParent(compA2);

		proxyA = compA.deepCopy(null);
		proxyA1 = compA1.deepCopy(null);
		proxyA2 = compA2.deepCopy(null);
		proxyA1a = compA1a.deepCopy(null);
		proxyA1b = compA1b.deepCopy(null);
		proxyA2a = compA2a.deepCopy(null);
		proxyA2b = compA2b.deepCopy(null);
		proxyA1.setParent(proxyA);
		proxyA2.setParent(proxyA);
		proxyA1a.setParent(proxyA1);
		proxyA1b.setParent(proxyA1);
		proxyA2a.setParent(proxyA2);
		proxyA2b.setParent(proxyA2);

		env.proxyMap.put(compA, proxyA);
		env.proxyMap.put(compA1, proxyA1);
		env.proxyMap.put(compA2, proxyA2);
		env.proxyMap.put(compA1a, proxyA1a);
		env.proxyMap.put(compA1b, proxyA1b);
		env.proxyMap.put(compA2a, proxyA2a);
		env.proxyMap.put(compA2b, proxyA2b);
		env.cacheableComponentList.add(compA);
		env.cacheableComponentList.add(compA1b);
		env.cacheableComponentList.add(compA2);
		env.cacheableComponentList.add(compA2b);
		List<Component> listA = new ArrayList<Component>();
		listA.add(compA);
		listA.add(compA1);
		listA.add(compA1a);
		env.subComponentMap.put(compA, listA);
		List<Component> listA2 = new ArrayList<Component>();
		listA2.add(compA2);
		listA2.add(compA2a);
		env.subComponentMap.put(compA2, listA2);
		List<Component> listA1b = new ArrayList<Component>();
		listA1b.add(compA1b);
		env.subComponentMap.put(compA1b, listA1b);
		List<Component> listA2b = new ArrayList<Component>();
		listA2b.add(compA2b);
		env.subComponentMap.put(compA2b, listA2b);
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testSetUp() {
		assertEquals(env.proxyMap.size(), 7);

		assertEquals(env.cacheableComponentList.size(), 4);
		assertArrayEquals(env.cacheableComponentList.toArray(),
				new Component[] { compA, compA1b, compA2, compA2b });

		assertEquals(env.subComponentMap.size(), 4);
		assertEquals(env.subComponentMap.get(compA).size(), 3);
		assertArrayEquals(env.subComponentMap.get(compA).toArray(),
				new Component[] { compA, compA1, compA1a });
		assertEquals(env.subComponentMap.get(compA1b).size(), 1);
		assertEquals(env.subComponentMap.get(compA1b).get(0), compA1b);
		assertEquals(env.subComponentMap.get(compA2).size(), 2);
		assertArrayEquals(env.subComponentMap.get(compA2).toArray(),
				new Component[] { compA2, compA2a });
		assertEquals(env.subComponentMap.get(compA2b).size(), 1);
		assertEquals(env.subComponentMap.get(compA2b).get(0), compA2b);
	}

	/**
	 * Test method for
	 * {@link PlotEnvironment#componentAdded(org.jplot2d.element.Component, java.util.Map)}
	 * . When a cacheable component added, verify the order.
	 */
	@Test
	public void testComponentAdded() {
		// create a Environment
		Environment env = new EnvironmentStub();

		DummyEnvironment denv = new DummyEnvironment();
		ContainerImpl compA = new ContainerImpl();
		ContainerImpl proxyA = compA.deepCopy(null);
		denv.registerElement(compA, proxyA);

		// add uncacheable component
		try {
			env.componentAdded(compA, denv);
		} catch (Exception e) {
			assertTrue(e instanceof IllegalArgumentException);
		}
		assertEquals(env.proxyMap.size(), 0);

		// add cacheable component
		denv = new DummyEnvironment();
		compA.setCacheable(true);
		proxyA.setCacheable(true);
		denv.registerElement(compA, proxyA);
		env.componentAdded(compA, denv);
		assertEquals(env.proxyMap.size(), 1);
		assertEquals(env.cacheableComponentList.size(), 1);
		assertEquals(env.subComponentMap.size(), 1);
		assertEquals(env.subComponentMap.get(compA).size(), 1);
		assertEquals(env.subComponentMap.get(compA).get(0), compA);

		// add uncacheable component which has a cacheable parent
		DummyEnvironment denvAA = new DummyEnvironment();
		ComponentImpl compAA = new ComponentImpl();
		ComponentImpl proxyAA = compAA.deepCopy(null);
		denvAA.registerElement(compAA, proxyAA);
		// link parent
		compAA.setParent(compA);
		proxyAA.setParent(proxyA);
		env.componentAdded(compAA, denvAA);
		assertEquals(env.proxyMap.size(), 2);
		assertEquals(env.cacheableComponentList.size(), 1);
		assertEquals(env.subComponentMap.size(), 1);
		assertEquals(env.subComponentMap.get(compA).size(), 2);
		assertEquals(env.subComponentMap.get(compA).get(0), compA);
		assertEquals(env.subComponentMap.get(compA).get(1), compAA);

		// add cacheable component with uncacheable sub-component
		DummyEnvironment denvB = new DummyEnvironment();
		ContainerImpl compB = new ContainerImpl();
		ContainerImpl proxyB = compB.deepCopy(null);
		ComponentImpl compBA = new ComponentImpl();
		ComponentImpl proxyBA = compBA.deepCopy(null);
		compB.setCacheable(true);
		proxyB.setCacheable(true);
		compBA.setParent(compB);
		proxyBA.setParent(proxyB);
		denvB.registerElement(compB, proxyB);
		denvB.registerElement(compBA, proxyBA);
		env.componentAdded(compB, denvB);
		assertEquals(env.proxyMap.size(), 4);
		assertEquals(env.cacheableComponentList.size(), 2);
		assertEquals(env.subComponentMap.size(), 2);
		assertEquals(env.subComponentMap.get(compB).size(), 2);
		assertEquals(env.subComponentMap.get(compB).get(0), compB);
		assertEquals(env.subComponentMap.get(compB).get(1), compBA);

	}

	/**
	 * Test removing an uncacheable component.
	 */
	@Test
	public void testUncacheableComponentRemoving() {
		Environment cenv = env.componentRemoving(compA1);

		assertEquals(env.proxyMap.size(), 4);
		assertEquals(env.cacheableComponentList.size(), 3);
		assertEquals(env.subComponentMap.size(), 3);
		assertEquals(env.subComponentMap.get(compA).size(), 1);
		assertEquals(env.subComponentMap.get(compA).get(0), compA);
		assertEquals(env.subComponentMap.get(compA2).size(), 2);
		assertEquals(env.subComponentMap.get(compA2).get(0), compA2);
		assertEquals(env.subComponentMap.get(compA2).get(1), compA2a);
		assertEquals(env.subComponentMap.get(compA2b).size(), 1);
		assertEquals(env.subComponentMap.get(compA2b).get(0), compA2b);

		assertEquals(cenv.proxyMap.size(), 3);
		assertEquals(cenv.cacheableComponentList.size(), 1);
		assertEquals(cenv.subComponentMap.size(), 2);
		assertEquals(cenv.subComponentMap.get(compA1).size(), 2);
		assertEquals(cenv.subComponentMap.get(compA1).get(0), compA1);
		assertEquals(cenv.subComponentMap.get(compA1).get(1), compA1a);
		assertEquals(cenv.subComponentMap.get(compA1b).size(), 1);
		assertEquals(cenv.subComponentMap.get(compA1b).get(0), compA1b);
	}

	/**
	 * Test removing a cacheable component.
	 */
	@Test
	public void testCacheableComponentRemoving() {
		Environment cenv = env.componentRemoving(compA2);

		assertEquals(env.proxyMap.size(), 4);
		assertEquals(env.cacheableComponentList.size(), 2);
		assertEquals(env.subComponentMap.size(), 2);
		assertEquals(env.subComponentMap.get(compA).size(), 3);
		assertEquals(env.subComponentMap.get(compA).get(0), compA);
		assertEquals(env.subComponentMap.get(compA).get(1), compA1);
		assertEquals(env.subComponentMap.get(compA).get(2), compA1a);
		assertEquals(env.subComponentMap.get(compA1b).size(), 1);
		assertEquals(env.subComponentMap.get(compA1b).get(0), compA1b);

		assertEquals(cenv.proxyMap.size(), 3);
		assertEquals(cenv.cacheableComponentList.size(), 2);
		assertEquals(cenv.subComponentMap.size(), 2);
		assertEquals(cenv.subComponentMap.get(compA2).size(), 2);
		assertEquals(cenv.subComponentMap.get(compA2).get(0), compA2);
		assertEquals(cenv.subComponentMap.get(compA2).get(1), compA2a);
		assertEquals(cenv.subComponentMap.get(compA2b).size(), 1);
		assertEquals(cenv.subComponentMap.get(compA2b).get(0), compA2b);
	}

	/**
	 * Test uncacheable Component's zorder changed .
	 */
	@Test
	public void testUncacheableComponentZOrderChanged() {
		compA1.setZOrder(1000);
		env.componentZOrderChanged(compA1);

		assertEquals(env.proxyMap.size(), 7);

		assertEquals(env.cacheableComponentList.size(), 4);
		assertArrayEquals(env.cacheableComponentList.toArray(),
				new Component[] { compA, compA1b, compA2, compA2b });

		assertEquals(env.subComponentMap.size(), 4);
		assertEquals(env.subComponentMap.get(compA).size(), 3);
		assertArrayEquals(env.subComponentMap.get(compA).toArray(),
				new Component[] { compA, compA1a, compA1 });
		assertEquals(env.subComponentMap.get(compA1b).size(), 1);
		assertEquals(env.subComponentMap.get(compA1b).get(0), compA1b);
		assertEquals(env.subComponentMap.get(compA2).size(), 2);
		assertArrayEquals(env.subComponentMap.get(compA2).toArray(),
				new Component[] { compA2, compA2a });
		assertEquals(env.subComponentMap.get(compA2b).size(), 1);
		assertEquals(env.subComponentMap.get(compA2b).get(0), compA2b);

	}

	/**
	 * Test uncacheable Component's z-order changed.
	 */
	@Test
	public void testCacheableComponentZOrderChanged() {
		compA2.setZOrder(1000);
		env.componentZOrderChanged(compA2);

		assertEquals(env.proxyMap.size(), 7);

		assertEquals(env.cacheableComponentList.size(), 4);
		assertArrayEquals(env.cacheableComponentList.toArray(),
				new Component[] { compA, compA1b, compA2b, compA2 });

		assertEquals(env.subComponentMap.size(), 4);
		assertEquals(env.subComponentMap.get(compA).size(), 3);
		assertArrayEquals(env.subComponentMap.get(compA).toArray(),
				new Component[] { compA, compA1, compA1a });
		assertEquals(env.subComponentMap.get(compA1b).size(), 1);
		assertEquals(env.subComponentMap.get(compA1b).get(0), compA1b);
		assertEquals(env.subComponentMap.get(compA2).size(), 2);
		assertArrayEquals(env.subComponentMap.get(compA2).toArray(),
				new Component[] { compA2, compA2a });
		assertEquals(env.subComponentMap.get(compA2b).size(), 1);
		assertEquals(env.subComponentMap.get(compA2b).get(0), compA2b);

	}

	/**
	 * test changing component's cache mode.
	 */
	@Test
	public void testComponentCacheModeChanged() {
		compA1.setCacheable(true);
		env.componentCacheModeChanged(compA1);

		assertEquals(env.cacheableComponentList.size(), 5);
		assertArrayEquals(env.cacheableComponentList.toArray(),
				new Component[] { compA, compA1, compA1b, compA2, compA2b });

		assertEquals(env.subComponentMap.size(), 5);
		assertEquals(env.subComponentMap.get(compA).size(), 1);
		assertEquals(env.subComponentMap.get(compA).get(0), compA);
		assertEquals(env.subComponentMap.get(compA1).size(), 2);
		assertArrayEquals(env.subComponentMap.get(compA1).toArray(),
				new Component[] { compA1, compA1a });
		assertEquals(env.subComponentMap.get(compA1b).size(), 1);
		assertEquals(env.subComponentMap.get(compA1b).get(0), compA1b);
		assertEquals(env.subComponentMap.get(compA2).size(), 2);
		assertArrayEquals(env.subComponentMap.get(compA2).toArray(),
				new Component[] { compA2, compA2a });
		assertEquals(env.subComponentMap.get(compA2b).size(), 1);
		assertEquals(env.subComponentMap.get(compA2b).get(0), compA2b);

		compA2.setCacheable(false);
		env.componentCacheModeChanged(compA2);

		assertEquals(env.cacheableComponentList.size(), 4);
		assertArrayEquals(env.cacheableComponentList.toArray(),
				new Component[] { compA, compA1, compA1b, compA2b });

		assertEquals(env.subComponentMap.size(), 4);
		assertEquals(env.subComponentMap.get(compA).size(), 3);
		assertEquals(env.subComponentMap.get(compA).get(0), compA);
		assertEquals(env.subComponentMap.get(compA).get(1), compA2);
		assertEquals(env.subComponentMap.get(compA).get(2), compA2a);
		assertEquals(env.subComponentMap.get(compA1).size(), 2);
		assertArrayEquals(env.subComponentMap.get(compA1).toArray(),
				new Component[] { compA1, compA1a });
		assertEquals(env.subComponentMap.get(compA1b).size(), 1);
		assertEquals(env.subComponentMap.get(compA1b).get(0), compA1b);
		assertEquals(env.subComponentMap.get(compA2b).size(), 1);
		assertEquals(env.subComponentMap.get(compA2b).get(0), compA2b);

	}

	/**
	 * test changing top component's cache mode.
	 */
	@Test
	public void testTopComponentCacheModeChanged() {
		compA.setCacheable(false);
		env.componentCacheModeChanged(compA);

		assertEquals(env.cacheableComponentList.size(), 3);
		assertArrayEquals(env.cacheableComponentList.toArray(),
				new Component[] { compA1b, compA2, compA2b });

		assertEquals(env.subComponentMap.size(), 4);
		assertEquals(env.subComponentMap.get(compA).size(), 3);
		assertArrayEquals(env.subComponentMap.get(compA).toArray(),
				new Component[] { compA, compA1, compA1a });
		assertEquals(env.subComponentMap.get(compA1b).size(), 1);
		assertEquals(env.subComponentMap.get(compA1b).get(0), compA1b);
		assertEquals(env.subComponentMap.get(compA2).size(), 2);
		assertArrayEquals(env.subComponentMap.get(compA2).toArray(),
				new Component[] { compA2, compA2a });
		assertEquals(env.subComponentMap.get(compA2b).size(), 1);
		assertEquals(env.subComponentMap.get(compA2b).get(0), compA2b);

		compA.setCacheable(true);
		env.componentCacheModeChanged(compA);

		assertEquals(env.cacheableComponentList.size(), 4);
		assertArrayEquals(env.cacheableComponentList.toArray(),
				new Component[] { compA, compA1b, compA2, compA2b });

		assertEquals(env.subComponentMap.size(), 4);
		assertEquals(env.subComponentMap.get(compA).size(), 3);
		assertArrayEquals(env.subComponentMap.get(compA).toArray(),
				new Component[] { compA, compA1, compA1a });
		assertEquals(env.subComponentMap.get(compA1b).size(), 1);
		assertEquals(env.subComponentMap.get(compA1b).get(0), compA1b);
		assertEquals(env.subComponentMap.get(compA2).size(), 2);
		assertArrayEquals(env.subComponentMap.get(compA2).toArray(),
				new Component[] { compA2, compA2a });
		assertEquals(env.subComponentMap.get(compA2b).size(), 1);
		assertEquals(env.subComponentMap.get(compA2b).get(0), compA2b);

	}

}
