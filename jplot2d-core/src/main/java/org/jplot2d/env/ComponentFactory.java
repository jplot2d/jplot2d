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
import org.jplot2d.data.XYData;
import org.jplot2d.element.Axis;
import org.jplot2d.element.AxisPosition;
import org.jplot2d.element.AxisTick;
import org.jplot2d.element.TextComponent;
import org.jplot2d.element.ViewportAxis;
import org.jplot2d.element.AxisLockGroup;
import org.jplot2d.element.Component;
import org.jplot2d.element.Layer;
import org.jplot2d.element.Plot;
import org.jplot2d.element.Subplot;
import org.jplot2d.element.XYDataPlot;
import org.jplot2d.element.impl.AxisImpl;
import org.jplot2d.element.impl.AxisLockGroupEx;
import org.jplot2d.element.impl.AxisLockGroupImpl;
import org.jplot2d.element.impl.AxisTickEx;
import org.jplot2d.element.impl.ComponentEx;
import org.jplot2d.element.impl.LayerImpl;
import org.jplot2d.element.impl.PlotImpl;
import org.jplot2d.element.impl.SubplotImpl;
import org.jplot2d.element.impl.TextComponentEx;
import org.jplot2d.element.impl.ViewportAxisImpl;
import org.jplot2d.element.impl.XYDataPlotImpl;
import org.jplot2d.layout.SimpleLayoutDirector;

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
		impl.setLayoutDirector(new SimpleLayoutDirector());
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

		assignDummyEnv(impl, proxy);
		return proxy;
	}

	public Layer createLayer(double[] xarray, double[] yarray) {
		return this.createLayer(new ArrayPair(xarray, yarray));
	}

	public Layer createLayer(ArrayPair xy) {
		return this.createLayer(new XYData(xy));
	}

	public Layer createLayer(XYData data) {
		Layer layer = this.createLayer();
		XYDataPlot plotter = this.createXYDataPlotter();
		plotter.setData(data);
		layer.addDataPlotter(plotter);
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

	public XYDataPlot createXYDataPlotter() {
		XYDataPlotImpl impl = new XYDataPlotImpl();
		ElementIH<XYDataPlot> ih = new ElementIH<XYDataPlot>(impl,
				XYDataPlot.class);
		XYDataPlot proxy = (XYDataPlot) Proxy.newProxyInstance(
				XYDataPlot.class.getClassLoader(), new Class[] {
						XYDataPlot.class, ElementAddition.class }, ih);

		assignDummyEnv(impl, proxy);
		return proxy;
	}

	/**
	 * Create a ViewportAxis, which contains an Axes. The position of the axis
	 * is {@link AxisPosition#NEGATIVE_SIDE}
	 * 
	 * @return
	 */
	public ViewportAxis createViewportAxis() {
		return createViewportAxis(1);
	}

	/**
	 * Create a ViewportAxis, which contains n Axes. The position of the 1st
	 * axis is {@link AxisPosition#NEGATIVE_SIDE}, the position of 2nd axis is
	 * {@link AxisPosition#POSITIVE_SIDE}
	 * 
	 * @return
	 */
	public ViewportAxis createViewportAxis(int n) {
		ViewportAxisImpl va = new ViewportAxisImpl();
		ElementIH<ViewportAxis> axisIH = new ElementIH<ViewportAxis>(va,
				ViewportAxis.class);
		ViewportAxis vaProxy = (ViewportAxis) Proxy.newProxyInstance(
				ViewportAxis.class.getClassLoader(), new Class[] {
						ViewportAxis.class, ElementAddition.class }, axisIH);

		AxisLockGroupEx group = va.getLockGroup();
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
		env.registerComponent(va, vaProxy);
		env.registerElement(group, groupProxy);

		// add axis
		for (int i = 0; i < n; i++) {
			Axis axis = createAxis();
			if (i % 2 == 1) {
				axis.setPosition(AxisPosition.POSITIVE_SIDE);
			}
			vaProxy.addAxis(axis);
		}

		return vaProxy;
	}

	/**
	 * Create an Axis. The default position is
	 * {@link AxisPosition#NEGATIVE_SIDE}
	 * 
	 * @return
	 */
	public Axis createAxis() {
		// this create tick and title inside
		AxisImpl axis = new AxisImpl();

		ElementIH<Axis> axisIH = new ElementIH<Axis>(axis, Axis.class);
		Axis axisProxy = (Axis) Proxy.newProxyInstance(
				Axis.class.getClassLoader(), new Class[] { Axis.class,
						ElementAddition.class }, axisIH);

		AxisTickEx tick = axis.getTick();
		ElementIH<AxisTick> tickIH = new ElementIH<AxisTick>(tick,
				AxisTick.class);
		AxisTick tickProxy = (AxisTick) Proxy.newProxyInstance(
				AxisTick.class.getClassLoader(), new Class[] { AxisTick.class,
						ElementAddition.class }, tickIH);

		TextComponentEx title = axis.getTitle();
		ElementIH<TextComponent> titleIH = new ElementIH<TextComponent>(title,
				TextComponent.class);
		TextComponent titleProxy = (TextComponent) Proxy.newProxyInstance(
				TextComponent.class.getClassLoader(), new Class[] {
						TextComponent.class, ElementAddition.class }, titleIH);

		DummyEnvironment env = (threadSafe) ? new ThreadSafeDummyEnvironment()
				: new DummyEnvironment();

		synchronized (Environment.getGlobalLock()) {
			((ElementAddition) axisProxy).setEnvironment(env);
			((ElementAddition) tickProxy).setEnvironment(env);
			((ElementAddition) titleProxy).setEnvironment(env);
		}
		env.registerComponent(axis, axisProxy);
		env.registerElement(tick, tickProxy);
		env.registerElement(title, titleProxy);

		return axisProxy;
	}

	/**
	 * @param axis
	 *            the axis the group will add first
	 * @return
	 */
	public AxisLockGroup createAxisLockGroup(Environment env) {
		AxisLockGroupImpl impl = new AxisLockGroupImpl();
		ElementIH<AxisLockGroup> ih = new ElementIH<AxisLockGroup>(impl,
				AxisLockGroup.class);
		AxisLockGroup proxy = (AxisLockGroup) Proxy.newProxyInstance(
				AxisLockGroup.class.getClassLoader(), new Class[] {
						AxisLockGroup.class, ElementAddition.class }, ih);

		env.begin();
		((ElementAddition) proxy).setEnvironment(env);
		env.registerElement(impl, proxy);
		env.end();

		return proxy;
	}

}
