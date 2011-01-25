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
package org.jplot2d.element.impl;

import java.awt.Graphics2D;
import java.awt.geom.Dimension2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jplot2d.element.Axis;
import org.jplot2d.element.Subplot;
import org.jplot2d.element.AxisRangeManager;
import org.jplot2d.element.AxisOrientation;
import org.jplot2d.element.Element;
import org.jplot2d.element.Layer;
import org.jplot2d.element.PhysicalTransform;
import org.jplot2d.layout.LayoutDirector;
import org.jplot2d.layout.SimpleLayoutDirector;
import org.jplot2d.util.DoubleDimension2D;

/**
 * @author Jingjing Li
 * 
 */
public class SubplotImpl extends ContainerImpl implements SubplotEx {

	protected double width, height;

	protected PhysicalTransform pxf;

	private SubplotMarginEx margin = new SubplotMarginImpl();

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

	private LayoutDirector layoutDirector = new SimpleLayoutDirector();

	private Rectangle2D contentConstraint;

	/**
	 * Must be valid size (positive width and height)
	 */
	private Dimension2D preferredContentSize = new DoubleDimension2D(320, 240);

	private final List<LayerEx> layers = new ArrayList<LayerEx>();

	private final List<AxisEx> xAxis = new ArrayList<AxisEx>();

	private final List<AxisEx> yAxis = new ArrayList<AxisEx>();

	private Rectangle2D contentBounds;

	protected final List<SubplotEx> subplots = new ArrayList<SubplotEx>();

	public String getSelfId() {
		if (getParent() != null) {
			return "Subplot" + getParent().indexOf(this);
		} else {
			return "Subplot@"
					+ Integer.toHexString(System.identityHashCode(this));
		}
	}

	public SubplotEx getParent() {
		return (SubplotEx) super.getParent();
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

	public void setLocation(double locX, double locY) {
		if (getLocation().getX() != locX || getLocation().getY() != locY) {
			super.setLocation(locX, locY);
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
		return new Rectangle2D.Double(getLocation().getX(), getLocation()
				.getY(), width, height);
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
		for (SubplotEx sp : subplots) {
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

	public Object getConstraint(Subplot subplot) {
		return layoutDirector.getConstraint((SubplotEx) subplot);
	}

	public void setConstraint(Subplot subplot, Object constraint) {
		layoutDirector.setConstraint((SubplotEx) subplot, constraint);
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

		for (SubplotEx subplot : subplots) {
			subplot.validate();
		}

		valid = true;
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
		this.contentBounds = bounds;
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
		layers.add((LayerEx) layer);
		((LayerEx) layer).setParent(this);
		((LayerEx) layer).setRangeManager(xRangeManager, yRangeManager);

		if (((LayerEx) layer).canContributeToParent()) {
			redraw();
		}
	}

	public void removeLayer(Layer layer) {
		layers.remove(layer);
		((LayerEx) layer).setParent(null);
		((LayerEx) layer).setRangeManager(null, null);

		if (((LayerEx) layer).canContributeToParent()) {
			redraw();
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
		}
		if (ax.isVisible()) {
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
		}
		if (ax.isVisible()) {
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
		}
		if (ax.isVisible()) {
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
		}
		if (ax.isVisible()) {
			invalidate();
		}
	}

	public SubplotEx getSubplot(int i) {
		return subplots.get(i);
	}

	public int indexOf(SubplotEx subplot) {
		return subplots.indexOf(subplot);
	}

	public SubplotEx[] getSubplots() {
		return subplots.toArray(new SubplotEx[subplots.size()]);
	}

	public void addSubplot(Subplot subplot, Object constraint) {
		subplots.add((SubplotEx) subplot);
		((SubplotEx) subplot).setParent(this);

		if (subplot.isVisible()) {
			invalidate();
		}

		LayoutDirector ld = getLayoutDirector();
		if (ld != null) {
			ld.setConstraint((SubplotEx) subplot, constraint);
		}
	}

	public void removeSubplot(Subplot subplot) {
		LayoutDirector ld = getLayoutDirector();
		if (ld != null) {
			ld.remove((SubplotEx) subplot);
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
		for (SubplotEx sp : subplots) {
			if (sp.canContributeToParent()) {
				return true;
			}
		}
		return false;
	}

	@Override
	public SubplotImpl copyStructure(Map<ElementEx, ElementEx> orig2copyMap) {
		SubplotImpl result = (SubplotImpl) super.copyStructure(orig2copyMap);

		// copy margin
		result.margin = (SubplotMarginEx) margin.copyStructure(orig2copyMap);
		result.margin.setParent(result);

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
		for (SubplotEx sp : subplots) {
			SubplotEx spCopy = (SubplotEx) sp.copyStructure(orig2copyMap);
			((ComponentEx) spCopy).setParent(this);
			result.subplots.add(spCopy);
		}

		// link layer and range manager
		linkLayerAndRangeManager(this, orig2copyMap);

		return result;
	}

	@Override
	public void copyFrom(ElementEx src) {
		super.copyFrom(src);

		SubplotImpl sp = (SubplotImpl) src;
		width = sp.width;
		height = sp.height;
		valid = sp.valid;
		layoutDirector = sp.layoutDirector;
		pxf = sp.pxf;
		preferredContentSize = sp.preferredContentSize;
		contentBounds = sp.contentBounds;
	}

	/**
	 * Deep search layers to find the copy whoes range manager not been linked,
	 * and set for them.
	 * 
	 * @param subplot
	 * @param orig2copyMap
	 */
	protected static void linkLayerAndRangeManager(SubplotEx subplot,
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
		for (SubplotEx sp : subplot.getSubplots()) {
			linkLayerAndRangeManager(sp, orig2copyMap);
		}
	}

	public void draw(Graphics2D g) {
		// draw nothing
		drawBounds(g);
	}

}
