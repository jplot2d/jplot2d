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
package org.jplot2d.element.impl;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jplot2d.element.Axis;
import org.jplot2d.element.Plot;
import org.jplot2d.element.AxisRangeManager;
import org.jplot2d.element.AxisOrientation;
import org.jplot2d.element.Element;
import org.jplot2d.element.Layer;
import org.jplot2d.element.PhysicalTransform;
import org.jplot2d.element.Title;
import org.jplot2d.layout.LayoutDirector;
import org.jplot2d.layout.SimpleLayoutDirector;
import org.jplot2d.sizing.SizeMode;
import org.jplot2d.util.DoubleDimension2D;
import org.jplot2d.util.WarningMessage;
import org.jplot2d.util.WarningReceiver;

/**
 * @author Jingjing Li
 * 
 */
public class PlotImpl extends ContainerImpl implements PlotEx {

	private Dimension2D containerSize = new Dimension(640, 480);

	private SizeMode sizeMode;

	private double locX, locY;

	private double width, height;

	private PhysicalTransform pxf;

	/**
	 * True when the object is valid. An invalid object needs to be laid out.
	 * This flag is set to false when the object size is changed. The initial
	 * value is true, because the {@link #contentBounds} and size are both 0*0.
	 * 
	 * @see #isValid
	 * @see #validate
	 * @see #invalidate
	 */
	private boolean valid = true;

	private boolean rerenderNeeded = true;

	private WarningReceiver warningReceiver;

	private LayoutDirector layoutDirector = new SimpleLayoutDirector();

	private Rectangle2D contentConstraint;

	/**
	 * Must be valid size (positive width and height)
	 */
	private Dimension2D preferredContentSize = new DoubleDimension2D(320, 240);

	private boolean preferredSizeChanged;

	private Rectangle2D contentBounds;

	private SubplotMarginEx margin;

	private LegendEx legend;

	private final List<TitleEx> titles = new ArrayList<TitleEx>();

	private final List<AxisEx> xAxis = new ArrayList<AxisEx>();

	private final List<AxisEx> yAxis = new ArrayList<AxisEx>();

	private final List<LayerEx> layers = new ArrayList<LayerEx>();

	private final List<PlotEx> subplots = new ArrayList<PlotEx>();

	public PlotImpl() {
		margin = new SubplotMarginImpl();
		margin.setParent(this);
		legend = new LegendImpl();
		legend.setParent(this);
	}

	protected PlotImpl(LegendEx legend) {
		margin = new SubplotMarginImpl();
		margin.setParent(this);
		this.legend = legend;
		legend.setParent(this);
	}

	public String getSelfId() {
		if (getParent() != null) {
			return "Subplot" + getParent().indexOf(this);
		} else {
			return "Subplot@"
					+ Integer.toHexString(System.identityHashCode(this));
		}
	}

	public PlotEx getParent() {
		return (PlotEx) super.getParent();
	}

	public Map<Element, Element> getMooringMap() {
		Map<Element, Element> result = new HashMap<Element, Element>();

		for (AxisEx axis : xAxis) {
			AxisRangeManagerEx va = axis.getRangeManager();
			if (va.getAxes().length > 1) {
				result.put(axis, va);
			} else if (va.getLockGroup().getRangeManagers().length > 1) {
				result.put(va, va.getLockGroup());
			}
			for (LayerEx layer : va.getLayers()) {
				if (layer.getParent() != this) {
					result.put(va, layer);
				}
			}
		}
		return result;
	}

	public Dimension2D getContainerSize() {
		return containerSize;
	}

	public void setContainerSize(Dimension2D size) {
		this.containerSize = size;
	}

	public SizeMode getSizeMode() {
		return sizeMode;
	}

	public void setSizeMode(SizeMode sizeMode) {
		this.sizeMode = sizeMode;
		sizeMode.setPlot(this);
	}

	public Point2D getLocation() {
		return new Point2D.Double(locX, locY);
	}

	public final void setLocation(Point2D p) {
		setLocation(p.getX(), p.getY());
	}

	public void setLocation(double locX, double locY) {
		if (getLocation().getX() != locX || getLocation().getY() != locY) {
			this.locX = locX;
			this.locY = locY;
			pxf = null;
			redraw();
		}
	}

	public Dimension2D getSize() {
		return new DoubleDimension2D(width, height);
	}

	public final void setSize(Dimension2D size) {
		this.setSize(size.getWidth(), size.getHeight());
	}

	public void setSize(double width, double height) {
		if (width < 0 || height < 0) {
			throw new IllegalArgumentException("paper size must be positive, "
					+ width + "x" + height + " is invalid.");
		}

		if (this.width != width || this.height != height) {
			this.width = width;
			this.height = height;
			pxf = null;
			invalidate();
		}
	}

	public Rectangle2D getBounds() {
		return new Rectangle2D.Double(locX, locY, width, height);
	}

	public PhysicalTransform getPhysicalTransform() {
		if (pxf == null) {
			pxf = getParent().getPhysicalTransform().translate(
					getLocation().getX(), getLocation().getY());
		}
		return pxf;
	}

	public void parentPhysicalTransformChanged() {
		pxf = null;
		redraw();

		// notify all layers
		for (LayerEx layer : layers) {
			layer.parentPhysicalTransformChanged();
		}
		// notify all subplots
		for (PlotEx sp : subplots) {
			sp.parentPhysicalTransformChanged();
		}
	}

	public SubplotMarginEx getMargin() {
		return margin;
	}

	public LayoutDirector getLayoutDirector() {
		return layoutDirector;
	}

	public void setLayoutDirector(LayoutDirector director) {
		this.layoutDirector = director;
	}

	public Object getConstraint(Plot subplot) {
		return layoutDirector.getConstraint((PlotEx) subplot);
	}

	public void setConstraint(Plot subplot, Object constraint) {
		layoutDirector.setConstraint((PlotEx) subplot, constraint);
	}

	public Rectangle2D getContentConstrant() {
		return contentConstraint;
	}

	public void setContentConstrant(Rectangle2D bounds) {
		if (bounds.getWidth() <= 0 || bounds.getHeight() <= 0) {
			throw new IllegalArgumentException("Size must be positive, "
					+ width + "x" + height + " is invalid.");
		}
		this.contentConstraint = bounds;
	}

	/**
	 * Determines whether this component is valid. A component is valid when it
	 * is correctly sized and positioned within its parent container and all its
	 * children are also valid.
	 * 
	 * @return <code>true</code> if the component is valid, <code>false</code>
	 *         otherwise
	 * @see #validate
	 * @see #invalidate
	 */
	public boolean isValid() {
		return valid;
	}

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

	public boolean isRerenderNeeded() {
		return rerenderNeeded;
	}

	public void rerender() {
		if (getParent() != null) {
			getParent().rerender();
		} else {
			rerenderNeeded = true;
		}
	}

	public void clearRerenderNeeded() {
		rerenderNeeded = false;
	}

	public void setWarningReceiver(WarningReceiver warningReceiver) {
		this.warningReceiver = warningReceiver;
	}

	public void warning(WarningMessage msg) {
		if ((getParent() != null)) {
			getParent().warning(msg);
		} else if (warningReceiver != null) {
			warningReceiver.warning(msg);
		}
	}

	public Dimension2D getPreferredContentSize() {
		return preferredContentSize;
	}

	public void setPreferredContentSize(Dimension2D size) {
		if (size == null) {
			throw new IllegalArgumentException(
					"Preferred content size cannpt be null.");
		}
		if (size.getWidth() <= 0 || size.getHeight() <= 0) {
			throw new IllegalArgumentException("Size must be positive, "
					+ width + "x" + height + " is invalid.");
		}
		preferredContentSize = size;
		childPreferredContentSizeChanged();
	}

	public void childPreferredContentSizeChanged() {
		if (getParent() != null) {
			getParent().childPreferredContentSizeChanged();
		} else {
			preferredSizeChanged = true;
		}
	}

	public Rectangle2D getContentBounds() {
		return contentBounds;
	}

	public void setContentBounds(Rectangle2D bounds) {
		if (bounds.getWidth() <= 0 || bounds.getHeight() <= 0) {
			throw new IllegalArgumentException("Size must be positive, "
					+ width + "x" + height + " is invalid.");
		}
		if (bounds.equals(this.contentBounds)) {
			return;
		}

		this.contentBounds = bounds;

		for (LayerEx layer : getLayers()) {
			layer.updateLocation();
		}
	}

	public LegendEx getLegend() {
		return legend;
	}

	public TitleEx getTitle(int index) {
		return titles.get(index);
	}

	public int indexOf(TitleEx title) {
		return titles.indexOf(title);
	}

	public TitleEx[] getTitles() {
		return titles.toArray(new TitleEx[titles.size()]);
	}

	public void addTitle(Title title) {
		TitleEx tx = (TitleEx) title;

		titles.add(tx);
		tx.setParent(this);

		if (tx.canContributeToParent()) {
			redraw();
		} else if (tx.canContribute()) {
			rerender();
		}
		if (tx.canContribute() && tx.getPosition() != null) {
			invalidate();
		}
	}

	public void removeTitle(Title title) {
		TitleEx tx = (TitleEx) title;

		titles.remove(tx);
		tx.setParent(null);

		if (tx.canContributeToParent()) {
			redraw();
		} else if (tx.canContribute()) {
			rerender();
		}
		if (tx.canContribute() && tx.getPosition() != null) {
			invalidate();
		}
	}

	public Layer getLayer(int index) {
		return layers.get(index);
	}

	public int indexOf(LayerEx layer) {
		return layers.indexOf(layer);
	}

	public LayerEx[] getLayers() {
		return layers.toArray(new LayerEx[layers.size()]);
	}

	public void addLayer(Layer layer, AxisRangeManager xRangeManager,
			AxisRangeManager yRangeManager) {
		LayerEx lx = (LayerEx) layer;
		layers.add(lx);
		lx.setParent(this);
		lx.setRangeManager(xRangeManager, yRangeManager);

		if (lx.canContributeToParent()) {
			redraw();
		} else if (lx.canContribute()) {
			rerender();
		}

		// add legend items
		for (GraphPlotterEx gp : lx.getGraphPlotters()) {
			getLegend().addLegendItem(gp.getLegendItem());
		}
	}

	public void removeLayer(Layer layer) {
		LayerEx lx = (LayerEx) layer;
		layers.remove(lx);
		lx.setParent(null);
		lx.setRangeManager(null, null);

		if (lx.canContributeToParent()) {
			redraw();
		} else if (lx.canContribute()) {
			rerender();
		}

		// remove legend items
		for (GraphPlotterEx gp : lx.getGraphPlotters()) {
			getLegend().removeLegendItem(gp.getLegendItem());
		}
	}

	public AxisEx getXAxis(int index) {
		return xAxis.get(index);
	}

	public AxisEx getYAxis(int index) {
		return yAxis.get(index);
	}

	public int indexOfXAxis(AxisEx axis) {
		return xAxis.indexOf(axis);
	}

	public int indexOfYAxis(AxisEx axis) {
		return yAxis.indexOf(axis);
	}

	public AxisEx[] getXAxes() {
		return xAxis.toArray(new AxisEx[xAxis.size()]);
	}

	public AxisEx[] getYAxes() {
		return yAxis.toArray(new AxisEx[yAxis.size()]);
	}

	public void addXAxis(Axis axis) {
		AxisEx ax = (AxisEx) axis;

		if (ax.getRangeManager() == null) {
			throw new IllegalArgumentException("The axis has no range manager.");
		}
		if (ax.getRangeManager().getLockGroup() == null) {
			throw new IllegalArgumentException(
					"The axis's range manager has no lock group.");
		}

		xAxis.add(ax);
		ax.setParent(this);
		ax.setOrientation(AxisOrientation.HORIZONTAL);

		if (ax.canContributeToParent()) {
			redraw();
		} else if (ax.canContribute()) {
			rerender();
		}
		if (ax.canContribute()) {
			invalidate();
		}
	}

	public void addYAxis(Axis axis) {
		AxisEx ax = (AxisEx) axis;

		if (ax.getRangeManager() == null) {
			throw new IllegalArgumentException("The axis has no range manager.");
		}
		if (ax.getRangeManager().getLockGroup() == null) {
			throw new IllegalArgumentException(
					"The axis's range manager has no lock group.");
		}

		yAxis.add(ax);
		ax.setParent(this);
		ax.setOrientation(AxisOrientation.VERTICAL);

		if (ax.canContributeToParent()) {
			redraw();
		} else if (ax.canContribute()) {
			rerender();
		}
		if (ax.canContribute()) {
			invalidate();
		}
	}

	public void removeXAxis(Axis axis) {
		AxisEx ax = (AxisEx) axis;
		ax.setParent(null);
		xAxis.remove(ax);

		if (ax.getRangeManager().getParent() == null) {
			// quit the range manager if axis is not its only member
			ax.setRangeManager(null);
		} else if (ax.getRangeManager().getLockGroup().getParent() == null) {
			// quit the lock group if range manager can be remove together but
			// it is not the lock group's only member
			ax.getRangeManager().setLockGroup(null);
		}

		if (ax.canContributeToParent()) {
			redraw();
		} else if (ax.canContribute()) {
			rerender();
		}
		if (ax.canContribute()) {
			invalidate();
		}
	}

	public void removeYAxis(Axis axis) {
		AxisEx ax = (AxisEx) axis;
		ax.setParent(null);
		yAxis.remove(ax);

		// quit the range manager if axis is not its only member
		if (ax.getRangeManager().getParent() == null) {
			ax.setRangeManager(null);
		}

		if (ax.canContributeToParent()) {
			redraw();
		} else if (ax.canContribute()) {
			rerender();
		}
		if (ax.canContribute()) {
			invalidate();
		}
	}

	public PlotEx getSubplot(int i) {
		return subplots.get(i);
	}

	public int indexOf(PlotEx subplot) {
		return subplots.indexOf(subplot);
	}

	public PlotEx[] getSubplots() {
		return subplots.toArray(new PlotEx[subplots.size()]);
	}

	public void addSubplot(Plot subplot, Object constraint) {
		PlotEx sp = (PlotEx) subplot;
		subplots.add(sp);
		sp.setParent(this);

		if (sp.canContributeToParent()) {
			redraw();
		} else if (sp.canContribute()) {
			rerender();
		}

		if (sp.isVisible()) {
			invalidate();
		}
		LayoutDirector ld = getLayoutDirector();
		if (ld != null) {
			ld.setConstraint(sp, constraint);
		}
	}

	public void removeSubplot(Plot subplot) {
		PlotEx sp = (PlotEx) subplot;
		subplots.remove(sp);
		sp.setParent(null);

		if (sp.canContributeToParent()) {
			redraw();
		} else if (sp.canContribute()) {
			rerender();
		}

		LayoutDirector ld = getLayoutDirector();
		if (ld != null) {
			ld.remove((PlotEx) subplot);
		}
		if (subplot.isVisible()) {
			invalidate();
		}
	}

	public boolean canContributeToParent() {
		if (!isVisible() || isCacheable()) {
			return false;
		}
		for (AxisEx vpa : xAxis) {
			if (vpa.canContributeToParent()) {
				return true;
			}
		}
		for (AxisEx vpa : yAxis) {
			if (vpa.canContributeToParent()) {
				return true;
			}
		}
		for (LayerEx layer : layers) {
			if (layer.canContributeToParent()) {
				return true;
			}
		}
		for (PlotEx sp : subplots) {
			if (sp.canContributeToParent()) {
				return true;
			}
		}
		return false;
	}

	public boolean canContribute() {
		if (!isVisible()) {
			return false;
		}
		for (AxisEx vpa : xAxis) {
			if (vpa.canContribute()) {
				return true;
			}
		}
		for (AxisEx vpa : yAxis) {
			if (vpa.canContribute()) {
				return true;
			}
		}
		for (LayerEx layer : layers) {
			if (layer.canContribute()) {
				return true;
			}
		}
		for (PlotEx sp : subplots) {
			if (sp.canContribute()) {
				return true;
			}
		}
		return false;
	}

	@Override
	public PlotImpl copyStructure(Map<ElementEx, ElementEx> orig2copyMap) {
		PlotImpl result = (PlotImpl) super.copyStructure(orig2copyMap);

		// copy margin
		result.margin = (SubplotMarginEx) margin.copyStructure(orig2copyMap);
		result.margin.setParent(result);

		// copy legend
		result.legend = (LegendEx) legend.copyStructure(orig2copyMap);
		result.legend.setParent(result);

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
			PlotEx spCopy = (PlotEx) sp.copyStructure(orig2copyMap);
			((ComponentEx) spCopy).setParent(this);
			result.subplots.add(spCopy);
		}

		if (getParent() == null) {
			// link layer and range manager
			linkLayerAndRangeManager(this, orig2copyMap);
			// link legend and LegendItem
			linkLegendAndLegendItem(this, orig2copyMap);
		}

		return result;
	}

	@Override
	public void copyFrom(ElementEx src) {
		super.copyFrom(src);

		PlotImpl sp = (PlotImpl) src;
		locX = sp.locX;
		locY = sp.locY;
		width = sp.width;
		height = sp.height;
		valid = sp.valid;
		layoutDirector = sp.layoutDirector;
		pxf = sp.pxf;
		preferredContentSize = sp.preferredContentSize;
		contentBounds = sp.contentBounds;

		containerSize = sp.containerSize;
		sizeMode = sp.sizeMode;
		rerenderNeeded = sp.rerenderNeeded;
	}

	/**
	 * Deep search layers to find the copy whoes range manager not been linked,
	 * and set for them.
	 * 
	 * @param subplot
	 * @param orig2copyMap
	 */
	protected static void linkLayerAndRangeManager(PlotEx subplot,
			Map<ElementEx, ElementEx> orig2copyMap) {
		for (LayerEx layer : subplot.getLayers()) {
			LayerEx layerCopy = (LayerEx) orig2copyMap.get(layer);
			if (layerCopy.getXRangeManager() == null
					&& layer.getXRangeManager() != null) {
				AxisRangeManagerEx xcopy = (AxisRangeManagerEx) orig2copyMap
						.get(layer.getXRangeManager());
				layerCopy.setXRangeManager(xcopy);
			}
			if (layerCopy.getYRangeManager() == null
					&& layer.getYRangeManager() != null) {
				AxisRangeManagerEx ycopy = (AxisRangeManagerEx) orig2copyMap
						.get(layer.getYRangeManager());
				layerCopy.setYRangeManager(ycopy);
			}
		}
		for (PlotEx sp : subplot.getSubplots()) {
			linkLayerAndRangeManager(sp, orig2copyMap);
		}
	}

	protected static void linkLegendAndLegendItem(PlotEx subplot,
			Map<ElementEx, ElementEx> orig2copyMap) {
		for (LayerEx layer : subplot.getLayers()) {
			for (GraphPlotterEx gp : layer.getGraphPlotters()) {
				LegendItemEx li = gp.getLegendItem();
				LegendItemEx liCopy = (LegendItemEx) orig2copyMap.get(li);
				if (liCopy.getLegend() == null) {
					LegendEx legendCopy = (LegendEx) orig2copyMap.get(li
							.getLegend());
					legendCopy.addLegendItem(liCopy);
				}
			}
		}
	}

	public void draw(Graphics2D g) {
		// for debugging
		// drawBounds(g);
	}

	public void commit() {

		// set the plot size if it is derived or fixed
		if (!getSizeMode().isAutoPack()) {
			getSizeMode().update();
			setSize(getSizeMode().getSize());
		}

		/*
		 * Axis a special component. Its length can be set by layout manager,
		 * but its thick depends on its internal status, such as tick height,
		 * labels. The auto range must be re-calculated after all axes length
		 * are set. So we cannot use deep-first validate tree. we must layout
		 * all subplot, then calculate auto range, then calculate thickness of
		 * all axes.
		 */

		/*
		 * The initial axis has 0 length and no label. The initial legend size
		 * as it contains 1 item. In most case, this assumption is correct.
		 */

		while (true) {

			// auto pack the plot size
			if (getSizeMode().isAutoPack()) {
				autoPack();
			}

			/*
			 * Laying out axes may register some axis that ticks need be
			 * re-calculated
			 */
			this.validate();

			/*
			 * Auto range axes MUST be executed after they are laid out. <br>
			 * Auto range axes may register some axis that ticks need be
			 * re-calculated
			 */
			calcPendingLockGroupAutoRange();

			/*
			 * Calculating axes tick may invalidate some axis. Their metrics
			 * need be re-calculated
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

		// update the size mode if the size if autoPacked
		if (getSizeMode().isAutoPack()) {
			getSizeMode().update();
		}

		// update physical transform of this plot
		if (!getSizeMode().getPhysicalTransform().equals(this.pxf)) {
			this.parentPhysicalTransformChanged();
			pxf = getSizeMode().getPhysicalTransform();
		}
	}

	private void autoPack() {
		if (getLayoutDirector() != null) {
			if (!isValid() || preferredSizeChanged) {
				Dimension2D prefSize = getLayoutDirector().getPreferredSize(
						this);
				this.setSize(prefSize);
				preferredSizeChanged = false;
			}
		}
	}

	/**
	 * Re-autorange on all AxisLockGroups whoes autorange are true.
	 */
	private void calcPendingLockGroupAutoRange() {
		Set<AxisLockGroupEx> algs = new HashSet<AxisLockGroupEx>();
		fillLockGroups(this, algs);

		for (AxisLockGroupEx alg : algs) {
			alg.calcAutoRange();
		}
	}

	/**
	 * fill all AxisLockGroups in the given subplot to the set.
	 */
	private void fillLockGroups(PlotEx subplot, Set<AxisLockGroupEx> algs) {
		for (AxisEx axis : subplot.getXAxes()) {
			AxisLockGroupEx alg = axis.getRangeManager().getLockGroup();
			algs.add(alg);
		}
		for (AxisEx axis : subplot.getYAxes()) {
			AxisLockGroupEx alg = axis.getRangeManager().getLockGroup();
			algs.add(alg);
		}
		for (PlotEx sp : subplot.getSubplots()) {
			fillLockGroups(sp, algs);
		}
	}

	/**
	 * Calculate axis thickness according to its tick height, label font and
	 * label orientation.
	 */
	private void calcAxesThickness(PlotEx subplot) {
		for (AxisEx axis : subplot.getXAxes()) {
			if (axis.isVisible()) {
				axis.calcThickness();
			}
		}
		for (AxisEx axis : subplot.getYAxes()) {
			if (axis.isVisible()) {
				axis.calcThickness();
			}
		}
		for (PlotEx sp : subplot.getSubplots()) {
			calcAxesThickness(sp);
		}
	}

	/**
	 * Calculate axis ticks according to its length, range and tick properties.
	 */
	private void calcAxesTick(PlotEx subplot) {
		for (AxisEx axis : subplot.getXAxes()) {
			axis.calcTicks();
		}
		for (AxisEx axis : subplot.getYAxes()) {
			axis.calcTicks();
		}
		for (PlotEx sp : subplot.getSubplots()) {
			calcAxesTick(sp);
		}
	}

	/**
	 * Calculate legend size according to its length constraint, items and item
	 * font.
	 */
	private void calcLegendSize(PlotEx subplot) {
		if (subplot.getLegend().isVisible()) {
			subplot.getLegend().calcSize();
		}
		for (PlotEx sp : subplot.getSubplots()) {
			calcLegendSize(sp);
		}
	}

}
