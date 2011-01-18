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

import java.awt.geom.Dimension2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jplot2d.element.Subplot;
import org.jplot2d.element.ViewportAxis;
import org.jplot2d.element.AxisOrientation;
import org.jplot2d.element.Element;
import org.jplot2d.element.Layer;
import org.jplot2d.element.PhysicalTransform;
import org.jplot2d.layout.LayoutDirector;
import org.jplot2d.util.DoubleDimension2D;

/**
 * @author Jingjing Li
 * 
 */
public class SubplotImpl extends ContainerImpl implements SubplotEx {

	protected PhysicalTransform pxf;

	private SubplotMarginEx margin = new SubplotMarginImpl();

	/**
	 * True when the object is valid. An invalid object needs to be laid out.
	 * This flag is set to false when the object size is changed.
	 * 
	 * @see #isValid
	 * @see #validate
	 * @see #invalidate
	 */
	private boolean valid = true;

	private LayoutDirector layoutDirector;

	private Rectangle2D contentConstraint;

	/**
	 * Must be valid size (positive width and height)
	 */
	private Dimension2D preferredContentSize = new DoubleDimension2D(320, 240);

	private final List<LayerEx> layers = new ArrayList<LayerEx>();

	private final List<ViewportAxisEx> xViewportAxis = new ArrayList<ViewportAxisEx>();

	private final List<ViewportAxisEx> yViewportAxis = new ArrayList<ViewportAxisEx>();

	private Rectangle2D contentBounds;

	protected final List<SubplotEx> subplots = new ArrayList<SubplotEx>();

	public String getSelfId() {
		if (getParent() != null) {
			return "Subplot" + getParent().indexOf(this);
		} else {
			return "Subplot@" + System.identityHashCode(this);
		}
	}

	public SubplotEx getParent() {
		return (SubplotEx) super.getParent();
	}

	public Map<Element, Element> getMooringMap() {
		Map<Element, Element> result = new HashMap<Element, Element>();

		for (ViewportAxisEx ag : xViewportAxis) {
			if (ag.getLockGroup().getViewportAxes().length > 1) {
				result.put(ag, ag.getLockGroup());
			}
			for (LayerEx layer : ag.getLayers()) {
				if (layer.getParent() != this) {
					result.put(ag, layer);
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

	public void addLayer(Layer layer, ViewportAxis xViewportAxis,
			ViewportAxis yViewportAxis) {
		layers.add((LayerEx) layer);
		((LayerEx) layer).setParent(this);
		((LayerEx) layer).setViewportAxes(xViewportAxis, yViewportAxis);

		if (((LayerEx) layer).canContributeToParent()) {
			redraw();
		}
	}

	public void removeLayer(Layer layer) {
		layers.remove(layer);
		((LayerEx) layer).setParent(null);
		((LayerEx) layer).detachAxes();

		if (((LayerEx) layer).canContributeToParent()) {
			redraw();
		}
	}

	public ViewportAxisEx getXViewportAxis(int index) {
		return xViewportAxis.get(index);
	}

	public ViewportAxisEx getYViewportAxis(int index) {
		return yViewportAxis.get(index);
	}

	public int indexOfXViewportAxis(ViewportAxisEx axisGroup) {
		return xViewportAxis.indexOf(axisGroup);
	}

	public int indexOfYViewportAxis(ViewportAxisEx axisGroup) {
		return yViewportAxis.indexOf(axisGroup);
	}

	public ViewportAxisEx[] getXViewportAxes() {
		return xViewportAxis.toArray(new ViewportAxisEx[xViewportAxis.size()]);
	}

	public ViewportAxisEx[] getYViewportAxes() {
		return yViewportAxis.toArray(new ViewportAxisEx[yViewportAxis.size()]);
	}

	public void addXViewportAxis(ViewportAxis vpAxis) {
		ViewportAxisEx vpa = (ViewportAxisEx) vpAxis;
		xViewportAxis.add(vpa);
		vpa.setParent(this);
		vpa.setOrientation(AxisOrientation.HORIZONTAL);

		if (vpa.canContributeToParent()) {
			redraw();
		}
		for (AxisEx axis : vpa.getAxes()) {
			if (axis.isVisible()) {
				invalidate();
				break;
			}
		}
	}

	public void addYViewportAxis(ViewportAxis vpAxis) {
		ViewportAxisEx vpa = (ViewportAxisEx) vpAxis;
		yViewportAxis.add(vpa);
		vpa.setParent(this);
		vpa.setOrientation(AxisOrientation.VERTICAL);

		if (vpa.canContributeToParent()) {
			redraw();
		}
		for (AxisEx axis : vpa.getAxes()) {
			if (axis.isVisible()) {
				invalidate();
				break;
			}
		}
	}

	public void removeXViewportAxis(ViewportAxis vpAxis) {
		ViewportAxisEx vpa = (ViewportAxisEx) vpAxis;
		if (vpa.getLayers().length > 0) {
			throw new IllegalStateException("The axis has layer attached");
		}

		vpa.setParent(null);
		xViewportAxis.remove(vpa);

		if (vpa.canContributeToParent()) {
			redraw();
		}
		for (AxisEx axis : vpa.getAxes()) {
			if (axis.isVisible()) {
				invalidate();
				break;
			}
		}
	}

	public void removeYViewportAxis(ViewportAxis vpAxis) {
		ViewportAxisEx vpa = (ViewportAxisEx) vpAxis;
		if (vpa.getLayers().length > 0) {
			throw new IllegalStateException("The axis has layer attached");
		}

		vpa.setParent(null);
		yViewportAxis.remove(vpAxis);

		if (vpa.canContributeToParent()) {
			redraw();
		}
		for (AxisEx axis : vpa.getAxes()) {
			if (axis.isVisible()) {
				invalidate();
				break;
			}
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
		for (ViewportAxisEx vpa : xViewportAxis) {
			if (vpa.canContributeToParent()) {
				return true;
			}
		}
		for (ViewportAxisEx vpa : yViewportAxis) {
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

	public SubplotImpl deepCopy(Map<ElementEx, ElementEx> orig2copyMap) {
		SubplotImpl result = (SubplotImpl) super.deepCopy(orig2copyMap);

		// copy margin
		result.margin = margin.deepCopy(orig2copyMap);
		result.margin.setParent(result);

		// copy axes
		for (ViewportAxisEx va : xViewportAxis) {
			ViewportAxisEx vaCopy = (ViewportAxisEx) va.deepCopy(orig2copyMap);
			vaCopy.setParent(result);
			result.xViewportAxis.add(vaCopy);
		}
		for (ViewportAxisEx va : yViewportAxis) {
			ViewportAxisEx vaCopy = (ViewportAxisEx) va.deepCopy(orig2copyMap);
			vaCopy.setParent(result);
			result.yViewportAxis.add(vaCopy);
		}

		// copy layers
		for (LayerEx layer : layers) {
			LayerEx layerCopy = (LayerEx) layer.deepCopy(orig2copyMap);
			layerCopy.setParent(result);
			result.layers.add(layerCopy);

			// link layer and viewport axis
			if (layer.getXViewportAxis() != null) {
				ViewportAxisEx xcopy = (ViewportAxisEx) orig2copyMap.get(layer
						.getXViewportAxis());
				layerCopy.setXViewportAxis(xcopy);
			}
			if (layer.getYViewportAxis() != null) {
				ViewportAxisEx ycopy = (ViewportAxisEx) orig2copyMap.get(layer
						.getYViewportAxis());
				layerCopy.setYViewportAxis(ycopy);
			}
		}

		return result;
	}

	public void copyFrom(ComponentEx src, Map<ElementEx, ElementEx> orig2copyMap) {
		super.copyFrom(src, orig2copyMap);

		SubplotImpl sp = (SubplotImpl) src;
		valid = sp.valid;
		layoutDirector = sp.layoutDirector;
		pxf = sp.pxf;
		preferredContentSize = sp.preferredContentSize;
		contentBounds = sp.contentBounds;

	}

}
