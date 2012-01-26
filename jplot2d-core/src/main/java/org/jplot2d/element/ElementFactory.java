/**
 * Copyright 2010, 2011 Jingjing Li.
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
package org.jplot2d.element;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.lang.reflect.Proxy;

import org.jplot2d.data.ArrayPair;
import org.jplot2d.data.XYGraph;
import org.jplot2d.element.impl.AxisImpl;
import org.jplot2d.element.impl.AxisRangeLockGroupImpl;
import org.jplot2d.element.impl.AxisTransformEx;
import org.jplot2d.element.impl.AxisTickManagerEx;
import org.jplot2d.element.impl.AxisTickManagerImpl;
import org.jplot2d.element.impl.AxisTitleEx;
import org.jplot2d.element.impl.HLineAnnotationImpl;
import org.jplot2d.element.impl.HStripAnnotationImpl;
import org.jplot2d.element.impl.LayerImpl;
import org.jplot2d.element.impl.LegendEx;
import org.jplot2d.element.impl.LegendItemEx;
import org.jplot2d.element.impl.PlotImpl;
import org.jplot2d.element.impl.PlotMarginEx;
import org.jplot2d.element.impl.AxisTransformImpl;
import org.jplot2d.element.impl.SymbolAnnotationImpl;
import org.jplot2d.element.impl.TitleImpl;
import org.jplot2d.element.impl.VLineAnnotationImpl;
import org.jplot2d.element.impl.VStripAnnotationImpl;
import org.jplot2d.element.impl.XYGraphPlotterImpl;
import org.jplot2d.env.DummyEnvironment;
import org.jplot2d.env.ElementAddition;
import org.jplot2d.env.ElementIH;
import org.jplot2d.env.Profile;
import org.jplot2d.util.Range;

/**
 * A factory to produce all kind of plot components.
 * 
 * @author Jingjing Li
 * 
 */
public class ElementFactory {

	private static ElementFactory instance = new ElementFactory(false, null);

	private static ElementFactory threadSafeInstance = new ElementFactory(true, null);

	/**
	 * Returns an instance of ComponentFactory. All component created by this factory are not
	 * thread-safe.
	 * 
	 * @return an instance of ComponentFactory
	 */
	public static ElementFactory getInstance() {
		return instance;
	}

	/**
	 * Returns an instance of ComponentFactory. All component created by this factory are
	 * thread-safe.
	 * 
	 * @return an instance of ComponentFactory
	 */
	public static ElementFactory getThreadSafeInstance() {
		return threadSafeInstance;
	}

	/**
	 * Returns an instance of ComponentFactory which is configured by profile. All component created
	 * by this factory are not thread-safe.
	 * 
	 * @param profile
	 * @return an instance of ComponentFactory
	 */
	public static ElementFactory getInstance(Profile profile) {
		return new ElementFactory(false, profile);
	}

	/**
	 * Returns an instance of ComponentFactory which is configured by profile. All component created
	 * by this factory are thread-safe.
	 * 
	 * @param profile
	 * @return an instance of ComponentFactory
	 */
	public static ElementFactory getThreadSafeInstance(Profile profile) {
		return new ElementFactory(true, profile);
	}

	private final boolean threadSafe;

	private final Profile profile;

	protected ElementFactory(boolean threadSafe, Profile profile) {
		this.threadSafe = threadSafe;
		this.profile = profile;
	}

	/**
	 * Apply profile to the given element
	 * 
	 * @param element
	 */
	protected final void applyProfile(Element element) {
		if (profile != null) {
			profile.applyTo(element);
		}
	}

	/**
	 * Create proxy for the given impl
	 * 
	 * @param <T>
	 *            the proxy type
	 * @param impl
	 *            the impl
	 * @param clazz
	 *            the class of proxy type
	 * @return the proxy
	 */
	@SuppressWarnings("unchecked")
	public static final <T extends Element> T proxy(T impl, Class<T> clazz) {
		ElementIH<T> ih = new ElementIH<T>(impl, clazz);
		return (T) Proxy.newProxyInstance(clazz.getClassLoader(), new Class[] { clazz,
				ElementAddition.class }, ih);
	}

	/**
	 * Create a plot with setting the default color, font, and margin. The default size mode is
	 * FillContainerSizeMode(1).
	 * 
	 * @return
	 */
	public Plot createPlot() {
		Plot plot = createSubplot();

		plot.setCacheable(true);
		plot.setColor(Color.BLACK);
		plot.setFont(new Font("Serif", Font.PLAIN, 12));
		plot.getMargin().setExtraTop(12);
		plot.getMargin().setExtraLeft(12);
		plot.getMargin().setExtraBottom(12);
		plot.getMargin().setExtraRight(12);

		return plot;
	}

	/**
	 * Create a plot which can be added to other plot later.
	 * 
	 * @return
	 */
	public Plot createSubplot() {
		PlotImpl subplot = new PlotImpl();
		applyProfile(subplot);
		Plot subplotProxy = proxy(subplot, Plot.class);

		LegendEx legend = subplot.getLegend();
		applyProfile(legend);
		Legend legendProxy = proxy(legend, Legend.class);

		PlotMarginEx margin = subplot.getMargin();
		applyProfile(margin);
		PlotMargin marginProxy = proxy(margin, PlotMargin.class);

		DummyEnvironment env = new DummyEnvironment(threadSafe);
		env.registerComponent(subplot, subplotProxy);
		env.registerComponent(legend, legendProxy);
		env.registerElement(margin, marginProxy);

		return subplotProxy;
	}

	/**
	 * Create a plot title.
	 * 
	 * @param text
	 *            the title text
	 * @return a plot title
	 */
	public Title createTitle(String text) {
		TitleImpl impl = new TitleImpl();
		if (text != null) {
			impl.setText(text);
		}

		applyProfile(impl);
		Title proxy = proxy(impl, Title.class);

		DummyEnvironment env = new DummyEnvironment(threadSafe);
		env.registerComponent(impl, proxy);
		return proxy;
	}

	/**
	 * Create an Axis. The default position is {@link AxisPosition#NEGATIVE_SIDE}
	 * 
	 * @return a axis
	 */
	public Axis createAxis() {
		return createAxes(1)[0];
	}

	/**
	 * Create n Axes which share the same tick manager. The position of the axis on index 0 is
	 * {@link AxisPosition#NEGATIVE_SIDE}, the position of the axis on index 1 is
	 * {@link AxisPosition#POSITIVE_SIDE}
	 * 
	 * @return axes in an array
	 */
	public Axis[] createAxes(int n) {

		AxisTickManager tm = createAxisTickManager();
		DummyEnvironment env = (DummyEnvironment) tm.getEnvironment();
		AxisTickManagerEx tme = (AxisTickManagerEx) ((ElementAddition) tm).getImpl();

		Axis[] result = new Axis[n];

		// add axis
		for (int i = 0; i < n; i++) {
			// this create tick and title inside
			AxisImpl axis = new AxisImpl();
			axis.setTickManager(tme);

			Axis axisProxy = proxy(axis, Axis.class);

			AxisTitleEx title = axis.getTitle();
			AxisTitle titleProxy = proxy(title, AxisTitle.class);

			if (i % 2 == 1) {
				axis.setPosition(AxisPosition.POSITIVE_SIDE);
			}

			env.registerComponent(axis, axisProxy);
			env.registerElement(title, titleProxy);

			result[i] = axisProxy;
		}

		return result;
	}

	/**
	 * Create an AxisTickManager, which contains an axis range manager.
	 * 
	 * @return an AxisTickManager
	 */
	public AxisTickManager createAxisTickManager() {
		AxisTransform rm = createAxisTransform();
		DummyEnvironment env = (DummyEnvironment) rm.getEnvironment();
		AxisTransformEx rme = (AxisTransformEx) ((ElementAddition) rm).getImpl();

		AxisTickManagerImpl tm = new AxisTickManagerImpl();
		applyProfile(tm);
		tm.setAxisTransform(rme);

		AxisTickManager vaProxy = proxy(tm, AxisTickManager.class);

		env.registerElement(tm, vaProxy);

		return vaProxy;
	}

	/**
	 * Create an AxisTransform, which contains an axis range lock group.
	 * 
	 * @return an AxisTransform
	 */
	public AxisTransform createAxisTransform() {
		AxisTransformImpl rm = new AxisTransformImpl();
		AxisRangeLockGroupImpl group = new AxisRangeLockGroupImpl();
		applyProfile(rm);
		applyProfile(group);
		rm.setLockGroup(group);

		AxisTransform vaProxy = proxy(rm, AxisTransform.class);
		AxisRangeLockGroup groupProxy = proxy(group, AxisRangeLockGroup.class);

		DummyEnvironment env = new DummyEnvironment(threadSafe);

		env.registerElement(rm, vaProxy);
		env.registerElement(group, groupProxy);

		return vaProxy;
	}

	/**
	 * Create an axis lock group.
	 * 
	 * @return an axis lock group
	 */
	public AxisRangeLockGroup createAxisRangeLockGroup() {
		AxisRangeLockGroupImpl impl = new AxisRangeLockGroupImpl();
		applyProfile(impl);
		AxisRangeLockGroup proxy = proxy(impl, AxisRangeLockGroup.class);

		DummyEnvironment env = new DummyEnvironment(threadSafe);
		env.registerElement(impl, proxy);

		return proxy;
	}

	public Layer createLayer(double[] xarray, double[] yarray) {
		return this.createLayer(new ArrayPair(xarray, yarray));
	}

	public Layer createLayer(double[] xarray, double[] yarray, String name) {
		return this.createLayer(new ArrayPair(xarray, yarray), name);
	}

	public Layer createLayer(ArrayPair xy) {
		return this.createLayer(new XYGraph(xy));
	}

	public Layer createLayer(ArrayPair xy, String name) {
		return this.createLayer(new XYGraph(xy), name);
	}

	public Layer createLayer(XYGraph graph) {
		return createLayer(graph, null);
	}

	public Layer createLayer(XYGraph graph, String name) {
		Layer layer = this.createLayer();
		XYGraphPlotter plotter = this.createXYGraphPlotter(graph, name);
		layer.addGraphPlotter(plotter);
		return layer;
	}

	/**
	 * Create a layer
	 * 
	 * @return a layer
	 */
	public Layer createLayer() {
		LayerImpl impl = new LayerImpl();
		applyProfile(impl);
		Layer proxy = proxy(impl, Layer.class);

		DummyEnvironment env = new DummyEnvironment(threadSafe);
		env.registerComponent(impl, proxy);
		return proxy;
	}

	public XYGraphPlotter createXYGraphPlotter(double[] xarray, double[] yarray) {
		return createXYGraphPlotter(new ArrayPair(xarray, yarray));
	}

	public XYGraphPlotter createXYGraphPlotter(double[] xarray, double[] yarray, String name) {
		return createXYGraphPlotter(new ArrayPair(xarray, yarray), name);
	}

	public XYGraphPlotter createXYGraphPlotter(double[] xarray, double[] yarray,
			double[] xErrorLow, double[] xErrorHigh, double[] yErrorLow, double[] yErrorHigh) {
		return createXYGraphPlotter(xarray, yarray, xErrorLow, xErrorHigh, yErrorLow, yErrorHigh,
				null);
	}

	public XYGraphPlotter createXYGraphPlotter(double[] xarray, double[] yarray,
			double[] xErrorLow, double[] xErrorHigh, double[] yErrorLow, double[] yErrorHigh,
			String name) {
		ArrayPair errorX = null;
		if (xErrorLow != null && xErrorHigh != null) {
			errorX = new ArrayPair(xErrorLow, xErrorHigh);
		}
		ArrayPair errorY = null;
		if (yErrorLow != null && yErrorHigh != null) {
			errorY = new ArrayPair(yErrorLow, yErrorHigh);
		}
		return createXYGraphPlotter(new ArrayPair(xarray, yarray), errorX, errorY, name);
	}

	public XYGraphPlotter createXYGraphPlotter(ArrayPair xy) {
		return createXYGraphPlotter(new XYGraph(xy));
	}

	public XYGraphPlotter createXYGraphPlotter(ArrayPair xy, String name) {
		return createXYGraphPlotter(new XYGraph(xy), name);
	}

	public XYGraphPlotter createXYGraphPlotter(ArrayPair xy, ArrayPair errorX, ArrayPair errorY) {
		return createXYGraphPlotter(new XYGraph(xy, errorX, errorY));
	}

	public XYGraphPlotter createXYGraphPlotter(ArrayPair xy, ArrayPair errorX, ArrayPair errorY,
			String name) {
		return createXYGraphPlotter(new XYGraph(xy, errorX, errorY), name);
	}

	public XYGraphPlotter createXYGraphPlotter(XYGraph graph) {
		return createXYGraphPlotter(graph, null);
	}

	/**
	 * Create a XYGraphPlotter with the given graph and name
	 * 
	 * @param graph
	 *            the graph to be plotted
	 * @param name
	 *            the name
	 * @return a XYGraphPlotter
	 */
	public XYGraphPlotter createXYGraphPlotter(XYGraph graph, String name) {
		XYGraphPlotterImpl gp = new XYGraphPlotterImpl();
		gp.setGraph(graph);
		applyProfile(gp);
		XYGraphPlotter gpProxy = proxy(gp, XYGraphPlotter.class);

		LegendItemEx li = gp.getLegendItem();
		if (name != null) {
			li.setText(name);
		}
		LegendItem liProxy = proxy(li, LegendItem.class);

		DummyEnvironment env = new DummyEnvironment(threadSafe);
		env.registerComponent(gp, gpProxy);
		env.registerElement(li, liProxy);
		return gpProxy;
	}

	public SymbolAnnotation createSymbolAnnotation(double x, double y) {
		SymbolAnnotationImpl annotation = new SymbolAnnotationImpl();
		annotation.setValuePoint(x, y);
		applyProfile(annotation);
		SymbolAnnotation annotationProxy = proxy(annotation, SymbolAnnotation.class);

		DummyEnvironment env = new DummyEnvironment(threadSafe);
		env.registerComponent(annotation, annotationProxy);
		return annotationProxy;
	}

	public SymbolAnnotation createSymbolAnnotation(double x, double y, String text) {
		SymbolAnnotationImpl annotation = new SymbolAnnotationImpl();
		annotation.setValuePoint(x, y);
		annotation.setText(text);
		applyProfile(annotation);
		SymbolAnnotation annotationProxy = proxy(annotation, SymbolAnnotation.class);

		DummyEnvironment env = new DummyEnvironment(threadSafe);
		env.registerComponent(annotation, annotationProxy);
		return annotationProxy;
	}

	public SymbolAnnotation createSymbolAnnotation(double x, double y, double angle, String text) {
		SymbolAnnotationImpl annotation = new SymbolAnnotationImpl();
		annotation.setValuePoint(x, y);
		annotation.setAngle(angle);
		annotation.setText(text);
		applyProfile(annotation);
		SymbolAnnotation annotationProxy = proxy(annotation, SymbolAnnotation.class);

		DummyEnvironment env = new DummyEnvironment(threadSafe);
		env.registerComponent(annotation, annotationProxy);
		return annotationProxy;
	}

	public HLineAnnotation createHLineAnnotation(double y) {
		HLineAnnotationImpl annotation = new HLineAnnotationImpl();
		annotation.setValue(y);
		applyProfile(annotation);
		HLineAnnotation annotationProxy = proxy(annotation, HLineAnnotation.class);

		DummyEnvironment env = new DummyEnvironment(threadSafe);
		env.registerComponent(annotation, annotationProxy);
		return annotationProxy;
	}

	public VLineAnnotation createVLineAnnotation(double y) {
		VLineAnnotationImpl annotation = new VLineAnnotationImpl();
		annotation.setValue(y);
		applyProfile(annotation);
		VLineAnnotation annotationProxy = proxy(annotation, VLineAnnotation.class);

		DummyEnvironment env = new DummyEnvironment(threadSafe);
		env.registerComponent(annotation, annotationProxy);
		return annotationProxy;
	}

	public HStripAnnotation createHStripAnnotation(double start, double end) {
		HStripAnnotationImpl annotation = new HStripAnnotationImpl();
		annotation.setValueRange(new Range.Double(start, end));
		applyProfile(annotation);
		HStripAnnotation annotationProxy = proxy(annotation, HStripAnnotation.class);

		DummyEnvironment env = new DummyEnvironment(threadSafe);
		env.registerComponent(annotation, annotationProxy);
		return annotationProxy;
	}

	public VStripAnnotation createVStripAnnotation(double start, double end) {
		VStripAnnotationImpl annotation = new VStripAnnotationImpl();
		annotation.setValueRange(new Range.Double(start, end));
		applyProfile(annotation);
		VStripAnnotation annotationProxy = proxy(annotation, VStripAnnotation.class);

		DummyEnvironment env = new DummyEnvironment(threadSafe);
		env.registerComponent(annotation, annotationProxy);
		return annotationProxy;
	}

	public BasicStroke createStroke(float width) {
		return new BasicStroke(width, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, null, 0f);
	}

	public BasicStroke createStroke(float width, float[] dash) {
		return new BasicStroke(width, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, dash, 0f);
	}
}
