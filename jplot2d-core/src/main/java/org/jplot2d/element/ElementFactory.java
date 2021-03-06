/*
 * Copyright 2010-2015 Jingjing Li.
 *
 * This file is part of jplot2d.
 *
 * jplot2d is free software:
 * you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation, either version 3 of the License, or any later version.
 *
 * jplot2d is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Lesser Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with jplot2d.
 * If not, see <http://www.gnu.org/licenses/>.
 */
package org.jplot2d.element;

import org.jplot2d.data.*;
import org.jplot2d.element.impl.*;
import org.jplot2d.env.*;
import org.jplot2d.sizing.FillContainerSizeMode;
import org.jplot2d.util.Range;
import org.jplot2d.util.SymbolShape;

import javax.annotation.Nullable;
import java.awt.*;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;

/**
 * A factory to produce all kind of plot components.
 *
 * @author Jingjing Li
 */
@SuppressWarnings("unused")
public class ElementFactory {

    private static final ElementFactory instance = new ElementFactory(false, null);

    private static final ElementFactory threadSafeInstance = new ElementFactory(true, null);
    private final boolean threadSafe;
    private final StyleConfiguration style;

    protected ElementFactory(boolean threadSafe, StyleConfiguration style) {
        this.threadSafe = threadSafe;
        this.style = style;
    }

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
     * Returns an instance of ComponentFactory which is configured by style.
     * All component created by this factory are not thread-safe.
     *
     * @param style the style used to config created components
     * @return an instance of ComponentFactory
     */
    public static ElementFactory getInstance(StyleConfiguration style) {
        return new ElementFactory(false, style);
    }

    /**
     * Returns an instance of ComponentFactory which is configured by style.
     * All component created by this factory are thread-safe.
     *
     * @param style the style used to config created components
     * @return an instance of ComponentFactory
     */
    public static ElementFactory getThreadSafeInstance(StyleConfiguration style) {
        return new ElementFactory(true, style);
    }

    /**
     * Returns the parent interface of the given element, which is in org.jplot2d.element package
     *
     * @param element the element
     * @return an interface in org.jplot2d.element package
     */
    public static Class<?> getElementInterface(Class<?> element) {
        String name = element.getName();
        int i = name.lastIndexOf('.');
        if (i != -1) {
            name = name.substring(0, i);
            if (name.equals("org.jplot2d.element")) {
                return element;
            }
        }

        Class<?>[] interfaces = element.getInterfaces();
        for (Class<?> anInterface : interfaces) {
            Class<?> eif = getElementInterface(anInterface);
            if (eif != null) {
                return eif;
            }
        }
        return null;
    }

    /**
     * Apply style to the given element
     *
     * @param element the element to be configured
     */
    protected final void applyProfile(Element element) {
        if (style != null) {
            style.applyTo(element);
        }
    }

    protected DummyEnvironment createDummyEnvironment() {
        return new DummyEnvironment(threadSafe);
    }

    /**
     * Create proxy for the given impl
     *
     * @param <T>   the proxy type
     * @param impl  the impl
     * @param clazz the class of proxy type
     * @return the proxy
     */
    @SuppressWarnings("unchecked")
    public <T extends Element> T proxy(ElementEx impl, Class<T> clazz) {
        ElementIH ih = new ElementIH(impl, clazz);
        return (T) Proxy.newProxyInstance(clazz.getClassLoader(), new Class[]{clazz, ElementAddition.class}, ih);
    }

    /**
     * Create a plot with the initial values.
     * <ul>
     * <li>cacheable: true</li>
     * <li>color: BLACK</li>
     * <li>font: Serif PLAIN 12</li>
     * <li>margin: extra 12 on left, top, right, bottom</li>
     * <li>sizeMode: FillContainerSizeMode(1)</li>
     * <li>containerSize: 640x480</li>
     * </ul>
     *
     * @return a plot
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

        DummyEnvironment env = createDummyEnvironment();
        env.registerElement(plot, plotProxy);
        env.registerElement(legend, legendProxy);
        env.registerElement(margin, marginProxy);

        return plotProxy;
    }

    /**
     * Create a plot which can be added as a subplot of another plot.
     * A subplot is same as a plot, except its size are managed by the layout director of its parent plot.
     * <p>
     * the initial values
     * <ul>
     * <li>cacheable: false</li>
     * <li>color: unassigned, means inherit from its parent plot</li>
     * <li>font: unassigned, means inherit from its parent plot</li>
     * <li>margin: no extra margin</li>
     * <li>sizeMode: unassigned</li>
     * <li>containerSize: unassigned</li>
     * </ul>
     *
     * @return a plot
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

        DummyEnvironment env = createDummyEnvironment();
        env.registerElement(subplot, subplotProxy);
        env.registerElement(legend, legendProxy);
        env.registerElement(margin, marginProxy);

        return subplotProxy;
    }

    /**
     * Create a plot title.
     *
     * @param text the title text
     * @return a plot title
     */
    public Title createTitle(String text) {
        TitleImpl impl = new TitleImpl();
        if (text != null) {
            impl.setText(text);
        }

        applyProfile(impl);
        Title proxy = proxy(impl, Title.class);

        DummyEnvironment env = createDummyEnvironment();
        env.registerElement(impl, proxy);
        return proxy;
    }

    /**
     * Create an Axis. The default position is {@link AxisPosition#NEGATIVE_SIDE}
     *
     * @return an axis
     */
    public PlotAxis createAxis() {
        return createAxes(1)[0];
    }

    /**
     * Create n Axes which share the same tick manager. The position of the axis on index 0 is
     * {@link AxisPosition#NEGATIVE_SIDE}, the position of the axis on index 1 is {@link AxisPosition#POSITIVE_SIDE}.
     * All the created axes must be added to a plot by {@link Plot#addXAxes(PlotAxis[])} or {@link Plot#addYAxes(PlotAxis[])}
     *
     * @return axes in an array
     */
    public PlotAxis[] createAxes(int n) {

        AxisTickManager tm = createAxisTickManager();
        DummyEnvironment env = (DummyEnvironment) tm.getEnvironment();
        AxisTickManagerEx tme = (AxisTickManagerEx) ((ElementAddition) tm).getImpl();

        PlotAxis[] result = new PlotAxis[n];

        // add axis
        for (int i = 0; i < n; i++) {
            // this create tick and title inside
            PlotAxisImpl axis = new PlotAxisImpl();
            axis.setTickManager(tme);

            PlotAxis axisProxy = proxy(axis, PlotAxis.class);

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
     * Create an AxisTickManager, which contains an AxisTransform.
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

        DummyEnvironment env = createDummyEnvironment();
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

        DummyEnvironment env = createDummyEnvironment();
        env.registerElement(impl, proxy);

        return proxy;
    }

    /**
     * Create a layer and add the given graph.
     *
     * @param graph the graph will be added to the created layer
     * @return the created layer
     */
    public Layer createLayer(Graph graph) {
        Layer layer = this.createLayer();
        layer.addGraph(graph);
        return layer;
    }

    /**
     * Create a layer.
     *
     * @return the created layer
     */
    public Layer createLayer() {
        LayerImpl impl = new LayerImpl();
        applyProfile(impl);
        Layer proxy = proxy(impl, Layer.class);

        DummyEnvironment env = createDummyEnvironment();
        env.registerElement(impl, proxy);
        return proxy;
    }

    /**
     * Create a XYGraph with the given x/y array.
     *
     * @param xarray the x data array, can be byte[], short[], int[], long[], float[] or double[]
     * @param yarray the y data array, can be byte[], short[], int[], long[], float[] or double[]
     * @return a XYGraph object
     */
    public XYGraph createXYGraph(Object xarray, Object yarray) {
        return createXYGraph(new ArrayPair(xarray, yarray));
    }

    /**
     * Create a XYGraph with the given x/y array.
     *
     * @param xarray the x data array, can be byte[], short[], int[], long[], float[] or double[]
     * @param yarray the y data array, can be byte[], short[], int[], long[], float[] or double[]
     * @param name   the name of created XYGraph, can display in legend
     * @return a XYGraph object
     */
    public XYGraph createXYGraph(Object xarray, Object yarray, String name) {
        return createXYGraph(new ArrayPair(xarray, yarray), name);
    }

    /**
     * Create a XYGraph with the given x/y array, x low/high error array and y low/high error array.
     *
     * @param xarray     the x data array, can be byte[], short[], int[], long[], float[] or double[]
     * @param yarray     the y data array, can be byte[], short[], int[], long[], float[] or double[]
     * @param xErrorLow  the x low error array, can be byte[], short[], int[], long[], float[] or double[]
     * @param xErrorHigh the x high error array, can be byte[], short[], int[], long[], float[] or double[]
     * @param yErrorLow  the y low error array, can be byte[], short[], int[], long[], float[] or double[]
     * @param yErrorHigh the y high error array, can be byte[], short[], int[], long[], float[] or double[]
     * @return a XYGraph object
     */
    public XYGraph createXYGraph(Object xarray, Object yarray, Object xErrorLow, Object xErrorHigh, Object yErrorLow,
                                 Object yErrorHigh) {
        return createXYGraph(xarray, yarray, xErrorLow, xErrorHigh, yErrorLow, yErrorHigh, null);
    }

    /**
     * Create a XYGraph with the given x/y array, x low/high error array and y low/high error array.
     *
     * @param xarray     the x data array, can be byte[], short[], int[], long[], float[] or double[]
     * @param yarray     the y data array, can be byte[], short[], int[], long[], float[] or double[]
     * @param xErrorLow  the x low error array, can be byte[], short[], int[], long[], float[] or double[]
     * @param xErrorHigh the x high error array, can be byte[], short[], int[], long[], float[] or double[]
     * @param yErrorLow  the y low error array, can be byte[], short[], int[], long[], float[] or double[]
     * @param yErrorHigh the y high error array, can be byte[], short[], int[], long[], float[] or double[]
     * @param name       the name of created XYGraph, can display in legend
     * @return a XYGraph object
     */
    public XYGraph createXYGraph(Object xarray, Object yarray, Object xErrorLow, Object xErrorHigh, Object yErrorLow,
                                 Object yErrorHigh, String name) {
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

    /**
     * Create a XYGraph with the given ArrayPair.
     *
     * @param xy the x/y ArrayPair
     * @return a XYGraph object
     */
    public XYGraph createXYGraph(ArrayPair xy) {
        return createXYGraph(new XYGraphData(xy));
    }

    /**
     * Create a XYGraph with the given x/y ArrayPair.
     *
     * @param xy   the x/y ArrayPair
     * @param name the name of created XYGraph, can display in legend
     * @return a XYGraph object
     */
    public XYGraph createXYGraph(ArrayPair xy, String name) {
        return createXYGraph(new XYGraphData(xy), name);
    }

    /**
     * Create a XYGraph with the given x/y ArrayPair, x low/high error ArrayPair and y low/high error ArrayPair.
     *
     * @param xy     the x/y ArrayPair
     * @param errorX the x low/high error ArrayPair
     * @param errorY the y low/high error ArrayPair
     * @return a XYGraph object
     */
    public XYGraph createXYGraph(ArrayPair xy, ArrayPair errorX, ArrayPair errorY) {
        return createXYGraph(new XYGraphData(xy, errorX, errorY));
    }

    /**
     * Create a XYGraph with the given x/y ArrayPair, x low/high error ArrayPair and y low/high error ArrayPair.
     *
     * @param xy     the x/y ArrayPair
     * @param errorX the x low/high error ArrayPair
     * @param errorY the y low/high error ArrayPair
     * @param name   the name of created XYGraph, can display in legend
     * @return a XYGraph object
     */
    public XYGraph createXYGraph(ArrayPair xy, ArrayPair errorX, ArrayPair errorY, String name) {
        return createXYGraph(new XYGraphData(xy, errorX, errorY), name);
    }

    /**
     * Create a XYGraph with the given graph data
     *
     * @param data the graph to be plotted
     * @return a XYGraph
     */
    public XYGraph createXYGraph(XYGraphData data) {
        return createXYGraph(data, null);
    }

    /**
     * Create a XYGraph with the given graph data and name
     *
     * @param data the graph to be plotted
     * @param name the name of created XYGraph, can display in legend
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

        DummyEnvironment env = createDummyEnvironment();
        env.registerElement(graph, gpProxy);
        env.registerElement(li, liProxy);
        return gpProxy;
    }

    /**
     * Create a symbol annotation at the given value point in layer's world coordinate system. The symbol and text can be set later.
     *
     * @param x the x of the value point
     * @param y the y of the value point
     * @return a symbol annotation
     */
    public SymbolAnnotation createSymbolAnnotation(double x, double y) {
        SymbolAnnotationImpl annotation = new SymbolAnnotationImpl();
        annotation.setValuePoint(x, y);
        applyProfile(annotation);
        SymbolAnnotation annotationProxy = proxy(annotation, SymbolAnnotation.class);

        DummyEnvironment env = createDummyEnvironment();
        env.registerElement(annotation, annotationProxy);
        return annotationProxy;
    }

    /**
     * Create a symbol annotation at the given value point in layer's world coordinate system.
     *
     * @param x    the x of the value point
     * @param y    the y of the value point
     * @param text the text will show on the point
     * @return a symbol annotation
     */
    public SymbolAnnotation createSymbolAnnotation(double x, double y, String text) {
        SymbolAnnotationImpl annotation = new SymbolAnnotationImpl();
        annotation.setValuePoint(x, y);
        annotation.setText(text);
        applyProfile(annotation);
        SymbolAnnotation annotationProxy = proxy(annotation, SymbolAnnotation.class);

        DummyEnvironment env = createDummyEnvironment();
        env.registerElement(annotation, annotationProxy);
        return annotationProxy;
    }

    /**
     * Create a symbol annotation at the given value point in layer's world coordinate system.
     *
     * @param x      the x of the value point
     * @param y      the y of the value point
     * @param symbol the symbol will show on the point, can be <code>null</code>
     * @return a symbol annotation
     */
    public SymbolAnnotation createSymbolAnnotation(double x, double y, SymbolShape symbol) {
        SymbolAnnotationImpl annotation = new SymbolAnnotationImpl();
        annotation.setValuePoint(x, y);
        annotation.setSymbolShape(symbol);
        applyProfile(annotation);
        SymbolAnnotation annotationProxy = proxy(annotation, SymbolAnnotation.class);

        DummyEnvironment env = createDummyEnvironment();
        env.registerElement(annotation, annotationProxy);
        return annotationProxy;
    }

    /**
     * Create a symbol annotation at the given value point in layer's world coordinate system.
     *
     * @param x      the x of the value point
     * @param y      the y of the value point
     * @param symbol the symbol will show on the point, can be <code>null</code>
     * @param text   the text will show on the side of the symbol
     * @return a symbol annotation
     */
    public SymbolAnnotation createSymbolAnnotation(double x, double y, SymbolShape symbol, String text) {
        SymbolAnnotationImpl annotation = new SymbolAnnotationImpl();
        annotation.setValuePoint(x, y);
        annotation.setSymbolShape(symbol);
        annotation.setText(text);
        applyProfile(annotation);
        SymbolAnnotation annotationProxy = proxy(annotation, SymbolAnnotation.class);

        DummyEnvironment env = createDummyEnvironment();
        env.registerElement(annotation, annotationProxy);
        return annotationProxy;
    }

    /**
     * Create a symbol annotation at the given value point in layer's world coordinate system.
     * The text will automatically update whenever the annotation changes to a new location.
     *
     * @param x      the x of the value point
     * @param y      the y of the value point
     * @param symbol the symbol will show on the point, can be <code>null</code>
     * @return a symbol annotation
     */
    public CoordinateAnnotation createCoordinateAnnotation(double x, double y, SymbolShape symbol) {
        CoordinateAnnotationImpl annotation = new CoordinateAnnotationImpl();
        annotation.setValuePoint(x, y);
        annotation.setSymbolShape(symbol);
        applyProfile(annotation);
        CoordinateAnnotation annotationProxy = proxy(annotation, CoordinateAnnotation.class);

        DummyEnvironment env = createDummyEnvironment();
        env.registerElement(annotation, annotationProxy);
        return annotationProxy;
    }

    /**
     * Create a horizontal line annotation with the given y value in world coordinate system
     *
     * @param y the y value in world coordinate system
     * @return a horizontal line annotation
     */
    public HLineAnnotation createHLineAnnotation(double y) {
        HLineAnnotationImpl annotation = new HLineAnnotationImpl();
        annotation.setValue(y);
        applyProfile(annotation);
        HLineAnnotation annotationProxy = proxy(annotation, HLineAnnotation.class);

        DummyEnvironment env = createDummyEnvironment();
        env.registerElement(annotation, annotationProxy);
        return annotationProxy;
    }

    /**
     * Create a vertical line annotation with the given y value in world coordinate system
     *
     * @param x the y value in world coordinate system
     * @return a horizontal line annotation
     */
    public VLineAnnotation createVLineAnnotation(double x) {
        VLineAnnotationImpl annotation = new VLineAnnotationImpl();
        annotation.setValue(x);
        applyProfile(annotation);
        VLineAnnotation annotationProxy = proxy(annotation, VLineAnnotation.class);

        DummyEnvironment env = createDummyEnvironment();
        env.registerElement(annotation, annotationProxy);
        return annotationProxy;
    }

    /**
     * Create a horizontal strip annotation with the given y value range in world coordinate system
     *
     * @param start the y start value in world coordinate system
     * @param end   the y end value in world coordinate system
     * @return a horizontal strip annotation
     */
    public HStripAnnotation createHStripAnnotation(double start, double end) {
        HStripAnnotationImpl annotation = new HStripAnnotationImpl();
        annotation.setValueRange(new Range.Double(start, end));
        applyProfile(annotation);
        HStripAnnotation annotationProxy = proxy(annotation, HStripAnnotation.class);

        DummyEnvironment env = createDummyEnvironment();
        env.registerElement(annotation, annotationProxy);
        return annotationProxy;
    }

    /**
     * Create a vertical strip annotation with the given x value range in world coordinate system.
     *
     * @param start the x start value in world coordinate system
     * @param end   the x end value in world coordinate system
     * @return a vertical strip annotation
     */
    public VStripAnnotation createVStripAnnotation(double start, double end) {
        VStripAnnotationImpl annotation = new VStripAnnotationImpl();
        annotation.setValueRange(new Range.Double(start, end));
        applyProfile(annotation);
        VStripAnnotation annotationProxy = proxy(annotation, VStripAnnotation.class);

        DummyEnvironment env = createDummyEnvironment();
        env.registerElement(annotation, annotationProxy);
        return annotationProxy;
    }

    /**
     * Create a rectangle annotation with the given x and y value range in world coordinate system.
     *
     * @param x1 the x start value in world coordinate system
     * @param x2 the x end value in world coordinate system
     * @param y1 the y start value in world coordinate system
     * @param y2 the y end value in world coordinate system
     * @return a rectangle annotation
     */
    public RectangleAnnotation createRectangleAnnotation(double x1, double x2, double y1, double y2) {
        RectangleAnnotationImpl annotation = new RectangleAnnotationImpl();
        annotation.setXValueRange(new Range.Double(x1, x2));
        annotation.setYValueRange(new Range.Double(y1, y2));
        applyProfile(annotation);
        RectangleAnnotation annotationProxy = proxy(annotation, RectangleAnnotation.class);

        DummyEnvironment env = createDummyEnvironment();
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
        return createImageGraph(new ByteDataBuffer.Array2D(byte2d), byte2d[0].length, byte2d.length);
    }

    public ImageGraph createImageGraph(short[][] short2d) {
        return createImageGraph(new ShortDataBuffer.Array2D(short2d), short2d[0].length, short2d.length);
    }

    public ImageGraph createImageGraph(int[][] int2d) {
        return createImageGraph(new IntDataBuffer.Array2D(int2d), int2d[0].length, int2d.length);
    }

    public ImageGraph createImageGraph(float[][] float2d) {
        return createImageGraph(new FloatDataBuffer.Array2D(float2d), float2d[0].length, float2d.length);
    }

    public ImageGraph createImageGraph(double[][] double2d) {
        return createImageGraph(new DoubleDataBuffer.Array2D(double2d), double2d[0].length, double2d.length);
    }

    public ImageGraph createImageGraph(ImageDataBuffer dataBuffer, int w, int h) {
        return createImageGraph(new SingleBandImageData(dataBuffer, w, h));
    }

    public ImageGraph createImageGraph(ImageDataBuffer dataBuffer, int w, int h, ImageCoordinateReference cr) {
        return createImageGraph(new SingleBandImageData(dataBuffer, w, h, cr));
    }

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

    public ImageMapping createImageMapping() {
        ImageMappingImpl mapping = new ImageMappingImpl();
        applyProfile(mapping);

        ImageMapping proxy = proxy(mapping, ImageMapping.class);

        DummyEnvironment env = createDummyEnvironment();
        env.registerElement(mapping, proxy);

        return proxy;
    }

    public RGBImageGraph createRGBImageGraph(byte[][] red2d, byte[][] green2d, byte[][] blue2d) {
        int w = red2d[0].length;
        int h = red2d.length;
        if (w != green2d[0].length || h != green2d.length || w != blue2d[0].length || h != blue2d.length) {
            throw new IllegalArgumentException("Input data do not have the same dimension.");
        }

        ImageDataBuffer redBuffer = new ByteDataBuffer.Array2D(red2d);
        ImageDataBuffer greenBuffer = new ByteDataBuffer.Array2D(green2d);
        ImageDataBuffer blueBuffer = new ByteDataBuffer.Array2D(blue2d);
        return createRGBImageGraph(new ImageDataBuffer[]{redBuffer, greenBuffer, blueBuffer}, w, h);
    }

    public RGBImageGraph createRGBImageGraph(short[][] red2d, short[][] green2d, short[][] blue2d) {
        int w = red2d[0].length;
        int h = red2d.length;
        if (w != green2d[0].length || h != green2d.length || w != blue2d[0].length || h != blue2d.length) {
            throw new IllegalArgumentException("Input data do not have the same dimension.");
        }

        ImageDataBuffer redBuffer = new ShortDataBuffer.Array2D(red2d);
        ImageDataBuffer greenBuffer = new ShortDataBuffer.Array2D(green2d);
        ImageDataBuffer blueBuffer = new ShortDataBuffer.Array2D(blue2d);
        return createRGBImageGraph(new ImageDataBuffer[]{redBuffer, greenBuffer, blueBuffer}, w, h);
    }

    public RGBImageGraph createRGBImageGraph(int[][] red2d, int[][] green2d, int[][] blue2d) {
        int w = red2d[0].length;
        int h = red2d.length;
        if (w != green2d[0].length || h != green2d.length || w != blue2d[0].length || h != blue2d.length) {
            throw new IllegalArgumentException("Input data do not have the same dimension.");
        }

        ImageDataBuffer redBuffer = new IntDataBuffer.Array2D(red2d);
        ImageDataBuffer greenBuffer = new IntDataBuffer.Array2D(green2d);
        ImageDataBuffer blueBuffer = new IntDataBuffer.Array2D(blue2d);
        return createRGBImageGraph(new ImageDataBuffer[]{redBuffer, greenBuffer, blueBuffer}, w, h);
    }

    public RGBImageGraph createRGBImageGraph(float[][] red2d, float[][] green2d, float[][] blue2d) {
        int w = red2d[0].length;
        int h = red2d.length;
        if (w != green2d[0].length || h != green2d.length || w != blue2d[0].length || h != blue2d.length) {
            throw new IllegalArgumentException("Input data do not have the same dimension.");
        }

        ImageDataBuffer redBuffer = new FloatDataBuffer.Array2D(red2d);
        ImageDataBuffer greenBuffer = new FloatDataBuffer.Array2D(green2d);
        ImageDataBuffer blueBuffer = new FloatDataBuffer.Array2D(blue2d);
        return createRGBImageGraph(new ImageDataBuffer[]{redBuffer, greenBuffer, blueBuffer}, w, h);
    }

    public RGBImageGraph createRGBImageGraph(double[][] red2d, double[][] green2d, double[][] blue2d) {
        int w = red2d[0].length;
        int h = red2d.length;
        if (w != green2d[0].length || h != green2d.length || w != blue2d[0].length || h != blue2d.length) {
            throw new IllegalArgumentException("Input data do not have the same dimension.");
        }

        ImageDataBuffer redBuffer = new DoubleDataBuffer.Array2D(red2d);
        ImageDataBuffer greenBuffer = new DoubleDataBuffer.Array2D(green2d);
        ImageDataBuffer blueBuffer = new DoubleDataBuffer.Array2D(blue2d);
        return createRGBImageGraph(new ImageDataBuffer[]{redBuffer, greenBuffer, blueBuffer}, w, h);
    }

    public RGBImageGraph createRGBImageGraph(ImageDataBuffer[] dataBuffers, int w, int h) {
        return createRGBImageGraph(new MultiBandImageData(dataBuffers, w, h));
    }

    public RGBImageGraph createRGBImageGraph(ImageDataBuffer[] dataBuffers, int w, int h, ImageCoordinateReference cr) {
        return createRGBImageGraph(new MultiBandImageData(dataBuffers, w, h, cr));
    }

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

    public RGBImageMapping createRGBImageMapping() {
        RGBImageMappingImpl impl = new RGBImageMappingImpl();
        applyProfile(impl);
        RGBImageMapping proxy = proxy(impl, RGBImageMapping.class);

        DummyEnvironment env = createDummyEnvironment();
        env.registerElement(impl, proxy);
        env.registerElement(impl.getRedTransform(), proxy(impl.getRedTransform(), ImageBandTransform.class));
        env.registerElement(impl.getGreenTransform(), proxy(impl.getGreenTransform(), ImageBandTransform.class));
        env.registerElement(impl.getBlueTransform(), proxy(impl.getBlueTransform(), ImageBandTransform.class));

        return proxy;
    }

    /**
     * Create a colorbar.
     *
     * @return a colorbar
     */
    public Colorbar createColorbar() {
        ColorbarImpl impl = new ColorbarImpl();
        applyProfile(impl);

        Colorbar proxy = proxy(impl, Colorbar.class);

        AxisEx lowerAxis = impl.getInnerAxis();
        Axis lowerAxisProxy = proxy(lowerAxis, ColorbarAxis.class);
        AxisTitleEx lowerTitle = lowerAxis.getTitle();
        AxisTitle lowerTitleProxy = proxy(lowerTitle, AxisTitle.class);
        AxisEx upperAxis = impl.getOuterAxis();
        Axis upperAxisProxy = proxy(upperAxis, ColorbarAxis.class);
        AxisTitleEx upperTitle = upperAxis.getTitle();
        AxisTitle upperTitleProxy = proxy(upperTitle, AxisTitle.class);
        AxisTickManagerEx atm = lowerAxis.getTickManager();
        AxisTickManager atmProxy = proxy(atm, AxisTickManager.class);
        AxisTransformEx axf = impl.getAxisTransform();
        AxisTransform axfProxy = proxy(axf, AxisTransform.class);

        DummyEnvironment env = createDummyEnvironment();
        env.registerElement(impl, proxy);
        env.registerElement(lowerAxis, lowerAxisProxy);
        env.registerElement(upperAxis, upperAxisProxy);
        env.registerElement(lowerTitle, lowerTitleProxy);
        env.registerElement(upperTitle, upperTitleProxy);
        env.registerElement(atm, atmProxy);
        env.registerElement(axf, axfProxy);

        return proxy;
    }


    /**
     * Create a copy of the given element. All sub-element are copied together.
     *
     * @param element the element to be copied.
     * @return the copy of given element
     */
    public <T extends Element> T copy(T element) {
        return copy(element, null);
    }

    /**
     * Create a copy of the given element. All sub-element are copied together.
     *
     * @param element the element to be copied.
     * @param copyMap a map to be filled with mapping from original element to copy element.
     * @return the copy of given element
     */
    @SuppressWarnings("unchecked")
    public <T extends Element> T copy(T element, @Nullable Map<Element, Element> copyMap) {
        if (copyMap == null) {
            copyMap = new HashMap<>();
        }

        DummyEnvironment denv = createDummyEnvironment();

        Environment senv = element.getEnvironment();
        BatchToken token = senv.beginBatch("copy " + element);

        HashMap<ElementEx, ElementEx> implCopyMap = new HashMap<>();
        ElementEx ecopy = ((ElementAddition) element).getImpl().copyStructure(implCopyMap);

        for (Map.Entry<ElementEx, ElementEx> me : implCopyMap.entrySet()) {
            ElementEx simpl = me.getKey();
            ElementEx dimpl = me.getValue();

            dimpl.copyFrom(simpl);

            Element sproxy = senv.getProxy(simpl);
            Class<? extends Element> itf = (Class<? extends Element>) sproxy.getClass().getInterfaces()[0];
            Element dproxy = proxy(me.getValue(), itf);
            denv.registerElement(dimpl, dproxy);

            copyMap.put(sproxy, dproxy);
        }

        senv.endBatch(token);

        return (T) denv.getProxy(ecopy);
    }
}
