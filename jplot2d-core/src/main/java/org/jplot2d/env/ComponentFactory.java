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

import java.lang.reflect.Proxy;

import org.jplot2d.data.ArrayPair;
import org.jplot2d.data.XYGraph;
import org.jplot2d.element.Axis;
import org.jplot2d.element.AxisPosition;
import org.jplot2d.element.AxisTick;
import org.jplot2d.element.SubplotMargin;
import org.jplot2d.element.TextComponent;
import org.jplot2d.element.AxisRangeManager;
import org.jplot2d.element.AxisLockGroup;
import org.jplot2d.element.Component;
import org.jplot2d.element.Layer;
import org.jplot2d.element.Plot;
import org.jplot2d.element.Subplot;
import org.jplot2d.element.XYGraphPlotter;
import org.jplot2d.element.impl.AxisImpl;
import org.jplot2d.element.impl.AxisLockGroupImpl;
import org.jplot2d.element.impl.AxisTickEx;
import org.jplot2d.element.impl.ComponentEx;
import org.jplot2d.element.impl.LayerImpl;
import org.jplot2d.element.impl.PlotImpl;
import org.jplot2d.element.impl.SubplotImpl;
import org.jplot2d.element.impl.SubplotMarginEx;
import org.jplot2d.element.impl.TextComponentEx;
import org.jplot2d.element.impl.AxisRangeManagerImpl;
import org.jplot2d.element.impl.XYGraphPlotterImpl;

/**
 * A factory to produce all kind of plot components.
 * 
 * @author Jingjing Li
 * 
 */
public class ComponentFactory {

	private static ComponentFactory instance = new ComponentFactory(false);

	private static ComponentFactory threadSafeInstance = new ComponentFactory(
			true);

	public static ComponentFactory getInstance() {
		return instance;
	}

	public static ComponentFactory getThreadSafeInstance() {
		return threadSafeInstance;
	}

	private final boolean threadSafe;

	// TODO : parameterized
	// public static ComponentFactory getInstance(Profile profile) {
	// return instance;
	// }

	// FIXME: sub-element

	private ComponentFactory(boolean threadSafe) {
		this.threadSafe = threadSafe;
	}

	/**
	 * Assign a dummy environment for the given component/proxy pair
	 * 
	 * @return
	 */
	private void assignDummyEnv(ComponentEx comp, Component proxy) {
		DummyEnvironment env = (threadSafe) ? new ThreadSafeDummyEnvironment()
				: new DummyEnvironment();

		synchronized (Environment.getGlobalLock()) {
			env.begin();
			((ElementAddition) proxy).setEnvironment(env);
		}
		env.registerComponent(comp, proxy);
		env.end();
	}

	public Plot createPlot() {
		PlotImpl impl = new PlotImpl();
		ElementIH<Plot> ih = new ElementIH<Plot>(impl, Plot.class);
		Plot proxy = (Plot) Proxy.newProxyInstance(Plot.class.getClassLoader(),
				new Class[] { Plot.class, ElementAddition.class }, ih);

		assignDummyEnv(impl, proxy);
		return proxy;
	}

	public Subplot createSubplot() {
		SubplotImpl impl = new SubplotImpl();
		ElementIH<Subplot> ih = new ElementIH<Subplot>(impl, Subplot.class);
		Subplot proxy = (Subplot) Proxy.newProxyInstance(
				Subplot.class.getClassLoader(), new Class[] { Subplot.class,
						ElementAddition.class }, ih);

		SubplotMarginEx margin = impl.getMargin();
		ElementIH<SubplotMargin> marginIH = new ElementIH<SubplotMargin>(
				margin, SubplotMargin.class);
		SubplotMargin marginProxy = (SubplotMargin) Proxy.newProxyInstance(
				SubplotMargin.class.getClassLoader(), new Class[] {
						SubplotMargin.class, ElementAddition.class }, marginIH);

		DummyEnvironment env = (threadSafe) ? new ThreadSafeDummyEnvironment()
				: new DummyEnvironment();
		synchronized (Environment.getGlobalLock()) {
			((ElementAddition) proxy).setEnvironment(env);
			((ElementAddition) marginProxy).setEnvironment(env);
		}
		env.registerComponent(impl, proxy);
		env.registerElement(margin, marginProxy);

		return proxy;
	}

	public Layer createLayer(double[] xarray, double[] yarray) {
		return this.createLayer(new ArrayPair(xarray, yarray));
	}

	public Layer createLayer(ArrayPair xy) {
		return this.createLayer(new XYGraph(xy));
	}

	public Layer createLayer(XYGraph graph) {
		Layer layer = this.createLayer();
		XYGraphPlotter plotter = this.createXYGraphPlotter();
		plotter.setGraph(graph);
		layer.addGraphPlotter(plotter);
		return layer;
	}

	public Layer createLayer() {
		LayerImpl impl = new LayerImpl();
		ElementIH<Layer> ih = new ElementIH<Layer>(impl, Layer.class);
		Layer proxy = (Layer) Proxy.newProxyInstance(
				Layer.class.getClassLoader(), new Class[] { Layer.class,
						ElementAddition.class }, ih);

		assignDummyEnv(impl, proxy);
		return proxy;
	}

	public XYGraphPlotter createXYGraphPlotter() {
		XYGraphPlotterImpl impl = new XYGraphPlotterImpl();
		ElementIH<XYGraphPlotter> ih = new ElementIH<XYGraphPlotter>(impl,
				XYGraphPlotter.class);
		XYGraphPlotter proxy = (XYGraphPlotter) Proxy.newProxyInstance(
				XYGraphPlotter.class.getClassLoader(), new Class[] {
						XYGraphPlotter.class, ElementAddition.class }, ih);

		assignDummyEnv(impl, proxy);
		return proxy;
	}

	/**
	 * Create a AxisRangeManager, which contains a group.
	 * 
	 * @return
	 */
	public AxisRangeManager createAxisRangeManager() {
		AxisRangeManagerImpl va = new AxisRangeManagerImpl();
		AxisLockGroupImpl group = new AxisLockGroupImpl();
		va.setLockGroup(group);

		ElementIH<AxisRangeManager> vaIH = new ElementIH<AxisRangeManager>(va,
				AxisRangeManager.class);
		AxisRangeManager vaProxy = (AxisRangeManager) Proxy.newProxyInstance(
				AxisRangeManager.class.getClassLoader(), new Class[] {
						AxisRangeManager.class, ElementAddition.class }, vaIH);

		ElementIH<AxisLockGroup> groupIH = new ElementIH<AxisLockGroup>(group,
				AxisLockGroup.class);
		AxisLockGroup groupProxy = (AxisLockGroup) Proxy.newProxyInstance(
				AxisLockGroup.class.getClassLoader(), new Class[] {
						AxisLockGroup.class, ElementAddition.class }, groupIH);

		DummyEnvironment env = (threadSafe) ? new ThreadSafeDummyEnvironment()
				: new DummyEnvironment();

		synchronized (Environment.getGlobalLock()) {
			((ElementAddition) vaProxy).setEnvironment(env);
			((ElementAddition) groupProxy).setEnvironment(env);
		}
		env.registerElement(va, vaProxy);
		env.registerElement(group, groupProxy);

		return vaProxy;
	}

	/**
	 * Create an Axis. The default position is
	 * {@link AxisPosition#NEGATIVE_SIDE}
	 * 
	 * @return
	 */
	public Axis createAxis() {
		return createAxes(1)[0];
	}

	/**
	 * Create n Axes which share the same range manager. The position of the
	 * axis on index 0 is {@link AxisPosition#NEGATIVE_SIDE}, the position of
	 * the axis on index 1 is {@link AxisPosition#POSITIVE_SIDE}
	 * 
	 * @return
	 */
	public Axis[] createAxes(int n) {

		Axis[] result = new Axis[n];

		AxisRangeManagerImpl va = new AxisRangeManagerImpl();
		AxisLockGroupImpl group = new AxisLockGroupImpl();
		va.setLockGroup(group);

		ElementIH<AxisRangeManager> vaIH = new ElementIH<AxisRangeManager>(va,
				AxisRangeManager.class);
		AxisRangeManager vaProxy = (AxisRangeManager) Proxy.newProxyInstance(
				AxisRangeManager.class.getClassLoader(), new Class[] {
						AxisRangeManager.class, ElementAddition.class }, vaIH);

		ElementIH<AxisLockGroup> groupIH = new ElementIH<AxisLockGroup>(group,
				AxisLockGroup.class);
		AxisLockGroup groupProxy = (AxisLockGroup) Proxy.newProxyInstance(
				AxisLockGroup.class.getClassLoader(), new Class[] {
						AxisLockGroup.class, ElementAddition.class }, groupIH);

		DummyEnvironment env = (threadSafe) ? new ThreadSafeDummyEnvironment()
				: new DummyEnvironment();

		synchronized (Environment.getGlobalLock()) {
			((ElementAddition) vaProxy).setEnvironment(env);
			((ElementAddition) groupProxy).setEnvironment(env);
		}
		env.registerElement(va, vaProxy);
		env.registerElement(group, groupProxy);

		// add axis
		for (int i = 0; i < n; i++) {
			// this create tick and title inside
			AxisImpl axis = new AxisImpl();
			axis.setRangeManager(va);

			ElementIH<Axis> axisIH = new ElementIH<Axis>(axis, Axis.class);
			Axis axisProxy = (Axis) Proxy.newProxyInstance(
					Axis.class.getClassLoader(), new Class[] { Axis.class,
							ElementAddition.class }, axisIH);

			AxisTickEx tick = axis.getTick();
			ElementIH<AxisTick> tickIH = new ElementIH<AxisTick>(tick,
					AxisTick.class);
			AxisTick tickProxy = (AxisTick) Proxy.newProxyInstance(
					AxisTick.class.getClassLoader(), new Class[] {
							AxisTick.class, ElementAddition.class }, tickIH);

			TextComponentEx title = axis.getTitle();
			ElementIH<TextComponent> titleIH = new ElementIH<TextComponent>(
					title, TextComponent.class);
			TextComponent titleProxy = (TextComponent) Proxy.newProxyInstance(
					TextComponent.class.getClassLoader(), new Class[] {
							TextComponent.class, ElementAddition.class },
					titleIH);

			if (i % 2 == 1) {
				axis.setPosition(AxisPosition.POSITIVE_SIDE);
			}

			synchronized (Environment.getGlobalLock()) {
				((ElementAddition) axisProxy).setEnvironment(env);
				((ElementAddition) tickProxy).setEnvironment(env);
				((ElementAddition) titleProxy).setEnvironment(env);
			}
			env.registerComponent(axis, axisProxy);
			env.registerElement(tick, tickProxy);
			/*
			 * register the title as an element, the axis take the
			 * responsibility to draw the title
			 */
			env.registerElement(title, titleProxy);

			result[i] = axisProxy;
		}

		return result;
	}

	/**
	 * @param axis
	 *            the axis the group will add first
	 * @return
	 */
	public AxisLockGroup createAxisLockGroup() {
		AxisLockGroupImpl impl = new AxisLockGroupImpl();
		ElementIH<AxisLockGroup> ih = new ElementIH<AxisLockGroup>(impl,
				AxisLockGroup.class);
		AxisLockGroup proxy = (AxisLockGroup) Proxy.newProxyInstance(
				AxisLockGroup.class.getClassLoader(), new Class[] {
						AxisLockGroup.class, ElementAddition.class }, ih);

		DummyEnvironment env = (threadSafe) ? new ThreadSafeDummyEnvironment()
				: new DummyEnvironment();

		synchronized (Environment.getGlobalLock()) {
			env.begin();
			((ElementAddition) proxy).setEnvironment(env);
		}
		env.registerElement(impl, proxy);
		env.end();

		return proxy;
	}

}
