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

import org.jplot2d.element.AxisGroup;
import org.jplot2d.element.Component;
import org.jplot2d.element.Layer;
import org.jplot2d.element.MainAxis;
import org.jplot2d.element.Plot;
import org.jplot2d.element.Subplot;
import org.jplot2d.element.impl.AxisGroupEx;
import org.jplot2d.element.impl.AxisGroupImpl;
import org.jplot2d.element.impl.ComponentEx;
import org.jplot2d.element.impl.LayerImpl;
import org.jplot2d.element.impl.MainAxisImpl;
import org.jplot2d.element.impl.PlotImpl;
import org.jplot2d.element.impl.SubplotImpl;
import org.jplot2d.layout.StackLayoutDirector;

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
		impl.setLayoutDirector(new StackLayoutDirector(impl));
		ElementIH<Plot> ih = new ElementIH<Plot>(impl, Plot.class);
		Plot proxy = (Plot) Proxy.newProxyInstance(Plot.class.getClassLoader(),
				new Class[] { Plot.class, ElementAddition.class }, ih);

		assignDummyEnv(impl, proxy);
		return proxy;
	}

	public Subplot createSubplot() {
		SubplotImpl impl = new SubplotImpl();
		ElementIH<Subplot> ih = new ElementIH<Subplot>(impl, Subplot.class);
		Subplot proxy = (Subplot) Proxy.newProxyInstance(Subplot.class
				.getClassLoader(), new Class[] { Subplot.class,
				ElementAddition.class }, ih);

		assignDummyEnv(impl, proxy);
		return proxy;
	}

	public Layer createLayer() {
		LayerImpl impl = new LayerImpl();
		ElementIH<Layer> ih = new ElementIH<Layer>(impl, Layer.class);
		Layer proxy = (Layer) Proxy.newProxyInstance(Layer.class
				.getClassLoader(), new Class[] { Layer.class,
				ElementAddition.class }, ih);

		assignDummyEnv(impl, proxy);
		return proxy;
	}

	public MainAxis createMainAxis() {
		MainAxisImpl axis = new MainAxisImpl();
		ElementIH<MainAxis> axisIH = new ElementIH<MainAxis>(axis,
				MainAxis.class);
		MainAxis axisProxy = (MainAxis) Proxy.newProxyInstance(MainAxis.class
				.getClassLoader(), new Class[] { MainAxis.class,
				ElementAddition.class }, axisIH);

		AxisGroupEx group = axis.getGroup();
		ElementIH<AxisGroup> groupIH = new ElementIH<AxisGroup>(
				axis.getGroup(), AxisGroup.class);
		AxisGroup groupProxy = (AxisGroup) Proxy.newProxyInstance(
				MainAxis.class.getClassLoader(), new Class[] { AxisGroup.class,
						ElementAddition.class }, groupIH);

		DummyEnvironment env = (threadSafe) ? new ThreadSafeDummyEnvironment()
				: new DummyEnvironment();

		((ElementAddition) axisProxy).setEnvironment(env);
		((ElementAddition) groupProxy).setEnvironment(env);
		env.registerComponent(axis, axisProxy);
		env.registerElement(group, groupProxy);

		// FIXME: register axis tick

		return axisProxy;
	}

	/**
	 * @param axis
	 *            the axis the group will add first
	 * @return
	 */
	public AxisGroup createAxisGroup(Environment env) {
		AxisGroupImpl impl = new AxisGroupImpl();
		ElementIH<AxisGroup> ih = new ElementIH<AxisGroup>(impl,
				AxisGroup.class);
		AxisGroup proxy = (AxisGroup) Proxy.newProxyInstance(AxisGroup.class
				.getClassLoader(), new Class[] { AxisGroup.class,
				ElementAddition.class }, ih);

		env.begin();
		((ElementAddition) proxy).setEnvironment(env);
		env.registerElement(impl, proxy);
		env.end();

		return proxy;
	}

}
