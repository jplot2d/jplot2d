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
import java.util.HashMap;
import java.util.Map;

import org.jplot2d.element.Component;
import org.jplot2d.element.Element;
import org.jplot2d.element.Plot;
import org.jplot2d.element.PlotImpl;
import org.jplot2d.element.SubPlot;
import org.jplot2d.element.SubPlotImpl;
import org.jplot2d.layout.GridLayoutDirector;

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
	private void assignDummyEnv(Component comp, Map<Element, Element> proxyMap) {
		Environment env = (threadSafe) ? new ThreadSafeDummyEnvironment()
				: new DummyEnvironment();

		synchronized (Environment.getGlobalLock()) {
			((ElementEx) proxyMap.get(comp)).setEnvironment(env);
			env.begin();
		}
		env.componentAdded(comp, proxyMap);
		env.end();
	}

	public Plot createPlot() {
		Plot impl = new PlotImpl();
		impl.setLayoutDirector(new GridLayoutDirector());
		ElementIH<Plot> ih = new ElementIH<Plot>(impl, Plot.class);
		Plot proxy = (Plot) Proxy.newProxyInstance(Plot.class.getClassLoader(),
				new Class[] { Plot.class, ElementEx.class }, ih);

		Map<Element, Element> proxyMap = new HashMap<Element, Element>();
		proxyMap.put(impl, proxy);
		assignDummyEnv(impl, proxyMap);
		return proxy;
	}

	public SubPlot createSubPlot() {
		SubPlot impl = new SubPlotImpl();
		ElementIH<SubPlot> ih = new ElementIH<SubPlot>(impl, SubPlot.class);
		SubPlot proxy = (SubPlot) Proxy.newProxyInstance(Plot.class
				.getClassLoader(),
				new Class[] { SubPlot.class, ElementEx.class }, ih);

		Map<Element, Element> proxyMap = new HashMap<Element, Element>();
		proxyMap.put(impl, proxy);
		assignDummyEnv(impl, proxyMap);
		return proxy;
	}

}
