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

import org.jplot2d.element.PComponent;
import org.jplot2d.element.ElementFactory;
import org.jplot2d.element.Element;
import org.jplot2d.element.impl.ComponentEx;
import org.jplot2d.element.impl.ContainerEx;
import org.jplot2d.element.impl.ElementEx;
import org.jplot2d.env.PlotEnvironment.CacheBlock;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author Jingjing Li
 * 
 */
public class PlotEnvironmentTest {

	private PlotEnvironment env;

	private ContainerEx containerA, containerA1, containerA2;

	private PComponent proxyA, proxyA1, proxyA2;

	private ComponentEx compA1a, compA1b, compA2a, compA2b;

	private PComponent proxyA1a, proxyA1b, proxyA2a, proxyA2b;

	private ElementEx elementA1c, elementA1c1, elementA2c, elementA2c1;

	private Element proxyA1c, proxyA1c1, proxyA2c, proxyA2c1;

	public static void checkCacheBlock(CacheBlock cb, ComponentEx comp, ComponentEx[] subcomps) {
		assertEquals(cb.getUid(), comp);
		assertEquals(cb.getComp(), comp);
		assertArrayEquals(cb.getSubcomps().toArray(), subcomps);
	}

	@BeforeClass
	public static void setUpBeforeClass() {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() {
		env = new PlotEnvironment(false);

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

		proxyA = ElementFactory.proxy(containerA, PComponent.class);
		proxyA1 = ElementFactory.proxy(containerA1, PComponent.class);
		proxyA2 = ElementFactory.proxy(containerA2, PComponent.class);
		proxyA1a = ElementFactory.proxy(compA1a, PComponent.class);
		proxyA1b = ElementFactory.proxy(compA1b, PComponent.class);
		proxyA1c = ElementFactory.proxy(elementA1c, Element.class);
		proxyA1c1 = ElementFactory.proxy(elementA1c1, Element.class);
		proxyA2a = ElementFactory.proxy(compA2a, PComponent.class);
		proxyA2b = ElementFactory.proxy(compA2b, PComponent.class);
		proxyA2c = ElementFactory.proxy(elementA2c, Element.class);
		proxyA2c1 = ElementFactory.proxy(elementA2c1, Element.class);

		env.proxyMap.put(containerA, proxyA);
		env.proxyMap.put(containerA1, proxyA1);
		env.proxyMap.put(compA1a, proxyA1a);
		env.proxyMap.put(compA1b, proxyA1b);
		env.proxyMap.put(elementA1c, proxyA1c);
		env.proxyMap.put(elementA1c1, proxyA1c1);
		env.proxyMap.put(containerA2, proxyA2);
		env.proxyMap.put(compA2a, proxyA2a);
		env.proxyMap.put(compA2b, proxyA2b);
		env.proxyMap.put(elementA2c, proxyA2c);
		env.proxyMap.put(elementA2c1, proxyA2c1);

		for (ElementEx element : env.proxyMap.keySet()) {
			env.copyMap.put(element, element);
		}
		env.buildComponentCacheBlock();
	}

	@After
	public void tearDown() {
	}

	@Test
	public void testSetUp() {
		assertEquals(env.proxyMap.size(), 11);

		assertEquals(env.cacheBlockList.size(), 4);
		checkCacheBlock(env.cacheBlockList.get(0), containerA, new ComponentEx[] { containerA, containerA1, compA1a });
		checkCacheBlock(env.cacheBlockList.get(1), compA1b, new ComponentEx[] { compA1b });
		checkCacheBlock(env.cacheBlockList.get(2), containerA2, new ComponentEx[] { containerA2, compA2a });
		checkCacheBlock(env.cacheBlockList.get(3), compA2b, new ComponentEx[] { compA2b });
	}

	/**
	 * When a uncacheable component added, verify the order.
	 */
	@Test
	public void testUncacheableComponentAdded() {
		// create a Environment
		PlotEnvironment env = new PlotEnvironment(false);

		// add uncacheable component
		DummyEnvironment denv = new DummyEnvironment(false);
		ContainerEx containerA = new ContainerStub();
		PComponent proxyA = ElementFactory.proxy(containerA, PComponent.class);
		denv.registerComponent(containerA, proxyA);
		env.componentAdded(containerA, denv);

		for (ElementEx element : env.proxyMap.keySet()) {
			env.copyMap.put(element, element);
		}
		env.buildComponentCacheBlock();

		assertEquals(env.proxyMap.size(), 1);
		assertEquals(env.cacheBlockList.size(), 0);
	}

	/**
	 * When a cacheable component added, verify the order.
	 */
	@Test
	public void testCacheableComponentAdded() {
		// create a Environment
		PlotEnvironment env = new PlotEnvironment(false);

		// add cacheable component
		DummyEnvironment denv = new DummyEnvironment(false);
		ContainerEx containerA = new ContainerStub();
		PComponent proxyA = ElementFactory.proxy(containerA, PComponent.class);
		containerA.setCacheable(true);
		denv.registerComponent(containerA, proxyA);
		env.componentAdded(containerA, denv);

		for (ElementEx element : env.proxyMap.keySet()) {
			env.copyMap.put(element, element);
		}
		env.buildComponentCacheBlock();

		assertEquals(env.proxyMap.size(), 1);
		assertEquals(env.cacheBlockList.size(), 1);
		checkCacheBlock(env.cacheBlockList.get(0), containerA, new ComponentEx[] { containerA });

		// add uncacheable component which has a cacheable parent
		DummyEnvironment denvAA = new DummyEnvironment(false);
		ComponentEx compAA = new ComponentStub();
		PComponent proxyAA = ElementFactory.proxy(compAA, PComponent.class);
		denvAA.registerComponent(compAA, proxyAA);
		// link parent
		compAA.setParent(containerA);
		env.componentAdded(compAA, denvAA);

		for (ElementEx element : env.proxyMap.keySet()) {
			env.copyMap.put(element, element);
		}
		env.buildComponentCacheBlock();

		assertEquals(env.proxyMap.size(), 2);
		assertEquals(env.cacheBlockList.size(), 1);
		checkCacheBlock(env.cacheBlockList.get(0), containerA, new ComponentEx[] { containerA, compAA });

		// add cacheable component with uncacheable sub-component
		DummyEnvironment denvB = new DummyEnvironment(false);
		ContainerEx containerB = new ContainerStub();
		PComponent proxyB = ElementFactory.proxy(containerB, PComponent.class);
		ComponentEx compBA = new ComponentStub();
		PComponent proxyBA = ElementFactory.proxy(compBA, PComponent.class);
		containerB.setCacheable(true);
		compBA.setParent(containerB);
		denvB.registerComponent(containerB, proxyB);
		denvB.registerComponent(compBA, proxyBA);
		env.componentAdded(containerB, denvB);

		for (ElementEx element : env.proxyMap.keySet()) {
			env.copyMap.put(element, element);
		}
		env.buildComponentCacheBlock();

		assertEquals(env.proxyMap.size(), 4);
		assertEquals(env.cacheBlockList.size(), 2);
		checkCacheBlock(env.cacheBlockList.get(1), containerB, new ComponentEx[] { containerB, compBA });
	}

	/**
	 * Test removing an uncacheable component.
	 */
	@Test
	public void testUncacheableComponentRemoved() {
		Environment cenv = env.componentRemoved(containerA1);
		env.buildComponentCacheBlock();

		assertEquals(env.proxyMap.size(), 6);
		assertEquals(env.cacheBlockList.size(), 3);
		checkCacheBlock(env.cacheBlockList.get(0), containerA, new ComponentEx[] { containerA });
		checkCacheBlock(env.cacheBlockList.get(1), containerA2, new ComponentEx[] { containerA2, compA2a });
		checkCacheBlock(env.cacheBlockList.get(2), compA2b, new ComponentEx[] { compA2b });

		assertEquals(cenv.proxyMap.size(), 5);
		assertArrayEquals(cenv.proxyMap.keySet().toArray(), new ElementEx[] { containerA1, compA1a, compA1b,
				elementA1c, elementA1c1 });
	}

	/**
	 * Test removing a cacheable component.
	 */
	@Test
	public void testCacheableComponentRemoved() {
		Environment cenv = env.componentRemoved(containerA2);

		env.buildComponentCacheBlock();

		assertEquals(env.proxyMap.size(), 6);
		assertEquals(env.cacheBlockList.size(), 2);
		checkCacheBlock(env.cacheBlockList.get(0), containerA, new ComponentEx[] { containerA, containerA1, compA1a });
		checkCacheBlock(env.cacheBlockList.get(1), compA1b, new ComponentEx[] { compA1b });

		assertEquals(cenv.proxyMap.size(), 5);
		assertArrayEquals(cenv.proxyMap.keySet().toArray(), new ElementEx[] { containerA2, compA2a, compA2b,
				elementA2c, elementA2c1 });
	}

	/**
	 * Test uncacheable Component's zorder changed .
	 */
	@Test
	public void testUncacheableComponentZOrderChanged() {
		containerA1.setZOrder(1000);

		env.buildComponentCacheBlock();

		assertEquals(env.proxyMap.size(), 11);
		assertEquals(env.cacheBlockList.size(), 4);
		checkCacheBlock(env.cacheBlockList.get(0), containerA, new ComponentEx[] { containerA, compA1a, containerA1 });
		checkCacheBlock(env.cacheBlockList.get(1), compA1b, new ComponentEx[] { compA1b });
		checkCacheBlock(env.cacheBlockList.get(2), containerA2, new ComponentEx[] { containerA2, compA2a });
		checkCacheBlock(env.cacheBlockList.get(3), compA2b, new ComponentEx[] { compA2b });
	}

	/**
	 * Test uncacheable Component's z-order changed.
	 */
	@Test
	public void testCacheableComponentZOrderChanged() {
		containerA2.setZOrder(1000);

		env.buildComponentCacheBlock();

		assertEquals(env.proxyMap.size(), 11);
		assertEquals(env.cacheBlockList.size(), 4);
		checkCacheBlock(env.cacheBlockList.get(0), containerA, new ComponentEx[] { containerA, containerA1, compA1a });
		checkCacheBlock(env.cacheBlockList.get(1), compA1b, new ComponentEx[] { compA1b });
		checkCacheBlock(env.cacheBlockList.get(2), compA2b, new ComponentEx[] { compA2b });
		checkCacheBlock(env.cacheBlockList.get(3), containerA2, new ComponentEx[] { compA2a, containerA2 });
	}

	/**
	 * test changing component's cache mode.
	 */
	@Test
	public void testComponentCacheModeChanged() {
		containerA1.setCacheable(true);

		env.buildComponentCacheBlock();

		assertEquals(env.cacheBlockList.size(), 5);
		checkCacheBlock(env.cacheBlockList.get(0), containerA, new ComponentEx[] { containerA });
		checkCacheBlock(env.cacheBlockList.get(1), containerA1, new ComponentEx[] { containerA1, compA1a });
		checkCacheBlock(env.cacheBlockList.get(2), compA1b, new ComponentEx[] { compA1b });
		checkCacheBlock(env.cacheBlockList.get(3), containerA2, new ComponentEx[] { containerA2, compA2a });
		checkCacheBlock(env.cacheBlockList.get(4), compA2b, new ComponentEx[] { compA2b });

		containerA2.setCacheable(false);

		env.buildComponentCacheBlock();

		assertEquals(env.cacheBlockList.size(), 4);
		checkCacheBlock(env.cacheBlockList.get(0), containerA, new ComponentEx[] { containerA, containerA2, compA2a });
		checkCacheBlock(env.cacheBlockList.get(1), containerA1, new ComponentEx[] { containerA1, compA1a });
		checkCacheBlock(env.cacheBlockList.get(2), compA1b, new ComponentEx[] { compA1b });
		checkCacheBlock(env.cacheBlockList.get(3), compA2b, new ComponentEx[] { compA2b });
	}

	/**
	 * test changing top component's cache mode.
	 */
	@Test
	public void testTopComponentCacheModeChanged() {
		containerA.setCacheable(false);

		env.buildComponentCacheBlock();

		assertEquals(env.cacheBlockList.size(), 3);
		checkCacheBlock(env.cacheBlockList.get(0), compA1b, new ComponentEx[] { compA1b });
		checkCacheBlock(env.cacheBlockList.get(1), containerA2, new ComponentEx[] { containerA2, compA2a });
		checkCacheBlock(env.cacheBlockList.get(2), compA2b, new ComponentEx[] { compA2b });

		containerA.setCacheable(true);

		env.buildComponentCacheBlock();

		assertEquals(env.cacheBlockList.size(), 4);
		checkCacheBlock(env.cacheBlockList.get(0), containerA, new ComponentEx[] { containerA, containerA1, compA1a });
		checkCacheBlock(env.cacheBlockList.get(1), compA1b, new ComponentEx[] { compA1b });
		checkCacheBlock(env.cacheBlockList.get(2), containerA2, new ComponentEx[] { containerA2, compA2a });
		checkCacheBlock(env.cacheBlockList.get(3), compA2b, new ComponentEx[] { compA2b });
	}

}
