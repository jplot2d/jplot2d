/**
 * Copyright 2010-2014 Jingjing Li.
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
package org.jplot2d.element.impl;

import java.awt.Graphics2D;
import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jplot2d.element.Axis;
import org.jplot2d.element.Plot;
import org.jplot2d.element.AxisTransform;
import org.jplot2d.element.AxisOrientation;
import org.jplot2d.element.Element;
import org.jplot2d.element.Layer;
import org.jplot2d.element.Title;
import org.jplot2d.element.TitlePosition;
import org.jplot2d.layout.LayoutDirector;
import org.jplot2d.layout.SimpleLayoutDirector;
import org.jplot2d.notice.RangeAdjustedToValueBoundsNotice;
import org.jplot2d.notice.RangeSelectionNotice;
import org.jplot2d.notice.Notifier;
import org.jplot2d.notice.Notice;
import org.jplot2d.sizing.SizeMode;
import org.jplot2d.transform.PaperTransform;
import org.jplot2d.util.DoubleDimension2D;
import org.jplot2d.util.Range;

/**
 * @author Jingjing Li
 * 
 */
public class PlotImpl extends ContainerImpl implements PlotEx {

	/**
	 * The container size only take effect when this plot is top plot and has a size mode assigned.
	 */
	private double containerWidth, containerHeight;

	/**
	 * The size mode only take effect when this plot is the root plot.
	 */
	private SizeMode sizeMode;

	private double locX, locY;

	private double width = 640, height = 480;

	private double scale = 1;

	private PaperTransform pxf;

	/**
	 * True when the object is valid. An invalid object needs to be laid out. This flag is set to false when the object
	 * size is changed. The initial value is true, because the {@link #contentSize} and size are same.
	 * 
	 * @see #isValid
	 * @see #validate
	 * @see #invalidate
	 */
	private boolean valid = true;

	private boolean rerenderNeeded;

	private Notifier notifier;

	private LayoutDirector layoutDirector = new SimpleLayoutDirector();

	/**
	 * Efficient Immutable
	 */
	private Dimension2D contentConstraint;

	/**
	 * Must be valid size (positive width and height)
	 */
	private double preferredContentWidth = 320;

	private double preferredContentHeight = 240;

	/**
	 * Efficient Immutable
	 */
	private Dimension2D contentSize;

	private PlotMarginEx margin;

	private LegendEx legend;

	private final List<TitleEx> titles = new ArrayList<TitleEx>();

	private final List<AxisEx> xAxis = new ArrayList<AxisEx>();

	private final List<AxisEx> yAxis = new ArrayList<AxisEx>();

	private final List<LayerEx> layers = new ArrayList<LayerEx>();

	private final List<PlotEx> subplots = new ArrayList<PlotEx>();

	public PlotImpl() {
		margin = new PlotMarginImpl();
		margin.setParent(this);
		legend = new LegendImpl();
		legend.setParent(this);
	}

	public PlotImpl(LegendEx legend) {
		margin = new PlotMarginImpl();
		margin.setParent(this);
		this.legend = legend;
		legend.setParent(this);
	}

	public String getShortId() {
		return null;
	}

	public String getId() {
		if (getParent() != null) {
			return "Plot" + getParent().indexOf(this);
		} else {
			return "Plot@" + Integer.toHexString(System.identityHashCode(this));
		}
	}

	public InvokeStep getInvokeStepFormParent() {
		if (parent == null) {
			return null;
		}

		Method method;
		try {
			method = PlotEx.class.getMethod("getSubplot", Integer.TYPE);
		} catch (NoSuchMethodException e) {
			throw new Error(e);
		}
		return new InvokeStep(method, getParent().indexOf(this));
	}

	@Override
	public PlotEx getParent() {
		return (PlotEx) super.getParent();
	}

	@Override
	public Map<Element, Element> getMooringMap() {
		Map<Element, Element> result = new HashMap<Element, Element>();

		for (AxisEx axis : xAxis) {
			AxisTransformEx arm = axis.getTickManager().getAxisTransform();
			for (LayerEx layer : arm.getLayers()) {
				if (layer.getParent() != this) {
					result.put(arm, layer);
				}
			}
		}
		for (AxisEx axis : yAxis) {
			AxisTransformEx arm = axis.getTickManager().getAxisTransform();
			for (LayerEx layer : arm.getLayers()) {
				if (layer.getParent() != this) {
					result.put(arm, layer);
				}
			}
		}

		return result;
	}

	@Override
	public Dimension2D getContainerSize() {
		return new DoubleDimension2D(containerWidth, containerHeight);
	}

	@Override
	public void setContainerSize(Dimension2D size) {
		if (sizeMode == null) {
			throw new IllegalStateException("The sizeMode property must be set.");
		}
		this.containerWidth = size.getWidth();
		this.containerHeight = size.getHeight();
	}

	@Override
	public SizeMode getSizeMode() {
		return sizeMode;
	}

	@Override
	public void setSizeMode(SizeMode sizeMode) {
		this.sizeMode = sizeMode;
		if (getParent() == null && sizeMode.isAutoPack()) {
			invalidate();
		}
	}

	@Override
	public Point2D getLocation() {
		return new Point2D.Double(locX, locY);
	}

	@Override
	public final void setLocation(Point2D p) {
		setLocation(p.getX(), p.getY());
	}

	@Override
	public void setLocation(double locX, double locY) {
		if (getLocation().getX() != locX || getLocation().getY() != locY) {
			this.locX = locX;
			this.locY = locY;
			pxf = null;

			// all its sub-components should redraw, even the cacheable sub-component
			redrawCascade(this);
		}
	}

	@Override
	public Dimension2D getSize() {
		return new DoubleDimension2D(width, height);
	}

	@Override
	public final void setSize(Dimension2D size) {
		this.setSize(size.getWidth(), size.getHeight());
	}

	@Override
	public void setSize(double width, double height) {
		if (width < 0 || height < 0) {
			throw new IllegalArgumentException("paper size must be positive, " + width + "x" + height + " is invalid.");
		}

		if (this.width != width || this.height != height) {
			this.width = width;
			this.height = height;
			invalidate();
		}
	}

	@Override
	public Rectangle2D getBounds() {
		return new Rectangle2D.Double(-margin.getLeft() - margin.getExtraLeft(), -margin.getBottom()
				- margin.getExtraBottom(), width, height);
	}

	@Override
	public double getScale() {
		return this.scale;
	}

	@Override
	public void setScale(double scale) {
		if (this.scale != scale) {
			this.scale = scale;
			pxf = null;
		}
	}

	@Override
	public PaperTransform getPaperTransform() {
		if (pxf == null) {
			if (getParent() != null) {
				pxf = getParent().getPaperTransform().translate(locX, locY);
			} else if (contentSize == null) {
				return null;
			} else {
				pxf = new PaperTransform(margin.getLeft() + margin.getExtraLeft(), contentSize.getHeight()
						+ margin.getTop() + margin.getExtraTop(), scale);
			}
		}
		return pxf;
	}

	@Override
	public void parentPaperTransformChanged() {
		pxf = null;
		redrawCascade(this);

		/* Layer, Axis, Title, Legend do not cache their paper transform */

		// notify all subplots to update their paper transform
		for (PlotEx sp : subplots) {
			sp.parentPaperTransformChanged();
		}
	}

	@Override
	public PlotMarginEx getMargin() {
		return margin;
	}

	@Override
	public LayoutDirector getLayoutDirector() {
		return layoutDirector;
	}

	@Override
	public void setLayoutDirector(LayoutDirector director) {
		this.layoutDirector = director;
		invalidate();
	}

	@Override
	public Object getConstraint(Plot subplot) {
		return layoutDirector.getConstraint((PlotEx) subplot);
	}

	@Override
	public void setConstraint(Plot subplot, Object constraint) {
		layoutDirector.setConstraint((PlotEx) subplot, constraint);
		invalidate();
	}

	@Override
	public Dimension2D getContentConstrant() {
		return contentConstraint;
	}

	@Override
	public void setContentConstrant(Dimension2D constraint) {
		if (!constraint.equals(this.contentConstraint)) {
			this.contentConstraint = constraint;
			invalidate();
		}
	}

	@Override
	public boolean isValid() {
		return valid;
	}

	@Override
	public void invalidate() {
		if (isValid()) {
			valid = false;
			if (getParent() != null) {
				getParent().invalidate();
			}
			if (layoutDirector != null) {
				layoutDirector.invalidateLayout(this);
			}
		}
	}

	@Override
	public void validate() {
		if (isValid()) {
			return;
		}

		if (layoutDirector != null) {
			layoutDirector.layout(this);
		}

		for (PlotEx subplot : subplots) {
			subplot.validate();
		}

		valid = true;
	}

	@Override
	public boolean isRerenderNeeded() {
		return rerenderNeeded;
	}

	@Override
	public void setRerenderNeeded(boolean flag) {
		rerenderNeeded = flag;
	}

	@Override
	public Notifier getNotifier() {
		return notifier;
	}

	@Override
	public void setNotifier(Notifier notifier) {
		this.notifier = notifier;
	}

	@Override
	public void notify(Notice msg) {
		if ((getParent() != null)) {
			getParent().notify(msg);
		} else if (notifier != null) {
			notifier.notify(msg);
		}
	}

	@Override
	public Dimension2D getPreferredContentSize() {
		return new DoubleDimension2D(preferredContentWidth, preferredContentHeight);
	}

	@Override
	public void setPreferredContentSize(Dimension2D size) {
		if (size == null) {
			throw new IllegalArgumentException("Preferred content size cannot be null.");
		}
		setPreferredContentSize(size.getWidth(), size.getHeight());
	}

	@Override
	public void setPreferredContentSize(double width, double height) {
		if (width <= 0 || height <= 0) {
			throw new IllegalArgumentException("Size must be positive, " + width + "x" + height + " is invalid.");
		}
		this.preferredContentWidth = width;
		this.preferredContentHeight = height;
		// In grid layout or auto pack sizing mode, changing preferred content size will invalidate the layout
		invalidate();
	}

	@Override
	public Dimension2D getContentSize() {
		return new DoubleDimension2D(contentSize);
	}

	@Override
	public void setContentSize(Dimension2D csize) {
		if (csize.getWidth() < 0 || csize.getHeight() < 0) {
			throw new IllegalArgumentException("Content size must be positive, " + csize.getWidth() + "x"
					+ csize.getHeight() + " is invalid.");
		}

		this.contentSize = csize;
		pxf = null;
	}

	@Override
	public ComponentEx[] getComponents() {

		int size = 1 + titles.size() + xAxis.size() + yAxis.size() + layers.size() + subplots.size();

		ComponentEx[] comps = new ComponentEx[size];

		int n = 0;
		comps[n++] = legend;
		for (int i = 0; i < titles.size(); i++) {
			comps[n++] = titles.get(i);
		}
		for (int i = 0; i < xAxis.size(); i++) {
			comps[n++] = xAxis.get(i);
		}
		for (int i = 0; i < yAxis.size(); i++) {
			comps[n++] = yAxis.get(i);
		}
		for (int i = 0; i < layers.size(); i++) {
			comps[n++] = layers.get(i);
		}
		for (int i = 0; i < subplots.size(); i++) {
			comps[n++] = subplots.get(i);
		}

		return comps;
	}

	@Override
	public LegendEx getLegend() {
		return legend;
	}

	@Override
	public TitleEx getTitle(int index) {
		return titles.get(index);
	}

	@Override
	public int indexOf(TitleEx title) {
		return titles.indexOf(title);
	}

	@Override
	public TitleEx[] getTitles() {
		return titles.toArray(new TitleEx[titles.size()]);
	}

	@Override
	public void addTitle(Title title) {
		TitleEx tx = (TitleEx) title;

		titles.add(tx);
		tx.setParent(this);

		redrawCascade(tx);

		if (tx.isVisible() && tx.canContribute() && tx.getPosition() != TitlePosition.FREE) {
			invalidate();
		}
	}

	@Override
	public void removeTitle(Title title) {
		TitleEx tx = (TitleEx) title;

		redrawCascade(tx);

		titles.remove(tx);
		tx.setParent(null);

		if (tx.isVisible() && tx.canContribute() && tx.getPosition() != TitlePosition.FREE) {
			invalidate();
		}
	}

	@Override
	public AxisEx getXAxis(int index) {
		return xAxis.get(index);
	}

	@Override
	public AxisEx getYAxis(int index) {
		return yAxis.get(index);
	}

	@Override
	public int indexOfXAxis(AxisEx axis) {
		return xAxis.indexOf(axis);
	}

	@Override
	public int indexOfYAxis(AxisEx axis) {
		return yAxis.indexOf(axis);
	}

	@Override
	public AxisEx[] getXAxes() {
		return xAxis.toArray(new AxisEx[xAxis.size()]);
	}

	@Override
	public AxisEx[] getYAxes() {
		return yAxis.toArray(new AxisEx[yAxis.size()]);
	}

	@Override
	public void addXAxis(Axis axis) {
		AxisEx ax = (AxisEx) axis;

		if (ax.getTickManager() == null) {
			throw new IllegalArgumentException("The axis has no tick manager.");
		}
		if (ax.getTickManager().getAxisTransform() == null) {
			throw new IllegalArgumentException("The axis' tick manager has no range manager.");
		}
		if (ax.getTickManager().getAxisTransform().getLockGroup() == null) {
			throw new IllegalArgumentException("The axis's range manager has no lock group.");
		}

		xAxis.add(ax);
		ax.setParent(this);
		ax.setOrientation(AxisOrientation.HORIZONTAL);

		redrawCascade(ax);

		if (ax.isVisible() && ax.canContribute()) {
			invalidate();
		}
	}

	@Override
	public void addYAxis(Axis axis) {
		AxisEx ax = (AxisEx) axis;

		if (ax.getTickManager() == null) {
			throw new IllegalArgumentException("The axis has no tick manager.");
		}
		if (ax.getTickManager().getAxisTransform() == null) {
			throw new IllegalArgumentException("The axis' tick manager has no range manager.");
		}
		if (ax.getTickManager().getAxisTransform().getLockGroup() == null) {
			throw new IllegalArgumentException("The axis's range manager has no lock group.");
		}

		yAxis.add(ax);
		ax.setParent(this);
		ax.setOrientation(AxisOrientation.VERTICAL);

		redrawCascade(ax);

		if (ax.isVisible() && ax.canContribute()) {
			invalidate();
		}
	}

	@Override
	public void addXAxes(Axis[] axes) {
		if (axes.length == 0) {
			return;
		}

		AxisTickManagerEx atm = ((AxisEx) axes[0]).getTickManager();
		for (int i = 1; i < axes.length; i++) {
			if (atm != ((AxisEx) axes[i]).getTickManager()) {
				throw new IllegalArgumentException("The axes must have the same tick manager.");
			}
		}

		if (atm == null) {
			throw new IllegalArgumentException("The axes have no tick manager.");
		}
		if (atm.getAxisTransform() == null) {
			throw new IllegalArgumentException("The axes' tick manager has no range manager.");
		}
		if (atm.getAxisTransform().getLockGroup() == null) {
			throw new IllegalArgumentException("The axes' range manager has no lock group.");
		}

		for (int i = 0; i < axes.length; i++) {
			AxisEx ax = (AxisEx) axes[i];
			xAxis.add(ax);
			ax.setParent(this);
			ax.setOrientation(AxisOrientation.HORIZONTAL);

			redrawCascade(ax);

			if (ax.isVisible() && ax.canContribute()) {
				invalidate();
			}
		}
	}

	@Override
	public void addYAxes(Axis[] axes) {
		if (axes.length == 0) {
			return;
		}

		AxisTickManagerEx atm = ((AxisEx) axes[0]).getTickManager();
		for (int i = 1; i < axes.length; i++) {
			if (atm != ((AxisEx) axes[i]).getTickManager()) {
				throw new IllegalArgumentException("The axes must have the same tick manager.");
			}
		}

		if (atm == null) {
			throw new IllegalArgumentException("The axes have no tick manager.");
		}
		if (atm.getAxisTransform() == null) {
			throw new IllegalArgumentException("The axes' tick manager has no range manager.");
		}
		if (atm.getAxisTransform().getLockGroup() == null) {
			throw new IllegalArgumentException("The axes' range manager has no lock group.");
		}

		for (int i = 0; i < axes.length; i++) {
			AxisEx ax = (AxisEx) axes[i];
			yAxis.add(ax);
			ax.setParent(this);
			ax.setOrientation(AxisOrientation.VERTICAL);

			redrawCascade(ax);

			if (ax.isVisible() && ax.canContribute()) {
				invalidate();
			}
		}
	}

	@Override
	public void removeXAxis(Axis axis) {
		AxisEx ax = (AxisEx) axis;

		redrawCascade(ax);

		xAxis.remove(ax);
		ax.setParent(null);

		if (ax.getTickManager().getParent() == null) {
			// quit the tick manager if axis is not its only member
			ax.setTickManager(null);
		} else if (ax.getTickManager().getAxisTransform().getParent() == null) {
			// quit the range manager if tick manager is not its only member
			ax.getTickManager().setAxisTransform(null);
		} else if (ax.getTickManager().getAxisTransform().getLockGroup().getParent() == null) {
			// quit the lock group if range manager is not the lock group's only member
			ax.getTickManager().getAxisTransform().setLockGroup(null);
		}

		if (ax.isVisible() && ax.canContribute()) {
			invalidate();
		}
	}

	@Override
	public void removeYAxis(Axis axis) {
		AxisEx ax = (AxisEx) axis;

		redrawCascade(ax);

		yAxis.remove(ax);
		ax.setParent(null);

		if (ax.getTickManager().getParent() == null) {
			// quit the tick manager if axis is not its only member
			ax.setTickManager(null);
		} else if (ax.getTickManager().getAxisTransform().getParent() == null) {
			// quit the range manager if tick manager is not its only member
			ax.getTickManager().setAxisTransform(null);
		} else if (ax.getTickManager().getAxisTransform().getLockGroup().getParent() == null) {
			// quit the lock group if range manager is not the lock group's only member
			ax.getTickManager().getAxisTransform().setLockGroup(null);
		}

		if (ax.isVisible() && ax.canContribute()) {
			invalidate();
		}
	}

	@Override
	public Layer getLayer(int index) {
		return layers.get(index);
	}

	@Override
	public int indexOf(LayerEx layer) {
		return layers.indexOf(layer);
	}

	@Override
	public LayerEx[] getLayers() {
		return layers.toArray(new LayerEx[layers.size()]);
	}

	@Override
	public void addLayer(Layer layer) {
		LayerEx lx = (LayerEx) layer;
		layers.add(lx);
		lx.setParent(this);

		redrawCascade(lx);

		// add legend items
		for (GraphEx gx : lx.getGraphs()) {
			if (gx instanceof XYGraphEx) {
				getLegend().addLegendItem(((XYGraphEx) gx).getLegendItem());
			}
		}
	}

	@Override
	public void addLayer(Layer layer, AxisTransform xRangeManager, AxisTransform yRangeManager) {
		addLayer(layer);
		layer.setAxesTransform(xRangeManager, yRangeManager);
	}

	@Override
	public void addLayer(Layer layer, Axis xaxis, Axis yaxis) {
		this.addLayer(layer, xaxis.getTickManager().getAxisTransform(), yaxis.getTickManager().getAxisTransform());
	}

	@Override
	public void removeLayer(Layer layer) {
		LayerEx lx = (LayerEx) layer;

		redrawCascade(lx);

		layers.remove(lx);
		lx.setParent(null);
		lx.setAxesTransform(null, null);

		// remove legend items
		for (GraphEx gx : lx.getGraphs()) {
			if (gx instanceof XYGraphEx) {
				getLegend().removeLegendItem(((XYGraphEx) gx).getLegendItem());
			}
		}
	}

	@Override
	public PlotEx getSubplot(int i) {
		return subplots.get(i);
	}

	@Override
	public int indexOf(PlotEx subplot) {
		return subplots.indexOf(subplot);
	}

	@Override
	public PlotEx[] getSubplots() {
		return subplots.toArray(new PlotEx[subplots.size()]);
	}

	@Override
	public void addSubplot(Plot subplot, Object constraint) {
		PlotEx sp = (PlotEx) subplot;
		subplots.add(sp);
		sp.setParent(this);

		redrawCascade(sp);

		if (sp.isVisible()) {
			invalidate();
		}
		LayoutDirector ld = getLayoutDirector();
		if (ld != null) {
			ld.setConstraint(sp, constraint);
		}

		// push the legend items if the legend is disabled
		if (!sp.getLegend().isEnabled()) {
			sp.getLegend().putItemsToEnabledLegend();
		}

	}

	@Override
	public void setSubplotConstraint(Plot subplot, Object constraint) {
		PlotEx sp = (PlotEx) subplot;
		LayoutDirector ld = getLayoutDirector();
		if (ld != null) {
			ld.setConstraint(sp, constraint);
			if (sp.isVisible()) {
				invalidate();
			}
		}
	}

	@Override
	public void removeSubplot(Plot subplot) {
		PlotEx sp = (PlotEx) subplot;

		redrawCascade(sp);

		subplots.remove(sp);
		sp.setParent(null);

		LayoutDirector ld = getLayoutDirector();
		if (ld != null) {
			ld.remove((PlotEx) subplot);
		}
		if (subplot.isVisible()) {
			invalidate();
		}
	}

	@Override
	public boolean canContribute() {
		return false;
	}

	@Override
	public PlotImpl copyStructure(Map<ElementEx, ElementEx> orig2copyMap) {
		PlotImpl result = copyStructureCascade(orig2copyMap);

		linkLayerAndAxisTransform(this, orig2copyMap);

		return result;
	}

	@Override
	public PlotImpl copyStructureCascade(Map<ElementEx, ElementEx> orig2copyMap) {
		PlotImpl result = (PlotImpl) super.copyStructure(orig2copyMap);

		// copy margin
		result.margin = (PlotMarginEx) margin.copyStructure(orig2copyMap);
		result.margin.setParent(result);

		// copy legend
		result.legend = (LegendEx) legend.copyStructure(orig2copyMap);
		result.legend.setParent(result);

		// copy titles
		for (TitleEx title : titles) {
			TitleEx titleCopy = (TitleEx) title.copyStructure(orig2copyMap);
			titleCopy.setParent(result);
			result.titles.add(titleCopy);
		}

		// copy axes
		for (AxisEx va : xAxis) {
			AxisEx vaCopy = (AxisEx) va.copyStructure(orig2copyMap);
			vaCopy.setParent(result);
			result.xAxis.add(vaCopy);
		}
		for (AxisEx va : yAxis) {
			AxisEx vaCopy = (AxisEx) va.copyStructure(orig2copyMap);
			vaCopy.setParent(result);
			result.yAxis.add(vaCopy);
		}

		// copy layers
		for (LayerEx layer : layers) {
			LayerEx layerCopy = (LayerEx) layer.copyStructure(orig2copyMap);
			layerCopy.setParent(result);
			result.layers.add(layerCopy);
		}

		// copy subplots
		for (PlotEx sp : subplots) {
			PlotEx spCopy = (PlotEx) sp.copyStructureCascade(orig2copyMap);
			((ComponentEx) spCopy).setParent(result);
			result.subplots.add(spCopy);
		}

		// link legend and legend items
		for (LegendItemEx item : getLegend().getItems()) {
			LegendItemEx liCopy = (LegendItemEx) orig2copyMap.get(item);
			result.legend.addLegendItem(liCopy);
		}

		return result;
	}

	@Override
	public void copyFrom(ElementEx src) {
		super.copyFrom(src);

		PlotImpl plot = (PlotImpl) src;
		locX = plot.locX;
		locY = plot.locY;
		width = plot.width;
		height = plot.height;
		scale = plot.scale;
		valid = plot.valid;
		layoutDirector = plot.layoutDirector;
		pxf = plot.pxf;
		preferredContentWidth = plot.preferredContentWidth;
		preferredContentHeight = plot.preferredContentHeight;
		contentSize = plot.contentSize;

		containerWidth = plot.containerWidth;
		containerHeight = plot.containerHeight;
		sizeMode = plot.sizeMode;
		rerenderNeeded = plot.rerenderNeeded;
	}

	/**
	 * Deep search layers to find the copy whoes range manager not been linked, and set for them.
	 * 
	 * @param plot
	 *            the plot to be copied
	 * @param orig2copyMap
	 */
	public static void linkLayerAndAxisTransform(PlotEx plot, Map<ElementEx, ElementEx> orig2copyMap) {
		for (LayerEx layer : plot.getLayers()) {
			LayerEx layerCopy = (LayerEx) orig2copyMap.get(layer);
			if (layerCopy.getXAxisTransform() == null && layer.getXAxisTransform() != null) {
				AxisTransformEx xcopy = (AxisTransformEx) orig2copyMap.get(layer.getXAxisTransform());
				layerCopy.linkXAxisTransform(xcopy);
				xcopy.linkLayer(layerCopy);
			}
			if (layerCopy.getYAxisTransform() == null && layer.getYAxisTransform() != null) {
				AxisTransformEx ycopy = (AxisTransformEx) orig2copyMap.get(layer.getYAxisTransform());
				layerCopy.linkYAxisTransform(ycopy);
				ycopy.linkLayer(layerCopy);
			}
		}
		for (PlotEx sp : plot.getSubplots()) {
			linkLayerAndAxisTransform(sp, orig2copyMap);
		}
	}

	@Override
	public void draw(Graphics2D g) {
		// for debugging
		// drawBounds(g);
	}

	@Override
	public void commit() {

		PaperTransform oldPxf = getPaperTransform();
		double scaleResult = 0;

		// set the plot size if it is decided by size mode
		if (getSizeMode() != null && !getSizeMode().isAutoPack()) {
			SizeMode.Result sizeResult = getSizeMode().update(this);
			setSize(sizeResult.getSize());
			scaleResult = sizeResult.getScale();
		}

		/*
		 * Axis is a special component. Its length can be set by layout manager, but its thick depends on its internal
		 * status, such as tick height, labels. The auto range must be re-calculated after all axes length are set. So
		 * we cannot use deep-first validate tree. we must layout all subplots, then calculate auto range, then
		 * calculate thickness of all axes.
		 */

		/*
		 * The initial axis has 0 length and no label. The initial legend size as it contains 1 item. In most case, this
		 * assumption is correct.
		 */

		/* calculate size may invalidate plot */
		calcAxesThickness(this);
		calcLegendSize(this);
		calcTitleSize(this);

		while (true) {

			// auto pack the plot size
			if (getSizeMode() != null && getSizeMode().isAutoPack()) {
				autoPack();
			}

			/*
			 * Laying out axes may register some axis that ticks need be re-calculated
			 */
			this.validate();

			/*
			 * Auto range axes MUST be executed after they are laid out. <br> Auto range axes may register some axis
			 * that ticks need be re-calculated
			 */
			calcPendingLockGroupAutoRange();

			/*
			 * Calculating axes tick may invalidate some axis. Their metrics need be re-calculated
			 */
			calcAxesTick(this);

			/* thickness changes may invalidate the plot */
			calcAxesThickness(this);
			/* length constraint changes may invalidate the plot */
			calcLegendSize(this);

			if (this.isValid()) {
				break;
			}
		}

		// update the scale if the size is autoPacked
		if (getSizeMode() != null && getSizeMode().isAutoPack()) {
			scaleResult = getSizeMode().update(this).getScale();
		}

		// invalidate pxf id scale changed
		if (getSizeMode() != null) {
			setScale(scaleResult);
		}

		// invalidate pxf on all children
		if (oldPxf == null || !oldPxf.equals(getPaperTransform())) {
			this.parentPaperTransformChanged();
		}

		calcPendingImageMappingLimits();
	}

	/**
	 * Sets the plot size according to its contents.
	 */
	private void autoPack() {
		if (getLayoutDirector() != null) {
			if (!isValid()) {
				Dimension2D prefSize = getLayoutDirector().getPreferredSize(this);
				this.setSize(prefSize);
			}
		}
	}

	/**
	 * Re-autorange on all AxisLockGroups whoes autorange are true.
	 */
	private void calcPendingLockGroupAutoRange() {
		Set<AxisRangeLockGroupEx> xalgs = new HashSet<AxisRangeLockGroupEx>();
		Set<AxisRangeLockGroupEx> yalgs = new HashSet<AxisRangeLockGroupEx>();
		fillLockGroups(this, xalgs, yalgs);

		/* calculating auto range may require auto range on y axis, and vice versa */
		boolean hasAutoRange = true;
		while (hasAutoRange) {
			hasAutoRange = false;
			for (AxisRangeLockGroupEx alg : xalgs) {
				hasAutoRange |= alg.calcAutoRange();
			}
			for (AxisRangeLockGroupEx alg : yalgs) {
				hasAutoRange |= alg.calcAutoRange();
			}
		}
	}

	/**
	 * find all AxisLockGroups in the given plot and fill them into the given set.
	 */
	private static void fillLockGroups(PlotEx plot, Set<AxisRangeLockGroupEx> xalgs, Set<AxisRangeLockGroupEx> yalgs) {
		for (AxisEx axis : plot.getXAxes()) {
			AxisRangeLockGroupEx alg = axis.getTickManager().getAxisTransform().getLockGroup();
			xalgs.add(alg);
		}
		for (AxisEx axis : plot.getYAxes()) {
			AxisRangeLockGroupEx alg = axis.getTickManager().getAxisTransform().getLockGroup();
			yalgs.add(alg);
		}
		for (PlotEx sp : plot.getSubplots()) {
			fillLockGroups(sp, xalgs, yalgs);
		}
	}

	/**
	 * Calculate axis thickness according to its tick height, label font and label orientation.
	 */
	private static void calcAxesThickness(PlotEx plot) {
		for (AxisEx axis : plot.getXAxes()) {
			if (axis.isVisible() && axis.canContribute()) {
				calcAxisThickness(axis);
			}
		}
		for (AxisEx axis : plot.getYAxes()) {
			if (axis.isVisible() && axis.canContribute()) {
				calcAxisThickness(axis);
			}
		}
		for (PlotEx sp : plot.getSubplots()) {
			calcAxesThickness(sp);
		}
	}

	private static void calcAxisThickness(AxisEx axis) {
		double asc = axis.getAsc();
		double desc = axis.getDesc();

		axis.calcThickness();

		if (Math.abs(asc - axis.getAsc()) > Math.abs(asc) * 1e-12
				|| Math.abs(desc - axis.getDesc()) > Math.abs(desc) * 1e-12) {
			axis.getParent().invalidate();
		}
	}

	/**
	 * Calculate axis ticks according to its length, range and tick properties.
	 */
	private static void calcAxesTick(PlotEx plot) {
		Set<AxisTickManagerEx> algs = new HashSet<AxisTickManagerEx>();
		fillTickManagers(plot, algs);

		for (AxisTickManagerEx alg : algs) {
			alg.calcTicks();
		}
	}

	/**
	 * find all AxisLockGroups in the given plot and fill them into the given set.
	 */
	private static void fillTickManagers(PlotEx plot, Set<AxisTickManagerEx> algs) {
		for (AxisEx axis : plot.getXAxes()) {
			AxisTickManagerEx alg = axis.getTickManager();
			algs.add(alg);
		}
		for (AxisEx axis : plot.getYAxes()) {
			AxisTickManagerEx alg = axis.getTickManager();
			algs.add(alg);
		}
		for (PlotEx sp : plot.getSubplots()) {
			fillTickManagers(sp, algs);
		}
	}

	/**
	 * Calculate title size according to its contents
	 */
	private static void calcTitleSize(PlotEx plot) {
		for (TitleEx title : plot.getTitles()) {
			if (title.isVisible() && title.canContribute()) {
				Dimension2D size = title.getSize();
				double oldThickness = (size == null) ? 0 : size.getHeight();
				title.calcSize();
				if (Math.abs(oldThickness - title.getSize().getHeight()) > Math.abs(oldThickness) * 1e-12) {
					plot.invalidate();
				}
			}
		}
		for (PlotEx sp : plot.getSubplots()) {
			calcTitleSize(sp);
		}
	}

	/**
	 * Calculate legend size according to its length constraint, items and item font.
	 */
	private static void calcLegendSize(PlotEx plot) {
		LegendEx legend = plot.getLegend();
		if (legend.isVisible() && legend.canContribute()) {
			double oldThickness = legend.getThickness();
			plot.getLegend().calcSize();
			if (Math.abs(oldThickness - legend.getThickness()) > Math.abs(oldThickness) * 1e-12) {
				plot.invalidate();
			}
		}
		for (PlotEx sp : plot.getSubplots()) {
			calcLegendSize(sp);
		}
	}

	/**
	 * Re-autorange on all AxisLockGroups whoes autorange are true.
	 */
	private void calcPendingImageMappingLimits() {
		Set<ImageMappingEx> ims = new HashSet<ImageMappingEx>();
		Set<RGBImageMappingEx> rgbims = new HashSet<RGBImageMappingEx>();
		fillImageMappings(this, ims, rgbims);

		for (ImageMappingEx im : ims) {
			im.calcLimits();
		}
		for (RGBImageMappingEx im : rgbims) {
			im.calcLimits();
		}
	}

	/**
	 * find all AxisLockGroups in the given plot and fill them into the given set.
	 */
	private static void fillImageMappings(PlotEx plot, Set<ImageMappingEx> ims, Set<RGBImageMappingEx> rgbims) {
		for (LayerEx layer : plot.getLayers()) {
			for (GraphEx graph : layer.getGraphs()) {
				if (graph instanceof ImageGraphEx) {
					ims.add(((ImageGraphEx) graph).getMapping());
				}
				if (graph instanceof RGBImageGraphEx) {
					rgbims.add(((RGBImageGraphEx) graph).getMapping());
				}
			}
		}
		for (PlotEx sp : plot.getSubplots()) {
			fillImageMappings(sp, ims, rgbims);
		}
	}

	@Override
	public void zoomXRange(double start, double end) {
		Set<AxisRangeLockGroupEx> xarlgs = getXAxisRangeLockGroup();
		zoomRange(xarlgs, start, end);
	}

	@Override
	public void zoomYRange(double start, double end) {
		Set<AxisRangeLockGroupEx> yarlgs = getYAxisRangeLockGroup();
		zoomRange(yarlgs, start, end);
	}

	/**
	 * @return all zoomable AxisRangeLockGroup
	 */
	private Set<AxisRangeLockGroupEx> getXAxisRangeLockGroup() {
		Set<AxisRangeLockGroupEx> algs = new HashSet<AxisRangeLockGroupEx>();
		for (LayerEx layer : layers) {
			if (layer.getXAxisTransform() != null) {
				AxisRangeLockGroupEx alg = layer.getXAxisTransform().getLockGroup();
				if (alg.isZoomable()) {
					algs.add(alg);
				}
			}
		}
		return algs;
	}

	/**
	 * @return all zoomable AxisRangeLockGroup
	 */
	private Set<AxisRangeLockGroupEx> getYAxisRangeLockGroup() {
		Set<AxisRangeLockGroupEx> algs = new HashSet<AxisRangeLockGroupEx>();
		for (LayerEx layer : layers) {
			if (layer.getXAxisTransform() != null) {
				AxisRangeLockGroupEx alg = layer.getYAxisTransform().getLockGroup();
				if (alg.isZoomable()) {
					algs.add(alg);
				}
			}
		}
		return algs;
	}

	/**
	 * Zoom the range on the given Axis Range Lock Groups
	 * 
	 * @param arlgs
	 * @param start
	 * @param end
	 */
	private void zoomRange(Collection<AxisRangeLockGroupEx> arlgs, double start, double end) {
		Collection<AxisTransformEx> arms = new ArrayList<AxisTransformEx>();
		for (AxisRangeLockGroupEx arlg : arlgs) {
			arms.addAll(Arrays.asList(arlg.getRangeManagers()));
		}

		Range range = new Range.Double(start, end);

		Range validRange = AxisRangeUtils.validateNormalRange(range, arms, false);
		if (!validRange.equals(range)) {
			notify(new RangeAdjustedToValueBoundsNotice("Range exceed valid boundary, has been adjusted."));
		}

		RangeStatus<PrecisionState> rs = AxisRangeUtils.ensurePrecision(validRange, arms);
		if (rs.getStatus() != null) {
			notify(new RangeSelectionNotice(rs.getStatus().getMessage()));
		}
		RangeStatus<PrecisionState> xrs = AxisRangeUtils.ensureCircleSpan(rs, arms);
		if (xrs.getStatus() != null) {
			notify(new RangeSelectionNotice(xrs.getStatus().getMessage()));
		}

		for (AxisRangeLockGroupEx arm : arlgs) {
			arm.zoomNormalRange(xrs);
		}
	}

	@Override
	public void adaptiveZoomX() {
		Set<AxisRangeLockGroupEx> xarlgs = getXAxisRangeLockGroup();
		for (AxisRangeLockGroupEx arm : xarlgs) {
			arm.reAutoRange();
		}
	}

	@Override
	public void adaptiveZoomY() {
		Set<AxisRangeLockGroupEx> yarlgs = getYAxisRangeLockGroup();
		for (AxisRangeLockGroupEx arm : yarlgs) {
			arm.reAutoRange();
		}
	}

}
