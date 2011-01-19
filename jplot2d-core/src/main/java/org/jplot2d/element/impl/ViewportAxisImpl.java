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
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jplot2d.axtrans.AxisTransform;
import org.jplot2d.axtrans.NormalTransform;
import org.jplot2d.axtrans.TransformType;
import org.jplot2d.axtype.AxisType;
import org.jplot2d.element.Axis;
import org.jplot2d.element.AxisLockGroup;
import org.jplot2d.element.AxisOrientation;
import org.jplot2d.element.Element;
import org.jplot2d.element.PhysicalTransform;
import org.jplot2d.util.Range2D;
import org.jplot2d.util.RangeAdjustedToValueBoundsWarning;
import org.jplot2d.util.RangeSelectionWarning;

public class ViewportAxisImpl extends ContainerImpl implements ViewportAxisEx {

	/** The default margin factor (used for both lower and upper margins) */
	public static final double DEFAULT_MARGIN_FACTOR = 1.0 / 32; // 0.03125

	private AxisType type;

	private TransformType txfType;

	private AxisOrientation orientation;

	private boolean autoMargin = true;

	private double marginFactor = DEFAULT_MARGIN_FACTOR;

	private Range2D coreRange;

	private NormalTransform ntf;

	private double offset;

	private double length;

	private AxisTransform axf;

	private AxisLockGroupEx group;

	private final List<AxisEx> axes = new ArrayList<AxisEx>();

	private final List<LayerEx> layers = new ArrayList<LayerEx>();

	public ViewportAxisImpl() {
		super();

		group = new AxisLockGroupImpl();
		group.addViewportAxis(this);

		type = AxisType.NUMBER;
		txfType = type.getDefaultTransformType();
		ntf = txfType.createNormalTransform(type.getDefaultWorldRange(txfType));
	}

	public String getSelfId() {
		if (getParent() != null) {
			int xidx = getParent().indexOfXViewportAxis(this);
			if (xidx != -1) {
				return "X" + xidx;
			}
			int yidx = getParent().indexOfYViewportAxis(this);
			if (yidx != -1) {
				return "Y" + yidx;
			}
			return null;
		} else {
			return "AxisGroup@" + System.identityHashCode(this);
		}
	}

	public SubplotEx getParent() {
		return (SubplotEx) super.getParent();
	}

	public Map<Element, Element> getMooringMap() {
		Map<Element, Element> result = new HashMap<Element, Element>();

		if (group.getViewportAxes().length > 1) {
			result.put(this, group);
		}
		for (LayerEx layer : layers) {
			result.put(this, layer);
		}

		return result;
	}

	public Dimension2D getSize() {
		if (getParent() == null) {
			return null;
		} else {
			return getParent().getSize();
		}
	}

	public Rectangle2D getBounds() {
		if (getParent() == null) {
			return null;
		} else {
			return getParent().getBounds();
		}
	}

	public PhysicalTransform getPhysicalTransform() {
		if (getParent() == null) {
			return null;
		} else {
			return getParent().getPhysicalTransform();
		}
	}

	public boolean isInverted() {
		// TODO Auto-generated method stub
		return false;
	}

	public void setInverted(boolean flag) {
		// TODO Auto-generated method stub

	}

	public boolean isAutoMargin() {
		return autoMargin;
	}

	public void setAutoMargin(boolean autoMargin) {
		this.autoMargin = autoMargin;

		if (group.isAutoRange() && group.getPrimaryAxis() == this) {
			group.reAutoRange();
		} else if (coreRange != null) {
			Range2D irng = coreRange.copy();
			setRange(coreRange, true);
			coreRange = irng;
		}
	}

	public double getMarginFactor() {
		return marginFactor;
	}

	public void setMarginFactor(double factor) {
		marginFactor = factor;

		if (group.isAutoRange() && group.getPrimaryAxis() == this) {
			group.reAutoRange();
		} else if (coreRange != null) {
			Range2D irng = coreRange.copy();
			setRange(coreRange, true);
			coreRange = irng;
		}
	}

	public AxisType getType() {
		return type;
	}

	public void setType(AxisType type) {
		if (group.getViewportAxes().length > 1) {
			throw new IllegalStateException(
					"The axis type can only be changed when the axis doses not lock with other axes.");
		}

		this.type = type;
		if (!type.canSupport(txfType)) {
			txfType = type.getDefaultTransformType();
		}

		for (AxisEx axis : axes) {
			axis.getTick().setTickAlgorithm(type.getTickAlgorithm(txfType));
		}

		group.validateAxesRange();
	}

	public TransformType getTransformType() {
		return txfType;
	}

	public void setTransformType(TransformType txfType) {
		if (group.getViewportAxes().length > 1) {
			throw new IllegalStateException(
					"The axis type can only be changed when the axis doses not lock with other axes.");
		}

		this.txfType = txfType;

	}

	public void changeTransformType(TransformType txfType) {
		this.txfType = txfType;

	}

	public AxisOrientation getOrientation() {
		return orientation;
	}

	public void setOrientation(AxisOrientation orientation) {
		this.orientation = orientation;
	}

	public AxisLockGroupEx getLockGroup() {
		return group;
	}

	public AxisLockGroupEx setLockGroup(AxisLockGroup group) {
		AxisLockGroupEx result = this.group;
		if (this.group != null) {
			this.group.removeViewportAxis(this);
		}
		this.group = (AxisLockGroupEx) group;
		this.group.addViewportAxis(this);
		return result;
	}

	public NormalTransform getNormalTransform() {
		return ntf;
	}

	public void setNormalTransfrom(NormalTransform ntf) {
		this.ntf = ntf;
		updateAxisTransform();
	}

	public double getOffset() {
		return offset;
	}

	public void setOffset(double offset) {
		if (this.offset != offset) {
			this.offset = offset;
			updateAxisTransform();
		}
	}

	public double getLength() {
		return length;
	}

	public void setLength(double length) {
		if (this.length != length) {
			this.length = length;
			updateAxisTransform();
		}
	}

	public AxisTransform getAxisTransform() {
		if (axf == null) {
			Range2D prange = new Range2D.Double(0, getLength());
			axf = type.getDefaultTransformType().createTransform(prange,
					ntf.getRangeW());
		}
		return axf;
	}

	/**
	 * Update axis transform when normal transform or paper range changed.
	 */
	private void updateAxisTransform() {
		axf = null;
	}

	public Axis getAxis(int index) {
		return axes.get(index);
	}

	public int indexOfAxis(AxisEx axis) {
		return axes.indexOf(axis);
	}

	public AxisEx[] getAxes() {
		return axes.toArray(new AxisEx[axes.size()]);
	}

	public void addAxis(Axis axis) {
		axes.add((AxisEx) axis);
		((AxisEx) axis).setParent(this);
	}

	public void removeAxis(Axis axis) {
		axes.remove(axis);
		((AxisEx) axis).setParent(null);
	}

	public boolean canContributeToParent() {
		if (!isVisible() || isCacheable()) {
			return false;
		}
		for (AxisEx ax : axes) {
			if (ax.isVisible() && !ax.isCacheable()) {
				return true;
			}
		}
		return false;
	}

	public LayerEx[] getLayers() {
		return layers.toArray(new LayerEx[layers.size()]);
	}

	public void addLayer(LayerEx layer) {
		layers.add(layer);
		if (group.isAutoRange()) {
			for (GraphPlotterEx plotter : ((LayerEx) layer).getGraphPlotters()) {
				if (plotter.isVisible()) {
					group.reAutoRange();
					break;
				}
			}
		}
	}

	public void removeLayer(LayerEx layer) {
		layers.remove(layer);
		if (group.isAutoRange()) {
			for (GraphPlotterEx plotter : ((LayerEx) layer).getGraphPlotters()) {
				if (plotter.isVisible()) {
					group.reAutoRange();
					break;
				}
			}
		}
	}

	public void copyFrom(ComponentEx src, Map<ElementEx, ElementEx> orig2copyMap) {
		super.copyFrom(src, orig2copyMap);

		ViewportAxisImpl va = (ViewportAxisImpl) src;
		this.orientation = va.orientation;
		this.ntf = va.ntf;
		this.offset = va.offset;
		this.length = va.length;

		for (AxisEx axis : va.axes) {
			AxisEx aCopy = (AxisEx) axis.deepCopy(orig2copyMap);
			aCopy.setParent(this);
			axes.add(aCopy);
		}

	}

	public Range2D getCoreRange() {
		return coreRange;
	}

	public void setCoreRange(Range2D range) {
		if (range == null) {

		} else {
			setRange(range, true);
		}
		coreRange = range;
	}

	public Range2D getRange() {
		return ntf.getRangeW();
	}

	public void setRange(Range2D urange) {
		if (urange.isInverted() != ntf.getRangeW().isInverted()) {
			urange = urange.invert();
		}

		setRange(urange, false);
	}

	public void setRange(double ustart, double uend) {
	}

	/**
	 * Notice after this method, the _coreRange will be reset to null
	 * 
	 * @param urange
	 *            the range to be set
	 * @param appendMargin
	 *            true to indicate a margin should be appended to the given
	 *            range, to derive a actual range.
	 * @throws WarningException
	 */
	private void setRange(Range2D urange, boolean appendMargin) {

		if (Double.isNaN(urange.getStart()) || Double.isNaN(urange.getEnd())) {
			throw new IllegalArgumentException(
					"Range cannot start or end at NaN.");
		}

		Map<ViewportAxisEx, NormalTransform> vtMap = AxisRangeUtils
				.createVirtualTransformMap(Arrays.asList(group
						.getViewportAxes()));

		NormalTransform vnt = vtMap.get(this);
		Range2D pr = vnt.getTransP(urange);

		if (appendMargin) {
			double span = pr.getSpan();
			double mpStart = pr.getStart() - span * getMarginFactor();
			double mpEnd = pr.getEnd() + span * getMarginFactor();
			pr = new Range2D.Double(mpStart, mpEnd);
		}
		Range2D pRange = AxisRangeUtils.validateNormalRange(pr, vtMap, false);
		if (pRange == null) {
			// no intersect at all
			throw new IllegalArgumentException(getId()
					+ ": The given range is not valid.");
		}

		double pLo = pRange.getMin();
		double pHi = pRange.getMax();

		if (!pRange.equals(pr)) {
			warning(new RangeAdjustedToValueBoundsWarning(
					getId()
							+ ": the given range contains invalid value, range adjusted to ["
							+ ntf.getTransU(pLo) + ", " + ntf.getTransU(pHi)
							+ "]"));
		}

		/* ensurePrecision */
		RangeStatus<PrecisionState> rs = AxisRangeUtils.ensurePrecision(pRange,
				vtMap);
		if (rs.getStatus() != null) {
			warning(new RangeSelectionWarning(rs.getStatus().getMessage()));
		}

		/* extend range to tick */
		Range2D extRange;
		if (appendMargin && isAutoMargin()) {
			Range2D ur = vnt.getTransU(rs);
			Range2D exur = expandRangeToTick(ur);
			extRange = vnt.getTransP(exur);
		} else {
			extRange = rs;
		}

		RangeStatus<PrecisionState> xrs = AxisRangeUtils.ensureCircleSpan(
				extRange, vtMap);
		if (xrs.getStatus() != null) {
			warning(new RangeSelectionWarning(xrs.getStatus().getMessage()));
		}
		group.zoomVirtualRange(xrs, vtMap);

	}

	public Range2D expandRangeToTick(Range2D ur) {
		if (axes.size() > 0) {
			return axes.get(0).getTick()
					.expandRangeToTick(getTransformType(), length, ur);
		}
		return null;
	}

}
