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

import org.jplot2d.element.PComponent;
import org.jplot2d.element.ElementFactory;
import org.jplot2d.element.PContainer;
import org.jplot2d.element.Element;
import org.jplot2d.element.impl.ComponentEx;
import org.jplot2d.element.impl.ContainerEx;
import org.jplot2d.element.impl.ElementEx;
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

		protected EnvironmentStub() {
			super(false);
			// TODO Auto-generated constructor stub
		}

		protected void commit() {
			// nothing to do
		}

	};

	private static final ElementFactory cf = ElementFactory.getInstance();

	private EnvironmentStub env;

	private ContainerEx containerA, containerA1, containerA2;

	private PContainer proxyA, proxyA1, proxyA2;

	private ComponentEx compA1a, compA1b, compA2a, compA2b;

	private PComponent proxyA1a, proxyA1b, proxyA2a, proxyA2b;

	private ElementEx elementA1c, elementA1c1, elementA2c, elementA2c1;

	private Element proxyA1c, proxyA1c1, proxyA2c, proxyA2c1;

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

		containerA = new ContainerStub();
		containerA1 = new ContainerStub();
		compA1a = new ComponentStub();
		compA1b = new ComponentStub();
		elementA1c = new ElementStub();
		elementA1c1 = new ElementStub();
		containerA2 = new ContainerStub();
		compA2a = new ComponentStub();
		compA2b = new ComponentStub();
		elementA2c = new ElementStub();
		elementA2c1 = new ElementStub();

		containerA.setCacheable(true);

		containerA1.setParent(containerA);
		compA1b.setCacheable(true);
		compA1a.setParent(containerA1);
		compA1b.setParent(containerA1);
		elementA1c.setParent(containerA1);
		elementA1c1.setParent(elementA1c);

		containerA2.setCacheable(true);
		containerA2.setParent(containerA);
		compA2b.setCacheable(true);
		compA2a.setParent(containerA2);
		compA2b.setParent(containerA2);
		elementA2c.setParent(containerA2);
		elementA2c1.setParent(elementA2c);

		proxyA = cf.proxy(containerA, PContainer.class);
		proxyA1 = cf.proxy(containerA1, PContainer.class);
		proxyA2 = cf.proxy(containerA2, PContainer.class);
		proxyA1a = cf.proxy(compA1a, PComponent.class);
		proxyA1b = cf.proxy(compA1b, PComponent.class);
		proxyA1c = cf.proxy(elementA1c, Element.class);
		proxyA1c1 = cf.proxy(elementA1c1, Element.class);
		proxyA2a = cf.proxy(compA2a, PComponent.class);
		proxyA2b = cf.proxy(compA2b, PComponent.class);
		proxyA2c = cf.proxy(elementA2c, Element.class);
		proxyA2c1 = cf.proxy(elementA2c1, Element.class);

		env.proxyMap.put(containerA, proxyA);
		env.proxyMap.put(containerA1, proxyA1);
		env.proxyMap.put(containerA2, proxyA2);
		env.proxyMap.put(compA1a, proxyA1a);
		env.proxyMap.put(compA1b, proxyA1b);
		env.proxyMap.put(elementA1c, proxyA1c);
		env.proxyMap.put(elementA1c1, proxyA1c1);
		env.proxyMap.put(compA2a, proxyA2a);
		env.proxyMap.put(compA2b, proxyA2b);
		env.proxyMap.put(elementA2c, proxyA2c);
		env.proxyMap.put(elementA2c1, proxyA2c1);
		env.cacheableComponentList.add(containerA);
		env.cacheableComponentList.add(compA1b);
		env.cacheableComponentList.add(containerA2);
		env.cacheableComponentList.add(compA2b);
		List<ComponentEx> listA = new ArrayList<ComponentEx>();
		listA.add(containerA);
		listA.add(containerA1);
		listA.add(compA1a);
		env.subComponentMap.put(containerA, listA);
		List<ComponentEx> listA2 = new ArrayList<ComponentEx>();
		listA2.add(containerA2);
		listA2.add(compA2a);
		env.subComponentMap.put(containerA2, listA2);
		List<ComponentEx> listA1b = new ArrayList<ComponentEx>();
		listA1b.add(compA1b);
		env.subComponentMap.put(compA1b, listA1b);
		List<ComponentEx> listA2b = new ArrayList<ComponentEx>();
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
		assertEquals(env.proxyMap.size(), 11);

		assertEquals(env.cacheableComponentList.size(), 4);
		assertArrayEquals(env.cacheableComponentList.toArray(),
				new ComponentEx[] { containerA, compA1b, containerA2, compA2b });

		assertEquals(env.subComponentMap.size(), 4);
		assertEquals(env.subComponentMap.get(containerA).size(), 3);
		assertArrayEquals(env.subComponentMap.get(containerA).toArray(),
				new ComponentEx[] { containerA, containerA1, compA1a });
		assertEquals(env.subComponentMap.get(compA1b).size(), 1);
		assertEquals(env.subComponentMap.get(compA1b).get(0), compA1b);
		assertEquals(env.subComponentMap.get(containerA2).size(), 2);
		assertArrayEquals(env.subComponentMap.get(containerA2).toArray(),
				new ComponentEx[] { containerA2, compA2a });
		assertEquals(env.subComponentMap.get(compA2b).size(), 1);
		assertEquals(env.subComponentMap.get(compA2b).get(0), compA2b);
	}

	/**
	 * When a uncacheable component added, verify the order.
	 */
	@Test
	public void testUncacheableComponentAdded() {
		// create a Environment
		Environment env = new EnvironmentStub();

		// add uncacheable component
		DummyEnvironment denv = new DummyEnvironment(false);
		ContainerEx containerA = new ContainerStub();
		PContainer proxyA = cf.proxy(containerA, PContainer.class);
		denv.registerComponent(containerA, proxyA);
		env.componentAdded(containerA, denv);
		assertEquals(env.proxyMap.size(), 1);
		assertEquals(env.cacheableComponentList.size(), 0);
		assertEquals(env.subComponentMap.size(), 1);
		assertEquals(env.subComponentMap.get(containerA).size(), 1);
		assertEquals(env.subComponentMap.get(containerA).get(0), containerA);

	}

	/**
	 * When a cacheable component added, verify the order.
	 */
	@Test
	public void testCacheableComponentAdded() {
		// create a Environment
		Environment env = new EnvironmentStub();

		// add cacheable component
		DummyEnvironment denv = new DummyEnvironment(false);
		ContainerEx containerA = new ContainerStub();
		PContainer proxyA = cf.proxy(containerA, PContainer.class);
		containerA.setCacheable(true);
		denv.registerComponent(containerA, proxyA);
		env.componentAdded(containerA, denv);
		assertEquals(env.proxyMap.size(), 1);
		assertEquals(env.cacheableComponentList.size(), 1);
		assertEquals(env.subComponentMap.size(), 1);
		assertEquals(env.subComponentMap.get(containerA).size(), 1);
		assertEquals(env.subComponentMap.get(containerA).get(0), containerA);

		// add uncacheable component which has a cacheable parent
		DummyEnvironment denvAA = new DummyEnvironment(false);
		ComponentEx compAA = new ComponentStub();
		PComponent proxyAA = cf.proxy(compAA, PComponent.class);
		denvAA.registerComponent(compAA, proxyAA);
		// link parent
		compAA.setParent(containerA);
		env.componentAdded(compAA, denvAA);
		assertEquals(env.proxyMap.size(), 2);
		assertEquals(env.cacheableComponentList.size(), 1);
		assertEquals(env.subComponentMap.size(), 1);
		assertEquals(env.subComponentMap.get(containerA).size(), 2);
		assertEquals(env.subComponentMap.get(containerA).get(0), containerA);
		assertEquals(env.subComponentMap.get(containerA).get(1), compAA);

		// add cacheable component with uncacheable sub-component
		DummyEnvironment denvB = new DummyEnvironment(false);
		ContainerEx containerB = new ContainerStub();
		PContainer proxyB = cf.proxy(containerB, PContainer.class);
		ComponentEx compBA = new ComponentStub();
		PComponent proxyBA = cf.proxy(compBA, PComponent.class);
		containerB.setCacheable(true);
		compBA.setParent(containerB);
		denvB.registerComponent(containerB, proxyB);
		denvB.registerComponent(compBA, proxyBA);
		env.componentAdded(containerB, denvB);
		assertEquals(env.proxyMap.size(), 4);
		assertEquals(env.cacheableComponentList.size(), 2);
		assertEquals(env.subComponentMap.size(), 2);
		assertEquals(env.subComponentMap.get(containerB).size(), 2);
		assertEquals(env.subComponentMap.get(containerB).get(0), containerB);
		assertEquals(env.subComponentMap.get(containerB).get(1), compBA);

	}

	/**
	 * Test removing an uncacheable component.
	 */
	@Test
	public void testUncacheableComponentRemoved() {
		Environment cenv = env.componentRemoved(containerA, containerA1);

		assertEquals(env.proxyMap.size(), 6);
		assertEquals(env.cacheableComponentList.size(), 3);
		assertEquals(env.subComponentMap.size(), 3);
		assertEquals(env.subComponentMap.get(containerA).size(), 1);
		assertEquals(env.subComponentMap.get(containerA).get(0), containerA);
		assertEquals(env.subComponentMap.get(containerA2).size(), 2);
		assertEquals(env.subComponentMap.get(containerA2).get(0), containerA2);
		assertEquals(env.subComponentMap.get(containerA2).get(1), compA2a);
		assertEquals(env.subComponentMap.get(compA2b).size(), 1);
		assertEquals(env.subComponentMap.get(compA2b).get(0), compA2b);

		assertEquals(cenv.proxyMap.size(), 5);
		assertEquals(cenv.cacheableComponentList.size(), 1);
		assertEquals(cenv.subComponentMap.size(), 2);
		assertEquals(cenv.subComponentMap.get(containerA1).size(), 2);
		assertEquals(cenv.subComponentMap.get(containerA1).get(0), containerA1);
		assertEquals(cenv.subComponentMap.get(containerA1).get(1), compA1a);
		assertEquals(cenv.subComponentMap.get(compA1b).size(), 1);
		assertEquals(cenv.subComponentMap.get(compA1b).get(0), compA1b);
	}

	/**
	 * Test removing a cacheable component.
	 */
	@Test
	public void testCacheableComponentRemoved() {
		Environment cenv = env.componentRemoved(containerA, containerA2);

		assertEquals(env.proxyMap.size(), 6);
		assertEquals(env.cacheableComponentList.size(), 2);
		assertEquals(env.subComponentMap.size(), 2);
		assertEquals(env.subComponentMap.get(containerA).size(), 3);
		assertEquals(env.subComponentMap.get(containerA).get(0), containerA);
		assertEquals(env.subComponentMap.get(containerA).get(1), containerA1);
		assertEquals(env.subComponentMap.get(containerA).get(2), compA1a);
		assertEquals(env.subComponentMap.get(compA1b).size(), 1);
		assertEquals(env.subComponentMap.get(compA1b).get(0), compA1b);

		assertEquals(cenv.proxyMap.size(), 5);
		assertEquals(cenv.cacheableComponentList.size(), 2);
		assertEquals(cenv.subComponentMap.size(), 2);
		assertEquals(cenv.subComponentMap.get(containerA2).size(), 2);
		assertEquals(cenv.subComponentMap.get(containerA2).get(0), containerA2);
		assertEquals(cenv.subComponentMap.get(containerA2).get(1), compA2a);
		assertEquals(cenv.subComponentMap.get(compA2b).size(), 1);
		assertEquals(cenv.subComponentMap.get(compA2b).get(0), compA2b);
	}

	/**
	 * Test uncacheable Component's zorder changed .
	 */
	@Test
	public void testUncacheableComponentZOrderChanged() {
		containerA1.setZOrder(1000);
		env.componentZOrderChanged(containerA1);

		assertEquals(env.proxyMap.size(), 11);

		assertEquals(env.cacheableComponentList.size(), 4);
		assertArrayEquals(env.cacheableComponentList.toArray(),
				new ComponentEx[] { containerA, compA1b, containerA2, compA2b });

		assertEquals(env.subComponentMap.size(), 4);
		assertEquals(env.subComponentMap.get(containerA).size(), 3);
		assertArrayEquals(env.subComponentMap.get(containerA).toArray(),
				new ComponentEx[] { containerA, compA1a, containerA1 });
		assertEquals(env.subComponentMap.get(compA1b).size(), 1);
		assertEquals(env.subComponentMap.get(compA1b).get(0), compA1b);
		assertEquals(env.subComponentMap.get(containerA2).size(), 2);
		assertArrayEquals(env.subComponentMap.get(containerA2).toArray(),
				new ComponentEx[] { containerA2, compA2a });
		assertEquals(env.subComponentMap.get(compA2b).size(), 1);
		assertEquals(env.subComponentMap.get(compA2b).get(0), compA2b);

	}

	/**
	 * Test uncacheable Component's z-order changed.
	 */
	@Test
	public void testCacheableComponentZOrderChanged() {
		containerA2.setZOrder(1000);
		env.componentZOrderChanged(containerA2);

		assertEquals(env.proxyMap.size(), 11);

		assertEquals(env.cacheableComponentList.size(), 4);
		assertArrayEquals(env.cacheableComponentList.toArray(),
				new ComponentEx[] { containerA, compA1b, compA2b, containerA2 });

		assertEquals(env.subComponentMap.size(), 4);
		assertEquals(env.subComponentMap.get(containerA).size(), 3);
		assertArrayEquals(env.subComponentMap.get(containerA).toArray(),
				new ComponentEx[] { containerA, containerA1, compA1a });
		assertEquals(env.subComponentMap.get(compA1b).size(), 1);
		assertEquals(env.subComponentMap.get(compA1b).get(0), compA1b);
		assertEquals(env.subComponentMap.get(containerA2).size(), 2);
		assertArrayEquals(env.subComponentMap.get(containerA2).toArray(),
				new ComponentEx[] { containerA2, compA2a });
		assertEquals(env.subComponentMap.get(compA2b).size(), 1);
		assertEquals(env.subComponentMap.get(compA2b).get(0), compA2b);

	}

	/**
	 * test changing component's cache mode.
	 */
	@Test
	public void testComponentCacheModeChanged() {
		containerA1.setCacheable(true);
		env.componentCacheModeChanged(containerA1);

		assertEquals(env.cacheableComponentList.size(), 5);
		assertArrayEquals(env.cacheableComponentList.toArray(),
				new ComponentEx[] { containerA, containerA1, compA1b,
						containerA2, compA2b });

		assertEquals(env.subComponentMap.size(), 5);
		assertEquals(env.subComponentMap.get(containerA).size(), 1);
		assertEquals(env.subComponentMap.get(containerA).get(0), containerA);
		assertEquals(env.subComponentMap.get(containerA1).size(), 2);
		assertArrayEquals(env.subComponentMap.get(containerA1).toArray(),
				new ComponentEx[] { containerA1, compA1a });
		assertEquals(env.subComponentMap.get(compA1b).size(), 1);
		assertEquals(env.subComponentMap.get(compA1b).get(0), compA1b);
		assertEquals(env.subComponentMap.get(containerA2).size(), 2);
		assertArrayEquals(env.subComponentMap.get(containerA2).toArray(),
				new ComponentEx[] { containerA2, compA2a });
		assertEquals(env.subComponentMap.get(compA2b).size(), 1);
		assertEquals(env.subComponentMap.get(compA2b).get(0), compA2b);

		containerA2.setCacheable(false);
		env.componentCacheModeChanged(containerA2);

		assertEquals(env.cacheableComponentList.size(), 4);
		assertArrayEquals(env.cacheableComponentList.toArray(),
				new ComponentEx[] { containerA, containerA1, compA1b, compA2b });

		assertEquals(env.subComponentMap.size(), 4);
		assertEquals(env.subComponentMap.get(containerA).size(), 3);
		assertEquals(env.subComponentMap.get(containerA).get(0), containerA);
		assertEquals(env.subComponentMap.get(containerA).get(1), containerA2);
		assertEquals(env.subComponentMap.get(containerA).get(2), compA2a);
		assertEquals(env.subComponentMap.get(containerA1).size(), 2);
		assertArrayEquals(env.subComponentMap.get(containerA1).toArray(),
				new ComponentEx[] { containerA1, compA1a });
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
		containerA.setCacheable(false);
		env.componentCacheModeChanged(containerA);

		assertEquals(env.cacheableComponentList.size(), 3);
		assertArrayEquals(env.cacheableComponentList.toArray(),
				new ComponentEx[] { compA1b, containerA2, compA2b });

		assertEquals(env.subComponentMap.size(), 4);
		assertEquals(env.subComponentMap.get(containerA).size(), 3);
		assertArrayEquals(env.subComponentMap.get(containerA).toArray(),
				new ComponentEx[] { containerA, containerA1, compA1a });
		assertEquals(env.subComponentMap.get(compA1b).size(), 1);
		assertEquals(env.subComponentMap.get(compA1b).get(0), compA1b);
		assertEquals(env.subComponentMap.get(containerA2).size(), 2);
		assertArrayEquals(env.subComponentMap.get(containerA2).toArray(),
				new ComponentEx[] { containerA2, compA2a });
		assertEquals(env.subComponentMap.get(compA2b).size(), 1);
		assertEquals(env.subComponentMap.get(compA2b).get(0), compA2b);

		containerA.setCacheable(true);
		env.componentCacheModeChanged(containerA);

		assertEquals(env.cacheableComponentList.size(), 4);
		assertArrayEquals(env.cacheableComponentList.toArray(),
				new ComponentEx[] { containerA, compA1b, containerA2, compA2b });

		assertEquals(env.subComponentMap.size(), 4);
		assertEquals(env.subComponentMap.get(containerA).size(), 3);
		assertArrayEquals(env.subComponentMap.get(containerA).toArray(),
				new ComponentEx[] { containerA, containerA1, compA1a });
		assertEquals(env.subComponentMap.get(compA1b).size(), 1);
		assertEquals(env.subComponentMap.get(compA1b).get(0), compA1b);
		assertEquals(env.subComponentMap.get(containerA2).size(), 2);
		assertArrayEquals(env.subComponentMap.get(containerA2).toArray(),
				new ComponentEx[] { containerA2, compA2a });
		assertEquals(env.subComponentMap.get(compA2b).size(), 1);
		assertEquals(env.subComponentMap.get(compA2b).get(0), compA2b);

	}

}
