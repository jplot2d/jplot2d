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
import java.util.List;
import java.util.Map;

import org.jplot2d.axtrans.NormalTransform;
import org.jplot2d.axtrans.TransformType;
import org.jplot2d.axtype.AxisType;
import org.jplot2d.element.Axis;
import org.jplot2d.element.AxisLockGroup;
import org.jplot2d.element.PhysicalTransform;
import org.jplot2d.util.Range2D;
import org.jplot2d.util.RangeAdjustedToValueBoundsWarning;
import org.jplot2d.util.RangeSelectionWarning;

public class AxisRangeManagerImpl extends ElementImpl implements
		AxisRangeManagerEx {

	/** The default margin factor (used for both lower and upper margins) */
	public static final double DEFAULT_MARGIN_FACTOR = 1.0 / 32; // 0.03125

	private AxisType type;

	private TransformType txfType;

	private boolean autoMargin = true;

	private double marginFactor = DEFAULT_MARGIN_FACTOR;

	private Range2D coreRange;

	private NormalTransform ntf;

	private AxisLockGroupEx group;

	private final List<AxisEx> axes = new ArrayList<AxisEx>();

	private final List<LayerEx> layers = new ArrayList<LayerEx>();

	public AxisRangeManagerImpl() {
		super();

		type = AxisType.NUMBER;
		txfType = type.getDefaultTransformType();
		ntf = txfType.createNormalTransform(type.getDefaultWorldRange(txfType));
	}

	public String getId() {
		if (group != null) {
			int xidx = group.indexOfRangeManager(this);
			return "RM" + xidx + "." + group.getId();
		} else {
			return "AxisRangeManager@"
					+ Integer.toHexString(System.identityHashCode(this));
		}
	}

	public AxisEx getParent() {
		return (AxisEx) super.getParent();
	}

	public boolean isReferenced() {
		return axes.size() > 0;
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
		if (group.getRangeManagers().length > 1) {
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
		if (group.getRangeManagers().length > 1) {
			throw new IllegalStateException(
					"The axis type can only be changed when the axis doses not lock with other axes.");
		}

		this.txfType = txfType;

	}

	public void changeTransformType(TransformType txfType) {
		this.txfType = txfType;

	}

	public AxisLockGroupEx getLockGroup() {
		return group;
	}

	public void setLockGroup(AxisLockGroup group) {
		if (this.group != null) {
			this.group.removeRangeManager(this);
		}
		this.group = (AxisLockGroupEx) group;
		if (this.group != null) {
			this.group.addRangeManager(this);
		}
	}

	public NormalTransform getNormalTransform() {
		return ntf;
	}

	public void setNormalTransfrom(NormalTransform ntf) {
		this.ntf = ntf;
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

	public void addAxis(AxisEx axis) {
		axes.add((AxisEx) axis);
		if (axes.size() == 1) {
			parent = axes.get(0);
		} else {
			parent = null;
		}
	}

	public void removeAxis(AxisEx axis) {
		axes.remove(axis);
		if (axes.size() == 1) {
			parent = axes.get(0);
		} else {
			parent = null;
		}
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

		Map<AxisRangeManagerEx, NormalTransform> vtMap = AxisRangeUtils
				.createVirtualTransformMap(Arrays.asList(group
						.getRangeManagers()));

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
			return axes
					.get(0)
					.getTick()
					.expandRangeToTick(getTransformType(),
							axes.get(0).getLength(), ur);
		}
		return null;
	}

	@Override
	public AxisRangeManagerEx copyStructure(Map<ElementEx, ElementEx> orig2copyMap) {
		AxisRangeManagerImpl result = new AxisRangeManagerImpl();

		if (orig2copyMap != null) {
			orig2copyMap.put(this, result);
		}

		// copy or link group
		AxisLockGroupEx algCopy = (AxisLockGroupEx) orig2copyMap.get(group);
		if (algCopy == null) {
			algCopy = (AxisLockGroupEx) group.copyStructure(orig2copyMap);
		}
		result.group = algCopy;
		algCopy.addRangeManager(result);

		return result;
	}

	@Override
	public void copyFrom(ElementEx src) {
		super.copyFrom(src);

		AxisRangeManagerImpl arm = (AxisRangeManagerImpl) src;
		this.type = arm.type;
		this.txfType = arm.txfType;
		this.autoMargin = arm.autoMargin;
		this.marginFactor = arm.marginFactor;
		this.coreRange = arm.coreRange;
		this.ntf = arm.ntf;
	}

}