/**
 * Copyright 2010-2013 Jingjing Li.
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
import java.awt.Dimension;
import java.awt.Font;
import java.lang.reflect.Proxy;

import org.jplot2d.data.ArrayPair;
import org.jplot2d.data.ByteDataBuffer;
import org.jplot2d.data.FloatDataBuffer;
import org.jplot2d.data.ImageDataBuffer;
import org.jplot2d.data.MultiBandImageData;
import org.jplot2d.data.SingleBandImageData;
import org.jplot2d.data.XYGraphData;
import org.jplot2d.element.impl.AxisImpl;
import org.jplot2d.element.impl.AxisRangeLockGroupImpl;
import org.jplot2d.element.impl.AxisTransformEx;
import org.jplot2d.element.impl.AxisTickManagerEx;
import org.jplot2d.element.impl.AxisTickManagerImpl;
import org.jplot2d.element.impl.AxisTitleEx;
import org.jplot2d.element.impl.HLineAnnotationImpl;
import org.jplot2d.element.impl.HStripAnnotationImpl;
import org.jplot2d.element.impl.ImageMappingEx;
import org.jplot2d.element.impl.ImageMappingImpl;
import org.jplot2d.element.impl.LayerImpl;
import org.jplot2d.element.impl.LegendEx;
import org.jplot2d.element.impl.LegendItemEx;
import org.jplot2d.element.impl.ImageGraphImpl;
import org.jplot2d.element.impl.PlotImpl;
import org.jplot2d.element.impl.PlotMarginEx;
import org.jplot2d.element.impl.AxisTransformImpl;
import org.jplot2d.element.impl.RGBImageGraphImpl;
import org.jplot2d.element.impl.RGBImageMappingEx;
import org.jplot2d.element.impl.RGBImageMappingImpl;
import org.jplot2d.element.impl.RectangleAnnotationImpl;
import org.jplot2d.element.impl.SymbolAnnotationImpl;
import org.jplot2d.element.impl.TitleImpl;
import org.jplot2d.element.impl.VLineAnnotationImpl;
import org.jplot2d.element.impl.VStripAnnotationImpl;
import org.jplot2d.element.impl.XYGraphImpl;
import org.jplot2d.env.DummyEnvironment;
import org.jplot2d.env.ElementAddition;
import org.jplot2d.env.ElementIH;
import org.jplot2d.env.Profile;
import org.jplot2d.sizing.FillContainerSizeMode;
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
	 * Returns an instance of ComponentFactory. All component created by this factory are not thread-safe.
	 * 
	 * @return an instance of ComponentFactory
	 */
	public static ElementFactory getInstance() {
		return instance;
	}

	/**
	 * Returns an instance of ComponentFactory. All component created by this factory are thread-safe.
	 * 
	 * @return an instance of ComponentFactory
	 */
	public static ElementFactory getThreadSafeInstance() {
		return threadSafeInstance;
	}

	/**
	 * Returns an instance of ComponentFactory which is configured by profile. All component created by this factory are
	 * not thread-safe.
	 * 
	 * @param profile
	 * @return an instance of ComponentFactory
	 */
	public static ElementFactory getInstance(Profile profile) {
		return new ElementFactory(false, profile);
	}

	/**
	 * Returns an instance of ComponentFactory which is configured by profile. All component created by this factory are
	 * thread-safe.
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
		return (T) Proxy.newProxyInstance(clazz.getClassLoader(), new Class[] { clazz, ElementAddition.class }, ih);
	}

	/**
	 * Create a plot with the initial values
	 * <ul>
	 * <li>cacheable: true</li>
	 * <li>color: BLACK</li>
	 * <li>font: Serif PLAIN 12</li>
	 * <li>margin: extra 12 on left, top, right, bottom</li>
	 * <li>sizeMode: FillContainerSizeMode(1)</li>
	 * <li>containerSize: 640x480</li>
	 * <ul>
	 * 
	 * @return
	 */
	public Plot createPlot() {

		PlotImpl plot = new PlotImpl();

		plot.setCacheable(true);
		plot.setColor(Color.BLACK);
		plot.setFont(new Font("Serif", Font.PLAIN, 12));
		plot.getMargin().setExtraTop(12);
		plot.getMargin().setExtraLeft(12);
		plot.getMargin().setExtraBottom(12);
		plot.getMargin().setExtraRight(12);
		plot.setSizeMode(new FillContainerSizeMode(1));
		plot.setContainerSize(new Dimension(640, 480));

		applyProfile(plot);
		Plot plotProxy = proxy(plot, Plot.class);

		LegendEx legend = plot.getLegend();
		applyProfile(legend);
		Legend legendProxy = proxy(legend, Legend.class);

		PlotMarginEx margin = plot.getMargin();
		applyProfile(margin);
		PlotMargin marginProxy = proxy(margin, PlotMargin.class);

		DummyEnvironment env = new DummyEnvironment(threadSafe);
		env.registerElement(plot, plotProxy);
		env.registerElement(legend, legendProxy);
		env.registerElement(margin, marginProxy);

		return plotProxy;
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
		env.registerElement(subplot, subplotProxy);
		env.registerElement(legend, legendProxy);
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
		env.registerElement(impl, proxy);
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
	 * {@link AxisPosition#NEGATIVE_SIDE}, the position of the axis on index 1 is {@link AxisPosition#POSITIVE_SIDE}.
	 * All the created axes must be added to a plot by {@link Plot#addXAxes(Axis[])} or {@link Plot#addYAxes(Axis[])}
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

			env.registerElement(axis, axisProxy);
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

	public Layer createLayer(XYGraph graph) {
		Layer layer = this.createLayer();
		layer.addGraph(graph);
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
		env.registerElement(impl, proxy);
		return proxy;
	}

	public XYGraph createXYGraph(double[] xarray, double[] yarray) {
		return createXYGraph(new ArrayPair(xarray, yarray));
	}

	public XYGraph createXYGraph(double[] xarray, double[] yarray, String name) {
		return createXYGraph(new ArrayPair(xarray, yarray), name);
	}

	public XYGraph createXYGraph(double[] xarray, double[] yarray, double[] xErrorLow, double[] xErrorHigh,
			double[] yErrorLow, double[] yErrorHigh) {
		return createXYGraph(xarray, yarray, xErrorLow, xErrorHigh, yErrorLow, yErrorHigh, null);
	}

	public XYGraph createXYGraph(double[] xarray, double[] yarray, double[] xErrorLow, double[] xErrorHigh,
			double[] yErrorLow, double[] yErrorHigh, String name) {
		ArrayPair errorX = null;
		if (xErrorLow != null && xErrorHigh != null) {
			errorX = new ArrayPair(xErrorLow, xErrorHigh);
		}
		ArrayPair errorY = null;
		if (yErrorLow != null && yErrorHigh != null) {
			errorY = new ArrayPair(yErrorLow, yErrorHigh);
		}
		return createXYGraph(new ArrayPair(xarray, yarray), errorX, errorY, name);
	}

	public XYGraph createXYGraph(ArrayPair xy) {
		return createXYGraph(new XYGraphData(xy));
	}

	public XYGraph createXYGraph(ArrayPair xy, String name) {
		return createXYGraph(new XYGraphData(xy), name);
	}

	public XYGraph createXYGraph(ArrayPair xy, ArrayPair errorX, ArrayPair errorY) {
		return createXYGraph(new XYGraphData(xy, errorX, errorY));
	}

	public XYGraph createXYGraph(ArrayPair xy, ArrayPair errorX, ArrayPair errorY, String name) {
		return createXYGraph(new XYGraphData(xy, errorX, errorY), name);
	}

	public XYGraph createXYGraph(XYGraphData data) {
		return createXYGraph(data, null);
	}

	/**
	 * Create a XYGraph with the given graph and name
	 * 
	 * @param data
	 *            the graph to be plotted
	 * @param name
	 *            the name
	 * @return a XYGraph
	 */
	public XYGraph createXYGraph(XYGraphData data, String name) {
		XYGraphImpl graph = new XYGraphImpl();
		graph.setData(data);
		graph.setName(name);
		applyProfile(graph);
		XYGraph gpProxy = proxy(graph, XYGraph.class);

		LegendItemEx li = graph.getLegendItem();
		LegendItem liProxy = proxy(li, LegendItem.class);

		DummyEnvironment env = new DummyEnvironment(threadSafe);
		env.registerElement(graph, gpProxy);
		env.registerElement(li, liProxy);
		return gpProxy;
	}

	public SymbolAnnotation createSymbolAnnotation(double x, double y) {
		SymbolAnnotationImpl annotation = new SymbolAnnotationImpl();
		annotation.setValuePoint(x, y);
		applyProfile(annotation);
		SymbolAnnotation annotationProxy = proxy(annotation, SymbolAnnotation.class);

		DummyEnvironment env = new DummyEnvironment(threadSafe);
		env.registerElement(annotation, annotationProxy);
		return annotationProxy;
	}

	public SymbolAnnotation createSymbolAnnotation(double x, double y, String text) {
		SymbolAnnotationImpl annotation = new SymbolAnnotationImpl();
		annotation.setValuePoint(x, y);
		annotation.setText(text);
		applyProfile(annotation);
		SymbolAnnotation annotationProxy = proxy(annotation, SymbolAnnotation.class);

		DummyEnvironment env = new DummyEnvironment(threadSafe);
		env.registerElement(annotation, annotationProxy);
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
		env.registerElement(annotation, annotationProxy);
		return annotationProxy;
	}

	/**
	 * Create a horizontal line annotation with the given y value in world coordinate system
	 * 
	 * @param y
	 *            the y value in world coordinate system
	 * @return a horizontal line annotation
	 */
	public HLineAnnotation createHLineAnnotation(double y) {
		HLineAnnotationImpl annotation = new HLineAnnotationImpl();
		annotation.setValue(y);
		applyProfile(annotation);
		HLineAnnotation annotationProxy = proxy(annotation, HLineAnnotation.class);

		DummyEnvironment env = new DummyEnvironment(threadSafe);
		env.registerElement(annotation, annotationProxy);
		return annotationProxy;
	}

	/**
	 * Create a vertical line annotation with the given y value in world coordinate system
	 * 
	 * @param x
	 *            the y value in world coordinate system
	 * @return a horizontal line annotation
	 */
	public VLineAnnotation createVLineAnnotation(double x) {
		VLineAnnotationImpl annotation = new VLineAnnotationImpl();
		annotation.setValue(x);
		applyProfile(annotation);
		VLineAnnotation annotationProxy = proxy(annotation, VLineAnnotation.class);

		DummyEnvironment env = new DummyEnvironment(threadSafe);
		env.registerElement(annotation, annotationProxy);
		return annotationProxy;
	}

	/**
	 * Create a horizontal strip annotation with the given y value range in world coordinate system
	 * 
	 * @param start
	 *            the y start value in world coordinate system
	 * @param end
	 *            the y end value in world coordinate system
	 * @return a horizontal strip annotation
	 */
	public HStripAnnotation createHStripAnnotation(double start, double end) {
		HStripAnnotationImpl annotation = new HStripAnnotationImpl();
		annotation.setValueRange(new Range.Double(start, end));
		applyProfile(annotation);
		HStripAnnotation annotationProxy = proxy(annotation, HStripAnnotation.class);

		DummyEnvironment env = new DummyEnvironment(threadSafe);
		env.registerElement(annotation, annotationProxy);
		return annotationProxy;
	}

	/**
	 * Create a vertical strip annotation with the given x value range in world coordinate system.
	 * 
	 * @param start
	 *            the x start value in world coordinate system
	 * @param end
	 *            the x end value in world coordinate system
	 * @return a vertical strip annotation
	 */
	public VStripAnnotation createVStripAnnotation(double start, double end) {
		VStripAnnotationImpl annotation = new VStripAnnotationImpl();
		annotation.setValueRange(new Range.Double(start, end));
		applyProfile(annotation);
		VStripAnnotation annotationProxy = proxy(annotation, VStripAnnotation.class);

		DummyEnvironment env = new DummyEnvironment(threadSafe);
		env.registerElement(annotation, annotationProxy);
		return annotationProxy;
	}

	/**
	 * Create a rectangle annotation with the given x and y value range in world coordinate system.
	 * 
	 * @param x1
	 *            the x start value in world coordinate system
	 * @param x2
	 *            the x end value in world coordinate system
	 * @param y1
	 *            the y start value in world coordinate system
	 * @param y2
	 *            the y end value in world coordinate system
	 * @return a rectangle annotation
	 */
	public RectangleAnnotation createRectangleAnnotation(double x1, double x2, double y1, double y2) {
		RectangleAnnotationImpl annotation = new RectangleAnnotationImpl();
		annotation.setXValueRange(new Range.Double(x1, x2));
		annotation.setYValueRange(new Range.Double(y1, y2));
		applyProfile(annotation);
		RectangleAnnotation annotationProxy = proxy(annotation, RectangleAnnotation.class);

		DummyEnvironment env = new DummyEnvironment(threadSafe);
		env.registerElement(annotation, annotationProxy);
		return annotationProxy;
	}

	public BasicStroke createStroke(float width) {
		return new BasicStroke(width, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, null, 0f);
	}

	public BasicStroke createStroke(float width, float[] dash) {
		return new BasicStroke(width, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, dash, 0f);
	}

	public ImageGraph createImageGraph(byte[][] byte2d) {
		return createImageGraph(new SingleBandImageData(new ByteDataBuffer.Array2D(byte2d), byte2d[0].length,
				byte2d.length));
	}

	public ImageGraph createImageGraph(float[][] float2d) {
		return createImageGraph(new SingleBandImageData(new FloatDataBuffer.Array2D(float2d), float2d[0].length,
				float2d.length));
	}

	/**
	 * Create an single band image graph.
	 * 
	 * @return an image graph
	 */
	public ImageGraph createImageGraph(SingleBandImageData data) {

		ImageMapping im = createImageMapping();
		DummyEnvironment env = (DummyEnvironment) im.getEnvironment();
		ImageMappingEx ime = (ImageMappingEx) ((ElementAddition) im).getImpl();

		ImageGraphImpl graph = new ImageGraphImpl();
		graph.setMapping(ime);
		graph.setData(data);
		applyProfile(graph);
		ImageGraph gpProxy = proxy(graph, ImageGraph.class);

		env.registerElement(graph, gpProxy);
		return gpProxy;
	}

	/**
	 * Create an image mapping.
	 * 
	 * @return an image mapping
	 */
	public ImageMapping createImageMapping() {
		ImageMappingImpl impl = new ImageMappingImpl();
		applyProfile(impl);
		ImageMapping proxy = proxy(impl, ImageMapping.class);

		DummyEnvironment env = new DummyEnvironment(threadSafe);
		env.registerElement(impl, proxy);

		return proxy;
	}

	public RGBImageGraph createRGBImageGraph(float[][] red2d, float[][] green2d, float[][] blue2d) {
		ImageDataBuffer redBuffer = new FloatDataBuffer.Array2D(red2d);
		ImageDataBuffer greenBuffer = new FloatDataBuffer.Array2D(green2d);
		ImageDataBuffer blueBuffer = new FloatDataBuffer.Array2D(blue2d);
		return createRGBImageGraph(new MultiBandImageData(new ImageDataBuffer[] { redBuffer, greenBuffer, blueBuffer },
				red2d[0].length, red2d.length));
	}

	/**
	 * Create an single band image graph.
	 * 
	 * @return an image graph
	 */
	public RGBImageGraph createRGBImageGraph(MultiBandImageData data) {

		RGBImageMapping im = createRGBImageMapping();
		DummyEnvironment env = (DummyEnvironment) im.getEnvironment();
		RGBImageMappingEx ime = (RGBImageMappingEx) ((ElementAddition) im).getImpl();

		RGBImageGraphImpl graph = new RGBImageGraphImpl();
		graph.setMapping(ime);
		graph.setData(data);
		applyProfile(graph);
		RGBImageGraph gpProxy = proxy(graph, RGBImageGraph.class);

		env.registerElement(graph, gpProxy);
		return gpProxy;
	}

	/**
	 * Create an image mapping.
	 * 
	 * @return an image mapping
	 */
	public RGBImageMapping createRGBImageMapping() {
		RGBImageMappingImpl impl = new RGBImageMappingImpl();
		applyProfile(impl);
		RGBImageMapping proxy = proxy(impl, RGBImageMapping.class);

		DummyEnvironment env = new DummyEnvironment(threadSafe);
		env.registerElement(impl, proxy);
		env.registerElement(impl.getRedTransform(), proxy(impl.getRedTransform(), ImageBandTransform.class));
		env.registerElement(impl.getGreenTransform(), proxy(impl.getGreenTransform(), ImageBandTransform.class));
		env.registerElement(impl.getBlueTransform(), proxy(impl.getBlueTransform(), ImageBandTransform.class));

		return proxy;
	}

}
