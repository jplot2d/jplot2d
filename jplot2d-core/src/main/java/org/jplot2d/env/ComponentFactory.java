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

import org.jplot2d.element.Element;
import org.jplot2d.element.Layer;
import org.jplot2d.element.MainAxis;
import org.jplot2d.element.Plot;
import org.jplot2d.element.Subplot;
import org.jplot2d.element.impl.LayerImpl;
import org.jplot2d.element.impl.MainAxisImpl;
import org.jplot2d.element.impl.PlotEx;
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
	 * @return
	 */
	private void assignDummyEnv(Element comp, Element proxy) {
		DummyEnvironment env = (threadSafe) ? new ThreadSafeDummyEnvironment()
				: new DummyEnvironment();

		synchronized (Environment.getGlobalLock()) {
			env.begin();
			((ElementAddition) proxy).setEnvironment(env);
		}
		env.registerElement(comp, proxy);
		env.end();
	}

	public Plot createPlot() {
		PlotEx impl = new PlotImpl();
		impl.setLayoutDirector(new StackLayoutDirector(impl));
		ElementIH<Plot> ih = new ElementIH<Plot>(impl, Plot.class);
		Plot proxy = (Plot) Proxy.newProxyInstance(Plot.class.getClassLoader(),
				new Class[] { Plot.class, ElementAddition.class }, ih);

		assignDummyEnv(impl, proxy);
		return proxy;
	}

	public Subplot createSubplot() {
		Subplot impl = new SubplotImpl();
		ElementIH<Subplot> ih = new ElementIH<Subplot>(impl, Subplot.class);
		Subplot proxy = (Subplot) Proxy.newProxyInstance(Subplot.class
				.getClassLoader(), new Class[] { Subplot.class,
				ElementAddition.class }, ih);

		assignDummyEnv(impl, proxy);
		return proxy;
	}

	public Layer createLayer() {
		Layer impl = new LayerImpl();
		ElementIH<Layer> ih = new ElementIH<Layer>(impl, Layer.class);
		Layer proxy = (Layer) Proxy.newProxyInstance(Layer.class
				.getClassLoader(), new Class[] { Layer.class,
				ElementAddition.class }, ih);

		assignDummyEnv(impl, proxy);
		return proxy;
	}

	public MainAxis createMainAxis() {
		MainAxis impl = new MainAxisImpl();
		ElementIH<MainAxis> ih = new ElementIH<MainAxis>(impl, MainAxis.class);
		MainAxis proxy = (MainAxis) Proxy.newProxyInstance(MainAxis.class
				.getClassLoader(), new Class[] { MainAxis.class,
				ElementAddition.class }, ih);

		assignDummyEnv(impl, proxy);
		return proxy;
	}

}
